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
	
	
	/** Adds node as a child of this node.
	 * @param node The node to add.
	 */
	public void addChild(ExpressionTreeNode node) { 
		children.add(node);
	}
	
	
	/** Removes a child node.
	 * @param node The node to remove.
	 */
	public void removeChild(ExpressionTreeNode node) {
		children.remove(node);
	}
	
	
	/** Applies action in post-order to each descendant for which predicate tests true.
	 * @param predicate The predicate to test whether to apply the action.
	 * @param action The action to perform.
	 */
	private void dfsPostApplyWhere(Predicate<ExpressionTreeNode> predicate, Consumer<ExpressionTreeNode> action) {
		for (ExpressionTreeNode child : children)
			child.dfsPostApplyWhere(predicate, action);
		
		if (predicate.test(this))
			action.accept(this);
	}
	
	/** Gets all the descendants of the tree of a given type.
	 * @param theClass The class of the descendants that have to be returned.
	 * @return A list containing the descendants of type theClass. 
	 */
	public List<ExpressionTreeNode> getNodesOfClass(Class<?> theClass) {
		ArrayList<ExpressionTreeNode> ans = new ArrayList<ExpressionTreeNode>();
		
		dfsPostApplyWhere(node -> node.getClass().equals(theClass), node -> ans.add(node));
		
		return ans;
	}

	
	/** Evaluates the expression tree and returns the result.
	 * @return The result.
	 * @throws ExpressionTreeTypeException If the type of a child or the type of the result doesn't match the expected type.
	 */
	public abstract Object evaluate() throws ExpressionTreeTypeException;
	
	/** Returns the result of a child and performs type checking of its result.
	 * @param i The index of the child.
	 * @param expectedClass The class the child's result should be an instance of.
	 * @return The result of the child.
	 * @throws ExpressionTreeTypeException If the type of the result does not match expectedClass.
	 */
	public Object evaluateChildAndTypeCheck(int i, Class<?> expectedClass) throws ExpressionTreeTypeException {
		Object value = children.get(i).evaluate();
		
		Class<?> valueClass = value == null? null : value.getClass();
		
		if (valueClass == null || !expectedClass.equals(valueClass))
			throw new ExpressionTreeTypeException(children.get(i), valueClass, expectedClass);
		
		return value;
	}
}
