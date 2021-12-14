package expressions;

import java.util.function.Function;

public class UnaryExpressionTreeNode extends ExpressionTreeNode {
	Function<Double, Double> function;
	
	public UnaryExpressionTreeNode(Function<Double, Double> function) {
		super();
		
		this.function = function;
	}

	@Override
	public void addChild(ExpressionTreeNode node) throws InvalidExpressionTreeException {
		if (children.size() >= 1)
			throw new InvalidExpressionTreeException("Unary nodes expect only one children.");
		
		super.addChild(node);
	}

	@Override
	public double evaluate() 
			throws InvalidExpressionTreeException {
		
		if (children.size() <= 0)
			throw new InvalidExpressionTreeException("This unary node has no children.");
		
		return function.apply(children.get(0).evaluate());
	}
}