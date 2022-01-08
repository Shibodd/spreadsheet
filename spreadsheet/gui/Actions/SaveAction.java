package spreadsheet.gui.Actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import spreadsheet.Spreadsheet;

public class SaveAction extends AbstractAction {
	final boolean isSaveAs;
	final Spreadsheet spreadsheet;
	final Component dialogParent;
	
	public SaveAction(boolean isSaveAs, Spreadsheet spreadsheet, Component dialogParent) {
		super(isSaveAs? "Save As" : "Save");
		
		if (!isSaveAs)
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		
		this.spreadsheet = spreadsheet;
		this.dialogParent = dialogParent;
		this.isSaveAs = isSaveAs;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (isSaveAs || spreadsheet.getFilePath() == null) {
				JFileChooser chooser = new JFileChooser() {
					@Override
					public void approveSelection() {
						File f = getSelectedFile();
						
						if (f.exists() && JOptionPane.showConfirmDialog(this, "The file already exists; Do you want to replace it?", "File already exists.", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
							return;
						
						super.approveSelection();
					}
				};
				
				if (chooser.showSaveDialog(dialogParent) == JFileChooser.APPROVE_OPTION)
					spreadsheet.saveToFile(chooser.getSelectedFile().getAbsolutePath());		
			} else
				spreadsheet.saveToFile();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(dialogParent, ex.getMessage(), "Failed to save to file.", JOptionPane.ERROR_MESSAGE);
		}
		
	}
}