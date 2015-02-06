package com.muellerspaeth.patty;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfIndirectReference;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.levigo.jbig2.JBIG2Globals;
import com.levigo.jbig2.JBIG2ImageReader;
import com.levigo.jbig2.JBIG2ImageReaderSpi;

public class ImageExtractor {
	
	private File currentFile = null;
	private ImageExplorer expImages = null;
	
	public  ImageExtractor(File pdfFile, ImageExplorer explorer) throws IOException {
		currentFile = pdfFile;
		this.expImages = explorer;
		int page=1;
        PdfReader reader = new PdfReader(pdfFile.getAbsolutePath());
        for (page = 1; page <= reader.getNumberOfPages(); page++) {
	        PdfDictionary resources = reader.getPageResources(page);
	        PdfDictionary xobjects = null;
			if (resources != null) {
				xobjects = resources.getAsDict(PdfName.XOBJECT);
			}
			if (resources == null || xobjects == null) {
				continue;
			}
	        byte[] globalBytes = null;
			for (PdfName key : xobjects.getKeys()) {
				// The heading "\" of the key must be dropped
				String objectid = key.toString().substring(1);
				PdfIndirectReference objRef = xobjects.getAsIndirectObject(new PdfName(objectid));
				PdfImageObject img = null;
				PRStream stream = null;
				if (objRef == null) {
					throw new NullPointerException("Reference " + objectid + " not found - Available keys are " + xobjects.getKeys());
				} else {
					stream = (PRStream) PdfReader.getPdfObject(objRef);
					try {
						img = new PdfImageObject(stream);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (img != null) {
					PdfDictionary decodeParams = stream.getAsDict(PdfName.DECODEPARMS);
		            if(decodeParams != null) {
		            	PdfIndirectReference globalRef = decodeParams.getAsIndirectObject(PdfName.JBIG2GLOBALS);
		                if (globalRef != null) {
		                	PRStream globalStream = (PRStream) PdfReader.getPdfObject(globalRef);
		                    globalBytes = PdfReader.getStreamBytesRaw(globalStream);
		                }
		            }
	
		            byte[] imageAsBytes = img.getImageAsBytes();
					BufferedImage bufferedImage = null;;
					bufferedImage = this.getBufferedImage(imageAsBytes, img.getFileType(), globalBytes, key, page);
					if (bufferedImage ==  null)  {
						System.out.println("Can't read image " + objectid);
					} else {
						System.out.println("Image key: " + objectid + " size: " + imageAsBytes.length);
					}
				}
			}
        }
        explorer.setImages();
	}

	private BufferedImage getBufferedImage(byte[] binary, String fileType, byte[] globalBytes, PdfName imgKey, int pageId) {
		//List<BufferedImage> images = new List<BufferedImage>();
		BufferedImage resultImage = null;
		BufferedImage image = null;
		ImageInputStream is = null;
		ImageInputStream gs = null;
		try {
			is = ImageIO.createImageInputStream(new ByteArrayInputStream(binary));
			gs = (globalBytes != null) ? ImageIO.createImageInputStream(new ByteArrayInputStream(globalBytes)) : null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.out.println("Trying to load image of type "+ fileType);
			if (fileType == "jbig2") { 
				HashMap<Number, ArrayList<Object>> symbols = new HashMap<Number, ArrayList<Object>>();
				JBIG2ImageReader reader = new JBIG2ImageReader(new JBIG2ImageReaderSpi());
				reader.setInput(is);
				if (gs != null) {
					JBIG2Globals globs = reader.processGlobals(gs);
					reader.setGlobals(globs);
				}
				image = reader.read(0, null, symbols);

				String imgFileName = changeExtension(currentFile.getAbsolutePath(), "-" + pageId + "-" + PdfName.decodeName(imgKey.toString()) + ".png");
				new ImageTool(image, expImages, symbols, imgFileName);
			} else {
				Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(fileType);
				while (readers.hasNext() && image == null) {
					ImageReader reader = readers.next();
					reader.setInput(is);
					resultImage = reader.read(0);
					new ImageTool(resultImage, expImages);
				}
			}
		} catch (IOException e) {
			System.out.println("Method ImageExtractor.getBufferedImage can't load image of type "+ fileType);
			e.printStackTrace();
			image=null;
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (image == null) {
			String[] supportedTypes = ImageIO.getReaderFileSuffixes();
			System.out.println("Supported suffixes:");
			for (int i=0; i< supportedTypes.length; i++) {
				System.out.println("\t"+supportedTypes[i]);	
			}
		}
		return resultImage;
	}
	
    public static PdfReader getResourceAsPdfReader(Object context, String resourceName) throws IOException{
    	BufferedInputStream bis = new BufferedInputStream(getResourceAsStream(context, resourceName));
    	return new PdfReader(bis);
    }
    
    public static InputStream getResourceAsStream(Object context, String resourceName){
        Class<?> contextClass;
        if (context instanceof Class<?>){
            contextClass = (Class<?>)context;
        }else{
            contextClass = context.getClass();
        }
        return contextClass.getClassLoader().getResourceAsStream(getFullyQualifiedResourceName(contextClass, resourceName));
    }
    
    public static String getFullyQualifiedResourceName(Class<?> context, String resourceName){
        return context.getName().replace('.', '/') + "/" + resourceName;
    }
    
    public static String changeExtension(String originalName, String newExtension) {
        int lastDot = originalName.lastIndexOf(".");
        if (lastDot != -1) {
            return originalName.substring(0, lastDot) + newExtension;
        } else {
            return originalName + newExtension;
        }
    }
}
