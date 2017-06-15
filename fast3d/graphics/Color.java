package fast3d.graphics;

import fast3d.math.Vector4d;

/**
 * a color in 3d-space is represented by 4 values: r(red),g(green),b(blue) and
 * alpha(transparency) in the rgba-model, but the values of r,g,b and alpha have
 * to in the interval [0d,1d] <br>
 * used for lighting and general coloring in a 3d-graphics-context
 * 
 * @author Tim Trense
 */
public class Color extends Vector4d {

	/**
	 * constructs a color-model, r,g,b and a in [0d,1d]<br>
	 * an alpha value of 1 means opaque and 0 means transparent
	 * 
	 * @param r
	 *            the red-part
	 * @param g
	 *            the green-part
	 * @param b
	 *            the blue-part
	 * @param a
	 *            the alpha component of this color
	 */
	public Color(final double r, final double g, final double b,
			final double a) {
		super(r, g, b, a);
		constrain(0, 1);
	}

	/**
	 * constructs a color-model, r,g and b in [0d,1d]<br>
	 * the alpha component will be 1d (opaque)
	 * 
	 * @param r
	 *            the red-part
	 * @param g
	 *            the green-part
	 * @param b
	 *            the blue-part
	 */
	public Color(final double r, final double g, final double b) {
		this(r, g, b, 1d);
	}

	/**
	 * constructs a color-model, r,g,b,a as declared by x,y,z,a in the given
	 * vector3d
	 * 
	 * @param res
	 *            the components of the color
	 */
	public Color(final Vector4d res) {
		super(res.x, res.y, res.z, res.a);
	}

	/**
	 * @return whether the alpha-component is 1
	 */
	public boolean isOpaque() {
		return a == 1;
	}

	/**
	 * @return whether the alpha-component is 0
	 */
	public boolean isTransparent() {
		return a == 0;
	}

	/**
	 * @return the red-part of this color
	 */
	public double red() {
		return super.x;
	}

	/**
	 * @return the green-part of this color
	 */
	public double green() {
		return super.y;
	}

	/**
	 * @return the blue-part of this color
	 */
	public double blue() {
		return super.z;
	}

	/**
	 * an alpha value of 1 means opaque and 0 means transparent
	 * 
	 * @return the alpha component of this color
	 */
	public double alpha() {
		return super.a;
	}

	/**
	 * @return the awt-rgba coding for this color
	 */
	public int getRGB() {
		return awtColor().getRGB();
	}

	/**
	 * @return the abstract window toolkit form of this color
	 */
	public java.awt.Color awtColor() {
		return new java.awt.Color((int) (red() * 255),
				(int) (green() * 255), (int) (blue() * 255),
				(int) (alpha() * 255));
	}

	/**
	 * @return the abstract window toolkit form of this color, but ignoring the
	 *         alpha component (usable to draw this color to a JButton
	 *         preventing artifacts)
	 */
	public java.awt.Color awtColorOpaque() {
		return new java.awt.Color((int) (red() * 255),
				(int) (green() * 255), (int) (blue() * 255));
	}

	/**
	 * @return an independent new color-instance with the same rgb-values
	 */
	@Override
	public Color clone() {
		return new Color(x, y, z, a);
	}

	@Override
	public String toString() {
		return "fast3d.graphics.Color[r=" + x + ";g=" + y + ";b=" + z
				+ ";alpha=" + a + "]";
	}

	/**
	 * inverts the rgb-range <br>
	 * the inverted value of any rgb-value is ( 1 - value )<br>
	 * alpha will be constant
	 */
	@Override
	public Color invert() {
		x = 1 - x;
		y = 1 - y;
		z = 1 - z;
		return this;
	}

	/**
	 * does the same as invert() but also inverts the alpha<br>
	 * 
	 * @see #invert()
	 * @return a this-reference
	 */
	public Color invertWithAlpha() {
		a = 1 - a;
		return invert();
	}

	/**
	 * adds the r,g,b values from the parameter to this, alpha remains constant
	 * 
	 * @param c
	 *            the r,g,b-values to add to this
	 * @return a this-reference
	 */
	public Color add(final Color c) {
		this.x += c.x;
		this.y += c.y;
		this.z += c.z;
		return this;
	}

	/**
	 * subtracts the r,g,b values from the parameter to this, alpha remains
	 * constant
	 * 
	 * @param c
	 *            the r,g,b-values to subtract from this
	 * @return a this-reference
	 */
	public Color sub(final Color c) {
		this.x -= c.x;
		this.y -= c.y;
		this.z -= c.z;
		return this;
	}

	/**
	 * constrains all parts automatically to the correct interval of [0d,1d]
	 */
	public void constrain() {
		super.constrain(0, 1);
	}

	/**
	 * reduces rgb
	 * 
	 * @param amount
	 *            the rgb-value to subtract from this
	 * @return a this reference
	 */
	public Color darker(final double amount) {
		this.x -= amount;
		this.y -= amount;
		this.z -= amount;
		return this;
	}

	/**
	 * raises rgb
	 * 
	 * @param amount
	 *            the rgb-value to add from this
	 * @return a this reference
	 */
	public Color lighter(final double amount) {
		this.x += amount;
		this.y += amount;
		this.z += amount;
		return this;
	}

	/**
	 * sets the alpha component to 0.5d
	 * 
	 * @return a this reference
	 */
	public Color setSemiOpaque() {
		a = .5;
		return this;
	}

	/**
	 * sets the alpha component to 1d
	 * 
	 * @return a this reference
	 */
	public Color setOpaque() {
		a = 1;
		return this;
	}

	/**
	 * sets the alpha component to 0d
	 * 
	 * @return a this reference
	 */
	public Color setTransparent() {
		a = 0;
		return this;
	}

	/**
	 * calculates the average color for the given ones
	 * 
	 * @param colors
	 *            the set of colors to mix
	 * @return the average of all given colors (including the alpha)
	 */
	public static Color mix(final Color... colors) {
		final Vector4d res = Vector4d.calculateAverage(colors);
		return new Color(res);
	}
}