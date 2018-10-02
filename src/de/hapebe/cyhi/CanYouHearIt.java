package de.hapebe.cyhi;

import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import de.hapebe.cyhi.io.MidiPlayer;
import de.hapebe.cyhi.io.ResourceLoader;
import de.hapebe.cyhi.io.StatsIO;
import de.hapebe.cyhi.logical.Lesson;
import de.hapebe.cyhi.logical.LessonTask;
import de.hapebe.cyhi.logical.StatsContainer;
import de.hapebe.cyhi.logical.TaskGuess;
import de.hapebe.cyhi.musical.ChordType;
import de.hapebe.cyhi.musical.IntervalType;
import de.hapebe.cyhi.musical.NoteType;
import de.hapebe.cyhi.musical.TheoChord;
import de.hapebe.cyhi.musical.TheoInterval;
import de.hapebe.cyhi.musical.TheoNote;
import de.hapebe.cyhi.ui.LookAndFeelItem;
import de.hapebe.cyhi.ui.PreviousTaskPanel;
import de.hapebe.cyhi.ui.SoundTaskStage;
import de.hapebe.cyhi.ui.StatusBar;
import de.hapebe.cyhi.ui.UserNameDialog;
import de.hapebe.cyhi.ui.lesson.BaseTonePanel;
import de.hapebe.cyhi.ui.lesson.ChordTypePanel;
import de.hapebe.cyhi.ui.lesson.IntervalTypePanel;
import de.hapebe.cyhi.ui.lesson.TypePanel;
import de.hapebe.cyhi.ui.stats.ChordStatsPanel;
import de.hapebe.cyhi.ui.stats.IntervalStatsPanel;
import de.hapebe.cyhi.ui.stats.StatsPanel;

public class CanYouHearIt extends JFrame implements ActionListener {

	private static final long serialVersionUID = 8030909355879840540L;
	
	public final static String VERSION = "2.1";
	public final static String DEFAULT_USER = "anonymous";

	final Set<LookAndFeelItem> lookAndFeels = new HashSet<LookAndFeelItem>();
	
	SoundTaskStage soundTaskStage = new SoundTaskStage();

	StatsContainer stats;

	JMenuBar menuBar;
	JMenu fileMenu;
	JMenu userMenu;
	JMenu optionMenu;
	JMenuItem miShowChordName;
	JMenu helpMenu;
	
	JToolBar toolBar;
	StatusBar statusBar;

	URL iconURL;
	ImageIcon icon;

	boolean firstTime = true;

	Thread runner;
	int runnerCycle = -1;

	boolean uShowChordName = false;
	
	String userName = null;

	int baseToneChoice = -1;
	ChordType chordTypeChoice = null;
	IntervalType intervalTypeChoice = null;

	Lesson lesson;

	// AudioClip[] ac = new AudioClip[43];

	public CanYouHearIt() {
		super("Can You Hear It? " + VERSION); // JFrame ...
		
		Config.app = this;
		Config.listener = this;
		Config.midiPlayer = new MidiPlayer();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit(true);
			}
		});	
	}

	public Lesson getLesson() {
		return lesson;
	}

	public StatsContainer getStats() {
		return stats;
	}

	/*void initAudioClips() {
		String filename;
		for (int i = 0; i < ac.length; i++) {
			filename = "wav/" + (i + 6) + ".wav";
			URL codeBase = null;
			System.out.println(i + ": " + filename);
			try {
				codeBase = getCodeBase();
			} catch (NullPointerException e) {
			}
			if (codeBase != null) {
				ac[i] = getAudioClip(codeBase, filename);
			}

			if (codeBase == null) {
				URL fileURL = null;
				try {
					fileURL = new URL("file://" + System.getProperty("user.dir") + System.getProperty("file.separator")
							+ filename);
				} catch (MalformedURLException e) {
					System.err.println("Couldn't read Audio File " + i + " .");
					System.exit(0);
				}
				System.out.println(fileURL + "");
				ac[i] = newAudioClip(fileURL);
			}
		}
	}*/

	public void init() {
		try {
		 	UIManager.setLookAndFeel(Preferences.getInstance().getLookAndFeel());
		 } catch (Exception ex) {}

		// initAudioClips();
		try {
			Config.midiPlayer.openMidi();
		} catch (RuntimeException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "MIDI not available", JOptionPane.ERROR_MESSAGE, null);
		}

		getContentPane().setLayout(new BorderLayout());

		// ***********************************************
		initMenu();
		initToolBar();

		statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		soundTaskStage.init();
		getContentPane().add(soundTaskStage);
		
		// ***********************************************
		setUserName(DEFAULT_USER);
		
		// switch accelerator keys for play/stop ...
		JPanel target = (JPanel) getContentPane(); 
	    target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	    	KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), 
	    	"play"
	    );
	    target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
		    	KeyStroke.getKeyStroke(KeyEvent.VK_P, 0, false), 
		    	"play"
		    );
	    target.getActionMap().put("play",  new AbstractAction() {
	        public void actionPerformed(ActionEvent actionEvent) {
	        	System.out.println("Action: play");
	        	playAction();
	        }
	    });
	    // ... and guess:
	    target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
    		KeyStroke.getKeyStroke(KeyEvent.VK_G, 0, false), 
    		"guess"
	    );
	    target.getActionMap().put("guess",  new AbstractAction() {
	        public void actionPerformed(ActionEvent actionEvent) {
	        	System.out.println("Action: guess");
	        	guessAction();
	        }
	    });
	}

	private void initMenu() {
		ResourceLoader loader = ResourceLoader.getInstance();

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenuItem mi;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("File&Control operations");
		menuBar.add(fileMenu);

		mi = new JMenuItem("Switch User...", KeyEvent.VK_S);
		mi.setEnabled(true);
		mi.addActionListener(this);
		fileMenu.add(mi);
		
		fileMenu.addSeparator();

		mi = new JMenuItem("Save & Quit", KeyEvent.VK_Q);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		mi.addActionListener(this);
		fileMenu.add(mi);


		userMenu = new JMenu("User");
		userMenu.setMnemonic(KeyEvent.VK_U);
		userMenu.getAccessibleContext().setAccessibleDescription("File&Control operations");
		menuBar.add(userMenu);

		mi = new JMenuItem("Series of Intervals", KeyEvent.VK_I);
		mi.addActionListener(this);
		userMenu.add(mi);

		mi = new JMenuItem("Series of Chords", KeyEvent.VK_C);
		mi.addActionListener(this);
		userMenu.add(mi);

		optionMenu = new JMenu("Options");
		optionMenu.setMnemonic(KeyEvent.VK_O);
		optionMenu.getAccessibleContext().setAccessibleDescription("Options");
		menuBar.add(optionMenu);

		icon = loader.getImageIcon("img/NoCheck24.gif", "unchecked");
		miShowChordName = new JMenuItem("Show Chord Name", icon);
		miShowChordName.setMnemonic(KeyEvent.VK_C);
		miShowChordName.setEnabled(true);
		miShowChordName.addActionListener(this);
		// optionMenu.add(miShowChordName);

		JMenu lfm = new JMenu("Look&Feel");
		lfm.setMnemonic(KeyEvent.VK_L);
		lfm.setEnabled(true);
		
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	    	JCheckBoxMenuItem cmi = new JCheckBoxMenuItem(info.getName());
			cmi.addActionListener(this);
			if (info.getName() == UIManager.getLookAndFeel().getName()) {
				cmi.setSelected(true);
			}
			lookAndFeels.add(new LookAndFeelItem(info.getName(), info.getClassName(), cmi));
			lfm.add(cmi); 
	    }
		optionMenu.add(lfm);

		menuBar.add(Box.createHorizontalGlue());

		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.getAccessibleContext().setAccessibleDescription("Help&Assistance");
		menuBar.add(helpMenu);

		ImageIcon splash = loader.getImageIcon("img/Splat.gif", "splash icon");
		mi = new JMenuItem("Help", splash);
		mi.setEnabled(true);
		mi.setMnemonic(KeyEvent.VK_H);
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mi.addActionListener(this);
		helpMenu.add(mi);

		helpMenu.addSeparator();

		ImageIcon about = loader.getImageIcon("img/About24.gif", "about icon");
		mi = new JMenuItem("About", about);
		mi.setMnemonic(KeyEvent.VK_A);
		mi.addActionListener(this);
		helpMenu.add(mi);
	}
	
	private void initToolBar() {
        toolBar = new JToolBar();
        toolBar.setSize(480, 20);
        toolBar.setFloatable(false);

		ResourceLoader loader = ResourceLoader.getInstance();
		
		JButton btnLessonIntv = new JButton(loader.getImageIcon("img/StepBack24.gif", "play"));
		btnLessonIntv.setMargin(new Insets(0,0,0,0));
		btnLessonIntv.setActionCommand("Series of Intervals");
		btnLessonIntv.setToolTipText("Start a new lesson of intervals");
		btnLessonIntv.addActionListener(Config.Listener());
		
		JButton btnLessonChord = new JButton(loader.getImageIcon("img/StepForward24.gif", "play"));
		btnLessonChord.setMargin(new Insets(0,0,0,0));
		btnLessonChord.setActionCommand("Series of Chords");
		btnLessonChord.setToolTipText("Start a new lesson of chords");
		btnLessonChord.addActionListener(Config.Listener());
		
		toolBar.add(btnLessonIntv);
		toolBar.add(btnLessonChord);
        
        getContentPane().add(toolBar, BorderLayout.NORTH);
	}

	public void stop() {
		Config.midiPlayer.stopMusic();
		
		if (runner != null) runner = null;
	}

	public void destroy() {
		Config.midiPlayer.closeMidi();
	}
	
	public void quit(boolean save) {
		if (save) save();
		
		stop();
		destroy();
		
		setVisible(false);
		dispose();
		System.exit(0);		
	}

	public Dimension getPreferredSize() {
		return new Dimension(640, 440);
	}

	public Dimension getMinimumSize() {
		return new Dimension(640, 440);
	}

	private void newLesson(int lessonType) {
		lesson = new Lesson(lessonType, Preferences.getInstance().getLessonLength());
		lesson.initNew();
		
		soundTaskStage.updateFor(lesson);
		updateControlPanelNameLabel();
		soundTaskStage.enableGeneralControls(lesson);
		
		statusBar.setMessage("Lesson: " + lesson.getTypeName());
	}
	
	private void endLesson() {
		soundTaskStage.setControlPanelNameLabel("");
		soundTaskStage.clear();
		soundTaskStage.disableControls();
		// we leave the stats panel as is, so the user can still review their stats until they start a new lesson
		statusBar.setMessage("");
	}

	private void setLookAndFeel(String name) {
		LookAndFeelItem lnf = null;
		for (LookAndFeelItem candidate : lookAndFeels) {
			if (candidate.getName().equals(name)) {
				lnf = candidate;
				lnf.getMenuItem().setState(true);
			} else {
				candidate.getMenuItem().setState(false);
			}
		}
		
		if (lnf != null) {
			Preferences.getInstance().setLookAndFeel(name);
			
			try {
				UIManager.setLookAndFeel(lnf.getClassName());
			} catch (Exception ex) {
			}
			updateAllUI();
			getRootPane().validate();
			repaint();
		}
	}

	// *******************************************
	public void actionPerformed(ActionEvent e) {
		ResourceLoader loader = ResourceLoader.getInstance();

		String cmd = e.getActionCommand();
		// System.out.println(cmd);
		if (cmd.equals("Series of Intervals")) {
			// TODO: clean up old lesson?
			newLesson(Lesson.TYPE_INTERVAL_LESSON);
		}
		if (cmd.equals("Series of Chords")) {
			// TODO: clean up old lesson?
			newLesson(Lesson.TYPE_CHORD_LESSON);
		}
		if (cmd.equals("Save & Quit")) {
			soundTaskStage.clear();
			soundTaskStage.disableControls();
			
			quit(true);
		}
		if (cmd.equals("Show Chord Name")) {
			uShowChordName = uShowChordName ? false : true;
			updateControlPanelNameLabel();
			if (uShowChordName) {
				icon = loader.getImageIcon("img/Check24.gif", "checkmark");
				miShowChordName.setIcon(icon);
			}
			if (!uShowChordName) {
				icon = loader.getImageIcon("img/NoCheck24.gif", "unchecked");
				miShowChordName.setIcon(icon);
			}
		}

		// one of the Look'n'Feels?
		for (LookAndFeelItem i : lookAndFeels) {
			if (cmd.equals(i.getName())) {
				setLookAndFeel(i.getName());
			}
		}

		if (cmd.equals("Switch User...")) {
			String beforeUser = userName;
			promptUser();
			if (!userName.equals(beforeUser)) {
				// there's a new user!
				lesson.rewind();
				soundTaskStage.updateFor(lesson);
			}
		}
		
		if (cmd.equals("Help")) {
			JOptionPane.showMessageDialog(this,
					"For Help you can refer to\n> help.html <\nprovided in the application directory.\n\nwww.hapebe.de",
					"Getting Help...", JOptionPane.INFORMATION_MESSAGE, null);
			return;
		}
		
		if (cmd.equals("About")) {
			ImageIcon icon = loader.getImageIcon("img/canyouhearit.gif", "Can You Hear It? " + VERSION);
			String[] message = new String[3];
			message[0] = "version " + VERSION;
			message[1] = "©2000-2018 Hans-Peter Bergner";
			message[2] = "http://www.hapebe.de/";
			JOptionPane.showMessageDialog(this, message, "About \"Can You Hear It?\"...",
					JOptionPane.INFORMATION_MESSAGE, icon);
			return;
		}
		if (cmd.startsWith("basetone:")) {
			String t = cmd.substring(9);
			System.out.println("basetone clicked: " + t);
			baseToneChoice = NoteType.ForCode(t).getMidiNote() % 12;
			return;
		}
		if (cmd.startsWith("chord:")) {
			String c = cmd.substring(6);
			System.out.println("chord type clicked: " + c);
			chordTypeChoice = ChordType.ForCode(c);
			return;
		}
		if (cmd.startsWith("interval:")) {
			String i = cmd.substring(9);
			System.out.println("interval type clicked: " + i);
			intervalTypeChoice = IntervalType.ForCode(i);
			return;
		}

		if (cmd.equals("submitGuess")) {
			guessAction();
			return;
		}
		if (cmd.equals("play")) {
			playAction();
			return;
		}
		if (cmd.equals("stop")) {
			Config.midiPlayer.stopMusic();
			return;
		}
		if (cmd.equals("skip")) {
			Config.midiPlayer.stopMusic();
			lesson.goToNextTask();
			baseToneChoice = -1;
			chordTypeChoice = null;
			intervalTypeChoice = null;
			updateControlPanelNameLabel();
			return;
		}

	}
	
	private void playAction() {
		if (lesson != null) {
			Config.midiPlayer.playMusic(lesson.getCurrentTask());
		}
	}
	
	private void guessAction() {
		if (lesson != null) {
			evaluateGuess();
		}
	}

	void updateAllUI() {
		SwingUtilities.updateComponentTreeUI(this);
		// SwingUtilities.updateComponentTreeUI(chordTypePanel);
		// SwingUtilities.updateComponentTreeUI(intervalTypePanel);
	}

	void showLessonStats() {
		long lessonTime = (System.currentTimeMillis() - lesson.getStartTime()) / 1000;
		long min = lessonTime / 60;
		long sec = lessonTime % 60;
		String timeString = "" + min + ":" + (sec < 10 ? "0" + sec : "" + sec);
		String[] message = new String[4];
		message[0] = "Congratulations!";
		message[1] = "You have completed " + lesson.getLength() + " exercises.";
		message[2] = "time: " + timeString + "min";
		message[3] = "";
		JOptionPane.showMessageDialog(this, message, "Finished...", JOptionPane.PLAIN_MESSAGE, null);
	}

	void updateControlPanelNameLabel() {
		LessonTask lt = lesson.getCurrentTask();
		if (lt != null) {
			String lessonTypeString = "(unknown)", taskObjectName = "?";
			if (lt instanceof TheoInterval) {
				lessonTypeString = "interval";
				taskObjectName = ((TheoInterval) lt).getName();
			} else if (lt instanceof TheoChord) {
				lessonTypeString = "chord";
				taskObjectName = ((TheoChord) lt).getName();
			}
			
			if (taskObjectName != null) {
				if (!uShowChordName) taskObjectName = "?";
				StringBuilder sb = new StringBuilder();
				sb.append(lessonTypeString).append(" ");
				sb.append((lesson.getCurrentTaskIndex() + 1)).append(" of ");
				sb.append(lesson.getLength()).append(": ");
				sb.append(taskObjectName);
				soundTaskStage.setControlPanelNameLabel(sb.toString());
			} else {
				// chordN == null (?)
				soundTaskStage.setControlPanelNameLabel("");
			}
		} else {
			// nothing going on here...
			soundTaskStage.setControlPanelNameLabel("");
		}
	}

	void promptUser() {
		UserNameDialog und = new UserNameDialog(this);
		und.pack();
		und.setLocationRelativeTo(this);
		und.setVisible(true);

		String s = und.getValidatedText();
		if ((s != null) && (!s.equals(""))) {
			save();
			setUserName(s);
		}
	}
	
	void setUserName(String userName) {
		
		this.userName = userName;
		userMenu.setText("User: " + userName);
		
		stats = StatsIO.getInstance().loadStats(userName + "-stats.json");
	}

	void evaluateGuess() {
		Config.midiPlayer.stopMusic();

		ResourceLoader loader = ResourceLoader.getInstance();
		
		String iconFile = "";
		String message = "";
		String afterMessage = "";

		boolean baseToneRight = false;
		boolean typeRight = false;

		// System.out.println("choice: "+baseToneChoice+","+typeChoice);
		// System.out.println(chordArrayType[currentChord]+","+chordArrayBase[currentChord]+"("+(chordArrayBase[currentChord]%12)+")");
		LessonTask lt = lesson.getCurrentTask();
		LessonTask guessTask = null;
		
		TheoNote baseToneGuess = null;
		String baseToneGuessName = "?";

		if (baseToneChoice >= 0) {
			// if any base tone was set in the UI:
			if (lt.getBaseNote().isNote(NoteType.ByMIDINote(baseToneChoice)))
				baseToneRight = true;
			
			baseToneGuess = new TheoNote(baseToneChoice);
			baseToneGuessName = baseToneGuess.getCode();
		}

		if (lt instanceof TheoChord) {
			if (chordTypeChoice == null) return; // TODO: error message / ask for input
			
			TheoChord t = (TheoChord) lt;
			if (t.getType().equals(chordTypeChoice))
				typeRight = true;
			
			if (chordTypeChoice != null) {
				guessTask = new TaskGuess(baseToneGuessName + " " + chordTypeChoice.getName());
			} else {
				guessTask = new TaskGuess(baseToneGuessName + " ?");
			}

			stats.getChordStats().registerAttempt(t, typeRight, baseToneRight);
		} else if (lt instanceof TheoInterval) {
			if (intervalTypeChoice == null) return; // TODO: error message / ask for input
			
			TheoInterval i = (TheoInterval) lt;
			if (i.getType().equals(intervalTypeChoice))
				typeRight = true;

			if (intervalTypeChoice != null) {
				guessTask = new TaskGuess(baseToneGuessName + " + " + intervalTypeChoice.getName());
			} else {
				guessTask = new TaskGuess(baseToneGuessName + " + ?");
			}
			
			stats.getIntervalStats().registerAttempt(i, typeRight, baseToneRight);
		} else {
			System.err.println("Unexpected LessonTask: " + lt);
		}

		// evaluation
		if (!(baseToneRight || typeRight)) {
			iconFile = "img/Wrong48.gif";
			message = " That was wrong, then... ";
		}

		if (baseToneRight || typeRight) {
			iconFile = "img/Medium48.gif";
			message = " Quite O.K. ... ";
		}

		if (baseToneRight && typeRight) {
			iconFile = "img/Correct48.gif";
			message = " Congratulations! You made it! ";
		}

		// give the right answer
		if (lesson.getType() == Lesson.TYPE_CHORD_LESSON) {
			message = message + "\nThe chord was:";
			if (!(baseToneRight && typeRight)) {
				if (baseToneChoice >= 0) {
					// if any base tone was set in the UI:
					TheoChord guess = new TheoChord(baseToneGuess, chordTypeChoice);
					afterMessage = "\n( Your guess: " + guess.getName() + " )";
				} else {
					afterMessage = "\n( Your guess: " + chordTypeChoice.getName() + " )";
				}
			}
		}
		if (lesson.getType() == Lesson.TYPE_INTERVAL_LESSON) {
			message = message + "\nThe interval was:";
			if (!(baseToneRight && typeRight)) {
				if (baseToneChoice >= 0) {
					// if any base tone was set in the UI:
					TheoInterval guess = new TheoInterval(baseToneGuess, intervalTypeChoice);
					afterMessage = "\n( Your guess: " + guess.getName() + " )";
				} else {
					afterMessage = "\n( Your guess: " + intervalTypeChoice.getName() + " )";
				}
			}
		}

		ImageIcon icon = loader.getImageIcon(iconFile, "");
		JOptionPane.showMessageDialog(this, message + "\n" + lesson.getCurrentTask().getName() + afterMessage, "Result",
				JOptionPane.INFORMATION_MESSAGE, icon);

		soundTaskStage.setPreviousTask(lt, guessTask);
		
		baseToneChoice = -1;
		chordTypeChoice = null;
		intervalTypeChoice = null;
		
		soundTaskStage.clear();
		
		if (lesson.isAtEnd()) {
			soundTaskStage.disableControls();
			
			showLessonStats(); // final evaluation
			
			endLesson();
			lesson = null;
		} else {
			soundTaskStage.updateStats();
			lesson.goToNextTask();
			
			updateControlPanelNameLabel();
			Config.midiPlayer.playMusic(lesson.getCurrentTask());
		}
	}
	
	private void save() {
		// TODO save program status / options / preferences
		if (stats != null && userName != null) {
			StatsIO.getInstance().saveStats(stats, userName + "-stats.json");
		}
	}
	
	private static void printDebugInfos() {
		String workingDir = System.getProperty("user.dir");
		System.out.println("Current working directory : " + workingDir);

		String workingPath = "n/a";
		try {
			workingPath = CanYouHearIt.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println("Current working path : " + workingPath);
	}

	public static void main(String args[]) {
		for (String arg : args) {
			System.out.println("arg: " + arg);
			
			if (arg.equalsIgnoreCase("-x")) {
				printDebugInfos();
				System.exit(0);
			}
		}
		
		final CanYouHearIt music = new CanYouHearIt();

		music.setVisible(true);

		music.setSize(
				music.getPreferredSize().width + music.getInsets().left + music.getInsets().right,
				music.getPreferredSize().height + music.getInsets().top + music.getInsets().bottom);
		// music.setSize(music.getPreferredSize());
		music.init();
		music.validate();
		music.repaint();
	}

} // end class
