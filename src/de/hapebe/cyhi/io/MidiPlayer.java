package de.hapebe.cyhi.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

import de.hapebe.cyhi.logical.LessonTask;
import de.hapebe.cyhi.musical.TheoNote;
import de.hapebe.cyhi.realtime.NoteOffTask;
import de.hapebe.cyhi.realtime.PlayBackAction;
import de.hapebe.cyhi.realtime.TimedMusicPlayer;

public class MidiPlayer {

	// Sequencer sequencer;
	Synthesizer synthesizer;
	Instrument instruments[];
	MidiChannel midiChannels[];
	MidiChannel cc;

	NoteOffTask noteOffTask;

	public MidiPlayer() {
	}

	public void openMidi() {
		try {
			if (synthesizer == null) {
				MidiDevice.Info[] mdi = MidiSystem.getMidiDeviceInfo();
				for (int i = 0; i < mdi.length; i++) {
					// System.out.println(mdi[i].getName()+",
					// "+mdi[i].getDescription());
					if (mdi[i].getName().equals("Java Sound Synthesizer")) {
						MidiDevice md = MidiSystem.getMidiDevice(mdi[i]);
						synthesizer = (Synthesizer) md;
						// System.out.println("got Java Sound Sythesizer!");
					}
				}
			}

			if (synthesizer == null) {
				synthesizer = MidiSystem.getSynthesizer();
				if (synthesizer == null) {
					throw new RuntimeException("Couldn't find a MIDI synthesizer.\n(MidiSystem.getSynthesizer() failed.)");
				}
			}

			try {
				synthesizer.open();
			} catch (MidiUnavailableException e) {
				throw new RuntimeException("Couldn't open MIDI synthesizer.\n  Probably another programm using synthesizer?\n  might as well be a JAVA problem...\n  ...or a BUG!?!" + e.getMessage());
			}

		} catch (Exception ex) {
			throw new RuntimeException("MIDI not available. " + ex.getMessage());
		}

		Soundbank sb = synthesizer.getDefaultSoundbank();
		if (sb != null) {
			instruments = synthesizer.getDefaultSoundbank().getInstruments();
			synthesizer.loadInstrument(instruments[0]);
		}
		midiChannels = synthesizer.getChannels();
		cc = midiChannels[0];
		cc.programChange(0);
	}

	public void closeMidi() {
		if (synthesizer != null) {
			synthesizer.close();
		}
		synthesizer = null;
		instruments = null;
		midiChannels = null;
	}

	
	public void playArpeggio(LessonTask lt, boolean upward) {
		List<Integer> midiNotes = new ArrayList<Integer>();
		for (TheoNote n : lt.getNotes()) {
			midiNotes.add(n.getMIDINote());
		}
		
		if (!upward) Collections.reverse(midiNotes); // reverse...
		
		TimedMusicPlayer tmp = new TimedMusicPlayer(this);
		
		int i=0;
		// all notes on one after the other:
		for (int midiNote : midiNotes) {
			tmp.addAction(new PlayBackAction(i*250, midiNote, true));
			i++;
		}

		i=0;
		// all notes off at the same time:
		for (int midiNote : midiNotes) {
			tmp.addAction(new PlayBackAction(2000, midiNote, false));
			i++;
		}
		
		// the TimedMusicPlayer is a Thread:
		tmp.start();
	}
	
	public void playMusic(LessonTask lt) {
		if (lt == null) return;
		
//		//for use with samples: 
//		ac [chord[currentChord][0]].play(); 
//		if (chord[currentChord][1]!=-1) ac[chord[currentChord][1]].play(); 
//		if (chord[currentChord][2]!=-1) ac[chord[currentChord][2]].play(); 
//		if (chord[currentChord][3]!=-1) ac[chord[currentChord][3]].play();
		
		// for use with midi:
		List<Integer> midiNotes = new ArrayList<Integer>();
		for (TheoNote n : lt.getNotes()) {
			midiNotes.add(n.getMIDINote());
		}

		stopMusic();
		noteOffTask = new NoteOffTask(cc);

		for (int midiNote : midiNotes) {
			noteOffTask.addNote(midiNote);
			cc.noteOn(midiNote, 127);
		}

		Timer t = new java.util.Timer();
		t.schedule(noteOffTask, 2000);
	}

	public void stopMusic() {
//		//for use with samples 
//		for (int i=0;i<ac.length;i++) { if (ac[i] != null) ac[i].stop(); }

		// for use with midi:
		if (noteOffTask != null) {
			noteOffTask.cancel();
			noteOffTask.run();
			noteOffTask = null;
		}
	}

	public MidiChannel getMidiChannel() {
		return cc;
	}
	
	

}
