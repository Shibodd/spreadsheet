package spreadsheet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Function;

import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import graph.DFSInfo;
import graph.GraphCycleException;
import graph.GraphNode;
import spreadsheet.Geometry.*;
import spreadsheet.expressions.BaseLibrary;
import spreadsheet.expressions.CellExpressionTreeNode;
import spreadsheet.expressions.ExpressionCompiler;


/** Represents a spreadsheet, which can be queried to update or retrieve the values and expression of its cells. */
public class Spreadsheet implements ICellValueChangedListener {
	public String filePath;
	
	GridRect rect;
	
	ExpressionCompiler compiler;
	Cell[][] cells;
	
	List<ICellValueChangedListener> cellValueChangedListeners;

	/** Initializes an empty spreadsheet with a certain size.
	 * @param width The width.
	 * @param height The height. 
	 */
	public Spreadsheet(GridVector2 size) {
		rect = new GridRect(0, 0, size.row, size.column);
		compiler = new ExpressionCompiler(this, BaseLibrary.makeBaseLibrary());
		cells = new Cell[size.row][size.column];
		cellValueChangedListeners = new ArrayList<ICellValueChangedListener>();
	}

	/** Retrieves the value of the cell at a certain position. 
	 * @param where The position of the cell.
	 * @return The value of the cell.
	 */
	public Object getValueAt(GridVector2 where) {
		Cell c = getCellAt(where);
		
		if (c == null)
			return "";
		else
			return c.value;
	}

	
	/** Retrieves the expression of the cell at a certain position.
	 * @param where The position of the cell.
	 * @return The expression of the cell.
	 */
	public String getExpressionAt(GridVector2 where) {
		Cell c = getCellAt(where);
		
		if (c == null)
			return "";
		else
			return c.expression;
	}

	
	/** Returns the cell at a certain position, or null if it does not exist.
	 * @param position The position of the cell.
 	 * @return The cell at that position, or null if it doesn't exist.
	 */
	private Cell getCellAt(GridVector2 position) {
		if (!rect.contains(position))
			throw new IllegalArgumentException(String.format("Position %s out of range (the size of the spreadsheet is %dx%d).", position, rect.size.row, rect.size.column));
		
		return cells[position.row][position.column];
	}
	
	/** If a cell at a certain position exists, returns it; else, creates a new empty cell.
	 * @param position The position of the cell.
 	 * @return The cell at that position.
	 */
	private Cell getCellAtElseCreate(GridVector2 position) {
		return Objects.requireNonNullElseGet(
				getCellAt(position), 
				() -> {
					Cell cell = new Cell(position, this);
					cells[position.row][position.column] = cell;
					
					return cell;
				}
		);
	}
	
	public int getWidth() {
		return rect.size.column;
	}
	public int getHeight() {
		return rect.size.row;
	}
	

	/** Updates the expression of a cell, then updates its value.
	 * @param position The position of the cell.
	 * @param expression The new expression the cell should have.
	 */
	public void updateExpression(GridVector2 position, String expression)  {
		Cell cell = getCellAtElseCreate(position);
		
		updateExpressionWithoutValueUpdate(cell, expression);
		
		cell.updateValue();
	}
	
	/**  Updates the expression of a cell, then compiles the expression. 
	 * @param cell The cell whose expression should be updated.
	 * @param expression The new expression the cell should have.
	 */
	private void updateExpressionWithoutValueUpdate(Cell cell, String expression) {
		cell.expression = expression;
		
		cell.dependencyGraphNode.removeAllParents(); // Unsubscribe from the previous expression's dependencies.		

		if (expression.startsWith("=")) {
			try {
				cell.expressionTree = compiler.compile(expression.substring(1));
			} catch (Exception ex) {
				cell.expressionTree = new ConstantExpressionTreeNode(new CellEvaluationError(ex));
			}
			
			// Register for notification changes on the Cells that this expression references
			for (ExpressionTreeNode x : cell.expressionTree.getNodesOfClass(CellExpressionTreeNode.class))
				cell.dependencyGraphNode.addParent(getCellAtElseCreate(((CellExpressionTreeNode)x).position).dependencyGraphNode);
		}
		else {
			Object constant;
			
			try { constant = Double.parseDouble(expression); } // Try to interpret it as a double
			catch (NumberFormatException ex) { constant = expression; } // else just as a string
			
			cell.expressionTree = new ConstantExpressionTreeNode(constant);
		}
	}
	
	/** Registers a listener for cell value changes. 
	 * @param listener The listener to add. 
	 */
	public void addCellValueChangedListener(ICellValueChangedListener listener) {
		cellValueChangedListeners.add(listener);
	}
	
	public void removeCellValueChangedListener(ICellValueChangedListener listener) {
		cellValueChangedListeners.remove(listener);
	}
	
	@Override
	public void onCellValueChanged(GridVector2 position) {
		for (ICellValueChangedListener listener : cellValueChangedListeners)
			listener.onCellValueChanged(position);
	}
	
	
	

	/** Reads a spreadsheet from a file.
	 * @param path The path from which to read the spreadsheet.
	 * @return The spreadsheet read.
	 * @throws IOException if an I/O error occurs when reading opening the file or reading from it.
	 * @throws FileFormatException If the file is in the incorrect format.
	 */
	public static Spreadsheet openFromFile(String path) throws IOException, FileFormatException {
		try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
			Scanner sizeScanner = new Scanner(reader.readLine());
			
			Spreadsheet spreadsheet = new Spreadsheet(new GridVector2(sizeScanner.nextInt(), sizeScanner.nextInt()));

			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\t");
				if (parts.length != 2)
					throw new FileFormatException("A row has a different amount of columns than expected.");

				GridVector2 pos = CellIdentifiers.parse(parts[0]);
				spreadsheet.updateExpressionWithoutValueUpdate(spreadsheet.getCellAtElseCreate(pos), parts[1]);
			}
			
			spreadsheet.refreshValues();

			return spreadsheet;
		} catch (NoSuchElementException | NumberFormatException | FileFormatException ex) {
			throw new FileFormatException("The savefile is not in the correct format.", ex);
		}
	}
	
	/** Applies action on each cell in the spreadsheet, as long as action returns true.
	 * @param action The action to apply to each cell; If it returns false, the iteration ends.
	 * @return True if the action has been applied on every cell; False if action returned false and the iteration ended. */
	public boolean forEachCell(Function<Cell, Boolean> action) {
		for (int r = 0; r < rect.size.row; ++r) {
			for (int c = 0; c < rect.size.column; ++c) {
				Cell cell = cells[r][c];
				
				if (cell == null || cell.expression.isEmpty())
					continue;
				
				if (!action.apply(cell))
					return false;
			}
		}
		
		return true;
	}
	
	/** Re-evaluate all expressions. */
	public void refreshValues() {
		// The expressions have to be evaluated in topological order.
		// Perform a topological sort using a DFS on the entire dependency forest.
		
		HashMap<String, DFSInfo<String, IDependencyChangedListener>> dfsInfo = new HashMap<String, DFSInfo<String, IDependencyChangedListener>>();
		Stack<GraphNode<String, IDependencyChangedListener>> stack = new Stack<GraphNode<String, IDependencyChangedListener>>();
		
		boolean cycle = !forEachCell(cell -> {
			if (dfsInfo.containsKey(cell.dependencyGraphNode.getId()))
				return true;
			
			try {
				cell.dependencyGraphNode.topologicalSort(stack, dfsInfo);
				return true;
			} catch (GraphCycleException ex) {
				forEachCell(c -> {
					c.value = new CellEvaluationError(ex);
					return true;
				});
				
				return false;
			}
		});
		
		if (cycle)
			return;
		
		while (stack.size() > 0)
			stack.pop().data.onDependencyChanged(this);
	}
	
	/** Writes the spreadsheet to a file.
	 * @param path The path of the file to write to.
	 * @throws IOException if an I/O error occurs when opening or creating the file, or when writing to it.
	 */
	public void saveToFile(String path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(path), StandardCharsets.UTF_8)) {
			writer.write(String.format("%d\t%d\n", rect.size.row, rect.size.column));
			
			for (int r = 0; r < rect.size.row; ++r) {
				for (int c = 0; c < rect.size.column; ++c) {
					Cell cell = cells[r][c];
					
					if (cell == null || cell.expression.isEmpty())
						continue;
					
					writer.write(String.format("%s\t%s\n", CellIdentifiers.toString(cell.position), cell.expression));
				}
			}
		}
	}
}
