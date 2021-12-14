package spreadsheet.expressions;

public enum ExpressionTokenType {
			NUMBER(0, "\\d+(\\.\\d+)?([eE][+\\-]?\\d+)?|0x[0-9A-Fa-f]+"),
	  EXPRESSION_OPERATOR(1, "[+\\-]"),
	  FACTOR_OPERATOR(2, "[*/]"),
		IDENTIFIER(3, "[A-Za-z_][A-Za-z_0-9]+"),
	  OPENING_PARENS(4, "\\("),
	  CLOSING_PARENS(5, "\\)"),
			 COMMA(6, ",");
	
	public final int id;
	public final String pattern;
	ExpressionTokenType(int id, String pattern) {
		this.id = id;
		this.pattern = pattern;
	}
	
	public static String[] toLanguage() {
		ExpressionTokenType[] types = ExpressionTokenType.class.getEnumConstants();
		
		String[] lang = new String[types.length];
		for (ExpressionTokenType type : types) {
			assert lang[type.id] == null;
			lang[type.id] = type.pattern;
		}
		
		return lang;
	}
}