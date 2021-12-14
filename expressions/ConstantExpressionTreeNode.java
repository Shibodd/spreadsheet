package expressions;

public class ConstantExpressionTreeNode extends ExpressionTreeNode {
	double value;
	
	public ConstantExpressionTreeNode(double value) {
		super();
		this.value = value;
	}

	@Override
	public void addChild(ExpressionTreeNode node) throws InvalidExpressionTreeException { 
		throw new InvalidExpressionTreeException("Constants expect no children.");
	}
	
	@Override
	public double evaluate() throws InvalidExpressionTreeException {
		return value;
	}
}
