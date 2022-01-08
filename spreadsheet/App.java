package spreadsheet;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import spreadsheet.expressions.AmbiguousFunctionException;
import spreadsheet.gui.AppStartupDialog;
import spreadsheet.gui.AppStartupDialog.Choice;
import spreadsheet.gui.MainWindow;

public class App {
	Spreadsheet spreadsheet;
	
	public App() {
		
	}
	
	
	public void run() throws AmbiguousFunctionException {
		AppStartupDialog dlg = new AppStartupDialog();
		dlg.setVisible(true);
		Choice choice = dlg.getActionChoice();
		
		Spreadsheet ss = null;		
		
		switch (choice) {
		case None:
			return;
		case CreateNew:
			ss = new Spreadsheet(dlg.getSpreadsheetWidth(), dlg.getSpreadsheetHeight());
			break;
		case OpenFromFile:
			try {
				ss = Spreadsheet.openFromFile(dlg.getSpreadsheetFilePath());
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Failed to open file.", JOptionPane.ERROR_MESSAGE);
			}
			break;
		}
		
		MainWindow win = new MainWindow(ss);
		win.setVisible(true);
	}
	
}
