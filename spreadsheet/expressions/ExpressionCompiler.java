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
	
	public ExpressionCompiler() throws AmbiguousFunctionException {
		this.tokenizer = new Tokenizer(ExpressionTokenType.toLanguage());
		this.library = new FunctionLibrary();
		library.registerFunction( new FunctionDefinition("log", 1, () -> BaseLibrary.log()) );
		library.registerFunction( new FunctionDefinition("log", 2, () -> BaseLibrary.logBase()) );
	}
	
	public ExpressionTreeNode compile(String expression) throws TokenizeException, Exception {
		tokenizer.reset(expression);
		
		ExpressionTreeNode compiled = readExpression();
		if (!tokenizer.exhausted())
			throw new Exception("A tail of the expression could not be compiled.");
		return compiled;
	}

	private ExpressionTreeNode readExpression() throws TokenizeException, Exception {
		ExpressionTreeNode fact1 = readFactor();
		if (fact1 == null)
			return null;
		
		Token op_token = tokenizer.nextToken();
		if (op_token == null)
			return fact1;
		if (op_token.type != ExpressionTokenType.EXPRESSION_OPERATOR.id) {
			tokenizer.pushBack(op_token);
			return fact1;
		}
		
		ExpressionTreeNode expr = readExpression();
		if (expr == null)
			throw new Exception("Unexpected end.");
		
		ExpressionTreeNode operation; 
		if (op_token.token.equals("+"))
			operation = BaseLibrary.sum();
		else if (op_token.token.equals("-"))
			operation = BaseLibrary.subtraction();
		else
			throw new Error("Unexpected operator due to a program error.");
		
		operation.addChild(fact1);
		operation.addChild(expr);
		
		return operation;
	}

	private ExpressionTreeNode readOperand() throws TokenizeException, Exception {
		Token operand_tok = tokenizer.nextToken();
		if (operand_tok == null)
			return null;
		
		if (operand_tok.type == ExpressionTokenType.NUMBER.id)
			return BaseLibrary.constant(Double.parseDouble(operand_tok.token));		
		else if (operand_tok.type == ExpressionTokenType.IDENTIFIER.id) {
			Token next = tokenizer.nextToken();
			if (next == null || next.type != ExpressionTokenType.OPENING_PARENS.id)
				return new ConstantExpressionTreeNode(1000d); // RETURN A GETTER TO THE CELL!
			
			// It's a function. Read the function parameters.
			// expressions separated by commas until closing parens
			ArrayList<ExpressionTreeNode> parameters = new ArrayList<ExpressionTreeNode>();
			while (true) {
				ExpressionTreeNode expr = readExpression();
				if (expr == null && tokenizer.exhausted())
					throw new Exception("Unexpected end.");
				
				parameters.add(expr);
				
				Token paramTok = tokenizer.nextToken();
				if (paramTok.type == ExpressionTokenType.CLOSING_PARENS.id)
					break;
				if (paramTok.type != ExpressionTokenType.COMMA.id)
					throw new Exception("Unexpected token.");
			}
			
			FunctionExpressionTreeNode func = library.makeFunctionInstance(operand_tok.token, parameters.size());
			for (ExpressionTreeNode param : parameters)
				func.addChild(param);
			
			return func;
		}
		else if (operand_tok.type == ExpressionTokenType.OPENING_PARENS.id) {
			ExpressionTreeNode expr = readExpression();
			Token tok = tokenizer.nextToken();
			if (tok == null)
				throw new Exception("Unexpected end.");
			
			if (tok.type != ExpressionTokenType.CLOSING_PARENS.id)
				throw new Exception("Unexpected token.");
			return expr;
		}
		else {
			tokenizer.pushBack(operand_tok);
			return null;
		}
	}
	
	private ExpressionTreeNode readFactor() throws TokenizeException, Exception {
		ExpressionTreeNode operand = readOperand();
		if (operand == null)
			return null;
		
		Token op_token = tokenizer.nextToken();
		if (op_token == null)
			return operand;
		if (op_token.type != ExpressionTokenType.FACTOR_OPERATOR.id) {
			tokenizer.pushBack(op_token);
			return operand;
		}
		
		ExpressionTreeNode secondOperand = readFactor();
		if (secondOperand == null)
			throw new Exception("Unexpected end.");
		
		ExpressionTreeNode operation; 
		if (op_token.token.equals("*"))
			operation = BaseLibrary.product();
		else if (op_token.token.equals("/"))
			operation = BaseLibrary.division();
		else
			throw new Error("Unexpected operator due to a program error.");
		
		operation.addChild(operand);
		operation.addChild(secondOperand);
		
		return operation;
	}
}
