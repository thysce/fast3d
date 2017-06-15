package fast3d.simple.fragment;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import fast3d.simple.SimpleUniverse;

/**
 * an implementation of DefaultFragmentShader using the SimpleUniverse
 * 
 * @author Tim Trense
 */
public class SimpleFragmentShader extends DefaultFragmentShader {

	/**
	 * calls super constructor
	 * 
	 * @param uni
	 *            the universe to shade
	 * @param img
	 *            the image where to shade the universe to
	 * @param area
	 *            the area of the image to shade
	 */
	public SimpleFragmentShader(final SimpleUniverse uni, final BufferedImage img,
			final Rectangle area) {
		super(uni, img, area);
	}

	/**
	 * calls super constructor
	 * 
	 * @param uni
	 *            the universe to shade
	 * @param img
	 *            the image where to shade the universe to
	 */
	public SimpleFragmentShader(final SimpleUniverse uni, final BufferedImage img) {
		super(uni, img);
	}

	/**
	 * calls super constructor with the a plain image in the specified size of
	 * type BufferedImage.TYPE_INT_RGB and the rectangle area being the entire
	 * image
	 * 
	 * @param uni
	 *            the universe to shade
	 * @param imagewidth
	 *            the width of the image to shade to
	 * @param imageheight
	 *            the height of the image to shade to
	 */
	public SimpleFragmentShader(final SimpleUniverse uni, final int imagewidth,
			final int imageheight) {
		super(uni,
				new BufferedImage(imagewidth, imageheight, BufferedImage.TYPE_INT_RGB));
	}

	@Override
	public SimpleUniverse getUniverse() {
		return (SimpleUniverse) super.getUniverse();
	}
}