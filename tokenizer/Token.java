package tokenizer;

public class Token {
	public final TokenType type;
	public final String token;
	public final int startIndex;
	
	public Token(TokenType type, String token, int index) {
		this.type = type;
		this.token = token;
		this.startIndex = index;
	}
	
	@Override
	public String toString() {
		return String.format("%s \"%s\" at character %d", type.description, token, startIndex);
	}
}
