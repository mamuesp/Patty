package com.muellerspaeth.patty;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.imageio.*;

import com.levigo.jbig2.Bitmap;
import com.levigo.jbig2.segments.RegionSegmentInformation;

public class ImageTool extends Component {
          
	private static final long serialVersionUID = -547984814146631118L;

	private final Preferences prefs = Preferences.userNodeForPackage(Gui.class);
	private final String pnRotateLeft = "rotateLeft";
	
	private BufferedImage imgRaw;
	private BufferedImage imgDisplay;
	private ImageExplorer imgExp;
	private HashMap<Number, ArrayList<Object>> symbols;
	private boolean doRotateLeft = false;
	
	public ImageTool(BufferedImage imgIn, ImageExplorer imgExp) {
    	this.imgRaw = imgIn;
    	this.imgDisplay = imgIn;
    	this.imgExp = imgExp;
        this.doRotateLeft = prefs.getBoolean(pnRotateLeft, false);
        if (this.doRotateLeft) {
        	this.rotate(-90);
        }
        this.imgExp.putImage(this);
     }

	public ImageTool(File imgFile, ImageExplorer imgExp) {
       try {
           this.imgRaw = ImageIO.read(imgFile);
           this.imgDisplay = imgRaw;
           this.imgExp = imgExp;
           this.doRotateLeft = prefs.getBoolean(pnRotateLeft, false);
           if (this.doRotateLeft) {
           	this.rotate(-90);
           }
           this.imgExp.putImage(this);
       } catch (IOException e) {
       }
    }

	public ImageTool(BufferedImage imgIn, ImageExplorer imgExp, HashMap<Number, ArrayList<Object>> symbols, String imgFileName) {
    	this.imgRaw = imgIn;
    	this.imgDisplay = imgIn;
    	this.symbols = symbols;
    	this.imgExp = imgExp;
        this.doRotateLeft = prefs.getBoolean(pnRotateLeft, false);
        this.markSymbols();
        if (this.doRotateLeft) {
        	this.rotate(-90);
        }
        this.imgExp.putImage(this);
        this.writeImage(imgFileName);
	}
	   
	public void rotate(int angle) {
		if (angle != 0) {
			BufferedImage imgIn = this.imgDisplay;
			BufferedImage bi = new BufferedImage(imgIn.getWidth(),
					imgIn.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			// Rotation information
			double rotationRequired = Math.toRadians(angle);
			double locationX = imgIn.getWidth() / 2;
			double locationY = imgIn.getHeight() / 2;
			AffineTransform tx = AffineTransform.getRotateInstance(
					rotationRequired, locationX, locationY);
			AffineTransformOp op = new AffineTransformOp(tx,
					AffineTransformOp.TYPE_BILINEAR);
			// Drawing the rotated image at the required drawing locations
			g2d.drawImage(op.filter(imgIn, null), 0, 0, null);
			g2d.dispose();
			this.imgDisplay = bi;
		}
	}

	@SuppressWarnings("unchecked")
	public void markSymbols() {
		BufferedImage resultImage = new BufferedImage(this.imgRaw.getWidth(), this.imgRaw.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = resultImage.createGraphics();
		g.drawRenderedImage(this.imgRaw, null);
		g.dispose();

		for (Number key : symbols.keySet()) {
			ArrayList<Object> symbolData = symbols.get(key);
			for (int i = 0; i < symbolData.size() - 2; i +=3) {
				ArrayList<Bitmap> sym = (ArrayList<Bitmap>) symbolData.get(i);
				HashMap<Number, ArrayList<Point>> allPos = (HashMap<Number, ArrayList<Point>>) symbolData.get(i+1);
				RegionSegmentInformation regionInfo = (RegionSegmentInformation) symbolData.get(i+2);
				Point offset = new Point(regionInfo.getXLocation(), regionInfo.getYLocation());
				for (Number bmpId : allPos.keySet()) {
					ArrayList<Point> posList = (ArrayList<Point>) allPos.get(bmpId);
					Bitmap symbol = sym.get(bmpId.intValue());
					for (Point rctPos : posList) {
			            Graphics2D g2d = (Graphics2D) resultImage.getGraphics();
			            g2d.setColor(new Color(255, 0, 0));
			            g2d.drawRect(rctPos.x + offset.x, rctPos.y + offset.y, symbol.getWidth(), symbol.getHeight());
					}
				}
			}
		} 	
		
		this.imgDisplay =  resultImage;
	}

	public void writeImage(String imgFileName) {

		if (imgFileName != null && imgFileName != "") {
			File outputfile = new File(imgFileName);
			try {
				ImageIO.write(this.imgDisplay, "png", outputfile);
			} catch (Exception e) {
				// TODO: handle exception
				// no exception handling wanted
			}
		}

	}

	public BufferedImage getRawImage() {
		return imgRaw;
	}
	
	public BufferedImage getDisplayImage() {
		return imgDisplay;
	}
}

