package spreadsheet.Geometry;

public class Rect {
	public Point position;
	public Span span;
	
	public Rect() {
		position = new Point();
		span = new Span();
	}
	public Rect(Point position, Span span) {
		this.position = position;
		this.span = span;
	}
	public Rect(int row, int column, int rowSpan, int columnSpan) {
		this.position = new Point(row, column);
		this.span = new Span(rowSpan, columnSpan);
	}
	

	public boolean intersects(Rect other) {
		return !(position.column + span.columnSpan <= other.position.column || 
				other.position.column + other.span.columnSpan <= position.column ||
				position.row + span.rowSpan <= other.position.row ||
				other.position.row + other.span.rowSpan <= position.row);
	}
}
