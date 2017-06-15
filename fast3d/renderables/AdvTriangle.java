package fast3d.renderables;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fast3d.complex.light.Illuminatable;
import fast3d.complex.light.Light;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.util.ColorGen;
import fast3d.util.math.MathUtil;

/**
 * an advanced version of triangle3d that is illuminatable with any built-in
 * light<br>
 * getColor() returns the current visible color
 * 
 * @author Tim Trense
 */
public class AdvTriangle extends Triangle implements Illuminatable {

	private Color currentVisibleColor = null;
	private final Vector3d normal;
	/**
	 * the lighting-color information of the triangles surface
	 */
	public final Material material;
	/**
	 * the texture-mapping coordinate for edge1
	 */
	public final Vector2d a2 = Vector2d.zero();
	/**
	 * the texture-mapping coordinate for edge2
	 */
	public final Vector2d b2 = Vector2d.zero();
	/**
	 * the texture-mapping coordinate for edge3
	 */
	public final Vector2d c2 = Vector2d.zero();

	/**
	 * normal-vector at edge 1 if gouraud == true
	 */
	public final Vector3d na = Vector3d.zero();
	/**
	 * normal-vector at edge 2 if gouraud == true
	 */
	public final Vector3d nb = Vector3d.zero();
	/**
	 * normal-vector at edge 3 if gouraud == true
	 */
	public final Vector3d nc = Vector3d.zero();

	/**
	 * determines whether to do lighting calculations if a texture is set in the
	 * material (illumination is always done if no texture is set)<br>
	 * default true
	 */
	public boolean illuminateTexture = true;

	/**
	 * call the super constructor<br>
	 * sets a custom normal vector for lighting calculation<br>
	 * for correct lighting the normal vector should be orthogonal to this and
	 * normalized<br>
	 * to set ambient, diffuse and specular light colors and the shininess,
	 * direct access to the fields is permitted
	 * 
	 * @param a
	 *            the first edge
	 * @param b
	 *            the second edge
	 * @param c
	 *            the third edge
	 * @param normal
	 *            the custom normal vector
	 */
	public AdvTriangle(final Vector3d a, final Vector3d b,
			final Vector3d c, final Vector3d normal) {
		super(a, b, c, new Color(0, 0, 0));
		this.normal = normal;
		this.material = new Material();
	}

	/**
	 * calls this constructor, the normal vector will be orthogonal to this and
	 * normalized<br>
	 * normal vector as the one of triangle3d
	 * 
	 * @param a
	 *            the first edge
	 * @param b
	 *            the second edge
	 * @param c
	 *            the third edge
	 */
	public AdvTriangle(final Vector3d a, final Vector3d b,
			final Vector3d c) {
		this(a, b, c, Vector3d.crossP(a.to(b), a.to(c)).normalize());
	}

	/**
	 * call the super constructor<br>
	 * sets a custom normal vector for lighting calculation<br>
	 * for correct lighting the normal vector should be orthogonal to this and
	 * normalized<br>
	 * 
	 * @param a
	 *            the first edge
	 * @param b
	 *            the second edge
	 * @param c
	 *            the third edge
	 * @param ambient
	 *            the color used for ambient lighting
	 * @param diffuse
	 *            the color used for diffuse lighting (lights extending
	 *            obstructableLight)
	 * @param specular
	 *            the color used for specular lighting
	 * @param emissive
	 *            the color definitely visible at minimum even if no light
	 *            shines to this
	 * @param shininess
	 *            the factor for specular lighting
	 * @param normal
	 *            the custom normal vector
	 */
	public AdvTriangle(final Vector3d a, final Vector3d b,
			final Vector3d c, final Color ambient,
			final Color diffuse, final Color specular,
			final Color emissive, final double shininess,
			final Vector3d normal) {
		super(a, b, c, new Color(0, 0, 0));
		this.material = new Material(ambient, diffuse, specular,
				emissive, shininess, null);
		this.normal = normal;
	}

	/**
	 * call this constructor<br>
	 * the normal vector will be orthogonal to this and normalized<br>
	 * 
	 * @param a
	 *            the first edge
	 * @param b
	 *            the second edge
	 * @param c
	 *            the third edge
	 * @param mat
	 *            the used material for this surface (reference)
	 */
	public AdvTriangle(final Vector3d a, final Vector3d b,
			final Vector3d c, final Material mat) {
		this(a, b, c, mat,
				Vector3d.crossP(a.to(b), a.to(c)).normalize());
	}

	/**
	 * call the super constructor<br>
	 * sets a custom normal vector for lighting calculation<br>
	 * for correct lighting the normal vector should be orthogonal to this and
	 * normalized<br>
	 * 
	 * @param a
	 *            the first edge
	 * @param b
	 *            the second edge
	 * @param c
	 *            the third edge
	 * @param mat
	 *            the used material for this surface (reference)
	 * @param normal
	 *            the normal vector used for lighting calculation (reference)
	 */
	public AdvTriangle(final Vector3d a, final Vector3d b,
			final Vector3d c, final Material mat,
			final Vector3d normal) {
		super(a, b, c, ColorGen.WHITE());
		this.material = mat;
		this.normal = normal;
	}

	/**
	 * returns a reference of the normal vector used for lighting calculation
	 * <br>
	 * if gouraud == true the result will be already interpolated for the
	 * current shading during the render-process
	 */
	@Override
	public Vector3d getNormal() {
		return normal;
	}

	/**
	 * @return true if the given AdvTriangle has an equal normal vector and
	 *         equal edges
	 * @see fast3d.renderables.Triangle#hasEqualEdges
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof AdvTriangle) {
			final AdvTriangle t = (AdvTriangle) obj;
			return normal.equals(t.normal)
					&& material.equals(t.material)
					&& super.hasEqualEdges(t);
		} else
			return false;
	}

	/**
	 * 
	 * @return fast3d.renderables.AdvTriangle[_parameter_]
	 **/
	public String toString() {
		return "fast3d.renderables.AdvTriangle[" + "edge1="
				+ getEdge1() + ";edge2=" + getEdge2() + ";edge3="
				+ getEdge3() + ";material=" + getMaterial()
				+ ";normal=" + getNormal() + ";currentVisibleColor="
				+ currentVisibleColor + "]";
	}

	private Vector2d as, bs, cs;
	private BufferedImage img;
	private int ox, oy;

	public void shade(final Graphics3d s) {
		if (s.getShader().cam.getLookDir()
				.angleTo(this.getNormal()) < MathUtil.piOver2) {
			img = null;
			return;
		}
		if (currentVisibleColor == null)
			currentVisibleColor = s.illuminateSurface(this);
		as = s.shade(this.a);
		bs = s.shade(this.b);
		cs = s.shade(this.c);
		if (as == null || bs == null || cs == null) {
			img = null;
			return;
		}
		final int sizex = (int) Math.max(Math.abs(as.x - bs.x), Math
				.max(Math.abs(as.x - cs.x), Math.abs(bs.x - cs.x)))
				+ 1;
		final int sizey = (int) Math.max(Math.abs(as.y - bs.y), Math
				.max(Math.abs(as.y - cs.y), Math.abs(bs.y - cs.y)))
				+ 1;
		ox = (int) Math.min(as.x, Math.min(bs.x, cs.x));
		oy = (int) Math.min(as.y, Math.min(bs.y, cs.y));
		final BufferedImage img2 = new BufferedImage(sizex, sizey,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = img2.createGraphics();
		g.translate(-ox, -oy);

		if (material.texture == null) {
			g.setColor(currentVisibleColor.awtColor());
			g.fillPolygon(
					new int[] { as.getX(), bs.getX(), cs.getX() },
					new int[] { as.getY(), bs.getY(), cs.getY() }, 3);
		} else {

			final Vector3d deltaN1 = na.to(nb);
			final Vector3d deltaN2 = na.to(nc);

			final Vector2d delta1 = as.to(bs);
			final Vector2d delta2 = as.to(cs);
			final Vector2d deltaT1 = a2.to(b2);
			final Vector2d deltaT2 = a2.to(c2);

			final double width = delta1.length();
			final double origHeight = delta2.length();
			double height = origHeight;
			final double heightDelta = height / width;

			delta2.normalize();
			delta1.normalize();

			deltaT1.scaleTo(deltaT1.length() / width);
			deltaT2.scaleTo(deltaT2.length() / origHeight);
			deltaN1.scaleTo(deltaN1.length() / width);
			deltaN2.scaleTo(deltaN2.length() / origHeight);

			final Vector2d texCoordSlider1 = a2.clone();
			final Vector2d sliderDelta1 = as.clone();
			Color pixelColor;
			for (int i = 0; i < width; i++) {
				final Vector2d pixel = sliderDelta1.clone();
				final Vector2d texCoord = texCoordSlider1.clone();
				for (int j = 0; j < height; j++) {
					pixelColor = material.texture
							.getAbsolute(texCoord);
					if (illuminateTexture)
						pixelColor.multiply(currentVisibleColor);
					g.setColor(pixelColor.awtColor());
					g.fillRect(pixel.getX(), pixel.getY(), 2, 2);

					pixel.add(delta2);
					texCoord.add(deltaT2);
				}
				sliderDelta1.add(delta1);
				texCoordSlider1.add(deltaT1);
				height -= heightDelta;
			}
		}

		g.dispose();
		img = img2;
	}

	public void render(final Graphics3d g) {
		if (img != null)
			g.getGraphics2d().drawImage(img, ox, oy, null);
	}

	/**
	 * sets the coloring-attributes as describes in the material<br>
	 * calls invalidateLight()
	 * 
	 * @see #invalidateLight()
	 * @param mat
	 *            the material holding the coloring-data
	 */
	public void setMaterial(final Material mat) {
		if (mat != null)
			this.material.setTo(mat);
		invalidateLight();
	}

	/**
	 * @return a reference to the used material with it's coloring-attributes
	 */
	@Override
	public Material getMaterial() {
		return material;
	}

	/**
	 * @return the current visible color
	 */
	@Override
	public Color getColor() {
		return currentVisibleColor;
	}

	/**
	 * invalidates the lighting calculation
	 * 
	 * @see #invalidateLight()
	 */
	@Override
	public void rot(final Vector3d axis, final double rad) {
		invalidateLight();
		super.rot(axis, rad);
	}

	/**
	 * invalidates the lighting calculation
	 * 
	 * @see #invalidateLight()
	 */
	@Override
	public void moveInAbsoluteDirection(Vector3d dir) {
		invalidateLight();
		super.moveInAbsoluteDirection(dir);
	}

	/**
	 * sets the current visible color to null so that it will be recalculated on
	 * the next render() call
	 */
	@Override
	public void invalidateLight() {
		currentVisibleColor = null;
	}

	/**
	 * sets the texture-mapping-coordinates to the given absolute ones<br>
	 * parameters not null
	 * 
	 * @see #setLogicalTextureCoordinates(Vector2d, Vector2d, Vector2d)
	 * @param a
	 *            absolute textureCoordinate for edge1
	 * @param b
	 *            absolute textureCoordinate for edge2
	 * @param c
	 *            absolute textureCoordinate for edge3
	 */
	public void setTextureCoordinates(final Vector2d a,
			final Vector2d b, final Vector2d c) {
		a2.set(a);
		b2.set(b);
		c2.set(c);
	}

	/**
	 * sets the texture-mapping-coordinates to the given logical ones<br>
	 * parameters not null
	 * 
	 * @see #setTextureCoordinates(Vector2d, Vector2d, Vector2d)
	 * @param a
	 *            logical textureCoordinate for edge1
	 * @param b
	 *            logical textureCoordinate for edge2
	 * @param c
	 *            logical textureCoordinate for edge3
	 * @return whether the logical coordinates were set, false if no texture is
	 *         set in the material
	 */
	public boolean setLogicalTextureCoordinates(Vector2d a,
			Vector2d b, Vector2d c) {
		if (material.texture != null) {
			a = a.clone();
			material.texture.toAbsoluteCoordinate(a);
			b = b.clone();
			material.texture.toAbsoluteCoordinate(b);
			c = c.clone();
			material.texture.toAbsoluteCoordinate(c);
			setTextureCoordinates(a, b, c);
			return true;
		} else
			return false;
	}

	@Override
	public void revalidateLight(Light... lights) {
		currentVisibleColor = Graphics3d.illuminateSurface(this,
				lights);
	}
}