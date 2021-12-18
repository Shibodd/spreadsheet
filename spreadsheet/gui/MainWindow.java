package spreadsheet.gui;

import java.awt.*;
import java.util.Base64;

import javax.swing.*;

public class MainWindow extends JFrame {

	public MainWindow() {
		super("Spreadsheet");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		initializeComponents();
	}
	
	private void initializeComponents() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		fileMenu.add(new JMenuItem("Save"));
		fileMenu.add(new JMenuItem("Open"));
		fileMenu.add(new JMenuItem("Exit"));
		
		
		
		add(new TableView());
		
		
	}
}
