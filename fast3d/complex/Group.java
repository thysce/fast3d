package fast3d.complex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import fast3d.Renderable;
import fast3d.complex.light.Illuminatable;
import fast3d.complex.light.Light;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.renderables.Triangle;

/**
 * a class to combine multiple triangles<br>
 * the renderable interface is implemented just to make this addable to an
 * universe and to make it detectable by raytracing, on call of render nothing
 * happens<br>
 * note: to add this to an universe does not mean to add the triangles to the
 * universe, but just to make clicking and selecting work<br>
 * a group is just a way of organizing data for a scenery or an object,
 * considering the model-view-controller model it is requested to used
 * addToUniverse() instead of adding this to a universe<br>
 * hint: although the contained Renderables can build an 3d-object, they can be
 * independent
 * 
 * @author Tim Trense
 */
public class Group extends java.lang.Object implements Illuminatable {

	/**
	 * direct access to the internal storage permitted
	 */
	public final List<Triangle> triangles;
	/**
	 * the radius used to determine whether another group is near enough to do
	 * collision-detection
	 */
	public double collisionSphereRadius = 0;
	private final Vector3d pos;
	private Vector3d forward, up;
	private String groupID;

	/**
	 * @param pos
	 *            the initial position of the object (can later just be edited
	 *            by move). the position is independent to the triangles, but by
	 *            move() they are moved too
	 * @param triangles
	 *            the initial list of triangles hold by this
	 */
	public Group(final Vector3d pos, final Triangle... triangles) {
		this.triangles = new ArrayList<Triangle>(triangles.length);
		for (Triangle t : triangles)
			this.triangles.add(t);
		this.pos = pos;
		forward = Vector3d.forward();
		up = Vector3d.up();
	}

	/**
	 * the initial position will be Vector3d.zero()
	 * 
	 * @param triangles
	 *            the initial list of triangles hold by this
	 */
	public Group(final Triangle... triangles) {
		this((Vector3d) Vector3d.zero(), triangles);
	}

	/**
	 * @param pos
	 *            the initial position of the object (can later just be edited
	 *            by move). the position is independent to the triangles, but by
	 *            move() they are moved too
	 * @param triangles
	 *            the initial list of triangles hold by this
	 */
	public Group(final Vector3d pos, final List<Triangle> triangles) {
		this.triangles = new ArrayList<Triangle>(triangles.size());
		this.triangles.addAll(triangles);
		this.pos = pos;
		forward = Vector3d.forward();
		up = Vector3d.up();
	}

	/**
	 * the initial position will be Vector3d.zero()
	 * 
	 * @param triangles
	 *            the initial list of triangles hold by this
	 */
	public Group(final List<Triangle> triangles) {
		this((Vector3d) Vector3d.zero(), triangles);
	}

	/**
	 * in an *.obj-file a set of faces/triangles can be grouped under some
	 * identifier-string<br>
	 * 
	 * @return the group-identifier
	 */
	public String getGroupID() {
		return groupID;
	}

	/**
	 * in an *.obj-file a set of faces/triangles can be grouped under some
	 * identifier-string<br>
	 * 
	 * @param groupID
	 *            the new group-identifier
	 */
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	/**
	 * @return a reference to the internal storage
	 */
	public List<Triangle> getTriangles() {
		return triangles;
	}

	/**
	 * @return a new list containing all the triangles of getTriangles() which
	 *         are AdvTriangles
	 */
	public List<AdvTriangle> getAdvTriangles() {
		final List<Triangle> gts = getTriangles();
		final List<AdvTriangle> ats = new ArrayList<AdvTriangle>(
				gts.size());
		for (Triangle t : gts)
			if (t instanceof AdvTriangle)
				ats.add((AdvTriangle) t);
		return ats;
	}

	/**
	 * @return a new array containing all the triangles of getTriangles() which
	 *         are AdvTriangles
	 */
	public AdvTriangle[] getAdvTrianglesAsArray() {
		final List<AdvTriangle> ats = getAdvTriangles();
		return ats.toArray(new AdvTriangle[ats.size()]);
	}

	/**
	 * @return an array-reference to the hold triangles
	 */
	public Triangle[] getTrianglesAsArray() {
		return triangles.toArray(new Triangle[triangles.size()]);
	}

	/**
	 * @return the count of triangles hold by this
	 */
	public int capacity() {
		return triangles.size();
	}

	/**
	 * the position will be cloned but the internal storage is just given by
	 * reference and not cloned
	 */
	@Override
	protected Group clone() {
		return new Group(pos.clone(), triangles);
	}

	/**
	 * @return whether the given group hold all the same triangles and no others
	 *         and is on the same location
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Group) {
			final Group o3d = (Group) obj;
			if (o3d.capacity() != this.capacity()
					|| !pos.equals(o3d.pos))
				return false;
			for (Triangle thisT : this.triangles)
				if (!o3d.triangles.contains(thisT))
					return false;
			return true;
		} else
			return false;
	}

	/**
	 * @return Group[\"[groupID]\";capacity=[capacity];pos=[position-vector]]
	 */
	@Override
	public String toString() {
		return "fast3d.complex.Group[\"" + groupID + "\";capacity="
				+ capacity() + ";pos=" + pos + "]";
	}

	/**
	 * returns a reference
	 */
	@Override
	public Vector3d getPos() {
		return pos;
	}

	/**
	 * translates all Triangles along the given vector and the position-vector
	 * too
	 * 
	 * @param dir
	 *            the vector to add to all triangles
	 */
	public void moveInAbsoluteDirection(final Vector3d dir) {
		pos.add(dir);
		for (Vector3d v : getVerticesOnce())
			v.add(dir);
	}

	/**
	 * calculates the difference-vector from this position to the positions of
	 * the triangles
	 * 
	 * @return the relative position of the triangles to the groups position
	 */
	public Hashtable<Triangle, Vector3d> getRelativePositions() {
		final Hashtable<Triangle, Vector3d> relatives = new Hashtable<Triangle, Vector3d>();
		for (Triangle t : triangles)
			relatives.put(t, pos.to(t.getPos()));
		return relatives;
	}

	/**
	 * moves in the absolute direction of the difference vector from the current
	 * position to the target
	 * 
	 * @param target
	 *            the location where the position-vector will be after call
	 */
	public void moveTo(final Vector3d target) {
		moveInAbsoluteDirection(this.pos.to(target));
	}

	/**
	 * a group is targeted if any of its triangles is targeted<br>
	 * does not necessarily return the nearest to rayOrig puncture point
	 */
	@Override
	public Vector3d rayTrace(final Vector3d rayOrig,
			final Vector3d rayDir) {
		Vector3d v;
		for (Triangle t : triangles)
			if ((v = t.rayTrace(rayOrig, rayDir)) != null)
				return v;
		return null;
	}

	/**
	 * a group renders all of its triangles in the correct order<br>
	 * if some groups may intersect each other user-recognizable, every triangle
	 * of those groups should be rendered directly by adding them to the
	 * universe using addToUniverse()
	 */
	@Override
	public void shade(final Graphics3d g) {
		for (Renderable r : triangles)
			r.shade(g);
	}
	
	/**
	 * a group renders all of its triangles in the correct order<br>
	 * if some groups may intersect each other user-recognizable, every triangle
	 * of those groups should be rendered directly by adding them to the
	 * universe using addToUniverse()
	 */
	@Override
	public void render(final Graphics3d g) {
		final Renderable[] triangs = new Renderable[triangles.size()];
		triangles.toArray(triangs);
		g.sort(triangs);
		for (Renderable r : triangs)
			r.render(g);
	}

	/**
	 * if the parameter is not a group, every triangle of this will be checked
	 * for collision, this collides if any of its triangles collides<br>
	 * if the parameter is a group
	 * <ul>
	 * <li>if this.collisionSphereRadius equals zero, an
	 * triangle-to-triangle-collision-detection will be done, this collides if
	 * any of its triangles collides to any of the parameters triangles
	 * <li>if the collisionSphereRadius is greater than zero, this collides if
	 * the distance between this position-vector and the parameters
	 * position-vector is not greater than the collisionSphereRadius of this
	 * <li>if the collisionSphereRadius is less than zero, this collides if the
	 * distance between this position-vector and the parameters position-vector
	 * is not greater than the collisionSphereRadius of the parameter
	 * </ul>
	 * 
	 * @param r
	 *            the renderable to check collision to
	 * @return whether this collides with the parameter
	 */
	public boolean collides(final Renderable r) {
		if (r instanceof Group) {
			final Group object = (Group) r;
			if (collisionSphereRadius == 0) {
				for (Triangle thisT : triangles)
					for (Triangle otherT : object.triangles)
						if (thisT.collides(otherT))
							return true;
				return false;
			} else if (this.collisionSphereRadius > 0)
				return !(getPos().distanceTo(object
						.getPos()) > this.collisionSphereRadius);
			else
				return !(getPos().distanceTo(object
						.getPos()) > object.collisionSphereRadius);
		} else if (r instanceof Triangle) {
			final Triangle t = (Triangle) r;
			for (Triangle own : triangles)
				if (own.collides(t))
					return true;
			return false;
		} else
			return false;
	}

	/**
	 * rotates the group around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotX(final double rad) {
		rot(Vector3d.right(), rad);
	}

	/**
	 * rotates the group around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotY(final double rad) {
		rot(Vector3d.up(), rad);
	}

	/**
	 * rotates the group around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotZ(final double rad) {
		rot(Vector3d.forward(), rad);
	}

	/**
	 * on any rotation, also either a translation and rotation of any triangles
	 * is performed
	 * 
	 * @param axis
	 *            the axe to rotate around in mathematically positive direction
	 * @param rad
	 *            the rotation angle in radiant
	 */
	public void rot(final Vector3d axis, final double rad) {
		for (Vector3d v : getVerticesOnce()) {
			v.sub(pos);
			v.rot(axis, rad);
			v.add(pos);
		}
		for (Triangle t : getTrianglesOnce())
			if (t instanceof AdvTriangle)
				t.getNormal().rot(axis, rad);
		forward.rot(axis, rad);
		up.rot(axis, rad);
	}

	/**
	 * @return a reference to the groups forward-vector to calculate alignments
	 */
	public Vector3d getForwardVector() {
		return forward;
	}

	/**
	 * @return a reference to the groups up-vector to calculate alignments
	 */
	public Vector3d getUpVector() {
		return up;
	}

	/**
	 * the forward-vector has to be orthogonal to the up-vector
	 * 
	 * @param f
	 *            the new forward-vector to calculate alignments (null is
	 *            ignored)
	 */
	public void setForwardVector(final Vector3d f) {
		if (f == null)
			return;
		else
			forward = f;
	}

	/**
	 * the up-vector has to be orthogonal to the forward-vector
	 * 
	 * @param u
	 *            the new up-vector to calculate alignments (null is ignored)
	 */
	public void setUpVector(final Vector3d u) {
		if (u == null)
			return;
		else
			up = u;
	}

	/**
	 * rotates in the way that the new forward vector will be parallel to the
	 * parameter dir and the new up vector will be parallel to the parameter up
	 * <br>
	 * dir and up should be orthogonal
	 * 
	 * @param dir
	 *            the vector that gives the new forward direction
	 * @param up
	 *            the vector giving the new up direction
	 */
	public void lookInDirection(final Vector3d dir,
			final Vector3d up) {
		final Vector3d axis = Vector3d.crossP(forward, dir);
		final double rad = forward.angleTo(dir);
		rot(axis, rad);
		final double rad2 = this.up.angleTo(up);
		rot(forward, rad2);
	}

	/**
	 * calculates the center/middle of all triangles
	 * 
	 * @return the middle between all hold triangles
	 */
	public Vector3d getMiddle() {
		double x, y, z;
		x = y = z = 0;
		int count = 0;
		for (Triangle t : triangles) {
			final Vector3d pos = t.getPos();
			x += pos.x;
			y += pos.y;
			z += pos.z;
			count++;
		}
		return new Vector3d(x / count, y / count, z / count);
	}

	/**
	 * sets the position-field to getMiddle(), does not move this or any of its
	 * hold triangles
	 */
	public void setPosToMiddle() {
		pos.set(getMiddle());
	}

	/**
	 * performs uni.add(Triangle) for all hold triangles
	 * 
	 * @param uni
	 *            the universe to add the internal storage to
	 */
	public void addToUniverse(final Universe uni) {
		for (Triangle t : triangles)
			uni.add(t);
	}

	/**
	 * performs uni.remove(Triangle) for all hold triangles
	 * 
	 * @param uni
	 *            the universe to remove the internal storage from
	 */
	public void removeFromUniverse(final Universe uni) {
		for (Triangle t : triangles)
			uni.remove(t);
	}

	@Override
	public Vector3d[] getVertices() {
		final List<Vector3d[]> vecs = new ArrayList<Vector3d[]>(
				triangles.size());
		int count = 0;
		for (Renderable o : triangles) {
			final Vector3d[] vs = o.getVertices();
			if (vs != null) {
				count += vs.length;
				vecs.add(vs);
			}
		}
		final Vector3d[] all = new Vector3d[count];
		count = 0;
		for (Vector3d[] vs : vecs)
			for (Vector3d v : vs)
				all[count++] = v;
		return all;
	}

	/**
	 * a wrapper to getVertices() removing all double-entries of a vertex so
	 * that it is only once in the result-set
	 * 
	 * @see #getVertices()
	 * @return a set of all vertices in the group without doublets
	 */
	public Set<Vector3d> getVerticesOnce() {
		final Vector3d[] vertices = getVertices();
		final Set<Vector3d> once = new HashSet<Vector3d>();
		for (Vector3d v : vertices)
			once.add(v); // does contains()-check automatically
		return once;
	}

	@Override
	public Color getColor() {
		return triangles.get(0).getColor();
	}

	/**
	 * @return null
	 */
	@Override
	public Material getMaterial() {
		return null;
	}

	/**
	 * @return a set of all materials used by the contained triangles (without
	 *         doublets)
	 */
	public Set<Material> getMaterials() {
		final Set<Material> mats = new HashSet<Material>();
		for (Triangle t : getTriangles())
			if (t instanceof AdvTriangle)
				mats.add(((AdvTriangle) t).getMaterial());
		return mats;
	}

	/**
	 * @return the normal of the first triangle in the internal storage
	 */
	@Override
	public Vector3d getNormal() {
		return triangles.get(0).getNormal();
	}

	/**
	 * a wrapper to getTriangles() removing the doublets
	 * 
	 * @see #getTriangles()
	 * @return all triangles of the internal storage but everyone only once
	 */
	public Set<Triangle> getTrianglesOnce() {
		final HashSet<Triangle> triangs = new HashSet<Triangle>();
		for (Triangle t : triangles)
			triangs.add(t); // does contains()-check automatically
		return triangs;
	}

	/**
	 * a wrapper to getAdvTriangles() removing the doublets
	 * 
	 * @see #getAdvTriangles()
	 * @return all AdvTriangles of the internal storage but everyone only once
	 */
	public Set<AdvTriangle> getAdvTrianglesOnce() {
		final HashSet<AdvTriangle> triangs = new HashSet<AdvTriangle>();
		for (AdvTriangle t : getAdvTriangles())
			triangs.add(t); // does contains()-check automatically
		return triangs;
	}

	/**
	 * scales every hold vertex by the given factor
	 * 
	 * @param factor
	 *            the factor how to enlarge ]1;infinite[ or shrink ]0;1[ or
	 *            mirror-scale ]-infinite;0[
	 */
	public void scale(final double factor) {
		for (Vector3d v : getVerticesOnce())
			v.scale(factor);
	}

	/**
	 * performs the given VertexAction on every vertex once
	 * 
	 * @param va
	 *            the action to perform once on every vertex
	 */
	public void forEveryVertexOnce(final VertexAction va) {
		for (Vector3d vertex : getVerticesOnce())
			va.perform(vertex);
	}

	/**
	 * performs the given VertexAction on every vertex
	 * 
	 * @param va
	 *            the action to perform on every vertex the many times it is
	 *            contained in the hold triangles
	 */
	public void forEveryVertex(final VertexAction va) {
		for (Vector3d vertex : getVertices())
			va.perform(vertex);
	}

	@Override
	public void revalidateLight(Light... lights) {
		for (Triangle t : triangles)
			if (t instanceof AdvTriangle)
				((AdvTriangle) t).revalidateLight(lights);
	}
	
	@Override
	public void invalidateLight() {
		for (Triangle t : triangles)
			if (t instanceof AdvTriangle)
				((AdvTriangle) t).invalidateLight();
	}
}