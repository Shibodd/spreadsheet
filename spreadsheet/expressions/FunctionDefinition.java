package spreadsheet.expressions;

import java.util.function.Supplier;
import expressions.FunctionExpressionTreeNode;

public class FunctionDefinition {
	final public String name;
	final public int parameterCount;
	final public Supplier<FunctionExpressionTreeNode> supplier;
	
	public FunctionDefinition(String name, int parameterCount, Supplier<FunctionExpressionTreeNode> supplier) {
		this.name = name;
		this.parameterCount = parameterCount;
		this.supplier = supplier;
	}
	

	/**
	 * @param name The name to match against.
	 * @param parameterCount The parameter count to match against.
	 * @return Whether this function definition matches the parameters.
	 */
	public boolean matchesDefinition(String name, int parameterCount) {
		return this.name.equals(name) && this.parameterCount == parameterCount;
	}
	
	public String toString() {
		return String.format("%s(%d)", name, parameterCount);
	}
}
