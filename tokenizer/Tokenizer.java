package tokenizer;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tokenizer  {
	Pattern[] language;
	Matcher[] matchers;
	
	String expression;
	int char_i;
	
	Queue<Token> tokenQueue;
	
	public Tokenizer(String[] language) {
		this.language = 
				Arrays.stream(language)
					.map(x -> Pattern.compile("^" + x))
					.toArray(count -> new Pattern[count]);
	}
	
	public void reset(String expression) {
		this.expression = expression;
		this.tokenQueue = new ArrayDeque<Token>(4);
		this.matchers = Arrays.stream(language)
					.map(x -> x.matcher(expression))
					.toArray(count -> new Matcher[count]);

		this.char_i = 0;
	}
	
	
	public boolean eof() {
		return char_i == expression.length();
	}
	
	public void pushBack(Token token) {
		tokenQueue.add(token);
	}
	
	public Token nextToken() throws TokenizeException {
		if (tokenQueue.size() > 0)
			return tokenQueue.remove();
		else
			return readToken();
	}
	
	private Token readToken() throws TokenizeException {
		// Consume whitespace between tokens
		while (!eof() && Character.isWhitespace(expression.charAt(char_i)))
			++char_i;
		
		if (eof())
			return null;
		
		for (int i = 0; i < matchers.length; ++i) {
			Matcher matcher = matchers[i];
			matcher.region(char_i, expression.length());
			
			if (matcher.find()) {
				char_i = matcher.end();
				return new Token(i, matcher.group());
			}
		}
		
		throw new TokenizeException(String.format("Unexpected character %c in position %d in string %s.", expression.charAt(char_i), char_i, expression));
	}
}