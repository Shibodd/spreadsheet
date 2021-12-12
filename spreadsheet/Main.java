package spreadsheet;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

import spreadsheet.expressions.ExpressionTokenType;
import tokenizer.Token;
import tokenizer.TokenizeException;
import tokenizer.Tokenizer;

public class Main {	
	public static void main(String[] args) throws TokenizeException {
		Tokenizer tokenizer = new Tokenizer(ExpressionTokenType.toLanguage());

		tokenizer.reset("log(525 / 55 + 22)");
		
		
		while (!tokenizer.eof()) {
			Token token = tokenizer.nextToken();
			
			System.out.println(token.type + ", " + token.token);
		}
	}
}