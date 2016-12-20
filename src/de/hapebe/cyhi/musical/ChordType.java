package de.hapebe.cyhi.musical;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChordType {

	@SuppressWarnings("serial")
	public final static ChordType MAJOR = new ChordType("maj", "Major", new ArrayList<IntervalType>() {{
		   add(IntervalType.MAJOR_THIRD);
		   add(IntervalType.FIFTH);
		}}
	); 
	@SuppressWarnings("serial")
	public final static ChordType MINOR = new ChordType("min", "Minor", new ArrayList<IntervalType>() {{
		   add(IntervalType.MINOR_THIRD);
		   add(IntervalType.FIFTH);
		}}
	); 
	@SuppressWarnings("serial")
	public final static ChordType POWER = new ChordType("1+5", "Power", new ArrayList<IntervalType>() {{
		   add(IntervalType.FIFTH);
		}}
	); 
	@SuppressWarnings("serial")
	public final static ChordType DIMINISHED = new ChordType("dim", "Diminished", new ArrayList<IntervalType>() {{
		   add(IntervalType.MINOR_THIRD);
		   add(IntervalType.TRITONE);
		}}
	); 
	@SuppressWarnings("serial")
	public final static ChordType AUGMENTED = new ChordType("aug", "Augmented", new ArrayList<IntervalType>() {{
		   add(IntervalType.MAJOR_THIRD);
		   add(IntervalType.MINOR_SIXTH);
		}}
	); 
	@SuppressWarnings("serial")
	public final static ChordType MAJOR_7 = new ChordType("maj7", "Major plus Seventh", new ArrayList<IntervalType>() {{
		   add(IntervalType.MAJOR_THIRD);
		   add(IntervalType.FIFTH);
		   add(IntervalType.MINOR_SEVENTH);
		}}
	); 
	@SuppressWarnings("serial")
	public final static ChordType MINOR_7 = new ChordType("min7", "Minor plus Seventh", new ArrayList<IntervalType>() {{
		   add(IntervalType.MINOR_THIRD);
		   add(IntervalType.FIFTH);
		   add(IntervalType.MINOR_SEVENTH);
		}}
	); 
	@SuppressWarnings("serial")
	public final static ChordType MAJOR_7J = new ChordType("maj7+", "Major plus Major Seventh", new ArrayList<IntervalType>() {{
		   add(IntervalType.MAJOR_THIRD);
		   add(IntervalType.FIFTH);
		   add(IntervalType.MAJOR_SEVENTH);
		}}
	); 
	@SuppressWarnings("serial")
	public final static ChordType MINOR_7J = new ChordType("min7+", "Minor plus Major Seventh", new ArrayList<IntervalType>() {{
		   add(IntervalType.MINOR_THIRD);
		   add(IntervalType.FIFTH);
		   add(IntervalType.MAJOR_SEVENTH);
		}}
	);
	
	public final static List<ChordType> TYPES = new ArrayList<ChordType>();
	static {
		TYPES.add(MAJOR);
		TYPES.add(MINOR);
		TYPES.add(POWER);
		TYPES.add(DIMINISHED);
		TYPES.add(AUGMENTED);
		TYPES.add(MAJOR_7);
		TYPES.add(MINOR_7);
		TYPES.add(MAJOR_7J);
		TYPES.add(MINOR_7J);
	}
	
	public static ChordType ForCode(String c) {
		for (ChordType t : TYPES) {
			if (t.getCode().equals(c)) return t;
		}
		return null;
	}
	
	
	public static ChordType Random() {
		int item = new Random().nextInt(TYPES.size());
		int i = 0;
		for(ChordType t : TYPES) {
		    if (i == item) return t;
		    i++;
		}
		
		throw new RuntimeException("Assertion failed.");
	}
	
	

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChordType)) return false;
		ChordType other = (ChordType)obj;
		
		if (other.getCode().equals(this.getCode())) return true;
		return false;
	}


	private ChordType(String code, String name, List<IntervalType> harmonicIntvs) {
		super();
		
		this.code = code;
		this.name = name;
		
		this.harmonicIntvs = new ArrayList<IntervalType>();
		if (!harmonicIntvs.contains(IntervalType.UNISON)) this.harmonicIntvs.add(IntervalType.UNISON);
		this.harmonicIntvs.addAll(harmonicIntvs);
	}

	
	
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public List<IntervalType> getHarmonicIntvs() {
		return harmonicIntvs;
	}

	String code;
	String name;
	List<IntervalType> harmonicIntvs;

	
}
