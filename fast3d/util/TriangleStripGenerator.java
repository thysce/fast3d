package fast3d.util;

import java.util.LinkedList;
import java.util.List;

import fast3d.complex.Group;
import fast3d.complex.Universe;
import fast3d.math.Vector3d;
import fast3d.renderables.Triangle;

/**
 * a base class to generate a mesh out of its vertices<br>
 * you may extend this class inline just overriding the makeTriangle method<br>
 * the length of the triangle-strip can be quite infinite large (considering the
 * amount of memory of the JVM)<br>
 * 
 * @author Tim Trense
 */
public abstract class TriangleStripGenerator {

	private Vector3d current, last, prelast;
	private List<Triangle> triangs;

	/**
	 * whether to pass the vertices to makeTriangle clockwise or not
	 */
	public boolean reverseOrder = true;

	/**
	 * whether the first vertex has to remain
	 */
	public boolean keepPreLast = false;

	/**
	 * constructs a plain mesh
	 */
	public TriangleStripGenerator() {
		this(true);
	}

	/**
	 * constructs a plain mesh
	 * 
	 * @param reverseOrder
	 *            whether get the vertices clockwise or not in makeTriangle
	 */
	public TriangleStripGenerator(final boolean reverseOrder) {
		triangs = new LinkedList<Triangle>();
		this.reverseOrder = reverseOrder;
	}

	/**
	 * from the third vertex call on, for every call one triangle is generated
	 * with the vertices from the previous and pre-previous call
	 * 
	 * @param vertex
	 *            a new vertex for the mesh, not null
	 */
	public void add(final Vector3d vertex) {
		if (!keepPreLast || prelast == null)
			prelast = last;
		last = current;
		current = vertex;
		if (prelast != null) {
			final Triangle t = reverseOrder ? makeTriangle(prelast, last, current)
					: makeTriangle(current, last, prelast);
			triangs.add(t);
		}
	}

	/**
	 * @return a reference to the internal storage of generated triangles
	 */
	public List<Triangle> getTriangles() {
		return triangs;
	}
	
	/**
	 * @return a group containing all the triangles of the internal storage
	 */
	public Group build(){
		return new Group(triangs);
	}

	/**
	 * adds the internal storage given by getTriangles to the given universe
	 * 
	 * @param uni
	 *            the universe to add the generated mesh to
	 */
	public void addToUniverse(final Universe uni) {
		for (Triangle t : getTriangles())
			uni.add(t);
	}

	/**
	 * this method should generate a triangle based on the given vertices
	 * organized by the generator<br>
	 * the pass-in-order of the arguments is managed by the generator and should
	 * not be reorganized normally
	 * 
	 * @param edge1
	 *            the first edge of the triangle
	 * @param edge2
	 *            the second edge of the triangle
	 * @param edge3
	 *            the third edge of the triangle
	 * @return a triangles based on the given edges, custom-colored or with
	 *         custom material if the result is an AdvTriangle
	 */
	protected abstract Triangle makeTriangle(final Vector3d edge1, final Vector3d edge2,
			final Vector3d edge3);
}