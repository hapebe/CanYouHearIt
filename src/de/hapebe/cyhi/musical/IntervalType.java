/**
 * 
 */
package de.hapebe.cyhi.musical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author hapebe
 * Models musical background (and human-readable names etc.) for Intervals; i.e. two-note combinations 
 */
public class IntervalType implements Comparable<Object> {

	public final static IntervalType UNISON = new IntervalType(0, "1", "Unison", "Unisons"); 
	public final static IntervalType MINOR_SECOND = new IntervalType(1, "<2", "Minor Second", "Minor Seconds"); 
	public final static IntervalType MAJOR_SECOND = new IntervalType(2, ">2", "Major Second", "Major Seconds"); 
	public final static IntervalType MINOR_THIRD = new IntervalType(3, "<3", "Minor Third", "Minor Thirds"); 
	public final static IntervalType MAJOR_THIRD = new IntervalType(4, ">3", "Major Third", "Major Thirds"); 
	public final static IntervalType FOURTH = new IntervalType(5, "4", "Fourth", "Fourths"); 
	public final static IntervalType TRITONE = new IntervalType(6, "tt", "Tritone", "Tritones"); 
	public final static IntervalType FIFTH = new IntervalType(7, "5", "Fifth", "Fifths"); 
	public final static IntervalType MINOR_SIXTH = new IntervalType(8, "<6", "Minor Sixth", "Minor Sixths"); 
	public final static IntervalType MAJOR_SIXTH = new IntervalType(9, ">6", "Major Sixth", "Major Sixths"); 
	public final static IntervalType MINOR_SEVENTH = new IntervalType(10, "<7", "Minor Seventh", "Minor Sevenths"); 
	public final static IntervalType MAJOR_SEVENTH = new IntervalType(11, ">7", "Major Seventh", "Major Sevenths"); 
	public final static IntervalType OCTAVE = new IntervalType(12, "8", "Octave", "Octaves"); 
	
	public final static List<IntervalType> TYPES = new ArrayList<IntervalType>();
	
	static {
		TYPES.add(UNISON);
		TYPES.add(MINOR_SECOND);
		TYPES.add(MAJOR_SECOND);
		TYPES.add(MINOR_THIRD);
		TYPES.add(MAJOR_THIRD);
		TYPES.add(FOURTH);
		TYPES.add(TRITONE);
		TYPES.add(FIFTH);
		TYPES.add(MINOR_SIXTH);
		TYPES.add(MINOR_SEVENTH);
		TYPES.add(MAJOR_SEVENTH);
		TYPES.add(OCTAVE);
	}
	
	public final static Map<Integer, IntervalType> BY_DISTANCE = new HashMap<Integer, IntervalType>();
	static {
		for (IntervalType i : TYPES) {
			BY_DISTANCE.put(i.getHalftoneDistance(), i);
			BY_DISTANCE.put(-1 * i.getHalftoneDistance(), i);
		}
	}
	
	public static IntervalType ForCode(String c) {
		for (IntervalType t : TYPES) {
			if (t.getCode().equals(c)) return t;
		}
		return null;
	}
	
	public static IntervalType Random() {
		int item = new Random().nextInt(TYPES.size());
		int i = 0;
		for(IntervalType t : TYPES) {
		    if (i == item) return t;
		    i++;
		}
		
		throw new RuntimeException("Assertion failed.");
	}

	
	
	int halftoneDistance; 
	String code;
	String name;
	String namePlural;
	
	
	private IntervalType(int halftoneDistance, String code, String name, String namePlural) {
		super();
		this.halftoneDistance = halftoneDistance;
		this.code = code;
		this.name = name;
		this.namePlural = namePlural;
	}
	
	
	
	public int getHalftoneDistance() {
		return halftoneDistance;
	}


	public String getCode() {
		return code;
	}


	public String getName() {
		return name;
	}


	public String getNamePlural() {
		return namePlural;
	}


	@Override
	public int compareTo(Object arg0) {
		if (!(arg0 instanceof IntervalType)) throw new RuntimeException("Cannot comparte to " + arg0 + ".");
		
		IntervalType other = (IntervalType)arg0;
		
		if (other.getHalftoneDistance() > this.getHalftoneDistance()) return -1;
		if (other.getHalftoneDistance() < this.getHalftoneDistance()) return +1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntervalType)) return false;
		
		IntervalType other = (IntervalType) obj;
		
		if (other.getHalftoneDistance() == this.getHalftoneDistance()) return true;
		return false;
	}
	

}
