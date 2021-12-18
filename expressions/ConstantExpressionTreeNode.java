package expressions;

public class ConstantExpressionTreeNode extends ExpressionTreeNode {
	Object value;
	
	public ConstantExpressionTreeNode(Object value) {
		super(value.getClass());
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
