package spreadsheet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.InvalidExpressionTreeException;
import graph.DFSInfo;
import graph.GraphCycleException;
import graph.GraphNode;
import spreadsheet.Geometry.*;
import spreadsheet.expressions.BaseLibrary;
import spreadsheet.expressions.CellExpressionTreeNode;
import spreadsheet.expressions.ExpressionCompiler;
import spreadsheet.expressions.UnexpectedTokenException;
import spreadsheet.expressions.UnknownFunctionException;


/** Represents a spreadsheet, which can be queried for cells. */
public class Spreadsheet implements ICellValueChangedListener {
	GridRect rect;
	
	ExpressionCompiler compiler;
	Cell[][] cells;
	
	List<ICellValueChangedListener> cellValueChangedListeners;
	
	String filePath;

	public Spreadsheet(int width, int height) {
		rect = new GridRect(0, 0, width, height);
		compiler = new ExpressionCompiler(this, BaseLibrary.makeBaseLibrary());
		cells = new Cell[width][height];
		cellValueChangedListeners = new ArrayList<ICellValueChangedListener>();
	}

	public Object getValueAt(GridVector2 where) {
		Cell c = getCellAt(where);
		
		if (c == null)
			return "";
		else
			return c.value;
	}

	public String getExpressionAt(GridVector2 where) {
		Cell c = getCellAt(where);
		
		if (c == null)
			return "";
		else
			return c.expression;
	}

	private Cell getCellAt(GridVector2 position) {
		if (!rect.contains(position))
			throw new IllegalArgumentException(String.format("Position %s out of range (the size of the spreadsheet is %dx%d).", position, rect.size.row, rect.size.column));
		
		return cells[position.row][position.column];
	}
	
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

	/** Updates the expression of a cell, compiles the expression, and updates the subscription to the dependency graph. 
	 * @throws UnknownFunctionException 
	 * @throws InvalidExpressionTreeException 
	 * @throws UnexpectedTokenException */
	public void updateExpression(GridVector2 position, String expression)  {
		Cell cell = getCellAtElseCreate(position);
		
		updateExpressionWithoutValueUpdate(cell, expression);
		
		cell.updateValue();
	}
	
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
	
	public String getFilePath() {
		return filePath;
	}
	
	
	public static Spreadsheet openFromFile(String path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
			Scanner sizeScanner = new Scanner(reader.readLine());
			
			Spreadsheet spreadsheet = new Spreadsheet(sizeScanner.nextInt(), sizeScanner.nextInt());

			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\t");
				if (parts.length != 2)
					throw new InputMismatchException("diocane");
				
				GridVector2 pos = CellIdentifiers.parse(parts[0]);
				spreadsheet.updateExpressionWithoutValueUpdate(spreadsheet.getCellAtElseCreate(pos), parts[1]);
			}
			
			spreadsheet.refreshValues();

			return spreadsheet;
		}
	}
	
	/** Applies action on each cell in the spreadsheet, as long as action returns true; when it returns false, the iteration ends.
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
	
	/** Re-evaluate all expressions in topological order, obtained by performing a topological sort using a DFS on the entire dependency forest. */
	public void refreshValues() {
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
	
	
	public void saveToFile() throws IOException {
		if (filePath == null)
			throw new IllegalStateException("No filepath stored - save with saveToFile(path) first.");
		
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath), StandardCharsets.UTF_8)) {
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
	
	public void saveToFile(String path) throws IOException {
		filePath = path;
		saveToFile();
	}
}
