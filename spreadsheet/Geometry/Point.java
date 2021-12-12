package spreadsheet.Geometry;

public class Point {
	public int row;
	public int column;
	
	public Point() { row = column = 0; }
	public Point(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public Point add(Span span) {
		return new Point(row + span.rowSpan, column + span.columnSpan);
	}
}
