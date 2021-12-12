package spreadsheet.expressions;

public class UnknownFunctionException extends Exception {
	public UnknownFunctionException() { super(); }
	public UnknownFunctionException(String message) { super(message); }
	public UnknownFunctionException(Throwable cause) { super(cause); }
	public UnknownFunctionException(String message, Throwable cause) { super(message, cause); }
}
