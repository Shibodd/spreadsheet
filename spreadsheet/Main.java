package spreadsheet;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import spreadsheet.expressions.AmbiguousFunctionException;
import spreadsheet.gui.MainWindow;


public class Main {	
	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		App app = new App();
		
		SwingUtilities.invokeAndWait(() -> app.run());
	}
}