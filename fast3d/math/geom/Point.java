package fast3d.math.geom;

import fast3d.math.Vector3d;

/**
 * representing a geometric point (a position in 3d-space)<br>
 * a point has no real width or diameter so it is infinitely small - also it has
 * no other properties than it's position (e.g. no color)
 * 
 * @author Tim Trense
 *
 */
public class Point implements GeometricObject {

	private final Vector3d pos;

	/**
	 * constructs a new point on the given location
	 * 
	 * @param p
	 *            the points location, must be finite
	 */
	public Point(final Vector3d p) {
		this.pos = p;
	}

	/**
	 * constructs a new point on the given location
	 * 
	 * @param x
	 *            the points x-location
	 * @param y
	 *            the points y-location
	 * @param z
	 *            the points z-location
	 */
	public Point(final double x, final double y, final double z) {
		this.pos = new Vector3d(x, y, z);
	}

	/**
	 * @return a reference to the points location (modifiable)
	 */
	public Vector3d getPos() {
		return pos;
	}

	/**
	 * @return an independent version of this, with cloned position
	 */
	@Override
	public Point clone() {
		return new Point(pos.clone());
	}

	@Override
	public String toString() {
		return "Point[" + pos + "]";
	}
}