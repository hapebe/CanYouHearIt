package de.hapebe.cyhi.realtime;

import java.util.PriorityQueue;
import java.util.Queue;

import de.hapebe.cyhi.io.MidiPlayer;

public class TimedMusicPlayer extends Thread {

	MidiPlayer midiPlayer;
	Queue<PlayBackAction> events = new PriorityQueue<PlayBackAction>();
	long startTime = 0;
	
	public TimedMusicPlayer(MidiPlayer midiPlayer) {
		super();
		this.midiPlayer = midiPlayer;
	}

	public void addAction(PlayBackAction action) {
		events.add(action);
	}

	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		
		while (!events.isEmpty()) {
//			System.out.println("Runnning:");
//			for (PlayBackAction a : events) {
//				System.out.println(a.toString());
//			}
			
			long localTime = System.currentTimeMillis() - startTime;
//			System.out.println("local time: " + localTime);
			
			boolean loop = false;
			do {
				loop = false;
				if (events.peek().getTime() <= localTime) {
					loop = true;

					PlayBackAction a = events.poll();
					a.perform(midiPlayer);
				}
				
				if (events.isEmpty()) loop = false;
			} while (loop);
			
			if (events.isEmpty()) break; // exit main loop
			
			long nextTime = events.peek().getTime();
			long sleepTime = nextTime - localTime;
			if (sleepTime < 10) sleepTime = 10;
			
			try {
//				System.out.println("sleeping for " + sleepTime + "ms ...");
				Thread.sleep(sleepTime);
			} catch(InterruptedException ex) {
				
			}
		}
	}
	
	
	
}
