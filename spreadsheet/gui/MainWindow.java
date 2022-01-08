package spreadsheet.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.table.TableRowSorter;

import spreadsheet.CellEvaluationError;
import spreadsheet.Spreadsheet;
import spreadsheet.Geometry.GridVector2;

public class MainWindow extends JFrame {
	final Spreadsheet spreadsheet;
	final SpreadsheetTable table;
	final JTextField expressionTextField;
	final JLabel errorLabel;
	
	GridVector2 selectedCell;
	
	public MainWindow(Spreadsheet spreadsheet) {
		super("Spreadsheet");

		this.spreadsheet = spreadsheet;
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(new AppMenuBar(this, spreadsheet));

		JPanel statusBar = new JPanel();
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		
		errorLabel = new JLabel();
		errorLabel.setPreferredSize(new Dimension(0, 20));
		
		statusBar.add(errorLabel);

		selectedCell = new GridVector2();
		
		table = new SpreadsheetTable(spreadsheet);
		RowSorter sorter = table.getRowSorter();
		
		
		expressionTextField = new JTextField();
		
		table.registerKeyboardAction((e) -> expressionTextField.requestFocus(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
		table.addSelectedCellChangedListener(new ISelectedCellChangedListener() {
			@Override
			public void onSelectedCellChanged(GridVector2 position) {
				expressionTextField.setEnabled(position != null);
				if (position == null) {
					selectedCell = null;
					expressionTextField.setText("");
					return;
				}
				
				GridVector2 actualPos = new GridVector2(sorter.convertRowIndexToModel(position.row), position.column);
				expressionTextField.setText(spreadsheet.getExpressionAt(actualPos));
				selectedCell = actualPos;
				updateError();
			}
		});
		
		expressionTextField.registerKeyboardAction((e) -> table.grabFocus(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
		expressionTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (selectedCell != null) {
					spreadsheet.updateExpression(selectedCell, expressionTextField.getText());
					updateError();
				}
			}
		});

		add(new JScrollPane(table), BorderLayout.CENTER);
		add(expressionTextField, BorderLayout.NORTH);
		add(statusBar, BorderLayout.SOUTH);
		
		
		pack();
		table.requestFocusInWindow();
	}
	
	void updateError() {
		Object value = spreadsheet.getValueAt(selectedCell);
		if (value instanceof CellEvaluationError) {
			CellEvaluationError err = (CellEvaluationError)value;
			errorLabel.setText(err.exception.getMessage());
		} else {
			errorLabel.setText("");
		}
	}
}
