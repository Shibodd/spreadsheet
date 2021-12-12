package expressions;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class FunctionExpressionTreeNode extends ExpressionTreeNode {
	Class<?>[] parametersClasses;
	IExpressionTreeFunction function;
	
	public FunctionExpressionTreeNode(Class<?> resultClass, Class<?>[] parametersClasses, IExpressionTreeFunction function) {
		super(resultClass);
		
		this.parametersClasses = parametersClasses;
		this.function = function;
	}

	@Override
	public void addChild(ExpressionTreeNode node) 
			throws ExpressionTreeTypeException, InvalidExpressionTreeException {
		
		if (children.size() >= parametersClasses.length)
			throw new InvalidExpressionTreeException(
					String.format("Too many parameters provided - this function only expects %d.", parametersClasses.length)
			);
		
		node.typeCheck(parametersClasses[children.size()]);
		super.addChild(node);
	}

	@Override
	public Object evaluate() 
			throws ExpressionTreeTypeException, InvalidExpressionTreeException {
		
		if (children.size() != parametersClasses.length)
			throw new InvalidExpressionTreeException(
					String.format("This function node expects %d children, but %d were provided.", parametersClasses.length, children.size())
			);
		
		return function.apply(Collections.unmodifiableList(children));
	}
}