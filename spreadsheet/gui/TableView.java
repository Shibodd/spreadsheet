package spreadsheet.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class TableView extends JPanel {
	int x;
	int y;
	
	public TableView() {
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.RED);
		g.drawRect(x, y, 50, 50);
	}
}
