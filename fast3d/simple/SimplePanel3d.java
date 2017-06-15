package fast3d.simple;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import fast3d.graphics.Graphics3d;
import fast3d.math.Camera;
import fast3d.mtOpt.MultiThreadPanel3d;
import fast3d.util.ColorGen;
import fast3d.util.click.ClickListener3d;
import fast3d.util.click.ClickManager;
import fast3d.util.click.ClickMode;

/**
 * a default implementation of Panel3d with various features to simplify coding
 * <br>
 * it is highly recommended from subclasses to call super on preRender() and
 * render()
 * 
 * @author Tim Trense
 */
@SuppressWarnings("serial")
public class SimplePanel3d extends MultiThreadPanel3d {

	private long lastFrame;

	/**
	 * displays the frames-per-second on the top left of the screen
	 */
	public boolean showActualFPS = false;

	private ClickManager clickmanager;
	private final FrameRate frameRate;

	/**
	 * constructs a new SimplePanel3d() with already started frameRate() (fps=0)
	 * 
	 * @see fast3d.simple.FrameRate
	 */
	public SimplePanel3d() {
		super(new SimpleUniverse()); // must be a simple one
		clickmanager = null;
		frameRate = new FrameRate(this);
		frameRate.start();
	}

	@Override
	public void render(final Graphics3d g) {
		super.render(g);
		if (showActualFPS) {
			final long currentFrame = System.currentTimeMillis();
			final double fps = 1000d / (currentFrame - lastFrame);
			g.setColor(ColorGen.WHITE());
			g.getGraphics2d().drawString((int) fps + " fps", 10,
					g.getGraphics2d().getFont().getSize());
			lastFrame = currentFrame;
		}
	}

	/**
	 * 
	 * @return fast3d.simple.SimplePanel3d[_parameter_]
	 **/
	public String toString() {
		return "fast3d.simple.SimplePanel3d[" + "universe=" + getUni()
				+ ";frameRate=" + frameRate() + ";3dClickManager="
				+ clickmanager + "]";
	}

	/**
	 * @return the internal frameRate-object
	 * @see fast3d.simple.FrameRate
	 */
	public FrameRate frameRate() {
		return frameRate;
	}

	/**
	 * equivalent to getUni().getCam()
	 * 
	 * @return the camera of the universe
	 */
	public Camera getCam() {
		return getUniverse().getCam();
	}

	/**
	 * equivalent to getUniverse() but returning the SimpleUniverse, because a
	 * SimplePanel3d in any case holds a SimpleUniverse
	 * 
	 * @return the universe containing all the 3d-objects
	 */
	public SimpleUniverse getUni() {
		return ((SimpleUniverse) getUniverse());
	}

	/**
	 * it is possible to react on clicks in the 3d-universe directly by
	 * registering a ClickListener3d and the information how to interpret clicks
	 * on this component
	 * 
	 * @param cl
	 *            the clickListener receiving the information about the targeted
	 *            3d-objects
	 * @param cm
	 *            the information how to interpret a click on this component
	 */
	public void setClickListener(final ClickListener3d cl,
			final ClickMode cm) {
		if (clickmanager != null) {
			clickmanager.deactivate();
		}
		if (cl != null) {
			clickmanager = new ClickManager(cl, this, cm);
			clickmanager.activate();
		} else
			clickmanager = null;
	}

	/**
	 * packs this component as the content-pane to a JFrame and sets it visible
	 * 
	 * @param fullScreen
	 *            whether the JFrame to be full screen size and undecorated (on
	 *            false: size is 500x500 and decorated)
	 * @param exitOnClose
	 *            whether to exit the jvm on close (on false: do nothing on
	 *            close is applied)
	 * @return the JFrame which contains this and is visible
	 */
	public JFrame showInFrame(final boolean fullScreen,
			final boolean exitOnClose) {
		final JFrame f = new JFrame("achieved with fast3d");
		try {
			final BufferedImage img = ImageIO
					.read(new File("fast3d/icon.png"));
			if (img != null)
				f.setIconImage(img);
		} catch (final IOException e) {
			// e.printStackTrace();
		}
		if (fullScreen) {
			f.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			f.setUndecorated(true);
		} else {
			final Dimension size = this.getSize();
			if (size != null && (size.width + size.height) > 2)
				f.setSize(size);
			else
				f.setSize(500, 500);
		}
		if (exitOnClose)
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		else
			f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.setContentPane(this);
		f.setVisible(true);
		return f;
	}

	/**
	 * packs this component as the content-pane to a JFrame and sets it visible
	 * <br>
	 * a wrapper to showInFrame(fullScreen,true);
	 * 
	 * @see #showInFrame(boolean, boolean)
	 * 
	 * @param fullScreen
	 *            whether the JFrame to be full screen size and undecorated
	 * @return the JFrame which contains this and is visible
	 */
	public JFrame showInFrame(final boolean fullScreen) {
		return showInFrame(fullScreen, true);
	}

	/**
	 * sets the cursor image to a plain transparent image
	 * 
	 * @return the cursor now active
	 */
	public Cursor hideCursor() {
		final Cursor c = Toolkit.getDefaultToolkit()
				.createCustomCursor(
						new BufferedImage(16, 16,
								BufferedImage.TYPE_INT_ARGB),
						new java.awt.Point(getWidth() / 2,
								getHeight() / 2),
						"nonVisibleCursor");
		setCursor(c);
		return c;
	}
}