package spreadsheet.expressions;

import java.util.Arrays;

import tokenizer.TokenType;

public enum ExpressionTokenType {
	NUMBER(
			"Number",
			"\\d+(\\.\\d+)?([eE][+\\-]?\\d+)?|0x[0-9A-Fa-f]+"),
	
	EXPRESSION_OPERATOR(
			"+ or -",
			"[+\\-]"),
	
	TERM_OPERATOR(
			"* or /",
			"[*/]"),
	
	IDENTIFIER(
			"Identifier",
			"[A-Za-z_][A-Za-z_0-9]+"),
	
	OPENING_PARENS(
			"(",
			"\\("),
	
	CLOSING_PARENS(
			")",
			"\\)"),
	
	COLON(
			":",
			":"),
	
	COMMA(
			",",
			",");
	
	
	final TokenType type;
	
	ExpressionTokenType(String description, String pattern) {
		type = new TokenType(pattern, description);
	}
	
	/**
	 * @return An array containing all the types of the values of this enum.
	 */
	public static TokenType[] toLanguage() {
		return Arrays.stream(ExpressionTokenType.values())
				.map(x -> x.type)
				.toArray(count -> new TokenType[count]);
	}
}