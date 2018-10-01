package de.hapebe.cyhi.ui.lesson;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import de.hapebe.cyhi.logical.Lesson;

/**
 * Chooser panel for either interval or chord types - 
 * based on the kind of current lesson...
 * @author hapebe@gmx.de
 */
public class TypePanel extends JPanel {
	private static final long serialVersionUID = 3324394670056256278L;
	
	IntervalTypePanel intervalTypePanel;
	ChordTypePanel chordTypePanel;
	
	public TypePanel() {
		super();
		
		setBorder(BorderFactory.createTitledBorder("type"));
		setLayout(new GridLayout(1, 1));
		
		chordTypePanel = new ChordTypePanel();
		intervalTypePanel = new IntervalTypePanel();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(480, 80);
	}
	
	public void updateFor(Lesson lesson) {
		if (lesson.getType() == Lesson.TYPE_INTERVAL_LESSON) {
			removeAll();
			add(intervalTypePanel);
			validate();
			
			intervalTypePanel.updateFor(lesson);
		} else if (lesson.getType() == Lesson.TYPE_CHORD_LESSON) {
			removeAll();
			add(chordTypePanel);
			validate();
			
			chordTypePanel.updateFor(lesson);
		} else {
			// error
		}
	}
	
	public void clearSelection() {
		intervalTypePanel.setSelected(null, true);
		chordTypePanel.setSelected(null, true);
	}
	
	public void disableControls() {
		intervalTypePanel.disableControls();
		chordTypePanel.disableControls();
	}
	
	public void enableControls(Lesson lesson) {
		intervalTypePanel.enableControls(lesson);
		chordTypePanel.enableControls(lesson);
	}
	
}
