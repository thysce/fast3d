package fast3d.util.math;

/**
 * Characterizes the relationship of two points, lines or plates
 * 
 * @author Tim Trense
 */
public enum Relationship {

	/**
	 * the points, lines or plates are mathematically identical not considering
	 * their length- or area-restrictions
	 */
	IDENTICAL,
	/**
	 * a point is in the line or plate or a line is in the plate
	 */
	INCIDENT,
	/**
	 * the lines or plates are mathematically identical not considering their
	 * length- or area-restrictions, the points are not on the same location<br>
	 * the point is not on the line or plate
	 */
	PARALLEL, 
	/**
	 * the lines are not parallel and do not intersect one another
	 */
	ASKEW, 
	/**
	 * the lines are not parallel and share one point<br>
	 * the line intersects the plate
	 */
	INTERSECTING, 
	/**
	 * for renderables not defined yet
	 */
	UNDEFINED
}