package de.hapebe.cyhi.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hapebe.cyhi.Config;
import de.hapebe.cyhi.io.MidiPlayer;
import de.hapebe.cyhi.io.ResourceLoader;
import de.hapebe.cyhi.logical.LessonTask;

public class PreviousTaskPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -5912687124025838762L;
	
	JLabel guess;
	JLabel actual;
	
	JButton btnPlayChord;
	JButton btnPlayUp;
	JButton btnPlayDown;
	
	LessonTask actualTask;
		
	public PreviousTaskPanel() {
		super();
		
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

		btnPlayChord = new JButton("", loader.getImageIcon("img/play16.png", "play"));
		btnPlayChord.setToolTipText("play");
		btnPlayChord.setActionCommand("play-chord");
		btnPlayChord.addActionListener(this);
		buttonPanel.add(btnPlayChord);

		// add(Box.createRigidArea(new Dimension(10, 0)));

		btnPlayUp = new JButton("", loader.getImageIcon("img/up16.png", "play"));
		btnPlayUp.setToolTipText("play upwards");
		btnPlayUp.setActionCommand("play-up");
		btnPlayUp.addActionListener(this);

		buttonPanel.add(btnPlayUp);
		
		// add(Box.createRigidArea(new Dimension(10, 0)));
		
		btnPlayDown = new JButton("", loader.getImageIcon("img/down16.png", "play"));
		btnPlayDown.setToolTipText("play downwards");
		btnPlayDown.setActionCommand("play-down");
		btnPlayDown.addActionListener(this);

		buttonPanel.add(btnPlayDown);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
		// initially, we don't have an older task to review:
		disableControls();
		
		this.add(buttonPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MidiPlayer midiPlayer = Config.MidiPlayer();
		if (midiPlayer == null) return; // TODO: error handling...
		
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
		
		enableControls();
	}
	
	private void enableControls() {
		btnPlayChord.setEnabled(true);
		btnPlayUp.setEnabled(true);
		btnPlayDown.setEnabled(true);
	}

	public void disableControls() {
		btnPlayChord.setEnabled(false);
		btnPlayUp.setEnabled(false);
		btnPlayDown.setEnabled(false);
	}

}
