package tokenizer;

public class Token {
	public TokenType type;
	public String token;
	
	public Token(TokenType type, String token) {
		this.type = type;
		this.token = token;
	}
}
