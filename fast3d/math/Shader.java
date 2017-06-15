package fast3d.math;

import java.util.LinkedList;
import java.util.List;

/**
 * to shade means to calculate the screen-position and color of a 3d-point at a
 * specified camera-setting<br>
 * a shader is a converter from 3d to 2d
 * 
 * @author Tim Trense
 */

public class Shader {

	/**
	 * the screens width in pixel (the width of the physical device is used to
	 * determine the width of the resulting image)
	 */
	public final int screenWidthPX;
	/**
	 * the screens height in pixel (the height of the physical device is used to
	 * determine the width of the resulting image)
	 */
	public final int screenHeightPX;

	/**
	 * the camera used to render the scene from
	 */
	public final Camera cam;

	private final Vector3d orthoNormal;

	/**
	 * Wrapper for the constructor getting all the cameras vectors
	 * 
	 * @param cam
	 *            the vectors needed to shade
	 * @param sw
	 *            the screens width in pixel
	 * @param sh
	 *            the screens height in pixel
	 */
	public Shader(final Camera cam, final int sw, final int sh) {
		super();
		this.cam = cam;
		this.screenWidthPX = sw;
		this.screenHeightPX = sh;
		this.orthoNormal = cam.getLookDir();
	}

	/**
	 * @return an independent shader thats vectors and fields are all cloned too
	 */
	protected Shader clone() {
		return new Shader(cam, screenWidthPX, screenHeightPX);
	}

	/**
	 * should just be used to debug
	 */
	public String toString() {
		return "Shader[" + "cam=" + cam + ";screenWidth="
				+ screenWidthPX + "px" + ";screenHeight="
				+ screenHeightPX + "px" + "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given Shader are equal considering their
	 *         camera and mode settings and screen-size
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Shader) {
			final Shader other = (Shader) obj;
			return cam.equals(other.cam)
					&& screenWidthPX == other.screenWidthPX
					&& screenHeightPX == other.screenHeightPX;
		} else
			return false;
	}

	/**
	 * shades all vertices
	 * 
	 * @param vertices
	 *            all points in 3d-space to project on the virtual camera-screen
	 * @return all projected points on camera / screen which correspond to the
	 *         camera-mode-setting
	 */
	public List<Vector2d> shade(final List<Vector3d> vertices) {
		final List<Vector2d> erg = new LinkedList<Vector2d>();
		Vector2d help;
		for (Vector3d vertex : vertices) {
			help = shadeVertex(vertex);
			if (help != null)
				erg.add(help);
		}
		return erg;
	}

	/**
	 * shades all vertices
	 * 
	 * @param vertices
	 *            all points in 3d-space to project on the virtual camera-screen
	 * @return all projected points on camera / screen which correspond to the
	 *         camera-mode-setting
	 */
	public List<Vector2d> shade(final Vector3d... vertices) {
		final List<Vector2d> erg = new LinkedList<Vector2d>();
		Vector2d help;
		for (int i = 0; i < vertices.length; i++) {
			help = shadeVertex(vertices[i]);
			if (help != null)
				erg.add(help);
		}
		return erg;
	}

	private double[][] getShadingMatrix(final Vector3d vertex) {
		switch (cam.mode.viewmode) {
		case ORTHOGONAL:
			return new double[][] {
					{ cam.screenWidth.x, cam.screenHeight.x,
							orthoNormal.x,
							vertex.x - cam.screenOrigin.x },
					{ cam.screenWidth.y, cam.screenHeight.y,
							orthoNormal.y,
							vertex.y - cam.screenOrigin.y },
					{ cam.screenWidth.z, cam.screenHeight.z,
							orthoNormal.z,
							vertex.z - cam.screenOrigin.z } };
		case PERSPECTIVE: // fall-through
		default: // if null
			return new double[][] {
					{ cam.screenWidth.x, cam.screenHeight.x,
							vertex.x - cam.pos.x,
							vertex.x - cam.screenOrigin.x },
					{ cam.screenWidth.y, cam.screenHeight.y,
							vertex.y - cam.pos.y,
							vertex.y - cam.screenOrigin.y },
					{ cam.screenWidth.z, cam.screenHeight.z,
							vertex.z - cam.pos.z,
							vertex.z - cam.screenOrigin.z } };
		}
	}

	/**
	 * shades a single vertex<br>
	 * returns null if the vertex is not visible on the screen (does not
	 * correspond to the camera-mode-setting)
	 * 
	 * @param vertex
	 *            a 3d-point in space to project on the cameras screen
	 * @return the screen-projection-position of the vertex
	 */
	public Vector2d shadeVertex(final Vector3d vertex) {
		// shading matrix
		final double[][] matrix = getShadingMatrix(vertex);

		final double[] result = MatrixCalculation
				.solveLinearEquationSystem(matrix);

		if (cam.mode.viewmode == Viewmode.ORTHOGONAL) {
			if (cam.mode.oriented && result[2] < 0)
				return null;
			// notinscreen not available in orthographic shading
		} else {
			// check whether the distance value is in [0;1]
			// <0 (vertex between camera-position and camera-screen) and
			// >1 (vertex behind the camera-position)
			if (cam.mode.notincam && result[2] < 0)
				return null;
			if (cam.mode.oriented && result[2] > 1)
				return null;
		}

		if (cam.mode.inscreen) {
			// check result-x and -y are in bound of the screen
			if (result[0] < 0 || result[0] > 1)
				return null;
			if (result[1] < 0 || result[1] > 1)
				return null;
		}

		return new Vector2d(result[0] * screenWidthPX,
				result[1] * screenHeightPX);
	}

	/**
	 * shades all vertices <br>
	 * returns a pixel coordinate for every given vertex
	 * 
	 * @param vertices
	 *            all points in 3d-space to project on the virtual camera-screen
	 * @return all projected points on camera / screen which were in AND
	 *         OUT-bound of the screen (may be used for HUDs), correctly
	 *         oriented OR NOT
	 */
	public List<Vector2d> shadeIgnoreMode(
			final List<Vector3d> vertices) {
		final List<Vector2d> erg = new LinkedList<Vector2d>();
		Vector2d help;
		for (Vector3d vertex : vertices) {
			help = shadeVertexIgnoreMode(vertex);
			erg.add(help);
		}
		return erg;
	}

	/**
	 * shades all vertices <br>
	 * returns a pixel coordinate for every given vertex
	 * 
	 * @param vertices
	 *            all points in 3d-space to project on the virtual camera-screen
	 * @return all projected points on camera / screen which were in AND
	 *         OUT-bound of the screen (may be used for HUDs), correctly
	 *         oriented OR NOT
	 */
	public List<Vector2d> shadeIgnoreMode(
			final Vector3d... vertices) {
		final List<Vector2d> erg = new LinkedList<Vector2d>();
		Vector2d help;
		for (int i = 0; i < vertices.length; i++) {
			help = shadeVertexIgnoreMode(vertices[i]);
			erg.add(help);
		}
		return erg;
	}

	/**
	 * shades a single vertex<br>
	 * returns the screen-projection-position of it even if the vertex is out of
	 * bounds of the screen or false oriented <br>
	 * returns never null
	 * 
	 * @param vertex
	 *            3d point in space to project on the boundless virtual
	 *            camera-screen
	 * @return the screen-projection-position of the vertex
	 */
	public Vector2d shadeVertexIgnoreMode(final Vector3d vertex) {
		final double[][] matrix = getShadingMatrix(vertex);

		final double[] result = MatrixCalculation
				.solveLinearEquationSystem(matrix);

		return new Vector2d(result[0] * screenWidthPX,
				result[1] * screenHeightPX);
	}
}