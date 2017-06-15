package fast3d.renderables;

import fast3d.Renderable;
import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.MatrixCalculation;
import fast3d.math.Vector3d;
import fast3d.math.geom.Straight;
import fast3d.util.ColorGen;

/**
 * a line in 3d space with width of 1 pixel<br>
 * lines disappear on mathematically correct ray-tracing imaging, because they
 * have no actual width (like when using the DefaultFragmentShader)
 * 
 * @author Tim Trense
 */
public class Line implements Renderable {

	/**
	 * the starting point of the line
	 */
	public final Vector3d a;

	/**
	 * the ending point of the line
	 */
	public final Vector3d b;

	/**
	 * determines the default value for the rayIntersectionZeroLockControl on
	 * instancing a line
	 */
	public static double defaultRayIntersectionZeroLockControl = 0.0001;
	/**
	 * on ray-intersection-computing (ray tracing) it is normally impossible to
	 * target a mathematical line (with actual no width) using rastered rays
	 * like when displaying an universe to any computer screen
	 */
	public double rayIntersectionZeroLockControl = defaultRayIntersectionZeroLockControl;
	private final Color c;

	/**
	 * constructs a line from start to end with the color c
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            end
	 * @param c
	 *            color
	 */
	public Line(final Vector3d a, final Vector3d b, final Color c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 * constructs a line from start to end with a white color
	 * 
	 * @param a
	 *            start
	 * @param b
	 *            end
	 */
	public Line(final Vector3d a, final Vector3d b) {
		this(a, b, ColorGen.WHITE());
	}

	/**
	 * @return a reference to the color of this line3d
	 */
	public Color getColor() {
		return c;
	}

	/**
	 * @return a reference to the first end of the line
	 */
	public Vector3d getStart() {
		return a;
	}

	/**
	 * @return a reference to the second end of the line
	 */
	public Vector3d getEnd() {
		return b;
	}

	/**
	 * @return a vector in the middle between the start/end
	 */
	public Vector3d getMiddle() {
		return getStart().clone()
				.add(getDirection().clone().scale(.5));
	}

	/**
	 * @see #getStart()
	 * @see #getEnd()
	 * @return the difference vector from getStart() to getEnd()
	 */
	public Vector3d getDirection() {
		return a.to(b);
	}

	/**
	 * @return an independent version of this line with cloned start- and
	 *         end-vector and cloned color
	 */
	@Override
	public Line clone() {
		return new Line(getStart().clone(), getEnd().clone(),
				c.clone());
	}

	/**
	 * @return true if the given Line has equal start- and end-vectors (may be
	 *         flipped) and the same color
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Line) {
			final Line l = (Line) obj;
			final Vector3d a = getStart();
			final Vector3d b = getEnd();
			final Vector3d la = l.getStart();
			final Vector3d lb = l.getEnd();

			if (a.equals(la))
				return b.equals(lb) && c.equals(l.c);
			else
				return a.equals(lb) && b.equals(la) && c.equals(l.c);
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Line[from=" + getStart() + ";to=" + getEnd()
				+ ";color=" + c + "]";
	}

	@Override
	public void shade(final Graphics3d s) {
		s.shade(a);
		s.shade(b);
	}

	@Override
	public void render(final Graphics3d g) {
		g.setColor(c);
		g.line(a, b);
	}

	/**
	 * @return getMiddle()
	 */
	@Override
	public Vector3d getPos() {
		return getMiddle();
	}

	/**
	 * @return the distance from start to end
	 */
	public double length() {
		return getStart().distanceTo(getEnd());
	}

	/**
	 * aligns the line along the given vector at constant line-length <br>
	 * could be understood as a rotation around the middle
	 * 
	 * @param dir
	 *            the new direction of the line (dir.length() will be ignored)
	 */
	private void align(Vector3d dir) {
		dir = dir.clone().scaleTo(length() * .5);

		final Vector3d middle = getMiddle();
		final Vector3d newA = (Vector3d) middle.clone().add(dir);
		dir.invert();
		final Vector3d newB = (Vector3d) middle.clone().add(dir);

		getStart().set(newA);
		getEnd().set(newB);
	}

	/**
	 * rotates the line around the constant x axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotX(final double rad) {
		align(getDirection().rotX(rad));
	}

	/**
	 * rotates the line around the constant y axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotY(final double rad) {
		align(getDirection().rotY(rad));
	}

	/**
	 * rotates the line around the constant z axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotZ(final double rad) {
		align(getDirection().rotZ(rad));
	}

	/**
	 * rotates the line around the given axis
	 * 
	 * @param axis
	 *            the axe to rotate around in mathematically positive direction
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rot(final Vector3d axis, final double rad) {
		align(getDirection().rot(axis, rad));
	}

	/**
	 * adds the given vector to the start- and end-vertices
	 * 
	 * @param dir
	 *            the translation vector
	 * 
	 */
	public void moveInAbsoluteDirection(final Vector3d dir) {
		getStart().add(dir);
		getEnd().add(dir);
	}

	/**
	 * the parameter-vector is along the constant x,y,z-axes <br>
	 * the direction of the line will remain constant
	 * 
	 * @param target
	 *            the vector where the new middle will be
	 */
	public void moveTo(final Vector3d target) {
		final Vector3d middle = getMiddle();
		final Vector3d ma = middle.to(getStart());
		final Vector3d mb = middle.to(getEnd());
		getStart().set(target.clone().add(ma));
		getEnd().set(target.clone().add(mb));
	}

	@Override
	public Vector3d rayTrace(final Vector3d s, final Vector3d r) {
		final Vector3d line = getDirection().clone();
		final Vector3d a = getStart();
		final double[][] matrix = new double[][] {
				{ r.x, -line.x, a.x - s.x },
				{ r.y, -line.y, a.y - s.y },
				// z not required because only 2 variables and z-line is check
		};
		final double[] intersect = MatrixCalculation
				.solveLinearEquationSystem(matrix);
		final double dirF = intersect[0];
		final double lineF = intersect[1];
		if (Math.abs((a.z + lineF * line.z) - (s.z
				+ dirF * r.z)) < rayIntersectionZeroLockControl)
			return (Vector3d) a.clone()
					.add(getDirection().clone().scale(lineF));
		else
			return null;
	}

	/**
	 * @return start- and end-point
	 */
	@Override
	public Vector3d[] getVertices() {
		return new Vector3d[] { getStart(), getEnd() };
	}

	/**
	 * @param point
	 *            a point in space
	 * @return whether the point is between the start and end - point of this
	 *         line and on this line
	 */
	public boolean contains(final Vector3d point) {
		final Vector3d ap = getStart().to(point);
		return (ap.isSameOrientated(getDirection())
				&& ap.length() < length());
	}

	/**
	 * @return an independent straight containing this
	 */
	public Straight getStraight() {
		return new Straight(getStart().clone(), getDirection());
	}
}