package spreadsheet.expressions;

import spreadsheet.Geometry.Point;
import java.util.ArrayList;
import java.util.function.Function;

import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.FunctionExpressionTreeNode;
import expressions.InvalidExpressionTreeException;
import spreadsheet.Cell;
import spreadsheet.CellIdentifiers;
import spreadsheet.Range;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.TokenizeException;
import tokenizer.Tokenizer;


/** Provides functionality for compiling expressions to Expression Trees. */
public class ExpressionCompiler {	
	public final Tokenizer tokenizer;
	public final FunctionLibrary library;
	final Function<Point, Cell> cellProvider;
	
	public ExpressionCompiler(Function<Point, Cell> cellProvider) {
		this.tokenizer = new Tokenizer(ExpressionTokenType.toLanguage());
		this.library = new FunctionLibrary();
		this.cellProvider = cellProvider;
	}
	
	
	/** Compiles the expression into an expression tree and returns the root. 
	 * @throws UnknownFunctionException 
	 * @throws InvalidExpressionTreeException 
	 * @throws UnexpectedTokenException */
	public ExpressionTreeNode compile(String expression) throws TokenizeException, UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException{
		tokenizer.reset(expression);
		
		ExpressionTreeNode compiled = readExpression();
		if (!tokenizer.exhausted())
			throw new UnexpectedTokenException("");
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
	private ExpressionTreeNode readExpression() throws TokenizeException, UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException {
		ExpressionTreeNode fact1 = readFactor();
		
		Token op_token = tokenizer.nextToken();
		if (op_token == null)
			return fact1;
		if (op_token.type != ExpressionTokenType.EXPRESSION_OPERATOR.type) {
			tokenizer.pushBack(op_token);
			return fact1;
		}
		
		ExpressionTreeNode expr = readExpression();
		if (expr == null)
			throw unexpectedEnd();
		
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

	/** Reads an operand (a parenthesized expression, a number constant, a function call, a range, a cell. */
	private ExpressionTreeNode readOperand() throws TokenizeException, UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException {
		Token operand_tok = tokenizer.nextToken();
		if (operand_tok == null)
			return null;
		
		// OPERAND := NUMBER | FUNCTION | RANGE | CELL | \(EXPRESSION\)
		// FUNCTION := IDENTIFIER\(PARAMETERS\)
		// PARAMETERS := |EXPRESSION(,EXPRESSION)*
		// RANGE := IDENTIFIER:IDENTIFIER
		// CELL := IDENTIFIER
		
		if (operand_tok.type == ExpressionTokenType.NUMBER.type)
			// NUMBER
			return BaseLibrary.constant(Double.parseDouble(operand_tok.token));		
		else if (operand_tok.type == ExpressionTokenType.IDENTIFIER.type) {
			Token next = tokenizer.nextToken();

			if (next != null && next.type == ExpressionTokenType.OPENING_PARENS.type) {
				// FUNCTION
				// PARAMETERS
				ArrayList<ExpressionTreeNode> parameters = new ArrayList<ExpressionTreeNode>();
				while (true) {
					ExpressionTreeNode expr = readExpression();
					if (expr == null && tokenizer.exhausted())
						throw unexpectedEnd();
					
					parameters.add(expr);
					
					Token paramTok = tokenizer.nextToken();
					if (paramTok.type == ExpressionTokenType.CLOSING_PARENS.type)
						break;
					if (paramTok.type != ExpressionTokenType.COMMA.type)
						throw unexpectedToken(paramTok, ExpressionTokenType.COMMA.type);
				}
				
				FunctionExpressionTreeNode func = library.makeFunctionInstance(operand_tok.token, parameters.size());
				for (ExpressionTreeNode param : parameters)
					func.addChild(param);
				
				return func;
			} else if (next != null && next.type == ExpressionTokenType.COLON.type) {
				// RANGE
				Token range_end_tok = tokenizer.nextToken();
				if (range_end_tok == null)
					throw unexpectedEnd();
				if (range_end_tok.type == ExpressionTokenType.IDENTIFIER.type) {
					Point start = CellIdentifiers.parse(operand_tok.token);
					Point end = CellIdentifiers.parse(range_end_tok.token);
					
					return BaseLibrary.constant(new Range(start, end));
				} else
					throw unexpectedToken(range_end_tok, ExpressionTokenType.IDENTIFIER.type);
			} else
				// CELL
				return new CellExpressionTreeNode(cellProvider.apply(CellIdentifiers.parse(operand_tok.token)));
		}
		else if (operand_tok.type == ExpressionTokenType.OPENING_PARENS.type) {
			// \(EXPRESSION\)
			
			ExpressionTreeNode expr = readExpression();
			Token tok = tokenizer.nextToken();
			if (tok == null)
				throw unexpectedEnd();
			
			if (tok.type != ExpressionTokenType.CLOSING_PARENS.type)
				throw unexpectedToken(tok, ExpressionTokenType.CLOSING_PARENS.type);
			return expr;
		}
		else {
			tokenizer.pushBack(operand_tok);
			return null;
		}
	}
	
	/** Reads a factor (an optional expression operator before an operand, optionally followed by a * or / operator and another factor. */
	private ExpressionTreeNode readFactor() throws TokenizeException, UnexpectedTokenException, InvalidExpressionTreeException, UnknownFunctionException {
		ExpressionTreeNode operand;
		
		// FACTOR := EXPRESSIONOP? OPERAND (FACTOROP FACTOR)?
		
		// If the first token is an operator, then expect an unary operation
		Token unary_op_tok = tokenizer.nextToken();
		if (unary_op_tok == null)
			return null;
		
		if (unary_op_tok.type == ExpressionTokenType.EXPRESSION_OPERATOR.type) {
			operand = readOperand();
			if (operand == null) // We have an operator but no operand for it
				throw unexpectedEnd();
			
			// The only unary operator we have is numeric negation. The + operator does nothing.
			if (unary_op_tok.token.equals("-")) {
				ExpressionTreeNode op = BaseLibrary.numericNegation();
				op.addChild(operand);
				operand = op;
			}
		} else {
			tokenizer.pushBack(unary_op_tok); 
			
			operand = readOperand();
			if (operand == null)
				return null;
		}
		
		Token op_token = tokenizer.nextToken();
		if (op_token == null)
			return operand;
		if (op_token.type != ExpressionTokenType.FACTOR_OPERATOR.type) {
			tokenizer.pushBack(op_token);
			return operand;
		}
		
		ExpressionTreeNode secondOperand = readFactor();
		if (secondOperand == null)
			throw unexpectedEnd();
		
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

	
	private UnexpectedTokenException unexpectedEnd() {
		return new UnexpectedTokenException("The expression is not complete.");
	}
	
	private UnexpectedTokenException unexpectedToken(Token unexpectedToken, TokenType... expectedTokenTypes) {
		return new UnexpectedTokenException(unexpectedToken, expectedTokenTypes);
	}
}
