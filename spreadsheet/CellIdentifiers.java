package spreadsheet;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import spreadsheet.Geometry.Point;

public class CellIdentifiers {
	static final int COL_BASE = 'Z' - 'A' + 1;
	static final String MAX_COL = colToString(Integer.MAX_VALUE);
	static final int MAX_COL_LEN = MAX_COL.length();
	static final int[] COL_BASE_POWERS = 
			IntStream.iterate(1, x -> x * COL_BASE)
				.limit(MAX_COL_LEN)
				.toArray();
	
	static final Pattern POINT_RE = Pattern.compile("([A-Z]+)([0-9]+)");
	
	public static Point parse(String str) {
		Matcher matcher = POINT_RE.matcher(str);
		
		if (!matcher.matches())
			throw new NumberFormatException("The Cell identifier is in the wrong format.");
		
		return new Point(
				Integer.parseInt(matcher.group(0)), 
				parseCol(matcher.group(1))
		);
	}
	
	public static int parseCol(String colStr) {
		if (colStr.length() > MAX_COL_LEN || (colStr.length() == MAX_COL_LEN && colStr.compareTo(MAX_COL) > 0))
			throw new IllegalArgumentException("The column identifier is too large.");
		
		int col = 0;
		int len = colStr.length();

		for (int i = 0; i < len; ++i)
			col += (colStr.charAt(len - i - 1) - 'A') * COL_BASE_POWERS[i];
		
		return col;
	}
	
	public static String colToString(int col) {
		if (col < 0)
			throw new IllegalArgumentException("Argument col must be greater or equal to zero.");
		if (col == 0)
			return "A";
		
		StringBuilder builder = new StringBuilder();
		for (; col > 0; col /= COL_BASE) {
			char c = (char)('A' + (col % COL_BASE));
			builder.append(c);
		}
		return builder.reverse().toString();
	}
	
	public static String toString(Point pt) {
		return colToString(pt.column) + pt.row;
	}
}
