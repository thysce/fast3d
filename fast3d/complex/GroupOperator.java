package fast3d.complex;

import fast3d.renderables.Triangle;

/**
 * if some triangles are hold by more than one group it might be necessary to
 * calculate which is in both, only in one, only in the other and so on, to not
 * remove or add one multiple times to or from a universe or remove a triangle
 * not if it is hold by another group in the universe
 * 
 * @author Tim Trense
 */
public abstract class GroupOperator {

	/**
	 * returns every triangle that is in a and b
	 * 
	 * @param a
	 *            the first set of triangles
	 * @param b
	 *            the second set of triangles
	 * @return a intersect b
	 */
	public static Group intersection(final Group a, final Group b) {
		final Group g = new Group();
		for (Triangle t : a.triangles)
			if (b.triangles.contains(t))
				g.triangles.add(t);
		return g;
	}

	/**
	 * returns every triangle that is in minuend but not in subtrahend
	 * 
	 * @param minuend
	 *            the first set of triangles
	 * @param subtrahend
	 *            the second set of triangles
	 * @return a minus b
	 */
	public static Group subtract(final Group minuend, final Group subtrahend) {
		final Group g = new Group();
		for (Triangle t : minuend.triangles)
			if (!subtrahend.triangles.contains(t))
				g.triangles.add(t);
		return g;
	}

	/**
	 * returns every triangle that is in a or b, but every triangle only once
	 * 
	 * @param a
	 *            the first set of triangles
	 * @param b
	 *            the second set of triangles
	 * @return a union b
	 */
	public static Group union(final Group a, final Group b) {
		final Group g = new Group();
		g.triangles.addAll(a.triangles);
		for (Triangle t : b.triangles)
			if (!g.triangles.contains(t))
				g.triangles.add(t);
		return g;
	}

	/**
	 * returns every triangle that is nor in a or b
	 * 
	 * @param a
	 *            the first set of triangles
	 * @param b
	 *            the second set of triangles
	 * @return a symmetricDifference b
	 */
	public static Group symmDifference(final Group a, final Group b) {
		final Group g = new Group();
		g.triangles.addAll(a.triangles);
		for (Triangle t : b.triangles)
			if (g.triangles.contains(t))
				g.triangles.remove(t);
			else
				g.triangles.add(t);
		return g;
	}
}