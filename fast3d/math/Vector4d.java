package fast3d.math;

/**
 * a vector4d is a set of 4 double-coordinates, named x, y, z and a<br>
 * the vectors dimension determines it's count of coordinates<br>
 * a vector can be interpreted as a location or direction in it's 4d universe
 * 
 * @author Tim Trense
 */
public class Vector4d {

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
	 * the coordinate a of the vector<br>
	 * fourth dimensions coordinate
	 */

	public double a;

	/**
	 * constructs a new vector based on the given coordinates<br>
	 * 
	 * @param x
	 *            the x-coordinate of this new vector
	 * @param y
	 *            the y-coordinate of this new vector
	 * @param z
	 *            the z-coordinate of this new vector
	 * @param a
	 *            the a-coordinate of this new vector
	 */

	public Vector4d(final double x, final double y, final double z,
			final double a) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
	}

	@Override
	public String toString() {
		return "(" + x + ";" + y + ";" + z + ";" + a + ")";
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
		final double a = (int) (this.a * pow) / pow;
		return "(" + x + ";" + y + ";" + z + ";" + a + ")";
	}

	@Override
	public Vector4d clone() {
		return new Vector4d(x, y, z, a);
	}

	/**
	 * @see #setCoordinates(double[])
	 * @return the vectors coordinates as an array
	 */
	public double[] getCoordinates() {
		return new double[] { x, y, z, a };
	}

	/**
	 * counterpart to getCoordinates<br>
	 * sets the coordinates of this to x=[0], y=[1], z=[2], a=[3]
	 * 
	 * @see #getCoordinates()
	 * @param vec
	 *            a this reference
	 * @return a this reference
	 */
	public Vector4d setCoordinates(final double[] vec) {
		this.x = vec[0];
		this.y = vec[1];
		this.z = vec[2];
		return this;
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
	public Vector4d scale(final double k) {
		x *= k;
		y *= k;
		z *= k;
		a *= k;
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
	public Vector4d scaleTo(final double length) {
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
	public Vector4d scaleTo(final Vector4d compare) {
		return scaleTo(compare.length());
	}

	/**
	 * scales the vector to the length 1<br>
	 * fails (does nothing than) for the zero-vector
	 * 
	 * @return a this-reference
	 */
	public Vector4d normalize() {
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
		if (obj instanceof Vector4d) {
			final Vector4d other = (Vector4d) obj;
			return this.x == other.x && this.y == other.y
					&& this.z == other.z && this.a == other.a;
		} else
			return false;
	}

	/**
	 * will always return 4<br>
	 * returns the length of the array given by getCoordinates();
	 * 
	 * @see #getCoordinates()
	 * @return 4
	 */
	public final int getDimension() {
		return 4;
	}

	/**
	 * adds the coordinates of the parameter to the coordinates of this<br>
	 * does not change the parameter
	 * 
	 * @param t
	 *            the coordinates to add (will not be changed), not null
	 * @return a this-reference
	 */
	public Vector4d add(final Vector4d t) {
		x += t.x;
		y += t.y;
		z += t.z;
		a += t.a;
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
	public Vector4d multiply(final Vector4d t) {
		x *= t.x;
		y *= t.y;
		z *= t.z;
		a *= t.a;
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
	public Vector4d sub(final Vector4d t) {
		x -= t.x;
		y -= t.y;
		z -= t.z;
		a -= t.a;
		return this;
	}

	/**
	 * does not change the parameter
	 * 
	 * @param t
	 *            the location to calculate the direction from this location to
	 * @return the difference vector from this to the parameter
	 */
	public Vector4d to(final Vector4d t) {
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
		return x * x + y * y + z * z + a * a;
	}

	/**
	 * calculates the length of the difference vector given by to(target)<br>
	 * does not change the parameter
	 * 
	 * @param target
	 *            the parameter to give to to(Vector)
	 * @return the length of the difference-vector
	 */
	public double distanceTo(final Vector4d target) {
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
	public double distanceToSquared(final Vector4d target) {
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
	public boolean isParallel(final Vector4d v) {
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
	public static double getRatio(final Vector4d a,
			final Vector4d b) {
		final double ratioX = a.x / b.x;
		final double ratioY = a.y / b.y;
		final double ratioZ = a.z / b.z;
		final double ratioA = a.a / b.a;
		final boolean finX = Double.isFinite(ratioX);
		final boolean finY = Double.isFinite(ratioY);
		final boolean finZ = Double.isFinite(ratioZ);
		final boolean finA = Double.isFinite(ratioA);
		if (!finX && a.x != 0)
			return Double.NaN;
		if (!finY && a.y != 0)
			return Double.NaN;
		if (!finZ && a.z != 0)
			return Double.NaN;
		if (!finA && a.a != 0)
			return Double.NaN;
		if (finX && finY && finZ && finA)
			if (ratioX == ratioY && ratioX == ratioZ
					&& ratioX == ratioA)
				return ratioX;
			else
				return Double.NaN;
		else {
			if (!finA)
				return Vector3d.getRatio(new Vector3d(a.x, a.y, a.z),
						new Vector3d(b.x, b.y, b.z));
			else if (!finX)
				return Vector3d.getRatio(new Vector3d(a.a, a.y, a.z),
						new Vector3d(b.a, b.y, b.z));
			else if (!finY)
				return Vector3d.getRatio(new Vector3d(a.x, a.a, a.z),
						new Vector3d(b.x, b.a, b.z));
			else // then z must be not finite
				return Vector3d.getRatio(new Vector3d(a.x, a.y, a.a),
						new Vector3d(b.x, b.y, b.a));
		}
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
	public boolean isSameOrientated(final Vector4d v) {
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
	public double dotP(final Vector4d v) {
		return this.x * v.x + this.y * v.y + this.z * v.z
				+ this.a * v.a;
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
	public static double dotP(final Vector4d a, final Vector4d b) {
		return a.x * b.x + a.y * b.y + a.z * b.z + a.a * b.a;
	}

	/**
	 * inverts this vector<br>
	 * this will point in the opposite direction, but with the same length
	 * 
	 * @return a this-reference
	 */
	public Vector4d invert() {
		return scale(-1);
	}

	/**
	 * calculates the angle between this and the parameter-vector in radiant<br>
	 * both vectors are direction-vectors and for that calculation interpreted
	 * as arrows laying on the 4d-space-origin<br>
	 * does not change the parameter
	 * 
	 * @param v
	 *            the vector to calculate the angle of this to
	 * @return the radiant-angle to the parameter-vector
	 */
	public double angleTo(final Vector4d v) {
		return Math.acos(this.dotP(v) / (this.length() * v.length()));
	}

	/**
	 * checks all coordinates to be 0d
	 * 
	 * @return true if so, false otherwise
	 */
	public boolean isZero() {
		return x == 0 && y == 0 && z == 0 && a == 0;
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
	public static Vector4d calculateAverage(final Vector4d... vecs) {
		final Vector4d avg = Vector4d.zero();
		double count = 0;
		for (Vector4d v : vecs)
			if (v != null) {
				avg.add(v);
				count++;
			}
		return avg.scale(1d / count);
	}

	/**
	 * @return an independent vector where all coordinates are 0d
	 */
	public static Vector4d zero() {
		return new Vector4d(0, 0, 0, 0);
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
	 * casts the a value to int
	 * 
	 * @return int-value of a
	 */
	public int getA() {
		return (int) a;
	}

	/**
	 * copies the coordinates of the parameter to this<br>
	 * does not change the parameter
	 * 
	 * @param t
	 *            the new coordinates of this
	 * @return a this-reference
	 */
	public Vector4d set(final Vector4d t) {
		this.x = t.x;
		this.y = t.y;
		this.z = t.z;
		this.a = t.a;
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
	 * @param a
	 *            the new a-coordinate of this
	 * @return a this-reference
	 */
	public Vector4d set(final double x, final double y,
			final double z, final double a) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
		return this;
	}

	/**
	 * sets all coordinates to 0
	 * 
	 * @return a this-reference
	 */
	public Vector4d setToZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.a = 0;
		return this;
	}

	/**
	 * @return an independent vector with x=y=z=1 and the length 1
	 */
	public static Vector4d one() {
		return new Vector4d(1, 1, 1, 1).normalize();
	}

	/**
	 * @return a random normalized vector
	 */
	public static Vector4d random() {
		return new Vector4d(Math.random() - .5, Math.random() - .5,
				Math.random() - .5, Math.random() - .5).normalize();
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
	public Vector4d constrain(final double min, final double max) {
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
		if (a < min)
			a = min;
		if (a > max)
			a = max;
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
	public Vector4d constrainLength(final double min,
			final double max) {
		final double length = length();
		if (length < min)
			scaleTo(min);
		if (length > max)
			scaleTo(max);
		return this;
	}

	/**
	 * creates an independent vector with 1 as x-coordinate and 0 as all other
	 * coordinates
	 * 
	 * @return a vector with x=1, other coordinates = 0
	 */
	public static Vector4d x() {
		return new Vector4d(1, 0, 0, 0);
	}

	/**
	 * creates an independent vector with 1 as y-coordinate and 0 as all other
	 * coordinates
	 * 
	 * @return a vector with y=1, other coordinates = 0
	 */
	public static Vector4d y() {
		return new Vector4d(0, 1, 0, 0);
	}

	/**
	 * creates an independent vector with 1 as z-coordinate and 0 as all other
	 * coordinates
	 * 
	 * @return a vector with z=1, other coordinates = 0
	 */
	public static Vector4d z() {
		return new Vector4d(0, 0, 1, 0);
	}

	/**
	 * creates an independent vector with 1 as a-coordinate and 0 as all other
	 * coordinates
	 * 
	 * @return a vector with a=1, other coordinates = 0
	 */
	public static Vector4d a() {
		return new Vector4d(0, 0, 0, 1);
	}

	/**
	 * checks whether the given vector is not null and all it's coordinates are
	 * finite
	 * 
	 * @param v
	 *            the vector to check
	 * @return whether the vector can be considered usable in general case
	 */
	public static boolean isFinite(final Vector4d v) {
		if (v == null)
			return false;
		return Double.isFinite(v.x) && Double.isFinite(v.y)
				&& Double.isFinite(v.z) && Double.isFinite(v.a);
	}
}