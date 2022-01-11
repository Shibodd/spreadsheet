package spreadsheet.expressions;

public class UnknownFunctionException extends Exception {
	public final FunctionDefinition definition;
	
	public UnknownFunctionException(FunctionDefinition definition) { 
		super(); 
		this.definition = definition; 
	}
	
	@Override
	public String getMessage() {
		return String.format("There is no known function with definition %s.", definition);
	}
}