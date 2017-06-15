package fast3d.math;

/**
 * a vector2d is a set of 2 double-coordinates, named x and y<br>
 * the vectors dimension determines it's count of coordinates<br>
 * a vector can be interpreted as a location or direction in it's 2d universe
 * (Cartesian coordinate system)
 * 
 * @author Tim Trense
 */
public class Vector2d {

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
	 * constructs using the getX() and getY() methods of the awt-point
	 * 
	 * @param point
	 *            the awt-vector
	 */
	public Vector2d(final java.awt.Point point) {
		this(point.getX(), point.getY());
	}

	/**
	 * constructs using getWidth() for x and getHeight() for y of the
	 * awt-dimension
	 * 
	 * @param dim
	 *            the awt-vector
	 */
	public Vector2d(final java.awt.Dimension dim) {
		this(dim.getWidth(), dim.getHeight());
	}

	/**
	 * constructs a new vector based on the given coordinates<br>
	 * 
	 * @param x
	 *            the x-coordinate of this new vector
	 * @param y
	 *            the y-coordinate of this new vector
	 */

	public Vector2d(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + ";" + y + ")";
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
		return "(" + x + ";" + y + ")";
	}

	@Override
	public Vector2d clone() {
		return new Vector2d(x, y);
	}

	/**
	 * @see #setCoordinates(double[])
	 * @return the vectors coordinates as an array
	 */
	public double[] getCoordinates() {
		return new double[] { x, y };
	}

	/**
	 * counterpart to getCoordinates<br>
	 * sets the coordinates of this to x=[0], y=[1]
	 * 
	 * @see #getCoordinates()
	 * @param vec
	 *            a this reference
	 * @return a this reference
	 */
	public Vector2d setCoordinates(final double[] vec) {
		this.x = vec[0];
		this.y = vec[1];
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
	public Vector2d scale(final double k) {
		x *= k;
		y *= k;
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
	public Vector2d scaleTo(final double length) {
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
	public Vector2d scaleTo(final Vector2d compare) {
		return scaleTo(compare.length());
	}

	/**
	 * scales the vector to the length 1<br>
	 * fails (does nothing than) for the zero-vector
	 * 
	 * @return a this-reference
	 */
	public Vector2d normalize() {
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
		if (obj instanceof Vector2d) {
			final Vector2d other = (Vector2d) obj;
			return this.x == other.x && this.y == other.y;
		} else
			return false;
	}

	/**
	 * will always return 2<br>
	 * returns the length of the array given by getCoordinates();
	 * 
	 * @see #getCoordinates()
	 * @return 2
	 */
	public final int getDimension() {
		return 2;
	}

	/**
	 * adds the coordinates of the parameter to the coordinates of this<br>
	 * does not change the parameter
	 * 
	 * @param t
	 *            the coordinates to add (will not be changed), not null
	 * @return a this-reference
	 */
	public Vector2d add(final Vector2d t) {
		x += t.x;
		y += t.y;
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
	public Vector2d multiply(final Vector2d t) {
		x *= t.x;
		y *= t.y;
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
	public Vector2d sub(final Vector2d t) {
		x -= t.x;
		y -= t.y;
		return this;
	}

	/**
	 * does not change the parameter
	 * 
	 * @param t
	 *            the location to calculate the direction from this location to
	 * @return the difference vector from this to the parameter
	 */
	public Vector2d to(final Vector2d t) {
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
		return x * x + y * y;
	}

	/**
	 * calculates the length of the difference vector given by to(target)<br>
	 * does not change the parameter
	 * 
	 * @param target
	 *            the parameter to give to to(Vector)
	 * @return the length of the difference-vector
	 */
	public double distanceTo(final Vector2d target) {
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
	public double distanceToSquared(final Vector2d target) {
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
	public boolean isParallel(final Vector2d v) {
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
	public static double getRatio(final Vector2d a,
			final Vector2d b) {
		final double ratioX = a.x / b.x;
		final double ratioY = a.y / b.y;
		final boolean finX = Double.isFinite(ratioX);
		final boolean finY = Double.isFinite(ratioY);
		if (!finX && a.x != 0)
			return Double.NaN;
		if (!finY && a.y != 0)
			return Double.NaN;
		if (finX && finY)
			if (ratioX == ratioY)
				return ratioX;
			else
				return Double.NaN;
		else {
			if (finX)
				return ratioX;
			if (finY)
				return ratioY;
			return 0;
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
	public boolean isSameOrientated(final Vector2d v) {
		final double ratio = getRatio(this, v);
		return Double.isFinite(ratio) && ratio >= 0;
	}

	/**
	 * calculates the dot-product of this and the parameter<br>
	 * does not change the parameter
	 * 
	 * @param v
	 *            the vector to calculate the dot-product with
	 * @return the dot-product between this and the parameter
	 */
	public double dotP(final Vector2d v) {
		return this.x * v.x + this.y * v.y;
	}

	/**
	 * calculates the dot-product of this and the parameter<br>
	 * does not change the parameter
	 * 
	 * @param a
	 *            the first vector to calculate the dot-product with
	 * @param b
	 *            the second vector to calculate the dot-product with
	 * @return the dot-product between a and b
	 */
	public static double dotP(final Vector2d a, final Vector2d b) {
		return a.x * b.x + a.y * b.y;
	}

	/**
	 * inverts this vector<br>
	 * this will point in the opposite direction, but with the same length
	 * 
	 * @return a this-reference
	 */
	public Vector2d invert() {
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
	public double angleTo(final Vector2d v) {
		return Math.acos(this.dotP(v) / (this.length() * v.length()));
	}

	/**
	 * checks all coordinates to be 0d
	 * 
	 * @return true if so, false otherwise
	 */
	public boolean isZero() {
		return x == 0 && y == 0;
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
	public static Vector2d calculateAverage(final Vector2d... vecs) {
		final Vector2d avg = Vector2d.zero();
		double count = 0;
		for (Vector2d v : vecs)
			if (v != null) {
				avg.add(v);
				count++;
			}
		return avg.scale(1d / count);
	}

	/**
	 * @return an independent vector where all coordinates are 0d
	 */
	public static Vector2d zero() {
		return new Vector2d(0, 0);
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
	 * copies the coordinates of the parameter to this<br>
	 * does not change the parameter
	 * 
	 * @param t
	 *            the new coordinates of this
	 * @return a this-reference
	 */
	public Vector2d set(final Vector2d t) {
		this.x = t.x;
		this.y = t.y;
		return this;
	}

	/**
	 * copies the coordinates of the parameter to this
	 * 
	 * @param x
	 *            the new x-coordinate of this
	 * @param y
	 *            the new y-coordinate of this
	 * @return a this-reference
	 */
	public Vector2d set(final double x, final double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * sets all coordinates to 0
	 * 
	 * @return a this-reference
	 */
	public Vector2d setToZero() {
		this.x = 0;
		this.y = 0;
		return this;
	}

	/**
	 * @return an independent vector with x=y=1 and the length 1
	 */
	public static Vector2d one() {
		return new Vector2d(1, 1).normalize();
	}

	/**
	 * @return an independent vector with x=0 and y=1
	 */
	public static Vector2d up() {
		return new Vector2d(0, 1);
	}

	/**
	 * @return an independent vector with y=0 and x=1
	 */
	public static Vector2d right() {
		return new Vector2d(1, 0);
	}

	/**
	 * @return a random normalized vector
	 */
	public static Vector2d random() {
		return new Vector2d(Math.random() - .5, Math.random() - .5)
				.normalize();
	}

	/**
	 * @return the angle of this vector to the Cartesian coordinate systems
	 *         positive x-axis
	 */
	public double angle() {
		if (x > 0) {
			if (y == 0)
				return 0;
			if (y > 0)
				return Math.atan(y / x);
			if (y < 0)
				return (Math.PI * 2) - Math.atan(Math.abs(y / x));
		}
		if (x == 0) {
			if (y == 0)
				return 0;
			if (y > 0)
				return Math.PI / 2;
			if (y < 0)
				return (Math.PI / 2) * 3;
		}
		if (x < 0) {
			if (y == 0)
				return Math.PI;
			if (y > 0)
				return Math.PI - Math.atan(Math.abs(y / x));
			if (y < 0)
				return Math.PI + Math.atan(y / x);
		}
		return 0;
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
	public Vector2d constrain(final double min, final double max) {
		if (x < min)
			x = min;
		if (x > max)
			x = max;
		if (y < min)
			y = min;
		if (y > max)
			y = max;
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
	public Vector2d constrainLength(final double min,
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
	public static Vector2d x() {
		return new Vector2d(1, 0);
	}

	/**
	 * creates an independent vector with 1 as y-coordinate and 0 as all other
	 * coordinates
	 * 
	 * @return a vector with y=1, other coordinates = 0
	 */
	public static Vector2d y() {
		return new Vector2d(0, 1);
	}

	/**
	 * converts this vectors Cartesian coordinates to polar coordinates<br>
	 * a polar coordinates representation is given by an angle (new
	 * x-coordinate) and a distance (new y-coordinate) from the
	 * coordinate-center
	 * <p>
	 * toPolar().toCartesian() should cause no change unless a rounding mistake
	 * 
	 * @see #toCartesian()
	 * @return a this-reference
	 */
	public Vector2d toPolar() {
		final double angle = angle();
		final double distance = length();
		this.x = angle;
		this.y = distance;
		return this;
	}

	/**
	 * converts this vectors polar coordinates (x=angle, y=distance) to
	 * Cartesian coordinates
	 * <p>
	 * toCartesian().toPolar() should cause no change unless a rounding mistake
	 * 
	 * @see #toPolar()
	 * @return a this-reference
	 */
	public Vector2d toCartesian() {
		final double angle = x;
		final double distance = y;
		this.x = distance * Math.cos(angle);
		this.y = distance * Math.sin(angle);
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
	public static boolean isFinite(final Vector2d v) {
		if (v == null)
			return false;
		return Double.isFinite(v.x) && Double.isFinite(v.y);
	}
}