package de.hapebe.cyhi.ui.stats;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import de.hapebe.cyhi.CanYouHearIt;
import de.hapebe.cyhi.logical.ChordStats;
import de.hapebe.cyhi.logical.TaskResultSeries;
import de.hapebe.cyhi.musical.ChordType;
import de.hapebe.cyhi.musical.NoteType;

public class ChordStatsPanel extends StatsPanel {
	private static final long serialVersionUID = 4478930706255443794L;
	
	Map<Integer, ChordType> chordLookup = new HashMap<Integer, ChordType>();
	Map<ChordType, Integer> chordReverseLookup = new HashMap<ChordType, Integer>();

	public ChordStatsPanel(CanYouHearIt music) {
		super(music);
		
		// create a rubrication (axis) for Chord Types:
		int i = 0;
		for (ChordType chordType : ChordType.TYPES) {
			chordLookup.put(i, chordType);
			chordReverseLookup.put(chordType, i);
			i++;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int percentage = -1;
		ChordStats stats = music.getStats().getChordStats();
		
		for (ChordType chordType : ChordType.TYPES) {
			int y = chordReverseLookup.get(chordType); // graphical row of this chord type

			TaskResultSeries trs = stats.getAttemptsByType(chordType);
			if (!trs.isEmpty()) {
				percentage = (int)Math.round((double)trs.getTypeSuccessRatio() * 100d);
			
				int ySize = trs.getNTypeAttempts();
				if (ySize < 2) ySize = 2;
				if (ySize > 24)	ySize = 24;
				
				g.setColor(colorForSuccessPercentage(percentage));
				g.fillRect(0, (y * 25) + 24 - ySize, 39, ySize);
			}

			// single chord/note stats
			for (int x = 0; x < 12; x++) {
				// todo: find note for x
				NoteType noteType = NoteType.ByMIDINote(x);
				
				TaskResultSeries noteTRS = trs.getFilteredByBaseNote(noteType);
				if (!noteTRS.isEmpty()) {
					percentage = (int)Math.round((double)noteTRS.getNoteSuccessRatio() * 100d);

					int ySize = noteTRS.getNNoteAttempts();
					if (ySize < 2) ySize = 2;
					if (ySize > 24) ySize = 24;
					g.setColor(colorForSuccessPercentage(percentage));
					g.fillRect(x * 10 + 41, (y * 25) + 24 - ySize, 9, ySize);
				}
			} // next base note
		}
		
		g.drawImage(uiImages.get("allChordsImage"), 0, 0, null);
	}
	

	@Override
	public void mouseMoved(MouseEvent e) {
		if (music.getLesson() == null) {
			setToolTipText(null);
			return;
		}
		
		// set tooltip text:

		int x = e.getX();
		int y = e.getY();
		// System.out.println(e.getX()+","+e.getY());

		if ((y > 0) && (y < 300)) {
			// TODO: can be 11 at max, but we only have 9 chord types (0..8)!
			int typeIdx = y / 25;  
			ChordType chordType = chordLookup.get(typeIdx);
			
			if (chordType == null) return; // this chord type does not exist
			
			String subjectName = chordType.getName();
			
			ChordStats stats = music.getStats().getChordStats();
			
			// single chord/tone area
			if ((x >= 40) && (x < 160)) {
				int noteIdx = (x - 40) / 10;
				NoteType noteType = NoteType.ByMIDINote(noteIdx);

				subjectName = subjectName + " based on " + noteType.getName();

				TaskResultSeries trsAll = stats.getAttemptsByType(chordType);
				TaskResultSeries trsByNote = trsAll.getFilteredByBaseNote(noteType);
				int t = trsByNote.getNTypeAttempts();
				int s = trsByNote.getNTypeSuccesses();
				if (t > 0) {
					subjectName = subjectName + ": " + s + " of " + t + " ("
							+ ((double) ((int) ((s * 1000d) / t)) / 10) + "%)";
				}
				
				setToolTipText(subjectName);
			}

			// general chord area
			if ((x > 0) && (x < 40)) {
				TaskResultSeries trs = stats.getAttemptsByType(chordType); 
				int t = trs.getNTypeAttempts();
				int s = trs.getNTypeSuccesses();
				if (t > 0) {
					subjectName = subjectName + ": " + s + " of " + t + " ("
							+ ((double) ((int) ((s * 1000d) / t)) / 10) + "%)";
				}
				
				setToolTipText(subjectName);
			}
		}
	}
	
	

}
