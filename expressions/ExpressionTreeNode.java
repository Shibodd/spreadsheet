package expressions;


import java.util.LinkedList;
import java.util.List;

public abstract class ExpressionTreeNode {
	protected List<ExpressionTreeNode> children;
	private Class<?> resultClass;
	
	public ExpressionTreeNode(Class<?> resultClass) {
		children = new LinkedList<ExpressionTreeNode>();
	}
	
	
	public void typeCheck(Class<?> expectedType) throws ExpressionTreeTypeException {
		if (!resultClass.equals(expectedType))
			throw new ExpressionTreeTypeException(String.format("Wrong type for child %d of %s: Expecting %s, got %s", children.size(), getClass().getName(), expectedType.getName(), resultClass.getName()));
	}
	
	public void addChild(ExpressionTreeNode node) throws ExpressionTreeTypeException, InvalidExpressionTreeException { 
		children.add(node);
	}
	public void removeChild(ExpressionTreeNode node) {
		children.remove(node);
	}

	public abstract Object evaluate() throws ExpressionTreeTypeException, InvalidExpressionTreeException;
	public Class<?> getResultClass() { return resultClass; }
}
