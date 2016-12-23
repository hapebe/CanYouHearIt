package de.hapebe.cyhi.io;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ResourceLoader {

	private static ResourceLoader instance;
	
	private ResourceLoader() {	}
	
	public static ResourceLoader getInstance() {
		if (instance == null) instance = new ResourceLoader();
		return instance;
	}
	
	/**
	 * loads an ImageIcon from application resources - i.e. no user-created files
	 * @param filename
	 * @param name
	 * @return
	 */
	public ImageIcon getImageIcon(String filename, String name) {
		ImageIcon temp = null;

		// try to read a file from within the JAR:
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		try {
			Image im = ImageIO.read(in);
			temp = new ImageIcon(im);
			temp.setDescription(name);
		} catch (IOException e) {
			System.err.println("Couldn't open image file " + filename + ": " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("Couldn't open image file " + filename + ": " + e.getMessage());
			e.printStackTrace();
		}

		if (temp == null) System.err.println("Returning null in getImageIcon(" + filename + "," + name + ")");
		
		// debug:
		// System.out.println("Returning " + temp.getImage() + " for getImageIcon(" + filename + "," + name + ")");
		
		return temp;
	}

//	public Image myGetImage(String filename) {
//		Image temp = null;
//
//		URL codeBase = null;
//		try {
//			codeBase = getCodeBase();
//		} catch (NullPointerException e) {
//		}
//		URL url = null;
//		if (codeBase != null) {
//			try {
//				url = new URL(codeBase, filename);
//				temp = getToolkit().createImage(url);
//			} catch (java.net.MalformedURLException e) {
//				System.err.println("Couldn't open file: badly specified URL");
//				return null;
//			}
//		}
//
//		if (codeBase == null) {
//			temp = getToolkit().createImage(filename);
//		}
//
//		MediaTracker mt = new MediaTracker(this);
//		mt.addImage(temp, 0);
//		try {
//			mt.waitForAll();
//		} catch (InterruptedException e) {
//		}
//
//		return temp;
//	}
	
	
}
