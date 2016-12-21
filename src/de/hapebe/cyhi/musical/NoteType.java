package de.hapebe.cyhi.musical;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class NoteType implements Comparable<NoteType> {
	
	public final static NoteType C = new NoteType(60, "C", "C");
	public final static NoteType CIS = new NoteType(61, "C#", "C sharp");
	public final static NoteType DES = new NoteType(61, "Db", "D flat");
	public final static NoteType D = new NoteType(62, "D", "D");
	public final static NoteType DIS = new NoteType(63, "D#", "D sharp");
	public final static NoteType ES = new NoteType(63, "Eb", "E flat");
	public final static NoteType E = new NoteType(64, "E", "E");
	public final static NoteType F = new NoteType(65, "F", "F");
	public final static NoteType FIS = new NoteType(66, "F#", "F sharp");
	public final static NoteType GES = new NoteType(66, "Gb", "G flat");
	public final static NoteType G = new NoteType(67, "G", "G");
	public final static NoteType GIS = new NoteType(68, "G#", "G sharp");
	public final static NoteType AS = new NoteType(68, "Ab", "A flat");
	public final static NoteType A = new NoteType(69, "A", "A");
	public final static NoteType AIS = new NoteType(70, "A#", "A sharp");
	public final static NoteType BES = new NoteType(70, "Bb", "B flat");
	public final static NoteType B = new NoteType(71, "B", "B");

	public final static SortedSet<NoteType> TYPES = new TreeSet<NoteType>();
	
	static {
		TYPES.add(C);
		TYPES.add(CIS);
		TYPES.add(DES);
		TYPES.add(D);
		TYPES.add(DIS);
		TYPES.add(ES);
		TYPES.add(E);
		TYPES.add(F);
		TYPES.add(FIS);
		TYPES.add(GES);
		TYPES.add(G);
		TYPES.add(GIS);
		TYPES.add(AS);
		TYPES.add(A);
		TYPES.add(AIS);
		TYPES.add(BES);
		TYPES.add(B);
	}
	
	public final static List<NoteType> DISTINCT_TYPES = new ArrayList<NoteType>();
	
	static {
		DISTINCT_TYPES.add(C);
		DISTINCT_TYPES.add(CIS);
		DISTINCT_TYPES.add(D);
		DISTINCT_TYPES.add(ES);
		DISTINCT_TYPES.add(E);
		DISTINCT_TYPES.add(F);
		DISTINCT_TYPES.add(FIS);
		DISTINCT_TYPES.add(G);
		DISTINCT_TYPES.add(AS);
		DISTINCT_TYPES.add(A);
		DISTINCT_TYPES.add(BES);
		DISTINCT_TYPES.add(B);
	}
	
	public int getMidiNote() {
		return midiNote;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static NoteType ByMIDINote(int midiNote) {
		if (midiNote < 0) throw new RuntimeException("Cannot (will not...) compute for negative MIDI note values.");
		
		int t = midiNote % 12;
		switch (t) {
			case 0: return C;
			case 1: return CIS;
			case 2: return D;
			case 3: return ES;
			case 4: return E;
			case 5: return F;
			case 6: return FIS;
			case 7: return G;
			case 8: return AS;
			case 9: return A;
			case 10: return BES;
			case 11: return B;
		}
		return null; // how could that happen?
	}
	
	public static NoteType ForCode(String c) {
		if ("C".equals(c)) return C;
		if ("C#".equals(c)) return CIS;
		if ("Db".equals(c)) return DES;
		if ("D".equals(c)) return D;
		if ("D#".equals(c)) return DIS;
		if ("Eb".equals(c)) return ES;
		if ("E".equals(c)) return E;
		if ("F".equals(c)) return F;
		if ("F#".equals(c)) return FIS;
		if ("Gb".equals(c)) return GES;
		if ("G".equals(c)) return G;
		if ("G#".equals(c)) return GIS;
		if ("Ab".equals(c)) return AS;
		if ("A".equals(c)) return A;
		if ("A#".equals(c)) return AIS;
		if ("Bb".equals(c)) return BES;
		if ("B".equals(c)) return B;
		return null;
	}
	
	
	int midiNote;
	String code;
	String name;
	
	private NoteType(int midiNote, String code, String name) {
		super();
		this.midiNote = midiNote;
		this.code = code;
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NoteType)) return false;
		NoteType t = (NoteType)obj;

		// octave does not matter:
		if ((this.getMidiNote() % 12) == (t.getMidiNote() % 12)) return true;
		return false;
	}

	@Override
	public int hashCode() {
		return this.getMidiNote() % 12;
	}

	@Override
	public String toString() {
		return this.getCode();
	}

	@Override
	public int compareTo(NoteType other) {
		return this.hashCode() - other.hashCode();
	}

}
