package spreadsheet;

import spreadsheet.Geometry.Rect;

public abstract class Cell implements IDependencyChangedListener {
	public Rect rect;
	public String expression;
	
	public DependencyGraphNode dependencyGraphNode;
	
	public Cell(Rect rect, String expression) {
		this.rect = rect;
		this.expression = expression;
	}

	@Override
	public void onDependencyChanged() {
		evaluate();
	}
	
	public abstract void evaluate();
}
