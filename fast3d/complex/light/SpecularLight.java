package fast3d.complex.light;

import fast3d.complex.Universe;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;

/**
 * specular light means the shining dot on polished surfaces
 * 
 * @author Tim Trense
 *
 */
public class SpecularLight extends DirectionalLight {

	/**
	 * constructs a new shiny light
	 * 
	 * @param uni
	 *            the universe to compute shadows cause by other renderable than
	 *            the current-to-illuminate on
	 * @param c
	 *            the color of this light
	 * @param dir
	 *            the direction of the virtual light-rays of this
	 */
	public SpecularLight(final Universe uni, final Color c,
			final Vector3d dir) {
		super(uni, c, dir);
	}

	@Override
	public Color illuminate(final Illuminatable c) {
		if (obstructings(c) != null)
			return null;
		final Vector3d light = directionOfLight().clone();
		final Vector3d norm = c.getNormal();
		final double lATn = light.angleTo(norm);
		if (lATn > Math.PI / 4 * 3) { // ignore shadow
			final Material mat = c.getMaterial();
			if (mat == null)
				return null;
			final Color cCol = mat.specular;
			final Vector3d cam = this.uni.getCam().getLookDir();
			final Vector3d axis = Vector3d.crossP(c.getNormal(),
					light);
			final Vector3d refl = light.clone().invert().rot(axis,
					2 * lATn);

			double factor = Math.pow(Math.cos(cam.angleTo(refl)),
					c.getMaterial().shininess);
			final double factor2 = (c.getMaterial().shininess + 2)
					/ (Math.PI * 2);

			if (factor > 0)
				return new Color(
						cCol.red() * lightColor.red() * factor
								* factor2,
						cCol.green() * lightColor.green() * factor
								* factor2,
						cCol.blue() * lightColor.blue() * factor
								* factor2,
						cCol.alpha() * lightColor.alpha());
			else
				return null;
		} else
			return null;
	}

	/**
	 * 
	 * @return fast3d.complex.light.SpecularLight[_parameter_]
	 **/
	public String toString() {
		return "fast3d.complex.light.SpecularLight["
				+ "lightingColor=" + lightColor + ";direction="
				+ direction + "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given SpecularLight are equal considering
	 *         their super.equals()-result
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof SpecularLight) {
			final SpecularLight other = (SpecularLight) obj;
			return super.equals(other);
		} else
			return false;
	}
}