package fast3d.renderables;

import fast3d.Renderable;
import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.MatrixCalculation;
import fast3d.math.Vector3d;
import fast3d.math.geom.Plate;
import fast3d.util.ColorGen;

/**
 * a simple, not illuminated (but with constant color) triangle in space
 * 
 * @author Tim Trense
 */
public class Triangle implements Renderable {

	/**
	 * edge 1
	 */
	public final Vector3d a;
	/**
	 * edge 2
	 */
	public final Vector3d b;
	/**
	 * edge 3
	 */
	public final Vector3d c;

	/**
	 * the used color to render this
	 */
	protected final Color color;

	/**
	 * constructs a triangle with the given edges
	 * 
	 * @param a
	 *            edge 1
	 * @param b
	 *            edge 2
	 * @param c
	 *            edge 3
	 * @param color
	 *            the color to render this
	 */
	public Triangle(final Vector3d a, final Vector3d b, final Vector3d c,
			final Color color) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.color = color;
	}

	/**
	 * constructs a triangle with the given edges and the color white
	 * 
	 * @param a
	 *            edge 1
	 * @param b
	 *            edge 2
	 * @param c
	 *            edge 3
	 */
	public Triangle(final Vector3d a, final Vector3d b, final Vector3d c) {
		this(a, b, c, ColorGen.WHITE());
	}

	/**
	 * @return a reference to the first edge given in the constructor
	 */
	public Vector3d getEdge1() {
		return a;
	}

	/**
	 * @return a reference to the second edge given in the constructor
	 */
	public Vector3d getEdge2() {
		return b;
	}

	/**
	 * @return a reference to the third edge given in the constructor
	 */
	public Vector3d getEdge3() {
		return c;
	}

	/**
	 * @return the difference-vector from edge 1 to edge 2
	 */
	public Vector3d getSide12() {
		return getEdge1().to(getEdge2());
	}

	/**
	 * @return the difference-vector from edge 1 to edge 3
	 */
	public Vector3d getSide13() {
		return getEdge1().to(getEdge3());
	}

	/**
	 * @return the difference-vector from edge 2 to edge 3
	 */
	public Vector3d getSide23() {
		return getEdge2().to(getEdge3());
	}

	/**
	 * @return a reference to the color of this
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return an independent version of this with cloned edges and color
	 */
	@Override
	public Triangle clone() {
		return new Triangle(getEdge1(), getEdge2(), getEdge3(), color.clone());
	}

	/**
	 * @return true if the given triangle has the same color and same edges
	 *         which can be flipped (edge1 may be edge2 and vice versa)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Triangle) {
			final Triangle t = (Triangle) obj;
			return color.equals(t.color) && hasEqualEdges(t);
		} else
			return false;
	}

	/**
	 * 
	 * @param t
	 *            the triangle to be checked
	 * @return true if the other triangle has the same edges, no matter in which
	 *         order (edge 1,2,3)
	 */
	public boolean hasEqualEdges(final Triangle t) {
		final Vector3d a = getEdge1();
		final Vector3d b = getEdge2();
		final Vector3d c = getEdge3();
		final Vector3d ta = t.getEdge1();
		final Vector3d tb = t.getEdge2();
		final Vector3d tc = t.getEdge3();

		return ((a.equals(ta) && b.equals(tb) && c.equals(tc))
				|| (a.equals(ta) && b.equals(tc) && c.equals(tb))
				|| (a.equals(tb) && b.equals(ta) && c.equals(tc))
				|| (a.equals(tb) && b.equals(tc) && c.equals(ta))
				|| (a.equals(tc) && b.equals(ta) && c.equals(tb))
				|| (a.equals(tc) && b.equals(tb) && c.equals(ta)));
	}

	@Override
	public String toString() {
		return "Triangle[" + getEdge1() + ";" + getEdge2() + ";" + getEdge3() + ";color="
				+ color + "]";
	}

	@Override
	public void shade(final Graphics3d s) {
		s.shade(a);
		s.shade(b);
		s.shade(c);
	}
	
	@Override
	public void render(final Graphics3d g) {
		g.setColor(color);
		g.polygon(a, b, c);
	}

	@Override
	public Vector3d rayTrace(final Vector3d rayOrig, final Vector3d rayDir) {
		final Vector3d ab = this.getSide12();
		final Vector3d ac = this.getSide13();
		final Vector3d diff = rayOrig.clone().sub(getEdge1());

		final double[][] matrix = new double[][] { { ab.x, ac.x, -rayDir.x, diff.x },
				{ ab.y, ac.y, -rayDir.y, diff.y }, { ab.z, ac.z, -rayDir.z, diff.z } };

		final double[] res = MatrixCalculation.solveLinearEquationSystem(matrix);

		if (res[0] + res[1] > 1 || res[2] < 0)
			return null;
		if (!(res[0] > 0 && res[0] < 1 && res[1] > 0 && res[1] < 1))
			return null;

		final Vector3d puncturepoint = getEdge1().clone().add(ab.clone().scale(res[0]))
				.add(ac.clone().scale(res[1]));

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
	 * @param v
	 *            the point to test
	 * @return whether the point is within this triangle
	 */
	public boolean contains(final Vector3d v) {
		final Vector3d w = this.getSide12();
		final Vector3d h = this.getSide13();
		final Vector3d av = v.clone().sub(getEdge1());
		final double[][] matrix = new double[][] { { w.x, h.x, av.x },
				{ w.y, h.y, av.y } };
		final double[] res = MatrixCalculation.solveLinearEquationSystem(matrix);
		final double factorW = res[0];
		final double factorH = res[1];
		return ((factorW * w.z + factorH * h.z == av.z) && (factorW + factorH <= 1)
				&& factorW <= 1 && factorW >= 0 && factorH <= 1 && factorH >= 0);
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
	 * rotates the triangle around the given axis<br>
	 * should not rotate with an angle of 0d
	 * 
	 * @param axis
	 *            the axe to rotate around in mathematically positive direction
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rot(final Vector3d axis, final double rad) {
		final Vector3d pos = getPos();
		a.sub(pos);
		b.sub(pos);
		c.sub(pos);
		a.rot(axis, rad);
		b.rot(axis, rad);
		c.rot(axis, rad);
		a.add(pos);
		b.add(pos);
		c.add(pos);
		getNormal().rot(axis, rad);
	}

	/**
	 * moves the triangle so that it's center will be at the target but holds it
	 * orientation
	 * 
	 * @param target
	 *            the new triangle's center
	 */
	public void moveTo(final Vector3d target) {
		final Vector3d dir = getPos().to(target);
		moveInAbsoluteDirection(dir);
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
		final Vector3d normal = getNormal().clone();
		final Vector3d rotAxis = Vector3d.crossP(dir, normal);
		final double rad = normal.angleTo(dir);
		rot(rotAxis, rad);
	}

	/**
	 * multiple triangles can be combined to an object, but share their edges so
	 * that on movement of the entire object every edge only has to be moved
	 * once
	 * 
	 * @param dir
	 *            the movements delta-vector
	 */
	public void moveInAbsoluteDirection(final Vector3d dir) {
		getEdge1().add(dir);
		getEdge2().add(dir);
		getEdge3().add(dir);
	}

	/**
	 * two triangles collide if they are not the same one and one side of the
	 * parameter triangle has a puncture point with this
	 * 
	 * @param t
	 *            the triangle to check collision with
	 * @return true if any side of the parameter has a puncture-point with this
	 */
	public boolean collides(final Triangle t) {
		if (t == this)
			return false;
		final Vector3d punctureSide12 = calculatePunctureWithLine(t.getEdge1(),
				t.getSide12());
		if (punctureSide12 != null)
			return true;
		final Vector3d punctureSide13 = calculatePunctureWithLine(t.getEdge1(),
				t.getSide13());
		if (punctureSide13 != null)
			return true;
		final Vector3d punctureSide23 = calculatePunctureWithLine(t.getEdge2(),
				t.getSide23());
		if (punctureSide23 != null)
			return true;
		return false;
	}

	/**
	 * @return the three triangle-edges
	 */
	@Override
	public Vector3d[] getVertices() {
		return new Vector3d[] { getEdge1(), getEdge2(), getEdge3() };
	}

	/**
	 * @return a clone vector that is orthogonal to the plate
	 */
	public Vector3d getNormal() {
		return Vector3d.crossP(getSide12(), getSide13()).normalize();
	}

	/**
	 * @return a clone to the triangles center
	 */
	@Override
	public Vector3d getPos() {
		return Vector3d.calculateAverage(a, b, c);
	}
	
	/**
	 * @return an independent plate object containing this
	 */
	public Plate getPlate(){
		return new Plate(getEdge1().clone(), getSide12(), getSide13());
	}
}