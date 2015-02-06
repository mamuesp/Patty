/**
 * Copyright (C) 1995-2013 levigo holding gmbh.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.levigo.jbig2.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.levigo.jbig2.Bitmap;
import com.levigo.jbig2.image.Bitmaps;

/**
 * This class is for debug purpose only. The {@code DictionaryViewer} is able to
 * show a single bitmap or all symbol bitmaps.
 * 
 * @author <a href="mailto:m.krzikalla@levigo.de">Matthäus Krzikalla</a>
 * @author Benjamin Zindel
 */
public class DictionaryViewer {

	public static void save (Bitmap b, String fileName) {
		try {
			// retrieve image
			BufferedImage bi = Bitmaps.asBufferedImage(b);
			File outputfile = new File(fileName);
			ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {
			// do nothing ...
		}
	}
	
	public static void show(List<Bitmap> symbols) {
		int width = 0;
		int height = 0;

		for (Bitmap b : symbols) {
			width += b.getWidth();

			if (b.getHeight() > height) {
				height = b.getHeight();
			}
		}

		Bitmap result = new Bitmap(width, height);

		int xOffset = 0;

		for (Bitmap b : symbols) {
			Bitmaps.blit(b, result, xOffset, 0, CombinationOperator.REPLACE);
			xOffset += b.getWidth();
		}
		try {
			// retrieve image
			BufferedImage bi = Bitmaps.asBufferedImage(result);
			File outputfile = new File("/Users/mmspaeth/manglednumbers/symbols.png");
			ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {
			// nischde
		}
	}
}