package spreadsheet.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import expressions.FunctionExpressionTreeNode;

public class FunctionLibrary {
	Map<String, List<FunctionDefinition>> functionDefinitions = new HashMap<String, List<FunctionDefinition>>();
	
	public void registerFunction(FunctionDefinition definition) throws AmbiguousFunctionException {
		List<FunctionDefinition> functions = functionDefinitions.computeIfAbsent(definition.name, x -> new ArrayList<FunctionDefinition>());
		
		if (functions.stream().anyMatch(x -> x.matchesDefinition(definition.name, definition.parameterCount)))
			throw new AmbiguousFunctionException("A function with the same definition already exists.");
		
		functions.add(definition);
	}
	
	public FunctionExpressionTreeNode makeFunctionInstance(String name, int parameterCount) throws UnknownFunctionException {
		Supplier<UnknownFunctionException> unknownFunctionEx = () -> new UnknownFunctionException(String.format("Unknown function %s(%d).", name, parameterCount));

		List<FunctionDefinition> functions = functionDefinitions.get(name);
		
		if (functions == null)
			throw new UnknownFunctionException(unknownFunctionEx.get());
		
		return functions.stream()
				.filter(x -> x.matchesDefinition(name, parameterCount))
				.findFirst()
				.orElseThrow(unknownFunctionEx)
				.supplier
				.get();
	}
}
