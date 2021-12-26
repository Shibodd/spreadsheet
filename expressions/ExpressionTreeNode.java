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
	protected Class<?> resultClass;
	protected List<ExpressionTreeNode> children;

	public ExpressionTreeNode(Class<?> resultType) {
		children = new LinkedList<ExpressionTreeNode>();
		this.resultClass = resultType;
	}
	
	public Class<?> getResultClass() { return resultClass; }
	
	public void addChild(ExpressionTreeNode node) throws InvalidExpressionTreeException { 
		children.add(node);
	}
	
	public void removeChild(ExpressionTreeNode node) {
		children.remove(node);
	}
	
	
	/** Shorthand for ensuring that the evaluated object of an ExpressionTreeNode is an instance of the declared class. */
	public Object resultTypeCheck(Object result) throws InvalidExpressionTreeException {
		Class<?> resultClass = result.getClass();
		
		if (!resultClass.equals(this.resultClass))
			throw new InvalidExpressionTreeException(String.format(
					"The declared result class is %s, but the class of the result is %s.",
					this.resultClass.getName(),
					resultClass.getName()));
		
		return result;
	}
	
	
	/** Applies action in post-order to each descendant for which predicate tests true. */
	private void dfsPostApplyWhere(Predicate<ExpressionTreeNode> predicate, Consumer<ExpressionTreeNode> action) {
		for (ExpressionTreeNode child : children) {
			child.dfsPostApplyWhere(predicate, action);
			
			if (predicate.test(child))
				action.accept(child);
		}
	}
	
	/** Gets all the descendants of the tree which are of type theClass. */
	public List<ExpressionTreeNode> getDescendantsOfClass(Class<?> theClass) {
		ArrayList<ExpressionTreeNode> ans = new ArrayList<ExpressionTreeNode>();
		
		dfsPostApplyWhere(node -> node.getClass().equals(theClass), node -> ans.add(node));
		
		return ans;
	}
	
	/** Evaluates the expression tree and returns the result. */
	public abstract Object evaluate() throws InvalidExpressionTreeException;
}
