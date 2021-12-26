package spreadsheet;

import java.util.Objects;

import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.InvalidExpressionTreeException;
import graph.GraphCycleException;
import graph.GraphNode;
import spreadsheet.Geometry.Point;
import spreadsheet.expressions.CellExpressionTreeNode;


/** Data structure for cells, which caches the result value of the expressionTree and can be notified of dependency changes to reevaluate itself.*/
public class Cell implements IDependencyChangedListener {
	Point position;
	String expression;
	
	GraphNode<String, IDependencyChangedListener> dependencyGraphNode;
	
	ExpressionTreeNode expressionTree;
	Object value;

	public Cell(Point position) {
		this.position = position;
		this.expression = "";
		this.expressionTree = new ConstantExpressionTreeNode(expression);
		this.dependencyGraphNode = 
				new GraphNode<String, IDependencyChangedListener>(
						String.format("%d,%d", position.row, position.column), 
						this
				);
	}
	
	@Override
	public void onDependencyChanged() {
		evaluate();
	}
	
	public Object getValue() {
		return value;
	}
	
	public Class<?> getValueClass() {
		return expressionTree.getResultClass();
	}
	
	
	/** Evaluates the expression and, if the value has changed, notifies its descendants in the dependency graph. */
	public void updateValue() throws GraphCycleException {
		Object oldValue = value;
		
		evaluate();
		
		if (!Objects.equals(oldValue, value))
			for (GraphNode<String, IDependencyChangedListener> child : dependencyGraphNode.topologicalSort())
				child.data.onDependencyChanged();
	}
	
	
	/** Evaluates the expression and caches the result. */
	private void evaluate() {
		if (expressionTree == null)
			throw new IllegalStateException("The expression hasn't yet been compiled.");
		
		try {
			this.value = expressionTree.evaluate();
		} catch (InvalidExpressionTreeException e) {
			this.value = null;
		}
	}
}