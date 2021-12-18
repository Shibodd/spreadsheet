package expressions;

import java.util.function.Function;

public class FunctionExpressionTreeNode extends ExpressionTreeNode {
	Function<Object[], Object> function;
	Class<?>[] parameterClasses;
	
	public FunctionExpressionTreeNode(Class<?> resultType, Class<?>[] parameterClasses, Function<Object[], Object> function) {
		super(resultType);
		
		this.parameterClasses =	parameterClasses;
		this.function = function;
	}

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