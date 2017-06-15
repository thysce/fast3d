package fast3d.complex.light;

import java.util.Hashtable;

import fast3d.Renderable;
import fast3d.complex.Universe;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;

/**
 * some forms of light (e.g. directional- or point- light) may cause shadows
 * 
 * @author Tim Trense
 */
public abstract class ObstructableLight implements Light {

	/**
	 * for illuminating it has to be calculated, whether an illuminatable lays
	 * in the shadow of another, so all renderables of the universe have to be
	 * considered
	 */
	protected final Universe uni;

	/**
	 * determines whether to calculate all shadows caused by this light-source
	 * (may cause insane computational effort)
	 */
	public boolean calculateShadows = false;

	/**
	 * for illuminating it has to be calculated, whether an illuminatable lays
	 * in the shadow of another
	 * 
	 * @param uni
	 *            the universe containing all Renderables to be checked for
	 *            causing shadow
	 */
	public ObstructableLight(final Universe uni) {
		this.uni = uni;
	}

	@Override
	public abstract Color illuminate(final Illuminatable c);

	/**
	 * should be used by illuminate(Illuminatable) to determine whether the
	 * Illuminatable to currently illuminate lays in the shadow of another
	 * 
	 * @param l
	 *            the illuminatable to illuminate just yet
	 * @return a list of any Renderables that is in between of the Illuminatable
	 *         and the light-source or null if no Renderable causes shadow to
	 *         the parameter
	 */
	public Hashtable<Renderable, Vector3d> obstructings(
			final Illuminatable l) {
		if (!calculateShadows)
			return null;
		final Vector3d directionOfRay = directionOfLight(l).clone()
				.invert();
		final Hashtable<Renderable, Vector3d> all = uni
				.rayTrace(l.getPos().clone(), directionOfRay);
		all.remove(l);
		Vector3d pos = getPos();

		if (pos != null) {
			final Hashtable<Renderable, Vector3d> behindLight = uni
					.rayTrace(pos, directionOfRay);
			for (Renderable r : behindLight.keySet())
				all.remove(r);
		}

		if (!all.isEmpty())
			return all;
		else
			return null;
	}

	/**
	 * any obstructable light has to have a direction of it's light-rays
	 * 
	 * @param l
	 *            may for different Illuminatable the light has to target in a
	 *            different direction
	 * @return the direction of the rays of light
	 */
	public abstract Vector3d directionOfLight(final Illuminatable l);

	/**
	 * to calculate all Renderables between the currently to illuminate
	 * Illuminatable and the light source using ray tracing it it necessary to
	 * create a ray out if the lights position to not consider renderables
	 * behind the light (from the currently to illuminate Illuminatable) as
	 * being in front of the light
	 * 
	 * @return the lights position
	 */
	public abstract Vector3d getPos();

	/**
	 * 
	 * @return fast3d.complex.light.ObstructableLight[_parameter_]
	 **/
	public String toString() {
		return "fast3d.complex.light.ObstructableLight["
				+ "calcShadows=" + calculateShadows + ";uni=" + uni
				+ "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given ObstructableLight are equal
	 *         considering their universe and whether they do shadowing
	 *         calculations
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ObstructableLight) {
			final ObstructableLight other = (ObstructableLight) obj;
			return calculateShadows == other.calculateShadows
					&& uni.equals(other.uni);
		} else
			return false;
	}

	/**
	 * @return the universe in which the shadowing calculation is done
	 */
	public Universe getUniverse() {
		return uni;
	}
}