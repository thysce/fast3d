package example3;

import fast3d.Renderable;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.math.Camera;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.simple.SimplePanel3d;
import fast3d.util.ColorGen;
import fast3d.util.click.ClickMode;

public class Example0 {

	public static void main(String[] args) {
		final SimplePanel3d p3d = new SimplePanel3d();

		p3d.setSize(800, 600);
		p3d.showInFrame(true);

		final Camera cam = p3d.getCam(); // short for p3d.getUniverse().getCam()
		final Vector3d position = new Vector3d(0, 0, 5);
		cam.moveTo(position);
		final Vector3d upVector = Vector3d.up();
		final Vector3d lookDirection = Vector3d.forward();
		cam.lookInDirection(lookDirection, upVector);

		final Material surface = new Material(new Color(.9, .8, .1, .5),
				ColorGen.CORNFLOWER_BLUE(), new Color(.5, .5, .9), ColorGen.BLACK(), 25);
		final Vector3d edge1 = new Vector3d(-1, 1, 0);
		final Vector3d edge2 = new Vector3d(1, 1, 0);
		final Vector3d edge3 = new Vector3d(0, -1, .3);

		final AdvTriangle triang = new AdvTriangle(edge1, edge2, edge3, surface);
		triang.getNormal().invert();
		p3d.getUniverse().add(triang);

		p3d.getUni().enableDefaultLighting();
		p3d.repaint();

		p3d.setClickListener((maybeClicked) -> {
			final Renderable clicked = maybeClicked[0];
			System.out.println(clicked);
		}, ClickMode.ON_MOUSE_POS);
	}

}