package com.muellerspaeth.patty;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class ImageExplorer extends JFrame {
	
	private static final long serialVersionUID = 7227565858401026073L;
	
	JPanel pnBack = new JPanel();
	JPanel panelLeft = new JPanel();
	JPanel panelRight = new JPanel();
	JSplitPane spExplorer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLeft, panelRight);
	ArrayList<BufferedImage> imgThumbs = new ArrayList<BufferedImage>();
	ImageList thumbs = null;
	
	public ImageExplorer(String title) {
		super(title);
		thumbs = new ImageList();
		this.add(thumbs);
		this.setLocationRelativeTo(null);
	}
	
	public void setVisible(boolean show) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = ge.getMaximumWindowBounds();
		setSize(new Dimension(bounds.width/2, bounds.height/2));
	    final Dimension d = this.getToolkit().getScreenSize();
	    this.setLocation((int) ((d.getWidth() - this.getWidth()) / 2), (int) ((d.getHeight() - this.getHeight()) / 2));
		//this.pack();
	    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationByPlatform(true);
		super.setVisible(show);
	}
	
	public void putImage(ImageTool imgIn) {
		imgThumbs.add(imgIn.getDisplayImage());
	}

	public void setImages() {
		thumbs.loadImages(imgThumbs);
	}

	public void unsetImages() {
		thumbs.removeImages();
	}
}