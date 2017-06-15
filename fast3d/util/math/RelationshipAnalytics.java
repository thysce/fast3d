package fast3d.util.math;

import fast3d.Renderable;
import fast3d.math.MatrixCalculation;
import fast3d.math.Vector3d;
import fast3d.math.geom.GeometricObject;
import fast3d.math.geom.Plate;
import fast3d.math.geom.Point;
import fast3d.math.geom.Straight;
import fast3d.renderables.Line;
import fast3d.renderables.Pixel;
import fast3d.renderables.Rectangle;
import fast3d.renderables.Triangle;

/**
 * this class offers some static functionalities to analyze the relationship
 * between points, straights and plates mathematically
 * <p>
 * sometimes (if rational) a method is available with overloading to a parameter
 * zeroLockControl:<br>
 * if the straights or plates may be intersecting; due to rounding errors of the
 * computer the intersection may not be detected properly so it might be helpful
 * to consider a slight rounding-mistake (0.0...01 may do)<br>
 * if the given value zeroLockControl is lower or equal 0 no mistake is
 * considered and the check of intersection will be done on double == double
 * 
 * @author Tim Trense
 */
public abstract class RelationshipAnalytics {
	/**
	 * a wrapper to analyze(GeometricalObject,GeometricalObject,0.0) with
	 * zeroLockControl disabled
	 * 
	 * @see #analyzeGeneral(GeometricObject, GeometricObject, double)
	 * @param a
	 *            the first 3d-object to check relationship to the second
	 * @param b
	 *            the second 3d-object
	 * @return their relationship
	 */
	public static Relationship analyzeGeneral(final GeometricObject a,
			final GeometricObject b) {
		return analyzeGeneral(a, b, 0);
	}

	/**
	 * a wrapper around all other analyze methods casting automatically points,
	 * straights and triangles/plates
	 * 
	 * @param a
	 *            the first 3d-object to check relationship to the second
	 * @param b
	 *            the second 3d-object
	 * @param zeroLockControl
	 *            the value to pass to appropriate methods (see documentation
	 *            for this class)
	 * @return their relationship
	 */
	public static Relationship analyzeGeneral(final GeometricObject a,
			final GeometricObject b, final double zeroLockControl) {
		if (a instanceof Point) {
			if (b instanceof Point)
				return analyze((Point) a, (Point) b);
			if (b instanceof Straight)
				return analyze((Straight) b, (Point) a);
			if (b instanceof Plate)
				return analyze((Plate) b, (Point) a, zeroLockControl);
		}
		if (a instanceof Straight) {
			if (b instanceof Point)
				return analyze((Straight) a, (Point) b);
			if (b instanceof Straight)
				return analyze((Straight) a, (Straight) b,
						zeroLockControl);
			if (b instanceof Plate)
				return analyze((Plate) b, (Straight) a,
						zeroLockControl);
		}
		if (a instanceof Plate) {
			if (b instanceof Point)
				return analyze((Plate) a, (Point) b, zeroLockControl);
			if (b instanceof Straight)
				return analyze((Plate) a, (Straight) b,
						zeroLockControl);
			if (b instanceof Plate)
				return analyze((Plate) a, (Plate) b, zeroLockControl);
		}
		return Relationship.UNDEFINED;
	}

	/**
	 * compares the position of the points<br>
	 * if they are equal result will be identical otherwise parallel
	 * 
	 * @param a
	 *            the point to check against the other
	 * @param b
	 *            the point to be checked against the other
	 * @return whether the points have the same location (IDENTICAL) or nor
	 *         (PARALLEL)
	 */
	public static Relationship analyze(final Point a, final Point b) {
		if (a.getPos().equals(b.getPos()))
			return Relationship.IDENTICAL;
		else
			return Relationship.PARALLEL;
	}

	/**
	 * compares the position of the point to the straight<br>
	 * if the point is on the straight result will be intersecting otherwise
	 * parallel
	 * 
	 * @param straight
	 *            the straight to check whether it contains the point
	 *            mathematically
	 * @param point
	 *            the point to be checked whether it is on the straight
	 * @return whether the point is element of the straight (INCIDENT) or not
	 *         (PARALLEL)
	 */
	public static Relationship analyze(final Straight straight,
			final Point point) {
		if (straight.getBase().to(point.getPos())
				.isParallel(straight.getDirection()))
			return Relationship.INCIDENT;
		else
			return Relationship.PARALLEL;
	}

	/**
	 * compares the position of the point to the plate<br>
	 * if the point is on the plate result will be intersecting otherwise
	 * parallel
	 * 
	 * @param plate
	 *            the mathematical plate to check whether it contains the point
	 * @param point
	 *            the point to be checked whether it is in the plate
	 * @param zeroLockControl
	 *            see documentation for this class
	 * @return whether the point is element of the plate (INCIDENT) or not
	 *         (PARALLEL)
	 */
	public static Relationship analyze(final Plate plate,
			final Point point, final double zeroLockControl) {
		final Vector3d w = plate.getSpan1().clone();
		final Vector3d h = plate.getSpan2().clone();
		final Vector3d av = plate.getBase().to(point.getPos());
		final double[][] matrix = new double[][] { { w.x, h.x, av.x },
				{ w.y, h.y, av.y } };
		final double[] res = MatrixCalculation
				.solveLinearEquationSystem(matrix);
		final double factorW = res[0];
		final double factorH = res[1];
		if (zeroLockControl <= 0) {
			if (factorW * w.z + factorH * h.z == av.z)
				return Relationship.INCIDENT;
			else
				return Relationship.PARALLEL;
		} else {
			if (Math.abs((factorW * w.z + factorH * h.z)
					- av.z) <= zeroLockControl)
				return Relationship.INCIDENT;
			else
				return Relationship.PARALLEL;
		}
	}

	/**
	 * a wrapper to analyze(a,b,0.0); with zeroLockControl disabled
	 * 
	 * @see #analyze(Plate, Point, double)
	 * @param a
	 *            the plate to check
	 * @param b
	 *            the comparison point to be checked
	 * @return their relationship
	 */
	public static Relationship analyze(final Plate a, final Point b) {
		return analyze(a, b, 0);
	}

	/**
	 * compares the two straights a and b<br>
	 * if they are ... result will be ...:
	 * <ul>
	 * <li>askew : ASKEW
	 * <li>identical : IDENTICAL
	 * <li>real-parallel : PARALLEL
	 * <li>intersecting : INTERSECTING
	 * </ul>
	 * 
	 * @param a
	 *            one straight to check against the other
	 * @param b
	 *            the other straight to check against the first
	 * @param zeroLockControl
	 *            see documentation for this class
	 * @return the relationship of the two straights
	 */
	public static Relationship analyze(final Straight a,
			final Straight b, final double zeroLockControl) {
		// first check parallel
		if (a.getDirection().isParallel(b.getDirection())) {
			// if so --> straights may be identical or real-parallel
			Relationship parOrIdent = RelationshipAnalytics
					.analyzeGeneral(a, new Point(b.getBase().clone()),
							zeroLockControl);
			if (parOrIdent == Relationship.INCIDENT)
				// identical if one straight contains one point of the other
				return Relationship.IDENTICAL;
			else
				return Relationship.PARALLEL;
		}
		// if not parallel --> straights may be askew or intersecting
		final Vector3d d1 = a.getDirection().clone();
		final Vector3d d2 = b.getDirection().clone();
		final Vector3d s1 = a.getBase().clone();
		final Vector3d s2 = b.getBase().clone();
		final Vector3d sdelta = s1.clone().sub(s2);
		final double[][] matrix = new double[][] {
				{ d2.x, -d1.x, sdelta.x }, { d2.y, -d1.y, sdelta.y },
				// z not required because only 2 variables and z-straight is
				// check
		};
		final double[] intersect = MatrixCalculation
				.solveLinearEquationSystem(matrix);
		final double fact2 = intersect[0];
		final double fact1 = intersect[1];
		if (zeroLockControl <= 0) {
			if (fact2 * d2.z - fact1 * d1.z == sdelta.z)
				// if straights share one point --> intersecting
				return Relationship.INTERSECTING;
			else
				return Relationship.ASKEW;
		} else {
			if (Math.abs((fact2 * d2.z - fact1 * d1.z)
					- sdelta.z) <= zeroLockControl)
				// if straights share one point --> intersecting
				return Relationship.INTERSECTING;
			else
				return Relationship.ASKEW;
		}
	}

	/**
	 * a wrapper to analyze(a,b,0.0); with zeroLockControl disabled
	 * 
	 * @see #analyze(Straight, Straight, double)
	 * @param a
	 *            one straight to check
	 * @param b
	 *            the comparison straight to be checked
	 * @return their relationship
	 */
	public static Relationship analyze(final Straight a,
			final Straight b) {
		return analyze(a, b, 0);
	}

	/**
	 * compares the straight and plate<br>
	 * if ... the result will be ...:
	 * <ul>
	 * <li>the plate contains the straight : INCIDENT
	 * <li>the straight is real-parallel to the plate : PARALLEL
	 * <li>the straight intersects the plate : INTERSECTING
	 * </ul>
	 * 
	 * @param plate
	 *            the plate to check against the straight
	 * @param straight
	 *            the straight to be checked against the plate
	 * @param zeroLockControl
	 *            see documentation for this class
	 * @return the relationship between the straight and plate
	 */
	public static Relationship analyze(final Plate plate,
			final Straight straight, final double zeroLockControl) {
		final Vector3d s = straight.getBase().clone();
		final Vector3d d = straight.getDirection().clone();
		final Vector3d p = plate.getBase().clone();
		final Vector3d v1 = plate.getSpan1().clone();
		final Vector3d v2 = plate.getSpan2().clone();
		final Vector3d delta = s.clone().sub(p);
		final double[][] matrix = new double[][] {
				{ v1.x, v2.x, -d.x, delta.x },
				{ v1.y, v2.y, -d.y, delta.y },
				{ v1.z, v2.z, -d.z, delta.z } };
		MatrixCalculation.rref(matrix);
		final double a = matrix[2][2];
		final double b = matrix[2][3];
		if (zeroLockControl <= 0) {
			if (a != 0)
				return Relationship.INTERSECTING;
			else {
				if (b == 0)
					return Relationship.INCIDENT;
				else
					return Relationship.PARALLEL;
			}
		} else {
			if (a < zeroLockControl)
				return Relationship.INTERSECTING;
			else {
				if (b < zeroLockControl)
					return Relationship.INCIDENT;
				else
					return Relationship.PARALLEL;
			}
		}
	}

	/**
	 * a wrapper to analyze(a,b,0.0); with zeroLockControl disabled
	 * 
	 * @see #analyze(Plate, Straight, double)
	 * @param a
	 *            the plate to be checked
	 * @param b
	 *            the straight to be checked
	 * @return their relationship
	 */
	public static Relationship analyze(final Plate a,
			final Straight b) {
		return analyze(a, b, 0.0);
	}

	/**
	 * compares the plates<br>
	 * if ... the result will be ...:
	 * <ul>
	 * <li>the plates are equal : IDENTICAL
	 * <li>the plates are real-parallel : PARALLEL
	 * <li>the first plate intersects the second plate (or vice versa) :
	 * INTERSECTING
	 * </ul>
	 * 
	 * @param a
	 *            the plate to check against the other
	 * @param b
	 *            the other plate to be checked against the first
	 * @param zeroLockControl
	 *            see documentation for this class
	 * @return the relationship between the plates
	 */
	public static Relationship analyze(final Plate a, final Plate b,
			final double zeroLockControl) {
		final Vector3d normalA = a.getNormal();
		final Vector3d normalB = b.getNormal();
		if (!normalA.isParallel(normalB))
			return Relationship.INTERSECTING;
		else if (analyzeGeneral(b, new Point(a.getBase().clone()),
				zeroLockControl) != Relationship.PARALLEL)
			return Relationship.IDENTICAL;
		else
			return Relationship.PARALLEL;
	}

	/**
	 * a wrapper to analyze(a,b,0.0); with zeroLockControl disabled
	 * 
	 * @see #analyze(Plate, Plate, double)
	 * @param a
	 *            the plate to be checked
	 * @param b
	 *            the other plate to be checked
	 * @return their relationship
	 */
	public static Relationship analyze(final Plate a, final Plate b) {
		return analyze(a, b, 0.0);
	}

	/**
	 * for two non-IDENTICAL points their distance between them is greater than
	 * 0, otherwise 0
	 * 
	 * @param a
	 *            one point
	 * @param b
	 *            another point
	 * @return the distance between them
	 */
	public static double computeDistance(final Point a,
			final Point b) {
		return a.getPos().distanceTo(b.getPos());
	}

	/**
	 * for a point non-INCIDENT to the straight the distance between the point
	 * and the straight is greater than 0 otherwise 0<br>
	 * the distance between a point and a straight is the distance between the
	 * point and its nadir point on the straight
	 * 
	 * @param a
	 *            the straight
	 * @param b
	 *            the point
	 * @return the minimal distance from the point to a point (in particular the
	 *         nadir point) on the straight
	 */
	public static double computeDistance(final Straight a,
			final Point b) {
		return computeDistance(b,
				new Point(a.nadirPoint(b.getPos())));
	}

	/**
	 * for a point non-INCIDENT to the plate the distance between the point and
	 * the plate is greater than 0 otherwise 0<br>
	 * the distance between a point and a plate is the distance between the
	 * point and its nadir point on the plate
	 * 
	 * @param a
	 *            the plate
	 * @param b
	 *            the point
	 * @return the minimal distance from the point to a point (in particular the
	 *         nadir point) on the plate
	 */
	public static double computeDistance(final Plate a,
			final Point b) {
		return computeDistance(b,
				new Point(a.nadirPoint(b.getPos())));
	}

	/**
	 * for a straight PARALLEL to the plate the distance between the straight
	 * and the plate is greater than 0 otherwise 0<br>
	 * the distance between a straight and a plate is the distance between the
	 * nadir point of any point on the straight to the plate
	 * 
	 * @param a
	 *            the plate
	 * @param b
	 *            the straight
	 * @return the minimal distance from the straight to a point (in particular
	 *         the nadir point) on the plate
	 */
	public static double computeDistance(final Plate a,
			final Straight b) {
		return computeDistance(a, new Point(b.getBase()));
	}

	/**
	 * for a plate PARALLEL to the other plate the distance between the plates
	 * is greater than 0 otherwise 0<br>
	 * the distance between two plates is the distance between the nadir point
	 * of any point on the first plate to the other plate
	 * 
	 * @param a
	 *            one plate
	 * @param b
	 *            another plate
	 * @return the minimal distance from any point on the first plate to a point
	 *         (in particular the nadir point) on the other plate
	 */
	public static double computeDistance(final Plate a,
			final Plate b) {
		return computeDistance(b,
				new Point(a.nadirPoint(b.getBase())));
	}

	/**
	 * if the straights are PARALLEL: their distance is defined as the distance
	 * from any point on one of the straights to the other straight<br>
	 * if the straights are ASKEW: their distance is the minimum distance
	 * between a pair of two points (one on the first straight and one on the
	 * other)<br>
	 * in any other case the distance between the straights is undefined (result
	 * -1);
	 * 
	 * @param a
	 *            one straight
	 * @param b
	 *            another straight
	 * @param r
	 *            their relationship
	 * @return the minimal distance between them if they are PARALLEL or ASKEW,
	 *         -1 otherwise
	 */
	public static double computeDistance(final Straight a,
			final Straight b, final Relationship r) {
		switch (r) {
		case PARALLEL:
			return computeDistance(a, new Point(b.getBase()));
		case ASKEW:
			final Vector3d normal = Vector3d
					.crossP(a.getDirection(), b.getDirection())
					.normalize();
			final Vector3d diff = b.getBase().clone()
					.sub(a.getBase());
			return diff.dotP(normal);
		default:
			return -1;
		}
	}

	/**
	 * a wrapper to computeDistance(Straight,Straight,Relationship)<br>
	 * first their relationship is computed then this method calls the wrapped
	 * method
	 * 
	 * @see #computeDistance(Straight, Straight, Relationship)
	 * @see #analyze(Straight, Straight, double)
	 * @param a
	 *            one straight
	 * @param b
	 *            another straight
	 * @param zeroLockControl
	 *            the zeroLockControl for analyze
	 * 
	 * @return the minimal distance between them if they are PARALLEL or ASKEW,
	 *         -1 otherwise
	 */
	public static double computeDistance(final Straight a,
			final Straight b, final double zeroLockControl) {
		final Relationship r = analyze(a, b);
		return computeDistance(a, b, r);
	}

	/**
	 * a wrapper to computeDistance(Straight,Straight,double) with
	 * zeroLockControl disabled<br>
	 * first their relationship is computed then this method calls the wrapped
	 * method
	 * 
	 * @see #computeDistance(Straight, Straight, Relationship)
	 * @see #analyze(Straight, Straight, double)
	 * @see #computeDistance(Straight, Straight, double)
	 * @param a
	 *            one straight
	 * @param b
	 *            another straight
	 * 
	 * @return the minimal distance between them if they are PARALLEL or ASKEW,
	 *         -1 otherwise
	 */
	public static double computeDistance(final Straight a,
			final Straight b) {
		return computeDistance(a, b, 0);
	}

	/**
	 * for INTERSECTING straights this method computes the point that is on both
	 * straights
	 * 
	 * @param a
	 *            one straight
	 * @param b
	 *            another straight
	 * @return the point they share
	 */
	public static Point computeIntersection(final Straight a,
			final Straight b) {
		final Vector3d d1 = a.getDirection().clone();
		final Vector3d d2 = b.getDirection().clone();
		final Vector3d s1 = a.getBase().clone();
		final Vector3d s2 = b.getBase().clone();
		final Vector3d sdelta = s1.clone().sub(s2);
		final double[][] matrix = new double[][] {
				{ d2.x, -d1.x, sdelta.x }, { d2.y, -d1.y, sdelta.y },
				// z not required because only 2 variables and the straights
				// must have a puncture
		};
		final double[] intersect = MatrixCalculation
				.solveLinearEquationSystem(matrix);
		final double fact2 = intersect[0];
		// final double fact1 = intersect[1];
		return new Point(s2.add(d2.scale(fact2)));
		// return new Point(s1.add(d1.scale(fact1)));
		// should return the same
	}

	/**
	 * for a straight INTERSECTING the plate this method computes the point that
	 * is on both the straight and the plate
	 * 
	 * @param plate
	 *            the plate intersected by the straight
	 * @param straight
	 *            the straight intersecting the plate
	 * @return the point they share
	 */
	public static Point computeIntersection(final Plate plate,
			final Straight straight) {
		final Vector3d s = straight.getBase().clone();
		final Vector3d d = straight.getDirection().clone();
		final Vector3d p = plate.getBase().clone();
		final Vector3d v1 = plate.getSpan1().clone();
		final Vector3d v2 = plate.getSpan2().clone();
		final Vector3d delta = s.clone().sub(p);
		final double[][] matrix = new double[][] {
				{ v1.x, v2.x, -d.x, delta.x },
				{ v1.y, v2.y, -d.y, delta.y },
				{ v1.z, v2.z, -d.z, delta.z } };

		final double[] res = MatrixCalculation
				.solveLinearEquationSystem(matrix);

		final Vector3d puncturepoint = p.add(v1.scale(res[0]))
				.add(v2.scale(res[1]));

		return new Point(puncturepoint);
	}

	/**
	 * for INTERSECTING plates this method computes the straight that is
	 * INCIDENT for both plates
	 * 
	 * @param a
	 *            one plate
	 * @param b
	 *            another plate
	 * @param zeroLockControl
	 *            see documentation for this class
	 * @return the straight they share
	 */
	public static Straight computeIntersection(final Plate a,
			final Plate b, final double zeroLockControl) {
		final Vector3d normal = a.getNormal();
		final Vector3d i1 = a.getSpan1().clone();
		final Straight s1 = new Straight(a.getBase().clone(), i1);
		if (analyze(b, s1,
				zeroLockControl) != Relationship.INTERSECTING)
			i1.rot(normal, Math.PI / 2);
		final Vector3d i2 = a.getSpan2().clone();
		final Straight s2 = new Straight(a.getBase().clone(), i2);
		if (analyze(b, s2,
				zeroLockControl) != Relationship.INTERSECTING)
			i2.rot(normal, Math.PI / 2);

		final Point first = computeIntersection(b, s1);
		final Point second = computeIntersection(b, s2);

		return new Straight(first.getPos(),
				first.getPos().to(second.getPos()));
	}

	/**
	 * a wrapper to computeIntersection(Plate,Plate,double) with zeroLockControl
	 * disabled
	 * 
	 * @see #computeIntersection(Plate, Plate, double)
	 * @param a
	 *            one plate
	 * @param b
	 *            another plate
	 * @return the straight they share
	 */
	public static Straight computeIntersection(final Plate a,
			final Plate b) {
		return computeIntersection(a, b, 0.0);
	}

	/**
	 * for a given renderable that is instance either of the following the
	 * result will be the geometric object that contains the renderable as a
	 * subset of points
	 * <ul>
	 * <li>Pixel to Point
	 * <li>Line to Straight
	 * <li>Triangle or Rectangle to Plate
	 * </ul>
	 * 
	 * @param r
	 *            a renderable that can be mapped to a geometric object
	 * @return the geometric object as explained above
	 */
	public static GeometricObject findCorrespond(final Renderable r) {
		if (r instanceof Pixel)
			return (Point) r;
		if (r instanceof Line)
			return ((Line) r).getStraight();
		if (r instanceof Triangle)
			return ((Triangle) r).getPlate();
		if (r instanceof Rectangle)
			return ((Rectangle) r).getPlate124();
		return null;
	}
}