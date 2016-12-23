package de.hapebe.cyhi;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import de.hapebe.cyhi.ui.UserNameDialog;
import de.hapebe.cyhi.ui.lesson.BaseTonePanel;
import de.hapebe.cyhi.ui.lesson.ChordTypePanel;
import de.hapebe.cyhi.ui.lesson.IntervalTypePanel;
import de.hapebe.cyhi.ui.lesson.TypePanel;
import de.hapebe.cyhi.ui.stats.ChordStatsPanel;
import de.hapebe.cyhi.ui.stats.IntervalStatsPanel;
import de.hapebe.cyhi.ui.stats.StatsPanel;

public class CanYouHearIt extends JApplet implements Runnable, ActionListener {

	private static final long serialVersionUID = 8030909355879840540L;

	final Set<LookAndFeelItem> lookAndFeels = new HashSet<LookAndFeelItem>();

	MidiPlayer midiPlayer = new MidiPlayer();
	
	JFrame parentFrame = null;
	JPanel contentPane;
	JPanel inputPanel;

	StatsContainer stats;
	StatsPanel statsPanel;

	JMenuBar menuBar;
	JMenu fileMenu;
	JMenu userMenu;
	JMenu optionMenu;
	JMenuItem miShowChordName;
	JMenu helpMenu;

	BaseTonePanel baseTonePanel;

	TypePanel genderPanel;
	ChordTypePanel chordTypePanel;
	IntervalTypePanel intervalTypePanel;

	JPanel controlPanel;
	JLabel controlPanelNameLabel;
	JButton playButton;
	JButton stopButton;
	JButton skipButton;

	JPanel submitPanel;
	JButton submitButton;
	
	PreviousTaskPanel previousTaskPanel;

	URL codeBase;

	URL iconURL;
	ImageIcon icon;

	boolean firstTime = true;
	public boolean appletMode = true;

	Thread runner;
	int runnerCycle = -1;

	boolean uShowChordName = false;
	String userName = null;

	int baseToneChoice = -1;
	ChordType chordTypeChoice = null;
	IntervalType intervalTypeChoice = null;

	Lesson lesson;

	AudioClip[] ac = new AudioClip[43];

	public CanYouHearIt() {
		// This is a hack to avoid an ugly error message in 1.1.
		getRootPane().putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
	}

	public void setParentFrame(JFrame parent) {
		parentFrame = parent;
	}

	public Lesson getLesson() {
		return lesson;
	}

	public StatsContainer getStats() {
		return stats;
	}

	void initAudioClips() {
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
	}

	void doRunner() {
		if (firstTime) {
			inputPanel.setBounds(inputPanel.getBounds().x, inputPanel.getBounds().y, inputPanel.getBounds().width,
					contentPane.getBounds().height);
			inputPanel.validate();
			firstTime = false;
			repaint();

			if (!appletMode) {
				setUserName("anonymous");
				// while (userName == null) promptUser();
				if (statsPanel != null)	statsPanel.repaint();
			} else {
				userName = "applet user";
			}
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	public void init() {

		try {
			codeBase = getCodeBase();
		} catch (NullPointerException e) {
			appletMode = false;
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		}

		// initAudioClips();
		try {
			midiPlayer.openMidi();
		} catch (RuntimeException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "MIDI not available", JOptionPane.ERROR_MESSAGE, null);
		}

		// setLayout(new GridLayout(1,1));

		// ***********************************************
		initMenu();

		// ***********************************************************
		contentPane = (JPanel) getContentPane();

		contentPane.setBackground(Color.white);

		// contentPane.setLayout(new GridLayout(1,1)); //3 rows, 1 column
		contentPane.setLayout(null);
		contentPane.setBackground(UIManager.getLookAndFeel().getDefaults().getColor("Panel.background"));

		// ****************************************************
		inputPanel = new JPanel();
		inputPanel.setBounds(0, 0, 480, 400);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

		// base note chooser:
		baseTonePanel = new BaseTonePanel(this);
		Dimension prefSize = baseTonePanel.getPreferredSize();
		baseTonePanel.setBounds(0, 70, prefSize.width, prefSize.height);
		
		// type chooser (and their parent):
		{
			chordTypePanel = new ChordTypePanel(this);
			intervalTypePanel = new IntervalTypePanel(this);
		}
		genderPanel = new TypePanel(intervalTypePanel, chordTypePanel);
		genderPanel.setBounds(0, 130, 480, 80);
		
		initControlPanel();
		initSubmitPanel();
		
		previousTaskPanel = new PreviousTaskPanel(midiPlayer); 

		// **************************************************
		inputPanel.add(controlPanel);
		inputPanel.add(baseTonePanel);
		inputPanel.add(genderPanel);
		inputPanel.add(submitPanel);
		inputPanel.add(previousTaskPanel);

		// ***************************************************
		contentPane.add(inputPanel);
		inputPanel.validate();

		contentPane.validate();
		contentPane.repaint();
	}

	void initControlPanel() {
		ResourceLoader loader = ResourceLoader.getInstance();
		
		controlPanel = new JPanel();
		controlPanel.setBounds(0, 0, 480, 70);
		controlPanel.setPreferredSize(new Dimension(480, 70));
		controlPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER));

		icon = loader.getImageIcon("img/Play24.gif", "play");
		playButton = new JButton("Play", icon);
		playButton.setEnabled(false);
		playButton.setToolTipText("play the current chord");
		playButton.setMnemonic(KeyEvent.VK_P);
		playButton.addActionListener(this);

		icon = loader.getImageIcon("img/Stop24.gif", "stop");
		stopButton = new JButton("Stop", icon);
		stopButton.setEnabled(false);
		stopButton.setToolTipText("stop playback");
		stopButton.addActionListener(this);

		icon = loader.getImageIcon("img/StepForward24.gif", "skip");
		skipButton = new JButton("Skip", icon);
		skipButton.setEnabled(false);
		skipButton.setToolTipText("skip this chord");
		skipButton.addActionListener(this);

		p.add(Box.createRigidArea(new Dimension(10, 0)));
		p.add(playButton);
		p.add(Box.createRigidArea(new Dimension(20, 0)));
		p.add(stopButton);
		p.add(Box.createRigidArea(new Dimension(10, 0)));
		// p.add(skipButton);
		// p.add(Box.createRigidArea(new Dimension(10,0)));

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.CENTER));

		controlPanelNameLabel = new JLabel();
		p2.add(controlPanelNameLabel);

		controlPanel.add(p);
		controlPanel.add(p2);
	}

	void initSubmitPanel() {
		submitPanel = new JPanel();
		submitPanel.setBounds(0, 320, 480, 120);
		submitPanel.setPreferredSize(new Dimension(480, 120));
		submitPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		submitPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		submitButton = new JButton("Guess");
		submitButton.setEnabled(false);
		submitButton.setToolTipText("guess the current chord");
		submitButton.setMnemonic(KeyEvent.VK_G);
		submitButton.addActionListener(this);

		submitPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		submitPanel.add(submitButton);
		submitPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	}

	void initMenu() {
		ResourceLoader loader = ResourceLoader.getInstance();

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenuItem mi;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("File&Control operations");
		menuBar.add(fileMenu);

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

		if (!appletMode) {
			userMenu.addSeparator();

			mi = new JMenuItem("Switch User...", KeyEvent.VK_S);
			mi.setEnabled(true);
			mi.addActionListener(this);
			userMenu.add(mi);
		}

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

	public void start() {
		validate();

		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}

	public void stop() {
		midiPlayer.stopMusic();
		
		if (runner != null) {
			runner = null;
		}
	}

	public void destroy() {
		midiPlayer.closeMidi();
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (runner == thisThread) {
			doRunner();
		}
	}

	public void update(Graphics g) {
		super.update(g);
	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	public Dimension getPreferredSize() {
		return new Dimension(640, 400);
	}

	public Dimension getMinimumSize() {
		return new Dimension(640, 400);
	}

	void enableGeneralControls() {
		baseTonePanel.enableControls(lesson);
		
		playButton.setEnabled(true);
		stopButton.setEnabled(true);
		skipButton.setEnabled(true);
		submitButton.setEnabled(true);
	}

	void disableControls() {
		genderPanel.disableControls();
		
		playButton.setEnabled(false);
		stopButton.setEnabled(false);
		skipButton.setEnabled(false);
		submitButton.setEnabled(false);
	}
	
	private void endLesson() {
		controlPanelNameLabel.setText("");
		baseTonePanel.clearSelection();
		genderPanel.clearSelection();
		disableControls();

		// we leave the stats panel as is, so the user can still review their stats until they start a new lesson
	}

	private void startNewLesson(int lessonType) {
		// if we have a left-over stats panel, let's get rid of it:
		if (statsPanel != null) {
			contentPane.remove(statsPanel);
			contentPane.validate();
			
			statsPanel = null;
		}
		
		lesson = new Lesson(lessonType, Preferences.getInstance().getLessonLength());
		lesson.initNew();
		
		genderPanel.updateFor(lesson);
		baseTonePanel.updateFor(lesson);
		
		updateControlPanelNameLabel();
		enableGeneralControls();
		
		if (lessonType == Lesson.TYPE_INTERVAL_LESSON) {
			statsPanel = new IntervalStatsPanel(this);
		} else if (lessonType == Lesson.TYPE_CHORD_LESSON) {
			statsPanel = new ChordStatsPanel(this);
		} else {
			System.err.println("Cannot start a lesson of type " + lessonType + ".");
		}
		Dimension size = statsPanel.getPreferredSize();
		statsPanel.setBounds(480, 0, size.width, size.height);
		// statsPanel.setBounds(statsPanel.getBounds().x, statsPanel.getBounds().y,	contentPane.getBounds().width - inputPanel.getBounds().width, contentPane.getBounds().height);
		statsPanel.validate();
		
		contentPane.add(statsPanel);
		
		statsPanel.validate();
		statsPanel.repaint();
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
			firstTime = true;
			contentPane.validate();
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
			startNewLesson(Lesson.TYPE_INTERVAL_LESSON);
		}
		if (cmd.equals("Series of Chords")) {
			// TODO: clean up old lesson?
			startNewLesson(Lesson.TYPE_CHORD_LESSON);
		}
		if (cmd.equals("Save & Quit")) {
			controlPanelNameLabel.setText("");
			baseTonePanel.clearSelection();
			genderPanel.clearSelection();
			disableControls();

			save();

			if (!appletMode)
				System.exit(0);
		}
		if (cmd.equals("Show Chord Name")) {
			if (uShowChordName)
				uShowChordName = false;
			else
				uShowChordName = true;
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
				updateControlPanelNameLabel();
				disableControls();
				
				statsPanel.repaint();
			}
		}
		if (cmd.equals("Help")) {
			if (appletMode) {
				try {
					getAppletContext().showDocument(new URL(codeBase, "help.html"), "_blank");
				} catch (MalformedURLException ex) {
				}
			} else // application
			{
				JOptionPane.showMessageDialog(this,
						"For Help you can refer to\n> help.html <\nprovided in the application directory.\n\nwww.hapebe.de",
						"Getting Help...", JOptionPane.INFORMATION_MESSAGE, null);
				return;
			}
		}
		if (cmd.equals("About")) {
			ImageIcon icon = loader.getImageIcon("img/canyouhearit.gif", "Can You Hear It? 2.0");
			String[] message = new String[3];
			message[0] = "version 2.0";
			message[1] = "�2000,2016 Hans-Peter Bergner";
			message[2] = "www.hapebe.de";
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

		if (e.getSource() == submitButton) {
			evaluateGuess();
			return;
		}
		if (e.getSource() == playButton) {
			midiPlayer.playMusic(lesson.getCurrentTask());
			return;
		}
		if (e.getSource() == stopButton) {
			midiPlayer.stopMusic();
			return;
		}
		if (e.getSource() == skipButton) {
			midiPlayer.stopMusic();
			lesson.goToNextTask();
			baseToneChoice = -1;
			chordTypeChoice = null;
			intervalTypeChoice = null;
			updateControlPanelNameLabel();
			return;
		}

	}

	void updateAllUI() {
		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(chordTypePanel);
		SwingUtilities.updateComponentTreeUI(intervalTypePanel);
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
			} else if (lt instanceof TheoInterval) {
				lessonTypeString = "chord";
				taskObjectName = ((TheoChord) lt).getName();
			}
			if (taskObjectName != null) {
				if (!uShowChordName)
					taskObjectName = "?";
				controlPanelNameLabel.setText(lessonTypeString + " " + (lesson.getCurrentTaskIndex() + 1) + " of "
						+ lesson.getLength() + ": " + taskObjectName);
			} else {
				// chordN == null (?)
				controlPanelNameLabel.setText("");
			}
		} else {
			// nothing going on here...
			controlPanelNameLabel.setText("");
		}
	}

	void promptUser() {
		UserNameDialog und = new UserNameDialog(parentFrame);
		und.pack();
		und.setLocationRelativeTo(parentFrame);
		und.setVisible(true);

		String s = und.getValidatedText();
		if ((s != null) && (!s.equals(""))) setUserName(s);
	}
	
	void setUserName(String userName) {
		save();
		
		this.userName = userName;
		userMenu.setText("User: " + userName);
		
		stats = StatsIO.getInstance().loadStats(userName + "-stats.json");
	}

	void evaluateGuess() {
		ResourceLoader loader = ResourceLoader.getInstance();
		
		midiPlayer.stopMusic();

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

		previousTaskPanel.setLessonTask(lt, guessTask);
		
		if (!lesson.isAtEnd()) {
			lesson.goToNextTask();
		}


		updateControlPanelNameLabel();
		
		baseToneChoice = -1;
		baseTonePanel.clearSelection();

		chordTypeChoice = null;
		intervalTypeChoice = null;
		genderPanel.clearSelection();

		if (lesson.isAtEnd()) {
			controlPanelNameLabel.setText("");
			disableControls();
			showLessonStats(); // final evaluation
			
			endLesson();
			lesson = null;
		} else {
			statsPanel.repaint();
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
		
		final JFrame frame = new JFrame("Can You Hear It? 2.0");
		final CanYouHearIt music = new CanYouHearIt();

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				music.save();
				
				music.stop();
				music.destroy();
				
				frame.setVisible(false);
				frame.dispose();
				System.exit(0);
			}
		});

		frame.getContentPane().add(music);
		frame.setVisible(true);

		frame.setSize(
				music.getPreferredSize().width + frame.getInsets().left + frame.getInsets().right,
				music.getPreferredSize().height + frame.getInsets().top + frame.getInsets().bottom);

		music.setSize(music.getPreferredSize());
		music.setParentFrame(frame);
		music.init();
		music.validate();
		music.repaint();

		music.start();
	}

} // end class
