package spreadsheet;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

import expressions.ExpressionTreeNode;
import spreadsheet.expressions.ExpressionCompiler;
import spreadsheet.expressions.ExpressionTokenType;
import tokenizer.Token;
import tokenizer.TokenizeException;
import tokenizer.Tokenizer;

public class Main {	
	public static void main(String[] args) throws Exception {
		ExpressionCompiler compiler = new ExpressionCompiler();
		ExpressionTreeNode n = compiler.compile("3 * log(100, 10) + 2 * 3.35e-6");
		
		System.out.println(n.evaluate());
	}
}