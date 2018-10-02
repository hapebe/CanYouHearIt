package de.hapebe.cyhi.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.hapebe.cyhi.Config;
import de.hapebe.cyhi.Preferences;
import de.hapebe.cyhi.io.ResourceLoader;
import de.hapebe.cyhi.logical.Lesson;
import de.hapebe.cyhi.logical.LessonTask;
import de.hapebe.cyhi.ui.lesson.BaseTonePanel;
import de.hapebe.cyhi.ui.lesson.ChordTypePanel;
import de.hapebe.cyhi.ui.lesson.IntervalTypePanel;
import de.hapebe.cyhi.ui.lesson.TypePanel;
import de.hapebe.cyhi.ui.stats.ChordStatsPanel;
import de.hapebe.cyhi.ui.stats.IntervalStatsPanel;
import de.hapebe.cyhi.ui.stats.StatsPanel;

/**
 * GUI class - view for working with interval or chord listening tasks
 * @author hapebe@gmx.de
 */
public class SoundTaskStage extends JPanel {
	private static final long serialVersionUID = -8323989887023356710L;

	// left layout column:
	BaseTonePanel baseTonePanel;
	TypePanel genderPanel;
	ChordTypePanel chordTypePanel;
	IntervalTypePanel intervalTypePanel;

	PreviousTaskPanel previousTaskPanel;

	// right layout column:
	StatsPanel statsPanel;

	JPanel controlPanel;
	JLabel controlPanelNameLabel;
	JButton playButton;
	JButton stopButton;
	JButton skipButton;

	JPanel submitPanel;
	JButton submitButton;
	
	public SoundTaskStage() {
		this(null, false); // JPanel
	}

	public SoundTaskStage(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public SoundTaskStage(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public SoundTaskStage(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}
	
	public void update(Graphics g) {
		super.update(g);
	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	public Dimension getPreferredSize() {
		return new Dimension(640, 380);
	}

	public Dimension getMinimumSize() {
		return new Dimension(640, 380);
	}

	public void init() {
		setBackground(Color.white);
		// setLayout(new GridLayout(1,1)); //rows, columns
		setLayout(null); // we will specify pos & dims explicitly
		setBackground(UIManager.getLookAndFeel().getDefaults().getColor("Panel.background"));

		// ****************************************************
		// play and stop:
		initControlPanel(); // 0, 0, 480, 70

		// base note chooser:
		baseTonePanel = new BaseTonePanel();
		baseTonePanel.setBounds(0, 70, 480, 60);
		
		// type chooser (and their parent):
		genderPanel = new TypePanel();
		genderPanel.setBounds(0, 130, 480, 80);

		// review of the preceding task:
		previousTaskPanel = new PreviousTaskPanel();
		previousTaskPanel.setBounds(0, 210, 480, 90);
		
		initSubmitPanel(); // 0, 300, 480, 80
		

		// **************************************************
		add(controlPanel);
		add(baseTonePanel);
		add(genderPanel);
		add(previousTaskPanel);
		add(submitPanel);

		// ***************************************************

		validate();
		repaint();
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

		playButton = new JButton("Play", loader.getImageIcon("img/Play24.gif", "play"));
		playButton.setMargin(new Insets(0,6,0,6));
		playButton.setActionCommand("play");
		playButton.setEnabled(false);
		playButton.setToolTipText("play the current chord");
		playButton.setMnemonic(KeyEvent.VK_P);
		playButton.addActionListener(Config.App());

		stopButton = new JButton("Stop", loader.getImageIcon("img/Stop24.gif", "stop"));
		stopButton.setMargin(new Insets(0,2,0,2));
		stopButton.setActionCommand("stop");
		stopButton.setEnabled(false);
		stopButton.setToolTipText("stop playback");
		stopButton.addActionListener(Config.App());

		skipButton = new JButton("Skip", loader.getImageIcon("img/StepForward24.gif", "skip"));
		skipButton.setMargin(new Insets(0,2,0,2));
		skipButton.setActionCommand("skip");
		skipButton.setEnabled(false);
		skipButton.setToolTipText("skip this chord");
		skipButton.addActionListener(Config.App());

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
		submitPanel.setBounds(0, 300, 480, 60);
		submitPanel.setPreferredSize(new Dimension(480, 60));
		submitPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		submitPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		submitButton = new JButton("Guess");
		submitButton.setActionCommand("submitGuess");
		submitButton.setEnabled(false);
		// TODO: update depending on the current lesson
		submitButton.setToolTipText("guess the current chord");
		submitButton.setMnemonic(KeyEvent.VK_G);
		submitButton.addActionListener(Config.Listener());

		submitPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		submitPanel.add(submitButton);
		submitPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		submitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("typed g"), "guessButton");
		submitPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("typed G"), "guessButton");
	}

	public void enableGeneralControls(Lesson lesson) {
		if (lesson != null) {
			baseTonePanel.enableControls(lesson);
			
			playButton.setEnabled(true);
			stopButton.setEnabled(true);
			skipButton.setEnabled(true);
			submitButton.setEnabled(true);
		}
	}

	public void disableControls() {
		baseTonePanel.disableControls();
		genderPanel.disableControls();
		previousTaskPanel.disableControls();
		
		playButton.setEnabled(false);
		stopButton.setEnabled(false);
		skipButton.setEnabled(false);
		submitButton.setEnabled(false);
	}
	
	public void clear() {
		controlPanelNameLabel.setText("");
		baseTonePanel.clearSelection();
		genderPanel.clearSelection();		
	}
	
	public void updateStats() {
		if (statsPanel != null) {
			statsPanel.repaint();
		}
	}
	
	public void updateFor(Lesson l) {
		// if we have a left-over stats panel, let's get rid of it:
		if (statsPanel != null) {
			remove(statsPanel);
			statsPanel = null;
		}
		
		genderPanel.updateFor(l);
		baseTonePanel.updateFor(l);
		// TODO maybe also update previousTaskPanel?
		
		if (l.getType() == Lesson.TYPE_INTERVAL_LESSON) {
			statsPanel = new IntervalStatsPanel();
		} else if (l.getType() == Lesson.TYPE_CHORD_LESSON) {
			statsPanel = new ChordStatsPanel();
		} else {
			System.err.println("Cannot start a lesson of type " + l.getType() + ".");
		}
		statsPanel.setBounds(480, 0, 160, 400);
		statsPanel.validate();
		add(statsPanel);
		statsPanel.repaint();

		validate();
	}
	
	public void setControlPanelNameLabel(String text) {
		controlPanelNameLabel.setText(text);
	}
	
	public void setPreviousTask(LessonTask actualTask, LessonTask guessTask) {
		previousTaskPanel.setLessonTask(actualTask, guessTask);
	}
	
	
}
