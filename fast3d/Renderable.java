package fast3d;

import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.Vector3d;

/**
 * Renderables can be passed to a universe to receive a render()-call during the
 * render-process of the universe
 * 
 * @author Tim Trense
 *
 */
public interface Renderable {

	/**
	 * should return every vertex used during render() to accelerate the shading
	 * and rendering <br>
	 * a renderable is requested to return all its vertices here <br>
	 * may return null if no vertices are used for whatever reason or the
	 * vertices are not known yet<br>
	 * 
	 * @return all the vertices used during render
	 */
	public Vector3d[] getVertices();

	/**
	 * called when this renderable is requested to calculate where to draw on
	 * screen<br>
	 * the graphics-context is not clipped, so it is greatly needed to really
	 * just draw itself<br>
	 * <b>during this method is is highly recommended to not draw anything</b>
	 * 
	 * @param s
	 *            the 3d-graphics-context to do calculations, not null
	 */
	public void shade(final Graphics3d s);

	/**
	 * called when this renderables is requested to draw itself on screen on the
	 * way prior calculated by shade()
	 * 
	 * @param g
	 *            the 3d-graphics-context to do drawings, not null
	 */
	public void render(final Graphics3d g);

	/**
	 * @return the current visible color of the renderable (preferred as a
	 *         reference)
	 */
	public Color getColor();

	/**
	 * for 3d-clicks on a panel3d or some lighting/shadowing calculation it is
	 * important to determine whether a renderable has some puncture point with
	 * a ray<br>
	 * may return null
	 * 
	 * @param s
	 *            the start-point of the ray
	 * @param r
	 *            the direction-vector of the ray
	 * @return the puncture-point with the ray
	 */
	public Vector3d rayTrace(final Vector3d s, final Vector3d r);

	/**
	 * should not return null <br>
	 * should return a reference (should not must)
	 * 
	 * @return the center-position of the renderable used to determine when the
	 *         render-call should be done between call of other renderables in
	 *         the universe to not hide this one behind some renderable actually
	 *         behind it
	 */
	public Vector3d getPos();

}