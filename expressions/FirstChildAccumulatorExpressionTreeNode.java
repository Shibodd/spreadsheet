package expressions;

public class FirstChildAccumulatorExpressionTreeNode extends ExpressionTreeNode {
	IExpressionTreeAccumulator accumulator;

	public FirstChildAccumulatorExpressionTreeNode(IExpressionTreeAccumulator accumulator) {
		super();
		this.accumulator = accumulator;
	}
	
	@Override
	public void addChild(ExpressionTreeNode node) 
			throws InvalidExpressionTreeException {
		super.addChild(node);
	}

	@Override
	public double evaluate() 
			throws InvalidExpressionTreeException  {
		
		if (children.size() <= 0)
			throw new InvalidExpressionTreeException("This accumulator node has no children and thus can't be evaluated.");

		double result = children.get(0).evaluate();
	    for (int i = 1; i < children.size(); ++i)
	    	result = accumulator.accumulate(result, children.get(i).evaluate());
	    
	    return result;
	}
}
