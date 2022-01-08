package expressions;


/** An expression tree node which contains no children and always returns the value it was initialized with. */
public class ConstantExpressionTreeNode extends ExpressionTreeNode {
	Object value;
	
	public ConstantExpressionTreeNode(Object value) {
		super();
		this.value = value;
	}

	@Override
	public void addChild(ExpressionTreeNode node) throws InvalidExpressionTreeException { 
		throw new InvalidExpressionTreeException("Constants expect no children.");
	}
	
	@Override
	public Object evaluate() throws InvalidExpressionTreeException {
		return value;
	}
}
