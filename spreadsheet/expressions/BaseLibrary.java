package spreadsheet.expressions;

import expressions.AccumulatorExpressionTreeNode;
import expressions.BinaryExpressionTreeNode;
import expressions.ConstantExpressionTreeNode;
import expressions.ExpressionTreeNode;
import expressions.FunctionExpressionTreeNode;

public class BaseLibrary {
	public static ExpressionTreeNode sum() {
		return new AccumulatorExpressionTreeNode(double.class, double.class, 0d, (acc, node) -> {
			return (double)acc + (double)node.evaluate();
		});
	}
	public static ExpressionTreeNode subtraction() {
		return new AccumulatorExpressionTreeNode(double.class, double.class, 0d, (acc, node) -> {
			return (double)acc - (double)node.evaluate();
		});
	}
	public static ExpressionTreeNode product() {
		return new AccumulatorExpressionTreeNode(double.class, double.class, 1d, (acc, node) -> {
			return (double)acc * (double)node.evaluate();
		});
	}
	public static ExpressionTreeNode logBase() {
		return new FunctionExpressionTreeNode(double.class, new Class<?>[] { double.class, double.class }, xs -> {
			double num = (double)xs.get(0).evaluate();
			double base = (double)xs.get(1).evaluate();
			
			return Math.log(num) / Math.log(base);
		});
	}
	public static ExpressionTreeNode log() {
		return new FunctionExpressionTreeNode(double.class, new Class<?>[] { double.class }, xs -> {
			double num = (double)xs.get(0).evaluate();
			
			return Math.log(num);
		});
	}
	public static ExpressionTreeNode log10() {
		return new FunctionExpressionTreeNode(double.class, new Class<?>[] { double.class }, xs -> {
			double num = (double)xs.get(0).evaluate();
			
			return Math.log10(num);
		});
	}
	public static ConstantExpressionTreeNode constant(Object value) {
		return new ConstantExpressionTreeNode(value);
	}
	
	public static ExpressionTreeNode division() {
		return new BinaryExpressionTreeNode(double.class, double.class, (a, b) -> {
			return (double)a / (double)b;
		});
	}
}
