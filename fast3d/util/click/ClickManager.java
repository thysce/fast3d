package fast3d.util.click;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import fast3d.Panel3d;
import fast3d.Renderable;
import fast3d.math.Vector2d;

/**
 * a click manager handles awt-mouse-clicks and converts them based on the
 * ClickMode to 3d clicks passed to a Panel3d to rayTrace and to a
 * clickListener3d to react on
 * 
 * @see fast3d.util.click.ClickMode
 * @author Tim Trense
 */
public class ClickManager implements MouseListener {

	private final Panel3d p3d;
	private final ClickListener3d cl;
	private final ClickMode cm;
	private Vector2d mouseDown;

	/**
	 * constructs a new ClickManager
	 * 
	 * @param cl
	 *            the receiver of 3d click events
	 * @param p3d
	 *            the panel3d to receive the awt-click-events from
	 * @param cm
	 *            the information about how to interpret a click
	 */
	public ClickManager(final ClickListener3d cl, final Panel3d p3d, final ClickMode cm) {
		this.cl = cl;
		this.p3d = p3d;
		this.cm = cm;
	}

	/**
	 * 
	 * @return fast3d.simple.click.ClickManager[_parameter_]
	 **/
	public String toString() {
		return "fast3d.simple.click.ClickManager[" + "panel3d=" + p3d + ";listener=" + cl
				+ ";mode=" + cm + "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given ClickManager are equal considering
	 *         their panel3d, listener and clickMode
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ClickManager) {
			final ClickManager other = (ClickManager) obj;
			return p3d.equals(other.p3d) && cl.equals(other.cl) && cm == other.cm;
		} else
			return false;
	}

	/**
	 * registers this as a mouse listener on the panel3d
	 */
	public void activate() {
		p3d.addMouseListener(this);
	}

	/**
	 * unregisters this as a mouse listener on the panel3d
	 */
	public void deactivate() {
		p3d.removeMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Vector2d click = null;
		if (cm == ClickMode.ON_MOUSE_POS) {
			click = new Vector2d(e.getX(), e.getY());
		} else if (cm == ClickMode.ON_CENTER) {
			click = new Vector2d(p3d.getWidth() / 2, p3d.getHeight() / 2);
		}
		if (click != null) {
			final Renderable closest = p3d.pickClosest(click.getX(), click.getY());
			cl.clicked(closest);
		}
	}

	public void mousePressed(MouseEvent e) {
		mouseDown = new Vector2d(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if ((cm != ClickMode.IN_BOUNDS && cm != ClickMode.IN_BOUNDS_ONCE)
				|| mouseDown == null)
			return;
		final Vector2d mouseUp = new Vector2d(e.getX(), e.getY());

		Collection<Renderable> list;
		if (cm == ClickMode.IN_BOUNDS_ONCE)
			list = new HashSet<Renderable>();
		else
			list = new LinkedList<Renderable>();

		for (int x = mouseDown.getX(); x < mouseUp.getX(); x++)
			for (int y = mouseDown.getY(); y < mouseUp.getY(); y++)
				list.add(p3d.pickClosest(x, y));

		if (!list.isEmpty()) {
			final Renderable[] arr = new Renderable[list.size()];
			list.toArray(arr);
			cl.clicked(arr);
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

}
