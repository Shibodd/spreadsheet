package spreadsheet;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.InvalidExpressionTreeException;
import graph.GraphCycleException;
import graph.GraphNode;
import spreadsheet.Geometry.GridVector2;
import spreadsheet.expressions.CellExpressionTreeNode;


/** Data structure for cells, which caches the result value of the expressionTree and can be notified of dependency changes to reevaluate itself.*/
public class Cell implements IDependencyChangedListener {
	GridVector2 position;
	String expression;
	
	GraphNode<String, IDependencyChangedListener> dependencyGraphNode;
	ICellValueChangedListener cellValueChangedListener;
	
	ExpressionTreeNode expressionTree;
	Object value;

	public Cell(GridVector2 position, ICellValueChangedListener valueChangedListener) {
		this.position = position;
		this.expression = "";
		this.expressionTree = new ConstantExpressionTreeNode(expression);
		this.value = "";
		this.dependencyGraphNode = 
				new GraphNode<String, IDependencyChangedListener>(
						String.format("%d,%d", position.row, position.column), 
						this
				);
		this.cellValueChangedListener = valueChangedListener;
	}
	
	public GridVector2 getPosition() { return position; }
	public String getExpression() { return expression; }
	public Object getValue() { return value; }

	
	@Override
	public void onDependencyChanged(Object sender) {
		evaluate();
	}

	/** Evaluates the expression and, if the value has changed, notifies its descendants in the dependency graph. */
	public void updateValue()  {
		Object oldValue = value;
		
		evaluate();
		
		try {
			if (!Objects.equals(oldValue, value)) {
				Stack<GraphNode<String, IDependencyChangedListener>> nodes = dependencyGraphNode.topologicalSort();
				
				nodes.pop(); // skip the first as it is this node
				
				while (!nodes.empty())
					nodes.pop().data.onDependencyChanged(this);
			}
		} catch (GraphCycleException ex) {
			this.value = new CellEvaluationError(new InvalidExpressionTreeException("The expression presents a circular reference."));
		}
	}
	
	
	/** Evaluates the expression and caches the result. */
	private void evaluate() {
		if (expressionTree == null)
			throw new IllegalStateException("The expression hasn't yet been compiled.");
		
		Object oldValue = value;
		
		try {
			this.value = expressionTree.evaluate();			
		} catch (InvalidExpressionTreeException ex) {
			this.value = new CellEvaluationError(ex);
		}
		
		if (!Objects.equals(oldValue, value) && cellValueChangedListener != null)
			cellValueChangedListener.onCellValueChanged(position);
			
	}
}