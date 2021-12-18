package expressions;

public class FirstChildAccumulatorExpressionTreeNode extends ExpressionTreeNode {
	IExpressionTreeAccumulator accumulator;
	Class<?> childClass;
	
	public FirstChildAccumulatorExpressionTreeNode(Class<?> resultClass, Class<?> childClass, IExpressionTreeAccumulator accumulator) {
		super(resultClass);
		this.childClass = childClass;
		this.accumulator = accumulator;
	}
	
	@Override
	public void addChild(ExpressionTreeNode node) 
			throws InvalidExpressionTreeException {
		
		Class<?> childClass = node.getResultType();
		if (!childClass.equals(this.childClass))
			throw new InvalidExpressionTreeException(
					String.format(
							"This node expects childs of type %s, but a parameter of type %s was provided.", 
							this.childClass.getName(), 
							childClass.getName())
			);
		
		super.addChild(node);
	}

	@Override
	public Object evaluate() 
			throws InvalidExpressionTreeException  {
		
		if (children.size() <= 0)
			throw new InvalidExpressionTreeException("This accumulator node has no children and thus can't be evaluated.");

		Object result = children.get(0).evaluate();
	    for (int i = 1; i < children.size(); ++i)
	    	result = accumulator.accumulate(result, children.get(i).evaluate());
	    
	    return result;
	}
}
