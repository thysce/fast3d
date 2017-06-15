package fast3d.complex.light;

import fast3d.graphics.Color;

/**
 * ambient light means background-light or the minimum light that is even in the
 * shadows
 * 
 * @author Tim Trense
 */
public class AmbientLight implements Light {

	/**
	 * the color of the ambient light (should be for realistic scenarios some
	 * dark gray)
	 */
	public final Color lightColor;

	/**
	 * constructs some ambient lighting with the given color
	 * 
	 * @param color
	 *            the light-color
	 */
	public AmbientLight(final Color color) {
		this.lightColor = color;
	}

	@Override
	public Color illuminate(final Illuminatable c) {
		final Material mat = c.getMaterial();
		if (mat != null) {
			final Color cCol = c.getMaterial().ambient.clone();
			return (Color) cCol.multiply(lightColor);
		} else
			return null;
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given AmbientLight are equal considering
	 *         their lighting color
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AmbientLight) {
			final AmbientLight other = (AmbientLight) obj;
			return this.lightColor.equals(other.lightColor);
		} else
			return false;
	}

	/**
	 * 
	 * @return fast3d.complex.light.AmbientLight[_parameter_]
	 **/
	public String toString() {
		return "fast3d.complex.light.AmbientLight[" + "lightingColor=" + lightColor + "]";
	}
}