package de.hapebe.cyhi.musical;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.hapebe.cyhi.logical.LessonTask;

/**
 * encapsulates a specific chord - i.e. with a defined base note and corresponding partner notes
 * @author hapebe
 */
public class TheoChord implements LessonTask {

	TheoNote baseNote;
	ChordType type;
	List<TheoNote> notes;
	
	/**
	 * this constructor is used when re-creating TheoIntervals from JSON data
	 */
	public TheoChord() {
		
	}
	
	public TheoChord(TheoNote baseNote, ChordType type) {
		super();
		this.baseNote = baseNote;
		this.type = type;
		
		init();
	}
	
	private void init() {
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

	@Override
	public String toShortCode() {
		return getClass().getSimpleName() + ":" + getBaseNote().getMIDINote() + ":" + getType().getCode(); 
	}

	@Override
	public void fromShortCode(String code) {
		StringTokenizer st = new StringTokenizer(code, ":");
		
		if (st.countTokens() != 3) {
			throw new IllegalArgumentException("Not a valid TheoChord short code: " + code);
		}
		
		// 1st token: class name
		String clazz = st.nextToken();
		if (!clazz.equals(getClass().getSimpleName())) {
			throw new IllegalArgumentException("Not a TheoChord short code: " + code);
		}
		
		// 2nd token: base note (MIDI note number)
		String baseNoteString = st.nextToken();
		try {
			int midiNote = Integer.parseInt(baseNoteString);
			baseNote = new TheoNote(midiNote);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("TheoChord short code: MIDI note error - " + baseNoteString);
		}
		
		// 3rd token: interval type
		String chordType = st.nextToken();
		type = ChordType.ForCode(chordType);
		if (type == null) {
			throw new IllegalArgumentException("TheoChord short code: ChordType error - " + chordType);
		}
		
		init();
	}
}
