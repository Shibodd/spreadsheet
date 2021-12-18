package expressions;


import java.util.LinkedList;
import java.util.List;

public abstract class ExpressionTreeNode {
	protected Class<?> resultClass;
	protected List<ExpressionTreeNode> children;

	public ExpressionTreeNode(Class<?> resultType) {
		children = new LinkedList<ExpressionTreeNode>();
		this.resultClass = resultType;
	}
	
	public Class<?> getResultType() { return resultClass; }
	
	public void addChild(ExpressionTreeNode node) throws InvalidExpressionTreeException { 
		children.add(node);
	}
	
	public void removeChild(ExpressionTreeNode node) {
		children.remove(node);
	}
	
	public Object resultTypeCheck(Object result) throws InvalidExpressionTreeException {
		Class<?> resultClass = result.getClass();
		
		
		if (!resultClass.equals(this.resultClass))
			throw new InvalidExpressionTreeException(String.format(
					"The declared result class is %s, but the class of the unary function result is %s.",
					this.resultClass.getName(),
					resultClass.getName()));
		
		return result;
	}

	public abstract Object evaluate() throws InvalidExpressionTreeException;
}
