package tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/** Allows separating a string into tokens determined by regular expressions. */
public class Tokenizer {
	TokenType[] language;
	Pattern[] languagePatterns;

	List<Token> tokens;
	int token_i;
	
	String expression;
	int char_i;
	
	/** @param language The token types. 
	 * When reading tokens, the first token type whose regex matches determines the token read. */
	public Tokenizer(TokenType[] language) {
		this.language = language;
		this.languagePatterns = 
				Arrays.stream(language)
					.map(x -> Pattern.compile("^" + x.regex))
					.toArray(count -> new Pattern[count]);
	}	
	
	/** Tokenizes an expression. */
	public void tokenize(String expression) throws TokenizeException {
		this.expression = expression;
		this.tokens = new ArrayList<Token>();
		
		this.char_i = 0;
		this.token_i = 0;
		
		Matcher[] matchers = Arrays.stream(languagePatterns)
				.map(x -> x.matcher(expression))
				.toArray(count -> new Matcher[count]);
		Token tok;
		while ((tok = readToken(matchers)) != null)
			tokens.add(tok);
	}
	
	public Token nextToken() {
		Token tok = token_i >= tokens.size()? null : tokens.get(token_i++);
		
		return tok;
	}
	
	public int tellToken() {
		return token_i;
	}
	
	public void pushBack() {
		if (token_i > 0)
			--token_i;
		else
			throw new IllegalStateException("No more tokens can be pushed back.");
	}
	
	public Token peek() {
		if (exhausted())
			return null;
		else
			return tokens.get(token_i);
	}
	
	public void seekToken(int token_i) {
		if (token_i < 0 || token_i >= tokens.size())
			throw new IllegalArgumentException();
		
		this.token_i = token_i;
	}
	
	public boolean exhausted() {
		return token_i >= tokens.size();
	}

	/** Reads a new token, advancing the cursor. */
	private Token readToken(Matcher[] matchers) throws TokenizeException {
		// Consume any whitespace.
		while (char_i < expression.length() && Character.isWhitespace(expression.charAt(char_i)))
			++char_i;
		
		// Check if there's anything left to read.
		if (char_i >= expression.length())
			return null;
		
		for (int i = 0; i < matchers.length; ++i) {
			Matcher matcher = matchers[i];
			matcher.region(char_i, expression.length());
			
			if (matcher.find()) {
				char_i = matcher.end();
				return new Token(language[i], matcher.group(), matcher.start());
			}
		}
		
		throw new TokenizeException(String.format("Unexpected character \"%c\" at position %d.", expression.charAt(char_i), char_i, expression));
	}
}