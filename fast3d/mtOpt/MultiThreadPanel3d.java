package fast3d.mtOpt;

import fast3d.Panel3d;
import fast3d.Renderable;
import fast3d.complex.Universe;
import fast3d.graphics.Graphics3d;

/**
 * A Panel3d that can render on multiple threads
 * 
 * @see #setSharedShadingThreadCount(int)
 * @author Tim Trense
 */
@SuppressWarnings("serial")
public class MultiThreadPanel3d extends Panel3d {

	private SharedShader[] threads;
	private int threadCount;

	/**
	 * calls the super constructor on the argument and sets the initial thread-count to 1
	 * @param uni the parameter for super
	 */
	public MultiThreadPanel3d(final Universe uni) {
		super(uni);
		this.threadCount = 1;
	}

	/**
	 * splits the universe into multiple render-pipelines
	 * @return a similar split of renderables to threads based on the CURRENT universe
	 */
	private SharedShader[] pack() {
		if (this.threadCount < 1)
			this.threadCount = 1;
		final SharedShader[] threads = new SharedShader[this.threadCount];
		final Renderable[] mobjs = getUniverse().getObjs();
		final int length = mobjs.length / threads.length;
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new SharedShader(mobjs, i * length,
					(i == threads.length - 1 ? Integer.MAX_VALUE
							: length));
		}
		return threads;
	}

	/**
	 * @return the number of threads used to render one frame
	 */
	public int getSharedShadingThreadCount() {
		return threadCount;
	}

	/**
	 * sets the number of threads used to render the following frames
	 * @param threadCount a positive count
	 */
	public void setSharedShadingThreadCount(final int threadCount) {
		this.threadCount = threadCount;
	}

	@Override
	public void repaint() {
		super.repaint();
		threads = pack();
	}

	@Override
	public void render(final Graphics3d g) {
		for (SharedShader t : threads)
			t.shade(g);
		final Renderable[] ms = getUniverse().getObjsSorted();
		for (SharedShader t : threads)
			t.finishShade();

		for (Renderable m : ms) {
			m.render(g);
		}
	}

}