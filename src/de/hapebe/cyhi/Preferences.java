package de.hapebe.cyhi;

import javax.swing.UIManager;

public class Preferences {

	private static Preferences instance;
	
	protected final static int DEFAULT_LESSON_LENGTH = 10;
	protected int lessonLength = DEFAULT_LESSON_LENGTH;
	
	protected final static String DEFAULT_LOOK_AND_FEEL = UIManager.getCrossPlatformLookAndFeelClassName();
	protected String lookAndFeel = DEFAULT_LOOK_AND_FEEL;
	
	private Preferences() {
		// TODO Auto-generated constructor stub
	}
	
	public static Preferences getInstance() {
		if (instance == null) instance = new Preferences();
		return instance;
	}

	public int getLessonLength() {
		return lessonLength;
	}

	public void setLessonLength(int lessonLength) {
		this.lessonLength = lessonLength;
	}

	public String getLookAndFeel() {
		return lookAndFeel;
	}

	public void setLookAndFeel(String lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

}
