package de.hapebe.cyhi.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hapebe.cyhi.io.MidiPlayer;
import de.hapebe.cyhi.io.ResourceLoader;
import de.hapebe.cyhi.logical.LessonTask;

public class PreviousTaskPanel extends JPanel implements ActionListener {
	JLabel guess;
	JLabel actual;
	
	LessonTask actualTask;
	
	MidiPlayer midiPlayer;
	
	public PreviousTaskPanel(MidiPlayer midiPlayer) {
		super();
		this.midiPlayer = midiPlayer;
		
		guess = new JLabel("");
		this.add(guess);
		
		actual = new JLabel("");
		this.add(actual);

		ResourceLoader loader = ResourceLoader.getInstance();

		add(Box.createRigidArea(new Dimension(10, 0)));

		ImageIcon icon = loader.getImageIcon("img/play16.png", "play");
		JButton btn = new JButton("", icon);
		btn.setEnabled(true);
		btn.setToolTipText("play");
		btn.setActionCommand("play-chord");
		btn.addActionListener(this);
		add(btn);

		// add(Box.createRigidArea(new Dimension(10, 0)));

		icon = loader.getImageIcon("img/up16.png", "play");
		btn = new JButton("", icon);
		btn.setEnabled(true);
		btn.setToolTipText("play upwards");
		btn.setActionCommand("play-up");
		btn.addActionListener(this);

		add(btn);
		
		// add(Box.createRigidArea(new Dimension(10, 0)));
		
		icon = loader.getImageIcon("img/down16.png", "play");
		btn = new JButton("", icon);
		btn.setEnabled(true);
		btn.setToolTipText("play downwards");
		btn.setActionCommand("play-down");
		btn.addActionListener(this);

		add(btn);
		
		add(Box.createRigidArea(new Dimension(10, 0)));
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("play-chord")) {
			midiPlayer.playMusic(actualTask);
		} else if (cmd.equals("play-up")) {
			// TODO
		} else if (cmd.equals("play-down")) {
			// TODO
		}
		
	}
	
	public void setLessonTask(LessonTask actualTask, LessonTask guessTask) {
		this.actualTask = actualTask;
		
		if (guessTask != null) {
			guess.setText("G: " + guessTask.getName());
		} else {
			guess.setText("G: ?");
		}
		actual.setText(actualTask.getName());
	}

}
