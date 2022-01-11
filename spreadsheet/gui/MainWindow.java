package spreadsheet.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

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
		
		setJMenuBar(new AppMenuBar(this, spreadsheet));

		JPanel statusBar = new JPanel();
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		
		errorLabel = new JLabel();
		errorLabel.setPreferredSize(new Dimension(0, 20));
		
		statusBar.add(errorLabel);

		selectedCell = new GridVector2();
		
		table = new SpreadsheetTable(spreadsheet);
		expressionTextField = new JTextField();

		table.registerKeyboardAction((e) -> expressionTextField.requestFocus(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
		
		table.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				char c = e.getKeyChar();
				boolean backspace = e.getKeyCode() == KeyEvent.VK_BACK_SPACE;
				
				if (backspace || c == '=' || Character.isAlphabetic(c) || Character.isDigit(c)) {
					if (backspace) {
						String expr = expressionTextField.getText();
						
						if (expr.length() > 0)
							expressionTextField.setText(expr.substring(0, expr.length() - 1));	
					} else
						expressionTextField.setText(expressionTextField.getText() + c);
					
					expressionTextField.requestFocus();
				}
			}
			
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
		});
		

		table.addSelectedCellChangedListener(new ISelectedCellChangedListener() {
			@Override
			public void onSelectedCellChanged(GridVector2 position) {
				expressionTextField.setEnabled(position != null);
				if (position == null) {
					selectedCell = null;
					expressionTextField.setText("");
					return;
				}
				
				GridVector2 actualPos = new GridVector2(table.getRowSorter().convertRowIndexToModel(position.row), position.column);
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
		
		JScrollPane tableScroll = new JScrollPane(table);
		SpreadsheetRowHeader tableRowHeader = new SpreadsheetRowHeader(spreadsheet, table);
		tableRowHeader.setFixedCellWidth(20);
		tableRowHeader.setFixedCellHeight(table.getRowHeight());
		tableScroll.setRowHeaderView(tableRowHeader);
		
		table.getRowSorter().addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) { tableRowHeader.repaint(); }
		});
		
		
		
		add(tableScroll, BorderLayout.CENTER);
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
