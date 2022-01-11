package spreadsheet;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class Main {	
	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		App app = new App();
		
		SwingUtilities.invokeLater(() -> app.run());
	}
}