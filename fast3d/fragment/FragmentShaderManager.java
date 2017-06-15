package fast3d.fragment;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import fast3d.complex.Universe;

/**
 * a fragment shader manager combines all fragment shader threads used to render
 * one frame
 * 
 * @author Tim Trense
 */
public abstract class FragmentShaderManager implements Runnable {

	private final Universe uni;
	private BufferedImage img;
	private final FragmentShader[] shaders;
	private Thread currentFrame = null;
	private boolean running = false, ready = false;

	/**
	 * constructs a new shader manager
	 * 
	 * @param uni
	 *            the universe to render
	 * @param threadcount
	 *            the count of shader to split the process up to, to accelerate
	 *            it
	 */
	public FragmentShaderManager(final Universe uni,
			int threadcount) {
		super();
		this.uni = uni;
		if (threadcount < 1)
			threadcount = 1;
		this.shaders = new FragmentShader[threadcount];
	}

	/**
	 * for every frame and every thread a new shader has to be created<br>
	 * because it is possible to build custom shader that method has to return
	 * one of them
	 * 
	 * @param uni
	 *            the universe to render
	 * @param img
	 *            the image to render to
	 * @param rect
	 *            the area of the image to render
	 * @return the constructed shader
	 */
	protected abstract FragmentShader createShader(final Universe uni,
			final BufferedImage img, final Rectangle rect);

	@Override
	public final void run() {
		running = true;
		final int width = img.getWidth() / shaders.length + 1;
		for (int i = 0; i < shaders.length; i++)
			shaders[i] = createShader(uni, img, new Rectangle(
					i * width, 0, width, img.getHeight()));

		for (int i = 0; i < shaders.length; i++) {
			shaders[i].start();
		}
		running = true;
		for (int i = 0; i < shaders.length; i++)
			try {
				shaders[i].join();
			} catch (final InterruptedException e) {
				// e.printStackTrace();
			}
		running = false;
		ready = true;
	}

	/**
	 * starts a new render-thread
	 * 
	 * @param imageSize
	 *            the size of the screen or resulting image
	 * @return whether the thread started successfully (false if already
	 *         running)
	 */
	public boolean start(final Dimension imageSize) {
		if (currentFrame != null)
			return false;
		img = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_ARGB);
		ready = false;
		running = false;
		currentFrame = new Thread(this);
		currentFrame.start();
		return true;
	}

	/**
	 * @return whether the complete image is rendered
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * stops all render-processes (irreversible for the current frame)
	 */
	public void interrupt() {
		if (currentFrame != null) {
			for (Thread t : shaders)
				if (t != null && t.isAlive())
					t.interrupt();
		}
		currentFrame = null;
	}

	/**
	 * @return the rendered image (ready or not)
	 */
	public BufferedImage getImage() {
		return img;
	}

	/**
	 * @return whether this is waiting for the shader to complete calculation
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @return a value between [0d,1d] determining the ratio of amount of
	 *         already shaded pixels per the images entire pixel count
	 */
	public double getProgress() {
		double count = 0;
		if (isRunning())
			for (FragmentShader fs : shaders)
				count += fs.getShadedPixelCount();
		else if (isReady())
			return 1;
		else
			return 0;
		return count
				/ (getImage().getWidth() * getImage().getHeight());
	}

	/**
	 * @return the universe to render
	 */
	public Universe getUniverse() {
		return uni;
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given FragmentShaderManager are equal
	 *         considering their universe to be rendered, image to render on and
	 *         shaders
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof FragmentShaderManager) {
			final FragmentShaderManager other = (FragmentShaderManager) obj;
			if (uni == null && other.uni != null)
				return false;
			if (img == null && other.img != null)
				return false;
			if (uni != null && img != null)
				if (!(uni.equals(other.uni) && img.equals(other.img)))
					return false;
			if (shaders != null && other.shaders == null)
				return false;
			if (shaders == null && other.shaders != null)
				return false;
			if (shaders.length != other.shaders.length)
				return false;
			return shaders.equals(other.shaders);
		} else
			return false;
	}

	/**
	 * 
	 * @return fast3d.fragment.FragmentShaderManager[_parameter_]
	 **/
	public String toString() {
		return "fast3d.fragment.FragmentShaderManager[" + "universe="
				+ uni + ";img=" + img + "shaders=" + shaders + "]";
	}
}
