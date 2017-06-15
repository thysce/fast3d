package fast3d.fragment;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import fast3d.complex.Universe;
import fast3d.simple.SimpleUniverse;
import fast3d.simple.fragment.DefaultFragmentShaderManager;

/**
 * the equivalent to Panel3d but using a FragmentShader instead of graphics3d to
 * render a scene<br>
 * the render process may take several time, the progress is shown as a green
 * line at the top<br>
 * to render the scene, call startRender();
 * 
 * @author Tim Trense
 */
@SuppressWarnings("serial")
public class FragPanel3d extends JComponent {

	private final FragmentShaderManager fsm;
	private BufferedImage last = null;

	/**
	 * default settings:<br>
	 * <ul>
	 * <li>background gray
	 * <li>foreground white
	 * <li>double buffered disabled
	 * <li>focusable
	 * </ul>
	 * 
	 * @param fsm
	 *            the manager controlling the render process, not null
	 */
	public FragPanel3d(final FragmentShaderManager fsm) {
		super();
		setBackground(java.awt.Color.DARK_GRAY);
		setForeground(java.awt.Color.WHITE);
		setDoubleBuffered(false);
		setFocusable(true);
		this.fsm = fsm;
	}

	/**
	 * calls this with a defaultFragmentShaderManager on 8 threads and a plain
	 * simple universe
	 */
	public FragPanel3d() {
		this(new DefaultFragmentShaderManager(new SimpleUniverse(), 8));
	}

	/**
	 * @return the used fragment shader manager
	 */
	public FragmentShaderManager getManager() {
		return fsm;
	}

	/**
	 * (re)starts the render process, this component is repainted automatically
	 * until the frame is completely rendered
	 */
	public void startRender() {
		fsm.interrupt();
		fsm.start(getSize());
		repaint();
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());

		if (last != null)
			g.drawImage(last, 0, 0, this);

		if (fsm != null) {
			final BufferedImage img = fsm.getImage();
			if (img != null)
				g.drawImage(img, 0, 0, this);
			if (fsm.isRunning()) {
				g.setColor(Color.GREEN);
				g.drawLine(0, 0, (int) (getWidth() * fsm.getProgress()), 0);
				repaint(100);
			} else
				last = img;
		}
	}

	/**
	 * @return the universe of the FragmentShaderManager given by getManager()
	 */
	public Universe getUniverse() {
		return getManager().getUniverse();
	}

	/**
	 * 
	 * @return fast3d.fragment.FragPanel3d[_parameter_]
	 **/
	public String toString() {
		return "fast3d.fragment.FragPanel3d[" + "shaderManager=" + fsm + "]";
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given FragPanel3d are equal considering
	 *         their fragment shader manager
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof FragPanel3d) {
			final FragPanel3d other = (FragPanel3d) obj;
			return fsm.equals(other.fsm);
		} else
			return false;
	}
}