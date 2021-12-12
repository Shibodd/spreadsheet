package spreadsheet.expressions;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import expressions.FunctionExpressionTreeNode;

public class FunctionDefinition {
	final public String name;
	final public Class<?>[] parameterClasses;
	final public Supplier<FunctionExpressionTreeNode> supplier;
	
	public FunctionDefinition(String name, Class<?>[] parameterClasses, Supplier<FunctionExpressionTreeNode> supplier) {
		this.name = name;
		this.parameterClasses = parameterClasses;
		this.supplier = supplier;
	}
	
	public boolean matchesDefinition(String name, Class<?>[] parameterClasses) {
		return this.name.equals(name) && Arrays.equals(this.parameterClasses, parameterClasses);
	}
	
	public static String format(String name, Class<?>[] parameterClasses) {
		StringBuilder builder = new StringBuilder();
		builder.append(name + "(");
		
		builder.append(
				Arrays.stream(parameterClasses)
					.map(x -> x.getName())
					.collect(Collectors.joining(", ")));
		
		builder.append(")");
		
		return builder.toString();
	}
}
