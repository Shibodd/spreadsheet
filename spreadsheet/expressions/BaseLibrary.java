package spreadsheet.expressions;

import expressions.ExpressionTreeNode;
import expressions.FirstChildAccumulatorExpressionTreeNode;
import expressions.FunctionExpressionTreeNode;

/** Contains methods that are used to produce expression tree nodes or function definitions. */
public class BaseLibrary {
	private BaseLibrary() {}
	
	public static ExpressionTreeNode op_sum() {
		return new FirstChildAccumulatorExpressionTreeNode(Double.class, (acc, x) -> (double)acc + (double)x);
	}
	public static ExpressionTreeNode op_subtraction() {
		return new FirstChildAccumulatorExpressionTreeNode(Double.class, (acc, x) -> (double)acc - (double)x);
	}
	public static ExpressionTreeNode op_product() {
		return new FirstChildAccumulatorExpressionTreeNode(Double.class, (acc, x) -> (double)acc * (double)x);
	}
	public static ExpressionTreeNode op_division() {
		return new FirstChildAccumulatorExpressionTreeNode(Double.class, (acc, x) -> (double)acc / (double)x);
	}
	public static ExpressionTreeNode op_numericNegation() {
		return new FunctionExpressionTreeNode(new Class<?>[] { Double.class }, 
				parameters -> -(double)parameters[0]);
	}
	
	public static double log(double x, double base) { return Math.log(x) / Math.log(base); }
	public static String concat(String a, String b) { return a + b; }
	
	private static void importBase(FunctionLibrary library) throws AmbiguousFunctionException, NoSuchMethodException, SecurityException {
		Class<?> baseClass = BaseLibrary.class;
		
		library.importMethod(baseClass.getMethod("log", double.class, double.class));
		library.importMethod(baseClass.getMethod("concat", String.class, String.class));
	}
	
	private static void importMath(FunctionLibrary library) throws AmbiguousFunctionException, NoSuchMethodException, SecurityException {
		Class<?> mathClass = Math.class;
		
		String[] methods = new String[] { "abs", "log", "sin", "cos", "acos", "asin", "atan", "sqrt" };
		for (String name : methods)
			library.importMethod(mathClass.getMethod(name, double.class));
		
		library.importMethod(mathClass.getMethod("random"));
	}
	
	/** @return A function library containing the base functions for the application. */
	public static FunctionLibrary makeBaseLibrary() {
		FunctionLibrary library = new FunctionLibrary();
		
		try {
			importMath(library);
			importBase(library);
		} catch (NoSuchMethodException | SecurityException | AmbiguousFunctionException e) {
			throw new Error("Failed to create the base library: " + e.getMessage());
		}
		
		return library;
	}
}