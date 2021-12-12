package expressions;

import java.util.function.Function;

public class UnaryExpressionTreeNode extends ExpressionTreeNode {
	Class<?> childClass;
	Function<Object, Object> function;
	
	public UnaryExpressionTreeNode(Class<?> resultClass, Class<?> childClass, Function<Object, Object> function) {
		super(Double.class);
		
		this.childClass = childClass;
		this.function = function;
	}

	@Override
	public void addChild(ExpressionTreeNode node) 
			throws ExpressionTreeTypeException, InvalidExpressionTreeException {
		
		if (children.size() >= 1)
			throw new InvalidExpressionTreeException("Unary nodes expect only one children.");
		
		node.typeCheck(childClass);
		super.addChild(node);
	}

	@Override
	public Object evaluate() 
			throws ExpressionTreeTypeException, InvalidExpressionTreeException {
		
		if (children.size() <= 0)
			throw new InvalidExpressionTreeException("This unary node has no children.");
		
		return function.apply(children.get(0).evaluate());
	}
}