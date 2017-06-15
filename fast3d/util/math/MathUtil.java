package fast3d.util.math;

/**
 * giving functionality commonly used but not given by java.lang.Math
 * 
 * @author Tim Trense
 */
public abstract class MathUtil {

	/**
	 * euler's number - base of the natural logarithm - 2.718281828459045
	 */
	public static final double e = Math.E;

	/**
	 * the ratio of the circumference of a circle to its diameter -
	 * 3.141592653589793 - 180 degrees
	 */
	public static final double pi = Math.PI;
	/**
	 * the ratio of the circumference of a circle to its radius -
	 * 6.283185307179586 - 360 degrees
	 */
	public static final double twoPi = Math.PI * 2;

	/**
	 * halve pi - 1.5707963267948965 - 90 degrees
	 * 
	 * @see #pi
	 */
	public static final double piOver2 = Math.PI / 2;

	/**
	 * fourth part of pi - 0.78539816339744825 - 45 degrees
	 * 
	 * @see #pi
	 */
	public static final double piOver4 = Math.PI / 4;
	
	/**
	 * fourth part of pi - 0.39269908169872415 - 22.5 degrees
	 * 
	 * @see #pi
	 */
	public static final double piOver8 = Math.PI / 8;

	/**
	 * the value within the interval of [originMin,originMax] will be at the
	 * same location relative to the new interval [rangeMin,rangeMax] as result
	 * <p>
	 * example:<br>
	 * map(value=20, originMin=0, originMax=40, intervalMin=0, intervalMin=20)
	 * will result in 10<br>
	 * map(value=20, originMin=0, originMax=40, intervalMin=0, intervalMin=100)
	 * will result in 50<br>
	 * map(value=3, originMin=3, originMax=4, intervalMin=0, intervalMin=100)
	 * will result in 0<br>
	 * map(value=3, originMin=2, originMax=4, intervalMin=50, intervalMin=100)
	 * will result in 75<br>
	 * 
	 * @param value
	 *            the double to be mapped
	 * @param originMin
	 *            the lower border of the original interval
	 * @param originMax
	 *            the upper border of the original interval
	 * @param rangeMin
	 *            the lower border of the new interval
	 * @param rangeMax
	 *            the upper border of the new interval
	 * @return the mapped correspondent to the passed-in value
	 */
	public static double map(double value, final double originMin,
			final double originMax, final double rangeMin,
			final double rangeMax) {
		final double originRange = originMax - originMin;
		final double newRange = rangeMax - rangeMin;
		value -= originMin;
		value /= originRange;
		value *= newRange;
		value += rangeMin;
		return value;
	}

	/**
	 * if the value is within the interval [min,max] it will be returned with no
	 * difference<br>
	 * if the value is lower than min, min is returned<br>
	 * if the value is higher than max, max is returned<br>
	 * 
	 * @param value
	 *            the value to be constrained
	 * @param min
	 *            the lower border of the interval
	 * @param max
	 *            the upper border of the interval
	 * @return the constrained value within the interval
	 */
	public static double constrain(double value, final double min,
			final double max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	/**
	 * if the value is within the interval [min,max[ it remains the same<br>
	 * otherwise if it will overflow upper and subtracted by max until it is in
	 * the interval<br>
	 * otherwise if it will overflow lower and added by max until it is in the
	 * interval
	 * 
	 * @param value
	 *            the value to handle overflow
	 * @param min
	 *            the lower border of the interval
	 * @param max
	 *            the upper border of the interval
	 * @return the controlled value
	 */
	public static double overflow(double value, final double min,
			final double max) {
		final double range = max - min;
		while (value < min)
			value += range;
		while (value >= max)
			value -= range;
		return value;
	}

	/**
	 * packs a float into the two lower (right) bytes of the result-int <br>
	 * loss of accuracy!!!
	 * 
	 * @see #charToFloat(int)
	 * @param number
	 *            the floating-point-number to pack into 2 bytes
	 * @return the 2 lower bytes of the result are the number
	 */
	public static int floatToChar(float number) {
		int result;
		final boolean minusSign = number < 0;
		if (minusSign) {
			number = -number;
			result = 0b1000000000000000;
		} else
			result = 0;

		int exp = -2;// offset
		for (int i = exp; i < 0; i++)
			number *= 10f;
		number = (int) number; // round
		exp = 0;
		while (number != 0
				&& number / 10f == Math.floor(number / 10f)) {
			number /= 10f;
			exp++;
		}
		int mantisse = (int) number; // cast
		while (mantisse != (mantisse & 0b0000111111111111)) {
			mantisse /= 10;
			exp++;
		}

		result = result | (exp << 12);
		result = result | (mantisse & 0b0000111111111111);
		return result;
	}

	/**
	 * extracts a floating-point-number from the lower (right) 2 bytes of the
	 * argument
	 * 
	 * @see #floatToChar(float)
	 * @param number the 2 lower bytes of the result are the number
	 * @return the extracted number
	 */
	public static float charToFloat(int number) {
		final boolean minusSign = ((number
				& 0b1000000000000000) >> 15) == 1;
		int exp = ((number & 0b0111000000000000) >> 12) - 2;
		int mantisse = ((number & 0b0000111111111111));
		float result = mantisse;
		if (exp > 0)
			for (int i = 0; i < exp; i++)
				result *= 10;
		else
			for (int i = exp; i < 0; i++)
				result /= 10;
		if (minusSign)
			return -result;
		else
			return result;
	}
}