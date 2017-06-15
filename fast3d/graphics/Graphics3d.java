package fast3d.graphics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fast3d.Renderable;
import fast3d.complex.RenderAction;
import fast3d.complex.light.Illuminatable;
import fast3d.complex.light.Light;
import fast3d.complex.light.Material;
import fast3d.math.Shader;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.util.ColorGen;
import fast3d.util.Sort;

/**
 * with a graphics3d you can draw directly onto a regular 2d-screen but in 3d
 * 
 * @author Tim Trense
 */
public class Graphics3d {

	private final Graphics2D g;
	private final Shader sh;
	private final Light[] lights;
	private final byte frameID;
	private static byte nextFrameID = 0;

	/**
	 * constructs a graphics3d-wrapper around a 2d-graphics-context with a
	 * shader to convert 3d in 2d
	 * 
	 * @param g
	 *            the 2d-graphics context to draw on
	 * @param sh
	 *            the 3d/2d-converter
	 * @param lights
	 *            all used lights to illuminate the scenery
	 */
	public Graphics3d(final Graphics2D g, final Shader sh,
			final Light... lights) {
		this.g = g;
		this.sh = sh;
		this.lights = lights;
		this.frameID = nextFrameID++;
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given Graphics3d are equal considering their
	 *         wireFrameMode-state, backing Graphics2D, backing Shader, used
	 *         lights and frameID
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Graphics3d) {
			final Graphics3d other = (Graphics3d) obj;
			return frameID == other.frameID && g.equals(other.g)
					&& sh.equals(other.sh)
					&& lights.equals(other.lights);
		} else
			return false;
	}

	/**
	 * it is highly recommended to not access the shader directly<br>
	 * you may get some camera-information out of it but changing the internal
	 * data will probably crash the render-process
	 * 
	 * @return the shader
	 */
	public Shader getShader() {
		return sh;
	}

	/**
	 * 
	 * @return fast3d.graphics.Graphics3d[_parameter_]
	 **/
	public String toString() {
		return "fast3d.graphics.Graphics3d[" + "frameID=" + frameID
				+ ";g2D=" + g + ";shader=" + sh + ";lights=" + lights
				+ "]";
	}

	/**
	 * converts all given vertices in pixel-coordinates <br>
	 * faster than getShader().shade(vertices) because of buffering-features
	 * <br>
	 * if a vertex is shaded to null according to the camera-mode-settings, it
	 * won't be added to the return-list
	 * 
	 * @param vecs
	 *            all vertices to shade
	 * @return all not-null shaded pixel coordinates of the vertices
	 */
	public List<Vector2d> shadeAll(final Vector3d... vecs) {
		final List<Vector2d> shaded = new ArrayList<Vector2d>();
		Vector2d current;
		for (Vector3d v : vecs) {
			current = shade(v);
			if (current != null)
				shaded.add(current);
		}
		return shaded;
	}

	/**
	 * converts one given vertex in pixel-coordinates <br>
	 * faster than getShader().shade(vertices) because of buffering-features
	 * <br>
	 * if a vertex is shaded to null according to the camera-mode-settings, this
	 * method will return null too
	 * 
	 * @param v
	 *            the vertex to shade
	 * @return its corresponding pixel-coordinates according to the current
	 *         camera setting
	 */
	public Vector2d shade(final Vector3d v) {
		if (v.shadedFrameID == frameID && v.shaded != null)
			return v.shaded;
		else {
			v.shadedFrameID = frameID;
			return v.shaded = sh.shadeVertex(v);
		}
	}

	/**
	 * converts the given color to the corresponding one from awt and sets the
	 * backing graphics2D to that
	 * 
	 * @param col
	 *            the color to draw in
	 */
	public void setColor(final Color col) {
		g.setColor(col.awtColor());
	}

	/**
	 * shades the given vertex and sets the pixel to the previous set color by
	 * setColor(Color) <br>
	 * works like drawing a 3d-pixel
	 * 
	 * @param pos
	 *            the vertex to color
	 */
	public void pixel(final Vector3d pos) {
		final Vector2d p = shade(pos);
		if (p != null)
			g.fillRect(p.getX(), p.getY(), 1, 1);
	}

	/**
	 * shades the given vertex and sets the pixel to the previous set color by
	 * setColor(Color) <br>
	 * works like drawing a 3d-pixel
	 * 
	 * @param pos
	 *            the vertex to color
	 * @param width
	 *            the size of the pixel
	 */
	public void pixel(final Vector3d pos, final int width) {
		final Vector2d p = shade(pos);
		if (p != null)
			g.fillRect(p.getX(), p.getY(), width, width);
	}

	/**
	 * shades the vertices and draws a line from one of these pixels to the
	 * other <br>
	 * works like drawing a 3d-line
	 * 
	 * @param start
	 *            one of the two vertices
	 * @param end
	 *            the other vertex
	 */
	public void line(final Vector3d start, final Vector3d end) {
		final Vector2d s = shade(start);
		if (s == null)
			return;
		final Vector2d e = shade(end);
		if (e == null)
			return;
		g.drawLine(s.getX(), s.getY(), e.getX(), e.getY());
	}

	/**
	 * shades all vertices using shadeAll() and draws a polygon over these
	 * pixels (filled if wireFrameMode is false) <br>
	 * works like drawing a 3d-polygon (should be not too big in complex
	 * sceneries) if wireframeMode is on(true), a polygon is just a polyline
	 * where the first and last point are equal
	 * 
	 * @see #polyline(Vector3d...)
	 * @param ps
	 *            the edges of the polygon
	 */
	public void polygon(final Vector3d... ps) {
		final List<Vector2d> p2s = shadeAll(ps);
		final int count = p2s.size();
		final int[] x = new int[count];
		final int[] y = new int[count];
		int i = 0;
		for (Vector2d v : p2s) {
			x[i] = v.getX();
			y[i] = v.getY();
			i++;
		}
		if (sh.cam.mode.wireframe)
			g.drawPolygon(x, y, count);
		else
			g.fillPolygon(x, y, count);
	}

	/**
	 * shades all vertices using shadeAll() and draws a polyline over these<br>
	 * works like drawing a 3d-polyline (should be not too big in complex
	 * sceneries)<br>
	 * if wireframeMode is on(true), a polygon is just a polyline where the
	 * first and last point are equal
	 * 
	 * @see #polygon(Vector3d...)
	 * 
	 * @param ps
	 *            the points of the line
	 */
	public void polyline(final Vector3d... ps) {
		final List<Vector2d> p2s = shadeAll(ps);
		final int count = p2s.size();
		final int[] x = new int[count];
		final int[] y = new int[count];
		int i = 0;
		for (Vector2d v : p2s) {
			x[i] = v.getX();
			y[i] = v.getY();
			i++;
		}

		g.drawPolyline(x, y, count);
	}

	/**
	 * sorts the given array very fast but correct to draw the renderables then
	 * in the order of the array, so that they will hide each other in the
	 * perspectively correct order <br>
	 * the nearest to the camera renderable will be at the last index of the
	 * array, the most far one at first
	 * 
	 * @param r
	 *            the scenery to be calculated
	 */
	public void sort(final Renderable... r) {
		Sort.Quicksort.Renderables.sort(sh.cam.getPos(), r);
	}

	/**
	 * sorts the given keys in relation to their position to the camera and then
	 * calls the run method for the corresponding value so it can draw something
	 * 
	 * @param call
	 *            a mapping of where to render what
	 */
	public void sort(final Map<Vector3d, RenderAction> call) {
		final Vector3d[] keys = (Vector3d[]) call.keySet().toArray();
		Sort.Quicksort.Vectors.sort(sh.cam.getPos(), keys);
		for (Vector3d v : keys)
			call.get(v).render(v, this);
	}

	/**
	 * shades the given vertex and uses the pixel to determine where to draw a
	 * 2d-string on the screen <br>
	 * works just fine for call-outs, remember the text will be not 3d
	 * 
	 * @param text
	 *            the string to draw
	 * @param pos
	 *            the vertex determining the screen pixel-position where to draw
	 */
	public void drawString(final String text, final Vector3d pos) {
		final Vector2d v = shade(pos);
		if (v != null)
			g.drawString(text, v.getX(), v.getY());
	}

	/**
	 * does the lighting calculation for any used light and combines them to the
	 * visible color for the given surface
	 * 
	 * @param illum
	 *            the surface to do the lighting-calculation for
	 * @return the visible color of the surface in the current scenery-setting
	 */
	public Color illuminateSurface(final Illuminatable illum) {
		return illuminateSurface(illum, lights);
	}

	/**
	 * does the lighting calculation for any given light and combines them to
	 * the visible color for the given surface
	 * 
	 * @param illum
	 *            the surface to do the lighting-calculation for
	 * @param lights
	 *            the lights effecting the surface
	 * @return the visible color of the surface in the current scenery-setting
	 */
	public static Color illuminateSurface(final Illuminatable illum,
			final Light... lights) {
		final List<Color> cols = new LinkedList<Color>();
		for (Light light : lights)
			cols.add(light.illuminate(illum));
		final Material mat = illum.getMaterial();
		Color col = (mat != null) ? mat.getEmissive() : null;
		if (col == null)
			col = ColorGen.TRANSPARENT_BLACK();
		for (Color c : cols) {
			if (c == null) {
				continue;
			}
			col.add(c);
		}
		col.constrain(0, 1);
		col.a = mat.alpha;
		return col;
	}

	/**
	 * @return the backing 2d-awt-graphics-context as a reference to draw in 2d
	 *         over the scenery
	 */
	public Graphics2D getGraphics2d() {
		return g;
	}

	/**
	 * sets the font for the contained 2d-graphics<br>
	 * equal to getGraphics2d().setFont(Font)
	 * @param font the font to set
	 */
	public void setFont(final Font font) {
		getGraphics2d().setFont(font);
	}

}