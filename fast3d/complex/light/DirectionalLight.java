package fast3d.complex.light;

import fast3d.complex.Universe;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;

/**
 * a directional light just works as sunlight on earth
 * 
 * @author Tim Trense
 */
public class DirectionalLight extends ObstructableLight {

	/**
	 * the color of the light (for sun a nice orange will do)
	 */
	public final Color lightColor;
	/**
	 * the directional-light direction
	 */
	public final Vector3d direction;

	/**
	 * for illuminating it has to be calculated, whether an Illuminatable lays
	 * in the shadow of another
	 * 
	 * @param uni
	 *            the universe to pass to super
	 * @param c
	 *            the lights color
	 * @param dir
	 *            the direction of the light (should be normalized)
	 */
	public DirectionalLight(final Universe uni, final Color c, final Vector3d dir) {
		super(uni);
		this.lightColor = c;
		this.direction = dir;
	}

	@Override
	public Color illuminate(final Illuminatable c) {
		if (obstructings(c) != null)
			return null;
		final Material mat = c.getMaterial();
		if(mat==null)
			return null;
		final Color cCol = mat.diffuse;
		final Vector3d normal = c.getNormal();
		double factor = -Math.cos(normal.angleTo(direction));
		// light should only effect the front of any Renderable
		if (factor < 0)
			factor = 0;
		return new Color(cCol.red() * lightColor.red() * factor,
				cCol.green() * lightColor.green() * factor,
				cCol.blue() * lightColor.blue() * factor,
				cCol.alpha() * lightColor.alpha());
	}

	/**
	 * for all Illuminatables in space, this will be the same<br>
	 * returns a reference
	 * 
	 * @return the direction of this light
	 */
	public Vector3d directionOfLight() {
		return direction;
	}

	/**
	 * @return directionOfLight().clone();
	 */
	@Override
	public Vector3d directionOfLight(final Illuminatable l) {
		return directionOfLight().clone();
	}

	/**
	 * @return null
	 */
	@Override
	public Vector3d getPos() {
		return null;
	}

	/**
	 * 
	 * @return fast3d.complex.light.DirectionalLight[_parameter_]
	 **/
	public String toString() {
		return "fast3d.complex.light.DirectionalLight[" + "lightingColor=" + lightColor
				+ ";direction=" + direction + "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given DirectionalLight are equal considering
	 *         their lighting color and direction
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof DirectionalLight) {
			final DirectionalLight other = (DirectionalLight) obj;
			return this.lightColor.equals(other.lightColor)
					&& this.direction.equals(other.direction);
		} else
			return false;
	}
}