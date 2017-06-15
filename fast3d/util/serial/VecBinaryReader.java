package fast3d.util.serial;

import java.io.InputStream;

import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.math.Vector4d;

/**
 * providing the possibility to read vectors<br>
 * can be treated as a BinaryWriter
 * 
 * @author Tim Trense
 *
 */
public class VecBinaryReader extends BinaryReader {

	/**
	 * just calls super-constructor
	 * 
	 * @param stream
	 *            the stream to deserialize from
	 */
	public VecBinaryReader(final InputStream stream) {
		super(stream);
	}

	/**
	 * reads the vectors double-coordinates and generates a single vector from
	 * them
	 * 
	 * @return the deserialized vector or null if something went wrong
	 */
	public Vector2d read2() {
		final double[] coord = readDouble(2);
		if (coord != null)
			return new Vector2d(coord[0], coord[1]);
		else
			return null;
	}

	/**
	 * reads all needed double-coordinates and generates vectors of them
	 * 
	 * @param amount
	 *            the amount of vectors to read
	 * @return the deserialized vector-array or null if something went wrong
	 */
	public Vector2d[] read2(final int amount) {
		final double[] coord = readDouble(2 * amount);
		if (coord != null) {
			final Vector2d[] vecs = new Vector2d[amount];
			for (int i = 0; i < vecs.length; i++)
				vecs[i] = new Vector2d(coord[i * 2], coord[i * 2 + 1]);
			return vecs;
		} else
			return null;
	}

	/**
	 * reads the vectors double-coordinates and generates a single vector from
	 * them
	 * 
	 * @return the deserialized vector or null if something went wrong
	 */
	public Vector3d read3() {
		final double[] coord = readDouble(3);
		if (coord != null)
			return new Vector3d(coord[0], coord[1], coord[2]);
		else
			return null;
	}

	/**
	 * reads all needed double-coordinates and generates vectors of them
	 * 
	 * @param amount
	 *            the amount of vectors to read
	 * @return the deserialized vector-array or null if something went wrong
	 */
	public Vector3d[] read3(final int amount) {
		final double[] coord = readDouble(3 * amount);
		if (coord != null) {
			final Vector3d[] vecs = new Vector3d[amount];
			for (int i = 0; i < vecs.length; i++)
				vecs[i] = new Vector3d(coord[i * 3], coord[i * 3 + 1], coord[i * 3 + 2]);
			return vecs;
		} else
			return null;
	}

	/**
	 * reads the vectors double-coordinates and generates a single vector from
	 * them
	 * 
	 * @return the deserialized vector or null if something went wrong
	 */
	public Vector4d read4() {
		final double[] coord = readDouble(4);
		if (coord != null)
			return new Vector4d(coord[0], coord[1], coord[2], coord[3]);
		else
			return null;
	}

	/**
	 * reads all needed double-coordinates and generates vectors of them
	 * 
	 * @param amount
	 *            the amount of vectors to read
	 * @return the deserialized vector-array or null if something went wrong
	 */
	public Vector4d[] read4(final int amount) {
		final double[] coord = readDouble(4 * amount);
		if (coord != null) {
			final Vector4d[] vecs = new Vector4d[amount];
			for (int i = 0; i < vecs.length; i++)
				vecs[i] = new Vector4d(coord[i * 4], coord[i * 4 + 1], coord[i * 4 + 2],
						coord[i * 4 + 3]);
			return vecs;
		} else
			return null;
	}

	/**
	 * just another way of writing read4() - but read4() gives no color
	 * 
	 * @see #read4()
	 * @return a single color or null if something went wrong
	 */
	public Color readColor() {
		final double[] coord = readDouble(4);
		if (coord != null)
			return new Color(coord[0], coord[1], coord[2], coord[3]);
		else
			return null;
	}

	/**
	 * just another way of writing read4(amount) - but read4() gives no color
	 * 
	 * @see #read4(int)
	 * @param amount
	 *            the count of colors to read
	 * @return a color-array or null if something went wrong
	 */
	public Color[] readColor(final int amount) {
		final double[] coord = readDouble(4 * amount);
		if (coord != null) {
			final Color[] vecs = new Color[amount];
			for (int i = 0; i < vecs.length; i++)
				vecs[i] = new Color(coord[i * 4], coord[i * 4 + 1], coord[i * 4 + 2],
						coord[i * 4 + 3]);
			return vecs;
		} else
			return null;
	}

	/**
	 * deserializes a single color of the stream
	 * 
	 * @return a single color or null if something went wrong
	 */
	public Material readMaterial() {
		final Color[] cols = readColor(4);
		if (cols != null) {
			final double shininess = readDouble(1)[0];
			return new Material(cols[0], cols[1], cols[2], cols[3], shininess, null);
		} else
			return null;
	}

}