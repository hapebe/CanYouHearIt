package de.hapebe.cyhi.logical;

import java.util.List;

import de.hapebe.cyhi.musical.TheoNote;

/**
 * a dummy implementation of LessonTask to transport a guess made by the user - more or less, only using getName() / setName()
 */
public class TaskGuess implements LessonTask {

	final String name;
	
	public TaskGuess(String name) {
		this.name = name;
	}

	@Override
	public TheoNote getBaseNote() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getCode() {
		return null;
	}

	@Override
	public List<TheoNote> getNotes() {
		return null;
	}

	@Override
	public String toShortCode() {
		return null;
	}

	@Override
	public void fromShortCode(String code) {
	}

}
