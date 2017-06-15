package fast3d.graphics;

import java.awt.Graphics2D;
import java.util.Stack;

import fast3d.complex.light.Light;
import fast3d.math.Shader;
import fast3d.math.Vector3d;

/**
 * gives the ability to draw directly into a 3d-scenery like a turtle<br>
 * each turtle-method starts with t<br>
 * you can move around in the scenery drawing lines or not (specified by
 * tsetPen(Color or null)), push and pop on a history of transformations and
 * rotate the turtle on it's own coordinate system<br>
 * on default the pen is down
 * 
 * @see #tmove(Vector3d)
 * @see #tmoveTo(Vector3d)
 * @see #tpop()
 * @see #tpush()
 * @see #trotX(double)
 * @see #trotXDeg(double)
 * @see #tsetPen(boolean)
 * @author Tim Trense
 */
public class Turtle3d extends Graphics3d {

	private class TurtleTransform {

		public final Vector3d position = Vector3d.zero();
		public double rotX = 0, rotY = 0, rotZ = 0;

		public TurtleTransform clone() {
			final TurtleTransform clone = new TurtleTransform();
			clone.position.set(position.clone());
			clone.rotX = rotX;
			clone.rotY = rotY;
			clone.rotZ = rotZ;
			return clone;
		}
	}

	private final Stack<TurtleTransform> history;
	private TurtleTransform transform;
	private boolean penDown;

	/**
	 * calls super and locates the turtle at (0,0,0) with no rotation
	 * 
	 * @see fast3d.graphics.Graphics3d#Graphics3d(Graphics2D, Shader, Light...)
	 * @param g
	 *            parameter for super
	 * @param sh
	 *            parameter for super
	 * @param lights
	 *            parameter for super
	 */
	public Turtle3d(final Graphics2D g, final Shader sh,
			final Light... lights) {
		super(g, sh, lights);
		transform = new TurtleTransform();
		history = new Stack<TurtleTransform>();
		penDown = true;
	}

	/**
	 * moves the turtle to the absolute position given and remains it rotation
	 * <br>
	 * if the pen is down then draw a line from current position to the new
	 * position
	 * 
	 * @param newpos
	 *            the new turtles position
	 */
	public void tmoveTo(final Vector3d newpos) {
		if (penDown)
			line(transform.position, newpos);
		transform.position.set(newpos);
	}

	/**
	 * moves the turtle relative to it's position and rotation: if the turtle
	 * looks to the right than it will go to the right on call
	 * tmove(Vector3d.forward())<br>
	 * if the pen is down then draw a line from current position to the new
	 * position
	 * 
	 * 
	 * @param dir
	 *            the turtle-relative direction to move
	 */
	public void tmove(final Vector3d dir) {
		dir.rotX(transform.rotX).rotY(transform.rotY)
				.rotZ(transform.rotZ);
		final Vector3d newpos = transform.position.clone().add(dir);
		if (penDown)
			line(transform.position, newpos);
		transform.position.set(newpos);
	}

	/**
	 * determines whether to draw a line on move-actions
	 * 
	 * @param down
	 *            whether the pen should be down
	 */
	public void tsetPen(final boolean down) {
		penDown = down;
	}

	/**
	 * rotates the turtle around the x-axis
	 * 
	 * @param rad
	 *            the angle in radians
	 */
	public void trotX(final double rad) {
		transform.rotX += rad;
	}

	/**
	 * rotates the turtle around the x-axis
	 * 
	 * @param deg
	 *            the angle in degrees
	 */
	public void trotXDeg(final double deg) {
		trotX(Math.toRadians(deg));
	}

	/**
	 * rotates the turtle around the y-axis
	 * 
	 * @param rad
	 *            the angle in radians
	 */
	public void trotY(final double rad) {
		transform.rotY += rad;
	}

	/**
	 * rotates the turtle around the y-axis
	 * 
	 * @param deg
	 *            the angle in degrees
	 */
	public void trotYDeg(final double deg) {
		trotY(Math.toRadians(deg));
	}

	/**
	 * rotates the turtle around the z-axis
	 * 
	 * @param rad
	 *            the angle in radians
	 */
	public void trotZ(final double rad) {
		transform.rotZ += rad;
	}

	/**
	 * rotates the turtle around the z-axis
	 * 
	 * @param deg
	 *            the angle in degrees
	 */
	public void trotZDeg(final double deg) {
		trotZ(Math.toRadians(deg));
	}

	/**
	 * saves the current transformation on a stack
	 * 
	 * @see #tpop()
	 */
	public void tpush() {
		history.push(transform.clone());
	}

	/**
	 * returns the turtle to the transformation on top of the stack or to the
	 * origin at no rotation if no transformation was saved using tpush()
	 * 
	 * @see #tpush()
	 */
	public void tpop() {
		if (history.isEmpty())
			transform = new TurtleTransform();
		else
			transform = history.pop();
	}
}