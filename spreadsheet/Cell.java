package spreadsheet;

import spreadsheet.Geometry.Rect;

public abstract class Cell implements IDependencyChangedListener {
	Rect rect;
	String expression;

	DependencyGraphNode dependencyGraphNode;
	
	public Cell(Rect rect, String expression) {
		
		this.rect = rect;
		this.expression = expression;
	}
	
	public void updateExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public void onDependencyChanged() {
		evaluate();
	}
	
	public abstract void evaluate();
}