package fast3d.fragment;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import fast3d.Renderable;
import fast3d.complex.Universe;
import fast3d.complex.light.Illuminatable;
import fast3d.complex.light.Light;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;

/**
 * a fragment shader is a per pixel shader <br>
 * for every pixel in the image (-part) to render it processes once defining
 * it's color <br>
 * the render process for the image (-part) is in a separate thread to
 * accelerate the entire render process by split it up into multiple parallel
 * tasks<br>
 * a subclasses has to implement all parts of the render process by overriding
 * the shadeFragment() methods and various others<br>
 * it is requested to hold the following order:<br>
 * <ol>
 * <li>shadeFragment(int,int) : arguments are the screen x,y position of the
 * pixel to process
 * <li>shadeFragment(double,double) : arguments are the screen x/width and
 * y/height ratios of the pixel to process
 * <li>shadeFragment(Vector3d,Vector3d) : arguments are the start and direction
 * vector of the ray tracing all renderables of the universe
 * <li>shadeFragment(Hashtable_Renderable_Vector3d) : argument is the collection
 * of all traced renderables by the ray of the previous method
 * <li>shadeFragment(Renderable,Vector3d) : arguments are the
 * closest-to-the-camera renderable (defined over his puncture-point with the
 * ray)
 * <li>calculateCompleteIllumination(Illuminatable,Vector3d) : arguments are the
 * renderable that is illuminatable and its puncture point with the ray
 * <li>calculateIllumination(Illuminatable,Vector3d,Light) : arguments as prior
 * and the light to calculate the illumination
 * </ol>
 * and findClosestToTheCam(Hashtable_Renderable_Vector3d) : searches the
 * hashtable for the renderable thats puncture-point is the nearest one to the
 * universes camera position
 * <p>
 * 
 * @author Tim Trense
 */
public abstract class FragmentShader extends Thread {

	private final Universe uni;
	private final BufferedImage img;
	private final Rectangle rect;
	private int shadedPixels;
	private final int pixelCount;

	/**
	 * constructs a new fragment shader
	 * 
	 * @param uni
	 *            the universe to shade
	 * @param img
	 *            the image where to shade the universe to
	 * @param area
	 *            the area of the image to shade
	 */
	public FragmentShader(final Universe uni, final BufferedImage img,
			final Rectangle area) {
		super();
		this.uni = uni;
		this.img = img;
		this.rect = area;
		this.pixelCount = rect.width * rect.height;
		this.shadedPixels = 0;
	}

	/**
	 * calls this constructor with the rectangle area being the entire image
	 * 
	 * @param uni
	 *            the universe to shade
	 * @param img
	 *            the image where to shade the universe to
	 */
	public FragmentShader(final Universe uni, final BufferedImage img) {
		this(uni, img, new Rectangle(0, 0, img.getWidth(), img.getHeight()));
	}

	@Override
	public final void run() {
		for (int y = rect.y; y < img.getHeight() && y < rect.y + rect.height; y++)
			for (int x = rect.x; x < img.getWidth() && x < rect.x + rect.width; x++) {
				final Color pixel = shadeFragment(x, y);
				if (pixel != null)
					img.setRGB(x, y, pixel.getRGB());
				shadedPixels++;
			}
	}

	/**
	 * converts the x,y-pixel-position to their ratios and calls
	 * shadeFragment(x_ratio,y_ratio) and returns the result
	 * 
	 * @param x
	 *            the x-coordinate of the pixel to process
	 * @param y
	 *            the y-coordinate of the pixel to process
	 * @return the color of the pixel
	 */
	protected abstract Color shadeFragment(final int x, final int y);

	/**
	 * converts the x,y-rational-pixel-position to a ray and calls
	 * shadeFragment(rayStart,rayDir) and returns the result
	 * 
	 * @param x
	 *            the rational x-coordinate of the pixel to process
	 * @param y
	 *            the rational y-coordinate of the pixel to process
	 * @return the color of the pixel
	 */
	protected abstract Color shadeFragment(final double xratio, final double yratio);

	/**
	 * uses the given ray to rayTrace all renderables of the universe and call
	 * shadeFragment(Hashtable) and returns the result
	 * 
	 * @param rayStart
	 *            the origin of the ray to pass to universe.rayTrace()
	 * @param rayDirection
	 *            the direction of the ray to pass to universe.rayTrace()
	 * @return the color of the pixel
	 */
	protected abstract Color shadeFragment(final Vector3d rayStart,
			final Vector3d rayDirection);

	/**
	 * calls findClosestToTheCam(argument0) and passes the result to
	 * shadeFragment(closestRenderable,puncture-point) and returns the result
	 * 
	 * @param traced
	 *            all renderables traced by the universe
	 * 
	 * @return the color of the pixel
	 */
	protected abstract Color shadeFragment(final Hashtable<Renderable, Vector3d> traced);

	/**
	 * checks whether lighting calculation needs to be done (if so calls
	 * calculateCompleteIllumination with casted argument0) and returns the
	 * result, otherwise returns the color of argument0
	 * 
	 * @param closestTraced
	 *            the renderable that is displayed at the pixel currently
	 *            processed
	 * @param puncture
	 *            the puncture-point between the renderable and the ray over the
	 *            currently processed pixel
	 * @return the color of the pixel
	 */
	protected abstract Color shadeFragment(final Renderable closestTraced,
			final Vector3d puncture);

	/**
	 * does the lighting calculation for all light-sources in the universe using
	 * calculateIllumination() and combines them to the visible color of the
	 * pixel and returns that<br>
	 * if no light is used return value should be black
	 * 
	 * @param closest
	 *            the illuminatable to calculate the visible color for on the
	 *            given point
	 * @param puncture
	 *            the point where on the illuminatable the visible color is
	 *            calculated for
	 * @return the color of the pixel
	 */
	protected abstract Color calculateCompleteIllumination(final Illuminatable closest,
			final Vector3d puncture);

	/**
	 * does the lighting calculation (and shadowing calculation! if the light
	 * source is obstructable) with the given light-source and returns the
	 * visible color if only that light source would be used
	 * 
	 * @param l
	 *            the illuminatable to be lightened
	 * @param puncture
	 *            the point of the illuminatable that is currently processed (to
	 *            be considered in the shadowing calculation if needed)
	 * @param light
	 *            the used light source
	 * @return the color of the pixel
	 */
	protected abstract Color calculateIllumination(final Illuminatable l,
			final Vector3d puncture, final Light light);

	/**
	 * iterates over all entries of the hashtable and detects the one where the
	 * vector-value is closest to the camera and returns that
	 * 
	 * @param punctures
	 *            the rayTracing result of the universe for a specified ray over
	 *            the currently processed pixel
	 * @return the most-front renderable (the one the viewer of the image should
	 *         be supposed to see)
	 */
	protected abstract Renderable findClosestToTheCam(
			final Hashtable<Renderable, Vector3d> punctures);

	/**
	 * @return the count of already processed pixels
	 */
	public final int getShadedPixelCount() {
		return shadedPixels;
	}

	/**
	 * @return the ratio of shaded pixels per the given rectangles entire pixel
	 *         count
	 */
	public double getProgress() {
		return ((double) getShadedPixelCount()) / pixelCount;
	}

	/**
	 * @return the universe to render
	 */
	protected Universe getUniverse() {
		return uni;
	}

	/**
	 * @return the image to render on
	 */
	protected BufferedImage getImage() {
		return img;
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given FragmentShader are equal considering
	 *         their universe to render, image to render on and rectangle of the
	 *         image to be rendered
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof FragmentShader) {
			final FragmentShader other = (FragmentShader) obj;
			return uni.equals(other.uni) && img.equals(other.img)
					&& rect.equals(other.rect);
		} else
			return false;
	}

	/**
	 * 
	 * @return fast3d.fragment.FragmentShader[_parameter_]
	 **/
	public String toString() {
		return "fast3d.fragment.FragmentShader[" + "universe=" + uni + ";image=" + img
				+ ";imageArea=" + rect + ";progress=" + getProgress() + "]";
	}
}