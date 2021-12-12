package spreadsheet;

import expressions.ExpressionTreeNode;
import spreadsheet.Geometry.Rect;

public class ExpressionCell extends Cell {
	public ExpressionCell(Rect rect, String expression) {
		super(rect, expression);
	}

	public ExpressionTreeNode expressionTree;

	@Override
	public void evaluate() {
		/* TODO If the expression has not yet been compiled (or updated, if the expression changed), compile it.
		 * Then, evaluate its result.
		 */
	}
	
	public String getRenderedValue() {
		// TODO: Return the value that should be rendered by the GUI.
		return "TODO";
	}

}
