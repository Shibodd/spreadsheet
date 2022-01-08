package spreadsheet.expressions;

import spreadsheet.Geometry.GridVector2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.FunctionExpressionTreeNode;
import expressions.InvalidExpressionTreeException;
import spreadsheet.Cell;
import spreadsheet.CellIdentifiers;
import spreadsheet.Range;
import spreadsheet.Spreadsheet;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.TokenizeException;
import tokenizer.Tokenizer;


/** Provides functionality for compiling expressions to Expression Trees. */
public class ExpressionCompiler {	
	public final Tokenizer tokenizer;
	public final FunctionLibrary library;
	final Spreadsheet spreadsheet;
	
	public ExpressionCompiler(Spreadsheet spreadsheet, FunctionLibrary functionLibrary) throws AmbiguousFunctionException {
		this.tokenizer = new Tokenizer(ExpressionTokenType.toLanguage());
		this.library = functionLibrary;
		this.spreadsheet = spreadsheet;
	}
	
	
	/** Compiles the expression into an expression tree and returns the root. 
	 * @throws UnknownFunctionException 
	 * @throws InvalidExpressionTreeException 
	 * @throws UnexpectedTokenException */
	public ExpressionTreeNode compile(String expression) throws TokenizeException, UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException{
		tokenizer.tokenize(expression);
		
		ExpressionTreeNode compiled = readExpression();
		
		if (!tokenizer.exhausted())
			throw new UnexpectedTokenException(tokenizer.nextToken(), null);
		
		// throw if the tokenizer isn't exhausted
		return compiled;
	}

	/** Compiles an expression.
	 * 
	 * @return The root of the tree, or null if the tokenizer is exhausted or the upcoming tokens cannot possibly represent an expression.
	 * @throws TokenizeException An unexpected character was found by the tokenizer.
	 * @throws UnexpectedTokenException When tokens that might represent an expression were read, but an unexpected token was then found.
	 * @throws InvalidExpressionTreeException 
	 * @throws UnknownFunctionException 
	 */
	private ExpressionTreeNode readExpression() throws UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException {
		System.out.println("expr\t" + tokenizer.peek());
		
		ExpressionTreeNode fact1 = readTerm();
		
		Token op_token = tokenizer.nextToken();
		if (op_token == null)
			return fact1;
		if (op_token.type != ExpressionTokenType.EXPRESSION_OPERATOR.type) {
			tokenizer.pushBack();
			return fact1;
		}
		
		ExpressionTreeNode expr = readExpression();
		
		ExpressionTreeNode operation; 
		if (op_token.token.equals("+"))
			operation = BaseLibrary.op_sum();
		else if (op_token.token.equals("-"))
			operation = BaseLibrary.op_subtraction();
		else
			throw new Error("Unexpected operator due to a program error.");
		
		operation.addChild(fact1);
		operation.addChild(expr);
		
		return operation;
	}
	
	
	/** Reads a token and performs type checks.
	 * @param eofIsExpected If false, throws if the token read is null.
	 * @param tokenTypes The allowed token types. If null, any token is accepted and only a null check is performed, considering eofIsExpected.
	 */
	private Token expectTokenOf(boolean eofIsExpected, TokenType... tokenTypes) throws UnexpectedTokenException {
		Token tok = tokenizer.nextToken();
		if (tok == null) {
			if (eofIsExpected)
				return null;
			else
				throw new UnexpectedTokenException(null, tokenTypes);
		}
		
		if (tokenTypes == null || tokenTypes.length == 0 || Arrays.asList(tokenTypes).contains(tok.type))
			return tok;
		else {
			throw new UnexpectedTokenException(tok, tokenTypes);
		}
	}
	
	private List<ExpressionTreeNode> readFunctionParameters() throws UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException {
		ArrayList<ExpressionTreeNode> parameters = new ArrayList<ExpressionTreeNode>();
		
		Token tok = tokenizer.nextToken();
		if (tok != null && tok.type == ExpressionTokenType.CLOSING_PARENS.type)
			return parameters;
		else
			tokenizer.pushBack();
		
		while (true) {
			ExpressionTreeNode expr = readExpression();				
			
			parameters.add(expr);

			Token paramTok = expectTokenOf(false, ExpressionTokenType.CLOSING_PARENS.type, ExpressionTokenType.COMMA.type);
			if (paramTok.type == ExpressionTokenType.CLOSING_PARENS.type)
				break;
		}
		
		return parameters;	
	}
	
	
	/** Reads an operand (a parenthesized expression, a number constant, a function call, a range, a cell. */
	private ExpressionTreeNode readOperand() throws UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException {
		System.out.println("operand\t" + tokenizer.peek());
		
		Token operand_tok = expectTokenOf(false, ExpressionTokenType.NUMBER.type, ExpressionTokenType.OPENING_PARENS.type, ExpressionTokenType.IDENTIFIER.type);
		
		// OPERAND := NUMBER | FUNCTION | RANGE | CELL | \(EXPRESSION\)
		// FUNCTION := IDENTIFIER\( (EXPRESSION(,EXPRESSION)*)? \)
		// RANGE := IDENTIFIER:IDENTIFIER
		// CELL := IDENTIFIER
		
		if (operand_tok.type == ExpressionTokenType.NUMBER.type)
			// NUMBER
			return new ConstantExpressionTreeNode(Double.parseDouble(operand_tok.token));		
		else if (operand_tok.type == ExpressionTokenType.IDENTIFIER.type) {
			Token next = tokenizer.nextToken();
			
			if (next != null && next.type == ExpressionTokenType.OPENING_PARENS.type) {
				// FUNCTION
				List<ExpressionTreeNode> parameters = readFunctionParameters();
				
				FunctionExpressionTreeNode func = library.makeFunctionInstance(operand_tok.token, parameters.size());
				for (ExpressionTreeNode param : parameters)
					func.addChild(param);
				
				return func;
			} else if (next != null && next.type == ExpressionTokenType.COLON.type) {
				// RANGE
				
				Token range_end_tok = expectTokenOf(false, ExpressionTokenType.IDENTIFIER.type);
				
				GridVector2 start = CellIdentifiers.parse(operand_tok.token);
				GridVector2 end = CellIdentifiers.parse(range_end_tok.token);
				
				return new ConstantExpressionTreeNode(new Range(start, end));
			} else {
				// CELL
				if (next != null)
					tokenizer.pushBack();
				return new CellExpressionTreeNode(spreadsheet, CellIdentifiers.parse(operand_tok.token));
			}
		}
		else if (operand_tok.type == ExpressionTokenType.OPENING_PARENS.type) {
			// \(EXPRESSION\)
			ExpressionTreeNode expr = readExpression();
			
			expectTokenOf(false, ExpressionTokenType.CLOSING_PARENS.type);
			
			return expr;
		} else
			throw new Error("lmao");
	}
	
	/** Reads a term (an optional expression operator before an operand, optionally followed by a * or / operator and another term. */
	private ExpressionTreeNode readTerm() throws UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException {
		System.out.println("term\t" + tokenizer.peek());
		
		ExpressionTreeNode operand;
		
		// TERM := EXPRESSIONOP? OPERAND (TERMOP TERM)?
		
		// If the first token is an operator, then expect an unary operation
		Token unary_op_tok = expectTokenOf(false);
		if (unary_op_tok.type == ExpressionTokenType.EXPRESSION_OPERATOR.type) {
			operand = readOperand();
			
			// The only unary operator we have is numeric negation. The + operator does nothing.
			if (unary_op_tok.token.equals("-")) {
				ExpressionTreeNode op = BaseLibrary.op_numericNegation();
				op.addChild(operand);
				operand = op;
			}
		} else {
			tokenizer.pushBack();
			operand = readOperand();
		}
		
		Token op_token = tokenizer.nextToken();
		if (op_token == null || op_token.type != ExpressionTokenType.TERM_OPERATOR.type) {
			if (op_token != null)
				tokenizer.pushBack();
			return operand;
		}
		
		ExpressionTreeNode secondOperand = readTerm();
		
		ExpressionTreeNode operation;
		if (op_token.token.equals("*"))
			operation = BaseLibrary.op_product();
		else if (op_token.token.equals("/"))
			operation = BaseLibrary.op_division();
		else
			throw new Error("Unexpected operator due to a program error.");
		
		operation.addChild(operand);
		operation.addChild(secondOperand);
		
		return operation;
	}
}
