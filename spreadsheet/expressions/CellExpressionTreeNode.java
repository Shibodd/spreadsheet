package spreadsheet.expressions;
import expressions.ExpressionTreeNode;
import expressions.InvalidExpressionTreeException;
import spreadsheet.Cell;

public class CellExpressionTreeNode
extends ExpressionTreeNode {
	Cell cell;

	public CellExpressionTreeNode(Cell cell) {
		super(null);
		this.cell = cell;
	}

	@Override
	public Object evaluate() throws InvalidExpressionTreeException {
		return cell.getValue();
	}
	
	public Cell getCell() {
		return cell;
	}
	
	@Override
	public Class<?> getResultClass() {
		return cell.getValueClass();
	}
}
