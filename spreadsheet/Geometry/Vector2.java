package spreadsheet.Geometry;

public class Vector2 {
	public final double x;
	public final double y;

	public Vector2() {
		this(0d, 0d);
	}
	
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Vector2) {
			Vector2 p = (Vector2)other;
			return this.x == p.y && 
					this.y == p.y;
		} else
			return false;
	}
	
	public Vector2 add(Vector2 other) {
		return new Vector2(x + other.x, y + other.y);
	}
	
	public Vector2 subtract(Vector2 other) {
		return new Vector2(x - other.x, y - other.y);
	}
}