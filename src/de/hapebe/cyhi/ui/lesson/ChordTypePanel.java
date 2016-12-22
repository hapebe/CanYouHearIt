package de.hapebe.cyhi.ui.lesson;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.hapebe.cyhi.logical.Lesson;
import de.hapebe.cyhi.musical.ChordType;

public class ChordTypePanel extends JPanel {
	
	int gridHeight;
	int gridWidth;
	
	Map<ChordType, JRadioButton> buttons = new HashMap<ChordType, JRadioButton>();
	ButtonGroup buttonGroup = new ButtonGroup();

	public ChordTypePanel(ActionListener listener) {
		super();

		gridHeight = 4;
		gridWidth = Math.floorDiv(ChordType.TYPES.size(), gridHeight);
		
		setLayout(new GridLayout(gridHeight, gridWidth));
		
		for (ChordType t : ChordType.TYPES) {
			JRadioButton b = new JRadioButton(t.getName());
			b.setActionCommand("chord:" + t.getCode());
			b.setEnabled(true);
			b.addActionListener(listener);
			
			buttonGroup.add(b);
			buttons.put(t, b);
			
			this.add(b);
		}
		
	}
	
	public void setSelected(ChordType t, boolean status) {
		if (t == null) {
			buttonGroup.clearSelection();
		} else {
			JRadioButton btn = buttons.get(t);
			buttonGroup.setSelected(btn.getModel(), status);
		}
	}
	
	public void updateFor(Lesson lesson) {
		setSelected(null, true); // clear selection
		disableControls();
		enableControls(lesson);
	}
	
	public void disableControls() {
		for (JRadioButton btn : buttons.values()) btn.setEnabled(false);
	}
	
	public void enableControls(Lesson lesson) {
		for (ChordType t : ChordType.TYPES) {
			if (lesson.containsType(t))	buttons.get(t).setEnabled(true);
		}
	}
	
	
}
