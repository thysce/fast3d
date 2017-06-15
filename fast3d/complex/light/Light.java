package fast3d.complex.light;

import fast3d.graphics.Color;

/**
 * some surfaces can offer data to calculate an almost realistic shadow- or
 * light- effects
 * 
 * @author Tim Trense
 */
public interface Light {

	/**
	 * calculates the visible color for the given illuminatable;<br>
	 * the visible color is the color of the pixels on the screen for the given
	 * renderable
	 * 
	 * @param illum
	 *            the illuminatable giving the data of its surface
	 * @return the visible color to draw the illuminatable in if no other light
	 *         is used
	 */
	public Color illuminate(final Illuminatable illum);

}