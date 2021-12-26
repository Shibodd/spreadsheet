package expressions;

import java.util.function.Function;
/** An expression tree node which executes a function, with the children's result value as parameters. */
public class FunctionExpressionTreeNode extends ExpressionTreeNode {
	Function<Object[], Object> function;
	Class<?>[] parameterClasses;
	
	
	/**
	 * @param resultType The result class of the function.
	 * @param parameterClasses An array containing the classes of the parameters. The amount of parameters is determined by the length of this array.
	 * @param function The function to execute.
	 */
	public FunctionExpressionTreeNode(Class<?> resultType, Class<?>[] parameterClasses, Function<Object[], Object> function) {
		super(resultType);
		
		this.parameterClasses =	parameterClasses;
		this.function = function;
	}

	/** Adds a children to this node.
	 * Parameters are positional, so children should be passed in order described by the parameterClasses parameter in the constructor.
	 * @throws InvalidExpressionTreeException When too many parameters are provided or node has a different result class from what is expected as the current parameter.
	 */
	@Override
	public void addChild(ExpressionTreeNode node) 
			throws InvalidExpressionTreeException {
		
		if (children.size() >= parameterClasses.length)
			throw new InvalidExpressionTreeException(
					String.format("Too many parameters provided - this function only expects %d.", parameterClasses.length)
			);
		
		if (!node.resultClass.equals(parameterClasses[children.size()]))
			throw new InvalidExpressionTreeException(
					String.format("The class %s of the %d parameter differs from the declared class %s.", 
							node.resultClass.getName(), children.size(), parameterClasses[children.size()])
			);
		
		super.addChild(node);
	}

	
	/** Evaluates the function, by passing the children's result values as parameters to the function passed in the constructor.
	 * @throws InvalidExpressionTreeException When the amount of children is less than the expected parameter count described by the length of the parameterClasses parameter in the constructor.
	 */
	@Override
	public Object evaluate() 
			throws InvalidExpressionTreeException {
		
		if (children.size() != parameterClasses.length)
			throw new InvalidExpressionTreeException(
					String.format("This function node expects %d children, but %d were provided.", parameterClasses.length, children.size())
			);

		Object[] parameters = new Object[parameterClasses.length];
		for (int i = 0; i < parameterClasses.length; ++i)
			parameters[i] = children.get(i).evaluate();
		
		return resultTypeCheck(function.apply(parameters));
	}
}