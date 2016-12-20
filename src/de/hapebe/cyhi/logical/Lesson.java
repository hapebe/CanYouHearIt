package de.hapebe.cyhi.logical;

import java.util.ArrayList;

import de.hapebe.cyhi.musical.ChordType;
import de.hapebe.cyhi.musical.IntervalType;
import de.hapebe.cyhi.musical.TheoChord;
import de.hapebe.cyhi.musical.TheoInterval;
import de.hapebe.cyhi.musical.TheoNote;

public class Lesson extends ArrayList<LessonTask> {
	
	public final static int TYPE_INTERVAL_LESSON = 1;
	public final static int TYPE_CHORD_LESSON = 2;
	
	/** how many single tasks should this lesson consist of? */
	int length;
	
	/** what kind of lesson is this? */
	int type;
	
	/** pointer to the current (or upcoming, if there is no current one, strictly speaking) task */
	int currentTask;
	
	/** time milliseconds at start */
	long startTime;
	
	/** stats for only this lesson */
	StatsContainer stats;

	public Lesson(int type, int length) {
		super();
		this.type = type;
		this.length = length;
		this.stats = new StatsContainer();
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getLength() {
		return length;
	}

	public int getType() {
		return type;
	}
	
	public StatsContainer stats() {
		return stats;
	}

	public LessonTask getCurrentTask() {
		return this.get(currentTask);
	}
	
	public int getCurrentTaskIndex() {
		return currentTask;
	}
	
	public void setCurrentTask(int currentTask) {
		this.currentTask = currentTask;
	}
	
	public void rewind() {
		currentTask = 0;
	}
	
	public void goToNextTask() {
		currentTask ++;
		if (currentTask >= this.size()) {
			// TODO: Error message?
			currentTask = this.size() - 1;
		}
	}
	
	public boolean isAtEnd() {
		return (currentTask == (this.size() - 1));
	}
	
	public void initNew() {
		// TODO: reset / clean up / summarize anything of the previous state?
		
		this.setCurrentTask(0);
		this.setStartTime(System.currentTimeMillis());
		
		if (this.getType() == TYPE_INTERVAL_LESSON) {
			initNewIntervals();
		} else if (this.getType() == TYPE_CHORD_LESSON) {
			initNewChords();
		} else {
			throw new RuntimeException("Unexpected lesson type: " + this.getClass().getSimpleName());
		}
	}
	
	public void initNewIntervals() {
		this.clear();
		
		for (int i=0 ; i < this.getLength() ; i++) {
			this.add(generateInterval());
		}
		
	}
	
	/**
	 * @param t Interval Type
	 * @return true, if this lesson contains any task of type t
	 */
	public boolean containsType(IntervalType t) {
		for (LessonTask lt : this) {
			if (lt instanceof TheoInterval) {
				TheoInterval i = (TheoInterval)lt;
				if (i.getType().equals(t)) return true;
			}
		}
		return false;
	}
	
	/**
	 * @param t Chord Type
	 * @return true, if this lesson contains any task of type t
	 */
	public boolean containsType(ChordType t) {
		for (LessonTask lt : this) {
			if (lt instanceof TheoChord) {
				TheoChord i = (TheoChord)lt;
				if (i.getType().equals(t)) return true;
			}
		}
		return false;
	}
	

	TheoInterval generateInterval() {
		// for use with midi:
		int base = (int) (Math.random() * 36) + 36;
		IntervalType t = IntervalType.Random();

		return new TheoInterval(new TheoNote(base), t);
	}
	
	
	public void initNewChords() {
		this.clear();
		
		for (int i=0 ; i < this.getLength() ; i++) {
			this.add(generateChord());
		}
	}

	TheoChord generateChord() {
		// for use with samples:
		// int base = (int)(Math.random()*12)+6;

		// for use with midi:
		int base = (int) (Math.random() * 36) + 36;
		ChordType t = ChordType.Random();

		return new TheoChord(new TheoNote(base), t);
	}


	

}
