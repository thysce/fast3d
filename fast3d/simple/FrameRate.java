package fast3d.simple;

import fast3d.math.Vector3d;

/**
 * a class used by SimplePanel3d to acquire constant over time repaint() calls
 * <br>
 * repaints the JComponent given to the constructor ongoing in specified
 * intervals <br>
 * 
 * @author Tim Trense
 */
public class FrameRate extends Thread {

	private final java.awt.Component comp;
	private long msPerFrame = 0;
	private long startTime;

	/**
	 * constructs a frame rate thread <br>
	 * need to be started separately
	 * 
	 * @param comp
	 *            the component to repaint
	 */
	public FrameRate(final java.awt.Component comp) {
		super();
		this.comp = comp;
	}

	@Override
	public void run() {
		this.startTime = System.currentTimeMillis();
		do {
			if (msPerFrame > 0) {
				comp.repaint();
				sleep();
			} else
				waitForEngage();
		} while (!isInterrupted());
	}

	/**
	 * @return how long the FrameRate-object since the start of run() in
	 *         milliseconds (usually a SimplePanel3d starts a frame-rate in the
	 *         constructor
	 */
	public long getRuntime() {
		return System.currentTimeMillis() - startTime;
	}

	/**
	 * @see #getRuntime()
	 * @return the runtime in seconds
	 */
	public double getRuntimeSeconds() {
		return getRuntime() / 1000d;
	}

	/**
	 * calculates the amount of time in milliseconds elapsed since the timestamp
	 * given by the parameter
	 * 
	 * @param since
	 *            when the time-measurement started
	 * @return the difference between now and the start
	 */
	public long elapsed(final long since) {
		return getRuntime() - since;
	}

	/**
	 * wrapper to elapsed(long) - converts the millisecond-result to seconds
	 * 
	 * @see #elapsed(long)
	 * @param since
	 *            when the time-measurement started
	 * @return the difference between now and the start
	 */
	public double elapsedSeconds(final long since) {
		return elapsed(since) / 1000d;
	}

	private void sleep() {
		try {
			sleep(msPerFrame);
		} catch (final InterruptedException ie) {
		}
	}

	/**
	 * sets how many repaint() calls should be done per second
	 * 
	 * @param fps
	 *            the frames-per-second
	 */
	public void setFPS(final double fps) {
		msPerFrame = (long) (1000d / fps);
		engage();
	}

	/**
	 * @return the frames-per-second
	 */
	public double getFPS() {
		return 1000d / msPerFrame;
	}

	/**
	 * sets the interval between every repaint() call
	 * 
	 * @param msPerFrame
	 *            the duration of one frame
	 */
	public void setMSperFrame(final long msPerFrame) {
		this.msPerFrame = msPerFrame;
		engage();
	}

	/**
	 * @return the interval between the repaint() calls
	 */
	public long getMSperFrame() {
		return msPerFrame;
	}

	/**
	 * notifies all waiting threads on this, used to not cycle the thread loop
	 * continuously when no repaint() call should be done
	 */
	public void engage() {
		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * waits for an engage() call
	 */
	public void waitForEngage() {
		synchronized (this) {
			try {
				this.wait();
			} catch (final InterruptedException ie) {
			}
		}
	}

	/**
	 * @return the targeted duration of a frame in seconds
	 */
	public double getFrameDurationSeconds() {
		return (msPerFrame / 1000d);
	}

	/**
	 * receives a value that is defines as X per second and converts it to X per
	 * frame based on the current frames-per-second
	 * 
	 * @see #getFPS()
	 * @param valuePerSecond
	 *            a value defined as X per second
	 * @return the corresponding amount of the given value per frame
	 */
	public double valuePerFrame(final double valuePerSecond) {
		return valuePerSecond / getFPS();
	}

	/**
	 * receives a value that is defines as X per second and converts it to X per
	 * frame based on the current frames-per-second
	 * 
	 * @see #valuePerFrame(double)
	 * @see #getFPS()
	 * @param vectorPerSecond
	 *            a value defined as X per second
	 * @return the corresponding amount of the given value per frame
	 */
	public Vector3d vectorPerFrame(final Vector3d vectorPerSecond) {
		return vectorPerSecond.clone().scale(1d / getFPS());
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given FrameRate are equal considering their
	 *         controlled component and msPerFrame
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof FrameRate) {
			final FrameRate other = (FrameRate) obj;
			return comp.equals(other.comp)
					&& msPerFrame == other.msPerFrame;
		} else
			return false;
	}

	/**
	 * 
	 * @return fast3d.simple.FrameRate[_parameter_]
	 **/
	public String toString() {
		return "fast3d.simple.FrameRate[" + "controlledComponent="
				+ comp + ";msPerFrame=" + msPerFrame + "]";
	}
}