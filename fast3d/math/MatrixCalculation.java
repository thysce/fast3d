package fast3d.math;

/**
 * Calculation Class for static usage<br>
 * a matrix is a 2dim- double-array with mostly/preferred a same length of first
 * and second dimension<br>
 * a matrixes height is the length of the first dimension, it's width the second
 * dimensions length<br>
 * the second dimensions length has to be the same for all of the first
 * dimensions entries
 * 
 * @author Tim Trense
 */
public abstract class MatrixCalculation {

	/**
	 * a value to determine what a zero in a matrix should be turn into, to
	 * prevent zero-locks<br>
	 * should be zero for lighting calculation<br>
	 * default = 0.0d<br>
	 * zeroLocks will probably not happen any more
	 */
	@Deprecated
	public static double zeroLockControl = 0.0d;

	/**
	 * a wrapper to solveLinearEquationMatrix(matrix) with check to zero-locks
	 * 
	 * @param matrix
	 *            a two-dim. array with the first dimension as its height
	 * @return the result of the LES as x=[0], y=[1], ...
	 */
	@Deprecated
	public static double[] solveLinearEquationSystemZLC(final double[][] matrix) {
		// clean zero-lock
		if (zeroLockControl != 0)
			for (int x = 0; x < matrix.length; x++)
				for (int y = 0; y < matrix[x].length; y++)
					if (matrix[x][y] == 0)
						matrix[x][y] = zeroLockControl;
		rref(matrix);
		final double[] res = new double[matrix.length];
		for (int line = 0; line < matrix.length; line++)
			res[line] = matrix[line][matrix[line].length - 1] / matrix[line][line];
		return res;
	}

	/**
	 * @param rad
	 *            the angle for rotation around the y axis in mathematically
	 *            positive direction in radiant
	 * @return the matrix to multiply with the vector to rotate
	 */
	public static double[][] rotationMatrixY(final double rad) {
		return new double[][] { { Math.cos(rad), 0, Math.sin(rad) }, { 0, 1, 0 },
				{ -Math.sin(rad), 0, Math.cos(rad) } };
	}

	/**
	 * @param rad
	 *            the angle for rotation around the x axis in mathematically
	 *            positive direction in radiant
	 * @return the matrix to multiply with the vector to rotate
	 */
	public static double[][] rotationMatrixX(final double rad) {
		return new double[][] { { 1, 0, 0 }, { 0, Math.cos(rad), -Math.sin(rad) },
				{ 0, Math.sin(rad), Math.cos(rad) } };
	}

	/**
	 * @param rad
	 *            the angle for rotation around the z axis in mathematically
	 *            positive direction in radiant
	 * @return the matrix to multiply with the vector to rotate
	 */
	public static double[][] rotationMatrixZ(final double rad) {
		return new double[][] { { Math.cos(rad), -Math.sin(rad), 0 },
				{ Math.sin(rad), Math.cos(rad), 0 }, { 0, 0, 1 } };
	}

	/**
	 * @param axis
	 *            the vector multiplied with the returned matrix will be turned
	 *            around that given axis in mathematical positive direction
	 * @param rad
	 *            the angle for rotation around the given axis in mathematically
	 *            positive direction in radiant
	 * @return the matrix to multiply with the vector to rotate
	 */
	public static double[][] rotationMatrix(Vector3d axis, final double rad) {
		axis = axis.clone().normalize();
		final double cosRad = Math.cos(rad);
		final double sinRad = Math.sin(rad);
		return new double[][] { rotationMatrixRow1(axis, cosRad, sinRad),
				rotationMatrixRow2(axis, cosRad, sinRad),
				rotationMatrixRow3(axis, cosRad, sinRad) };
	}

	private static double[] rotationMatrixRow1(final Vector3d axis, final double cosRad,
			final double sinRad) {
		return new double[] { axis.x * axis.x * (1 - cosRad) + cosRad,
				axis.x * axis.y * (1 - cosRad) - axis.z * sinRad,
				axis.x * axis.z * (1 - cosRad) + axis.y * sinRad };
	}

	private static double[] rotationMatrixRow2(final Vector3d axis, final double cosRad,
			final double sinRad) {
		return new double[] { axis.x * axis.y * (1 - cosRad) + axis.z * sinRad,
				axis.y * axis.y * (1 - cosRad) + cosRad,
				axis.y * axis.z * (1 - cosRad) - axis.x * sinRad };
	}

	private static double[] rotationMatrixRow3(final Vector3d axis, final double cosRad,
			final double sinRad) {
		return new double[] { axis.x * axis.z * (1 - cosRad) - axis.y * sinRad,
				axis.y * axis.z * (1 - cosRad) + axis.x * sinRad,
				axis.z * axis.z * (1 - cosRad) + cosRad };
	}

	/**
	 * m is (y X x) - matrix. m.length==y , m[0].length==x<br>
	 * multiplies the given vector-coordinates with the matrix as defined
	 * mathematical <br>
	 * the matrixes dimensions length has to equal the length of the given
	 * vector-array
	 * 
	 * @param m
	 *            the matrix
	 * @param vec
	 *            the vector which will not be changed
	 * @return the multiplied vector
	 */
	public static double[] mul(final double[][] m, final double[] vec) {
		if (m.length != vec.length)
			return null;
		final double[] res = new double[m.length];
		// i is result-coordinate index and matrix-line
		for (int i = 0; i < res.length; i++) {
			res[i] = 0;
			// l is matrix-row and vector-coordinate
			for (int l = 0; l < m[i].length; l++)
				res[i] += m[i][l] * vec[l];
		}
		return res;
	}

	/**
	 * a faster way for a vector-instance to be multiplied and not to generate
	 * an array of its coordinates <br>
	 * the matrixes dimension length has to be 3
	 * 
	 * @param m
	 *            the matrix to multiply the vector with
	 * @param vec
	 *            the vector to be multiplied with the matrix
	 */
	public static void mul(final double[][] m, final Vector3d vec) {
		final double x = (m[0][0] * vec.x) + (m[0][1] * vec.y) + (m[0][2] * vec.z);
		final double y = (m[1][0] * vec.x) + (m[1][1] * vec.y) + (m[1][2] * vec.z);
		final double z = (m[2][0] * vec.x) + (m[2][1] * vec.y) + (m[2][2] * vec.z);
		vec.x = x;
		vec.y = y;
		vec.z = z;
	}

	/**
	 * edits the parameter<br>
	 * reduced row echelon form (rref)<br>
	 * the result of a linear equation system can be determined directly if its
	 * matrix is in the rref <br>
	 * at this point it is magic
	 * 
	 * @param matrix
	 *            the matrix of the LES
	 */
	public static void rref(final double[][] matrix) {
		// go through every col
		for (int row = 0, col = 0; row < matrix.length; row++, col++) {
			// if first element in current col is zero change the current row
			// with another
			if (matrix[row][col] == 0d) {
				for (int i = row; i < matrix.length; i++)
					if (matrix[i][col] != 0.0) {
						final double[] help = matrix[i];
						matrix[i] = matrix[row];
						matrix[row] = help;
						break;
					}
				// if change was unsuccessful because every row starts with a
				// zero beginning with the current col, go to the next col and
				// retry
				if (matrix[row][col] == 0d) {
					col++;
					continue;
				}
			}
			// reduce the first index of the row beginning with the col to 1 and
			// apply the conversion to every other element of the row - that
			// means multiply the equation so that the first summand has a
			// coefficient of 1
			if (matrix[row][col] != 1.0) {
				final double divisor = matrix[row][col];
				for (int i = col; i < matrix[row].length; i++) {
					matrix[row][i] = matrix[row][i] / divisor;
				}
			}
			// find other equation lines thats first element beginning from the
			// current col aren't zero
			for (int i = 0; i < matrix.length; i++) {
				if (i != row && matrix[i][col] != 0) {
					// do the addition process with those lines
					final double multiply = 0 - matrix[i][col];
					for (int j = col; j < matrix[row].length; j++) {
						matrix[i][j] += multiply * matrix[row][j];
					}
				}
			}
		}
	}

	/**
	 * a linear equation system (LES) has the row-form aX+bY+...=C<br>
	 * the LES has to have one row per variable to solve<br>
	 * the coefficients of the variables are entries of the matrix; C is the
	 * last entry of each line and a constant<br>
	 * 
	 * @param matrix
	 *            a two-dim. array with the first dimension as its height
	 * @return the result of the LES as x=[0], y=[1], ...
	 */
	public static double[] solveLinearEquationSystem(final double[][] matrix) {
		rref(matrix);
		final double[] res = new double[matrix.length];
		for (int line = 0; line < matrix.length; line++)
			res[line] = matrix[line][matrix[line].length - 1] / matrix[line][line];
		return res;
	}

}