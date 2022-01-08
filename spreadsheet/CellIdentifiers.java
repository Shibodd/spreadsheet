package spreadsheet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import spreadsheet.Geometry.GridVector2;


/** Collection of static methods for working with string cell identifiers.*/
public class CellIdentifiers {
	static final int COL_BASE = 'Z' - 'A' + 1;
	static final String MAX_COL = colToString(Integer.MAX_VALUE - 1);
	static final int MAX_COL_LEN = MAX_COL.length();
	
	static final Pattern POINT_RE = Pattern.compile("([A-Z]+)([0-9]+)");
	
	/** Parses the position of a cell from a string. 
	 * @throws IllegalArgumentException When the string is not in a valid format, or the column part is too large.
	 */
	public static GridVector2 parse(String str) throws IllegalArgumentException {
		Matcher matcher = POINT_RE.matcher(str);
		
		if (!matcher.matches())
			throw new IllegalArgumentException("The Cell identifier is in the wrong format.");
		
		return new GridVector2(
				Integer.parseInt(matcher.group(2)), 
				parseCol(matcher.group(1))
		);
	}
	
	/** Parses the column of a cell from colStr. */
	public static int parseCol(String colStr) throws IllegalArgumentException {
		if (colStr.length() > MAX_COL_LEN || (colStr.length() == MAX_COL_LEN && colStr.compareTo(MAX_COL) > 0))
			throw new IllegalArgumentException("The column identifier is too large.");
		
		int col = 0;
		int len = colStr.length();
		int val = 1;

		for (int i = 0; i < len; ++i) {
			col += (colStr.charAt(len - i - 1) - 'A' + 1) * val;
			val *= COL_BASE;
		}
		
		return col - 1;
	}
	
	
	/** Converts the column of a cell to its string representation. */
	public static String colToString(int col) throws IllegalArgumentException {
		if (col < 0)
			throw new IllegalArgumentException("Argument col must be greater or equal to zero.");
		
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
	
	/** Converts the position of a cell to a string. */
	public static String toString(GridVector2 pt) {
		return colToString(pt.column) + pt.row;
	}
}
