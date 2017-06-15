package fast3d.util.serial;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * a simple way of writing complex primitive information into a binary stream
 * <br>
 * the backing stream needs to be opened and closed separately - this instance
 * can just be collected by the JVM garbage collector <br>
 * every IO-Exception will be caught and it's stack trace is printed
 * 
 * @author Tim Trense
 *
 */
public class BinaryWriter {

	private final OutputStream stream;

	/**
	 * constructs a writer serializing into the specified stream
	 * 
	 * @param stream
	 *            the binary-stream to serialize to
	 */
	public BinaryWriter(final OutputStream stream) {
		this.stream = stream;
	}

	/**
	 * writes the plain bytes into the stream
	 * 
	 * @param bytes
	 *            the data to write to the stream
	 * @return whether all bytes were successfully written
	 */
	public boolean write(final byte[] bytes) {
		try {
			stream.write(bytes);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * writes the plain bytes into the stream
	 * 
	 * @param bytes
	 *            the data to write to the stream
	 * @return whether all bytes were successfully written
	 */
	public boolean write(final byte[][] bytes) {
		for (int i = 0; i < bytes.length; i++)
			if (!write(bytes[i]))
				return false;
		return true;
	}

	/**
	 * serializes the data and writes then the binary data into the stream
	 * 
	 * @see #write(byte[])
	 * @param idata
	 *            the data to serialize
	 * @return whether the entire data was successfully written
	 */
	public boolean write(final int... idata) {
		final ByteBuffer bb = ByteBuffer.allocate(idata.length * Integer.BYTES);
		for (int i : idata)
			bb.putInt(i);
		bb.flip();
		return write(bb.array());
	}

	/**
	 * serializes the data and writes then the binary data into the stream
	 * 
	 * @see #write(byte[])
	 * @param ldata
	 *            the data to serialize
	 * @return whether the entire data was successfully written
	 */
	public boolean write(final long... ldata) {
		final ByteBuffer bb = ByteBuffer.allocate(ldata.length * Long.BYTES);
		for (long l : ldata)
			bb.putLong(l);
		bb.flip();
		return write(bb.array());
	}

	/**
	 * serializes the data and writes then the binary data into the stream
	 * 
	 * @see #write(byte[])
	 * @param ddata
	 *            the data to serialize
	 * @return whether the entire data was successfully written
	 */
	public boolean write(final double... ddata) {
		final ByteBuffer bb = ByteBuffer.allocate(ddata.length * Double.BYTES);
		for (double i : ddata)
			bb.putDouble(i);
		bb.flip();
		return write(bb.array());
	}

	/**
	 * serializes the data and writes then the binary data into the stream
	 * 
	 * @see #write(byte[])
	 * @param fdata
	 *            the data to serialize
	 * @return whether the entire data was successfully written
	 */
	public boolean write(final float... fdata) {
		final ByteBuffer bb = ByteBuffer.allocate(fdata.length * Float.BYTES);
		for (float i : fdata)
			bb.putFloat(i);
		bb.flip();
		return write(bb.array());
	}

	/**
	 * serializes the string based on the UTF-8 encoding and writes the binary
	 * data to the stream <br>
	 * attention: this method does not write the length of the string
	 * 
	 * @see #write(byte[])
	 * @see #writeString(String)
	 * @param s
	 *            the string to serialize
	 * @return whether the entire string has been written successfully
	 */
	public boolean write(final String s) {
		try {
			final byte[] bytes = s.getBytes("UTF-8");
			return write(bytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * serializes the string based on the UTF-8 encoding and writes the binary
	 * data to the stream <br>
	 * attention: before writing the string this method writes the length of the
	 * string as an integer
	 * 
	 * @see #write(byte[])
	 * @see #write(String)
	 * @param s
	 *            the string to serialize
	 * @return whether the strings length and the entire string have been
	 *         written successfully
	 */
	public boolean writeString(final String s) {
		try {
			final byte[] bytes = s.getBytes("UTF-8");
			if (!write(bytes.length))
				return false;
			return write(bytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}
}