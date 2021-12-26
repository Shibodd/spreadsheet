package spreadsheet;

import spreadsheet.Geometry.*;


public class Main {	
	public static void main(String[] args) throws Exception {
		Spreadsheet spreadsheet = new Spreadsheet();
		
		Cell cell = spreadsheet.getCellAt(new Point(1, 1));
		Cell cell2 = spreadsheet.getCellAt(new Point(0, 0));
		
		spreadsheet.updateExpression(cell, "=-4");
		spreadsheet.updateExpression(cell2, "=-3 * B1");
		spreadsheet.updateExpression(cell, "=-3 * 3");
		
		System.out.println(cell2.getValue());
	}
}