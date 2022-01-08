package spreadsheet.Geometry;

public class GridVector2 {
	public final int row;
	public final int column;

	public GridVector2() {
		this(0, 0);
	}
	
	public GridVector2(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof GridVector2) {
			GridVector2 p = (GridVector2)other;
			return this.row == p.column && 
					this.column == p.column;
		} else
			return false;
	}
	
	public GridVector2 add(GridVector2 other) {
		return new GridVector2(row + other.row, row + other.row);
	}
	
	public GridVector2 subtract(GridVector2 other) {
		return new GridVector2(row - other.row, row - other.row);
	}
	
	public String toString() {
		return String.format("(%d, %d)", row, column);
	}
}