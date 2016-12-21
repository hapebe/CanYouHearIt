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
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.hapebe.cyhi.io.ResourceLoader;
import de.hapebe.cyhi.logical.Lesson;
import de.hapebe.cyhi.logical.LessonTask;
import de.hapebe.cyhi.logical.StatsContainer;
import de.hapebe.cyhi.musical.ChordType;
import de.hapebe.cyhi.musical.IntervalType;
import de.hapebe.cyhi.musical.NoteType;
import de.hapebe.cyhi.musical.TheoChord;
import de.hapebe.cyhi.musical.TheoInterval;
import de.hapebe.cyhi.musical.TheoNote;
import de.hapebe.cyhi.realtime.NoteOffTask;
import de.hapebe.cyhi.ui.StatsPanel;
import de.hapebe.cyhi.ui.UserNameDialog;
import de.hapebe.cyhi.ui.lesson.BaseTonePanel;
import de.hapebe.cyhi.ui.lesson.ChordTypePanel;
import de.hapebe.cyhi.ui.lesson.IntervalTypePanel;
import de.hapebe.cyhi.ui.lesson.TypePanel;

public class CanYouHearIt extends JApplet implements Runnable, ActionListener {

	private static final long serialVersionUID = 8030909355879840540L;

	// Sequencer sequencer;
	Synthesizer synthesizer;
	Instrument instruments[];
	MidiChannel midiChannels[];
	MidiChannel cc;

	NoteOffTask noteOffTask;

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
	JCheckBoxMenuItem miJava;
	JCheckBoxMenuItem miWindows;
	JCheckBoxMenuItem miMac;
	JCheckBoxMenuItem miCDE;
	JCheckBoxMenuItem miSystem;
	JMenu helpMenu;

	BaseTonePanel baseTonePanel;
//	ButtonGroup baseToneGroup;
//	ArrayList<JRadioButton> baseToneRadioButtons;

	TypePanel genderPanel;
	ChordTypePanel chordTypePanel;
//	ButtonGroup genderGroup;
//	List<JRadioButton> genderRadioButtons;
	IntervalTypePanel intervalTypePanel;
//	ButtonGroup intervalGroup;
//	List<JRadioButton> intervalRadioButtons;

	JPanel controlPanel;
	JLabel controlPanelNameLabel;
	JButton playButton;
	JButton stopButton;
	JButton skipButton;

	JPanel submitPanel;
	JButton submitButton;

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

	protected final int LESSON_LENGTH = 10;
	Lesson lesson; // new Lesson(Lesson.TYPE_INTERVAL_LESSON, LESSON_LENGTH);

	AudioClip[] ac = new AudioClip[43];

	public CanYouHearIt() {
		// This is a hack to avoid an ugly error message in 1.1.
		getRootPane().putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
	}

	public void setParentFrame(JFrame parent) {
		parentFrame = parent;
	}

	public Lesson getCurrentLesson() {
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

	void playMusic() {
		/*
		 * //for use with samples: ac[chord[currentChord][0]].play(); if
		 * (chord[currentChord][1]!=-1) ac[chord[currentChord][1]].play(); if
		 * (chord[currentChord][2]!=-1) ac[chord[currentChord][2]].play(); if
		 * (chord[currentChord][3]!=-1) ac[chord[currentChord][3]].play();
		 */

		// what do we want to hear?
		List<Integer> midiNotes = new ArrayList<Integer>();
		LessonTask lt = lesson.getCurrentTask();
		if (lt instanceof TheoInterval) {
			TheoInterval i = (TheoInterval) lt;
			// log: what is it?
			midiNotes.add(i.getBaseNote().getMIDINote());
			midiNotes.add(i.getPartnerNote().getMIDINote());
		} else if (lt instanceof TheoChord) {
			TheoChord c = (TheoChord) lt;
			// log: what is it?
			for (TheoNote n : c.getNotes()) {
				midiNotes.add(n.getMIDINote());
			}
		} else {
			throw new RuntimeException(
					"The current lesson is of an unexpected type: " + lesson.getClass().getSimpleName());
		}

		// for use with midi:
		stopMusic();
		noteOffTask = new NoteOffTask(cc);

		for (int midiNote : midiNotes) {
			noteOffTask.addNote(midiNote);
			cc.noteOn(midiNote, 127);
		}

		java.util.Timer t = new java.util.Timer();
		t.schedule(noteOffTask, 2000);
	}

	void stopMusic() {
		/*
		 * //for use with samples for (int i=0;i<ac.length;i++) { if (ac[i] !=
		 * null) ac[i].stop(); }
		 */

		// for use with midi:
		if (noteOffTask != null) {
			noteOffTask.cancel();
			noteOffTask.run();
		}
	}


	void doRunner() {
		// contentPane.validate();
		// System.out.println(contentPane.getBounds().height+"");
		if (firstTime) {
			inputPanel.setBounds(inputPanel.getBounds().x, inputPanel.getBounds().y, inputPanel.getBounds().width,
					contentPane.getBounds().height);
			inputPanel.validate();
			statsPanel.setBounds(statsPanel.getBounds().x, statsPanel.getBounds().y,
					contentPane.getBounds().width - inputPanel.getBounds().width, contentPane.getBounds().height);
			statsPanel.validate();
			firstTime = false;
			repaint();

			if (!appletMode) {
				setUserName("anonymous");
				// while (userName == null) promptUser();
				
				stats = StatsContainer.load(userName + ".dat");
				if (stats == null)
					stats = new StatsContainer();

				statsPanel.repaint();
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
		openMidi();

		// setLayout(new GridLayout(1,1));

		// ***********************************************
		initMenu();

		// ***********************************************************
		// contentPane = new JPanel();
		// setContentPane(contentPane);
		contentPane = (JPanel) getContentPane();
		// Container contentPane = getContentPane();

		contentPane.setBackground(Color.white);

		// contentPane.setLayout(new GridLayout(1,1)); //3 rows, 1 column
		contentPane.setLayout(null);

		// ****************************************************
		inputPanel = new JPanel();
		inputPanel.setBounds(0, 0, 480, 400);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

		statsPanel = new StatsPanel();
		statsPanel.music = this;
		Dimension size = statsPanel.getPreferredSize();
		statsPanel.setBounds(480, 0, size.width, size.height);

		initBaseTonePanel();
		initGenderPanel();
		initControlPanel();
		initSubmitPanel();

		// **************************************************
		inputPanel.add(controlPanel);
		inputPanel.add(baseTonePanel);
		inputPanel.add(genderPanel);
		inputPanel.add(submitPanel);

		// ***************************************************
		contentPane.add(inputPanel);
		inputPanel.validate();
		contentPane.add(statsPanel);
		statsPanel.validate();

		contentPane.validate();
		contentPane.repaint();
		// System.out.println(label1.getBounds().x+"
		// "+label1.getBounds().width+","+label1.getBounds().y+"
		// "+label1.getBounds().height);
		// System.out.println(label2.getBounds().x+"
		// "+label2.getBounds().width+","+label2.getBounds().y+"
		// "+label2.getBounds().height);
		// System.out.println(label3.getBounds().x+"
		// "+label3.getBounds().width+","+label3.getBounds().y+"
		// "+label3.getBounds().height);
		// System.out.println(statsPanel.getBounds().x+","+statsPanel.getBounds().y);
		// System.out.println(inputPanel.getBounds().width+","+inputPanel.getBounds().height);
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

	void initBaseTonePanel() {
		baseTonePanel = new BaseTonePanel(this);
		Dimension prefSize = baseTonePanel.getPreferredSize();
		baseTonePanel.setBounds(0, 70, prefSize.width, prefSize.height);
	}

	void initGenderPanel() {
		chordTypePanel = new ChordTypePanel(this);

		intervalTypePanel = new IntervalTypePanel(this);

		genderPanel = new TypePanel(intervalTypePanel, chordTypePanel);
		genderPanel.setBounds(0, 130, 480, 80);
	}

	void initSubmitPanel() {
		submitPanel = new JPanel();
		submitPanel.setBounds(0, 320, 480, 60);
		submitPanel.setPreferredSize(new Dimension(480, 60));
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
		mi.setEnabled(true);
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

		miJava = new JCheckBoxMenuItem("Java");
		miJava.setMnemonic(KeyEvent.VK_J);
		miJava.setEnabled(true);
		miJava.addActionListener(this);
		lfm.add(miJava);

		miWindows = new JCheckBoxMenuItem("Windows");
		miWindows.setMnemonic(KeyEvent.VK_W);
		miWindows.setEnabled(true);
		miWindows.addActionListener(this);
		lfm.add(miWindows);

		miMac = new JCheckBoxMenuItem("Mac");
		miMac.setMnemonic(KeyEvent.VK_M);
		miMac.setEnabled(true);
		miMac.addActionListener(this);
		lfm.add(miMac);

		miCDE = new JCheckBoxMenuItem("CDE/Motif");
		miCDE.setMnemonic(KeyEvent.VK_C);
		miCDE.setEnabled(true);
		miCDE.addActionListener(this);
		lfm.add(miCDE);

		lfm.addSeparator();

		miSystem = new JCheckBoxMenuItem("Your System");
		miSystem.setMnemonic(KeyEvent.VK_Y);
		miSystem.setEnabled(true);
		miSystem.setState(true);
		miSystem.addActionListener(this);
		lfm.add(miSystem);

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
				if ((synthesizer = MidiSystem.getSynthesizer()) == null) {
					System.out.println("MidiSystem.getSynthesizer() failed!");
					return;
				}
			}

			try {
				synthesizer.open();
			} catch (MidiUnavailableException e) {
				JOptionPane.showMessageDialog(this,
						"Couldn't open MIDI synthesizer.\n  Probably another programm using synthesizer?\n  might as well be a JAVA problem...\n  ...or a BUG!?!",
						"Midi not available", JOptionPane.INFORMATION_MESSAGE, null);
				if (!appletMode)
					System.exit(-1);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
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
		/*
		 * if (sequencer != null) { sequencer.close(); } sequencer = null;
		 */
		synthesizer = null;
		instruments = null;
		midiChannels = null;
	}

	public void start() {

		validate();

		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}

	public void stop() {

		stopMusic();
		if ((userName != null)) {
			stats.saveStats(userName + ".dat");
		}

		if (runner != null) {
			runner = null;
		}
	}

	public void destroy() {
		closeMidi();
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

	// *******************************************
	public void actionPerformed(ActionEvent e) {
		ResourceLoader loader = ResourceLoader.getInstance();

		String cmd = e.getActionCommand();
		// System.out.println(cmd);
		if (cmd.equals("Series of Intervals")) {
			// TODO: clean up old lesson?
			lesson = new Lesson(Lesson.TYPE_INTERVAL_LESSON, LESSON_LENGTH);
			lesson.initNew();
			
			genderPanel.updateFor(lesson);
			baseTonePanel.updateFor(lesson);
			
			resetLessonStats();
			statsPanel.repaint();
			updateControlPanelNameLabel();
			enableGeneralControls();
		}
		if (cmd.equals("Series of Chords")) {
			// TODO: clean up old lesson?
			lesson = new Lesson(Lesson.TYPE_CHORD_LESSON, LESSON_LENGTH);
			lesson.initNew();
			
			genderPanel.updateFor(lesson);
			baseTonePanel.updateFor(lesson);

			resetLessonStats();
			statsPanel.repaint();
			updateControlPanelNameLabel();
			enableGeneralControls();
		}
		if (cmd.equals("Save & Quit")) {
			controlPanelNameLabel.setText("");
			baseTonePanel.clearSelection();
			genderPanel.clearSelection();
			disableControls();

			stats.saveStats(userName + ".dat");

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

		if (cmd.equals("Java")) {
			miJava.setState(true);
			miWindows.setState(false);
			miSystem.setState(false);
			miMac.setState(false);
			miCDE.setState(false);
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
			}
			updateAllUI();
			firstTime = true;
			contentPane.validate();
			repaint();
		}

		if (cmd.equals("Windows")) {
			miJava.setState(false);
			miWindows.setState(true);
			miSystem.setState(false);
			miMac.setState(false);
			miCDE.setState(false);
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} catch (Exception ex) {
			}
			updateAllUI();
			firstTime = true;
			contentPane.validate();
			repaint();
		}

		if (cmd.equals("Mac")) {
			miJava.setState(false);
			miWindows.setState(false);
			miSystem.setState(false);
			miMac.setState(true);
			miCDE.setState(false);
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
				// UIManager.setLookAndFeel(
				// "javax.swing.plaf.mac.MacLookAndFeel" );
			} catch (Exception ex) {
			}
			updateAllUI();
			firstTime = true;
			contentPane.validate();
			repaint();
		}

		if (cmd.equals("CDE/Motif")) {
			miJava.setState(false);
			miWindows.setState(false);
			miSystem.setState(false);
			miMac.setState(false);
			miCDE.setState(true);
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			} catch (Exception ex) {
			}
			updateAllUI();
			firstTime = true;
			contentPane.validate();
			repaint();
		}

		if (cmd.equals("Your System")) {
			miJava.setState(false);
			miWindows.setState(false);
			miSystem.setState(true);
			miMac.setState(false);
			miCDE.setState(false);
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ex) {
			}
			updateAllUI();
			firstTime = true;
			contentPane.validate();
			repaint();
		}

		if (cmd.equals("Switch User...")) {
			String beforeUser = userName;
			promptUser();
			if (!userName.equals(beforeUser)) {
				// there's a new user!
				lesson.rewind();
				updateControlPanelNameLabel();
				disableControls();
				stats = StatsContainer.load(userName + ".dat");
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
			message[1] = "©2000,2016 Hans-Peter Bergner";
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
			playMusic();
			return;
		}
		if (e.getSource() == stopButton) {
			stopMusic();
			return;
		}
		if (e.getSource() == skipButton) {
			stopMusic();
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

	void endLesson() {
		controlPanelNameLabel.setText("");
		baseTonePanel.clearSelection();
		genderPanel.clearSelection();
		disableControls();
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

	void resetLessonStats() {
		lesson.stats().clear();
		lesson.setStartTime(System.currentTimeMillis());
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
		// TODO: save current stats container, load/create new one
		
		this.userName = userName;
		userMenu.setText("User: " + userName);
	}

	void evaluateGuess() {
		ResourceLoader loader = ResourceLoader.getInstance();
		
		stopMusic();

		String iconFile = "";
		String message = "";
		String afterMessage = "";

		boolean baseToneRight = false;
		boolean typeRight = false;

		// System.out.println("choice: "+baseToneChoice+","+typeChoice);
		// System.out.println(chordArrayType[currentChord]+","+chordArrayBase[currentChord]+"("+(chordArrayBase[currentChord]%12)+")");
		LessonTask lt = lesson.getCurrentTask();

		if (baseToneChoice >= 0) {
			// if any base tone was set in the UI:
			if (lt.getBaseNote().isNote(NoteType.ByMIDINote(baseToneChoice)))
				baseToneRight = true;
		}

		if (lt instanceof TheoChord) {
			TheoChord t = (TheoChord) lt;
			if (t.getType().equals(chordTypeChoice))
				typeRight = true;

			lesson.stats().registerChordAttempt(t, typeRight, baseToneRight);
		} else if (lt instanceof TheoInterval) {
			TheoInterval i = (TheoInterval) lt;
			if (i.getType().equals(intervalTypeChoice))
				typeRight = true;

			lesson.stats().registerIntervalAttempt(i, typeRight, baseToneRight);
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
					TheoChord guess = new TheoChord(new TheoNote(baseToneChoice), chordTypeChoice);
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
					TheoInterval guess = new TheoInterval(new TheoNote(baseToneChoice), intervalTypeChoice);
					afterMessage = "\n( Your guess: " + guess.getName() + " )";
				} else {
					afterMessage = "\n( Your guess: " + intervalTypeChoice.getName() + " )";
				}
			}
		}

		ImageIcon icon = loader.getImageIcon(iconFile, "");
		JOptionPane.showMessageDialog(this, message + "\n" + lesson.getCurrentTask().getName() + afterMessage, "Result",
				JOptionPane.INFORMATION_MESSAGE, icon);

		if (lesson.isAtEnd()) {
			endLesson();
			showLessonStats();
			lesson = null;
		} else {
			lesson.goToNextTask();
		}

		baseToneChoice = -1;
		chordTypeChoice = null;
		intervalTypeChoice = null;

		updateControlPanelNameLabel();
		baseTonePanel.clearSelection();
		genderPanel.clearSelection();

		if (lesson.isAtEnd()) {
			// final evaluation
			controlPanelNameLabel.setText("");
			disableControls();
		}

		statsPanel.repaint();
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
		
		final JFrame appletFrame = new JFrame("Can You Hear It? 2.0");
		final CanYouHearIt music = new CanYouHearIt();

		appletFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				music.stop();
				music.destroy();
				appletFrame.setVisible(false);
				appletFrame.dispose();
				System.exit(0);
			}
		});

		appletFrame.getContentPane().add(music);
		appletFrame.setVisible(true);

		appletFrame.setSize(
				music.getPreferredSize().width + appletFrame.getInsets().left + appletFrame.getInsets().right,
				music.getPreferredSize().height + appletFrame.getInsets().top + appletFrame.getInsets().bottom);

		music.setSize(music.getPreferredSize());

		music.setParentFrame(appletFrame);

		music.init();
		music.validate();
		music.repaint();

		music.start();
	}

} // end class
