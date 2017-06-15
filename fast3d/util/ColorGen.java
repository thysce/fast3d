package fast3d.util;

import fast3d.graphics.Color;
import fast3d.math.Vector3d;
import fast3d.math.Vector4d;

/**
 * a color generator has some constant color values and functionality to create
 * various color easily
 * 
 * @author Tim Trense
 */
public abstract class ColorGen {

	/**
	 * @return a color with r=g=b=1 and full transparency
	 */
	public static final Color TRANSPARENT_WHITE() {
		return new Color(1, 1, 1, 0);
	}

	/**
	 * @return a color with r=g=b=0 and full transparency
	 */
	public static final Color TRANSPARENT_BLACK() {
		return new Color(0, 0, 0, 0);
	}

	/**
	 * @return a color with r=g=b=0
	 */
	public static final Color BLACK() {
		return new Color(0, 0, 0);
	}

	/**
	 * @return a color with r=g=b=1
	 */
	public static final Color WHITE() {
		return new Color(1, 1, 1);
	}

	/**
	 * @return a color with r=1 ,g=b=0
	 */
	public static final Color RED() {
		return new Color(1, 0, 0);
	}

	/**
	 * @return a color with g=1 ,r=b=0
	 */
	public static final Color GREEN() {
		return new Color(0, 1, 0);
	}

	/**
	 * @return a color with b=1 ,g=r=0
	 */
	public static final Color BLUE() {
		return new Color(0, 0, 1);
	}

	/**
	 * @return a color with r=g=b=0.7
	 */
	public static final Color LIGHT_GRAY() {
		return new Color(.7, .7, .7);
	}

	/**
	 * @return a color with r=g=b=0.3
	 */
	public static final Color DARK_GRAY() {
		return new Color(.3, .3, .3);
	}

	/**
	 * @return a color with r=g=b=0.5
	 */
	public static final Color GRAY() {
		return new Color(.5, .5, .5);
	}

	/**
	 * @return the default windows style color
	 */
	public static final Color CORNFLOWER_BLUE() {
		return new Color(0.3921, 0.5843, 0.9294);
	}

	/**
	 * @return a color with r=g=1 and b=0
	 */
	public static Color YELLOW() {
		return new Color(1, 1, 0);
	}

	/**
	 * @return a color with r=1, g=.55 and b=0
	 */
	public static Color ORANGE() {
		return new Color(1, .55, 0);
	}

	/**
	 * a not scientific way to convert a value between 0 and 1 to a color, where
	 * 0 means red, 0.5 means green and 1 means blue
	 * 
	 * @param wl
	 *            a value between 0d and 1d
	 * @return the corresponding color
	 */
	public static Color genLinear(final double wl) {
		if (wl < 0 || wl > 1)
			return null;
		double red = -1 * wl + 1;
		double green;
		if (wl < 0.5)
			green = 4 * wl - 1;
		else
			green = -4 * wl + 3;
		double blue = 1 * wl - .5;
		if (red < 0 || red > 1)
			red = 0;
		if (green < 0 || green > 1)
			green = 0;
		if (blue < 0 || blue > 1)
			blue = 0;
		return new Color(red, green, blue);
	}

	/**
	 * a wrapper to genPhysically() but with a mathematical range of values<br>
	 * a value of 1 will be ultra-violet<br>
	 * a value of 0 will be infra-red<br>
	 * so with look to genPhysically() the range of values is flipped
	 * 
	 * @see #genPhysically(double)
	 * @param wl
	 *            the lights wavelength but between 0d and 1d
	 * @return the corresponding color
	 */
	public static Color genRealistic(double wl) {
		wl = 1 - wl;
		wl *= 780 - 380;
		wl += 380;
		return genPhysically(wl);
	}

	/**
	 * a scientifically nearly correct way to convert the wavelength of light to
	 * a rgb-color in the visible spectrum<br>
	 * a value of 380 will be ultra-violet<br>
	 * a value of 780 will be infra-red
	 * 
	 * @param wavelength
	 *            the lights wavelength in nanometers (visible between 380 and
	 *            780)
	 * @return the corresponding color
	 */
	public static Color genPhysically(final double wavelength) {
		final double gamma = .9;
		final double max = 1;
		double factor;
		double r, g, b;

		if ((wavelength >= 380) && (wavelength < 440)) {
			r = -(wavelength - 440) / (440 - 380);
			g = 0.0;
			b = 1.0;
		} else if ((wavelength >= 440) && (wavelength < 490)) {
			r = 0.0;
			g = (wavelength - 440) / (490 - 440);
			b = 1.0;
		} else if ((wavelength >= 490) && (wavelength < 510)) {
			r = 0.0;
			g = 1.0;
			b = -(wavelength - 510) / (510 - 490);
		} else if ((wavelength >= 510) && (wavelength < 580)) {
			r = (wavelength - 510) / (580 - 510);
			g = 1.0;
			b = 0.0;
		} else if ((wavelength >= 580) && (wavelength < 645)) {
			r = 1.0;
			g = -(wavelength - 645) / (645 - 580);
			b = 0.0;
		} else if ((wavelength >= 645) && (wavelength < 781)) {
			r = 1.0;
			g = 0.0;
			b = 0.0;
		} else {
			r = 0.0;
			g = 0.0;
			b = 0.0;
		}

		if ((wavelength >= 380) && (wavelength < 420)) {
			factor = 0.3 + 0.7 * (wavelength - 380) / (420 - 380);
		} else if ((wavelength >= 420) && (wavelength < 701)) {
			factor = 1.0;
		} else if ((wavelength >= 701) && (wavelength < 781)) {
			factor = 0.3 + 0.7 * (780 - wavelength) / (780 - 700);
		} else {
			factor = 0.0;
		}
		;

		r = r <= 0.0 ? 0 : max * Math.pow(r * factor, gamma);
		g = g <= 0.0 ? 0 : max * Math.pow(g * factor, gamma);
		b = b <= 0.0 ? 0 : max * Math.pow(b * factor, gamma);

		return new Color(r, g, b);
	}

	/**
	 * converts a color with rgb-range 0 to 255 (like in the awt-color-model) to
	 * one of the range 0 to 1
	 * 
	 * @param r
	 *            the red part
	 * @param g
	 *            the green part
	 * @param b
	 *            the blue part
	 * @return the corresponding color
	 */
	public static Color from255RGB(double r, double g, double b) {
		r /= 255;
		g /= 255;
		b /= 255;
		if (r < 0)
			r = 0;
		if (r > 1)
			r = 1;
		if (g < 0)
			g = 0;
		if (g > 1)
			g = 1;
		if (b < 0)
			b = 0;
		if (b > 1)
			b = 1;
		return new Color(r, g, b);
	}

	/**
	 * gets an int where:<br>
	 * bits-indices color-component<br>
	 * 32-24 alpha<br>
	 * 23-16 red <br>
	 * 15-08 green<br>
	 * 08-00 blue
	 * 
	 * @param argb
	 *            the ARGB-components of the color
	 * @return the corresponding color
	 */
	public static Color from255ARGB(final int argb) {
		final int alpha = (argb & 0xFF000000) >> 24;
		final int red = (argb & 0x00FF0000) >> 16;
		final int green = (argb & 0x0000FF00) >> 8;
		final int blue = (argb & 0x000000FF);
		return from255RGBA(red, green, blue, alpha);
	}

	/**
	 * converts a color with rgba-range 0 to 255 (like in the awt-color-model)
	 * to one of the range 0 to 1
	 * 
	 * @param r
	 *            the red part
	 * @param g
	 *            the green part
	 * @param b
	 *            the blue part
	 * @param a
	 *            the alpha component
	 * @return the corresponding color
	 */
	public static Color from255RGBA(double r, double g, double b, double a) {
		r /= 255;
		g /= 255;
		b /= 255;
		a /= 255;
		if (r < 0)
			r = 0;
		if (r > 1)
			r = 1;
		if (g < 0)
			g = 0;
		if (g > 1)
			g = 1;
		if (b < 0)
			b = 0;
		if (b > 1)
			b = 1;
		if (a < 0)
			a = 0;
		if (a > 1)
			a = 1;
		return new Color(r, g, b, a);
	}

	/**
	 * a wrapper to from255RGBA()
	 * 
	 * @param c
	 *            the awtColor
	 * @return the corresponding color
	 */
	public static Color fromAWTColor(final java.awt.Color c) {
		return from255RGBA(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}

	/**
	 * a wrapper to from255RGB()
	 * 
	 * @param c
	 *            the awtColor
	 * @return the corresponding color
	 */
	public static Color fromAWTColorWithoutAlpha(final java.awt.Color c) {
		return from255RGB(c.getRed(), c.getGreen(), c.getBlue());
	}

	/**
	 * just copies the xyz-values to rgb, if the are in range of 0d to 1d <br>
	 * if they are out of range they are constraint: all values over 1 are set
	 * to 1, all values under 0 are set to 0
	 * 
	 * @param vec
	 *            the xyz coordinates as rgb-parts
	 * @return the corresponding color
	 */
	public static Color fromVector(final Vector3d vec) {
		double r = vec.x;
		double g = vec.y;
		double b = vec.z;
		if (r < 0)
			r = 0;
		if (r > 1)
			r = 1;
		if (g < 0)
			g = 0;
		if (g > 1)
			g = 1;
		if (b < 0)
			b = 0;
		if (b > 1)
			b = 1;
		return new Color(r, g, b);
	}

	/**
	 * just copies the xyza-values to rgba, if the are in range of 0d to 1d <br>
	 * if they are out of range they are constraint: all values over 1 are set
	 * to 1, all values under 0 are set to 0
	 * 
	 * @param vec
	 *            the xyza coordinates as rgba-parts
	 * @return the corresponding color
	 */
	public static Color fromVector(final Vector4d vec) {
		double r = vec.x;
		double g = vec.y;
		double b = vec.z;
		double a = vec.a;
		if (r < 0)
			r = 0;
		if (r > 1)
			r = 1;
		if (g < 0)
			g = 0;
		if (g > 1)
			g = 1;
		if (b < 0)
			b = 0;
		if (b > 1)
			b = 1;
		if (a < 0)
			a = 0;
		if (a > 1)
			a = 1;
		return new Color(r, g, b, a);
	}

	/**
	 * a wrapper to the awtColor-method getHSBColor<br>
	 * all arguments within the range of [0d,1d]
	 * 
	 * @param h
	 *            the hue components
	 * @param s
	 *            the saturation of the color
	 * @param b
	 *            the brightness of the color
	 * @return the corresponding color
	 */
	public static Color fromHSB(final double h, final double s, final double b) {
		return fromAWTColor(java.awt.Color.getHSBColor((float) h, (float) s, (float) b));
	}

	/**
	 * @return a random opaque color
	 */
	public static Color random() {
		return new Color(Math.random(), Math.random(), Math.random());
	}

	/**
	 * @param opacity
	 *            the opacity value of the returned color
	 * @return a random color with the given opacity
	 */
	public static Color randomOpacity(final double opacity) {
		return new Color(Math.random(), Math.random(), Math.random(), opacity);
	}

	/**
	 * @return a random color with random opacity
	 */
	public static Color randomOpacity() {
		return new Color(Math.random(), Math.random(), Math.random(), Math.random());
	}

	/**
	 * @return a random physically nearly correct color-wavelength which can be
	 *         used by genPhysically() in the visible spectrum
	 */
	public static double randomPhysicallyWavelength() {
		return Math.random() * (780 - 380) + 380;
	}

	/**
	 * @return a random physically nearly correct color in the visible spectrum
	 */
	public static Color randomPhysically() {
		return genPhysically(randomPhysicallyWavelength());
	}

	/**
	 * converts a rgba-color-integer (like in the awt-color-model) to a color
	 * 
	 * @param rgba
	 *            the rgba integer given by awtColor.getRGB()
	 * @return the corresponding color
	 */
	public static Color from255RGBA(final int rgba) {
		return fromAWTColor(new java.awt.Color(rgba));
	}

	/**
	 * converts a rgb-color-integer (like in the awt-color-model) to a color
	 * 
	 * @param rgb
	 *            the rgb integer given by awtColor.getRGB()
	 * @return the corresponding color
	 */
	public static Color from255RGB(final int rgb) {
		return fromAWTColorWithoutAlpha(new java.awt.Color(rgb));
	}
}