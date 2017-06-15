package fast3d.renderables;

import fast3d.Renderable;
import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.MatrixCalculation;
import fast3d.math.Vector3d;
import fast3d.math.geom.Plate;
import fast3d.util.ColorGen;

/**
 * a simple, not illuminated (but with constant color) rectangle in space
 * 
 * @author Tim Trense
 */
public class Rectangle implements Renderable {

	/**
	 * edge 1
	 */
	protected final Vector3d a;
	/**
	 * edge 2
	 */
	protected final Vector3d b;
	/**
	 * edge 3
	 */
	protected final Vector3d c;
	/**
	 * edge 4
	 */
	protected final Vector3d d;

	/**
	 * the used color to render this
	 */
	protected final Color color;

	/**
	 * constructs a rectangle with the given edges
	 * 
	 * @param a
	 *            edge 1
	 * @param b
	 *            edge 2
	 * @param c
	 *            edge 3
	 * @param d
	 *            edge 4
	 * @param color
	 *            the color to render this
	 */
	public Rectangle(final Vector3d a, final Vector3d b, final Vector3d c,
			final Vector3d d, final Color color) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.color = color;
	}

	/**
	 * constructs a rectangle with the given edges and the color white
	 * 
	 * @param a
	 *            edge 1
	 * @param b
	 *            edge 2
	 * @param c
	 *            edge 3
	 * @param d
	 *            edge 4
	 */
	public Rectangle(final Vector3d a, final Vector3d b, final Vector3d c,
			final Vector3d d) {
		this(a, b, c, d, ColorGen.WHITE());
	}

	/**
	 * @return the four edges of the rectangle
	 */
	@Override
	public Vector3d[] getVertices() {
		return new Vector3d[] { a, b, c, d };
	}

	@Override
	public Vector3d getPos() {
		return Vector3d.calculateAverage(a, b, c, d);
	}

	/**
	 * @see #getPos()
	 * @return getPos();
	 */
	public Vector3d getCenter() {
		return getPos();
	}

	@Override
	public void shade(Graphics3d s) {
		s.shade(a);
		s.shade(b);
		s.shade(c);
		s.shade(d);
	}
	
	@Override
	public void render(final Graphics3d g) {
		g.setColor(color);
		g.polygon(a, b, c, d);
	}

	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * should be the upper left corner
	 * 
	 * @return a reference to the first edge given in the constructor
	 */
	public Vector3d getEdge1() {
		return a;
	}

	/**
	 * should be the upper right corner
	 * 
	 * @return a reference to the second edge given in the constructor
	 */
	public Vector3d getEdge2() {
		return b;
	}

	/**
	 * should be the lower right corner
	 * 
	 * @return a reference to the third edge given in the constructor
	 */
	public Vector3d getEdge3() {
		return c;
	}
	
	/**
	 * should be the lower left corner
	 * 
	 * @return a reference to the fourth edge given in the constructor
	 */
	public Vector3d getEdge4() {
		return d;
	}
	
	/**
	 * @return the difference-vector from edge 1 to edge 2
	 */
	public Vector3d getSide12() {
		return getEdge1().to(getEdge2());
	}

	/**
	 * @return the difference-vector from edge 2 to edge 3
	 */
	public Vector3d getSide23() {
		return getEdge2().to(getEdge3());
	}

	/**
	 * @return the difference-vector from edge 3 to edge 4
	 */
	public Vector3d getSide34() {
		return getEdge3().to(getEdge4());
	}

	/**
	 * @return the difference-vector from edge 4 to edge 1
	 */
	public Vector3d getSide41() {
		return getEdge4().to(getEdge1());
	}

	/**
	 * @return the difference-vector from edge 1 to edge 3
	 */
	public Vector3d getDiagon13() {
		return getEdge1().to(getEdge3());
	}

	/**
	 * @return the difference-vector from edge 2 to edge 4
	 */
	public Vector3d getDiagon24() {
		return getEdge2().to(getEdge4());
	}

	/**
	 * @see #getSide12()
	 * @return getSide12();
	 */
	public Vector3d getWidth() {
		return getSide12();
	}

	/**
	 * @see #getSide41()
	 * @return getSide41().invert();
	 */
	public Vector3d getHeight() {
		return getSide41().invert();
	}

	@Override
	public Vector3d rayTrace(final Vector3d rayOrig, final Vector3d rayDir) {
		final Vector3d widthSide = this.getWidth();
		final Vector3d heightSide = this.getHeight();

		final double[][] matrix = new double[][] {
				{ widthSide.x, heightSide.x, -rayDir.x, rayOrig.x - a.x },
				{ widthSide.y, heightSide.y, -rayDir.y, rayOrig.y - a.y },
				{ widthSide.z, heightSide.z, -rayDir.z, rayOrig.z - a.z } };

		final double[] res = MatrixCalculation.solveLinearEquationSystem(matrix);

		if (!(res[0] > 0 && res[0] < 1 && res[1] > 0 && res[1] < 1))
			return null;

		final Vector3d puncturepoint = (Vector3d) a.clone().add(widthSide.clone().scale(res[0]))
				.add(heightSide.clone().scale(res[1]));

		return puncturepoint;
	}

	/**
	 * calculates the point that is on both this and the given line<br>
	 * the lines length is the length of its direction-vector<br>
	 * the given ray is mathematically restricted with the length-coefficient of
	 * the direction-vector in the interval [0.0d,1.0d]
	 * 
	 * @param lineOrig
	 *            the start-point of the ray to puncture this
	 * @param lineDir
	 *            the direction of the ray to puncture this
	 * @return the puncture-point of the ray to this, null if the ray does not
	 *         puncture this
	 */
	public Vector3d calculatePunctureWithLine(final Vector3d lineOrig,
			final Vector3d lineDir) {
		final Vector3d puncture = rayTrace(lineOrig, lineDir);
		if (puncture.distanceTo(lineOrig) < lineDir.length())
			return puncture;
		else
			return null;
	}

	/**
	 * @return an independent version of this with cloned edges and color
	 */
	@Override
	public Rectangle clone() {
		return new Rectangle(a.clone(), b.clone(), c.clone(), d.clone(), color.clone());
	}

	/**
	 * @return true if the given rectangle has the same color and same edges
	 *         which can be flipped (edge1 may be edge2 and vice versa)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Rectangle) {
			final Rectangle t = (Rectangle) obj;
			return color.equals(t.color) && hasEqualEdges(t);
		} else
			return false;
	}

	/**
	 * 
	 * @param t
	 *            the rectangle to be checked
	 * @return true if the other rectangle has the same edges, no matter in
	 *         which order (edge 1,2,3,4)
	 */
	private boolean hasEqualEdges(final Rectangle t) {
		if (d.equals(t.d)) // d is not flipped
			return ((a.equals(t.a) && b.equals(t.b) && c.equals(t.c))
					|| (a.equals(t.a) && b.equals(t.c) && c.equals(t.b))
					|| (a.equals(t.b) && b.equals(t.a) && c.equals(t.c))
					|| (a.equals(t.b) && b.equals(t.c) && c.equals(t.a))
					|| (a.equals(t.c) && b.equals(t.a) && c.equals(t.b))
					|| (a.equals(t.c) && b.equals(t.b) && c.equals(t.a)));
		if (d.equals(t.a)) // d is flipped with a
			return ((a.equals(t.d) && b.equals(t.b) && c.equals(t.c))
					|| (a.equals(t.d) && b.equals(t.c) && c.equals(t.b))
					|| (a.equals(t.b) && b.equals(t.d) && c.equals(t.c))
					|| (a.equals(t.b) && b.equals(t.c) && c.equals(t.d))
					|| (a.equals(t.c) && b.equals(t.d) && c.equals(t.b))
					|| (a.equals(t.c) && b.equals(t.b) && c.equals(t.d)));
		if (d.equals(t.b)) // d is flipped with b
			return ((a.equals(t.a) && b.equals(t.d) && c.equals(t.c))
					|| (a.equals(t.a) && b.equals(t.c) && c.equals(t.d))
					|| (a.equals(t.d) && b.equals(t.a) && c.equals(t.c))
					|| (a.equals(t.d) && b.equals(t.c) && c.equals(t.a))
					|| (a.equals(t.c) && b.equals(t.a) && c.equals(t.d))
					|| (a.equals(t.c) && b.equals(t.d) && c.equals(t.a)));
		if (d.equals(t.c)) // d is flipped with c
			return ((a.equals(t.a) && b.equals(t.b) && c.equals(t.d))
					|| (a.equals(t.a) && b.equals(t.d) && c.equals(t.b))
					|| (a.equals(t.b) && b.equals(t.a) && c.equals(t.d))
					|| (a.equals(t.b) && b.equals(t.d) && c.equals(t.a))
					|| (a.equals(t.d) && b.equals(t.a) && c.equals(t.b))
					|| (a.equals(t.d) && b.equals(t.b) && c.equals(t.a)));
		return false; // d is not equal to any edge of the other rectangle
	}

	@Override
	public String toString() {
		return "Rectangle[" + a + ";" + b + ";" + c + ";" + d + ";color=" + color + "]";
	}

	/**
	 * @return a vector that is orthogonal to the rectangle
	 */
	public Vector3d getNormal() {
		return Vector3d.crossP(getSide12(), getSide41().invert()).normalize();
	}

	/**
	 * rotates the line around the constant x axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotX(final double rad) {
		rot(Vector3d.right(), rad);
	}

	/**
	 * rotates the line around the constant y axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotY(final double rad) {
		rot(Vector3d.up(), rad);
	}

	/**
	 * rotates the line around the constant z axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotZ(final double rad) {
		rot(Vector3d.forward(), rad);
	}

	/**
	 * rotates the rectangle around the given axis
	 * 
	 * @param axis
	 *            the axe to rotate around in mathematically positive direction
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rot(final Vector3d axis, final double rad) {
		final Vector3d pos = getCenter();
		a.sub(pos);
		b.sub(pos);
		c.sub(pos);
		d.sub(pos);
		a.rot(axis, rad);
		b.rot(axis, rad);
		c.rot(axis, rad);
		d.rot(axis, rad);
		a.add(pos);
		b.add(pos);
		c.add(pos);
		d.add(pos);
	}

	/**
	 * moves the rectangle so that it's center will be at the target but holds
	 * it orientation
	 * 
	 * @param target
	 *            the new rectangle's center
	 */
	public void moveTo(final Vector3d target) {
		final Vector3d dir = getCenter().to(target);
		moveInAbsoluteDirection(dir);
	}

	/**
	 * multiple rectangles can share their edges so that on movement of those
	 * rectangles every edge only has to be moved once
	 * 
	 * @param dir
	 *            the movements delta-vector
	 */
	public void moveInAbsoluteDirection(final Vector3d dir) {
		a.add(dir);
		b.add(dir);
		c.add(dir);
		d.add(dir);
	}

	/**
	 * aligns the direction as new normal<br>
	 * lookInDirection calculates a rotation, the rotation-axis will be the
	 * crossProduct of currentNormal and newNormal (== given direction)
	 * 
	 * @param dir
	 *            the new triangles normal-vector
	 */
	public void lookInDirection(final Vector3d dir) {
		final Vector3d normal = getNormal();
		final Vector3d rotAxis = Vector3d.crossP(normal, dir);
		final double rad = normal.angleTo(dir);
		rot(rotAxis, rad);
	}

	/**
	 * two rectangles collide if they are not the same one and one side of the
	 * parameter triangle has a puncture point with this
	 * 
	 * @param t
	 *            the rectangle to check collision with
	 * @return true if any side of the parameter has a puncture-point with this
	 */
	public boolean collides(final Rectangle t) {
		if (t == this)
			return false;
		final Vector3d punctureSide12 = calculatePunctureWithLine(t.getEdge1(),
				t.getSide12());
		if (punctureSide12 != null)
			return true;
		final Vector3d punctureSide23 = calculatePunctureWithLine(t.getEdge2(),
				t.getSide23());
		if (punctureSide23 != null)
			return true;
		final Vector3d punctureSide34 = calculatePunctureWithLine(t.getEdge3(),
				t.getSide34());
		if (punctureSide34 != null)
			return true;
		final Vector3d punctureSide41 = calculatePunctureWithLine(t.getEdge4(),
				t.getSide41());
		if (punctureSide41 != null)
			return true;
		return false;
	}

	/**
	 * @param v
	 *            the point to test
	 * @return whether the point is within this rectangle
	 */
	public boolean contains(final Vector3d v) {
		final Vector3d w = this.getWidth();
		final Vector3d h = this.getHeight();
		final Vector3d av = v.clone().sub(a);
		final double[][] matrix = new double[][] { { w.x, h.x, av.x },
				{ w.y, h.y, av.y } };
		final double[] res = MatrixCalculation.solveLinearEquationSystem(matrix);
		final double factorW = res[0];
		final double factorH = res[1];
		return ((factorW * w.z + factorH * h.z == av.z) && factorW <= 1 && factorW >= 0
				&& factorH <= 1 && factorH >= 0);
	}
	
	/**
	 * calculates the nadir point of the given point to this plate<br>
	 * the nadir point is that point on the plate that is shortest from the given
	 * point<br>
	 * the nadir point has not to be on the plate necessarily - in particular:
	 * contains(nadir point) does not need to return true
	 * 
	 * @param point
	 *            the point to calculate the nadir point from
	 * @return the nadir point on this plate/rectangle from the given point
	 */
	public Vector3d nadirVector3d(final Vector3d point){
		return rayTrace(point,getNormal());
	}

	/**
	 * @return the plate over the edges 1,2,3
	 */
	public Plate getPlate123() {
		return new Plate(getEdge1().clone(),getSide12(),getDiagon13());
	}
	/**
	 * @return the plate over the edges 1,2,4
	 */
	public Plate getPlate124() {
		return new Plate(getEdge1().clone(),getSide12(),getSide41().invert());
	}
	/**
	 * @return the plate over the edges 1,3,4
	 */
	public Plate getPlate134() {
		return new Plate(getEdge1().clone(),getDiagon13(),getSide41().invert());
	}
	
	/**
	 * @return the plate over the edges 3,2,4
	 */
	public Plate getPlate324() {
		return new Plate(getEdge3().clone(),getSide23().invert(),getSide34());
	}
	/**
	 * @return the plate over the edges 3,2,4
	 */
	public Plate getPlate314() {
		return new Plate(getEdge3().clone(),getDiagon13().invert(),getSide34());
	}
	/**
	 * @return the plate over the edges 3,1,2
	 */
	public Plate getPlate312() {
		return new Plate(getEdge3().clone(),getDiagon13().invert(),getSide23().invert());
	}
}