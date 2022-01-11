package spreadsheet.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.function.Predicate;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import myUtils.Utils;
import spreadsheet.Geometry.GridVector2;

/** The dialog which prompts the user to open a file or create a new spreadsheet when starting the application.*/
public class AppStartupDialog extends JDialog {
	private JTextField widthField;
	private JTextField heightField;
	
	private Choice choice = Choice.None;
	private int width;
	private int height;
	private String filePath;
	
	public enum Choice {
		None,
		CreateNew,
		OpenFromFile
	}
	
	/** @return The choice the user made. */
	public Choice getActionChoice() {
		return choice;
	}
	
	/** @return The size of the spreadsheet the user chose.
	 * @throws IllegalStateException if the user's choice is not CreateNew. */
	public GridVector2 getSpreadsheetSize() {
		if (choice != Choice.CreateNew)
			throw new IllegalStateException("The user did not choose to create a new spreadsheet.");
		
		return new GridVector2(width, height);
	}
	
	/** @return The filepath the user chose.
	 * @throws IllegalStateException if the user's choice is not OpenFromFile.
	 */
	public String getSpreadsheetFilePath() {
		if (choice != Choice.OpenFromFile)
			throw new IllegalStateException("The user did not choose to open from file.");
		
		return filePath;
	}

	public AppStartupDialog() {
		super();
		
		setDefaultCloseOperation(AppStartupDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setResizable(false);
		setTitle("Spreadsheet Startup");
		
		JDialog parent = this;
		
		Container contentPane = getContentPane();		
		
		contentPane.setLayout(new GridLayout(3, 1));

		JPanel createPanel = new JPanel();		
		contentPane.add(createPanel);

		JPanel separatorPanel = new JPanel();
		contentPane.add(separatorPanel);
		
		JPanel openPanel = new JPanel();
		contentPane.add(openPanel);
		
		createPanel.add(widthField = new JTextField("20", 5));
		createPanel.add(new JLabel("x"));
		createPanel.add(heightField = new JTextField("30", 5));
		
		
		Predicate<JTextField> validation = x -> Utils.isParseableAsInteger(x.getText());
		
		Border badBorder = BorderFactory.createStrokeBorder(new BasicStroke(2), Color.RED);
		widthField.getDocument().addDocumentListener(new TextFieldVisualValidation(widthField,  widthField.getBorder(), badBorder, validation));
		heightField.getDocument().addDocumentListener(new TextFieldVisualValidation(heightField, heightField.getBorder(), badBorder, validation));
		

		createPanel.add(new JButton(new AbstractAction("Create") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					width = Integer.parseInt(widthField.getText());
					height = Integer.parseInt(heightField.getText());
				} catch (NumberFormatException ex) {
					choice = Choice.None;
					return;
				}
				
				choice = Choice.CreateNew;
				dispose();
			}
		}));
		
		separatorPanel.add(new JLabel("------ or ------"));
		
		openPanel.add(new JButton(new AbstractAction("Open from file...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser() {
					public void approveSelection() {
						if (!getSelectedFile().exists()) {
							JOptionPane.showMessageDialog(this, "The file does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						else
							super.approveSelection();
					};
				};

				if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
					choice = Choice.OpenFromFile;
					filePath = fileChooser.getSelectedFile().getAbsolutePath();
				} else {
					choice = Choice.None;
					filePath = null;
				}
				
				dispose();
			}
		}));
		
		pack();
	}
}
