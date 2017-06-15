package fast3d.control;

import fast3d.math.Camera;
import fast3d.simple.FrameRate;
import fast3d.simple.SimplePanel3d;
import fast3d.simple.SimpleUniverse;

/**
 * provides some protected fields to easily access the most useful properties of
 * a SimplePanel3d<br>
 * <ul>
 * <li>sp3d - the cast of p3d to SimplePanel3d
 * <li>framerate - short for sp3d.frameRate()
 * <li>universe - short for sp3d.getUni()
 * <li>cam - short for sp3d.getCam()
 * </ul>
 * <br>
 * can be used just like a normal control<br>
 * 
 * @see fast3d.control.Control
 * @author Tim Trense
 */
public abstract class SimpleControl extends Control {

	protected final SimplePanel3d sp3d;
	protected final FrameRate framerate;
	protected final SimpleUniverse universe;
	protected final Camera cam;

	/**
	 * constructs a new SimpleControl on the given SimplePanel3d, which is
	 * passed to super
	 * 
	 * @param p3d
	 *            the SimplePanel3d
	 */
	public SimpleControl(final SimplePanel3d p3d) {
		super(p3d);
		this.sp3d = p3d;
		this.framerate = p3d.frameRate();
		this.universe = p3d.getUni();
		this.cam = p3d.getCam();
	}

}