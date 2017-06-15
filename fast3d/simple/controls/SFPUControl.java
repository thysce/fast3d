package fast3d.simple.controls;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import fast3d.control.SimpleControl;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.simple.SimplePanel3d;

/**
 * SimpleFirstPersonUserControl<br>
 * does not check collisions of the camera, not appropriate for games<br>
 * WASD to move: W-forward D-right, UP/DOWN for nicking-camera-rotation,
 * LEFT/RIGHT or mouseDrag-cameraRotation, R for up, F for down moving in
 * moveSpeed, rotating in rotSpeed ; speeds may be set directly as fields, L to
 * log stats<br>
 * max-rotationX = +- Math.PI/3, no max-rotationY
 * 
 * @author Tim Trense
 */
public class SFPUControl extends SimpleControl {

	/**
	 * the current rotation of the camera look-direction around the y-axis <br>
	 * default at start is 0 (means look along Vector3d.forward())
	 */
	public double rotY = 0;
	/**
	 * the current rotation of the camera look-direction around the x-axis <br>
	 * default at start is 0 (means look along Vector3d.forward())
	 */
	public double rotX = 0;
	/**
	 * the current zoom-factor of the camera<br>
	 * default at start is 1 (means no zoom)
	 */
	public double zoom = 1;
	private Vector3d translation;
	private Vector2d lastMouseDownPos;
	/**
	 * the speed for rotation around x and y axis in radiant per
	 * mouse-move-pixel<br>
	 * default is 0.001
	 */
	public double rotSpeed = 0.001;
	/**
	 * the speed for rotation around x and y axis in radiant per key press on
	 * LEFT or RIGHT<br>
	 * default is rotSpeed * 50
	 */
	public double keyRotSpeed = rotSpeed * 50;
	/**
	 * the speed for moving along the look-direction-axes of the camera in
	 * length-units per key-press-tick<br>
	 * default is 0.5
	 */
	public double moveSpeed = 0.5;

	/**
	 * calls super and sets rotX and rotY to 0d
	 * 
	 * @param p3d
	 *            the panel3d to pass to super
	 */
	public SFPUControl(final SimplePanel3d p3d) {
		super(p3d);
		rotY = 0;
		rotX = 0;
		translation = null;
	}

	/**
	 * 
	 * @return fast3d.simple.control.userControl.SimpleFirstPersonUserControl[
	 *         _parameter_]
	 **/
	public String toString() {
		return "fast3d.simple.control.userControl.SimpleFirstPersonUserControl["
				+ "currentSettings=[rotX=" + rotX + ";rotY=" + rotY + ";zoom=" + zoom
				+ ";move=" + translation + "]; speeds[move=" + moveSpeed + ";rotPerMouse="
				+ rotSpeed + ";rotPerKey=" + keyRotSpeed + "]]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given SimpleFirstPersonUserControl are equal
	 *         considering their speeds
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof SFPUControl) {
			final SFPUControl other = (SFPUControl) obj;
			return moveSpeed == other.moveSpeed && rotSpeed == other.rotSpeed
					&& keyRotSpeed == other.keyRotSpeed;
		} else
			return false;
	}

	/**
	 * registers the needed listeners<br>
	 * makes the panel3d focusable
	 */
	@Override
	public void onActivate() {
		super.activate(true, true, false, false, false);
	}

	/**
	 * unregisters the listeners
	 */
	@Override
	public void onDeactivate() {
		super.deactivate(true, true, false, false, false);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (lastMouseDownPos == null)
			lastMouseDownPos = new Vector2d(e.getX(), e.getY());

		final double dx = e.getX() - lastMouseDownPos.x;
		final double dy = e.getY() - lastMouseDownPos.y;

		final double dry = dx * rotSpeed;
		final double drx = dy * rotSpeed;

		rotY -= dry;
		rotX -= drx;

		lastMouseDownPos = new Vector2d(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		lastMouseDownPos = null;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Vector3d dir = null;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			dir = Vector3d.forward();
			break;
		case KeyEvent.VK_S:
			dir = Vector3d.forward().invert();
			break;
		case KeyEvent.VK_A:
			dir = Vector3d.right().invert();
			break;
		case KeyEvent.VK_D:
			dir = Vector3d.right();
			break;
		case KeyEvent.VK_R:
			dir = Vector3d.up();
			break;
		case KeyEvent.VK_F:
			dir = Vector3d.up().invert();
			break;
		case KeyEvent.VK_SPACE:
			rotY = rotX = 0;
			zoom = 1;
			break;
		case KeyEvent.VK_LEFT:
			rotY += keyRotSpeed;
			break;
		case KeyEvent.VK_RIGHT:
			rotY -= keyRotSpeed;
			break;
		case KeyEvent.VK_UP:
			rotX += keyRotSpeed;
			break;
		case KeyEvent.VK_DOWN:
			rotX -= keyRotSpeed;
			break;
		case KeyEvent.VK_L:
			System.out.println("User-control stats: rotY=" + rotY + " rotX=" + rotX
					+ " zoom=" + zoom + " cam-pos=" + cam.getPos());
			break;
		default:
			return;
		}
		if (dir != null) {
			dir.scaleTo(moveSpeed);
			dir.rotY(rotY);
			translation = dir;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		translation = null;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoom += e.getWheelRotation() / 100d;
		if (zoom <= 0)
			zoom = 0.01;
		if (zoom > 3)
			zoom = 3;
	}

	@Override
	protected void performInput() {
		if (rotX > Math.PI / 3)
			rotX = Math.PI / 3;
		if (rotX < -Math.PI / 3)
			rotX = -Math.PI / 3;
		final Vector3d dir = Vector3d.forward();
		final Vector3d up = Vector3d.up();
		dir.rotX(rotX);
		dir.rotY(rotY);
		up.rotX(rotX);
		up.rotY(rotY);
		dir.scale(zoom);
		cam.lookInDirection(dir, up);
		if (translation != null)
			cam.moveInAbsoluteDirection(translation);
	}
}