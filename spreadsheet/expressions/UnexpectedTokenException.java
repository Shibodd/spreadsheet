package spreadsheet.expressions;

import java.util.Arrays;

import tokenizer.Token;
import tokenizer.TokenType;

public class UnexpectedTokenException extends Exception {
	public final Token unexpectedToken;
	public final TokenType[] expectedTokens;
	
	public UnexpectedTokenException(String message) {
		super(message);
		
		unexpectedToken = null;
		expectedTokens = null;
	}
	
	public UnexpectedTokenException(Token unexpectedToken, TokenType... expectedTokens) {
		super();
		
		this.unexpectedToken = unexpectedToken;
		this.expectedTokens = expectedTokens;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getMessage() {
		if (unexpectedToken == null)
			return super.getMessage();
		else if (expectedTokens == null || expectedTokens.length <= 0) {
			return String.format("Unexpected token \"%s\" found.", unexpectedToken.token);
		} else {
			return String.format("Unexpected token \"%s\" (%s) found, when one of %s was expected.", 
					unexpectedToken.token,
					unexpectedToken.type.description,
					String.join(", ", (Iterable<String>)Arrays.stream(expectedTokens).map(x -> x.description))
			);
		}
	}
}
