package fast3d.util.click;

import fast3d.Renderable;

/**
 * a click listener receives all renderables which may be targeted by a user
 * clicking on a SimplePanel3d
 * 
 * @see fast3d.simple.SimplePanel3d
 * @author Tim Trense
 */
public interface ClickListener3d {

	/**
	 * called after rayTracing of a user click
	 * 
	 * @param r
	 *            all renderables which may be targeted by a user clicking on a
	 *            SimplePanel3d
	 */
	public void clicked(final Renderable... r);

}