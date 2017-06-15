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
 * a class to combine multiple Objects<br>
 * the renderable interface is implemented just to make this addable to an
 * universe and to make it detectable by ray tracing<br>
 * note: to add this to an universe does not mean to add the objects to the
 * universe, but just to make clicking and selecting work<br>
 * although the contained objects can be part of a stringent 3d-scenery, they
 * can be independent
 * 
 * @author Tim Trense
 */
public class Scene extends java.lang.Object implements Illuminatable {

	/**
	 * direct access to the internal storage permitted
	 */
	public final List<Object> objs;
	private final Vector3d pos;
	private Vector3d forward, up;

	/**
	 * @param pos
	 *            the initial position of the scene (can later just be edited by
	 *            move), independent to the objects
	 * @param objs
	 *            the initial list of objects hold by this
	 */
	public Scene(final Vector3d pos, final Object... objs) {
		this.objs = new ArrayList<Object>(objs.length);
		for (Object o : objs)
			this.objs.add(o);
		this.pos = pos;
		forward = Vector3d.forward();
		up = Vector3d.up();
	}

	/**
	 * the initial position will be Vector3d.zero()
	 * 
	 * @param objs
	 *            the initial list of objects hold by this
	 */
	public Scene(final Object... objs) {
		this((Vector3d) Vector3d.zero(), objs);
	}

	/**
	 * @param pos
	 *            the initial position of the scene (can later just be edited by
	 *            move), independent to the object3ds
	 * @param objs
	 *            the initial list of objects hold by this
	 */
	public Scene(final Vector3d pos, final List<Object> objs) {
		this.objs = new ArrayList<Object>(objs.size());
		this.objs.addAll(objs);
		this.pos = pos;
		forward = Vector3d.forward();
		up = Vector3d.up();
	}

	/**
	 * the initial position will be Vector3d.zero()
	 * 
	 * @param objs
	 *            the initial list of objects hold by this
	 */
	public Scene(final List<Object> objs) {
		this((Vector3d) Vector3d.zero(), objs);
	}

	/**
	 * shades all contained objects
	 */
	@Override
	public void shade(final Graphics3d g) {
		for (Renderable r : objs)
			r.shade(g);
	}
	/**
	 * renders all objects in the sorted, correct order
	 */
	@Override
	public void render(final Graphics3d g) {
		final Renderable[] o = new Renderable[objs.size()];
		objs.toArray(o);
		g.sort(o);
		for (Renderable r : o)
			r.render(g);
	}

	/**
	 * @return a reference to the internal storage
	 */
	public List<Object> getObjects() {
		return objs;
	}

	/**
	 * @return an array-reference of the internal storage
	 */
	public Group[] getObjectsAsArray() {
		return objs.toArray(new Group[objs.size()]);
	}

	/**
	 * @return the count of objects hold by this
	 */
	public int capacity() {
		return objs.size();
	}

	/**
	 * clones the position-vector but passes just a reference of the internal
	 * storage
	 */
	@Override
	protected Scene clone() {
		return new Scene(pos.clone(), objs);
	}

	/**
	 * @return true if the given scene contains all of the objects hold by this
	 *         an no more and is on the same location
	 */
	@Override
	public boolean equals(java.lang.Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Scene) {
			final Scene s = (Scene) obj;
			if (s.capacity() != this.capacity() || !pos.equals(s.pos))
				return false;
			for (Object thisT : this.objs)
				if (!s.objs.contains(thisT))
					return false;
			return true;
		} else
			return false;
	}

	@Override
	public String toString() {
		return "fast3d.complex.Scene[capacity=" + capacity() + ";pos="
				+ pos + "]";
	}

	/**
	 * returns a reference
	 */
	@Override
	public Vector3d getPos() {
		return pos;
	}

	/**
	 * moves all objects along the given vector and the position-vector too
	 * 
	 * @param dir
	 *            the vector to add to all objects
	 */
	public void moveInAbsoluteDirection(Vector3d dir) {
		pos.add(dir);
		for (Object t : objs)
			t.moveInAbsoluteDirection(dir);
	}

	/**
	 * calculates the difference-vector from this position to the positions of
	 * the objects
	 * 
	 * @return the relative position of the triangles to the object3ds position
	 */
	public Hashtable<Object, Vector3d> getRelativePositions() {
		final Hashtable<Object, Vector3d> relatives = new Hashtable<Object, Vector3d>();
		for (Object t : objs)
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
		final Hashtable<Object, Vector3d> relatives = getRelativePositions();
		pos.set(target);
		for (Object t : relatives.keySet())
			t.moveTo(pos.clone().add(relatives.get(t)));
	}

	/**
	 * a scene is targeted if any of its objects is targeted
	 */
	@Override
	public Vector3d rayTrace(final Vector3d rayOrig,
			final Vector3d rayDir) {
		Vector3d v;
		for (Object t : objs)
			if ((v = t.rayTrace(rayOrig, rayDir)) != null)
				return v;
		return null;
	}

	/**
	 * collides if any of its objects collides
	 * 
	 * @param r
	 *            the renderable to check collision with
	 * @return whether this collides with the renderable
	 */
	public boolean collides(final Renderable r) {
		for (Object o : objs)
			if (o.collides(r))
				return true;
		return false;
	}

	/**
	 * rotates the scene around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotX(final double rad) {
		rot(Vector3d.right(), rad);
	}

	/**
	 * rotates the scene around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotY(final double rad) {
		rot(Vector3d.up(), rad);
	}

	/**
	 * rotates the scene around the specified axis
	 * 
	 * @see #rot(Vector3d, double)
	 * @param rad
	 *            the radians-angle to rotate mathematically positive
	 */
	public void rotZ(final double rad) {
		rot(Vector3d.forward(), rad);
	}

	/**
	 * on any rotation, also either a translation and rotation of any object is
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
	 * @return a reference to the scenes forward-vector to calculate alignments
	 */
	public Vector3d getForwardVector() {
		return forward;
	}

	/**
	 * @return a reference to the scenes up-vector to calculate alignments
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
	 * calculates the center/middle of all objects
	 * 
	 * @return the middle between all hold objects
	 */
	public Vector3d getMiddle() {
		double x, y, z;
		x = y = z = 0;
		int count = 0;
		for (Object t : objs) {
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
	 * hold objects
	 */
	public void setPosToMiddle() {
		pos.set(getMiddle());
	}

	/**
	 * calls addToUniverse for all contained objects
	 * 
	 * @param uni
	 *            the universe to add to
	 */
	public void addToUniverse(final Universe uni) {
		for (Object o : objs)
			o.addToUniverse(uni);
	}

	/**
	 * adds all hold objects to the given Universe
	 * 
	 * @param uni
	 *            the universe to add to
	 */
	public void addObjectsToUniverse(final Universe uni) {
		for (Object o : objs)
			uni.add(o);
	}

	/**
	 * removes all hold objects from the given Universe
	 * 
	 * @param uni
	 *            the universe to remove from
	 */
	public void removeObjectsFromUniverse(final Universe uni) {
		for (Object o : objs)
			uni.remove(o);
	}

	/**
	 * calls removeFromUniverse for all contained groups
	 * 
	 * @param uni
	 *            the universe to remove from
	 */
	public void removeFromUniverse(final Universe uni) {
		for (Object o : objs)
			o.removeFromUniverse(uni);
	}

	@Override
	public Vector3d[] getVertices() {
		final List<Vector3d[]> vecs = new ArrayList<Vector3d[]>(
				objs.size());
		int count = 0;
		for (Object o : objs) {
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
		final Triangle[][] trianglesArr = new Triangle[objs.size()][];
		int i = 0, count = 0;
		;
		for (Object g : objs) {
			trianglesArr[i] = g.getTriangles();
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
		return objs.get(0).getColor();
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
	 * @return the normal of the first object in the internal storage
	 */
	@Override
	public Vector3d getNormal() {
		return objs.get(0).getNormal();
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
		for (Object o : objs)
			o.invalidateLight();
	}

	@Override
	public void revalidateLight(final Light... lights) {
		for (Object o : objs)
			o.revalidateLight(lights);
	}
}