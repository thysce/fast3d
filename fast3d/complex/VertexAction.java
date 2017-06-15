package fast3d.complex;

import fast3d.math.Vector3d;

/**
 * a VertexAction is called when using forEveryVertex(VertexAction) on Group,
 * Object or Scene
 * 
 * @author Tim Trense
 */
public interface VertexAction {

	/**
	 * to manipulate the given vertex
	 * 
	 * @param vertex
	 *            the point in space to manipulate
	 */
	public void perform(final Vector3d vertex);

}