package fast3d.math;

import fast3d.math.MatrixCalculation;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;

/**
 * a vector3d is a set of 3 double-coordinates, named x, y and z<br>
 * the vectors dimension determines it's count of coordinates<br>
 * a vector can be interpreted as a location or direction in it's 3d universe
 * 
 * @author Tim Trense
 */
public class Vector3d {

	/**
	 * to shade a vertex means to calculate its position on a viewers
	 * camera-screen<br>
	 * to not calculate that multiple times for one image, the shaded position
	 * can be buffered here
	 */
	public Vector2d shaded;
	/**
	 * if multiple images are calculated with changes in the scenery or camera
	 * movement in between it is necessary to know whether the
	 * shaded-field-value is valid or was from previous calculations
	 * 
	 * @see #shaded
	 */
	public byte shadedFrameID;

	/**
	 * the coordinate x of the vector<br>
	 * first dimensions coordinate
	 */
	public double x;

	/**
	 * the coordinate y of the vector<br>
	 * second dimensions coordinate
	 */
	public double y;

	/**
	 * the coordinate z of the vector<br>
	 * third dimensions coordinate
	 */

	public double z;

	/**
	 * constructs a new vector based on the given coordinates<br>
	 * 
	 * @param x
	 *            the x-coordinate of this new vector
	 * @param y
	 *            the y-coordinate of this new vector
	 * @param z
	 *            the z-coordinate of this new vector
	 */

	public Vector3d(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "(" + x + ";" + y + ";" + z + ")";
	}

	/**
	 * to display the vector in an user-appropriate form with x digits after the
	 * comma<br>
	 * for negative parameters, the coordinates will be displayed rounded.<br>
	 * this will not be changed
	 * 
	 * @param decimalPlace
	 *            digits to display after comma
	 * @return an readable string-representation of the coordinates
	 */
	public String toRoundedString(final int decimalPlace) {
		final double pow = (int) Math.pow(10, decimalPlace);
		final double x = (int) (this.x * pow) / pow;
		final double y = (int) (this.y * pow) / pow;
		final double z = (int) (this.z * pow) / pow;
		return "(" + x + ";" + y + ";" + z + ")";
	}

	@Override
	public Vector3d clone() {
		return new Vector3d(x, y, z);
	}

	/**
	 * @see #setCoordinates(double[])
	 * @return the vectors coordinates as an array
	 */
	public double[] getCoordinates() {
		return new double[] { x, y, z };
	}

	/**
	 * counterpart to getCoordinates<br>
	 * sets the coordinates of this to x=[0], y=[1], z=[2]
	 * 
	 * @see #getCoordinates()
	 * @param vec
	 *            a this reference
	 * @return a this reference
	 */
	public Vector3d setCoordinates(final double[] vec) {
		this.x = vec[0];
		this.y = vec[1];
		this.z = vec[2];
		invalidateShaded();
		return this;
	}

	/**
	 * indicate that a prior shade in the same frame is now invalid, due to
	 * changes of the coordinate
	 */
	public void invalidateShaded() {
		shaded = null;
	}

	/**
	 * enlarges the vector by k, it's direction will be constant<br>
	 * for k between 0 and 1, the vector will be shortened<br>
	 * for negative k the vector will be inverted and then enlarged by positive
	 * k
	 * 
	 * @param k
	 *            factor to enlarge
	 * @return a this-reference
	 */
	public Vector3d scale(final double k) {
		x *= k;
		y *= k;
		z *= k;
		invalidateShaded();
		return this;
	}

	/**
	 * scales this vector to the length of the parameter-vector, but will not
	 * change this direction
	 * 
	 * @param length
	 *            the vectors new length
	 * @return a this reference
	 */
	public Vector3d scaleTo(final double length) {
		return normalize().scale(length);
	}

	/**
	 * scales this vector to the length of the parameter-vector, but will not
	 * change this direction<br>
	 * the parameter will not be changed
	 * 
	 * @param compare
	 *            the vector to get the length to scale this to
	 * @return a this reference
	 */
	public Vector3d scaleTo(final Vector3d compare) {
		return scaleTo(compare.length());
	}

	/**
	 * scales the vector to the length 1<br>
	 * fails (does nothing than) for the zero-vector
	 * 
	 * @return a this-reference
	 */
	public Vector3d normalize() {
		final double fac = 1d / length();
		if (Double.isFinite(fac))
			return scale(fac);
		else
			return this;
	}

	/**
	 * compares this with the parameter and returns false if the parameter is
	 * not of the same type<br>
	 * does not change the parameter
	 * 
	 * @return whether this and the given Vector are equal considering their
	 *         coordinates
	 **/
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Vector3d) {
			final Vector3d other = (Vector3d) obj;
			return this.x == other.x && this.y == other.y
					&& this.z == other.z;
		} else
			return false;
	}

	/**
	 * will always return 3<br>
	 * returns the length of the array given by getCoordinates();
	 * 
	 * @see #getCoordinates()
	 * @return 3
	 */
	public final int getDimension() {
		return 3;
	}

	/**
	 * adds the coordinates of the parameter to the coordinates of this<br>
	 * does not change the parameter
	 * 
	 * @param t
	 *            the coordinates to add (will not be changed), not null
	 * @return a this-reference
	 */
	public Vector3d add(final Vector3d t) {
		x += t.x;
		y += t.y;
		z += t.z;
		invalidateShaded();
		return this;
	}

	/**
	 * multiplies the coordinates of the parameter to the coordinates of this
	 * <br>
	 * does not change the parameter
	 * 
	 * @param t
	 *            the coordinates to multiply (will not be changed), not null
	 * @return a this-reference
	 */
	public Vector3d multiply(final Vector3d t) {
		x *= t.x;
		y *= t.y;
		z *= t.z;
		invalidateShaded();
		return this;
	}

	/**
	 * subtracts the coordinates of the parameter from the coordinates of this
	 * <br>
	 * does not change the parameter
	 * 
	 * @param t
	 *            the subtrahend-coordinates (will not be changed), not null
	 * @return a this-reference
	 */
	public Vector3d sub(final Vector3d t) {
		x -= t.x;
		y -= t.y;
		z -= t.z;
		invalidateShaded();
		return this;
	}

	/**
	 * does not change the parameter
	 * 
	 * @param t
	 *            the location to calculate the direction from this location to
	 * @return the difference vector from this to the parameter
	 */
	public Vector3d to(final Vector3d t) {
		return t.clone().sub(this);
	}

	/**
	 * @param dimensionIndex
	 *            the index of the value from getCoordinates()
	 * @return the value of the index from getCoordinates() or Double.NaN if the
	 *         index is out of bounds
	 */
	public double getCoordinate(final int dimensionIndex) {
		final double[] coord = getCoordinates();
		if (dimensionIndex < 0 || dimensionIndex >= coord.length)
			return Double.NaN;
		else
			return coord[dimensionIndex];
	}

	/**
	 * calculates the length of the vector as a n-dimensional arrow
	 * 
	 * @return the vectors length
	 */
	public double length() {
		return Math.sqrt(lengthSquared());
	}

	/**
	 * calculates the squared length of the vector as a n-dimensional arrow <br>
	 * faster than length()
	 * 
	 * @see #length()
	 * @return the vectors length
	 */
	public double lengthSquared() {
		return x * x + y * y + z * z;
	}

	/**
	 * calculates the length of the difference vector given by to(target)<br>
	 * does not change the parameter
	 * 
	 * @param target
	 *            the parameter to give to to(Vector)
	 * @return the length of the difference-vector
	 */
	public double distanceTo(final Vector3d target) {
		return to(target).length();
	}

	/**
	 * calculates the square of the length of the difference vector given by
	 * to(target)<br>
	 * does not change the parameter
	 * 
	 * @param target
	 *            the parameter to give to to(Vector)
	 * @return the length of the difference-vector
	 */
	public double distanceToSquared(final Vector3d target) {
		return to(target).lengthSquared();
	}

	/**
	 * checks, whether the parameter-vector has the same direction<br>
	 * it may have another length<br>
	 * does not change the parameter
	 * 
	 * @param v
	 *            the Vector to check
	 * @return true if the parameter-vector has the same direction
	 */
	public boolean isParallel(final Vector3d v) {
		return Double.isFinite(getRatio(this, v));
	}

	/**
	 * returns the ratio between the two given parallel vectors or Double.NaN if
	 * they are not parallel<br>
	 * if given vectors a and b the ratio is defined as:<br>
	 * a = ratio * b so that ratio = a / b;
	 * 
	 * @param a
	 *            the dividend
	 * @param b
	 *            the divisor
	 * @return the ratio
	 */
	public static double getRatio(final Vector3d a,
			final Vector3d b) {
		final double ratioX = a.x / b.x;
		final double ratioY = a.y / b.y;
		final double ratioZ = a.z / b.z;
		final boolean finX = Double.isFinite(ratioX);
		final boolean finY = Double.isFinite(ratioY);
		final boolean finZ = Double.isFinite(ratioZ);
		if (!finX && a.x != 0)
			return Double.NaN;
		if (!finY && a.y != 0)
			return Double.NaN;
		if (!finZ && a.z != 0)
			return Double.NaN;
		if (finX && finY && finZ)
			if (ratioX == ratioY && ratioX == ratioZ)
				return ratioX;
			else
				return Double.NaN;
		else {
			if (finX && finY)
				if (ratioX == ratioY)
					return ratioX;
				else
					return Double.NaN;
			if (finX && finZ)
				if (ratioX == ratioZ)
					return ratioX;
				else
					return Double.NaN;
			if (finZ && finY)
				if (ratioZ == ratioY)
					return ratioY;
				else
					return Double.NaN;
			if (finX)
				return ratioX;
			if (finY)
				return ratioY;
			if (finZ)
				return ratioZ;
			return 0;
		}
	}

	/**
	 * checks, whether the parameter-vector is orthogonal<br>
	 * it may have another length
	 * 
	 * @param v
	 *            the Vector to check
	 * @return true if the parameter-vector is orthogonal to this, false
	 *         otherwise
	 */
	public boolean isOrthogonal(final Vector3d v) {
		return this.dotP(v) == 0;
	}

	/**
	 * checks, whether the parameter-vector has the same direction and
	 * orientation<br>
	 * it may have another length<br>
	 * does not change the parameter
	 * 
	 * @param v
	 *            the Vector to check
	 * @return true if the parameter-vector has the same direction and is not
	 *         inverted
	 */
	public boolean isSameOrientated(final Vector3d v) {
		final double ratio = getRatio(this, v);
		return Double.isFinite(ratio) && ratio >= 0;
	}

	/**
	 * calculates the dot-product of this and the parameter<br>
	 * does not change both the parameter or this
	 * 
	 * @param v
	 *            the vector to calculate the dot-product with
	 * @return the dot-product between this and the parameter
	 */
	public double dotP(final Vector3d v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}

	/**
	 * the cross-product of two vectors is a vector that is orthogonal to the
	 * parameters<br>
	 * it does matter in which order the parameters are given. In a reverse
	 * order, the result vector will be orientated inverted<br>
	 * the result vector will be the third one for the right-hand-rule<br>
	 * does not change both the parameter and this
	 * 
	 * @param b
	 *            the second vector
	 * @return an orthogonal vector to the two parameters
	 */
	public Vector3d crossP(final Vector3d b) {
		return new Vector3d(this.y * b.z - b.y * this.z,
				b.x * this.z - this.x * b.z,
				this.x * b.y - b.x * this.y);
	}

	/**
	 * the cross-product of two vectors is a vector that is orthogonal to the
	 * parameters<br>
	 * it does matter in which order the parameters are given. In a reverse
	 * order, the result vector will be orientated inverted<br>
	 * the result vector will be the third one for the right-hand-rule
	 * 
	 * @param a
	 *            the first vector
	 * @param b
	 *            the second vector
	 * @return an orthogonal vector to the two parameters
	 */
	public static Vector3d crossP(final Vector3d a,
			final Vector3d b) {
		return new Vector3d(a.y * b.z - b.y * a.z,
				b.x * a.z - a.x * b.z, a.x * b.y - b.x * a.y);
	}

	/**
	 * the dot product of two vectors is an double that can represent part of
	 * the angle between the parameters<br>
	 * the dotP is positive if the angle between the vectors is pointed, a
	 * negative value otherwise<br>
	 * if the parameter-vectors are orthogonal, the result will be 0<br>
	 * it does not matter in which order the parameters are given
	 * 
	 * @param a
	 *            the first vector
	 * @param b
	 *            the second vector
	 * @return the dot product of the vectors
	 */
	public static double dotP(final Vector3d a, final Vector3d b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	/**
	 * inverts this vector<br>
	 * this will point in the opposite direction, but with the same length
	 * 
	 * @return a this-reference
	 */
	public Vector3d invert() {
		return scale(-1);
	}

	/**
	 * calculates the angle between this and the parameter-vector in radiant<br>
	 * both vectors are direction-vectors and for that calculation interpreted
	 * as arrows laying on the 3d-space-origin<br>
	 * does not change the parameter
	 * 
	 * @param v
	 *            the vector to calculate the angle of this to
	 * @return the radiant-angle to the parameter-vector
	 */
	public double angleTo(final Vector3d v) {
		return Math.acos(this.dotP(v) / (this.length() * v.length()));
	}

	/**
	 * checks all coordinates to be 0d
	 * 
	 * @return true if so, false otherwise
	 */
	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	/**
	 * 
	 * @return true if the length of this is 1d, false otherwise
	 */
	public boolean isNormalized() {
		return lengthSquared() == 1;
	}

	/**
	 * the average vector is: <br>
	 * does not change the parameter<br>
	 * <ul>
	 * <li>the center position (if the given vectors were interpreted as
	 * positions)
	 * <li>the middle-direction (if the given vectors were interpreted as
	 * directions)
	 * </ul>
	 * 
	 * @param vecs
	 *            all vectors to calculate the average one
	 * @return the average vector of all given ones
	 */
	public static Vector3d calculateAverage(final Vector3d... vecs) {
		final Vector3d avg = Vector3d.zero();
		double count = 0;
		for (Vector3d v : vecs)
			if (v != null) {
				avg.add(v);
				count++;
			}
		return avg.scale(1d / count);
	}

	/**
	 * casts the x value to int
	 * 
	 * @return int-value of x
	 */
	public int getX() {
		return (int) x;
	}

	/**
	 * casts the y value to int
	 * 
	 * @return int-value of y
	 */
	public int getY() {
		return (int) y;
	}

	/**
	 * casts the z value to int
	 * 
	 * @return int-value of z
	 */
	public int getZ() {
		return (int) z;
	}

	/**
	 * copies the coordinates of the parameter to this<br>
	 * does not change the parameter
	 * 
	 * @param t
	 *            the new coordinates of this
	 * @return a this-reference
	 */
	public Vector3d set(final Vector3d t) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
		invalidateShaded();
		return this;
	}

	/**
	 * copies the coordinates of the parameter to this
	 * 
	 * @param x
	 *            the new x-coordinate of this
	 * @param y
	 *            the new y-coordinate of this
	 * @param z
	 *            the new z-coordinate of this
	 * @return a this-reference
	 */
	public Vector3d set(final double x, final double y,
			final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		invalidateShaded();
		return this;
	}

	/**
	 * sets all coordinates to 0
	 * 
	 * @return a this-reference
	 */
	public Vector3d setToZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		invalidateShaded();
		return this;
	}

	/**
	 * @return an independent vector where all coordinates are 0d
	 */
	public static Vector3d zero() {
		return new Vector3d(0, 0, 0);
	}

	/**
	 * @return an independent vector with x=y=z=1 and the length 1
	 */
	public static Vector3d one() {
		return new Vector3d(1, 1, 1).normalize();
	}

	/**
	 * @return an independent vector with x=z=0 and y=1
	 */
	public static Vector3d up() {
		return new Vector3d(0, 1, 0);
	}

	/**
	 * @return an independent vector with y=z=0 and x=1
	 */
	public static Vector3d right() {
		return new Vector3d(1, 0, 0);
	}

	/**
	 * @return an independent vector with x=y=0 and z=-1
	 */
	public static Vector3d forward() {
		return new Vector3d(0, 0, -1);
	}

	/**
	 * @return a random normalized vector
	 */
	public static Vector3d random() {
		return new Vector3d(Math.random() - .5, Math.random() - .5,
				Math.random() - .5).normalize();
	}

	/**
	 * @return a random normalized vector where z is 0
	 */
	public static Vector3d random2dxy() {
		return new Vector3d(Math.random() - .5, Math.random() - .5, 0)
				.normalize();
	}

	/**
	 * @return a random normalized vector where y is 0
	 */
	public static Vector3d random2dxz() {
		return new Vector3d(Math.random() - .5, 0, Math.random() - .5)
				.normalize();
	}

	/**
	 * @return a random normalized vector where x is 0
	 */
	public static Vector3d random2dyz() {
		return new Vector3d(0, Math.random() - .5, Math.random() - .5)
				.normalize();
	}

	/**
	 * rotates the vector around the z-axis in mathematically positive
	 * direction, means leaning<br>
	 * mathematically positive means counter-clockwise
	 * 
	 * @param rad
	 *            the radiant angle to rotate
	 * @return a this reference
	 */
	public Vector3d rotZ(final double rad) {
		MatrixCalculation.mul(MatrixCalculation.rotationMatrixZ(rad),
				this);
		invalidateShaded();
		return this;
	}

	/**
	 * rotates the vector around the x-axis in mathematically positive
	 * direction, means nicking<br>
	 * mathematically positive means nick up
	 * 
	 * @param rad
	 *            the radiant angle to rotate
	 * @return a this reference
	 */
	public Vector3d rotX(final double rad) {
		MatrixCalculation.mul(MatrixCalculation.rotationMatrixX(rad),
				this);
		invalidateShaded();
		return this;
	}

	/**
	 * rotates the vector around the y-axis in mathematically positive
	 * direction, means turning<br>
	 * mathematically positive means turn left
	 * 
	 * @param rad
	 *            the radiant angle to rotate
	 * @return a this reference
	 */
	public Vector3d rotY(final double rad) {
		MatrixCalculation.mul(MatrixCalculation.rotationMatrixY(rad),
				this);
		invalidateShaded();
		return this;
	}

	/**
	 * rotates this vector around the aligned axis in mathematically positive
	 * direction
	 * 
	 * @param axis
	 *            the aligned axis to turn around (need not to be normalized)
	 * @param rad
	 *            the radiant angle to rotate
	 * @return a this reference
	 */
	public Vector3d rot(final Vector3d axis, final double rad) {
		MatrixCalculation.mul(
				MatrixCalculation.rotationMatrix(axis, rad), this);
		invalidateShaded();
		return this;
	}

	/**
	 * just cut off the z-coordinate<br>
	 * 
	 * @return an independent 2 dimensional vector with the x and y coordinate
	 *         of this
	 */
	public Vector2d clipTo2dXY() {
		return new Vector2d(x, y);
	}

	/**
	 * just cut off the y-coordinate<br>
	 * 
	 * @return an independent 2 dimensional vector with the x and z coordinate
	 *         of this as the x (is this.x) and y (is this.z) coordinates
	 */
	public Vector2d clipTo2dXZ() {
		return new Vector2d(x, z);
	}

	/**
	 * just cut off the x-coordinate<br>
	 * 
	 * @return an independent 2 dimensional vector with the y and z coordinate
	 *         of this as the x (is this.y) and y (is this.z) coordinates
	 */
	public Vector2d clipTo2dYZ() {
		return new Vector2d(y, z);
	}

	/**
	 * constrains the coordinates to the given range<br>
	 * if a coordinate is lower than min, it will be set to min<br>
	 * if a coordinate is higher than max, it will be set to max<br>
	 * after this method call all coordinates will be within the interval
	 * [min,max], including the limits
	 * 
	 * @param min
	 *            the lower-limit
	 * @param max
	 *            the upper-limit
	 * @return a this reference
	 */
	public Vector3d constrain(final double min, final double max) {
		if (x < min)
			x = min;
		if (x > max)
			x = max;
		if (y < min)
			y = min;
		if (y > max)
			y = max;
		if (z < min)
			z = min;
		if (z > max)
			z = max;
		invalidateShaded();
		return this;
	}

	/**
	 * constrains the length to the given range<br>
	 * if this length is lower than min, it will be scaled to min<br>
	 * if this length is higher than max, it will be scaled to max<br>
	 * 
	 * @param min
	 *            the lower-limit
	 * @param max
	 *            the upper-limit
	 * @return a this reference
	 */
	public Vector3d constrainLength(final double min,
			final double max) {
		final double length = length();
		if (length < min)
			scaleTo(min);
		if (length > max)
			scaleTo(max);
		invalidateShaded();
		return this;
	}

	/**
	 * creates an independent vector with 1 as x-coordinate and 0 as all other
	 * coordinates
	 * 
	 * @return a vector with x=1, other coordinates = 0
	 */
	public static Vector3d x() {
		return new Vector3d(1, 0, 0);
	}

	/**
	 * creates an independent vector with 1 as y-coordinate and 0 as all other
	 * coordinates
	 * 
	 * @return a vector with y=1, other coordinates = 0
	 */
	public static Vector3d y() {
		return new Vector3d(0, 1, 0);
	}

	/**
	 * creates an independent vector with 1 as z-coordinate and 0 as all other
	 * coordinates
	 * 
	 * @return a vector with z=1, other coordinates = 0
	 */
	public static Vector3d z() {
		return new Vector3d(0, 0, 1);
	}

	/**
	 * converts this vectors Cartesian coordinates to polar coordinates<br>
	 * a polar coordinates representation is given by two angles (new
	 * x-coordinate as inclination (theta), new y-coordinate as azimuth (phi))
	 * and a distance (new z-coordinate) from the coordinate-center
	 * <p>
	 * toPolar().toCartesian() should cause no change unless a rounding mistake
	 * 
	 * @see #toCartesian()
	 * @return a this-reference
	 */
	public Vector3d toPolar() {
		final double distance = length();
		final double inclination = Math.acos(z / distance);
		final double azimuth = Math.atan(y / x);
		this.x = inclination;
		this.y = azimuth;
		this.z = distance;
		invalidateShaded();
		return this;
	}

	/**
	 * converts this vectors polar coordinates (x=inclination (theta), y=azimuth
	 * (phi), z=distance) to Cartesian coordinates
	 * <p>
	 * toCartesian().toPolar() should cause no change unless a rounding mistake
	 * 
	 * @see #toPolar()
	 * @return a this-reference
	 */
	public Vector3d toCartesian() {
		final double distance = z;
		final double inclination = x;
		final double azimuth = y;
		this.x = distance * Math.sin(inclination) * Math.cos(azimuth);
		this.y = distance * Math.sin(inclination) * Math.sin(azimuth);
		this.z = distance * Math.cos(inclination);
		invalidateShaded();
		return this;
	}

	/**
	 * checks whether the given vector is not null and all it's coordinates are
	 * finite
	 * 
	 * @param v
	 *            the vector to check
	 * @return whether the vector can be considered usable in general case
	 */
	public static boolean isFinite(final Vector3d v) {
		if (v == null)
			return false;
		return Double.isFinite(v.x) && Double.isFinite(v.y)
				&& Double.isFinite(v.z);
	}
}