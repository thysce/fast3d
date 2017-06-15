package fast3d.complex.light;

import fast3d.Renderable;
import fast3d.math.Vector3d;

/**
 * a renderable is illuminatable if it has a surface that can be colored
 * considering the universes lighting data
 * 
 * @author Tim Trense
 */
public interface Illuminatable extends Renderable {

	/**
	 * may return null
	 * 
	 * @return all data needed to calculate the visible color of the surface
	 */
	public Material getMaterial();

	/**
	 * needed for some aligned lighting calculation like diffusive light or
	 * specular light may return null
	 * 
	 * @return the vector outside-pointing and orthogonal to the surface
	 */
	public Vector3d getNormal();

	/**
	 * indicates that the previous (maybe cached) visible color has to be
	 * recalculated during the next render process
	 */
	public void invalidateLight();

	/**
	 * called when the renderable has to recalculate the visible in here (for
	 * asynchronous calls)
	 * 
	 * @param lights
	 *            all lights effecting the surface of the renderable
	 */
	public void revalidateLight(final Light... lights);

}