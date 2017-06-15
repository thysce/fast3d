package fast3d.math;

/**
 * each shader needs information about the camera-setting to shade from, to
 * project all the vertices correctly to the screen<br>
 * a camera has a position and an orientation (look-direction and up-direction)
 * and a mode-setting
 * 
 * @author Tim Trense
 */
// the access-modifiers for the vectors are package to allow the shader to
// directly access these to improve performance extremely
public class Camera {

	/**
	 * the cameras setting about how it looks into the universe
	 */
	public final CameraMode mode;

	/**
	 * the position of the camera
	 */
	/* package */ final Vector3d pos;
	/**
	 * the screen origin is the position of the top-left corner of the 2d-screen
	 * in the 3d-space
	 */
	/* package */ final Vector3d screenOrigin;
	/**
	 * screenWidth and screenHeight are vectors representing the normalized
	 * 2d-screen
	 */
	/* package */ final Vector3d screenWidth, screenHeight;

	/**
	 * position=Vector3d.zero <br>
	 * lookDirection=Vector3d.forward <br>
	 * mode=CameraMode.normalPerspectiveMode();
	 */
	public Camera() {
		this(Vector3d.zero(), new Vector3d(-0.5, 0.5, -1), new Vector3d(1, 0, 0),
				new Vector3d(0, -1, 0), CameraMode.normalPerspectiveMode());
		lookInDirection(Vector3d.forward(), Vector3d.up());
	}

	/**
	 * the camera will contain the following settings:
	 * 
	 * @param pos
	 *            the position of the camera
	 * @param sO
	 *            the upper-left position of the 3d-virtual-screen
	 * @param sW
	 *            the screens x-axis
	 * @param sH
	 *            the screens y-axis
	 * @param mode
	 *            the cameras render-mode
	 */
	private Camera(final Vector3d pos, final Vector3d sO, final Vector3d sW,
			final Vector3d sH, final CameraMode mode) {
		this.pos = pos;
		this.screenOrigin = sO;
		this.screenWidth = sW;
		this.screenHeight = sH;
		this.mode = mode;
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given Camera are equal considering their
	 *         position, orientation, aspect ratio and mode-setting
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Camera) {
			final Camera other = (Camera) obj;
			return pos.equals(other.pos) && screenOrigin.equals(other.screenOrigin)
					&& screenWidth.equals(other.screenWidth)
					&& screenHeight.equals(other.screenHeight) && mode.equals(other.mode);
		} else
			return false;
	}

	/**
	 * @return an independent but equal camera setting
	 */
	@Override
	protected Camera clone() {
		final Camera cam = new Camera(getPos().clone(), getScreenOrig().clone(),
				getScreenWidth().clone(), getScreenHeight().clone(), mode);
		return cam;
	}

	/**
	 * just usable for debug
	 */
	@Override
	public String toString() {
		return "Camera[pos=" + pos.toString() + ";screenOrigin=" + screenOrigin
				+ ";screenWidth=" + screenWidth + ";screenHeight=" + screenHeight
				+ ";aspectRatioWpH=" + getAspectRatioWpH() + ";mode=" + mode + "]";
	}

	/**
	 * moves position and screen origin
	 * 
	 * @param newpos
	 *            the new position of the camera
	 */
	public void moveTo(final Vector3d newpos) {
		screenOrigin.sub(pos);
		this.pos.set(newpos);
		screenOrigin.add(pos);
	}

	/**
	 * the parameter-vector is along the constant x,y,z-axes
	 * 
	 * @param dir
	 *            the vector to add to the cameras position
	 */
	public void moveInAbsoluteDirection(final Vector3d dir) {
		moveTo(pos.clone().add(dir));
	}

	/**
	 * sets focus to the specified target with zoom=1d<br>
	 * means look in direction towards the target
	 * 
	 * @param target
	 *            the vector to determine the direction to look along
	 * @param up
	 *            the vector to determine the z-rotation of the camera; may be
	 *            just Vector3d.up() (if the camera doesn't look straight up or
	 *            down that will work just fine)
	 */
	public void lookTo(final Vector3d target, final Vector3d up) {
		lookTo(target, 1, up);
	}

	/**
	 * sets focus to the specified target with zoom<br>
	 * means look in direction towards the target<br>
	 * a zoom-level under 0 means lookAway from target
	 * 
	 * @param target
	 *            the vector to determine the direction to look along
	 * @param up
	 *            the vector to determine the z-rotation of the camera; may be
	 *            just Vector3d.up()
	 * @param zoom
	 *            positive value; the distance between camera position and
	 *            virtual screen
	 */
	public void lookTo(final Vector3d target, final double zoom, final Vector3d up) {
		final Vector3d dir = pos.to(target).scaleTo(zoom);
		lookInDirection(dir, up);
	}

	/**
	 * the up-vector should be orthogonal to the direction-vector, otherwise the
	 * shading may be warped
	 * 
	 * @param dir
	 *            the vector (starting from the cameras position) determining
	 *            the relative position of the virtual screen to the cameras
	 *            position
	 * @param up
	 *            the vector to determine the z-rotation of the camera; may be
	 *            just Vector3d.up()
	 */
	public void lookInDirection(final Vector3d dir, final Vector3d up) {
		final Vector3d middle = pos.clone().add(dir);

		final Vector3d width = Vector3d.crossP(dir, up).scaleTo(screenWidth);

		// convert up to new screenHeight
		up.invert();
		up.scaleTo(screenHeight);

		this.screenHeight.set(up);
		this.screenWidth.set(width);

		middle.add(getScreenDiagon().invert().scale(0.5));
		this.screenOrigin.set(middle);
	}

	/**
	 * should definitely cloned for calculations
	 * 
	 * @return a position-reference
	 */
	public Vector3d getPos() {
		return pos;
	}

	/**
	 * should definitely cloned for calculations
	 * 
	 * @return a screenOrigin-reference
	 */
	public Vector3d getScreenOrig() {
		return screenOrigin;
	}

	/**
	 * @return the cameras up-angle, normalized
	 */
	public Vector3d getUpVector() {
		return screenHeight.clone().invert().normalize();
	}

	/**
	 * should definitely be cloned for calculations
	 * 
	 * @return the camera screens x-axis-reference
	 */
	public Vector3d getScreenWidth() {
		return screenWidth;
	}

	/**
	 * should definitely be cloned for calculations
	 * 
	 * @return the camera screens y-axis-reference
	 */
	public Vector3d getScreenHeight() {
		return screenHeight;
	}

	/**
	 * @return the diagonal line on the screen as a clone
	 */
	public Vector3d getScreenDiagon() {
		return screenWidth.clone().add(screenHeight);
	}

	/**
	 * @return the look-direction as a normalized clone
	 */
	public Vector3d getLookDir() {
		return Vector3d.crossP(screenWidth, screenHeight).normalize();
	}

	/**
	 * position = (0,1,3) <br>
	 * look to zero
	 */
	public void setNominalView() {
		this.moveTo(new Vector3d(0, 1, 3));
		this.lookTo(Vector3d.zero(), Vector3d.up());
	}

	/**
	 * rotates the look direction around the constant x axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotX(final double rad) {
		lookInDirection(getLookDir().rotX(rad), getUpVector().rotX(rad));
	}

	/**
	 * rotates the look direction around the constant y axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotY(final double rad) {
		lookInDirection(getLookDir().rotY(rad), getUpVector().rotY(rad));
	}

	/**
	 * rotates the look direction around the constant z axis
	 * 
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rotZ(final double rad) {
		lookInDirection(getLookDir().rotZ(rad), getUpVector().rotZ(rad));
	}

	/**
	 * rotates the look direction around the constant given axis
	 * 
	 * @param axis
	 *            the axe to rotate around in mathematically positive direction
	 * @param rad
	 *            the angle to rotate mathematically positive in radiant
	 */
	public void rot(final Vector3d axis, final double rad) {
		lookInDirection(getLookDir().rot(axis, rad), getUpVector().rot(axis, rad));
	}

	/**
	 * scales the 3d virtual screen to the given aspect ratio at a normalized
	 * height and scaled width
	 * 
	 * @param widthPerHeight
	 *            the ratio of display width divided by display height
	 */
	public void applyAspectRatioWpH(final double widthPerHeight) {
		final Vector3d xcenter = screenOrigin.clone().add(screenWidth.scale(.5));
		// screenWidth is resized regarding that it has to be resized not only
		// to right
		// but also to the left to hold the rotation
		screenWidth.scaleTo(widthPerHeight);
		screenHeight.normalize();
		screenOrigin.set(xcenter.add(screenWidth.clone().scale(-.5)));
	}

	/**
	 * scales the 3d virtual screen to the given aspect ratio at a normalized
	 * width and scaled height<br>
	 * if used right this method is equal to
	 * applyAspectRatio(1d/heightPerWidth);<br>
	 * it is preferred to use the width/height version because modern screens
	 * have a bigger width than height and if this method is used some of the
	 * bottom parts of the image are cut off (unless by using the other version
	 * some right parts of the image are cut off - on smartphones in portrait
	 * mode this method version is eventually the better choice)
	 * 
	 * @param heightPerWidth
	 *            the ratio of display height divided by display width
	 */
	public void applyAspectRatioHpW(final double heightPerWidth) {
		final Vector3d ycenter = screenOrigin.clone().add(screenHeight.scale(.5));
		// screenHeight is resized regarding that it has to be resized not only
		// to down
		// but also to up to hold the rotation
		screenHeight.scaleTo(heightPerWidth);
		screenWidth.normalize();
		screenOrigin.set(ycenter.add(screenHeight.clone().scale(-.5)));
	}

	/**
	 * @return the current aspect ratio as width/height
	 */
	public double getAspectRatioWpH() {
		return screenWidth.length() / screenHeight.length();
	}

	/**
	 * @return the current aspect ratio as height/width
	 */
	public double getAspectRatioHpW() {
		return screenHeight.length() / screenWidth.length();
	}
}