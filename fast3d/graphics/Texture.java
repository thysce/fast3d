package fast3d.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import fast3d.math.Vector2d;
import fast3d.util.ColorGen;
import fast3d.util.math.MathUtil;

/**
 * used to store a texture for a TextureTriangle
 * 
 * @author Tim Trense
 */
public class Texture {

	private final Color[][] pixels;
	private final int width, height;

	/**
	 * constructs a new texture that holds the ARGB-data of the parameter
	 * 
	 * @param img
	 *            the texture-image to parse
	 */
	public Texture(final BufferedImage img) {
		width = img.getWidth();
		height = img.getHeight();
		pixels = new Color[width][height];
		for (int x = 0; x < pixels.length; x++)
			for (int y = 0; y < pixels[x].length; y++)
				pixels[x][y] = ColorGen.from255RGBA(img.getRGB(x, y));
	}

	/**
	 * @return the width of the texture
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height of the texture
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return a vector2d like (width;height)
	 */
	public Vector2d getDimension() {
		return new Vector2d(width, height);
	}

	/**
	 * gives the color for a given pixel on the texture<br>
	 * the coordinates are within 0..width or 0..height (overflow is managed
	 * automatically)
	 * 
	 * @param coord
	 *            the pixel-coordinates
	 * @return the argb pixel color (cloned)
	 */
	public Color getAbsolute(final Vector2d coord) {
		final double x = MathUtil.overflow(coord.x, 0, width);
		final double y = MathUtil.overflow(coord.y, 0, height);
		return pixels[(int) (x)][(int) (y)].clone();
	}

	/**
	 * gives the color for a given pixel on the texture<br>
	 * the coordinates are within 0..1 (overflow is managed automatically)
	 * 
	 * @param coord
	 *            the logical pixel-coordinates
	 * @return the argb pixel color (cloned)
	 */
	public Color getLogical(Vector2d coord) {
		coord = coord.clone();
		toAbsoluteCoordinate(coord);
		return getAbsolute(coord);
	}

	/**
	 * converts the given logical coordinate (max = 1) to the corresponding
	 * absolute coordinate on this texture (max = width or height)
	 * 
	 * @param logicalCoordinate
	 *            the coordinate to convert
	 */
	public void toAbsoluteCoordinate(
			final Vector2d logicalCoordinate) {
		logicalCoordinate.multiply(getDimension());
	}

	/**
	 * converts the given absolute coordinate on this texture (max = width or
	 * height) to the corresponding logical coordinate (max = 1)
	 * 
	 * @param absCoordinate
	 *            the coordinate to convert
	 */
	public void toLogicalCoordinate(final Vector2d absCoordinate) {
		final Vector2d dim = getDimension();
		absCoordinate.x /= dim.x;
		absCoordinate.y /= dim.y;
	}

	/**
	 * loads a Texture from the given file using ImageIO.read
	 * 
	 * @param filename
	 *            the full name of the file to read
	 * @return the texture stored in the file
	 */
	public static Texture load(final String filename) {
		return load(new File(filename));
	}

	/**
	 * loads a Texture from the given file using ImageIO.read
	 * 
	 * @param file
	 *            the file to read
	 * @return the texture stored in the file
	 */
	public static Texture load(final File file) {
		try {
			final BufferedImage img = ImageIO.read(file);
			if (img != null)
				return new Texture(img);
			else
				return null;
		} catch (IOException e) {
			return null;
		}
	}
}
