package fast3d.complex;

import fast3d.graphics.Graphics3d;
import fast3d.math.Vector3d;

/**
 * a render-action is called when the graphics3d sort()-methods calls it's
 * parameter values to be rendered, for example
 * 
 * @author Tim Trense
 */
public interface RenderAction {

	/**
	 * called when the render-action should draw something
	 * @param pos where to draw the something
	 * @param g the context to render to
	 */
	void render(final Vector3d pos, final Graphics3d g);
}