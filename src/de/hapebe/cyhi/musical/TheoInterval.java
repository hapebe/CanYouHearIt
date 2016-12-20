package de.hapebe.cyhi.musical;

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

	public String getCode() {
		return getType().getCode();
	}
	
	public String getName() {
		return getBaseNote().getCode() + (getDirection()?" + ":" - ") + getType().getName();
	}
	
	public String toString() {
		return getBaseNote().getCode() + (getDirection()?" + ":" - ") + getType().getName();
	}
}
