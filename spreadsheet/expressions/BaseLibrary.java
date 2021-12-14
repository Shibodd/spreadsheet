package spreadsheet.expressions;


import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.FirstChildAccumulatorExpressionTreeNode;
import expressions.FunctionExpressionTreeNode;

public class BaseLibrary {
	public static ExpressionTreeNode sum() {
		return new FirstChildAccumulatorExpressionTreeNode((acc, x) -> acc + x);
	}
	public static ExpressionTreeNode subtraction() {
		return new FirstChildAccumulatorExpressionTreeNode((acc, x) -> acc - x);
	}
	public static ExpressionTreeNode product() {
		return new FirstChildAccumulatorExpressionTreeNode((acc, x) -> acc * x);
	}
	public static ExpressionTreeNode division() {
		return new FirstChildAccumulatorExpressionTreeNode((acc, x) -> acc / x);
	}
	
	public static FunctionExpressionTreeNode logBase() {
		return new FunctionExpressionTreeNode(2, parameters -> {
			double num = parameters[0];
			double base = parameters[1];
			
			return Math.log(num) / Math.log(base);
		});
	}
	public static FunctionExpressionTreeNode log() {
		return new FunctionExpressionTreeNode(1, parameters -> Math.log(parameters[0]));
	}
	public static FunctionExpressionTreeNode log10() {
		return new FunctionExpressionTreeNode(1, parameters -> Math.log10(parameters[0]));
	}
	public static ConstantExpressionTreeNode constant(double value) {
		return new ConstantExpressionTreeNode(value);
	}
	
}

