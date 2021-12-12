package expressions;

public class AccumulatorExpressionTreeNode extends ExpressionTreeNode {
	IExpressionTreeAccumulator accumulator;
	Class<?> childrenClass;
	Object identity;
	
	public AccumulatorExpressionTreeNode(Class<?> resultClass, Class<?> childrenClass, Object identity, IExpressionTreeAccumulator accumulator) {
		super(resultClass);
		this.childrenClass = childrenClass;
		this.identity = identity;
		this.accumulator = accumulator;
	}
	
	@Override
	public void addChild(ExpressionTreeNode node) 
			throws ExpressionTreeTypeException, InvalidExpressionTreeException {
		
		node.typeCheck(childrenClass);
		super.addChild(node);
	}

	@Override
	public Object evaluate() 
			throws ExpressionTreeTypeException, InvalidExpressionTreeException  {
		if (children.size() <= 0)
			throw new InvalidExpressionTreeException("This accumulator node has no children and thus can't be evaluated.");

		Object result = identity;
	    for (ExpressionTreeNode child : children)
	         result = accumulator.accumulate(result, child);
	    return result;
	}
}
