package fast3d.renderables;

import java.awt.Font;

import fast3d.Renderable;
import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.util.ColorGen;

/**
 * a callout is a 2d-text floating on a 3d-location, facing the camera, heading
 * up <br>
 * the callout uses a built-in font class
 * 
 * @author Tim Trense
 */
public class Callout implements Renderable {

	/**
	 * the class to represent the awt-font but with further information
	 * 
	 * @author Tim Trense
	 */
	public static class Text {

		/**
		 * the actual text
		 */
		public final String text;
		/**
		 * the awt-font name
		 */
		public final String fontname;
		/**
		 * the size of the text in the 3d-space-length-units
		 */
		public final double size3d;
		/**
		 * the modifiers passed to the java.awt.Font-constructor
		 */
		public final int awt_modifiers; // bitmask
		/**
		 * a field to store the color of the text
		 */
		public final Color color;

		/**
		 * @param text
		 *            the actual text-field value
		 * @param fontname
		 *            the name-field value
		 * @param size
		 *            the size3d-field value
		 * @param color
		 *            the color-field value
		 * @param awt_mod
		 *            the awt_modifiers-field values combined bitwise or to
		 *            construct the awt-modifier-bitmask (if not given
		 *            java.awt.Font.PLAIN used)
		 */
		public Text(final String text, final String fontname,
				final double size, final Color color,
				final int... awt_mod) {
			super();
			this.text = text;
			this.color = color;
			this.fontname = fontname;
			this.size3d = size;
			if (awt_mod != null && awt_mod.length > 0) {
				int mod = awt_mod[0];
				for (int i = 1; i < awt_mod.length; i++)
					mod = mod | awt_mod[i]; // bitwise or
				this.awt_modifiers = mod;
			} else
				this.awt_modifiers = java.awt.Font.PLAIN;

		}

		/**
		 * a default constructor<br>
		 * fontname is Arial<br>
		 * size3d is 1d<br>
		 * color is ColorGen.WHITE<br>
		 * awt_modifiers is java.awt.Font.PLAIN
		 * 
		 * @param text
		 *            the actual text-field value
		 */
		public Text(final String text) {
			this(text, "Arial", 1, ColorGen.WHITE());
		}

		/**
		 * a default constructor<br>
		 * fontname is Arial
		 * 
		 * @param text
		 *            the actual text-field value
		 * @param size
		 *            the size3d-field value
		 * @param color
		 *            the color-field value
		 * @param awt_mod
		 *            the awt_modifiers-field values combined bitwise or to
		 *            construct the awt-modifier-bitmask (if not given
		 *            java.awt.Font.PLAIN used)
		 */
		public Text(final String text, final double size,
				final Color color, final int... awt_mod) {
			this(text, "Arial", size, color, awt_mod);
		}

		/**
		 * a default constructor<br>
		 * fontname is Arial<br>
		 * color is ColorGen.WHITE
		 * 
		 * @param text
		 *            the actual text-field value
		 * @param size
		 *            the size3d-field value
		 * @param awt_mod
		 *            the awt_modifiers-field values combined bitwise or to
		 *            construct the awt-modifier-bitmask (if not given
		 *            java.awt.Font.PLAIN used)
		 */
		public Text(final String text, final double size,
				final int... awt_mod) {
			this(text, "Arial", size, ColorGen.WHITE(), awt_mod);
		}

		/**
		 * a default constructor<br>
		 * fontname is Arial<br>
		 * awt_mod is java.awt.Font.PLAIN
		 * 
		 * @param text
		 *            the actual text-field value
		 * @param size
		 *            the size3d-field value
		 * @param color
		 *            the color-field value
		 */
		public Text(final String text, final double size,
				final Color color) {
			this(text, "Arial", size, color, java.awt.Font.PLAIN);
		}

		/**
		 * @param sizePX
		 *            the awt-pixelwise-size
		 * @return the awt-font with the given pixel-size
		 */
		public java.awt.Font getAwt3d(final double sizePX) {
			return new java.awt.Font(fontname, awt_modifiers,
					(int) sizePX);
		}

		/**
		 * @return an independent version of this with all fields cloned
		 */
		@Override
		public Text clone() {
			return new Text(new String(text), new String(fontname),
					size3d, color.clone(), awt_modifiers);
		}

		/**
		 * @return true if all fields of the given Font are equal
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof Text) {
				final Text f = (Text) obj;
				return text.equals(f.text)
						&& fontname.equals(f.fontname)
						&& size3d == f.size3d
						&& awt_modifiers == f.awt_modifiers
						&& color.equals(f.color);
			} else
				return false;
		}

		@Override
		public String toString() {
			return "Font[text=" + text + ";fontname=" + fontname
					+ ";size3d=" + size3d + ";color=" + color
					+ ";awt_mod=" + awt_modifiers + "]";
		}

	}

	private final Vector3d pos;
	private Text text;

	/**
	 * constructs a new callout
	 * 
	 * @param text
	 *            the callout-text
	 * @param pos
	 *            the lower-left corner of the text
	 */
	public Callout(final Text text, final Vector3d pos) {
		this.text = text;
		this.pos = pos;
	}

	/**
	 * constructs a new callout on defaultFont
	 * 
	 * @param text
	 *            the callout-text
	 * @param pos
	 *            the lower-left corner of the text
	 */
	public Callout(final String text, final Vector3d pos) {
		this(new Text(text), pos);
	}

	@Override
	protected Callout clone() {
		return new Callout(text.clone(), pos.clone());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Callout) {
			final Callout call = (Callout) obj;
			return call.text.equals(this.text)
					&& call.pos.equals(this.pos);
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Callout[\"" + text + "\";pos=" + pos + "]";
	}

	private Vector2d pos2;
	private Font font2;

	public void shade(final Graphics3d g) {
		final Vector3d _top = g.getShader().cam.getScreenHeight()
				.clone().scaleTo(text.size3d).add(pos);
		pos2 = g.shade(this.pos);
		Vector2d top2 = g.shade(_top);
		if (pos2 == null || top2 == null)
			return;
		final double size = pos2.distanceTo(top2);
		font2 = text.getAwt3d(size);
	}

	public void render(final Graphics3d g) {
		g.setColor(text.color);
		g.setFont(font2);
		g.drawString(text.text, pos);
	}

	@Override
	public Vector3d getPos() {
		return pos;
	}

	/**
	 * @return an independent point on the location of this with the color of
	 *         this
	 */
	public Pixel getPosVector3d() {
		return new Pixel((Vector3d) getPos().clone(),
				getColor().clone());
	}

	/**
	 * adds the given vector to the position
	 * 
	 * @param dir
	 *            the translation vector
	 */
	public void moveInAbsoluteDirection(final Vector3d dir) {
		pos.add(dir);
	}

	/**
	 * moves the callout so that it's position will be at the target
	 * 
	 * @param target
	 *            the new callout's position
	 */
	public void moveTo(final Vector3d target) {
		pos.set(target);
	}

	/**
	 * rayTrace is not supported for callouts
	 * 
	 * @return null
	 */
	@Override
	public Vector3d rayTrace(Vector3d s, Vector3d r) {
		return null;
	}

	/**
	 * @return the callouts position
	 */
	@Override
	public Vector3d[] getVertices() {
		return new Vector3d[] { pos };
	}

	@Override
	public Color getColor() {
		return text.color;
	}
}