package com.muellerspaeth.patty;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.*;

import net.coobird.thumbnailator.Thumbnailator;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ImageList extends JPanel {

	private static final long serialVersionUID = -4175421500021934562L;

//	private final Preferences prefs = Preferences.userNodeForPackage(Gui.class);
//	private final String pnBorder = "borderSize";
	
	DefaultListModel model;

	public ImageList() {
		super(new GridLayout());
		
		final JPanel imageViewContainer = new JPanel(new GridBagLayout());
		final JLabel imageView = new JLabel();
		imageViewContainer.add(imageView);

		model = new DefaultListModel();
		final JList imageList = new JList(model);
		imageList.setCellRenderer(new IconCellRenderer());
		ListSelectionListener listener = new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent lse) {
				Object imgCurr = imageList.getSelectedValue();
				if (imgCurr instanceof BufferedImage) {
					BufferedImage imgView = Thumbnailator.createThumbnail((BufferedImage) imgCurr, imageViewContainer.getWidth(), imageViewContainer.getHeight());
					imageView.setIcon(new ImageIcon(imgView));
				}
			}
		};
		imageList.addListSelectionListener(listener);

		this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(
				imageList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), new JScrollPane(
				imageViewContainer)));
	}

	public void loadImages(ArrayList images) {
		removeImages();
		for (int ii = 0; ii < images.size(); ii++) {
			model.addElement(images.get(ii));
		}
	}

	public void removeImages() {
		model.removeAllElements();
	}
	/*
	 * public static void main(String[] args) { SwingUtilities.invokeLater(new
	 * Runnable(){
	 * 
	 * @Override public void run() { ImageList imageList = new ImageList();
	 * 
	 * JFrame f = new JFrame("Image Browser");
	 * f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); f.add(imageList);
	 * f.setLocationByPlatform(true); f.pack(); f.setSize(800,600);
	 * f.setVisible(true); } }); }
	 */
}

class IconCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	private int size;
	private BufferedImage icon;

	public IconCellRenderer() {
		this(166);
	}

	public IconCellRenderer(int size) {
		this.size = size;
		icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component rndComp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (rndComp instanceof JLabel && value instanceof BufferedImage) {
			JLabel lblThumb = (JLabel) rndComp;
			lblThumb.setText("");
			BufferedImage img = (BufferedImage) value;
			lblThumb.setIcon(new ImageIcon(icon));
			
			int border = 8;
			Graphics2D g = icon.createGraphics();
			g.setColor(new Color(0, 0, 0 , 0));
			g.clearRect(0, 0, size, size);
			g.drawImage(img, border, border, size - 2*border, size - 2*border, this);
			g.dispose();
		}
		return rndComp;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size, size);
	}
}