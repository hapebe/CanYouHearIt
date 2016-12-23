package de.hapebe.cyhi.logical;

import java.util.ArrayList;

import de.hapebe.cyhi.musical.NoteType;

public class TaskResultSeries extends ArrayList<TaskResult> {
	private static final long serialVersionUID = -8050639031148138664L;
	
	int typeSuccesses = -1;
	int typeFailures = -1;
	
	int noteSuccesses = -1;
	int noteFailures = -1;

	public boolean add(TaskResult tr) {
		invalidateStats();
		return super.add(tr);
	}
	
	public void invalidateStats() {
		typeSuccesses = -1;
		typeFailures = -1;
		
		noteSuccesses = -1;
		noteFailures = -1;
	}
	
	
	public int getNTypeAttempts() {
		return getNTypeSuccesses() + getNTypeFailures();
	}
	
	public int getNTypeSuccesses() {
		if (typeSuccesses >= 0) return typeSuccesses;
		
		typeSuccesses = 0;
		for (TaskResult t : this) {
			if (t.isTypeSuccess()) typeSuccesses ++;
		}
		return typeSuccesses;
	}
	
	public int getNTypeFailures() {
		if (typeFailures >= 0) return typeFailures;
		
		typeFailures = 0;
		for (TaskResult t : this) {
			if (t.isTypeFailure()) typeFailures ++;
		}
		return typeFailures;
	}
	
	public double getTypeSuccessRatio() {
		double numerator = getNTypeSuccesses();
		double denominator = getNTypeAttempts();
		return numerator / denominator; 
	}
	

	public int getNNoteAttempts() {
		return getNNoteSuccesses() + getNNoteFailures();
	}
	
	public int getNNoteSuccesses() {
		if (noteSuccesses >= 0) return noteSuccesses;
		
		noteSuccesses = 0;
		for (TaskResult t : this) {
			if (t.isBaseToneSuccess()) noteSuccesses ++;
		}
		return noteSuccesses;
	}
	
	public int getNNoteFailures() {
		if (noteFailures >= 0) return noteFailures;
		
		noteFailures = 0;
		for (TaskResult t : this) {
			if (t.isBaseToneFailure()) noteFailures ++;
		}
		return noteFailures;
	}
	
	public double getNoteSuccessRatio() {
		double numerator = getNNoteSuccesses();
		double denominator = getNNoteAttempts();
		return numerator / denominator; 
	}
	
	public TaskResultSeries getFilteredByBaseNote(NoteType noteType) {
		TaskResultSeries retval = new TaskResultSeries();
		
		for (TaskResult tr : this) {
			if (tr.getLessonTask().getBaseNote().isNote(noteType)) {
				retval.add(tr);
			}
		}
		
		return retval;
	}
	
	
}
