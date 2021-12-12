package spreadsheet.expressions;

public class AmbiguousFunctionException extends Exception {
	public AmbiguousFunctionException() { super(); }
	public AmbiguousFunctionException(String message) { super(message); }
	public AmbiguousFunctionException(Throwable cause) { super(cause); }
	public AmbiguousFunctionException(String message, Throwable cause) { super(message, cause); }
}
