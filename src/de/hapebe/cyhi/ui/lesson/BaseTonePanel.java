package de.hapebe.cyhi.ui.lesson;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.hapebe.cyhi.Config;
import de.hapebe.cyhi.logical.Lesson;
import de.hapebe.cyhi.musical.NoteType;
import de.hapebe.cyhi.musical.TheoNote;

public class BaseTonePanel extends JPanel {
	private static final long serialVersionUID = -8403982665610930860L;
	
	int gridHeight;
	int gridWidth;
	
	Map<NoteType, JRadioButton> buttons = new HashMap<NoteType, JRadioButton>();
	ButtonGroup buttonGroup = new ButtonGroup();

	public BaseTonePanel() {
		super();
		
		setBorder(BorderFactory.createTitledBorder("base tone"));
		
		gridHeight = 2;
		gridWidth = Math.floorDiv(NoteType.DISTINCT_TYPES.size(), gridHeight);
		setLayout(new GridLayout(gridHeight, gridWidth));

		buttonGroup = new ButtonGroup();
		for (NoteType type : NoteType.DISTINCT_TYPES) {
			TheoNote n = new TheoNote(type.getMidiNote());

			JRadioButton b = new JRadioButton(n.getCode());
			b.setActionCommand("basetone:" + n.getCode());
			b.setEnabled(false);
			b.addActionListener(Config.Listener());
			buttonGroup.add(b);
			buttons.put(type, b);
			
			add(b);
		}
		
	}

	public void clearSelection() {
		setSelected(null, true);
	}
	
	public void setSelected(NoteType t, boolean status) {
		if (t == null) {
			buttonGroup.clearSelection();
		} else {
			JRadioButton btn = buttons.get(t);
			buttonGroup.setSelected(btn.getModel(), status);
		}
	}

	public void updateFor(Lesson lesson) {
		clearSelection();
		disableControls();
		enableControls(lesson);
	}
	
	public void disableControls() {
		for (JRadioButton btn : buttons.values()) btn.setEnabled(false);
	}
	
	public void enableControls(Lesson lesson) {
		for (NoteType t : NoteType.DISTINCT_TYPES) {
			buttons.get(t).setEnabled(true);
		}
	}
	
	
}
