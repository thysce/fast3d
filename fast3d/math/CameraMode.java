package fast3d.math;

/**
 * defining how a shader should render things on a cameras screen
 * 
 * @author Tim Trense
 */
public class CameraMode {

	/**
	 * the cameras viewmode<br>
	 * for GET-access please use getViewmode()
	 */
	public Viewmode viewmode;

	/**
	 * determines whether to fill polygons (false) or just draw the sides
	 * (true), default / normal is false
	 */
	public boolean wireframe = false;

	/**
	 * if turned true, for all vertices behind the cameras position the shading
	 * result will be null. <br>
	 * if turned false, the camera's screen will also mirror everything behind
	 * the camera <br>
	 * default / normal is true
	 */
	public boolean oriented = true;

	/**
	 * not in camera<br>
	 * if turned true, only vertices outside of the camera are shaded -otherwise
	 * the shading result is null<br>
	 * if turned false: vertices between camera-position and screen are shaded
	 * <br>
	 * default / normal is false<br>
	 * notincam is disabled (not considered) for viewmode=ORTHOGONAL
	 */
	public boolean notincam = false;

	/**
	 * for all vertices shaded outside the screen-bounds, the result will be
	 * null. <br>
	 * if turned true, some triangles partly out-of-screen will not be drawn
	 * entirely <br>
	 * if turned false, all out-of-clip drawing actions will be done which costs
	 * time <br>
	 * default / normal is false
	 */
	public boolean inscreen = false;

	/**
	 * constructs a new camera mode with the given parameters
	 * 
	 * @param vm
	 *            the viewmode field value
	 * @param m_nIC
	 *            the notincam field value
	 * @param m_o
	 *            the oriented field value
	 * @param m_iS
	 *            the inscreen field value
	 * @param wf
	 *            the wireframe field value
	 */
	public CameraMode(final Viewmode vm, final boolean m_nIC,
			final boolean m_o, final boolean m_iS, final boolean wf) {
		super();
		this.viewmode = vm;
		this.oriented = m_o;
		this.notincam = m_nIC;
		this.inscreen = m_iS;
		this.wireframe = wf;
	}

	/**
	 * should be used instead of direct access to the field because enums can be
	 * null and the viewmode should have a valid value -not null-<br>
	 * if the viewmode field contains null it will be set to
	 * Viewmode.PERSPECTIVE as requested by the Viewmode doc and then the value
	 * is returned
	 * 
	 * @return the valid viewmode field value
	 */
	public Viewmode getViewmode() {
		if (viewmode != null)
			return viewmode;
		else
			return viewmode = Viewmode.PERSPECTIVE;
	}

	/**
	 * @return a camera mode as the eyes of an human have in the real world
	 */
	public static CameraMode normalPerspectiveMode() {
		return new CameraMode(Viewmode.PERSPECTIVE, false, true,
				false, false);
	}

	/**
	 * @return a camera mode useful as a construction painting (blueprint-view)
	 */
	public static CameraMode normalOrthogonalMode() {
		return new CameraMode(Viewmode.ORTHOGONAL, false, true, false,
				false);
	}

	@Override
	public CameraMode clone() {
		return new CameraMode(viewmode, notincam, oriented, inscreen,
				wireframe);
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * 
	 * @return whether this and the given CameraMode are equal considering their
	 *         field values
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof CameraMode) {
			final CameraMode other = (CameraMode) obj;
			return viewmode == other.viewmode
					&& oriented == other.oriented
					&& inscreen == other.inscreen
					&& notincam == other.notincam
					&& wireframe == other.wireframe;
		} else
			return false;
	}

	@Override
	public String toString() {
		return "CameraMode[viewmode=" + viewmode + "; "
				+ (oriented ? "oriented " : "")
				+ (inscreen ? "inscreen " : "")
				+ (notincam ? "notInCam " : "")
				+ (wireframe ? "wireframe " : "") + "]";
	}
}