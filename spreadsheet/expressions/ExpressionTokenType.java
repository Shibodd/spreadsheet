package spreadsheet.expressions;

public enum ExpressionTokenType {
	Number(0, "\\d+(\\.\\d+)?([eE][+\\-]\\d+)?|0x[0-9A-Fa-f]+"),
	Operator(1, "[+\\-/*]"),
	Identifier(2, "[A-Za-z_][A-Za-z_0-9]+"),
	OpeningParen(3, "\\("),
	ClosingParen(4, "\\)"),
	Comma(5, ","),
	RangeColon(6, ":");
	
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