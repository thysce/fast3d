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
 * a class to combine multiple groups<br>
 * the renderable interface is implemented just to make this addable to an
 * universe and to make it detectable by ray tracing<br>
 * note: to add this to an universe does not mean to add the groups to the
 * universe, but just to make clicking and selecting work<br>
 * although the contained groups can be part of a 3d-object, they can be
 * independent
 * 
 * @author Tim Trense
 */
public class Object extends java.lang.Object
		implements Illuminatable {

	/**
	 * direct access to the internal storage permitted
	 */
	public final List<Group> groups;
	private final Vector3d pos;
	private String objectID;
	private Vector3d forward, up;

	/**
	 * @param pos
	 *            the initial position of the object (can later just be edited
	 *            by move), independent to the groups
	 * @param objs
	 *            the initial list of groups hold by this
	 */
	public Object(final Vector3d pos, final Group... objs) {
		this.groups = new ArrayList<Group>(objs.length);
		for (Group o : objs)
			this.groups.add(o);
		this.pos = pos;
		forward = Vector3d.forward();
		up = Vector3d.up();
	}

	/**
	 * the initial position will be Vector3d.zero()
	 * 
	 * @param objs
	 *            the initial list of groups hold by this
	 */
	public Object(final Group... objs) {
		this((Vector3d) Vector3d.zero(), objs);
	}

	/**
	 * @param pos
	 *            the initial position of the object (can later just be edited
	 *            by move), independent to the groups
	 * @param objs
	 *            the initial list of groups hold by this
	 */
	public Object(final Vector3d pos, final List<Group> objs) {
		this.groups = new ArrayList<Group>(objs.size());
		this.groups.addAll(objs);
		this.pos = pos;
		forward = Vector3d.forward();
		up = Vector3d.up();
	}

	/**
	 * the initial position will be Vector3d.zero()
	 * 
	 * @param objs
	 *            the initial list of groups hold by this
	 */
	public Object(final List<Group> objs) {
		this((Vector3d) Vector3d.zero(), objs);
	}

	/**
	 * in an *.obj-file multiple groups can be declared to be combined in an
	 * object
	 * 
	 * @return the objects identifier
	 */
	public String getObjectID() {
		return objectID;
	}

	/**
	 * in an *.obj-file multiple groups can be declared to be combined in an
	 * object
	 * 
	 * @param objectID
	 *            the objects identifier
	 */
	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	/**
	 * calls shade() for every hold Group
	 */
	@Override
	public void shade(final Graphics3d g) {
		for (Renderable r : groups)
			r.shade(g);
	}
	
	/**
	 * calls render() for every hold Group, but in the sorted, right order
	 */
	@Override
	public void render(final Graphics3d g) {
		final Renderable[] o = new Renderable[groups.size()];
		groups.toArray(o);
		g.sort(o);
		for (Renderable r : o)
			r.render(g);
	}

	/**
	 * @return a reference to the internal storage
	 */
	public List<Group> getObjects() {
		return groups;
	}

	/**
	 * @return an array-reference of the internal storage
	 */
	public Group[] getObjectsAsArray() {
		return groups.toArray(new Group[groups.size()]);
	}

	/**
	 * @return the count of object3ds hold by this
	 */
	public int capacity() {
		return groups.size();
	}

	/**
	 * @return an not independent Object considered its internal storage but
	 *         with an independent position
	 */
	@Override
	protected Object clone() {
		return new Object(pos.clone(), groups);
	}

	/**
	 * @return true if the internal storage hold the same groups and no more and
	 *         is on the same location
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Object) {
			final Object o3d = (Object) obj;
			if (o3d.capacity() != this.capacity()
					|| !pos.equals(o3d.pos))
				return false;
			for (Group thisT : this.groups)
				if (!o3d.groups.contains(thisT))
					return false;
			return true;
		} else
			return false;
	}

	@Override
	public String toString() {
		return "fast3d.complex.Object[capacity=" + capacity()
				+ ";pos=" + pos + "]";
	}

	/**
	 * returns a reference
	 */
	@Override
	public Vector3d getPos() {
		return pos;
	}

	/**
	 * moves the position-pointer and all groups in the given axis-aligned
	 * direction
	 * 
	 * @param dir
	 *            the direction to move along the length of the vector
	 */
	public void moveInAbsoluteDirection(final Vector3d dir) {
		pos.add(dir);
		for (Group t : groups)
			t.moveInAbsoluteDirection(dir);
	}

	/**
	 * calculates the difference-vector from this position to the positions of
	 * the groups
	 * 
	 * @return the relative position of the triangles to the objects position
	 */
	public Hashtable<Group, Vector3d> getRelativePositions() {
		final Hashtable<Group, Vector3d> relatives = new Hashtable<Group, Vector3d>();
		for (Group t : groups)
			relatives.put(t, pos.to(t.getPos()));
		return relatives;
	}

	/**
	 * moves along the difference-vector from getPos() to target
	 * 
	 * @param target
	 *            the vector where the position-pointer of this will be
	 */
	public void moveTo(final Vector3d target) {
		moveInAbsoluteDirection(pos.to(target));
	}

	/**
	 * an object is targeted if any of its groups is targeted<br>
	 * does not necessarily return the nearest to rayOrig puncture point
	 */
	@Override
	public Vector3d rayTrace(final Vector3d rayOrig,
			final Vector3d rayDir) {
		Vector3d v;
		for (Group t : groups)
			if ((v = t.rayTrace(rayOrig, rayDir)) != null)
				return v;
		return null;
	}

	/**
	 * collides if any of its groups collides
	 * 
	 * @param r
	 *            the renderable to check collision with
	 * @return whether this collides with the renderable
	 */
	public boolean collides(final Renderable r) {
		for (Group o : groups)
			if (o.collides(r))
				return true;
		return false;
	}

	/**
	 * rotates the object around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotX(final double rad) {
		rot(Vector3d.right(), rad);
	}

	/**
	 * rotates the object around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotY(final double rad) {
		rot(Vector3d.up(), rad);
	}

	/**
	 * rotates the object around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotZ(final double rad) {
		rot(Vector3d.forward(), rad);
	}

	/**
	 * on any rotation, also either a translation and rotation of any group is
	 * performed
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
	 * @return a reference to the objects forward-vector to calculate alignments
	 */
	public Vector3d getForwardVector() {
		return forward;
	}

	/**
	 * @return a reference to the objects up-vector to calculate alignments
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
	 * calculates the center/middle of all groups
	 * 
	 * @return the middle between all hold groups
	 */
	public Vector3d getMiddle() {
		double x, y, z;
		x = y = z = 0;
		int count = 0;
		for (Group t : groups) {
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
	 * hold groups
	 */
	public void setPosToMiddle() {
		pos.set(getMiddle());
	}

	/**
	 * calls addToUniverse for all contained groups
	 * 
	 * @param uni
	 *            the universe to add to
	 */
	public void addToUniverse(final Universe uni) {
		for (Group o : groups)
			o.addToUniverse(uni);
	}

	/**
	 * adds all hold groups to the given Universe
	 * 
	 * @param uni
	 *            the universe to add to
	 */
	public void addGroupsToUniverse(final Universe uni) {
		for (Group o : groups)
			uni.add(o);
	}

	/**
	 * removes all hold groups from the given Universe
	 * 
	 * @param uni
	 *            the universe to remove from
	 */
	public void removeGroupsFromUniverse(final Universe uni) {
		for (Group o : groups)
			uni.remove(o);
	}

	/**
	 * calls removeFromUniverse for all contained groups
	 * 
	 * @param uni
	 *            the universe to remove from
	 */
	public void removeFromUniverse(final Universe uni) {
		for (Group o : groups)
			o.removeFromUniverse(uni);
	}

	@Override
	public Vector3d[] getVertices() {
		final List<Vector3d[]> vecs = new ArrayList<Vector3d[]>(
				groups.size());
		int count = 0;
		for (Group o : groups) {
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
			if (!once.contains(v))
				once.add(v);
		return once;
	}

	/**
	 * a wrapper to getTriangles() removing the doublets
	 * 
	 * @see #getTriangles()
	 * @return all triangles of the internal storage but everyone only once
	 */
	public Set<Triangle> getTrianglesOnce() {
		final Triangle[] triangles = getTriangles();
		final HashSet<Triangle> triangs = new HashSet<Triangle>();
		for (Triangle t : triangles)
			triangs.add(t); // does contains()-check automatically
		return triangs;
	}

	/**
	 * @return an array of all triangles of all groups (not checking doublets)
	 */
	public Triangle[] getTriangles() {
		final Triangle[][] trianglesArr = new Triangle[groups
				.size()][];
		int i = 0, count = 0;

		for (Group g : groups) {
			trianglesArr[i] = g.getTrianglesAsArray();
			count += trianglesArr[i].length;
			i++;
		}
		final Triangle[] triangles = new Triangle[count];
		i = 0;
		for (Triangle[] subArr : trianglesArr)
			for (Triangle t : subArr) {
				triangles[i] = t;
				i++;
			}
		return triangles;
	}

	/**
	 * @return a new list containing all the triangles of getTriangles() which
	 *         are AdvTriangles
	 */
	public List<AdvTriangle> getAdvTriangles() {
		final Triangle[] gts = getTriangles();
		final List<AdvTriangle> ats = new ArrayList<AdvTriangle>(
				gts.length);
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

	@Override
	public Color getColor() {
		return groups.get(0).getColor();
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
	 * @return the normal of the first group in the internal storage
	 */
	@Override
	public Vector3d getNormal() {
		return groups.get(0).getNormal();
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
	 *            the action to perform on every vertex the many times it occurs
	 *            on getVertices()
	 */
	public void forEveryVertex(final VertexAction va) {
		for (Vector3d vertex : getVertices())
			va.perform(vertex);
	}

	@Override
	public void invalidateLight() {
		for (Group g : groups)
			g.invalidateLight();
	}

	@Override
	public void revalidateLight(final Light... lights) {
		for (Group g : groups)
			g.revalidateLight(lights);
	}
}