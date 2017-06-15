package fast3d.complex;

import java.util.Hashtable;

import fast3d.Renderable;
import fast3d.complex.light.Light;
import fast3d.math.Camera;
import fast3d.math.Vector3d;

/**
 * a universe holds all the data for a panel3d in an internal storage to render
 * <br>
 * the universes data contains
 * <ul>
 * <li>all Renderables
 * <li>all lights
 * <li>the camera to render from
 * </ul>
 * 
 * @author Tim Trense
 */
public interface Universe {

	/**
	 * synchronized add to the internal renderables-storage
	 * 
	 * @param r
	 *            the Renderables to add to the internal storage
	 */
	public void add(final Renderable... r);

	/**
	 * synchronized remove to the internal renderables-storage
	 * 
	 * @param r
	 *            the Renderables to remove from the internal storage (if it is
	 *            contained)
	 */
	public void remove(final Renderable... r);

	/**
	 * may just be called from getObjsSorted()
	 * 
	 * @return an array of all Renderables in the internal storage
	 */
	public Renderable[] getObjs();

	/**
	 * @return an array of all Renderables with those at first, which were most
	 *         far away from the camera
	 */
	public Renderable[] getObjsSorted();

	/**
	 * @return a reference to the used camera to render from
	 */
	public Camera getCam();

	/**
	 * @return any form of light used to illuminate the universes Illuminatables
	 *         during rendering process
	 */
	public Light[] getLights();

	/**
	 * should be synchronized
	 * 
	 * @param l
	 *            the light to add to the universe
	 */
	public void addLight(final Light l);

	/**
	 * should be synchronized
	 * 
	 * @param l
	 *            the light to remove from the universe
	 */
	public void removeLight(final Light l);

	/**
	 * 1)on a picking-action on the holding panel3d this method is called to
	 * determine, which Renderables may be targeted<br>
	 * 2)on a lighting-calculation the lighting-method will call rayTrace to
	 * determine whether some rays of light my be obstructed and now some
	 * objects lay in the shadow<br>
	 * this method should call rayTrace for every Renderable that is in the
	 * universes internal storage and list the Renderables which return not null
	 * from that method in a result-list
	 * 
	 * @param s
	 *            the start-vector of the ray
	 * @param r
	 *            the direction-vector of the ray
	 * @return every Renderable that may be targeted by a user clicking on the
	 *         panel3d-screen and it's puncture point with the ray
	 */
	public Hashtable<Renderable, Vector3d> rayTrace(final Vector3d s, final Vector3d r);
	
	/**
	 * for all Illuminatables invalidateLight() is called
	 * 
	 * @see fast3d.complex.light.Illuminatable#invalidateLight()
	 */
	public void invalidateLights();
}