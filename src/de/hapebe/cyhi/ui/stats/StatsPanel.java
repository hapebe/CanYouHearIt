package de.hapebe.cyhi.ui.stats;
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

public abstract class StatsPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -1089135848428518249L;
	
	protected CanYouHearIt music;

//	Image img[];
	Map<String, Image> uiImages = new HashMap<String, Image>();
	
	public StatsPanel(CanYouHearIt music) {
		this.music = music;
		
		setToolTipText(null);
		addMouseMotionListener(this);
	}
	
	private void validateUiImages() {
		ResourceLoader loader = ResourceLoader.getInstance();
		
		if (uiImages.get("allChordsImage") == null) {
			uiImages.put("allChordsImage", loader.getImageIcon("img/allchords.png", "allChordsImage").getImage());
		}
		if (uiImages.get("intervalsImage") == null) {
			uiImages.put("intervalsImage", loader.getImageIcon("img/allintervals.png", "intervalsImage").getImage());
		}
		if (uiImages.get("notesImage") == null) {
			uiImages.put("notesImage", loader.getImageIcon("img/notes.gif", "").getImage());
		}
	}

	public void paint(Graphics g) {
		// g.setColor(new Color(0xff0000));
		// g.drawRect(0,0,159,376);
		
		// make sure we have our images ready:
		validateUiImages();
		
		super.paint(g);

		// paint stats
		Lesson lesson = music.getLesson();
		
		if ((lesson == null) || (lesson.isEmpty())) {
			// getCurrentLesson() is null or empty
			g.setColor(getBackground());
			g.fillRect(0, 0, getSize().width, getSize().height);
			return;
		}
		
		g.drawImage(uiImages.get("notesImage"), 40, 300, null);

		
		// the derived classes do the details for intervals or chords (or whatever)

		
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

}