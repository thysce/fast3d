package _tutorial.src.Fast3dTutorial.example4;

import fast3d.graphics.Graphics3d;
import fast3d.math.Vector3d;
import fast3d.simple.SimplePanel3d;
import fast3d.simple.controls.RotationControl;
import fast3d.util.ColorGen;

public class Example4 extends SimplePanel3d {

	/**
	 * SimplePanel3d extends Panel3d, which extends JPanel, which is
	 * Serializable
	 */
	private static final long serialVersionUID = -5439376853579436655L;

	public static void main(String[] args) {

		final Example4 e4 = new Example4();

		final RotationControl c = new RotationControl(e4);
		c.allowNegativeRotX = true;
		c.allowPositiveRotX = true;
		c.activate();
		e4.setControl(c);
		e4.frameRate().setFPS(20);

		e4.showInFrame(true);

	}

	private final Vector3d base = Vector3d.zero();

	@Override
	public void render(final Graphics3d g) {
		final double length = 1;
		final double minLength = .1;
		g.setColor(ColorGen.YELLOW());
		renderTree(base, g, length, minLength);
	}

	private void renderTree(Vector3d b, Graphics3d g, double length,
			double minLength) {

		final Vector3d t = b.clone();
		g.line(b, t.add(Vector3d.up().scaleTo(length)));

		length /= 1.5;
		if (length > minLength) {
			final Vector3d t2 = t.clone()
					.add(Vector3d.up().scaleTo(length));
			final Vector3d b1 = t2.clone()
					.add(Vector3d.right().scaleTo(length));
			final Vector3d b2 = t2.clone()
					.add(Vector3d.right().invert().scaleTo(length));
			final Vector3d b3 = t2.clone()
					.add(Vector3d.forward().scaleTo(length));
			final Vector3d b4 = t2.clone()
					.add(Vector3d.forward().scaleTo(-length));
			// scaleTo(-x) == invert().scaleTo(x)

			renderTree(b1, g, length, minLength);
			g.line(t, b1);
			renderTree(b2, g, length, minLength);
			g.line(t, b2);
			renderTree(b3, g, length, minLength);
			g.line(t, b3);
			renderTree(b4, g, length, minLength);
			g.line(t, b4);
		}
	}

}
