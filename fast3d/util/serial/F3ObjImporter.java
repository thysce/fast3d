package fast3d.util.serial;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import fast3d.complex.Group;
import fast3d.complex.Scene;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.util.ColorGen;
import fast3d.util.math.MathUtil;

/**
 * a loader for the fast3d-format *.f3obj - a binary compressed version of
 * Wavefront object *.obj<br>
 * in advance to .obj the fast3d-obj-format will make up to 90% data-compression
 * <br>
 * Just call loadSave() and then take the loaded scene from the "scene" field -
 * the result value will tell you the success. <br>
 * if something went wrong just take the occurred exception from the exception
 * field<br>
 * 
 * @see #scene
 * @see #exception
 * @see #loadSave()
 * @author Tim Trense
 */
public class F3ObjImporter {

	/**
	 * the scene loaded from the file
	 */
	public final Scene scene;
	private fast3d.complex.Object currentObject;
	private Group currentGroup;
	private final ArrayList<Vector3d> vectors;
	private final ArrayList<Color> colors;
	private final ArrayList<Material> materials;
	private Material currentMaterial;
	private final File file;
	private boolean hardDecompress;
	/**
	 * if an exception occurred
	 */
	public Exception exception;

	/**
	 * constructs a new importer to load the file
	 * 
	 * @param file
	 *            the .f3obj-file to load
	 */
	public F3ObjImporter(final File file) {
		super();
		this.scene = new Scene();
		this.file = file;
		this.exception = null;
		this.vectors = new ArrayList<Vector3d>();
		this.colors = new ArrayList<Color>();
		this.materials = new ArrayList<Material>();
	}

	/**
	 * wrapper for load(File)
	 * 
	 * @see #load(File)
	 * @param filename
	 *            the name of the file to load
	 * @return the loaded Scene or null if an exception occurred
	 */
	public static Scene load(final String filename) {
		return load(new File(filename));
	}

	/**
	 * loads the specified file
	 * 
	 * @param f
	 *            the .f3obj file to load
	 * @return the loaded Scene or null if an exception occurred
	 */
	public static Scene load(final File f) {
		final F3ObjImporter imp = new F3ObjImporter(f);
		if (imp.loadSave())
			return imp.scene;
		else
			return null;
	}

	/**
	 * does the try-catch of load() for you
	 * 
	 * @see #load()
	 * @return the success of the operation
	 */
	public boolean loadSave() {
		try {
			load();
			return true;
		} catch (final Exception ex) {
			this.exception = ex;
			return false;
		}
	}

	/**
	 * loads the file to scene
	 * 
	 * @see #scene
	 * @throws IOException
	 *             if an exception occurred
	 */
	public void load() throws IOException {
		final InputStream is = new FileInputStream(file);
		final DataInputStream dis = new DataInputStream(is);
		IOException occured = null;
		try {
			load(dis);
		} catch (final IOException ex) {
			occured = ex;
		}
		try {
			is.close();
		} catch (final IOException ex) {
			// well was worth the shot
		}
		if (occured != null)
			throw occured;
	}

	private void load(final DataInputStream dis) throws IOException {
		currentGroup = new Group();
		currentGroup.setGroupID("default");
		currentObject = new fast3d.complex.Object();
		currentObject.setObjectID("default");

		hardDecompress = dis.readUnsignedByte() == 1;

		int block = 1;
		while (block > -1)
			switch (block = dis.readUnsignedByte()) {
			case 0: // Fall-through
			case 1:
				block = -1; // break loop
				break;
			case 2:
				loadVectors(dis);
				break;
			case 3:
				newGroup(dis);
				break;
			case 4:
				newObject(dis);
				break;
			case 5:
				loadFaces(dis, true);
				break;
			case 6:
				loadColors(dis);
				break;
			case 7:
				loadColorsWithAlpha(dis);
				break;
			case 8:
				loadMaterials(dis);
				break;
			case 9:
				loadFaces(dis, false);
				break;
			case 10: { // use material
				currentMaterial = materials
						.get(dis.readUnsignedByte());
			}
				break;
			case 11:
				loadFacesM(dis);
				break;
			default:
				throw new IOException(
						"xobj-block " + block + " unknown");
			}
		// add last group and object
		currentObject.groups.add(currentGroup);
		scene.objs.add(currentObject);
	}

	private void loadFacesM(final DataInputStream dis)
			throws IOException {
		final int count = dis.readUnsignedByte();
		int i1, i2, i3;
		Vector3d e1, e2, e3;
		for (int i = 0; i < count; i++) {
			i1 = dis.readChar();
			i2 = dis.readChar();
			i3 = dis.readChar();
			e1 = vectors.get(i1);
			e2 = vectors.get(i2);
			e3 = vectors.get(i3);
			final AdvTriangle triang;
			triang = new AdvTriangle(e1, e2, e3, currentMaterial);
			currentGroup.triangles.add(triang);
		}
	}

	private void loadMaterials(final DataInputStream dis)
			throws IOException {
		final int count = dis.readUnsignedByte();
		Color amb, dif, spc, ems;
		double shn, aph;
		Material m;
		for (int i = 0; i < count; i++) {
			amb = colors.get(dis.readChar());
			dif = colors.get(dis.readChar());
			spc = colors.get(dis.readChar());
			ems = colors.get(dis.readChar());
			shn = dis.readUnsignedByte();
			aph = dis.readUnsignedByte() / 255d;
			m = new Material(amb, dif, spc, ems, shn, aph, null);
			materials.add(m);
		}
	}

	private void loadColors(final DataInputStream dis)
			throws IOException {
		final int count = dis.readChar();
		int r, g, b;
		Color c;
		for (int i = 0; i < count; i++) {
			r = dis.readUnsignedByte();
			g = dis.readUnsignedByte();
			b = dis.readUnsignedByte();
			c = ColorGen.from255RGB(r, g, b);
			colors.add(c);
		}
	}

	private void loadColorsWithAlpha(final DataInputStream dis)
			throws IOException {
		final int count = dis.readUnsignedByte();
		byte r, g, b, a;
		Color c;
		for (int i = 0; i < count; i++) {
			r = dis.readByte();
			g = dis.readByte();
			b = dis.readByte();
			a = dis.readByte();
			c = ColorGen.from255RGBA(r, g, b, a);
			colors.add(c);
		}
	}

	private void newObject(final DataInputStream dis)
			throws IOException {
		if (!currentObject.groups.isEmpty())
			scene.objs.add(currentObject);
		final byte[] id = new byte[dis.readUnsignedByte()];
		for (int i = 0; i < id.length; i++)
			id[i] = dis.readByte();
		final String newObjectID = new String(id,
				StandardCharsets.UTF_8);
		currentObject = new fast3d.complex.Object();
		currentObject.setObjectID(newObjectID);
	}

	private void newGroup(final DataInputStream dis)
			throws IOException {
		if (!currentGroup.getTriangles().isEmpty())
			currentObject.groups.add(currentGroup);
		final byte[] id = new byte[dis.readUnsignedByte()];
		for (int i = 0; i < id.length; i++)
			id[i] = dis.readByte();
		final String newGroupID = new String(id,
				StandardCharsets.UTF_8);
		currentGroup = new Group();
		currentGroup.setGroupID(newGroupID);
	}

	private void loadFaces(final DataInputStream dis,
			final boolean loadNormals) throws IOException {
		final int count = dis.readUnsignedByte();
		int i1, i2, i3, iN = 0, iM;
		Vector3d e1, e2, e3, n = null;
		Material m;
		for (int i = 0; i < count; i++) {
			i1 = dis.readChar();
			i2 = dis.readChar();
			i3 = dis.readChar();
			if (loadNormals)
				iN = dis.readChar();
			iM = dis.readUnsignedByte();
			e1 = vectors.get(i1);
			e2 = vectors.get(i2);
			e3 = vectors.get(i3);
			if (loadNormals)
				n = vectors.get(iN);
			m = materials.get(iM);
			final AdvTriangle triang;
			if (loadNormals)
				triang = new AdvTriangle(e1, e2, e3, m, n);
			else
				triang = new AdvTriangle(e1, e2, e3, m);
			currentGroup.triangles.add(triang);
		}
	}

	private void loadVectors(final DataInputStream dis)
			throws IOException {
		vectors.clear();
		final int count;
		vectors.ensureCapacity(count = dis.readChar());
		double x, y, z;
		if (hardDecompress)
			for (int i = 0; i < count; i++) {
				x = MathUtil.charToFloat(dis.readChar());
				y = MathUtil.charToFloat(dis.readChar());
				z = MathUtil.charToFloat(dis.readChar());

				vectors.add(new Vector3d(x, y, z));
			}
		else
			for (int i = 0; i < count; i++) {
				x = dis.readFloat();
				y = dis.readFloat();
				z = dis.readFloat();
				vectors.add(new Vector3d(x, y, z));
			}
	}
}