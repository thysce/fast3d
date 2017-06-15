package fast3d.renderables;

import fast3d.Renderable;
import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.Vector3d;
import fast3d.math.geom.Point;

/**
 * visualized like a 3d-pixel (a point with a width of 1px and a color)<br>
 * no matter how far the camera is away, a pixel will always be shaded to the
 * size of one single pixel on the screen<br>
 * points/pixels disappear on mathematically correct ray-tracing imaging, because they
 * have no actual width (like when using the DefaultFragmentShader)
 * 
 * @author Tim Trense
 */
public class Pixel extends Point implements Renderable {

	/**
	 * determines the default value for the rayIntersectionZeroLockControl on
	 * instancing a line
	 */
	public static double defaultRayIntersectionZeroLockControl = 0.0001;
	/**
	 * on ray-intersection-computing (ray tracing) it is normally impossible to
	 * target a mathematical point (with actual no width) using rastered rays
	 * like when displaying an universe to any computer screen
	 */
	public double rayIntersectionZeroLockControl = defaultRayIntersectionZeroLockControl;
	private final Color color;

	/**
	 * constructs a new 3d-pixel on the specified location and with the
	 * specified color<br>
	 * both can be changed directly by the references given by getPos() and
	 * getColor()
	 * 
	 * @param pos
	 *            the pixels position in space
	 * @param col
	 *            the pixels color
	 */
	public Pixel(final Vector3d pos, final Color col) {
		super(pos);
		this.color = col;
	}

	@Override
	public void shade(Graphics3d s) {
		s.shade(getPos());
	}
	
	@Override
	public void render(final Graphics3d g) {
		g.setColor(color);
		g.pixel(getPos());
	}

	/**
	 * the pixels color (rgb) might be changed by the (xyz)-coordinates of the
	 * casted vector3d
	 * 
	 * @return a reference to the pixels color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * adds the given vector to the position
	 * 
	 * @param dir
	 *            the translation vector
	 */
	public void moveInAbsoluteDirection(final Vector3d dir) {
		getPos().add(dir);
	}
	
	/**
	 * moves the pixel so that it will be at the target
	 * 
	 * @param target
	 *            the new position of this point
	 */
	public void moveTo(final Vector3d target) {
		getPos().set(target);
	}

	/**
	 * @return an independent version of this pixel, with cloned position and
	 *         color
	 */
	@Override
	public Pixel clone() {
		return new Pixel(getPos().clone(), color.clone());
	}

	/**
	 * @return true if the given pixel has the same position and color
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Pixel) {
			final Pixel p = (Pixel) obj;
			return p.getPos().equals(this.getPos()) && p.color.equals(this.color);
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Pixel[pos=" + getPos() + ";color=" + color + "]";
	}

	@Override
	public Vector3d rayTrace(final Vector3d s, final Vector3d r) {
		final Vector3d spos = s.to(getPos());
		if (spos.angleTo(r) < rayIntersectionZeroLockControl)
			return getPos().clone();
		else
			return null;
	}

	@Override
	public Vector3d[] getVertices() {
		return new Vector3d[] { getPos() };
	}
	
	/**
	 * @return the geometric object point
	 */
	public Point getPoint(){
		return new Point(this.getPos().clone());
	}
}