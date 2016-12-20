package de.hapebe.cyhi.musical;

public class TheoNote {
	int midiNote;

	public TheoNote(int midiNote) {
		if (midiNote < 0) throw new RuntimeException("Cannot (will not...) construct a note with MIDI number less than zero. (" + midiNote + ")");
		if (midiNote > 127) throw new RuntimeException("Cannot (will not...) construct a note with MIDI number higher than 127. (" + midiNote + ")");
		
		this.midiNote = midiNote;
	}
	
	public String getCode() {
		return NoteType.ByMIDINote(midiNote).getCode();
	}
	
	public String getName() {
		return NoteType.ByMIDINote(midiNote).getName();
	}
	
	public int getMIDINote() {
		return midiNote;
	}
	
	public void octaveUp() {
		midiNote += 12;
		if (midiNote > 127) throw new RuntimeException("Cannot (will not...) build a note with MIDI number higher than 127. (" + midiNote + ")");
	}
	
	public void octaveDown() {
		midiNote -= 12;
		if (midiNote < 0) throw new RuntimeException("Cannot (will not...) build a note with MIDI number less than zero. (" + midiNote + ")");
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TheoNote)) return false;
		TheoNote n = (TheoNote)obj;
		
		return (this.getMIDINote() == n.getMIDINote());
	}
	
	public boolean isNote(NoteType t) {
		return NoteType.ByMIDINote(getMIDINote()).equals(t);
	}

	@Override
	public int hashCode() {
		return this.getMIDINote();
	}

	@Override
	public String toString() {
		return NoteType.ByMIDINote(getMIDINote()).getCode() + " ("+this.getMIDINote()+")";
	}
	
	
}
