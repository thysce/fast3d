package fast3d.math.geom;

import fast3d.math.Vector3d;

/**
 * representing a geometric (infinitely long) straight line<br>
 * a mathematical straight has no end or begin and no thickness and has no other
 * properties than it's position and orientation
 * 
 * @author Tim Trense
 */
public class Straight implements GeometricObject {

	private final Vector3d base;
	private final Vector3d dir;

	/**
	 * constructs a new straight, both parameters must be finite
	 * 
	 * @param s
	 *            one point on the straight
	 * @param d
	 *            the direction-vector of the straight (must not be zero)
	 */
	public Straight(final Vector3d s, final Vector3d d) {
		super();
		this.base = s;
		this.dir = d;
	}

	/**
	 * @return a reference to the starting point of the straight
	 */
	public Vector3d getBase() {
		return base;
	}

	/**
	 * @return a reference to the direction-vector
	 */
	public Vector3d getDirection() {
		return dir;
	}

	/**
	 * checks whether the point is on the straight
	 * 
	 * @param p
	 *            the point to check for incidence
	 * @return true if the relationship between this and the point is incidence
	 */
	public boolean contains(final Vector3d p) {
		final Vector3d ap = getBase().to(p);
		return ap.isParallel(getDirection());
	}

	/**
	 * calculates the nadir point of the given point to this<br>
	 * the nadir point is that point on the straight that is shortest from the
	 * given point<br>
	 * 
	 * @param point
	 *            the point to calculate the nadir point from
	 * @return the nadir point on this line from the given point
	 */
	public Vector3d nadirPoint(final Vector3d point) {
		final Vector3d hypothenuse = getBase().to(point);
		final Vector3d adjacent = getDirection().clone();
		final double angle = adjacent.angleTo(hypothenuse);
		final double lengthAdj = Math.cos(angle)
				* hypothenuse.length();
		return getBase().clone().add(adjacent.scaleTo(lengthAdj));
	}

	/**
	 * @return an independent straight with cloned base and direction
	 */
	@Override
	public Straight clone() {
		return new Straight(getBase().clone(),
				getDirection().clone());
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given Straight are equal considering their
	 *         base and direction
	 **/
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Straight) {
			final Straight other = (Straight) obj;
			return getBase().equals(other.getBase())
					&& getDirection().equals(other.getDirection());
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Straight[base=" + getBase() + ";dir=" + getDirection()
				+ "]";
	}
}