/**
 * 
 */
package utils;

import java.awt.Image;
import java.awt.image.BufferedImage;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * @author m.c.kunkel
 *
 */
public class ImageUtils {
	private Java2DFrameConverter converter = new Java2DFrameConverter();

	public Image getImage(Frame image) {
		return converter.getBufferedImage(image,
				converter.getBufferedImageType(image) == BufferedImage.TYPE_CUSTOM ? 1.0 : 1.0, false, null);
	}

}
