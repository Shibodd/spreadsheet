package spreadsheet.expressions;

public class AmbiguousFunctionException extends RuntimeException {
	public final FunctionDefinition definition;
	
	public AmbiguousFunctionException(FunctionDefinition definition) { 
		super(); 
		this.definition = definition; 
	}
	
	@Override
	public String getMessage() {
		return String.format("A function with definition %s is already defined.");
	}
}
