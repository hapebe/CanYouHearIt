package de.hapebe.cyhi.ui;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//Property change stuff
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class UserNameDialog extends JDialog {

	private static final long serialVersionUID = -7830467312463988577L;

	private String userName = null;
	// private String pwd = null;

	private JOptionPane optionPane;

	public UserNameDialog(Frame parent) {
		super(parent, true);
		setTitle("Switch User");

		final String msgString1 = "Please enter your username:";
		final String msgString2 = "(or a username of your choice)";
		final JTextField textField = new JTextField(10);
		Object[] array = { msgString1, msgString2, textField };

		final String btnString1 = "Enter";
		final String btnString2 = "Cancel";
		Object[] options = { btnString1, btnString2 };

		optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, null, options,
				options[0]);

		setContentPane(optionPane);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				optionPane.setValue(JOptionPane.CLOSED_OPTION);
			}
		});

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				optionPane.setValue(btnString1);
			}
		});

		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				if (isVisible() && (e.getSource() == optionPane)
						&& (prop.equals(JOptionPane.VALUE_PROPERTY) || prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
					Object value = optionPane.getValue();

					if (value == JOptionPane.UNINITIALIZED_VALUE) {
						// ignore reset
						return;
					}

					// Reset the JOptionPane's value.
					// If you don't do this, then if the user
					// presses the same button next time, no
					// property change event will be fired.
					optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

					if (value.equals(btnString1)) {
						userName = textField.getText().toLowerCase();
						setVisible(false);
						/*
						 * if (userName.equals(magicWord)) { // we're done;
						 * dismiss the dialog setVisible(false); } else { //
						 * text was invalid textField.selectAll(); }
						 */
					} else { // user closed dialog or clicked cancel
						userName = null;
						setVisible(false);
					}
				}
			}
		});
	}

	public String getValidatedText() {
		return userName;
	}

}