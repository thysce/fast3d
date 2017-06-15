package fast3d.util.math;

import java.util.Random;

/**
 * a simple value-noise generator<br>
 * noise means smooth, organic randomness<br>
 * the static random field gives the used random-generator<br>
 * seeds may be set by the random field before every method call<br>
 * a noise generation is a recursive function: for every step the interval and
 * the maximum will be halved and a new array is generated with those parameter
 * to be add to the given one<br>
 * the interval defines how many random values will be generated - for the other
 * indices of the array those values are interpolated
 * 
 * @author Tim Trense
 */
public class Noise {

	/**
	 * the random generator used to calculate the noise
	 */
	public static final Random random = new Random();

	/**
	 * fills the given array with noise values within the range [-max;max]
	 * 
	 * @param array
	 *            the array to be filled with noise values
	 * @param interval
	 *            the initial resolution
	 * @param minInterval
	 *            the final/result resolution
	 * @param max
	 *            the highest possible value of the array
	 */
	public static void generate(final double[] array, final int interval,
			final int minInterval, final double max) {
		final double[] arr = new double[array.length + interval];
		int last = 0;
		for (int i = 0; i < arr.length; i += interval) {
			arr[i] = random.nextDouble() * max - max / 2;
			final double delta = arr[i] - arr[last];
			final double step = delta / (i - last);
			double value = arr[i];
			for (int j = i - 1; j > last; j--) {
				value -= step;
				arr[j] = value;
			}
			last = i;
		}

		for (int i = 0; i < array.length; i++) {
			array[i] += arr[i];
		}

		final int nextInterval = interval / 2;
		if (nextInterval >= minInterval)
			generate(array, nextInterval, minInterval, max / 2);
	}

	/**
	 * fills a new array with noise values within the range [-max;max]
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @param interval
	 *            the initial resolution
	 * @param minInterval
	 *            the final/result resolution
	 * @param max
	 *            the highest possible value of the array
	 * @return the generated array
	 */
	public static double[] generate(final int length, final int interval,
			final int minInterval, final double max) {
		final double[] arr = new double[length];
		generate(arr, interval, minInterval, max);
		return arr;

	}

	/**
	 * fills a new array with noise values within the range [-max;max]<br>
	 * the initial interval will be length / 4 and the final interval 4
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @param max
	 *            the highest possible value of the array
	 * @return the generated array
	 */
	public static double[] generate(final int length, final double max) {
		return generate(length, length / 4, 4, max);
	}

	/**
	 * fills a new array with noise values within the range [-1;1]<br>
	 * the initial interval will be length / 4 and the final interval 4
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @return the generated array
	 */
	public static double[] generate(final int length) {
		return generate(length, 1);
	}

	/**
	 * fills the given array with noise values within the range [-max;max]
	 * 
	 * @param array
	 *            the array to be filled with noise values
	 * @param interval
	 *            the initial resolution
	 * @param minInterval
	 *            the final/result resolution
	 * @param max
	 *            the highest possible value of the array
	 */
	public static void generate2d(final double[][] array, final int interval,
			final int minInterval, final double max) {

		final double[][] arr = new double[array.length + interval][array[0].length];
		int last = 0;
		for (int x = interval; x < arr.length; x += interval) {
			generate(arr[x], interval, minInterval, max);
			for (int y = 0; y < arr[x].length; y++) {
				final double delta = arr[x][y] - arr[last][y];
				final double step = delta / (x - last);
				double value = arr[x][y];
				for (int j = x - 1; j > last; j--) {
					value -= step;
					arr[j][y] = value;
				}
			}
			last = x;
		}

		for (int x = 0; x < array.length; x++) {
			for (int y = 0; y < array[x].length; y++) {
				array[x][y] += arr[x][y];
			}
		}
		final int nextInterval = interval / 2;
		if (nextInterval >= minInterval) {
			generate2d(array, nextInterval, minInterval, max / 2);
		}
	}

	/**
	 * fills a new array with noise values within the range [-max;max]
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @param interval
	 *            the initial resolution
	 * @param minInterval
	 *            the final/result resolution
	 * @param max
	 *            the highest possible value of the array
	 * @return the generated array
	 */
	public static double[][] generate2d(final int length, final int interval,
			final int minInterval, final double max) {
		final double[][] arr = new double[length][length];
		generate2d(arr, interval, minInterval, max);
		return arr;
	}

	/**
	 * fills a new array with noise values within the range [-max;max]<br>
	 * the initial interval will be length / 4 and the final interval 4
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @param max
	 *            the highest possible value of the array
	 * @return the generated array
	 */
	public static double[][] generate2d(final int length, final double max) {
		return generate2d(length, length / 4, 4, max);
	}

	/**
	 * fills a new array with noise values within the range [-1;1]<br>
	 * the initial interval will be length / 4 and the final interval 4
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @return the generated array
	 */
	public static double[][] generate2d(final int length) {
		return generate2d(length, 1);
	}

	/**
	 * fills the given array with noise values within the range [-max;max]
	 * 
	 * @param array
	 *            the array to be filled with noise values
	 * @param interval
	 *            the initial resolution
	 * @param minInterval
	 *            the final/result resolution
	 * @param max
	 *            the highest possible value of the array
	 */
	public static void generate3d(final double[][][] array, final int interval,
			final int minInterval, final double max) {

		final int length2d = array.length;
		final double[][][] arr = new double[length2d + interval][length2d][length2d];
		int last = 0;
		for (int z = interval; z < arr.length; z += interval) {
			generate2d(arr[z], interval, minInterval, max);

			for (int x = 0; x < length2d; x++)
				for (int y = 0; y < length2d; y++) {
					final double delta = arr[z][x][y] - arr[last][x][y];
					final double step = delta / (z - last);
					double value = arr[z][x][y];
					for (int j = z - 1; j > last; j--) {
						value -= step;
						arr[j][x][y] = value;
					}
				}
			last = z;
		}

		for (int x = 0; x < array.length; x++)
			for (int y = 0; y < array[x].length; y++)
				for (int z = 0; z < array[x][y].length; z++) {
					array[x][y][z] += arr[x][y][z];
				}

		final int nextInterval = interval / 2;
		if (nextInterval >= minInterval) {
			generate3d(array, nextInterval, minInterval, max / 2);
		}
	}

	/**
	 * fills a new array with noise values within the range [-max;max]
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @param interval
	 *            the initial resolution
	 * @param minInterval
	 *            the final/result resolution
	 * @param max
	 *            the highest possible value of the array
	 * @return the generated array
	 */
	public static double[][][] generate3d(final int length, final int interval,
			final int minInterval, final double max) {
		final double[][][] arr = new double[length][length][length];
		generate3d(arr, interval, minInterval, max);
		return arr;
	}

	/**
	 * fills a new array with noise values within the range [-max;max]<br>
	 * the initial interval will be length / 4 and the final interval 4
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @param max
	 *            the highest possible value of the array
	 * @return the generated array
	 */
	public static double[][][] generate3d(final int length, final double max) {
		return generate3d(length, length / 4, 4, max);
	}

	/**
	 * fills a new array with noise values within the range [-1;1]<br>
	 * the initial interval will be length / 4 and the final interval 4
	 * 
	 * @param length
	 *            the length of the result array to be filled with noise values
	 * @return the generated array
	 */
	public static double[][][] generate3d(final int length) {
		return generate3d(length, 1);
	}

}