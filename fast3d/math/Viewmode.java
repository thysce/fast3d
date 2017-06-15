package fast3d.math;

/**
 * defines what type of calculation a shader should do to achieve the desired
 * view of the camera<br>
 * users of the viewmode should consider null as PERSPECTIVE
 * 
 * @author Tim Trense
 */
public enum Viewmode {

	/**
	 * a normal view : just like a humans eyes view in real world<br>
	 * objects more far away will appear smaller on the screen
	 */
	PERSPECTIVE,
	/**
	 * a view like on a blueprint of a construction<br>
	 * no matter how far something is away from the camera it will maintain the
	 * same size on the screen so there is no impression of depth but scales can
	 * be matched more exactly for two thing covering each other
	 */
	ORTHOGONAL

}
