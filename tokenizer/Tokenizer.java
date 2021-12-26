package tokenizer;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/** Allows separating a string into tokens determined by regular expressions. */
public class Tokenizer {
	TokenType[] language;
	Pattern[] languagePatterns;
	Matcher[] matchers;
	
	String expression;
	int char_i;
	
	Stack<Token> tokenQueue;
	
	/** @param language The token types. 
	 * When reading tokens, the first token type whose regex matches determines the token read. */
	public Tokenizer(TokenType[] language) {
		this.language = language;
		this.languagePatterns = 
				Arrays.stream(language)
					.map(x -> Pattern.compile("^" + x))
					.toArray(count -> new Pattern[count]);
	}	
	
	/** Starts a new tokenization */
	public void reset(String expression) {
		this.expression = expression;
		this.tokenQueue = new Stack<Token>();
		this.matchers = Arrays.stream(languagePatterns)
					.map(x -> x.matcher(expression))
					.toArray(count -> new Matcher[count]);

		this.char_i = 0;
	}
	
	/** Returns whether there is any token left to be consumed. */
	public boolean exhausted() {
		return tokenQueue.size() <= 0 && eof();
	}
	
	
	/** Consumes any whitespace and returns whether the cursor is at the end of the expression. */ 
	private boolean eof() {
		consumeWhitespace();
		return char_i >= expression.length();
	}
	
	/** Queues token to be returned by a nextToken operation instead of parsing new tokens. */
	public void pushBack(Token token) {
		tokenQueue.push(token);
	}
	
	/** Reads a new token if there are no tokens in the pushBack queue, or else returns the first token in the queue. */
	public Token nextToken() throws TokenizeException {
		if (tokenQueue.size() > 0)
			return tokenQueue.pop();
		else
			return readToken();
	}
	
	/** Reads a new token, advancing the cursor. */
	private Token readToken() throws TokenizeException {
		if (eof())
			return null;
		
		for (int i = 0; i < matchers.length; ++i) {
			Matcher matcher = matchers[i];
			matcher.region(char_i, expression.length());
			
			if (matcher.find()) {
				char_i = matcher.end();
				return new Token(language[i], matcher.group());
			}
		}
		
		throw new TokenizeException(String.format("Unexpected character %c in position %d in string %s.", expression.charAt(char_i), char_i, expression));
	}
	
	/** Advances the cursor until the next non whitespace character. */
	private void consumeWhitespace() {
		while (char_i < expression.length() && Character.isWhitespace(expression.charAt(char_i)))
			++char_i;
	}
}