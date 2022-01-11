package expressions;

public class ExpressionTreeTypeException extends Exception {
	public final ExpressionTreeNode node;
	public final Class<?> valueClass;
	public final Class<?> expectedClass;
	
	public ExpressionTreeTypeException(ExpressionTreeNode node, Class<?> valueClass, Class<?> expectedClass) { 
		super();
		
		this.node = node;
		this.valueClass = valueClass;
		this.expectedClass = expectedClass;
	}
	
	@Override
	public String getMessage() {
		return String.format("A child of the tree has result type %s, but the expected type is %s.", valueClass.getName(), expectedClass.getName());
	}
}