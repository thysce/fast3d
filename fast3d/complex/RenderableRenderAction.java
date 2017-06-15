package fast3d.complex;

import fast3d.Renderable;
import fast3d.graphics.Graphics3d;

/**
 * a render-action is called when the graphics3d sort()-methods calls it's parameter values for example
 * @author Tim Trense
 * @param <T> the renderable-class to render on call
 */
public interface RenderableRenderAction<T extends Renderable> {

	/**
	 * called when the parameter should be rendered
	 * @param obj the object to be rendered
	 * @param g the context to render to
	 */
	void render(final T obj, final Graphics3d g);
	
}