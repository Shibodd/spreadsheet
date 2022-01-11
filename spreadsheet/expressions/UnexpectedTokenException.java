package spreadsheet.expressions;

import java.util.Arrays;

import tokenizer.Token;
import tokenizer.TokenType;

public class UnexpectedTokenException extends Exception {
	public final Token unexpectedToken;
	public final TokenType[] expectedTokens;
	
	public UnexpectedTokenException(Token unexpectedToken, TokenType... expectedTokens) {
		super();
		
		this.unexpectedToken = unexpectedToken;
		this.expectedTokens = expectedTokens;
	}

	@Override
	public String getMessage() {
		String expected = null;
		
		if (expectedTokens != null && expectedTokens.length > 0)		
			expected = "one of " + "\"" + String.join("\", \"", Arrays.stream(expectedTokens).map(x -> x.description).toArray(count -> new String[count])) + "\"";

		if (unexpectedToken == null)
			if (expected == null)
				return "Unexpected end of expression found.";
			else
				return String.format(
						"Unexpected end of expression found, when %s was expected.", 
						expected
				);
		else if (expected != null)
			return String.format(
					"Unexpected token \"%s\" (\"%s\") found at character %d, when %s was expected.", 
					unexpectedToken.token, 
					unexpectedToken.type.description,
					unexpectedToken.startIndex,
					expected
			);
		else
			return String.format(
					"Unexpected token \"%s\" (\"%s\") found at character %d.", 
					unexpectedToken.token, 
					unexpectedToken.type.description,
					unexpectedToken.startIndex
			);
	}
}
