package spreadsheet.gui;

import javax.swing.table.AbstractTableModel;

import spreadsheet.CellIdentifiers;
import spreadsheet.ICellValueChangedListener;
import spreadsheet.Spreadsheet;
import spreadsheet.Geometry.GridVector2;


public class SpreadsheetTableModel extends AbstractTableModel implements ICellValueChangedListener {
	final Spreadsheet spreadsheet;
	
	public SpreadsheetTableModel(Spreadsheet spreadsheet) {
		this.spreadsheet = spreadsheet;
		
		spreadsheet.addCellValueChangedListener(this);
	}

	@Override
	public int getRowCount() {
		return spreadsheet.getHeight();
	}

	@Override
	public int getColumnCount() {
		return spreadsheet.getWidth();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return spreadsheet.getValueAt(new GridVector2(rowIndex, columnIndex));
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) { return String.class; }	
	
	@Override
	public String getColumnName(int column) {
		return CellIdentifiers.colToString(column);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		spreadsheet.updateExpression(new GridVector2(rowIndex, columnIndex), aValue.toString());
	}

	@Override
	public void onCellValueChanged(GridVector2 position) { 
		fireTableCellUpdated(position.row, position.column); 
	}
}
