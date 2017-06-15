package fast3d.simple.controls;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import fast3d.control.SimpleControl;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.simple.SimplePanel3d;
import fast3d.util.math.MathUtil;

/**
 * SimpleTopDownRotationUserControl<br>
 * works like fast3d.simple.control.autoControl.SimpleTopDownRotationAutoControl
 * but with user input and no auto-rotation<br>
 * rotSpeed and zoomSpeed may be adjusted by using the permitted fields
 * 
 * @see fast3d.simple.controls.RotationAutoControl
 * @author Tim Trense
 *
 */
public class RotationControl extends SimpleControl {
	/**
	 * the current rotation angle in radiant
	 */
	public double ry = 0;
	/**
	 * angle determining whether to look up or down to the Vector3d.zero()<br>
	 * default is 0.0
	 */
	public double rx = 0;
	/**
	 * the cameras distance from Vector3d.zero()<br>
	 * default is 10
	 */
	public double distanceToZero = 10;

	/**
	 * the automated rotation speed around the y-axis in radiant per second<br>
	 * default is 0 meaning auto-rotation disabled
	 */
	public double auto_rotPerSecond = 0;

	/**
	 * determines around which location in space the camera should orbit
	 */
	public final Vector3d translation = Vector3d.zero();

	/**
	 * whether to invert the rotation direction around the y-axis
	 */
	public boolean invertRY = false;
	/**
	 * whether to invert the rotation direction around the x-axis
	 */
	public boolean invertRX = false;

	private Vector2d lastMousePos;

	/**
	 * the absolute rotation in radiant to rotate up/down to not loose
	 * orientation
	 */
	public double maxRotX = MathUtil.piOver2 - 0.0001;
	/**
	 * how fast the camera rotates on mouseDrag<br>
	 * default is 0.001d
	 */
	public double rotSpeed = 0.001;
	/**
	 * how fast the camera zooms on mouseWheelMove<br>
	 * default is 0.1d
	 */
	public double zoomSpeed = 0.1;

	/**
	 * whether to allow the user looking down until -maxRotX<br>
	 * means rotation over the x-z-plate<br>
	 * default false
	 */
	public boolean allowNegativeRotX = false;
	/**
	 * whether to allow the user looking up until maxRotX<br>
	 * means rotation under the x-z-plate<br>
	 * default false
	 */
	public boolean allowPositiveRotX = false;

	/**
	 * 1d / friction where friction is [1d;+infinity[<br>
	 * coefficient to determine how extreme the slow-down after dragging is if
	 * slide == true<br>
	 * default 0d<br>
	 * values within [0.85d;0.95d] will do good
	 */
	public double invFriction = 0;
	private final Vector2d velocity = Vector2d.zero();

	/**
	 * constructs a new simple camera control for the user
	 * 
	 * @param p
	 *            the SimplePanel3d to pass to super
	 */
	public RotationControl(final SimplePanel3d p) {
		super(p);
	}

	/**
	 * 
	 * @return fast3d.simple.control.userControl.
	 *         SimpleTopDownRotationUserControl[_parameter_]
	 **/
	public String toString() {
		return "fast3d.simple.control.userControl.SimpleTopDownRotationUserControl["
				+ "currentView[rotX=" + rx + ";rotY=" + ry
				+ ";distanceToZero=" + distanceToZero
				+ "];rotationSettings[allowNegativeX"
				+ allowNegativeRotX + ";allowPositiveX="
				+ allowPositiveRotX + ";speed=" + rotSpeed
				+ ";maxRotX=" + maxRotX + "];zoomSpeed=" + zoomSpeed
				+ "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given SimpleTopDownRotationUserControl are
	 *         equal considering their speed settings
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof RotationControl) {
			final RotationControl other = (RotationControl) obj;
			return maxRotX == other.maxRotX
					&& allowPositiveRotX == other.allowPositiveRotX
					&& allowNegativeRotX == other.allowNegativeRotX
					&& rotSpeed == other.rotSpeed
					&& zoomSpeed == other.zoomSpeed;
		} else
			return false;
	}

	@Override
	protected void onActivate() {
		super.activate(false, true, false, false, false);
	}

	@Override
	protected void onDeactivate() {
		super.deactivate(false, true, false, false, false);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		super.mouseWheelMoved(e);
		distanceToZero += e.getPreciseWheelRotation() * distanceToZero
				* zoomSpeed;
		if (distanceToZero <= 0)
			distanceToZero = 0;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if (lastMousePos == null)
			lastMousePos = new Vector2d(e.getX(), e.getY());

		double delta = (e.getX() - lastMousePos.getX()) * rotSpeed;
		ry -= velocity.y = invertRY ? -delta : delta;
		delta = (e.getY() - lastMousePos.getY()) * rotSpeed;
		rx += velocity.x = invertRX ? -delta : delta;

		if (rx > maxRotX)
			rx = maxRotX;
		if (rx < -maxRotX)
			rx = -maxRotX;
		if (!allowNegativeRotX && rx < 0)
			rx = 0;
		if (!allowPositiveRotX && rx > 0)
			rx = 0;

		lastMousePos = new Vector2d(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		lastMousePos = null;
	}

	@Override
	protected void performInput() {
		ry += framerate.valuePerFrame(auto_rotPerSecond);
		final Vector3d campos = Vector3d.forward()
				.scale(-distanceToZero);
		final Vector3d up = Vector3d.up();
		up.rotX(-rx).rotY(ry);
		campos.rotX(-rx).rotY(ry);
		cam.moveTo(campos.add(translation));
		cam.lookTo(translation.clone(), up);
		ry -= velocity.y;
		rx += velocity.x;
		velocity.scale(invFriction);
	}

}