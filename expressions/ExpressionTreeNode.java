package expressions;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * The base class for expression tree nodes.
 *  Provides functionality for managing children, querying the tree, evaluating the expression represented by the tree and type-checking.
 */
public abstract class ExpressionTreeNode {
	protected List<ExpressionTreeNode> children;

	public ExpressionTreeNode() {
		children = new LinkedList<ExpressionTreeNode>();
	}
	
	public void addChild(ExpressionTreeNode node) throws InvalidExpressionTreeException { 
		children.add(node);
	}
	
	public void removeChild(ExpressionTreeNode node) {
		children.remove(node);
	}
	
	
	/** Applies action in post-order to each descendant for which predicate tests true. */
	private void dfsPostApplyWhere(Predicate<ExpressionTreeNode> predicate, Consumer<ExpressionTreeNode> action) {
		for (ExpressionTreeNode child : children)
			child.dfsPostApplyWhere(predicate, action);
		
		if (predicate.test(this))
			action.accept(this);
	}
	
	/** Gets all the descendants of the tree which are of type theClass. */
	public List<ExpressionTreeNode> getNodesOfClass(Class<?> theClass) {
		ArrayList<ExpressionTreeNode> ans = new ArrayList<ExpressionTreeNode>();
		
		dfsPostApplyWhere(node -> node.getClass().equals(theClass), node -> ans.add(node));
		
		return ans;
	}

	
	/** Evaluates the expression tree and returns the result. */
	public abstract Object evaluate() throws InvalidExpressionTreeException;
	
	
	public Object evaluateChildAndTypeCheck(int i, Class<?> expectedClass) throws InvalidExpressionTreeException {
		Object value = children.get(i).evaluate();
		
		Class<?> valueClass = value == null? null : value.getClass();
		
		if (valueClass == null || !expectedClass.equals(valueClass))
			throw new InvalidExpressionTreeException(
					String.format("The class \"%s\" of the children %d differs from the expected class \"%s\".", 
							valueClass == null? "null" : valueClass.getName(), i, expectedClass.getName())
			);
		
		return value;
	}
}
