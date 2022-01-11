package expressions;


/** An expression tree node which contains no children and always returns the value it was initialized with. */
public class ConstantExpressionTreeNode extends ExpressionTreeNode {
	Object value;
	
	 
	/** @param value The value this constant node should have.
	 */
	public ConstantExpressionTreeNode(Object value) {
		this.value = value;
	}

	/** Unsupported because Constant nodes are leaves in the tree.
	 * @param node Ignored.
	 * @throws UnsupportedOperationException Always.
	 */
	@Override
	public void addChild(ExpressionTreeNode node) { 
		throw new UnsupportedOperationException("Constant nodes expect no children.");
	}
	
	/** Unsupported because Constant nodes are leaves in the tree.
	 * @param node Ignored.
	 * @throws UnsupportedOperationException Always.
	 */
	@Override
	public void removeChild(ExpressionTreeNode node) {
		throw new UnsupportedOperationException("Constant nodes expect no children.");
	}
		
	
	/** Returns the value of the constant. 
	 * @return The value this constant was initialized with.
	 * @throws ExpressionTreeTypeException Never.
	 */
	@Override
	public Object evaluate() throws ExpressionTreeTypeException {
		return value;
	}
}
