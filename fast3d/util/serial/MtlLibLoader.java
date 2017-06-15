package fast3d.util.serial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.graphics.Texture;

/**
 * to load an *.obj file it is necessary to declare the used coloring-data as
 * materials in a separate file
 *
 * @author Tim Trense
 */
public abstract class MtlLibLoader {

	/**
	 * loads all materials describes in the *.mtl-file
	 * 
	 * @param filename
	 *            the *.mtl-files name
	 * @return all materials in that file mapped to their name described in the
	 *         file
	 */
	public static Hashtable<String, Material> loadMtlLib(
			final String filename) {
		return loadMtlLib(new File(filename));
	}

	/**
	 * loads all materials describes in the *.mtl-file
	 * 
	 * @param file
	 *            the *.mtl-file
	 * @return all materials in that file mapped to their name described in the
	 *         file
	 */
	public static Hashtable<String, Material> loadMtlLib(
			final File file) {
		try {
			final BufferedReader reader = new BufferedReader(
					new FileReader(file));
			final Hashtable<String, Material> mats = loadMtlLib(
					file.getParent(), reader);
			reader.close();
			return mats;
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * loads all materials describes in the *.mtl-file
	 * 
	 * @param dir
	 *            the directory of the material-library-file
	 * @param reader
	 *            the description of the material-library
	 * @return all materials in that file
	 * @throws IOException
	 *             if any io-exception occurs- that will be thrown
	 */
	public static Hashtable<String, Material> loadMtlLib(
			final String dir, final BufferedReader reader)
			throws IOException {
		final Hashtable<String, Material> mats = new Hashtable<String, Material>();
		String line = reader.readLine();
		newMtl:
		while (line != null) {
			while (!line.startsWith("newmtl"))
				line = reader.readLine();
			final String mtlName = line.split(" ")[1];
			Color ambient = new Color(1, 1, 1),
					diffuse = new Color(1, 1, 1),
					specular = new Color(1, 1, 1),
					emissive = new Color(1, 1, 1);
			Texture tex = null;
			double shininess = 1, alpha = 1;
			line = reader.readLine();
			// decodeMtl:
			do {
				final String[] lineparts = line.split(" ");
				switch (lineparts[0]) {
				case "Ka":
					ambient = generateColor(lineparts, 1);
					break;
				case "Kd":
					diffuse = generateColor(lineparts, 1);
					break;
				case "Ks":
					specular = generateColor(lineparts, 1);
					break;
				case "Ke":
					emissive = generateColor(lineparts, 1);
					break;
				case "Ns":
					shininess = Double.parseDouble(lineparts[1]);
					break;
				case "d":
					alpha = Double.parseDouble(lineparts[1]);
					break;
				case "map_Kd": {
					int index = 1;
					while (lineparts[index].isEmpty())
						index++;
					File given = new File(lineparts[index]);
					if (!given.isAbsolute())
						given = new File(dir, lineparts[index]);
					tex = Texture.load(given);
				}
					break;
				}
				line = reader.readLine();
				if (line == null)
					break;
				if (line.startsWith("newmtl")) {
					mats.put(mtlName,
							new Material(ambient, diffuse, specular,
									emissive, shininess, alpha, tex));
					continue newMtl;
				}
				if (line.length() < 2)
					line = null;
			} while (line != null);
			mats.put(mtlName, new Material(ambient, diffuse, specular,
					emissive, shininess, alpha, tex));
			line = reader.readLine();
		}
		return mats;
	}

	private static Color generateColor(final String[] lineparts,
			final int offset) {
		final double red = Double.parseDouble(lineparts[offset]);
		final double green = Double
				.parseDouble(lineparts[offset + 1]);
		final double blue = Double.parseDouble(lineparts[offset + 2]);
		final double alpha;
		if (lineparts.length > offset + 3) {
			alpha = Double.parseDouble(lineparts[offset + 3]);
		} else
			alpha = 1;
		return new Color(red, green, blue, alpha);
	}
}