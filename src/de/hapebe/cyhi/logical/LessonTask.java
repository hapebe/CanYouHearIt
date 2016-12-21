package de.hapebe.cyhi.logical;

import de.hapebe.cyhi.musical.TheoNote;

public interface LessonTask {

	public TheoNote getBaseNote();
	public String getName();
	public String getCode();
	
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
