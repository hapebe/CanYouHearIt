package de.hapebe.cyhi.logical;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.hapebe.cyhi.musical.ChordType;
import de.hapebe.cyhi.musical.IntervalType;
import de.hapebe.cyhi.musical.TheoChord;
import de.hapebe.cyhi.musical.TheoInterval;

public class StatsContainer implements Serializable {
	
	private static final long serialVersionUID = 7861255329251280671L;
	
	
	final TaskResultSeries intervalAttempts;
	final TaskResultSeries chordAttempts;
	
	final Map<IntervalType, TaskResultSeries> intervalAttemptsByType;
	final Map<ChordType, TaskResultSeries> chordAttemptsByType;
	
	final TaskResultSeries EMTPY = new TaskResultSeries();
	
	public StatsContainer() {
		super();
		intervalAttempts = new TaskResultSeries();
		chordAttempts = new TaskResultSeries();

		intervalAttemptsByType = new HashMap<IntervalType, TaskResultSeries>();
		chordAttemptsByType = new HashMap<ChordType, TaskResultSeries>();
	}

	public void clear() {
		intervalAttempts.clear();
		chordAttempts.clear();

		intervalAttemptsByType.clear();
		chordAttemptsByType.clear();
	}


	public TaskResultSeries getIntervalAttempts() {
		return intervalAttempts;
	}


	public TaskResultSeries getIntervalAttemptsByType(IntervalType type) {
		TaskResultSeries trs = intervalAttemptsByType.get(type);
		if (trs == null) return EMTPY;
		return trs;
	}

	public TaskResultSeries getChordAttempts() {
		return chordAttempts;
	}

	public TaskResultSeries getChordAttemptsByType(ChordType type) {
		TaskResultSeries trs = chordAttemptsByType.get(type);
		if (trs == null) return EMTPY;
		return trs;
	}


	public void registerIntervalAttempt(TheoInterval i, boolean typeSuccess, boolean baseToneSuccess) {
		byte typeStatus = typeSuccess ? TaskResult.SUCCESS : TaskResult.FAILURE;
		byte baseToneStatus = baseToneSuccess ? TaskResult.SUCCESS : TaskResult.FAILURE;
		
		this.registerAttempt(new TaskResult(i, typeStatus, baseToneStatus));
	}

	public void registerChordAttempt(TheoChord c, boolean typeSuccess, boolean baseToneSuccess) {
		byte typeStatus = typeSuccess ? TaskResult.SUCCESS : TaskResult.FAILURE;
		byte baseToneStatus = baseToneSuccess ? TaskResult.SUCCESS : TaskResult.FAILURE;
		
		this.registerAttempt(new TaskResult(c, typeStatus, baseToneStatus));
	}
	
	private void registerAttempt(TaskResult tr) {
		LessonTask lt = tr.getLessonTask();
		
		if (lt instanceof TheoChord) {
			getChordAttempts().add(tr); // mixed chord attempts
	
			TheoChord c = (TheoChord)lt; 
			ChordType type = c.getType();
			TaskResultSeries attemptsByType = chordAttemptsByType.get(type);
			if (attemptsByType == null) {
				attemptsByType = new TaskResultSeries();
				chordAttemptsByType.put(type, attemptsByType);
			}
			attemptsByType.add(tr); // chord attempts by type
			
		} else if (lt instanceof TheoInterval) {
			getIntervalAttempts().add(tr); // mixed interval attempts
			
			TheoInterval intv = (TheoInterval)tr.getLessonTask();
			IntervalType type = intv.getType();
			TaskResultSeries attemptsByType = intervalAttemptsByType.get(type);
			if (attemptsByType == null) {
				attemptsByType = new TaskResultSeries();
				intervalAttemptsByType.put(type, attemptsByType);
			}
			attemptsByType.add(tr); // interval attempts by type
			
		} else {
			System.err.println("Unexpected type of TaskResult for LessonTask: " + lt);
		}
	}
	
	/**
	 * add the contents of another StatsContainer to this one - e.g. the results of one lesson to the master / global stats.
	 * @param other a different instance of StatsContainer (should only be added once, and possibly cleared afterwards, for data purity's sake)
	 */
	public void add(StatsContainer other) {
		for (TaskResult tr : other.getChordAttempts()) {
			this.registerAttempt(tr);
		}
		for (TaskResult tr : other.getIntervalAttempts()) {
			this.registerAttempt(tr);
		}
	}

	
	public void saveStats(String filename) {
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(new FileOutputStream(filename));
		} catch (IOException e) {
			System.err.println("Couldn't create ObjectOutputStream to " + filename + " .");
		}
		try {
			oos.writeObject(this);
			oos.close();
		} catch (IOException e) {
			System.err.println("Couldn't write stats to " + filename + " .");
		}
	}

	public static StatsContainer load(String filename) {
		StatsContainer retval = null;
		
		ObjectInputStream ois = null;
		
		try {
			ois = new ObjectInputStream(new FileInputStream(filename));
		} catch (IOException e) {
			System.err.println("Couldn't create ObjectInputStream from " + filename + " .");
		}
		if (ois != null) {
			try {
				retval = (StatsContainer) ois.readObject();
				ois.close();
			} catch (Exception e) {
				System.err.println("Couldn't read stats from " + filename + " .");
				System.err.println(e.toString());
			}
		} // endif ois!=null
		
		return retval;
	}
	
}
