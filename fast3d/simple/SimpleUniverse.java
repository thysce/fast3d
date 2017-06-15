package fast3d.simple;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import fast3d.Renderable;
import fast3d.complex.Universe;
import fast3d.complex.light.AmbientLight;
import fast3d.complex.light.DirectionalLight;
import fast3d.complex.light.Illuminatable;
import fast3d.complex.light.Light;
import fast3d.complex.light.SpecularLight;
import fast3d.math.Camera;
import fast3d.math.Vector3d;
import fast3d.util.ColorGen;
import fast3d.util.Sort;

/**
 * a default implementation of universe
 * 
 * @author Tim Trense
 */
public class SimpleUniverse implements Universe {

	/**
	 * the universes objects are all renderable<br>
	 * this list should be accessed by the wrapper methods add, remove, ...
	 * because of synchronization
	 */
	private final List<Renderable> objs;

	private Renderable[] sorted;
	private Vector3d lastCamPos;

	/**
	 * the universe may be enlightened
	 */
	private final List<Light> lights;
	/**
	 * each universe is just visible from one perspective at the same time
	 */
	private final Camera cam;

	private final List<Vector3d> vecs;

	/**
	 * generates an empty universe
	 */
	public SimpleUniverse() {
		objs = new LinkedList<Renderable>();
		cam = new Camera();
		lights = new ArrayList<Light>();
		vecs = new LinkedList<Vector3d>();
		sorted = null;
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given SimpleUniverse are equal considering
	 *         their contained renderables, lights and camera
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof SimpleUniverse) {
			final SimpleUniverse other = (SimpleUniverse) obj;
			return cam.equals(other.cam) && objs.equals(other.objs)
					&& lights.equals(other.lights);
		} else
			return false;
	}

	/**
	 * 
	 * @return fast3d.simple.SimpleUniverse[_parameter_]
	 **/
	public String toString() {
		return "fast3d.simple.SimpleUniverse[" + "cam=" + cam + ";renderables=" + objs
				+ ";lights=" + lights + "]";
	}

	/**
	 * synchronized variant to objs.add()<br>
	 * all renderables are asked for their vertices which than are stored in an
	 * internal list
	 * 
	 * @param r
	 *            the renderable to put in the universe
	 */
	@Override
	public void add(final Renderable... r) {
		sorted = null;
		synchronized (objs) {
			for (Renderable c : r)
				objs.add(c);
		}
		synchronized (vecs) {
			for (Renderable c : r) {
				final Vector3d[] verticies = c.getVertices();
				if (verticies != null)
					for (Vector3d vertex : verticies)
						vecs.add(vertex);
			}
		}
	}

	/**
	 * synchronized variant to objs.remove()<br>
	 * all renderables are asked for their vertices which than are removed from
	 * an internal list
	 * 
	 * @param r
	 *            the renderable to remove from the universe
	 */
	@Override
	public void remove(final Renderable... r) {
		sorted = null;
		synchronized (objs) {
			for (Renderable c : r)
				objs.remove(c);
		}
		synchronized (vecs) {
			for (Renderable c : r) {
				final Vector3d[] verticies = c.getVertices();
				if (verticies != null)
					for (Vector3d vertex : verticies)
						vecs.add(vertex);
			}
		}
	}

	/**
	 * to render all objects, it is necessary to order them by distance to the
	 * camera<br>
	 * the best way of sorting is detected automatically
	 * 
	 * @return a sorted list of all Renderables in the universe
	 */
	@Override
	public Renderable[] getObjsSorted() {
		if (sorted == null || !getCam().getPos().equals(lastCamPos)) {
			final Renderable[] objs = getObjs();
			Sort.Quicksort.Renderables.sort(getCam().getPos(), objs);
			sorted = objs;
			lastCamPos = getCam().getPos().clone();
			return objs;
		} else {
			Sort.Bubblesort.Renderables.sort(getCam().getPos(), sorted);
			return sorted;
		}
	}

	/**
	 * sometimes it may be useful to have all Renderables at one time as array
	 * 
	 * @return an array of all renderables at time of call
	 */
	@Override
	public Renderable[] getObjs() {
		final Renderable[] objs;
		synchronized (this.objs) {
			objs = new Renderable[this.objs.size()];
			this.objs.toArray(objs);
		}
		return objs;
	}

	/**
	 * @param s
	 *            start of ray
	 * @param r
	 *            direction of ray
	 * @return all Renderables which are targeted by the ray and their puncture
	 *         point with the ray
	 */
	@Override
	public Hashtable<Renderable, Vector3d> rayTrace(final Vector3d s, final Vector3d r) {
		if(s==null || r==null)
			return null;
		final Renderable[] objs = getObjs();
		final Hashtable<Renderable, Vector3d> res = new Hashtable<Renderable, Vector3d>();
		Vector3d current;
		for (Renderable rend : objs)
			if ((current = rend.rayTrace(s, r)) != null)
				res.put(rend, current);
		return res;
	}

	/**
	 * @return a reference to the used camera to render the universe
	 */
	@Override
	public Camera getCam() {
		return cam;
	}

	/**
	 * synchronized implementation to provide all light-sources to the rendering
	 * process
	 */
	@Override
	public Light[] getLights() {
		synchronized (lights) {
			return this.lights.toArray(
					(Light[]) Array.newInstance(Light.class, this.lights.size()));
		}
	}

	@Override
	public void addLight(final Light l) {
		synchronized (lights) {
			lights.add(l);
		}
	}

	@Override
	public void removeLight(Light l) {
		synchronized (lights) {
			lights.remove(l);
		}
	}

	/**
	 * adds an ambient light with full bright white color
	 */
	public void enableFullLighting() {
		final AmbientLight al = new AmbientLight(ColorGen.WHITE());
		this.addLight(al);
	}

	/**
	 * a very dark gray ambient light, a directional light (vector -1,-1,-1) full
	 * bright white and a specular light (same vector, light gray) are added
	 */
	public void enableDefaultLighting() {
		final AmbientLight al = new AmbientLight(ColorGen.DARK_GRAY().darker(.1));
		this.addLight(al);
		final DirectionalLight dl = new DirectionalLight(this, ColorGen.WHITE(),
				new Vector3d(-1, -1, -1).normalize());
		this.addLight(dl);
		final SpecularLight sl = new SpecularLight(this, ColorGen.LIGHT_GRAY(),
				new Vector3d(-1, -1, -1).normalize());
		this.addLight(sl);
		invalidateLights();
	}

	/**
	 * for all Illuminatables invalidateLight() is called
	 * 
	 * @see fast3d.complex.light.Illuminatable#invalidateLight()
	 */
	public void invalidateLights() {
		synchronized (objs) {
			for (Renderable r : objs)
				if (r instanceof Illuminatable)
					((Illuminatable) r).invalidateLight();
		}
	}

	/**
	 * @return all vertices of all renderables as a reference-list
	 */
	public List<Vector3d> getVertices() {
		return vecs;
	}
}