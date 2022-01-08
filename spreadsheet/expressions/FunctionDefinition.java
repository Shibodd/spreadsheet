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
	
	public boolean matchesDefinition(String name, int parameterCount) {
		return this.name.equals(name) && this.parameterCount == parameterCount;
	}
}
