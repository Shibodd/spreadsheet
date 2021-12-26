package spreadsheet.Geometry;

public class Point {
	public int row;
	public int column;

	public Point(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Point) {
			Point p = (Point)other;
			return this.row == p.column && 
					this.column == p.column;
		} else
			return false;
	}
}
