package de.hapebe.cyhi.realtime;

import de.hapebe.cyhi.io.MidiPlayer;

public class PlayBackAction implements Comparable<PlayBackAction> {
	long time;
	int midiNote;
	int velocity;
	boolean on;
	
	public PlayBackAction(long time, int midiNote, boolean on) {
		super();
		this.time = time;
		this.midiNote = midiNote;
		this.on = on;
		
		velocity = 100;
	}
	
	public void perform(MidiPlayer midiPlayer) {
		if (this.on) {
			// note on:
			midiPlayer.getMidiChannel().noteOn(midiNote, velocity);
		} else {
			// note off:
			midiPlayer.getMidiChannel().noteOff(midiNote);
		}
	}

	public long getTime() {
		return time;
	}

	public boolean isOn() {
		return on;
	}

	@Override
	public int compareTo(PlayBackAction other) {
		if (this.time < other.time) return -1;
		if (this.time > other.time) return +1;
		
		if (this.on && (!other.on)) return -1;
		if ((!this.on) && other.on) return +1;
		
		if (this.midiNote < other.midiNote) return -1;
		if (this.midiNote > other.midiNote) return +1;
		
		return 0;
	}

	@Override
	public String toString() {
		return "PlayBackAction @" + time + ": " + (on?"noteOn":"noteOff") + "#" + midiNote;
	}
	
	
	
	
}
