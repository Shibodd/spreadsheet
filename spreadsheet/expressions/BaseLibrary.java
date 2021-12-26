package spreadsheet.expressions;


import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.FirstChildAccumulatorExpressionTreeNode;
import expressions.FunctionExpressionTreeNode;

public class BaseLibrary {
	public static ExpressionTreeNode sum() {
		return new FirstChildAccumulatorExpressionTreeNode(Double.class, Double.class, (acc, x) -> (double)acc + (double)x);
	}
	public static ExpressionTreeNode subtraction() {
		return new FirstChildAccumulatorExpressionTreeNode(Double.class, Double.class, (acc, x) -> (double)acc - (double)x);
	}
	public static ExpressionTreeNode product() {
		return new FirstChildAccumulatorExpressionTreeNode(Double.class, Double.class, (acc, x) -> (double)acc * (double)x);
	}
	public static ExpressionTreeNode division() {
		return new FirstChildAccumulatorExpressionTreeNode(Double.class, Double.class, (acc, x) -> (double)acc / (double)x);
	}
	
	public static ExpressionTreeNode numericNegation() {
		return new FunctionExpressionTreeNode(Double.class, new Class<?>[] { Double.class }, 
				parameters -> -(double)parameters[0]);
	}
	
	public static FunctionExpressionTreeNode logBase() {
		return new FunctionExpressionTreeNode(Double.class, new Class<?>[] { Double.class, Double.class }, parameters -> {
			double num = (double)parameters[0];
			double base = (double)parameters[1];
			
			return Math.log(num) / Math.log(base);
		});
	}
	public static FunctionExpressionTreeNode log() {
		return new FunctionExpressionTreeNode(Double.class, new Class<?>[] { Double.class }, 
				parameters -> Math.log((double)parameters[0]));
	}
	public static FunctionExpressionTreeNode log10() {
		return new FunctionExpressionTreeNode(Double.class, new Class<?>[] { Double.class }, 
				parameters -> Math.log10((double)parameters[0]));
	}
	public static ConstantExpressionTreeNode constant(Object value) {
		return new ConstantExpressionTreeNode(value);
	}
	
}

