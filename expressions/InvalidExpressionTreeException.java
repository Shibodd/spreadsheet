package expressions;

public class InvalidExpressionTreeException extends Exception {
	public InvalidExpressionTreeException() { super(); }
	public InvalidExpressionTreeException(String message) { super(message); }
	public InvalidExpressionTreeException(Throwable cause) { super(cause); }
	public InvalidExpressionTreeException(String message, Throwable cause) { super(message, cause); }
}