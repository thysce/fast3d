package fast3d.complex.light;

import fast3d.graphics.Color;
import fast3d.graphics.Texture;

/**
 * combines all data used to render under light
 * 
 * @author Tim Trense
 */
public class Material extends java.lang.Object {

	/**
	 * the color used to illuminate with ambient light (background-light)
	 */
	public final Color ambient;
	/**
	 * the color used to illuminate with diffuse light (directional/point-light)
	 */
	public final Color diffuse;
	/**
	 * the color used to illuminate with specular light (shine-light)
	 */
	public final Color specular;
	/**
	 * the factor how wide the shine-dot of the shine-light is
	 */
	public double shininess;
	/**
	 * the color that is visible at minimum even if no light source shines to
	 * the surfaces with this material
	 */
	public final Color emissive;

	/**
	 * the alpha-component of the colors is not respected due to illumination
	 * has to be on solid surfaces - this alpha is applied to the visible color
	 * as result of the lighting-calculation<br>
	 * opaque is 1d, transparent is 0d
	 */
	public double alpha = 1;

	/**
	 * if not null an AdvTriangle has a lighted texture on it
	 */
	public Texture texture;

	/**
	 * all colors will be white, shininess=1, alpha=1, no texture
	 */
	public Material() {
		this(new Color(1, 1, 1), new Color(1, 1, 1),
				new Color(1, 1, 1), new Color(1, 1, 1), 1, null);
	}

	/**
	 * initiates the materials data
	 * 
	 * @param ambient
	 *            the ambient-field value
	 * @param diffuse
	 *            the diffuse-field value
	 * @param specular
	 *            the specular-field value
	 * @param emissive
	 *            the emissive-field value
	 * @param shininess
	 *            the shininess-field value
	 * @param texture
	 *            the texture-field value
	 */
	public Material(final Color ambient, final Color diffuse,
			final Color specular, final Color emissive,
			final double shininess, final Texture texture) {
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.emissive = emissive;
		this.shininess = shininess;
		this.texture = texture;
	}

	/**
	 * initiates the materials data
	 * 
	 * @param ambient
	 *            the ambient-field value
	 * @param diffuse
	 *            the diffuse-field value
	 * @param specular
	 *            the specular-field value
	 * @param emissive
	 *            the emissive-field value
	 * @param shininess
	 *            the shininess-field value
	 * @param alpha
	 *            the alpha-field value
	 * @param texture
	 *            the texture-field value
	 */
	public Material(final Color ambient, final Color diffuse,
			final Color specular, final Color emissive,
			final double shininess, final double alpha,
			final Texture texture) {
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.emissive = emissive;
		this.shininess = shininess;
		this.alpha = alpha;
		this.texture = texture;
	}

	/**
	 * initiates the materials data, opaque, no texture
	 * 
	 * @param ambient
	 *            the ambient-field value
	 * @param diffuse
	 *            the diffuse-field value
	 * @param specular
	 *            the specular-field value
	 * @param emissive
	 *            the emissive-field value
	 * @param shininess
	 *            the shininess-field value
	 */
	public Material(Color ambient, Color diffuse, Color specular,
			Color emissive, double shininess) {
		this(ambient, diffuse, specular, emissive, shininess, null);
	}

	/**
	 * initiates the materials data, no texture
	 * 
	 * @param ambient
	 *            the ambient-field value
	 * @param diffuse
	 *            the diffuse-field value
	 * @param specular
	 *            the specular-field value
	 * @param emissive
	 *            the emissive-field value
	 * @param shininess
	 *            the shininess-field value
	 * @param alpha
	 *            the alpha-field value
	 */
	public Material(Color ambient, Color diffuse, Color specular,
			Color emissive, double shininess, double alpha) {
		this(ambient, diffuse, specular, emissive, shininess,
				alpha, null);
	}

	/**
	 * @return the shininess-field value
	 */
	public double getShininess() {
		return shininess;
	}

	/**
	 * @return the alpha-field value
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @return a clone of the ambient-field value
	 */
	public Color getAmbient() {
		return ambient.clone();
	}

	/**
	 * @return a clone of the diffuse-field value
	 */
	public Color getDiffuse() {
		return diffuse.clone();
	}

	/**
	 * @return a clone of the specular-field value
	 */
	public Color getSpecular() {
		return specular.clone();
	}

	/**
	 * @return a clone of the emissive-field value
	 */
	public Color getEmissive() {
		return emissive.clone();
	}

	/**
	 * clones the field data
	 */
	@Override
	public Material clone() {
		return new Material(ambient.clone(), diffuse.clone(),
				specular.clone(), emissive.clone(), shininess, alpha,
				texture);
	}

	/**
	 * @return true if the given Material has equal field data
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Material) {
			final Material m = (Material) obj;
			return ambient.equals(m.ambient)
					&& diffuse.equals(m.diffuse)
					&& specular.equals(m.specular)
					&& emissive.equals(m.emissive)
					&& shininess == m.shininess && alpha == m.alpha
					&& (texture == null ? m.texture == null
							: texture.equals(m.texture));
		} else
			return false;
	}

	/**
	 * 
	 * @return fast3d.complex.light.Material[_parameter_]
	 **/
	@Override
	public String toString() {
		return "fast3d.complex.light.Material[ambient=" + ambient
				+ ";diffuse=" + diffuse + ";specular=" + specular
				+ ";emissive=" + emissive + ";shininess=" + shininess
				+ ";a=" + alpha + ";texture=" + texture + "]";
	}

	/**
	 * copies the field-data of the parameter to this
	 * 
	 * @param mat
	 *            the new field-data
	 */
	public void setTo(final Material mat) {
		if (mat == null)
			return;
		if (mat.ambient != null)
			this.ambient.set(mat.ambient);
		if (mat.diffuse != null)
			this.diffuse.set(mat.diffuse);
		if (mat.specular != null)
			this.specular.set(mat.specular);
		if (mat.emissive != null)
			this.emissive.set(mat.emissive);
		this.texture = mat.texture;
		this.shininess = mat.shininess;
		this.alpha = mat.alpha;
	}
}