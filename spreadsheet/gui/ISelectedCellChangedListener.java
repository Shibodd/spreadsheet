package spreadsheet.gui;

import spreadsheet.Geometry.GridVector2;

public interface ISelectedCellChangedListener {
	void onSelectedCellChanged(GridVector2 position);
}
