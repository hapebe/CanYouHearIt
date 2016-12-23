package de.hapebe.cyhi.logical;

import java.util.List;

import de.hapebe.cyhi.musical.TheoNote;

public interface LessonTask {

	public TheoNote getBaseNote();
	public String getName();
	public String getCode();
	
	public List<TheoNote> getNotes();
	
	/**
	 * for storage / serialization
	 * @return
	 */
	public String toShortCode();
	
	/**
	 * for storage / serialization
	 */
	public void fromShortCode(String code);

}
