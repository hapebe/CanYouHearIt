package de.hapebe.cyhi.musical;

import java.util.ArrayList;
import java.util.List;

import de.hapebe.cyhi.logical.LessonTask;

/**
 * encapsulates a specific chord - i.e. with a defined base note and corresponding partner notes
 * @author hapebe
 */
public class TheoChord implements LessonTask {

	TheoNote baseNote;
	ChordType type;
	List<TheoNote> notes;
	
	public TheoChord(TheoNote baseNote, ChordType type) {
		super();
		this.baseNote = baseNote;
		this.type = type;
		
		this.notes = new ArrayList<TheoNote>();
		for (IntervalType i : type.getHarmonicIntvs()) {
			TheoNote n = new TheoNote(baseNote.getMIDINote() + i.getHalftoneDistance());
			notes.add(n);
		}
	}
	
	public String getCode() {
		return baseNote.getCode() + " " + type.getCode();
	}
	
	public String getName() {
		return baseNote.getCode() + " " + type.getName();
	}
	
	public List<TheoNote> getNotes() {
		return notes;
	}
	
	public ChordType getType() {
		return type;
	}
	
	public TheoNote getBaseNote() {
		return baseNote;
	}
	
	public String toString() {
		List<String> noteCodes = new ArrayList<String>();
		for (TheoNote n : notes) {
			noteCodes.add(n.getCode());
		}
		
		return getCode() + " <" + String.join(", ", noteCodes) + ">";
	}

}
