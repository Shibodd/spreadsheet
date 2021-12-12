package spreadsheet.expressions;

import java.util.ArrayList;

import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.FunctionExpressionTreeNode;
import tokenizer.Token;
import tokenizer.TokenizeException;
import tokenizer.Tokenizer;

public class ExpressionCompiler {	
	public final Tokenizer tokenizer;
	public final FunctionLibrary library;
	
	public ExpressionCompiler() {
		this.tokenizer = new Tokenizer(ExpressionTokenType.toLanguage());
		this.library = null;
	}
	
	public ExpressionTreeNode compile(String expression) {
		tokenizer.reset(expression);
		return null;
	}
	
	private FunctionExpressionTreeNode readFunction() throws TokenizeException, Exception {
		Token identifier_tok = tokenizer.nextToken();
		if (identifier_tok == null)
			return null;
		
		if (identifier_tok.type != ExpressionTokenType.Identifier.id)
			throw new Exception("Unexpected token.");
		
		if (tokenizer.nextToken().type != ExpressionTokenType.OpeningParen.id)
			throw new Exception("Unexpected token.");
		
		ArrayList<ExpressionTreeNode> parameters = new ArrayList<ExpressionTreeNode>();		
		
		Token tok;		
		while ((tok = tokenizer.nextToken()) == null || tok.type != ExpressionTokenType.ClosingParen.id) {
			if (tok == null)
				throw new Exception("Unexpected end.");
			
			tokenizer.pushBack(tok);
			
			ExpressionTreeNode param = readExpression();
			if (param == null)
				throw new Exception("Wtf?");
			
			parameters.add(param);
			
			tok = tokenizer.nextToken();
			
			if (tok.type == ExpressionTokenType.ClosingParen.id)
				break;
			else if (tok.type != ExpressionTokenType.Comma.id)
				throw new Exception("Unexpected token.");
		}
		
		return library.makeFunctionInstance(
				identifier_tok.token, 
				parameters.stream()
					.map(x -> x.getResultClass())
					.toArray(count -> new Class<?>[count])
		);
	}
	
	private ExpressionTreeNode readExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	private ExpressionTreeNode readIdentifier() throws TokenizeException, Exception {
		Token identifier_tok = tokenizer.nextToken();
		if (identifier_tok == null)
			return null;
		
		if (identifier_tok.type != ExpressionTokenType.Identifier.id)
			throw new Exception("Unexpected token.");
		
		
		Token next = tokenizer.nextToken();
		if (next.type == ExpressionTokenType.OpeningParen.id) {
			tokenizer.pushBack(next);
			tokenizer.pushBack(identifier_tok);
			
			return readFunction();
		}
		else 
		{
			return null; // TODO: getter to identifier?
		}
	}
	
	private ConstantExpressionTreeNode readConstant() throws TokenizeException, Exception {
		Token tok = tokenizer.nextToken();
		if (tok == null)
			return null;
		
		if (tok.type == ExpressionTokenType.Number.id)
			return BaseLibrary.constant(Double.parseDouble(tok.token));
		else
			throw new Exception("Unexpected token.");	
	}

	private ExpressionTreeNode readOperand() throws TokenizeException, Exception {
		Token tok = tokenizer.nextToken();
		if (tok == null)
			return null;
		
		if (tok.type == ExpressionTokenType.Identifier.id) {
			tokenizer.pushBack(tok);
			return readIdentifier();
		}
		else if (tok.type == ExpressionTokenType.Number.id) {
			tokenizer.pushBack(tok);
			return readConstant();
		}
		else
			throw new Exception("Unexpected token.");
	}
	
	private ExpressionOperator readOperator() throws Exception {
		Token tok = tokenizer.nextToken();
		
		if (tok == null)
			return null;
		
		if (tok.type != ExpressionTokenType.Operator.id)
			throw new Exception("Unexpected token.");
		
		switch (tok.token) {
			case "+": return ExpressionOperator.Sum;
			case "-": return ExpressionOperator.Subtraction;
			case "*": return ExpressionOperator.Product;
		    case "/": return ExpressionOperator.Division;
			 default: throw new Exception("Unexpected token.");
		}
	}
	
	private ExpressionTreeNode readFactor() throws TokenizeException, Exception {
		ExpressionTreeNode operand = readOperand();
		if (operand == null)
			return null;
		
		Token tok = tokenizer.nextToken();
		
		ExpressionOperator op = readOperator();
		if (op == null || (op != ExpressionOperator.Product && op != ExpressionOperator.Division))
			return operand;	
		
		// The operator is either a product or a division.
		ExpressionTreeNode secondOperand = readFactor();
		if (secondOperand == null)
			throw new Exception("Unexpected end.");
		
		
		ExpressionTreeNode factor;
		if (op == ExpressionOperator.Product) {
			factor = BaseLibrary.product();
			factor.addChild(operand);
			factor.addChild(secondOperand);
		} else {
			
		}
		return factor;
	}
}
