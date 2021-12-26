package tokenizer;

public class TokenType {
	public final String regex;
	public final String description;
	
	public TokenType(String regex, String description) {
		this.regex = regex;
		this.description = description;
	}
}
