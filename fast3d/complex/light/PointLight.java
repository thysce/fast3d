package fast3d.complex.light;

import fast3d.complex.Universe;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;

/**
 * a point light works like the light out of some street light
 * 
 * @author Tim Trense
 */
public class PointLight extends ObstructableLight {

	/**
	 * the color of the light
	 */
	public final Color lightColor;
	/**
	 * the position of this light-source in the universe
	 */
	public final Vector3d pos;

	/**
	 * because this sort of light is located, it may be obstructed and cause
	 * shadow behind a Renderable
	 * 
	 * @param uni
	 *            the universe to contain all shadow-causing Renderables
	 * @param c
	 *            the light-color
	 * @param pos
	 *            the position of this light-source in the universe
	 */
	public PointLight(final Universe uni, final Color c, final Vector3d pos) {
		super(uni);
		this.lightColor = c;
		this.pos = pos;
	}

	@Override
	public Color illuminate(final Illuminatable c) {
		if (obstructings(c) != null)
			return null;
		final Material mat = c.getMaterial();
		if (mat == null)
			return null;
		final Color cCol = mat.diffuse;
		final Vector3d direction = directionOfLight(c);

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
	 * @return a reference
	 */
	public Vector3d getPos() {
		return pos;
	}

	/**
	 * for every Illuminatable in space, this will be different because of it's
	 * relative position to this light-source
	 * 
	 * @param r
	 *            the Illuminatable to calculate the relative position to
	 * @return the direction of this light to the given Renderable
	 */
	@Override
	public Vector3d directionOfLight(final Illuminatable r) {
		return pos.to(r.getPos());
	}

	/**
	 * 
	 * @return fast3d.complex.light.PointLight[_parameter_]
	 **/
	public String toString() {
		return "fast3d.complex.light.PointLight[" + "lightingColor=" + lightColor
				+ ";pos=" + pos + "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given PointLight are equal considering their
	 *         lighting color and position
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof PointLight) {
			final PointLight other = (PointLight) obj;
			return lightColor.equals(other.lightColor) && pos.equals(other.pos);
		} else
			return false;
	}
}