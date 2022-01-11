package spreadsheet.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

public class RowHeaderCellRenderer extends JLabel implements ListCellRenderer<Integer> {
	public RowHeaderCellRenderer(JTable table) {
		super();
		
		JTableHeader header = table.getTableHeader();
		setOpaque(true);
	    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	    setHorizontalAlignment(CENTER);
	    setForeground(header.getForeground());
	    setBackground(header.getBackground());
	    setFont(header.getFont());
	}	
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setText(value.toString());
		return this;
	}
}
