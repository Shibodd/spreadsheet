package expressions;

import java.util.function.BiFunction;

public class BinaryExpressionTreeNode extends ExpressionTreeNode {
	BiFunction<Double, Double, Double> function;
	
	public BinaryExpressionTreeNode(BiFunction<Double, Double, Double> function) {
		super();
		this.function = function;
	}

	@Override
	public void addChild(ExpressionTreeNode node) 
			throws InvalidExpressionTreeException {
		
		if (children.size() >= 2)
			throw new InvalidExpressionTreeException("Binary nodes expect only two children.");
		
		super.addChild(node);
	}

	@Override
	public double evaluate() 
			throws InvalidExpressionTreeException {
		
		if (children.size() <= 0)
			throw new InvalidExpressionTreeException("This binary node has no children.");
		
		return function.apply(children.get(0).evaluate(), children.get(1).evaluate());
	}
}