package com.muellerspaeth.patty;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import  com.muellerspaeth.patty.tools.ReadOnlyFileChooser;

public class Gui implements ActionListener, ItemListener  {
	
	protected Preferences prefs = Preferences.userNodeForPackage(Gui.class);
	protected final String pnPath = "CompletePath";
	protected final String mnuOpenPdf = "mnuOpenPDF";
	protected final String mnuOpenImg = "mnuOpenImg";
	
	private ImageExplorer expImages = new ImageExplorer("PDF Images - Explorer");
	private JTextArea output = new JTextArea();
	private static final String newline = "\n";
	
	public Gui() {
		createAndShowGUI();
	}

	public ImageExplorer getImageExplorer() {
		return expImages;
	}

	public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String name = source.getName();
        switch(name) {
        	case mnuOpenPdf:
        	case mnuOpenImg:
        		this.openFile(name);
        	break;
        }
    }
 	 
    // Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

	public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Item event detected."
                   + newline
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    New state: "
                   + ((e.getStateChange() == ItemEvent.SELECTED) ?
                     "selected":"unselected");
        output.append(s + newline);
        output.setCaretPosition(output.getDocument().getLength());
    }

	public void openFile(String type) {
		File currFile = new File(prefs.get(type + pnPath, ""));
		ImageIO.scanForPlugins();

		JFileChooser chooser = new JFileChooser(currFile);
		chooser.setDialogTitle("PDF Datei öffnen");
		FileFilter ffPdf = new FileNameExtensionFilter("PDF-Dateien", "pdf"); 
		chooser.setFileFilter(ffPdf);
		int result = chooser.showDialog(null, "PDF öffnen");
		if (result == JFileChooser.APPROVE_OPTION) {
			currFile = chooser.getSelectedFile();
			expImages.setVisible(false);
			expImages.dispose();
			expImages = new ImageExplorer("PDF Images - Explorer");
			//expImages.setJMenuBar(mainMenuBar);
			switch (type) {
				case mnuOpenPdf:
					try {
						new ImageExtractor(currFile, expImages);
					} catch (IOException e) {
						e.printStackTrace();
					}
				break;
				case mnuOpenImg:
					try {
						new ImageTool(currFile, expImages);
					} catch (Exception e) {
						e.printStackTrace();
					}
				break;
			}
			prefs.put(type + pnPath, currFile.getAbsolutePath());
			expImages.setVisible(true);
			expImages.setJMenuBar(this.createMenu());
		}
	}

	private JMenuBar createMenu()  {

		JMenuBar menuBar = new JMenuBar();
		JMenu mnuFile;
		JMenuItem menuItem;
		mnuFile = new JMenu("Ablage");
		mnuFile.setMnemonic(KeyEvent.VK_A);
		menuBar.add(mnuFile);

		// a group of JMenuItems
		menuItem = new JMenuItem("Bild-Datei öffnen ...", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Stellt einen Dialog zum Auswahl einer Bilddatei dar");
		menuItem.addActionListener(this);
		menuItem.setName("mnuOpenImage");
		mnuFile.add(menuItem);

		menuItem = new JMenuItem("PDF-Datei öffnen ...", KeyEvent.VK_I);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.META_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Stellt einen Dialog zum Auswahl einer PDF-Datei dar");
		menuItem.addActionListener(this);
		menuItem.setName(mnuOpenPdf);
		mnuFile.add(menuItem);

		return menuBar;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		JFrame mainWindow = new JFrame("Pattern Map");
		JScrollPane scrollPane = new JScrollPane();
		JPanel panel = new JPanel();
		JPanel pnSettings = new JPanel();
		
		panel.add(scrollPane);
		
		mainWindow.getContentPane().add(pnSettings, BorderLayout.WEST);
		pnSettings.setLayout(new BoxLayout(pnSettings, BoxLayout.Y_AXIS));
		
		final Preferences prefs = Preferences.userNodeForPackage(Gui.class);
		final String pnRotateLeft = "rotateLeft";
		boolean doRot = prefs.getBoolean(pnRotateLeft, false);
		
		final JCheckBox cbxRotateLeft = new JCheckBox("Rotate images left");
		cbxRotateLeft.setHorizontalAlignment(SwingConstants.LEFT);
		cbxRotateLeft.setForeground(new Color(0, 0, 0));
		cbxRotateLeft.setSelected(doRot);
		cbxRotateLeft.setVerticalAlignment(SwingConstants.TOP);
		cbxRotateLeft.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				prefs.putBoolean(pnRotateLeft, cbxRotateLeft.isSelected());
			}
		});
		pnSettings.add(cbxRotateLeft);

		mainWindow.getContentPane().add(panel, BorderLayout.EAST);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.CENTER);
		flowLayout.setVgap(240);
		flowLayout.setHgap(320);

		mainWindow.setJMenuBar(this.createMenu());
		//GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//Rectangle bounds = ge.getMaximumWindowBounds();
		mainWindow.setSize(new Dimension(640, 480));
	    final Dimension d = mainWindow.getToolkit().getScreenSize();
	    mainWindow.setLocation((int) ((d.getWidth() - mainWindow.getWidth()) / 2), (int) ((d.getHeight() - mainWindow.getHeight()) / 2));
	    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    mainWindow.setLocationByPlatform(true);
		
		mainWindow.setVisible(true);
	}

}