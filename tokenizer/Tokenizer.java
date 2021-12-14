package tokenizer;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tokenizer  {
	Pattern[] language;
	Matcher[] matchers;
	
	String expression;
	int char_i;
	
	Stack<Token> tokenQueue;
	
	public Tokenizer(String[] language) {
		this.language = 
				Arrays.stream(language)
					.map(x -> Pattern.compile("^" + x))
					.toArray(count -> new Pattern[count]);
	}
	
	public void reset(String expression) {
		this.expression = expression;
		this.tokenQueue = new Stack<Token>();
		this.matchers = Arrays.stream(language)
					.map(x -> x.matcher(expression))
					.toArray(count -> new Matcher[count]);

		this.char_i = 0;
	}
	
	
	public boolean exhausted() {
		return tokenQueue.size() <= 0 && eof();
	}
	
	private boolean eof() {
		consumeWhitespace();
		return char_i >= expression.length();
	}
	
	public void pushBack(Token token) {
		tokenQueue.push(token);
	}
	
	public Token nextToken() throws TokenizeException {
		if (tokenQueue.size() > 0)
			return tokenQueue.pop();
		else
			return readToken();
	}
	
	private Token readToken() throws TokenizeException {
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
	
	private void consumeWhitespace() {
		while (char_i < expression.length() && Character.isWhitespace(expression.charAt(char_i)))
			++char_i;
	}
}