package expressions;

/** An expression tree node which accumulates the result values of an arbitrary number of children through an accumulator function, with the accumulator initialized to the result value of the first child. */
public class FirstChildAccumulatorExpressionTreeNode extends ExpressionTreeNode {
	IExpressionTreeAccumulator accumulator;
	Class<?> childClass;

	
	
	/**
	 * @param childClass The classes the children values should have.
	 * @param accumulator The accumulator that is invoked for each child when evaluate is called.
	 */
	public FirstChildAccumulatorExpressionTreeNode(Class<?> childClass, IExpressionTreeAccumulator accumulator) {
		super();
		this.childClass = childClass;
		this.accumulator = accumulator;
	}
	
	/** Accumulates the values initializing the accumulator to the first child's result, and then returns the value of the accumulator.
	 * @throws IllegalStateException If this node has no children.
	 */
	@Override
	public Object evaluate() 
			throws ExpressionTreeTypeException  {
		
		if (children.size() <= 0)
			throw new IllegalStateException("An accumulator node must have at least one children before it can be evaluated.");

		Object result = evaluateChildAndTypeCheck(0, childClass);
	    for (int i = 1; i < children.size(); ++i)
	    	result = accumulator.accumulate(result, evaluateChildAndTypeCheck(i, childClass));
	    
	    return result;
	}
}
