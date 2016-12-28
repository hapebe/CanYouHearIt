package de.hapebe.cyhi.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
		
		setBorder(BorderFactory.createTitledBorder("previous task"));
		setLayout(new GridLayout(1, 3));
		
		JPanel guessPanel = new JPanel();
		guessPanel.setLayout(new BoxLayout(guessPanel, BoxLayout.Y_AXIS));
		guessPanel.add(new JLabel("Your Guess:"));
		guess = new JLabel("");
		guessPanel.add(guess);
		guessPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		this.add(guessPanel);

		
		JPanel actualPanel = new JPanel();
		actualPanel.setLayout(new BoxLayout(actualPanel, BoxLayout.Y_AXIS));
		actualPanel.add(new JLabel("Actual Sound:"));
		actual = new JLabel("");
		actualPanel.add(actual);
		actualPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		this.add(actualPanel);

		
		ResourceLoader loader = ResourceLoader.getInstance();


		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

		ImageIcon icon = loader.getImageIcon("img/play16.png", "play");
		JButton btn = new JButton("", icon);
		btn.setEnabled(true);
		btn.setToolTipText("play");
		btn.setActionCommand("play-chord");
		btn.addActionListener(this);
		buttonPanel.add(btn);

		// add(Box.createRigidArea(new Dimension(10, 0)));

		icon = loader.getImageIcon("img/up16.png", "play");
		btn = new JButton("", icon);
		btn.setEnabled(true);
		btn.setToolTipText("play upwards");
		btn.setActionCommand("play-up");
		btn.addActionListener(this);

		buttonPanel.add(btn);
		
		// add(Box.createRigidArea(new Dimension(10, 0)));
		
		icon = loader.getImageIcon("img/down16.png", "play");
		btn = new JButton("", icon);
		btn.setEnabled(true);
		btn.setToolTipText("play downwards");
		btn.setActionCommand("play-down");
		btn.addActionListener(this);

		buttonPanel.add(btn);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		this.add(buttonPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("play-chord")) {
			midiPlayer.playMusic(actualTask);
		} else if (cmd.equals("play-up")) {
			midiPlayer.playArpeggio(actualTask, true);
		} else if (cmd.equals("play-down")) {
			midiPlayer.playArpeggio(actualTask, false);
		}
		
	}
	
	public void setLessonTask(LessonTask actualTask, LessonTask guessTask) {
		this.actualTask = actualTask;
		
		if (guessTask != null) {
			guess.setText(guessTask.getName());
		} else {
			guess.setText("?");
		}
		actual.setText(actualTask.getName());
	}

}
