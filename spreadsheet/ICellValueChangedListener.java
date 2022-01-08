package spreadsheet;

import spreadsheet.Geometry.GridVector2;

public interface ICellValueChangedListener {
	public void onCellValueChanged(GridVector2 position);
}
