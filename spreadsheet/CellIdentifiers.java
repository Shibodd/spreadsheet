package spreadsheet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spreadsheet.Geometry.GridVector2;


/** Collection of static methods for working with string cell identifiers.*/
public class CellIdentifiers {
	static final int COL_BASE = 'Z' - 'A' + 1;
	static final String MAX_COL = colToString(Integer.MAX_VALUE - 1);
	static final int MAX_COL_LEN = MAX_COL.length();
	
	static final Pattern POINT_RE = Pattern.compile("([A-Z]+)([0-9]+)");
	
	private CellIdentifiers() {}
	
	/** Parses the position from an identifier.
	 * @param str The string from which to parse the position.
	 * @throws NumberFormatException When the string is not in a valid format, or the column part is too large.
	 */
	public static GridVector2 parse(String str) {
		Matcher matcher = POINT_RE.matcher(str);
		
		if (!matcher.matches())
			throw new NumberFormatException("The Cell identifier is in the wrong format.");
		
		return new GridVector2(
				Integer.parseInt(matcher.group(2)) - 1, 
				parseCol(matcher.group(1))
		);
	}
	
	/** Parses a column identifier from a string. Does not perform any check.
	 * @param colStr The string from which to parse the column.
	 * @return The column.
	 * @throws NumberFormatException If the column identifier represents a column which is too large.
	 * */
	private static int parseCol(String colStr) {
		if (colStr.length() > MAX_COL_LEN || (colStr.length() == MAX_COL_LEN && colStr.compareTo(MAX_COL) > 0))
			throw new NumberFormatException("The column identifier is too large.");
		
		int col = 0;
		int len = colStr.length();
		int val = 1;

		for (int i = 0; i < len; ++i) {
			col += (colStr.charAt(len - i - 1) - 'A' + 1) * val;
			val *= COL_BASE;
		}
		
		return col - 1;
	}
	
	
	/** Converts a column to its string representation. 
	 * @param col The column to convert.
	 * @returns The string representation of the column.
	 * @throws IllegalArgumentException If col is negative.
	 * */
	public static String colToString(int col) throws IllegalArgumentException {
		if (col < 0)
			throw new IllegalArgumentException("col must be non negative.");
		
		StringBuilder builder = new StringBuilder();

		++col;
		
	    while (col > 0)
	    {
	        int modulo = (col - 1) % 26;
	        builder.append((char)('A' + modulo));
	        col = (col - modulo) / 26;
	    } 

		return builder.reverse().toString();
	}
	

	/** Converts a position to its string representation.
	 * @param pt The position to convert.
	 * @return The string representation of the position.
	 * @throws IllegalArgumentException If the column is negative.
	 */
	public static String toString(GridVector2 pt) {
		return colToString(pt.column) + (pt.row + 1);
	}
}
