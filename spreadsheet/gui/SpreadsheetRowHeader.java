package spreadsheet.gui;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JTable;

import spreadsheet.Spreadsheet;

public class SpreadsheetRowHeader extends JList<Integer> {
	final Spreadsheet spreadsheet;
	
	public SpreadsheetRowHeader(Spreadsheet spreadsheet, JTable table) {
		super(new AbstractListModel<Integer>() {
			@Override
			public int getSize() { return spreadsheet.getHeight(); }
			@Override
			public Integer getElementAt(int index) { return table.getRowSorter().convertRowIndexToModel(index) + 1; }
		});
		
		this.spreadsheet = spreadsheet;
		this.setCellRenderer(new RowHeaderCellRenderer(table));
	}
}
