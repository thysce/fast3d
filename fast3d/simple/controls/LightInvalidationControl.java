package fast3d.simple.controls;

import fast3d.Panel3d;
import fast3d.control.Control;

/**
 * accomplishes that the lighting-calculation for everything is done in every
 * single frame if activated
 * 
 * @author Tim Trense
 */
public class LightInvalidationControl extends Control {

	/**
	 * constructs a new control to invalidate the lights of the universe of the
	 * given Panel3d
	 * 
	 * @param p3d
	 *            the source of the Universe to invalidate the lights
	 */
	public LightInvalidationControl(final Panel3d p3d) {
		super(p3d);
	}

	@Override
	protected void performInput() {
		if (isActivated())
			p3d.getUniverse().invalidateLights();
	}

	@Override
	protected void onActivate() {
	}

	@Override
	protected void onDeactivate() {
	}

}
