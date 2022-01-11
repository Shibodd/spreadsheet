package spreadsheet.expressions;

import expressions.ExpressionTreeNode;
import spreadsheet.Spreadsheet;
import spreadsheet.Geometry.GridVector2;

/** Expression tree node that retrieves the value of a cell from a spreadsheet. */
public class CellExpressionTreeNode
extends ExpressionTreeNode {
	public final GridVector2 position;
	final Spreadsheet spreadsheet;

	
	/**
	 * @param spreadsheet The spreadsheet to query for the value of the cell.
	 * @param position The position of the cell.
	 */
	public CellExpressionTreeNode(Spreadsheet spreadsheet, GridVector2 position) {
		super();
		this.position = position;
		this.spreadsheet = spreadsheet;
	}

	
	/**
	 * @return The value of the cell.
	 */
	@Override
	public Object evaluate() {
		return spreadsheet.getValueAt(position);
	}
}
