package fast3d.renderables;

import fast3d.Renderable;
import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.Vector3d;

/**
 * a base class to draw on 2d over the 3d-scene<br>
 * a HeadUpDisplay is not ray-traceable <br>
 * a basic HUDs 3d-location given by getPos() is at the cameras location
 * instantly after any render call <br>
 * a basic HUD returns null for its color <br>
 * a HUD do not block any events on the 3d-scenery (used by controls for
 * instance) - it is only a class to represent something visually on the screen,
 * but not "physically" in the universe
 * 
 * @author Tim Trense
 */
public abstract class HUD implements Renderable {

	private final Vector3d lastcampos = Vector3d.zero();

	@Override
	public Vector3d[] getVertices() {
		return null;
	}

	@Override
	public void shade(final Graphics3d g) {
		lastcampos.set(g.getShader().cam.getPos());
	}
	
	@Override
	public abstract void render(final Graphics3d g);

	/**
	 * @return null
	 */
	@Override
	public Color getColor() {
		return null;
	}

	/**
	 * @return null
	 */
	@Override
	public Vector3d rayTrace(Vector3d s, Vector3d r) {
		return null;
	}

	@Override
	public Vector3d getPos() {
		return lastcampos;
	}

}
