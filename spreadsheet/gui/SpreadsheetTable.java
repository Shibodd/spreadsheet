package spreadsheet.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import spreadsheet.CellEvaluationError;
import spreadsheet.CellIdentifiers;
import spreadsheet.Spreadsheet;
import spreadsheet.Geometry.GridVector2;

public class SpreadsheetTable extends JTable {
	final Spreadsheet spreadsheet;
	final SpreadsheetTableModel model;
	final ListSelectionModel colSM;
	final ListSelectionModel rowSM;
	
	public SpreadsheetTable(Spreadsheet spreadsheet) {
		this.spreadsheet = spreadsheet;
		selectedCellChangedListeners = new ArrayList<ISelectedCellChangedListener>();
		
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
		setModel(this.model = new SpreadsheetTableModel(spreadsheet));
		setCellSelectionEnabled(false);
		
		changeSelection(0, 0, false, false);
		
		
		getTableHeader().setReorderingAllowed(false);
		
		TableRowSorter<SpreadsheetTableModel> sorter = new TableRowSorter<SpreadsheetTableModel>(model) {
			@Override
			public void toggleSortOrder(int column) {
				// Apparently the original method only rotates between ASCENDING and DESCENDING, so it can't rotate to UNSORTED.
				// It also keeps all sort keys - so it's completely out of sync with the JTable.
				// This method rotates between all three states and keeps only the latest sort order.

				SortOrder order = 
						getSortKeys()
							.stream()
							.filter(x -> x.getColumn() == column)
							.map(x -> x.getSortOrder())
							.findFirst()
							.orElseGet(() -> SortOrder.UNSORTED);

				switch (order) {
				case UNSORTED:
					order = SortOrder.ASCENDING;
					break;
				case ASCENDING:
					order = SortOrder.DESCENDING;
					break;
				case DESCENDING:
					order = SortOrder.UNSORTED;
					break;
				}
				
				
				List<SortKey> newSortKeys = new ArrayList<SortKey>();
				newSortKeys.add(new SortKey(column, order));
				setSortKeys(newSortKeys);
			}
			
			@Override
			public Comparator<?> getComparator(int column) {
				int top = getSortKeys().get(0).getSortOrder() == SortOrder.ASCENDING? -1 : 1;

				return new Comparator<Object>() {
					@Override
					public int compare(Object o1, Object o2) {
						Class<?> class1 = o1.getClass();
						Class<?> class2 = o2.getClass();
						
						boolean error1 = class1 == CellEvaluationError.class;
						boolean error2 = class2 == CellEvaluationError.class;
						// If at least one is an error, then the result is (in case of descending order):
						// 0 if both are errors; 1 if o1 is an error and o2 is not; -1 viceversa.
						// In short, errors have maximum value;
						if (error1 || error2)
							return (error1? top : 0) + (error2? -top : 0);
						
						boolean number1 = class1 == Double.class;
						boolean number2 = class2 == Double.class;
						// If both are numbers, then compare them as such;
						if (number1 && number2)
							return ((Double)o1).compareTo((Double)o2);

						// If only one is a number, then the result is (in case of descending order):
						// 1 if o1 is a number, -1 if o2 is a number.
						// Numbers have higher value over any type other than errors.
						if (number1 || number2)
							return (number1? top : 0) + (number2? -top : 0);
						
						String s1 = o1.toString();
						String s2 = o2.toString();
						
						boolean blank1 = s1.isBlank();
						boolean blank2 = s2.isBlank();
						// Blank strings should go to the end of the ordering.
						if (blank1 || blank2)
							return (s1.isBlank()? 0 : -top) + (s2.isBlank()? 0 : -top);;
						
						// In any other case just compare them as strings.
						return s1.compareTo(s2);
					}
				};
				
			};
		};
		
		setRowSorter(sorter);
		
		
		colSM = getColumnModel().getSelectionModel();
		rowSM = getSelectionModel();
		rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// This fires selectedCellChanged two times if both row and column change...
		ListSelectionListener selectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				
				fireSelectedCellChanged(new GridVector2(rowSM.getMinSelectionIndex(), colSM.getMinSelectionIndex()));
			}
		};

		rowSM.addListSelectionListener(selectionListener);
		colSM.addListSelectionListener(selectionListener);
	}
	
	
	
	
	List<ISelectedCellChangedListener> selectedCellChangedListeners;
	
	public GridVector2 getSelectedCell() {
		return new GridVector2(rowSM.getMinSelectionIndex(), colSM.getMinSelectionIndex());
	}
	
	public void addSelectedCellChangedListener(ISelectedCellChangedListener listener) {
		selectedCellChangedListeners.add(listener);
	}
	public void removeSelectedCellChangedListener(ISelectedCellChangedListener listener) {
		selectedCellChangedListeners.remove(listener);
	}
	
	private void fireSelectedCellChanged(GridVector2 position) {
		for (ISelectedCellChangedListener listener : selectedCellChangedListeners)
			listener.onSelectedCellChanged(position);
	}
}
