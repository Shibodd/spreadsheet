package spreadsheet;

import java.util.LinkedList;
import java.util.Objects;

import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import graph.GraphCycleException;
import graph.GraphNode;
import spreadsheet.Geometry.*;
import spreadsheet.expressions.CellExpressionTreeNode;
import spreadsheet.expressions.ExpressionCompiler;
import tokenizer.TokenizeException;


/** Represents a spreadsheet, which can be queried for cells. */
public class Spreadsheet {
	ExpressionCompiler compiler;
	LinkedList<Cell> cells;
	
	
	public Spreadsheet() {
		compiler = new ExpressionCompiler(pt -> getCellAt(pt));
		cells = new LinkedList<Cell>();
	}	
	
	/** Gets the cell at a certain position.
	 * If the cell was previously non existent, it creates it.*/
	public Cell getCellAt(Point pos) {
		pos.toString();
		
		return cells.stream()
				.filter(x -> x.position.equals(pos))
				.findFirst()
				.orElseGet(() -> {
					Cell cell = new Cell(pos);
					cells.add(cell);
					return cell;
				});
	}
	
	/** Updates the expression of a cell, compiles the expression, and updates the subscription to the dependency graph. */
	public void updateExpression(Cell cell, String expression) throws TokenizeException, Exception {
		cell.expression = expression;
		cell.dependencyGraphNode.removeAllParents(); // Unsubscribe from the previous expression's dependencies.		

		if (expression.startsWith("="))
			cell.expressionTree = compiler.compile(expression.substring(1));
		else
			cell.expressionTree = new ConstantExpressionTreeNode(expression);
		
		for (ExpressionTreeNode x : cell.expressionTree.getDescendantsOfClass(CellExpressionTreeNode.class))
			cell.dependencyGraphNode.addParent(((CellExpressionTreeNode)x).getCell().dependencyGraphNode);
		
		cell.updateValue();
	}
}
