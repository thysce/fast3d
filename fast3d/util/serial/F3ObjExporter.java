package fast3d.util.serial;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fast3d.complex.Scene;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.util.serial.ObjFileLoader;
import fast3d.util.math.MathUtil;

/**
 * the counterpart to fast3d.util.serial.F3ObjImporter to shrink file sizes of
 * .obj-files
 * 
 * @author Tim Trense
 */
public class F3ObjExporter {

	private final Scene scn;
	private final File file;
	/**
	 * if an exception occurred
	 */
	public Exception exception;
	/**
	 * whether to compress the data - may force loss of accuracy<br>
	 * default is false
	 */
	public boolean hardCompress = false;
	private final ArrayList<Vector3d> vectors;
	private final ArrayList<Color> colors;
	private final ArrayList<Material> materials;

	/**
	 * constructs a new exporter to save the scene
	 * 
	 * @param scn
	 *            the scene to export
	 * @param file
	 *            the .f3obj-file to export to
	 */
	public F3ObjExporter(final Scene scn, final File file) {
		this(scn, file, false);
	}

	/**
	 * constructs a new exporter to save the scene
	 * 
	 * @param scn
	 *            the scene to export
	 * @param file
	 *            the .f3obj-file to export to
	 * @param hardCompress
	 *            whether to compress the data (may lead to lack of accuracy -
	 *            default is false)
	 */
	public F3ObjExporter(final Scene scn, final File file,
			final boolean hardCompress) {
		this.scn = scn;
		this.file = file;
		this.hardCompress = hardCompress;
		this.exception = null;
		this.vectors = new ArrayList<Vector3d>();
		this.colors = new ArrayList<Color>();
		this.materials = new ArrayList<Material>();
	}

	/**
	 * converts the given *.obj-file to an equal *.f3obj-file
	 * 
	 * @param objfile
	 *            the file to convert
	 * @param compress
	 *            whether to compress the data (may lead to lack of accuracy)
	 * @return the success of the operation
	 */
	public static boolean convert(final File objfile,
			final boolean compress) {
		final Scene scn = ObjFileLoader.load(objfile);
		if (scn == null)
			return false;
		final File dest = new File(objfile.getParent(),
				objfile.getName().replace(".obj", ".f3obj"));
		return export(scn, dest, compress);
	}

	/**
	 * exports the given scene to the given file
	 * 
	 * @param scn
	 *            the scene to export
	 * @param dest
	 *            the destination filename to export to
	 * 
	 * @return the success of the operation
	 */
	public static boolean export(final Scene scn, final String dest) {
		return export(scn, new File(dest), true);
	}

	/**
	 * exports the given scene to the given file
	 * 
	 * @param scn
	 *            the scene to export
	 * @param dest
	 *            the destination filename to export to
	 * @param compress
	 *            whether to use compress-mode
	 * @return the success of the operation
	 */
	public static boolean export(final Scene scn, final String dest,
			final boolean compress) {
		return export(scn, new File(dest), compress);
	}

	/**
	 * exports the given scene to the given file
	 * 
	 * @param scn
	 *            the scene to export
	 * @param dest
	 *            the destination to export to
	 * @param compress
	 *            whether to use compress-mode
	 * @return the success of the operation
	 */
	public static boolean export(final Scene scn, final File dest,
			final boolean compress) {
		final F3ObjExporter exp = new F3ObjExporter(scn, dest,
				compress);
		return exp.exportSave();
	}

	/**
	 * does the try-catch of export() for you
	 * 
	 * @see #export()
	 * @return the success of the operation
	 */
	public boolean exportSave() {
		try {
			export();
			return true;
		} catch (final Exception ex) {
			this.exception = ex;
			return false;
		}
	}

	/**
	 * exports the scene to the file
	 * 
	 * @throws IOException
	 *             if an exception occurred
	 */
	public void export() throws IOException {
		final OutputStream os = new FileOutputStream(file);
		final DataOutputStream dos = new DataOutputStream(os);
		IOException occured = null;
		try {
			export(dos);
		} catch (final IOException ex) {
			occured = ex;
		}
		try {
			os.close();
		} catch (final IOException ex) {
			// well was worth the shot
		}
		if (occured != null)
			throw occured;
	}

	private void export(final DataOutputStream dos)
			throws IOException {
		initialize();
		if (hardCompress)
			dos.writeByte(1);
		else
			dos.writeByte(0);
		exportVectors(dos);
		exportColors(dos);
		exportMaterials(dos);
		exportFaces(dos);
		dos.writeByte(0);
	}

	private void exportFaces(final DataOutputStream dos)
			throws IOException {
		final List<AdvTriangle> ts = new ArrayList<AdvTriangle>(
				scn.getAdvTrianglesOnce());
		final HashMap<Material, List<AdvTriangle>> tss = split(ts);
		for (Material key : tss.keySet())
			exportFaces(key, tss.get(key), dos);
	}

	private void exportFaces(final Material m,
			final List<AdvTriangle> ts, final DataOutputStream dos)
			throws IOException {
		dos.writeByte(10);
		dos.writeByte(materials.indexOf(m));
		final int max = 255;
		final int iterations = ts.size() / max;
		final int rest = ts.size() - iterations * max;
		int i = 0;
		for (int j = 0; j < iterations; j++, i += max)
			exportFaces(dos, ts, i, max);
		if (rest > 0)
			exportFaces(dos, ts, i, rest);
	}

	private HashMap<Material, List<AdvTriangle>> split(
			List<AdvTriangle> ts) {
		final HashMap<Material, List<AdvTriangle>> map = new HashMap<Material, List<AdvTriangle>>();
		for (AdvTriangle t : ts) {
			if (!map.containsKey(t.material))
				map.put(t.material, new LinkedList<AdvTriangle>());
			final List<AdvTriangle> toadd = map.get(t.material);
			toadd.add(t);
		}
		return map;
	}

	private void exportFaces(final DataOutputStream dos,
			List<AdvTriangle> ts, final int i, final int amount)
			throws IOException {
		dos.writeByte(11);
		dos.writeByte(amount);
		AdvTriangle current;
		for (int j = 0; j < amount; j++) {
			current = ts.get(i + j);
			dos.writeChar(vectors.indexOf(current.a));
			dos.writeChar(vectors.indexOf(current.b));
			dos.writeChar(vectors.indexOf(current.c));
		}
	}

	private void exportMaterials(final DataOutputStream dos)
			throws IOException {
		dos.writeByte(8);
		dos.writeByte(materials.size());
		for (Material current : materials) {
			dos.writeChar(colors.indexOf(current.ambient));
			dos.writeChar(colors.indexOf(current.diffuse));
			dos.writeChar(colors.indexOf(current.specular));
			dos.writeChar(colors.indexOf(current.emissive));
			dos.writeByte((int) current.shininess);
			dos.writeByte((int) (current.alpha * 255));
		}
	}

	private void exportColors(final DataOutputStream dos)
			throws IOException {
		final Set<Color> withAlpha = new HashSet<Color>();
		final List<Color> all = new ArrayList<Color>(colors.size());
		all.addAll(colors);
		for (Color c : colors)
			if (!c.isOpaque()) {
				withAlpha.add(c);
				all.remove(c);
			}
		java.awt.Color awtCol;
		if (!all.isEmpty()) {
			dos.writeByte(6);
			dos.writeChar(all.size());
			for (Color c : all) {
				awtCol = c.awtColor();
				dos.writeByte(awtCol.getRed());
				dos.writeByte(awtCol.getGreen());
				dos.writeByte(awtCol.getBlue());
			}
		}
		if (!withAlpha.isEmpty()) {
			dos.writeByte(7);
			dos.writeChar(withAlpha.size());
			for (Color c : withAlpha) {
				awtCol = c.awtColor();
				dos.writeByte(awtCol.getRed());
				dos.writeByte(awtCol.getGreen());
				dos.writeByte(awtCol.getBlue());
				dos.writeByte(awtCol.getAlpha());
			}
		}
	}

	private void exportVectors(final DataOutputStream dos)
			throws IOException {
		dos.writeByte(2);
		dos.writeChar(vectors.size());
		if (hardCompress)
			for (Vector3d current : vectors) {
				dos.writeChar(
						MathUtil.floatToChar((float) current.x));
				dos.writeChar(
						MathUtil.floatToChar((float) current.y));
				dos.writeChar(
						MathUtil.floatToChar((float) current.z));
			}
		else
			for (Vector3d current : vectors) {
				dos.writeFloat((float) current.x);
				dos.writeFloat((float) current.y);
				dos.writeFloat((float) current.z);
			}
	}

	private void initialize() throws IOException {
		vectors.addAll(scn.getVerticesOnce());
		if (vectors.size() > Character.MAX_VALUE)
			throw new IOException(
					"The amount of vectors is over 0xFFFF");
		materials.addAll(scn.getMaterials());
		if (materials.size() > 256)
			throw new IOException(
					"The amount of materials is over 256");
		for (Material m : materials) {
			if (!colors.contains(m.ambient))
				colors.add(m.ambient);
			if (!colors.contains(m.diffuse))
				colors.add(m.diffuse);
			if (!colors.contains(m.specular))
				colors.add(m.specular);
			if (!colors.contains(m.emissive))
				colors.add(m.emissive);
		}
		if (colors.size() > Character.MAX_VALUE)
			throw new IOException(
					"The amount of colors is over 0xFFFF");
	}

}
