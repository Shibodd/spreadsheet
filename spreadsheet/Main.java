package spreadsheet;

import spreadsheet.gui.MainWindow;

public class Main {	
	public static void main(String[] args) throws Exception {
		MainWindow win = new MainWindow();
		win.setSize(500, 500);
		win.setVisible(true);
	}
}