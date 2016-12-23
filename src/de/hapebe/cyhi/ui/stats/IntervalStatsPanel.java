package de.hapebe.cyhi.ui.stats;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import de.hapebe.cyhi.CanYouHearIt;
import de.hapebe.cyhi.logical.IntervalStats;
import de.hapebe.cyhi.logical.TaskResultSeries;
import de.hapebe.cyhi.musical.IntervalType;
import de.hapebe.cyhi.musical.NoteType;

public class IntervalStatsPanel extends StatsPanel {
	private static final long serialVersionUID = 8256155214505032872L;
	
	Map<Integer, IntervalType> intervalLookup = new HashMap<Integer, IntervalType>();
	Map<IntervalType, Integer> intervalReverseLookup = new HashMap<IntervalType, Integer>();
	
	
	public IntervalStatsPanel(CanYouHearIt music) {
		super(music);
		
		// create a rubrication (axis) for Interval Types:
		int i = 0;
		for (IntervalType intervalType : IntervalType.TYPES) {
			if (intervalType.equals(IntervalType.UNISON)) continue; // skip unison
			
			intervalLookup.put(i, intervalType);
			intervalReverseLookup.put(intervalType, i);
			i++;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int height = 300;
		int yOffset = 0;
		
		int percentage = -1;
		IntervalStats stats = music.getStats().getIntervalStats();

		for (IntervalType type : IntervalType.TYPES) {
			if (type.equals(IntervalType.UNISON)) continue; // skip unison

			int y = intervalReverseLookup.get(type); // graphical row of this interval type

			TaskResultSeries trs = stats.getAttemptsByType(type);
			if (!trs.isEmpty()) {
				percentage = (int)Math.round((double)trs.getTypeSuccessRatio() * 100d);
			
				int ySize = trs.getNTypeAttempts();
				if (ySize < 2) ySize = 2;
				if (ySize > 24)	ySize = 24;
				
				g.setColor(colorForSuccessPercentage(percentage));
				g.fillRect(0, height - (y * 25) - ySize + yOffset, 39, ySize);
			}

			// single chord/note stats
			for (NoteType noteType : NoteType.DISTINCT_TYPES) {
				TaskResultSeries noteTRS = trs.getFilteredByBaseNote(noteType);
				
				int x = noteType.getMidiNote() % 12;
				
				if (!noteTRS.isEmpty()) {
					percentage = (int)Math.round((double)noteTRS.getNoteSuccessRatio() * 100d);

					int ySize = noteTRS.getNNoteAttempts();
					if (ySize < 2) ySize = 2;
					if (ySize > 24) ySize = 24;
					g.setColor(colorForSuccessPercentage(percentage));
					g.fillRect(x * 10 + 41, height - (y * 25) - ySize + yOffset, 9, ySize);
				}
			} // next base note
		}
		
		g.drawImage(uiImages.get("intervalsImage"), 0, 0, null);
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
			int typeIdx = (300 - y) / 25;  
			IntervalType intervalType = intervalLookup.get(typeIdx);

			IntervalStats stats = music.getStats().getIntervalStats();

			String subjectName = intervalType.getNamePlural();
			
			// single interval/note area
			if ((x > 40) && (x < 160)) {
				
				int noteIdx = (x - 40) / 10;
				NoteType noteType = NoteType.ByMIDINote(noteIdx);

				subjectName += " based on " + noteType.getName();
				
				TaskResultSeries trsAll = stats.getAttemptsByType(intervalType); 
				TaskResultSeries trsByNote = trsAll.getFilteredByBaseNote(noteType);
				
				int t = trsByNote.getNTypeAttempts();  // count of trials
				int s = trsByNote.getNTypeSuccesses(); // count of successes
				
				if (t > 0) {
					subjectName = subjectName + ": " + s + " of " + t + " ("
							+ ((double) ((int) ((s * 1000d) / t)) / 10) + "%)";
				}
				setToolTipText(subjectName);
			}

			// general interval area
			if ((x > 0) && (x < 40)) {
				TaskResultSeries trs = stats.getAttemptsByType(intervalType);
				
				int t = trs.getNTypeAttempts();  // count of trials
				int s = trs.getNTypeSuccesses(); // count of successes
				
				if (t > 0) {
					subjectName = subjectName + ": " + s + " of " + t + " ("
							+ ((double) ((int) ((s * 1000d) / t)) / 10) + "%)";
				}
				setToolTipText(subjectName);
			}
		}
	}
	
	
	

}
