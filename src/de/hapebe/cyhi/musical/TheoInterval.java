package de.hapebe.cyhi.musical;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.hapebe.cyhi.logical.LessonTask;

/**
 * encapsulates a specific interval (i.e. with defined base note and partner note)
 * @author hapebe
 */
public class TheoInterval implements LessonTask {

	TheoNote baseNote;
	TheoNote partnerNote;
	IntervalType type;
	/**
	 * true is upwards,
	 * false is downwards
	 */
	boolean direction;
	
	/**
	 * this constructor is used when re-creating TheoIntervals from JSON data
	 */
	public TheoInterval() {
		
	}
	
	public TheoInterval(TheoNote baseNote, IntervalType type) {
		this(baseNote, type, true);
	}

	/**
	 * @param baseNote main note
	 * @param type type of the interval
	 * @param direction direction: true = upwards, false = downwards
	 */
	public TheoInterval(TheoNote baseNote, IntervalType type, boolean direction) {
		super();
		this.baseNote = baseNote;
		this.type = type;
		this.direction = direction;
		
		init();
	}
	
	private void init() {
		int halfToneDistance = type.getHalftoneDistance();
		if (!direction) halfToneDistance *= -1;
		
		this.partnerNote = new TheoNote(getBaseNote().getMIDINote() + halfToneDistance);
	}

	
	
	public TheoNote getBaseNote() {
		return baseNote;
	}

	public TheoNote getPartnerNote() {
		return partnerNote;
	}

	public IntervalType getType() {
		return type;
	}

	public boolean getDirection() {
		return direction;
	}
	
	@Override
	public List<TheoNote> getNotes() {
		List<TheoNote> retval = new ArrayList<TheoNote>();
		
		retval.add(getBaseNote());
		retval.add(getPartnerNote());
		
		return retval;
	}

	public String getCode() {
		return getType().getCode();
	}
	
	public String getName() {
		return getBaseNote().getCode() + (getDirection()?" + ":" - ") + getType().getName();
	}
	
	public String toString() {
		return getBaseNote().getCode() + (getDirection()?" + ":" - ") + getType().getName();
	}

	@Override
	public String toShortCode() {
		return getClass().getSimpleName() + ":" + getBaseNote().getMIDINote() + ":" + getType().getCode() + ":" + (getDirection()?"up":"down"); 
	}

	@Override
	public void fromShortCode(String code) {
		StringTokenizer st = new StringTokenizer(code, ":");
		
		if (st.countTokens() != 4) {
			throw new IllegalArgumentException("Not a valid TheoInterval short code: " + code);
		}
		
		// 1st token: class name
		String clazz = st.nextToken();
		if (!clazz.equals(getClass().getSimpleName())) {
			throw new IllegalArgumentException("Not a TheoInterval short code: " + code);
		}
		
		// 2nd token: base note (MIDI note number)
		String baseNoteString = st.nextToken();
		try {
			int midiNote = Integer.parseInt(baseNoteString);
			baseNote = new TheoNote(midiNote);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("TheoInterval short code: MIDI note error - " + baseNoteString);
		}
		
		// 3rd token: interval type
		String intervalType = st.nextToken();
		type = IntervalType.ForCode(intervalType);
		if (type == null) {
			throw new IllegalArgumentException("TheoInterval short code: IntervalType error - " + intervalType);
		}

		// 4th token: direction
		String directionString = st.nextToken();
		direction = true; // up - the default case
		if (directionString.equalsIgnoreCase("down")) direction = false;
		
		init();
	}
}
