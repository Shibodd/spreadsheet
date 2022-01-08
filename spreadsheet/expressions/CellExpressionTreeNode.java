package spreadsheet.expressions;
import expressions.ExpressionTreeNode;
import expressions.InvalidExpressionTreeException;
import spreadsheet.Cell;
import spreadsheet.Spreadsheet;
import spreadsheet.Geometry.GridVector2;

public class CellExpressionTreeNode
extends ExpressionTreeNode {
	public final GridVector2 position;
	final Spreadsheet spreadsheet;

	public CellExpressionTreeNode(Spreadsheet spreadsheet, GridVector2 position) {
		super();
		this.position = position;
		this.spreadsheet = spreadsheet;
	}

	@Override
	public Object evaluate() {
		return spreadsheet.getValueAt(position);
	}
}
