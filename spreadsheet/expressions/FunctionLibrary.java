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

/** Represents a collection of function definitions and allows defining functions and producing the corresponding ExpressionTreeNodes. */
public class FunctionLibrary {
	Map<String, List<FunctionDefinition>> functionDefinitions = new HashMap<String, List<FunctionDefinition>>();
	
	/** Registers a function definition in the function library. 
	 *  @param definition The function definition to register.
	 *  @throws AmbiguousFunctionException If the function already exists.
	 */
	public void registerFunction(FunctionDefinition definition) throws AmbiguousFunctionException {
		List<FunctionDefinition> functions = functionDefinitions.computeIfAbsent(definition.name, x -> new ArrayList<FunctionDefinition>());
		
		if (functions.stream().anyMatch(x -> x.matchesDefinition(definition.name, definition.parameterCount)))
			throw new AmbiguousFunctionException(definition);
		
		functions.add(definition);
	}

	
	/** Registers a method in the library, as a function definition with the same name and parameters.
	 * @param method The method to import.
	 * @throws AmbiguousFunctionException If a function with the same name and parameter already exists.
	 */
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
					throw new Error(String.format("Failed to invoke the method %s.", method.getName()), e);
				}
			});
		}));
	}
	
	/** Looks up a matching function definition and produces the corresponding ExpressionTreeNode.
	 * @param name The name of the function.
	 * @param parameterCount The parameter count of the function.
	 * @return The ExpressionTreeNode representing the matching function definition from this library.
	 * @throws UnknownFunctionException If no matching function definition was found.
	 */
	public FunctionExpressionTreeNode makeFunctionInstance(String name, int parameterCount) throws UnknownFunctionException {
		Supplier<UnknownFunctionException> unknownFunctionEx = () -> new UnknownFunctionException(new FunctionDefinition(name, parameterCount, null));

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
