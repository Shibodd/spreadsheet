package expressions;

import java.util.function.BiFunction;

public class BinaryExpressionTreeNode extends ExpressionTreeNode {
	Class<?> childrenClass;
	BiFunction<Object, Object, Object> function;
	
	public BinaryExpressionTreeNode(Class<?> resultClass, Class<?> childrenClass, BiFunction<Object, Object, Object> function) {
		super(Double.class);
		
		this.childrenClass = childrenClass;
		this.function = function;
	}

	@Override
	public void addChild(ExpressionTreeNode node) 
			throws ExpressionTreeTypeException, InvalidExpressionTreeException {
		
		if (children.size() >= 2)
			throw new InvalidExpressionTreeException("Binary nodes expect only two children.");
		
		node.typeCheck(childrenClass);
		super.addChild(node);
	}

	@Override
	public Object evaluate() 
			throws ExpressionTreeTypeException, InvalidExpressionTreeException {
		
		if (children.size() <= 0)
			throw new InvalidExpressionTreeException("This binary node has no children.");
		
		return function.apply(children.get(0).evaluate(), children.get(1).evaluate());
	}
}