package spreadsheet.gui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import spreadsheet.Spreadsheet;
import spreadsheet.gui.Actions.SaveAction;

/** The menu bar of the application. */
public class AppMenuBar extends JMenuBar {
	public AppMenuBar(JFrame frame, Spreadsheet spreadsheet) {
		super();
	
		JMenu fileMenu = new JMenu("File");
		
		fileMenu.add(new SaveAction(false, spreadsheet, frame));
		fileMenu.add(new SaveAction(true, spreadsheet, frame));
		
		
		add(fileMenu);
	}
}
