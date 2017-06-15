package fast3d.util.serial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import fast3d.complex.Group;
import fast3d.complex.Object;
import fast3d.complex.Scene;
import fast3d.complex.light.Material;
import fast3d.math.Vector2d;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;

/**
 * loads an *.obj-file<br>
 * the filename does not have to end with *.obj / *.mtl, just the files content
 * matters<br>
 * every "g"-tag will create a new fast3d.complex.Group and every "o"-tag will
 * create a new fast3d.complex.Object<br>
 * every Group will be packed to the previously declared Object, every Object
 * will be packed to one fast3d.complex.Scene<br>
 * the first undeclared group and object are called as the content of the
 * unnamed field (should only be changed for good reason)
 * 
 * @author Tim Trense
 */
public abstract class ObjFileLoader {

	/**
	 * the groupID or objectID if no other is specified in the file to load
	 */
	public static String unnamed = "unnamed";
	/**
	 * the offset to add to any index for vertices, normals and
	 * texture-coordinates<br>
	 * default is -1 because many .obj-files implicitly define indices as
	 * 1-based but in this loader they are 0-based
	 */
	public static int indexOffset = -1;
	private static int _line = 0;

	/**
	 * creates a file with the given filename and calls load(file)
	 * 
	 * @param filename
	 *            the name of the file to load
	 * @return the Scene described in the *.obj-file
	 */
	public static Scene load(final String filename) {
		return load(new File(filename));
	}

	/**
	 * creates a bufferedReader and calls load(reader) but in try-catch
	 * 
	 * @param f
	 *            the file containing the description of the object to load
	 * @return the Scene described in the *.obj-file
	 */
	public static Scene load(final File f) {
		try {
			_line = 0;
			final BufferedReader r = new BufferedReader(
					new FileReader(f));
			final Scene obj = load(f.getParent(), r);
			r.close();
			return obj;
		} catch (final Throwable t) {
			t.printStackTrace();
			System.err.println(
					"Error in file " + f + " - line " + _line);
			return null;
		}
	}

	/**
	 * reads the content of the reader and builds a Scene just as declared in
	 * the readers content
	 * 
	 * @param dir
	 *            the directory of the *.obj file
	 * @param reader
	 *            the description of the Scene
	 * @return the Scene described in the *.obj-file-bufferedReader
	 * @throws IOException
	 *             if any io-exception occurs- that will be thrown
	 */
	public static Scene load(final String dir,
			final BufferedReader reader) throws IOException {
		final List<Vector3d> vertices = new LinkedList<Vector3d>();
		final List<Vector3d> normals = new LinkedList<Vector3d>();
		final List<Vector2d> textureCoordinates = new LinkedList<Vector2d>();
		final List<Object> o = new LinkedList<Object>();
		final Hashtable<String, Material> materials = new Hashtable<String, Material>();
		Group currentG = new Group(); // group
		currentG.setGroupID(unnamed);
		Object currentO = new Object();
		currentO.setObjectID(unnamed);
		Material currentM = new Material();

		String[] line = readLine(reader);
		_line++;
		do {
			if (line.length < 1) {
				line = readLine(reader);
				continue;
			}
			switch (line[0].toLowerCase()) {
			case "v": {
				final double x = Double.parseDouble(line[1]);
				final double y = Double.parseDouble(line[2]);
				final double z = Double.parseDouble(line[3]);
				vertices.add(new Vector3d(x, y, z));
			}
				break;
			case "vn": {
				final double x = Double.parseDouble(line[1]);
				final double y = Double.parseDouble(line[2]);
				final double z = Double.parseDouble(line[3]);
				normals.add(new Vector3d(x, y, z));
			}
				break;
			case "vt": {
				final double x = Double.parseDouble(line[1]);
				final double y = Double.parseDouble(line[2]);
				textureCoordinates.add(new Vector2d(x, y));
			}
				break;
			case "f": {
				final int noValue = Integer.MIN_VALUE;
				final int count = line.length - 1;
				final int[] fvertices = new int[count];
				final int[] fnormals = new int[count];
				final int[] ftextures = new int[count];
				// read v, vn and vt -indices

				// line [0] [1] [2] [...]
				// regex of line f v/vt/vn v/vt/vn v/vt/vn
				for (int i = 0; i < count; i++) {
					// part [0] [1] [2]
					// regex of part v vt vn
					final String[] indecies = line[i + 1].split("/");
					// v
					if (!indecies[0].isEmpty())
						fvertices[i] = Integer.parseInt(indecies[0])
								+ indexOffset;
					else
						fvertices[i] = noValue;
					// vt
					if (!indecies[1].isEmpty())
						ftextures[i] = Integer.parseInt(indecies[1])
								+ indexOffset;
					else
						ftextures[i] = noValue;
					// vn
					if (!indecies[2].isEmpty())
						fnormals[i] = Integer.parseInt(indecies[2])
								+ indexOffset;
					else
						fnormals[i] = noValue;
				}
				// resolve indices
				final Vector3d[] edges = new Vector3d[fvertices.length];
				for (int i = 0; i < edges.length; i++)
					if (fvertices[i] != noValue)
						edges[i] = vertices.get(fvertices[i]);
					else
						edges[i] = null;
				// resolve texture-coordinates
				final Vector2d[] textures = new Vector2d[ftextures.length];
				for (int i = 0; i < textures.length; i++)
					if (ftextures[i] != noValue)
						textures[i] = textureCoordinates
								.get(ftextures[i]);
					else
						textures[i] = null;
				// resolve normals
				final Vector3d[] norms = new Vector3d[fnormals.length];
				for (int i = 0; i < norms.length; i++)
					if (fnormals[i] != noValue)
						norms[i] = normals.get(fnormals[i]);
					else
						norms[i] = null;

				// the triangles normal is the average of all normals
				final Vector3d normal = Vector3d
						.calculateAverage(norms);

				final AdvTriangle triangle = new AdvTriangle(edges[0],
						edges[1], edges[2], currentM, normal);
				{// set texture-coordinate
					triangle.setLogicalTextureCoordinates(textures[0],
							textures[1], textures[2]);
				}

				currentG.triangles.add(triangle);
			}
				break;
			case "usemtl":
				if (materials.containsKey(line[1]))
					currentM = materials.get(line[1]);
				else
					currentM = new Material();
				break;
			case "mtllib": {
				final Hashtable<String, Material> loaded = MtlLibLoader
						.loadMtlLib(
								(dir != null ? dir + File.separator
										: "") + line[1]);
				for (String name : loaded.keySet())
					materials.put(name, loaded.get(name));
			}
				break;
			case "o": {
				if (!currentG.getGroupID().equals(unnamed))
					currentO.groups.add(currentG);
				if (!currentO.getObjectID().equals(unnamed))
					o.add(currentO);
				currentO = new Object();
				currentO.setObjectID(line[1]);
				currentG = new Group();
				currentG.setGroupID(unnamed);
			}
				break;
			case "g": {
				if (!currentG.getGroupID().equals(unnamed))
					currentO.groups.add(currentG);
				currentG = new Group();
				currentG.setGroupID(line[1]);
			}
				break;
			case "#": // comment
				// fall-through
			default:// ignore other
			}
			line = readLine(reader);
			_line++;
		} while (line != null);
		currentO.groups.add(currentG);
		o.add(currentO);
		return new Scene(o);
	}

	private static String[] readLine(final BufferedReader reader)
			throws IOException {
		final String line = reader.readLine();
		if (line == null)
			return null;
		final StringTokenizer t = new StringTokenizer(line, " ",
				false);
		final String[] parts = new String[t.countTokens()];
		for (int i = 0; i < parts.length; i++)
			parts[i] = t.nextToken();
		return parts;
	}
}