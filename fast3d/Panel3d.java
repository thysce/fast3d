package fast3d;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import fast3d.complex.Universe;
import fast3d.graphics.Graphics3d;
import fast3d.math.Camera;
import fast3d.math.Shader;
import fast3d.math.Vector3d;
import fast3d.math.Viewmode;
import fast3d.control.Control;

/**
 * a swing component to display a 3d-scene, containing a universe<br>
 * subclasses are permitted the possibility to override the render()-method to
 * draw directly into the 3d-scenery or to override the preRender()-method to
 * prepare all data to be draw (for instance apply camera-movement or
 * transformations of the scenery)<br>
 * applies the screens aspect ratio automatically to the camera
 * <p>
 * <i>HUDs- Head-up-Displays and 2d GUI components:</i><br>
 * 
 * @see fast3d.renderables.HUD
 *      <ul>
 *      <li>you can add static HUDs as a renderable to the universe<br>
 *      <li>you can add interactive HUDs as AWT/swing components to the Panel3d
 *      <br>
 *      </ul>
 *      both types are rendered on top (over) the 3d-scenery and will not effect
 *      user-controls which react only on events just on the Panel3d but not on
 *      the GUI-components or the renderable-HUDs
 * 
 * @author Tim Trense
 */
@SuppressWarnings("serial")
public class Panel3d extends JComponent {

	private Control control;
	private final Universe uni;

	/**
	 * default settings:<br>
	 * <ul>
	 * <li>background black
	 * <li>foreground white
	 * <li>double buffered disabled
	 * <li>focusable
	 * </ul>
	 * 
	 * @param uni
	 *            the universe containing all the data to display
	 */
	public Panel3d(final Universe uni) {
		super();
		this.uni = uni;
		setBackground(java.awt.Color.BLACK);
		setForeground(java.awt.Color.WHITE);
		setDoubleBuffered(false);
		setFocusable(true);
	}

	/**
	 * @return the universe containing all the 3d-objects
	 */
	public Universe getUniverse() {
		return uni;
	}

	@Override
	public void paintComponent(final Graphics g) {
		preRender();
		final Graphics2D g2d = (Graphics2D) g;

		final double width = getWidth();
		final double height = getHeight();

		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, (int) width, (int) height);
		g2d.setColor(getForeground());
		super.paintComponent(g2d);

		uni.getCam().applyAspectRatioWpH(width / height);

		final Graphics3d g3d = createGraphics3d(g2d);
		if (g3d != null)
			render(g3d);
		super.paintComponents(g2d);
	}

	/**
	 * subclasses may override this method to adjust the Graphics3d passed to
	 * render(Graphics3d) or give in a subclass of Graphics3d
	 * 
	 * @param g2d
	 *            the backing Graphics2D
	 * @return a blank Graphics3d for the current frame to draw
	 */
	protected Graphics3d createGraphics3d(final Graphics2D g2d) {
		final Graphics3d g3d = new Graphics3d(g2d,
				new Shader(uni.getCam(), getWidth(), getHeight()),
				uni.getLights());
		return g3d;
	}

	@Override
	public void repaint() {
		super.repaint();
		asyncPreRender();
	}

	/**
	 * called on repaint() to perform changes on the Universe that are not
	 * synchronous to the render-process
	 */
	public void asyncPreRender() {
	}

	/**
	 * called before every drawing activity or initializing the
	 * graphics3d-context <br>
	 * called in paintComponent() <br>
	 * every camera movement or changes on the scene should be done here <br>
	 * subclasses are requested to call super, because of some automatically
	 * stuff the super class may do<br>
	 * if a control is set, subclasses should call super.preRender() to not
	 * break the control-input-performance
	 */
	public void preRender() {
		if (control != null)
			control.applyChanges();
	}

	/**
	 * called on every paintComponent-call<br>
	 * draws the universe <br>
	 * subclasses are requested to call super, because of the automatically
	 * render of the universe
	 * 
	 * @param g
	 *            the 3d-graphics context
	 */
	public void render(final Graphics3d g) {
		for (Renderable r : getUniverse().getObjsSorted())
			r.render(g);
	}

	/**
	 * used to pick all Renderables by click on the panel<br>
	 * may return null if rayTracing is not supported by the universe or no
	 * Renderable was detected<br>
	 * works like a 3d-click
	 * 
	 * @param x
	 *            the x-coordinate of the click
	 * @param y
	 *            the y-coordinate of the click
	 * @return all Renderables which were detected
	 */
	public Hashtable<Renderable, Vector3d> pick(final int x,
			final int y) {
		final Camera cam = uni.getCam();
		final Vector3d pix = get3dScreenPoint(x, y);
		final Vector3d s, r;
		if (cam.mode.viewmode == Viewmode.ORTHOGONAL) {
			s = pix;
			if (!cam.mode.notincam)
				s.add(cam.getLookDir().invert());
			r = cam.getLookDir();
		} else {
			if (cam.mode.notincam)
				s = pix;
			else
				s = cam.getPos().clone();
			r = cam.getPos().to(pix);
		}

		final Hashtable<Renderable, Vector3d> oriented = uni
				.rayTrace(s, r);
		if (!cam.mode.oriented) {
			final Vector3d sInv;
			if (cam.mode.viewmode == Viewmode.ORTHOGONAL) {
				sInv = pix;
				if (cam.mode.notincam)
					s.add(cam.getLookDir().invert());
			} else {
				sInv = cam.getPos().clone();
			}
			final Hashtable<Renderable, Vector3d> inv = uni
					.rayTrace(sInv, r.invert());
			oriented.putAll(inv);
		}
		return oriented;
	}

	/**
	 * for a given coordinate on the Panel3d the 3d point in the universe is
	 * returned that is on the screen at the transformed given screen position
	 * 
	 * @param x
	 *            the x-coordinate of this Panel3d
	 * @param y
	 *            the y-coordinate of this Panel3d
	 * @return the corresponding point on the screen of the camera in the
	 *         scenery
	 */
	public Vector3d get3dScreenPoint(final int x, final int y) {
		final double xd = ((double) x) / getWidth();
		final double yd = ((double) y) / getHeight();
		final Vector3d s = uni.getCam().getScreenOrig().clone()
				.add(uni.getCam().getScreenWidth().clone().scale(xd))
				.add(uni.getCam().getScreenHeight().clone()
						.scale(yd));
		return s;
	}

	/**
	 * used to pick the closest-to-the-camera Renderable of all Renderables in
	 * the Universe by click on the panel<br>
	 * may return null if rayTracing is not supported by the universe or no
	 * Renderable was detected
	 * 
	 * @param x
	 *            the x-coordinate of the click
	 * @param y
	 *            the y-coordinate of the click
	 * @return probably the Renderable the user was expecting to click on
	 */
	public Renderable pickClosest(final int x, final int y) {
		final Hashtable<Renderable, Vector3d> punctures = pick(x, y);
		if (punctures.size() == 0)
			return null;

		Renderable close;
		final List<Renderable> keys = new LinkedList<Renderable>();
		keys.addAll(punctures.keySet());
		close = keys.get(0);
		double closeDistance = punctures.get(close)
				.distanceTo(uni.getCam().getPos());
		double currentDistance;
		for (Renderable r : keys) {
			currentDistance = punctures.get(r)
					.distanceTo(uni.getCam().getPos());
			if (currentDistance < closeDistance) {
				closeDistance = currentDistance;
				close = r;
			}
		}
		return close;
	}

	/**
	 * @return true if the backing universes are equal
	 */
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Panel3d) {
			final Panel3d other = (Panel3d) obj;
			if (other.getUniverse().equals(this.getUniverse()))
				return true;
			return false;
		} else
			return false;
	}

	public String toString() {
		return "fast3d.Panel3d[" + "universe=" + getUniverse()
				+ ";control=" + control + "]";
	}

	/**
	 * sets the given control to the default one for this <br>
	 * the controls performInput() method is called on preRender()
	 * 
	 * @see #preRender()
	 * @param c
	 *            the new default control
	 */
	public void setControl(final Control c) {
		control = c;
	}

	/**
	 * @return the default control thats performInput() method is called on
	 *         every preRender() call
	 */
	public Control getControl() {
		return control;
	}

}