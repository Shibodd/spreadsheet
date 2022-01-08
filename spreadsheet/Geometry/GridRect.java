package spreadsheet.Geometry;

public class GridRect {
	public final GridVector2 position;
	public final GridVector2 size;

	public GridRect(GridVector2 position, GridVector2 size) {
		this.position = position;
		this.size = size;
	}
	
	public GridRect(int row, int column, int rows, int columns) {
		this(new GridVector2(row, column), new GridVector2(rows, columns));
	}
	
	public boolean contains(GridVector2 point) {
		return point.row >= position.row && point.row <= position.row + size.row &&
				point.column >= position.column && point.column <= position.column + size.column;
	}
	
	public GridVector2 getBottomRight() {
		return position.add(size);
	}
}
