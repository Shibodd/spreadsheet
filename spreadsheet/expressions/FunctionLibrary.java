package spreadsheet.expressions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import expressions.FunctionExpressionTreeNode;
import myUtils.ClassUtils;

public class FunctionLibrary {
	Map<String, List<FunctionDefinition>> functionDefinitions = new HashMap<String, List<FunctionDefinition>>();
	
	public void registerFunction(FunctionDefinition definition) throws AmbiguousFunctionException {
		List<FunctionDefinition> functions = functionDefinitions.computeIfAbsent(definition.name, x -> new ArrayList<FunctionDefinition>());
		
		if (functions.stream().anyMatch(x -> x.matchesDefinition(definition.name, definition.parameterCount)))
			throw new AmbiguousFunctionException("A function with the same definition already exists.");
		
		functions.add(definition);
	}

	public void importMethod(Method method) throws AmbiguousFunctionException {
		if (!method.canAccess(null))
			throw new Error(String.format("The method %s could not be imported as it is not accessible.", method));
		
		Class<?>[] parameters = 
				Arrays.stream(method.getParameters())
				.map(x -> {
					Class<?> type = x.getType();
					
					if (type.equals(double.class))
						type = Double.class;
					
					return type;
				})
				.toArray(Class<?>[]::new);
		
		boolean boxDouble = method.getReturnType().equals(double.class);
		
		registerFunction(new FunctionDefinition(method.getName(), parameters.length, () -> {
			return new FunctionExpressionTreeNode(parameters, params -> {
				try {	
					Object result = method.invoke(null, params);
					if (boxDouble)
						return (Double)result; 
					else
						return result;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new Error(String.format("Failed to invoke the %s method: %s.", method, e.getMessage()));
				}
			});
		}));
	}
	
	public FunctionExpressionTreeNode makeFunctionInstance(String name, int parameterCount) throws UnknownFunctionException {
		Supplier<UnknownFunctionException> unknownFunctionEx = () -> new UnknownFunctionException(String.format("Unknown function %s(%d).", name, parameterCount));

		List<FunctionDefinition> functions = functionDefinitions.get(name);
		
		if (functions == null)
			throw unknownFunctionEx.get();
		
		return functions.stream()
				.filter(x -> x.matchesDefinition(name, parameterCount))
				.findFirst()
				.orElseThrow(unknownFunctionEx)
				.supplier
				.get();
	}
}
