package com.muellerspaeth.patty;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Patty {
	public static void main(String[] args) {
		try {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Patty");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException: " + e.getMessage());
		} catch (InstantiationException e) {
			System.out.println("InstantiationException: " + e.getMessage());
		} catch (IllegalAccessException e) {
			System.out.println("IllegalAccessException: " + e.getMessage());
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Gui();
			}
		});
	}
}
