package myUtils;

import java.util.Set;

public final class ClassUtils {
	private ClassUtils() {}
	
	final static Set<Class<?>> PRIMITIVE_NUMERIC_TYPES = Set.of( byte.class, short.class, int.class, long.class, float.class, double.class );
	public static boolean isNumeric(Class<?> type) {
		return PRIMITIVE_NUMERIC_TYPES.contains(type) || Number.class.isAssignableFrom(type);
	}
}
