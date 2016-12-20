package de.hapebe.cyhi.ui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import de.hapebe.cyhi.CanYouHearIt;
import de.hapebe.cyhi.io.ResourceLoader;
import de.hapebe.cyhi.logical.Lesson;
import de.hapebe.cyhi.logical.LessonTask;
import de.hapebe.cyhi.logical.StatsContainer;
import de.hapebe.cyhi.logical.TaskResult;
import de.hapebe.cyhi.logical.TaskResultSeries;
import de.hapebe.cyhi.musical.ChordType;
import de.hapebe.cyhi.musical.IntervalType;
import de.hapebe.cyhi.musical.NoteType;
import de.hapebe.cyhi.musical.TheoChord;

public class StatsPanel extends JPanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -1089135848428518249L;
	
	static final Dimension PREFERRED_SIZE = new Dimension(160, 400);
	
	public CanYouHearIt music;

	Image img[];
	Map<String, Image> uiImages = new HashMap<String, Image>();
	
	Map<Integer, ChordType> chordLookup = new HashMap<Integer, ChordType>();
	Map<ChordType, Integer> chordReverseLookup = new HashMap<ChordType, Integer>();

	Map<Integer, IntervalType> intervalLookup = new HashMap<Integer, IntervalType>();
	Map<IntervalType, Integer> intervalReverseLookup = new HashMap<IntervalType, Integer>();

	public StatsPanel() {
		setToolTipText(null);
		addMouseMotionListener(this);
		
		// create a rubrication (axis) for Chord Types:
		int i = 0;
		for (ChordType chordType : ChordType.TYPES) {
			chordLookup.put(i, chordType);
			chordReverseLookup.put(chordType, i);
			i++;
		}

		// create a rubrication (axis) for Interval Types:
		i = 0;
		for (IntervalType intervalType : IntervalType.TYPES) {
			intervalLookup.put(i, intervalType);
			intervalReverseLookup.put(intervalType, i);
			i++;
		}

	}
	
	private void validateUiImages() {
		ResourceLoader loader = ResourceLoader.getInstance();
		
		if (uiImages.get("allChordsImage") == null) {
			uiImages.put("allChordsImage", loader.getImageIcon("img/allchords.gif", "allChordsImage").getImage());
		}
		if (uiImages.get("intervalsImage") == null) {
			uiImages.put("intervalsImage", loader.getImageIcon("img/intervals.gif", "intervalsImage").getImage());
		}
		if (uiImages.get("notesImage") == null) {
			uiImages.put("notesImage", loader.getImageIcon("img/notes.gif", "").getImage());
		}
	}

	public void paint(Graphics g) {
		// g.setColor(new Color(0xff0000));
		// g.drawRect(0,0,159,376);
		
		validateUiImages();


		super.paint(g);

		// paint stats
		Lesson lesson = music.getCurrentLesson();
		int percentage = -1;
		int red, green;
		if ((lesson != null) && (!lesson.isEmpty())) {
			StatsContainer stats = music.getStats();
			
			if (lesson.getType() == Lesson.TYPE_CHORD_LESSON) {
				for (ChordType chordType : ChordType.TYPES) {
					int y = chordReverseLookup.get(chordType); // graphical row of this chord type

					TaskResultSeries trs = stats.getChordAttemptsByType(chordType);
					if (!trs.isEmpty()) {
						percentage = (int)Math.round((double)trs.getTypeSuccessRatio() * 100d);
					
						int ySize = trs.getNTypeAttempts();
						if (ySize < 2) ySize = 2;
						if (ySize > 24)	ySize = 24;
						
						g.setColor(colorForSuccessPercentage(percentage));
						g.fillRect(0, y * 25 + (24 - ySize), 39, ySize);
					}
	
					// single chord/note stats
					for (int x = 0; x < 12; x++) {
						// todo: find note for x
						NoteType noteType = NoteType.ByMIDINote(x);
						
						TaskResultSeries noteTRS = new TaskResultSeries();
						for (TaskResult tr : trs) {
							LessonTask lt = tr.getLessonTask();
							TheoChord chord = (TheoChord)lt; // this should be okay, because we check the lesson type outside this loop:
							if (chord.getBaseNote().isNote(noteType)) {
								noteTRS.add(tr);
							}
						}
	
						if (!noteTRS.isEmpty()) {
							percentage = (int)Math.round((double)noteTRS.getNoteSuccessRatio() * 100d);

							int ySize = noteTRS.getNNoteAttempts();
							if (ySize < 2) ySize = 2;
							if (ySize > 20) ySize = 20;
							g.setColor(colorForSuccessPercentage(percentage));
							g.fillRect(x * 10 + 41, y * 25 + 3 + (20 - ySize), 8, ySize);
						}
					} // next note
				}
			}

			if (music.getCurrentLesson().getType() == Lesson.TYPE_CHORD_LESSON) {
				g.drawImage(uiImages.get("allChordsImage"), 0, 0, null);
			} else if (music.getCurrentLesson().getType() == Lesson.TYPE_INTERVAL_LESSON) {
				g.drawImage(uiImages.get("intervalsImage"), 0, 0, null);
			} else {
				System.err.println("Unexpected Lesson type " + music.getCurrentLesson().getType());
			}

			g.drawImage(uiImages.get("notesImage"), 40, 300, null);
		} else {
			// getCurrentLesson() is null or empty
			g.setColor(getBackground());
			g.fillRect(0, 0, getSize().width, getSize().height);
		}

		// System.out.println(getBounds().width+","+getBounds().height);
	}
	
	protected Color colorForSuccessPercentage(int percentage) {
		int red, green;
		
		if (percentage < 50) {
			red = 255;
		} else {
			red = (int) ((100 - percentage) * 2 * 2.55f);
		}
		if (percentage > 50) {
			green = 255;
		} else {
			green = (int) (percentage * 2 * 2.55f);
		}
		// System.out.println(percentage+"->"+red+","+green);
		return new Color(red, green, 0);
	}

	public void mouseMoved(MouseEvent e) {
		if (music.getCurrentLesson() == null) {
			setToolTipText(null);
			return;
		}

		int x = e.getX();
		int y = e.getY();
		// System.out.println(e.getX()+","+e.getY());

		// single chord/tone area
		if ((x > 40) && (x < 160)) {
			if ((y > 0) && (y < 300)) {
				// TODO: can be 11 at max, but we only have 9 chord types (0..8)!
				int typeIdx = (300 - y) / 25;  
				ChordType chordType = chordLookup.get(typeIdx);
				
				IntervalType intervalType = intervalLookup.get(typeIdx);
				
				int noteIdx = (x - 40) / 10;
				NoteType noteType = NoteType.ByMIDINote(noteIdx);

				String subjectName = "";
				int s = 0; // count of successes
				int t = 0; // count of trials
				subjectName = subjectName + noteType.getCode();
				if (music.getCurrentLesson().getType() == Lesson.TYPE_CHORD_LESSON) {
					if (chordType == null) return; // this chord type does not exist
					
					subjectName = subjectName + " " + chordType.getName();
					
					TaskResultSeries trsAll = music.getStats().getChordAttemptsByType(chordType);
					TaskResultSeries trsByNote = trsAll.getFilteredByBaseNote(noteType);
					t = trsByNote.getNTypeAttempts();
					s = trsByNote.getNTypeSuccesses();
				} else if (music.getCurrentLesson().getType() == Lesson.TYPE_INTERVAL_LESSON) {
					subjectName = intervalType.getNamePlural();
					
					TaskResultSeries trsAll = music.getStats().getIntervalAttemptsByType(intervalType); 
					TaskResultSeries trsByNote = trsAll.getFilteredByBaseNote(noteType);
					t = trsByNote.getNTypeAttempts();
					s = trsByNote.getNTypeSuccesses();
				} else {
					System.err.println("Unexpected lesson type: " + music.getCurrentLesson().getType());
				}
				if (t > 0) {
					subjectName = subjectName + ": " + s + " of " + t + " ("
							+ ((double) ((int) ((s * 1000d) / t)) / 10) + "%)";
				}
				setToolTipText(subjectName);
			}
		}

		// general chord area
		if ((x > 0) && (x < 40)) {
			if ((y > 0) && (y < 300)) {
				int typeIdx = (300 - y) / 25;  

				String subjectName = "";
				int s = 0; // count of successes
				int t = 0; // count of trials
				if (music.getCurrentLesson().getType() == Lesson.TYPE_CHORD_LESSON) {
					ChordType chordType = chordLookup.get(typeIdx);
					subjectName = chordType.getName()+"s";
					
					TaskResultSeries trs = music.getStats().getChordAttemptsByType(chordType); 
					t = trs.getNTypeAttempts();
					s = trs.getNTypeSuccesses();
				} else if (music.getCurrentLesson().getType() == Lesson.TYPE_INTERVAL_LESSON) {
					IntervalType intervalType = intervalLookup.get(typeIdx);
					subjectName = intervalType.getNamePlural();
					
					TaskResultSeries trs = music.getStats().getIntervalAttemptsByType(intervalType); 
					t = trs.getNTypeAttempts();
					s = trs.getNTypeSuccesses();
				} else {
					System.err.println("Unexpected lesson type: " + music.getCurrentLesson().getType());
				}
				if (t > 0) {
					subjectName = subjectName + ": " + s + " of " + t + " ("
							+ ((double) ((int) ((s * 1000d) / t)) / 10) + "%)";
				}
				setToolTipText(subjectName);
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}
	
	

}