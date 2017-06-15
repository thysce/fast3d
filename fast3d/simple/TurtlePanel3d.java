package fast3d.simple;

import java.awt.Graphics2D;

import fast3d.graphics.Graphics3d;
import fast3d.graphics.Turtle3d;
import fast3d.math.Shader;

/**
 * a Panel3d using Turtle3d-graphics to render a scenery<br>
 * render(Turtle3d) is called after rendering the universe as usual
 * 
 * @author Tim Trense
 */
@SuppressWarnings("serial")
public abstract class TurtlePanel3d extends SimplePanel3d {

	@Override
	protected Turtle3d createGraphics3d(final Graphics2D g2d) {
		final Turtle3d g3d = new Turtle3d(g2d,
				new Shader(getUniverse().getCam(), getWidth(),
						getHeight()),
				getUniverse().getLights());
		return g3d;
	}

	@Override
	public void render(final Graphics3d g3d) {
		super.render(g3d);
		render((Turtle3d) g3d);
	}

	/**
	 * do your turtle-graphics here<br>
	 * you can of cause use the turtle as a normal Graphics3d<br>
	 * turtle-own methods start with the character t
	 * 
	 * @param t3d
	 *            the Turtle to use to draw
	 */
	public abstract void render(final Turtle3d t3d);
}
