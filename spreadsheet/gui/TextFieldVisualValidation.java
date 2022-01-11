package spreadsheet.gui;

import java.util.function.Predicate;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Validates a document with a Predicate, changing the border of a textField if it is not valid. */
public class TextFieldVisualValidation implements DocumentListener {
	final JTextField field;
	boolean wasSuccessful;
	
	final Border good;
	final Border bad;
	final Predicate<JTextField> predicate;
	
	public TextFieldVisualValidation(JTextField field, Border good, Border bad, Predicate<JTextField> predicate) {
		this.field = field;
		this.good = good;
		this.bad = bad;
		this.predicate = predicate;
		this.wasSuccessful = true;
	}
	
	private void validate() {
		if (predicate.test(field)) {
			if (!wasSuccessful) {
				field.setBorder(good);
				wasSuccessful = true;
			}
		} else if (wasSuccessful) {
			field.setBorder(bad);
			wasSuccessful = false;
		}
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) { validate(); }
	@Override
	public void removeUpdate(DocumentEvent e) { validate(); }
	@Override
	public void changedUpdate(DocumentEvent e) {}
}
