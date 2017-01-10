package example9;

import fast3d.complex.light.Material;
import fast3d.graphics.Texture;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.simple.SimplePanel3d;
import fast3d.util.ColorGen;

public class TextureTriangleExample {

	public static void main(String[] args) {
		final SimplePanel3d p3d = new SimplePanel3d();

		final Texture texture = Texture.load("mytext.png");
		// check whether load() was successful
		if (texture == null)
			return;

		final Vector3d a = new Vector3d(-1, -1, 0); // bottom left in 3d space
		final Vector3d b = new Vector3d(1, -1, 0); // bottom right in 3d space
		final Vector3d c = new Vector3d(0, 1, 0); // top middle in 3d space

		final Material mat = new Material(ColorGen.GRAY(),
				ColorGen.WHITE(), ColorGen.CORNFLOWER_BLUE(),
				ColorGen.DARK_GRAY(), 25, texture);

		final AdvTriangle tt = new AdvTriangle(a, b, c, mat);
		tt.setLogicalTextureCoordinates(new Vector2d(0, 1), // bottom left on
															// the texture
				new Vector2d(1, 1), // bottom right on the texture
				new Vector2d(0.5, 0) // middle top on the texture
		);
		tt.illuminateTexture = false; // deactivate shadowing on this triangle
										// only if it has a texture

		p3d.getUni().add(tt);

		p3d.getCam().setNominalView();
		p3d.showInFrame(true);
	}

}
