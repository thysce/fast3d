package fast3d.mtOpt;

import fast3d.Renderable;
import fast3d.graphics.Graphics3d;

/**
 * a support-class for MultiThreadPanel3d<br>
 * holds the information about it and one shading-pipeline itself
 * @author Tim Trense
 */
public class SharedShader {

	private final Renderable[] list;
	private final int offset, length;
	private SharedShadingThread shade;

	/**
	 * multiple SharedShaders work on one list, each from offset length Renderables long
	 * @param list the list to shade
	 * @param offset the index of first element in the list for this pipeline to shade
	 * @param length the amount of elements for this pipeline to cope with
	 */
	public SharedShader(final Renderable[] list, final int offset,
			final int length) {
		this.length = length;
		this.offset = offset;
		this.list = list;
	}

	/**
	 * starts a thread shading all declared renderables
	 * @param g3d the parameter for Renderable.shade
	 */
	public void shade(final Graphics3d g3d) {
		if (shade == null) {
			shade = new SharedShadingThread(g3d);
			shade.start();
		}
	}

	/**
	 * joins the shading-thread and waits safely for it to complete it's task
	 */
	public void finishShade() {
		if (shade != null) {
			try {
				shade.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			shade = null;
		}
	}

	private class SharedShadingThread extends Thread {
		private final Graphics3d g3d;

		public SharedShadingThread(final Graphics3d g3d) {
			this.g3d = g3d;
			this.setPriority(MAX_PRIORITY);
		}

		@Override
		public void run() {
			for (int i = 0; i < length
					&& i + offset < list.length; i++) {
				list[i + offset].shade(g3d);
			}
		}
	}
}
