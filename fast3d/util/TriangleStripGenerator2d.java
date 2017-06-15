package fast3d.util;

import java.util.LinkedList;
import java.util.List;

import fast3d.complex.Group;
import fast3d.complex.Universe;
import fast3d.math.Vector3d;
import fast3d.renderables.Triangle;

/**
 * a base class to generate a surface-mesh out of its vertices<br>
 * you may extend this class inline just overriding the makeTriangle method and
 * call add(Vector3d) for all vertices in the mesh and if the mesh is complete -
 * you may call addToUniverse(Universe)<br>
 * one dimension of the 2d-surface is limited to the amount of calls of
 * add(Vector3d) you determine in the constructor by setting the rowLength<br>
 * if that dimension is the width, than the height can be quite infinite large
 * (considering the amount of memory of the JVM)<br>
 * to get triangles in the internal storage (given by getTriangles) you need to
 * add at least (rowLength + 1) vertices
 * 
 * @author Tim Trense
 */
public abstract class TriangleStripGenerator2d {

	private Vector3d[] lastRow, currentRow;
	private int rowIndex;
	private List<Triangle> triangs;
	/**
	 * whether to pass the vertices to makeTriangle clockwise or not
	 */
	public boolean reverseOrder = true;

	/**
	 * constructs a plain surface-mesh
	 * 
	 * @param rowLength
	 *            the width or height of the 2d-surface-plate in vertex-count or
	 *            calls of add(Vector3d)
	 * @param reverseOrder
	 *            whether get the vertices clockwise or not in makeTriangle
	 */
	public TriangleStripGenerator2d(final int rowLength, final boolean reverseOrder) {
		triangs = new LinkedList<Triangle>();
		this.reverseOrder = reverseOrder;
		lastRow = null;
		currentRow = new Vector3d[rowLength];
		rowIndex = 0;
	}

	/**
	 * constructs a plain surface-mesh
	 * 
	 * @param rowLength
	 *            the width or height of the 2d-surface-plate in vertex-count or
	 *            calls of add(Vector3d)
	 **/
	public TriangleStripGenerator2d(final int rowLength) {
		this(rowLength, true);
	}

	/**
	 * from the third vertex call on, for every call one triangle is generated
	 * with the vertices from the previous and pre-previous call
	 * 
	 * @param vertex
	 *            a new vertex for the mesh, not null
	 */
	public void add(final Vector3d vertex) {
		currentRow[rowIndex] = vertex;
		if (rowIndex > 0 && lastRow != null) {
			final Vector3d edge1 = lastRow[rowIndex - 1];
			final Vector3d edge2 = lastRow[rowIndex];
			final Vector3d edge3 = currentRow[rowIndex];
			final Vector3d edge4 = currentRow[rowIndex - 1];
			final Triangle t1 = reverseOrder ? makeTriangle(edge1, edge3, edge2)
					: makeTriangle(edge1, edge2, edge3);
			final Triangle t2 = reverseOrder ? makeTriangle(edge1, edge4, edge3)
					: makeTriangle(edge1, edge3, edge4);
			triangs.add(t1);
			triangs.add(t2);
		}
		rowIndex++;
		if (rowIndex >= currentRow.length) {
			lastRow = currentRow;
			currentRow = new Vector3d[currentRow.length];
			rowIndex = 0;
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