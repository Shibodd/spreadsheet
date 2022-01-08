package spreadsheet;

import spreadsheet.Geometry.GridVector2;

public class Range {
	public GridVector2 start;
	public GridVector2 end;
	
	public Range(GridVector2 start, GridVector2 end) {
		this.start = start;
		this.end = end;
	}
}
