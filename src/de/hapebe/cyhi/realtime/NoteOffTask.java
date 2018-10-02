package de.hapebe.cyhi.realtime;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.sound.midi.MidiChannel;

public class NoteOffTask extends TimerTask {

	List<Integer> notes = new ArrayList<Integer>();
	MidiChannel c;

	public NoteOffTask(MidiChannel c) {
		super();
		this.c = c;
	}

	public boolean addNote(int note) {
		return notes.add(note);
	}

	synchronized public void run() {
		for (int note : notes) {
			c.noteOff(note);
		}
		notes.clear();
	}

}
