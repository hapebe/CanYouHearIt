package de.hapebe.cyhi.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * @author hapebe@gmx.de
 */
public class StatusBar extends JPanel {
	JLabel lblText;
	JLabel lblClock;
	
	Timer clockTimer;
	
	public StatusBar() {
		super();
		setPreferredSize(new Dimension(480,20));
		setBorder(new EmptyBorder(0,4,0,4));
		setLayout(new BorderLayout());
		
		lblText = new JLabel("", JLabel.LEFT);
		add(lblText, BorderLayout.WEST);
		
		lblClock = new JLabel("", JLabel.RIGHT);
		add(lblClock, BorderLayout.EAST);
		
		//display date time to status bar
		clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.Date now = new java.util.Date();
                String ss = DateFormat
                		.getTimeInstance(DateFormat.MEDIUM, Locale.GERMAN)
                		.format(now);
                lblClock.setText(ss);
                lblClock.setToolTipText("Current time: " + ss);
            }
        });
        clockTimer.start();		
	}
	
	public void setMessage(String text) {
		lblText.setText(text);
	}

}
