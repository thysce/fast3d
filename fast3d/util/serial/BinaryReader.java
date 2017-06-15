package fast3d.util.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * a simple way of reading complex primitive information out of a binary stream
 * <br>
 * the backing stream needs to be opened and closed separately - this instance
 * can just be collected by the JVM garbage collector <br>
 * every IO-Exception will be caught and it's stack trace is printed
 * 
 * @author Tim Trense
 *
 */
public class BinaryReader {

	private final InputStream stream;

	/**
	 * constructs a reader deserializing from the data read out of the stream
	 * 
	 * @param stream the stream to deserialize from
	 */
	public BinaryReader(final InputStream stream) {
		this.stream = stream;
	}

	/**
	 * reads a plain byte-array from the stream
	 * 
	 * @param length
	 *            the length of the array to read
	 * @return the read array or null if reading failed (even if it failed
	 *         partly)
	 */
	public byte[] read(final int length) {
		final byte[] r = new byte[length];
		try {
			final int rlength = stream.read(r);
			if (rlength < length)
				return null;
			else
				return r;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * reads amount integers from the stream
	 * 
	 * @param amount
	 *            the count of integers to read
	 * @return an array of the primitives read from the stream or null if
	 *         reading or deserialization failed (even partly)
	 */
	public int[] readInt(final int amount) {
		final ByteBuffer bb = ByteBuffer.allocate(amount * Integer.BYTES);
		final byte[] backing = read(bb.remaining());
		if (backing == null)
			return null;
		bb.put(backing);
		bb.flip();
		final int[] res = new int[amount];
		for (int i = 0; i < res.length; i++)
			res[i] = bb.getInt();
		return res;
	}

	/**
	 * reads amount longs from the stream
	 * 
	 * @param amount
	 *            the count of longs to read
	 * @return an array of the primitives read from the stream or null if
	 *         reading or deserialization failed (even partly)
	 */
	public long[] readLong(final int amount) {
		final ByteBuffer bb = ByteBuffer.allocate(amount * Long.BYTES);
		final byte[] backing = read(bb.remaining());
		if (backing == null)
			return null;
		bb.put(backing);
		bb.flip();
		final long[] res = new long[amount];
		for (int i = 0; i < res.length; i++)
			res[i] = bb.getLong();
		return res;
	}

	/**
	 * reads amount doubles from the stream
	 * 
	 * @param amount
	 *            the count of doubles to read
	 * @return an array of the primitives read from the stream or null if
	 *         reading or deserialization failed (even partly)
	 */
	public double[] readDouble(final int amount) {
		final ByteBuffer bb = ByteBuffer.allocate(amount * Double.BYTES);
		final byte[] backing = read(bb.remaining());
		if (backing == null)
			return null;
		bb.put(backing);
		bb.flip();
		final double[] res = new double[amount];
		for (int i = 0; i < res.length; i++)
			res[i] = bb.getDouble();
		return res;
	}

	/**
	 * reads amount floats from the stream
	 * 
	 * @param amount
	 *            the count of floats to read
	 * @return an array of the primitives read from the stream or null if
	 *         reading or deserialization failed (even partly)
	 */
	public float[] readFloat(final int amount) {
		final ByteBuffer bb = ByteBuffer.allocate(amount * Float.BYTES);
		final byte[] backing = read(bb.remaining());
		if (backing == null)
			return null;
		bb.put(backing);
		bb.flip();
		final float[] res = new float[amount];
		for (int i = 0; i < res.length; i++)
			res[i] = bb.getFloat();
		return res;
	}

	/**
	 * reads an array of bytes with the specified length from the stream and
	 * deserializes it based on the UTF-8 encoding
	 * 
	 * @see #readString()
	 * @param length
	 *            the length of the bytes-array of the string
	 * @return the deserialized string or null if something went wrong
	 */
	public String readString(final int length) {
		final byte[] data = read(length);
		if (data == null)
			return null;
		else
			try {
				return new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
	}

	/**
	 * reads an integer from the stream and then calls readString(length) with
	 * length being the read integer
	 * 
	 * @see #readString(int)
	 * @return the deserialized string or null if something went wrong
	 */
	public String readString() {
		final int[] length = readInt(1);
		if (length != null && length.length > 0)
			return readString(length[0]);
		else
			return null;
	}
}