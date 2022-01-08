package expressions;

/** An expression tree node which accumulates the result values of an arbitrary number of children through an accumulator function, with the accumulator initialized to the result value of the first child. */
public class FirstChildAccumulatorExpressionTreeNode extends ExpressionTreeNode {
	IExpressionTreeAccumulator accumulator;
	Class<?> childClass;

	public FirstChildAccumulatorExpressionTreeNode(Class<?> childClass, IExpressionTreeAccumulator accumulator) {
		super();
		this.childClass = childClass;
		this.accumulator = accumulator;
	}
	
	
	/** Adds a child to the node.
	 * @throws InvalidExpressionTreeException The result class of node differs from the expected passed as parameter to the constructor.
	 */
	@Override
	public void addChild(ExpressionTreeNode node) 
			throws InvalidExpressionTreeException {
		
		super.addChild(node);
	}
	
	
	/** Accumulates the values and returns the result.
	 * @throws InvalidExpressionTreeException No children were added to this node.
	 */
	@Override
	public Object evaluate() 
			throws InvalidExpressionTreeException  {
		
		if (children.size() <= 0)
			throw new InvalidExpressionTreeException("This accumulator node has no children and thus can't be evaluated.");

		Object result = evaluateChildAndTypeCheck(0, childClass);
	    for (int i = 1; i < children.size(); ++i)
	    	result = accumulator.accumulate(result, evaluateChildAndTypeCheck(i, childClass));
	    
	    return result;
	}
}
