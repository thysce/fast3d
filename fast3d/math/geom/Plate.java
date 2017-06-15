package fast3d.math.geom;

import fast3d.math.MatrixCalculation;
import fast3d.math.Vector3d;

/**
 * representing a geometric (infinitely large) plate in 3d space<br>
 * a mathematical plate has no thickness or other properties other than it's
 * base position and orientation
 * 
 * @author Tim Trense
 */
public class Plate implements GeometricObject {

	private final Vector3d base;
	private final Vector3d span1, span2;

	/**
	 * constructs a new plate<br>
	 * all given vectors must be finite and dir1 must not be parallel to dir2
	 * 
	 * @param base
	 *            any point on the plate
	 * @param dir1
	 *            one of the two span-vectors giving the orientation, must not be zero
	 * @param dir2
	 *            the other of the two span-vectors giving the orientation, must not be zero
	 */
	public Plate(final Vector3d base, final Vector3d dir1,
			final Vector3d dir2) {
		this.base = base;
		this.span1 = dir1;
		this.span2 = dir2;
	}

	/**
	 * @return a reference to the starting point of the straight
	 */
	public Vector3d getBase() {
		return base;
	}

	/**
	 * @return a reference to the first span vector
	 */
	public Vector3d getSpan1() {
		return span1;
	}

	/**
	 * @return a reference to the second span vector
	 */
	public Vector3d getSpan2() {
		return span2;
	}

	/**
	 * @param v
	 *            the point to test
	 * @return whether the point is within this triangle
	 */
	public boolean contains(final Vector3d v) {
		final Vector3d w = this.getSpan1().clone();
		final Vector3d h = this.getSpan2().clone();
		final Vector3d av = v.clone().sub(getBase());
		final double[][] matrix = new double[][] { { w.x, h.x, av.x },
				{ w.y, h.y, av.y } };
		final double[] res = MatrixCalculation
				.solveLinearEquationSystem(matrix);
		final double factorW = res[0];
		final double factorH = res[1];
		return factorW * w.z + factorH * h.z == av.z;
	}

	/**
	 * calculates the nadir point of the given point to this plate<br>
	 * the nadir point is that point on the plate that is shortest from the
	 * given point<br>
	 * 
	 * @param point
	 *            the point to calculate the nadir point from
	 * @return the nadir point on this plate/triangle from the given point
	 */
	public Vector3d nadirPoint(final Vector3d point) {
		return (Vector3d) calculatePuncture(point, getNormal());
	}

	/**
	 * calculates the puncture point of the straight with this<br>
	 * the straight must not be parallel to this
	 * 
	 * @param rayOrig
	 *            the base of the straight
	 * @param rayDir
	 *            the direction of the straight
	 * @return the puncture point of this with the straights
	 */
	public Vector3d calculatePuncture(final Vector3d rayOrig,
			final Vector3d rayDir) {
		final Vector3d ab = this.getSpan1().clone();
		final Vector3d ac = this.getSpan2().clone();
		final Vector3d diff = rayOrig.clone().sub(getBase());

		final double[][] matrix = new double[][] {
				{ ab.x, ac.x, -rayDir.x, diff.x },
				{ ab.y, ac.y, -rayDir.y, diff.y },
				{ ab.z, ac.z, -rayDir.z, diff.z } };

		final double[] res = MatrixCalculation
				.solveLinearEquationSystem(matrix);

		final Vector3d puncturepoint = (Vector3d) getBase().clone()
				.add(ab.clone().scale(res[0]))
				.add(ac.clone().scale(res[1]));

		return puncturepoint;

	}

	/**
	 * @return a clone vector that is orthogonal to the plate
	 */
	public Vector3d getNormal() {
		return Vector3d.crossP(getSpan1(), getSpan2()).normalize();
	}
}
