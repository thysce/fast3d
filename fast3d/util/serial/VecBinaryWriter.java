package fast3d.util.serial;

import java.io.OutputStream;

import fast3d.complex.light.Material;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.math.Vector4d;

/**
 * providing the possibility to write vectors<br>
 * can be treated as a BinaryWriter
 * 
 * @author Tim Trense
 *
 */
public class VecBinaryWriter extends BinaryWriter {

	/**
	 * just calling super-constructor
	 * 
	 * @param stream the stream to serialize to
	 */
	public VecBinaryWriter(final OutputStream stream) {
		super(stream);
	}

	/**
	 * serializes and writes all given vectors to the stream<br>
	 * in fact this does just write the coordinates of the given vectors as
	 * doubles
	 * 
	 * @param vecs
	 *            all vectors to serialize (will not be changed)
	 * @return whether all vectors have been serialized and written successfully
	 */
	public boolean write(final Vector4d... vecs) {
		final double[] bufferArray = new double[vecs.length * 4];
		int i = 0;
		for (Vector4d v : vecs) {
			bufferArray[i] = v.x;
			bufferArray[i + 1] = v.y;
			bufferArray[i + 2] = v.z;
			bufferArray[i + 3] = v.a;
			i += 4;
		}
		return write(bufferArray);
	}

	/**
	 * serializes and writes all given vectors to the stream<br>
	 * in fact this does just write the coordinates of the given vectors as
	 * doubles
	 * 
	 * @param vecs
	 *            all vectors to serialize (will not be changed)
	 * @return whether all vectors have been serialized and written successfully
	 */
	public boolean write(final Vector3d... vecs) {
		final double[] bufferArray = new double[vecs.length * 3];
		int i = 0;
		for (Vector3d v : vecs) {
			bufferArray[i] = v.x;
			bufferArray[i + 1] = v.y;
			bufferArray[i + 2] = v.z;
			i += 3;
		}
		return write(bufferArray);
	}

	/**
	 * serializes and writes all given vectors to the stream<br>
	 * in fact this does just write the coordinates of the given vectors as
	 * doubles
	 * 
	 * @param vecs
	 *            all vectors to serialize (will not be changed)
	 * @return whether all vectors have been serialized and written successfully
	 */
	public boolean write(final Vector2d... vecs) {
		final double[] bufferArray = new double[vecs.length * 2];
		int i = 0;
		for (Vector2d v : vecs) {
			bufferArray[i] = v.x;
			bufferArray[i + 1] = v.y;
			i += 2;
		}
		return write(bufferArray);
	}

	/**
	 * serializes and writes all given materials to the stream
	 * 
	 * @param mats
	 *            all materials to write
	 * @return whether every material has been written successfully
	 */
	public boolean write(final Material... mats) {
		for (Material m : mats) {
			if (!write(m.getAmbient(), m.getEmissive(), m.getDiffuse(), m.getSpecular()))
				return false;

			if (!write(m.getShininess()))
				return false;
		}
		return true;
	}
}