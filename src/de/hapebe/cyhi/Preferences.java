package de.hapebe.cyhi;

public class Preferences {

	private static Preferences instance;
	
	protected final static int DEFAULT_LESSON_LENGTH = 10;
	protected int lessonLength = DEFAULT_LESSON_LENGTH;
	
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
	
	
	

}
