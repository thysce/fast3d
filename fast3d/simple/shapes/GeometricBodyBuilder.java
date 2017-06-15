package fast3d.simple.shapes;

import fast3d.complex.Group;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.util.ColorGen;

/**
 * base class for constructing groups of triangles or AdvTriangles (depending on
 * what field you set (material or color)) that make up the shape of a geometric
 * body (a box or cone for instance)<br>
 * at least setMaterial(Material) XOR setColor() has to be set (if both are set
 * than color is ignored)<br>
 * multiple builds are independent to one another<br>
 * at last, when all attributes are set, call build() to create a Group of the
 * specified shape<br>
 * subclasses are meant to build the shapes around Vector3d.zero() (as the
 * groups center) and by default of size (diameter) 1d<br>
 * 
 * @author Tim Trense
 */
public abstract class GeometricBodyBuilder {

	private Material material = null;
	private Color color = null;

	/**
	 * to set if the resulting geometric body has to be made out of AdvTriangles
	 * 
	 * @param m
	 *            the material used for AdvTriangles
	 */
	public void setMaterial(final Material m) {
		this.material = m;
	}

	/**
	 * to set if the resulting geometric body has to be made out of AdvTriangles
	 * <br>
	 * the emissive color will be black, shininess = 25 and alpha = opaque
	 * 
	 * @param average
	 *            the average materials color (ambient, diffuse, specular)
	 */
	public void setMaterial(final Color average) {
		this.material = new Material(average, average, average,
				ColorGen.BLACK(), 25, null);
	}

	/**
	 * to set if the resulting geometric body has to be made out of simple
	 * Triangles
	 * 
	 * @param c
	 *            the color used for simple Triangles
	 */
	public void setColor(final Color c) {
		this.color = c;
	}

	/**
	 * constructs a group that makes up the specified body<br>
	 * multiple calls make multiple groups independent to each other
	 * 
	 * @return the specified shape or null if setMaterial() or setColor() where
	 *         not called
	 */
	public Group build() {
		final Group g = new Group();
		if (material != null)
			build(g, material.clone());
		else if (color != null)
			build(g, color.clone());
		else
			return null;
		return g;
	}

	protected abstract void build(final Group plain,
			final Color color);

	protected abstract void build(final Group plain,
			final Material material);

	/**
	 * clone not supported
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * @return false
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public String toString() {
		return "Builder for GeometricBodies[ "
				+ (material != null ? material + "; " : "")
				+ (color != null ? color : "") + " ]";
	}

}