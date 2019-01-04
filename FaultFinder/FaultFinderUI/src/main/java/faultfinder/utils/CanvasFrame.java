/**
 * 
 */
package faultfinder.utils;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * @author m.c.kunkel
 *
 */
public class CanvasFrame extends org.bytedeco.javacv.CanvasFrame {
	private Java2DFrameConverter converter = new Java2DFrameConverter();
	private Color color = null;
	private Image image = null;

	/**
	 * @param title
	 */
	public CanvasFrame(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	// Java2D will do gamma correction for TYPE_CUSTOM BufferedImage, but
	// not for the standard types, so we need to do it manually.
	public void setImage(Frame image) {
		setImage(image, false);
	}

	public void setImage(Frame image, boolean flipChannels) {
		setImage(converter.getBufferedImage(image,
				converter.getBufferedImageType(image) == BufferedImage.TYPE_CUSTOM ? 1.0 : inverseGamma, flipChannels,
				null));
	}

	public void setImage(Image image) {
		if (image == null) {
			return;
		} else if (isResizable() && needInitialResize) {
			int w = (int) Math.round(image.getWidth(null) * initialScale);
			int h = (int) Math.round(image.getHeight(null) * initialScale);
			setCanvasSize(w, h);
		}
		this.color = null;
		this.image = image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bytedeco.javacv.CanvasFrame#getCanvas()
	 */
	@Override
	public Canvas getCanvas() {
		super.canvas.paint(null);
		return canvas;
	}

}
