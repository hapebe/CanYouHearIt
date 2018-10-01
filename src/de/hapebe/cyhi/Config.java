package de.hapebe.cyhi;

import java.awt.event.ActionListener;

import de.hapebe.cyhi.io.MidiPlayer;

public class Config {

	static MidiPlayer midiPlayer;
	static CanYouHearIt app;
	static ActionListener listener;
	
	public static MidiPlayer MidiPlayer() { return midiPlayer; }
	public static CanYouHearIt App() { return app; }
	public static ActionListener Listener() { return listener; }
	
}
