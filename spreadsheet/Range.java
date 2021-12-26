package spreadsheet;

import spreadsheet.Geometry.Point;

public class Range {
	public Point start;
	public Point end;
	
	public Range(Point start, Point end) {
		this.start = start;
		this.end = end;
	}
}
