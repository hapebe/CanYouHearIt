package de.hapebe.cyhi.ui;

import javax.swing.JCheckBoxMenuItem;

public class LookAndFeelItem {
	final String name;
	final String className;
	final JCheckBoxMenuItem menuItem;
	
	public LookAndFeelItem(String name, String className, JCheckBoxMenuItem menuItem) {
		super();
		this.name = name;
		this.className = className;
		this.menuItem = menuItem;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	public JCheckBoxMenuItem getMenuItem() {
		return menuItem;
	}
	
	
}
