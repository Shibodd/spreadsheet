package spreadsheet;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import spreadsheet.gui.AppStartupDialog;
import spreadsheet.gui.AppStartupDialog.Choice;
import spreadsheet.gui.MainWindow;

public class App {
	MainWindow win;
	Spreadsheet spreadsheet;
	Timer timer;
	
	/** The application entry point. */
	public void run() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		final String AUTOSAVE_FILE_PATH = "spreadsheet_autosave";

		String filePath = null;
		
		File autosaveFile = new File("spreadsheet_autosave");
		if (autosaveFile.exists() && 
				JOptionPane.showConfirmDialog(null, 
					String.format(
							"An autosave (%s) was found. Do you want to load it?", 
							dateFormat.format(new Date(autosaveFile.lastModified()))), 
					"Autosave found.", 
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			
			filePath = AUTOSAVE_FILE_PATH;
		} else {
			AppStartupDialog dlg = new AppStartupDialog();
			dlg.setVisible(true);
			Choice choice = dlg.getActionChoice();

			switch (choice) {
			case None:
				return;
			case CreateNew:
				spreadsheet = new Spreadsheet(dlg.getSpreadsheetSize());
				break;
			case OpenFromFile:
				filePath = dlg.getSpreadsheetFilePath();
				break;
			}
		}
		
		
		if (filePath != null) {
			try {
				spreadsheet = Spreadsheet.openFromFile(filePath);
			} catch (IOException | FileFormatException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Failed to open file.", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}			
		}
		

		win = new MainWindow(spreadsheet);
		win.setVisible(true);
		win.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);

				timer.cancel();
				System.exit(0);
			}
		});
		
		
		final int AUTOSAVE_INTERVAL_MILLI = 30*1000; // 30 seconds
		timer = new Timer("autosave");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					spreadsheet.saveToFile("autosave");
					System.out.println("Autosave performed.");
				} catch (IOException e) {
					timer.cancel();
					
					SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(win, "Autosave stopped because an error occurred while autosaving: " + e.getMessage(), "Autosave failed.", JOptionPane.ERROR_MESSAGE);
					});
				}
			}
		}, AUTOSAVE_INTERVAL_MILLI, AUTOSAVE_INTERVAL_MILLI);
	}
}
