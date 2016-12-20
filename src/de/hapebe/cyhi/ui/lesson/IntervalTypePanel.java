package de.hapebe.cyhi.ui.lesson;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.hapebe.cyhi.logical.Lesson;
import de.hapebe.cyhi.musical.IntervalType;

public class IntervalTypePanel extends JPanel {
	
	int gridHeight;
	int gridWidth;
	
	Map<IntervalType, JRadioButton> buttons = new HashMap<IntervalType, JRadioButton>();
	ButtonGroup buttonGroup = new ButtonGroup();

	public IntervalTypePanel(ActionListener listener) {
		super();

		gridHeight = 3;
		gridWidth = Math.floorDiv(IntervalType.TYPES.size(), gridHeight);
		
		setLayout(new GridLayout(gridHeight, gridWidth));
		
		for (IntervalType t : IntervalType.TYPES) {
			JRadioButton b = new JRadioButton(t.getName());
			b.setActionCommand("interval:" + t.getCode());
			b.setEnabled(true);
			b.addActionListener(listener);
			
			buttonGroup.add(b);
			buttons.put(t, b);
			
			this.add(b);
		}
		
	}
	
	public void setSelected(IntervalType t, boolean status) {
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
		for (IntervalType t : IntervalType.TYPES) {
			if (lesson.containsType(t))	buttons.get(t).setEnabled(true);
		}
	}
	
	
}
