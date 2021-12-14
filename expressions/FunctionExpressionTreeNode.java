package expressions;

public class FunctionExpressionTreeNode extends ExpressionTreeNode {
	IExpressionTreeFunction function;
	int parameterCount;
	
	public FunctionExpressionTreeNode(int parameterCount, IExpressionTreeFunction function) {
		super();
		
		this.parameterCount = parameterCount;
		this.function = function;
	}

	@Override
	public void addChild(ExpressionTreeNode node) 
			throws InvalidExpressionTreeException {
		
		if (children.size() >= parameterCount)
			throw new InvalidExpressionTreeException(
					String.format("Too many parameters provided - this function only expects %d.", parameterCount)
			);
		
		super.addChild(node);
	}

	@Override
	public double evaluate() 
			throws InvalidExpressionTreeException {
		
		if (children.size() != parameterCount)
			throw new InvalidExpressionTreeException(
					String.format("This function node expects %d children, but %d were provided.", parameterCount, children.size())
			);

		double[] parameters = new double[parameterCount];
		for (int i = 0; i < parameterCount; ++i)
			parameters[i] = children.get(i).evaluate();
		
		return function.apply(parameters);
	}
}