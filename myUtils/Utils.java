package myUtils;

public final class Utils {
	private Utils() {}
	
	public static boolean isParseableAsInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
