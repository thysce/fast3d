package fast3d.simple.controls;

import fast3d.control.SimpleControl;
import fast3d.math.Vector3d;
import fast3d.simple.SimplePanel3d;
import fast3d.util.math.MathUtil;

/**
 * SimpleTopDownRotationAutoControl<br>
 * an automated control rotating the camera around the universes center
 * (Vector3d.zero())
 * 
 * @author Tim Trense
 */
public class RotationAutoControl extends SimpleControl {

	/**
	 * the current rotation angle in radiant
	 */
	public double ry = 0;
	/**
	 * angle determining whether to look up or down to the Vector3d.zero()<br>
	 * default is Math.PI / 4
	 */
	public double rx = MathUtil.piOver4;
	/**
	 * the cameras distance from Vector3d.zero()<br>
	 * default is 5
	 */
	public double distanceToZero = 5;
	/**
	 * the rotation speed in radiant per second<br>
	 * default is Math.PI / 8 meaning 16 seconds per round
	 */
	public double rotPerSecond = MathUtil.piOver8;

	/**
	 * constructs a rotation on the camera
	 * 
	 * @param p3d the SimplePanel3d to rotate its camera
	 */
	public RotationAutoControl(final SimplePanel3d p3d) {
		super(p3d);
	}

	/**
	 * 
	 * @return fast3d.simple.control.autoControl.
	 *         SimpleTopDownRotationAutoControl[_parameter_]
	 **/
	public String toString() {
		return "fast3d.simple.control.autoControl.SimpleTopDownRotationAutoControl["
				+ "currentRotY=" + ry + ";currentRotX=" + rx + ";currentDistanceToZero="
				+ distanceToZero + ";currentRotYPerSecond=" + rotPerSecond + "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given SimpleTopDownRotationAutoControl are
	 *         equal considering their rotation speed and current
	 *         camera-viewing-position
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof RotationAutoControl) {
			final RotationAutoControl other = (RotationAutoControl) obj;
			return rx == other.rx && ry == other.ry
					&& distanceToZero == other.distanceToZero
					&& rotPerSecond == other.rotPerSecond;
		} else
			return false;
	}

	@Override
	protected void performInput() {
		ry += framerate.valuePerFrame(rotPerSecond);

		final Vector3d pos = new Vector3d(0, 0, distanceToZero);
		final Vector3d up = Vector3d.up();
		pos.rotX(-rx).rotY(ry);
		up.rotX(-rx).rotY(ry);
		cam.moveTo(pos);
		cam.lookTo(Vector3d.zero(), up);
	}

	@Override
	protected void onActivate() {
		//does not need any AWT-listeners
	}

	@Override
	protected void onDeactivate() {
		//see onActivate
	}

}