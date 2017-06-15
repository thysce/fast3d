package fast3d.simple.shapes;

import fast3d.complex.Group;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.renderables.Triangle;
import fast3d.util.ColorGen;

/**
 * a geometric body builder to create boxes, cubes, colorcube or rectangular
 * prisms
 * 
 * @author Tim Trense
 */
public class Box extends GeometricBodyBuilder {

	/**
	 * the dimension along the x axis
	 */
	public double sizeX = 1;
	/**
	 * the dimension along the y axis
	 */
	public double sizeY = 1;
	/**
	 * the dimension along the z axis
	 */
	public double sizeZ = 1;

	/**
	 * in addition to build() you may call colorcube() to create a shape for
	 * debug
	 * 
	 * @return a random colored cube of size 1
	 */
	public static Group colorcube() {
		final Group g = new Group();

		final Vector3d[] e = cubeEdges(1, 1, 1);

		final Color[] c = new Color[6];
		for (int i = 0; i < c.length; i++)
			c[i] = ColorGen.random();

		g.triangles.add(new Triangle(e[0], e[1], e[3], c[0])); // u1
		g.triangles.add(new Triangle(e[1], e[2], e[3], c[0])); // u2

		g.triangles.add(new Triangle(e[0], e[1], e[4], c[1])); // v1
		g.triangles.add(new Triangle(e[1], e[5], e[4], c[1])); // v2

		g.triangles.add(new Triangle(e[4], e[5], e[7], c[2])); // o1
		g.triangles.add(new Triangle(e[5], e[6], e[7], c[2])); // o2

		g.triangles.add(new Triangle(e[7], e[3], e[2], c[3])); // h1
		g.triangles.add(new Triangle(e[2], e[6], e[7], c[3])); // h2

		g.triangles.add(new Triangle(e[4], e[3], e[0], c[4])); // l1
		g.triangles.add(new Triangle(e[4], e[7], e[3], c[4])); // l2

		g.triangles.add(new Triangle(e[5], e[1], e[2], c[5])); // r1
		g.triangles.add(new Triangle(e[5], e[2], e[6], c[5])); // r2

		return g;

	}

	@Override
	protected void build(final Group g, final Color color) {
		final Vector3d[] e = cubeEdges(sizeX, sizeY, sizeZ);

		g.triangles.add(new Triangle(e[0], e[3], e[1], color)); // u1
		g.triangles.add(new Triangle(e[1], e[3], e[2], color)); // u2

		g.triangles.add(new Triangle(e[0], e[1], e[4], color)); // v1
		g.triangles.add(new Triangle(e[1], e[5], e[4], color)); // v2

		g.triangles.add(new Triangle(e[4], e[5], e[7], color)); // o1
		g.triangles.add(new Triangle(e[5], e[6], e[7], color)); // o2

		g.triangles.add(new Triangle(e[7], e[2], e[3], color)); // h1
		g.triangles.add(new Triangle(e[2], e[7], e[6], color)); // h2

		g.triangles.add(new Triangle(e[4], e[3], e[0], color)); // l1
		g.triangles.add(new Triangle(e[4], e[7], e[3], color)); // l2

		g.triangles.add(new Triangle(e[5], e[1], e[2], color)); // r1
		g.triangles.add(new Triangle(e[5], e[2], e[6], color)); // r2
	}

	@Override
	protected void build(final Group g, final Material material) {
		final Vector3d[] e = cubeEdges(sizeX, sizeY, sizeZ);

		g.triangles.add(new AdvTriangle(e[0], e[3], e[1], material)); // u1
		g.triangles.add(new AdvTriangle(e[1], e[3], e[2], material)); // u2

		g.triangles.add(new AdvTriangle(e[0], e[1], e[4], material)); // v1
		g.triangles.add(new AdvTriangle(e[1], e[5], e[4], material)); // v2

		g.triangles.add(new AdvTriangle(e[4], e[5], e[7], material)); // o1
		g.triangles.add(new AdvTriangle(e[5], e[6], e[7], material)); // o2

		g.triangles.add(new AdvTriangle(e[7], e[2], e[3], material)); // h1
		g.triangles.add(new AdvTriangle(e[2], e[7], e[6], material)); // h2

		g.triangles.add(new AdvTriangle(e[4], e[3], e[0], material)); // l1
		g.triangles.add(new AdvTriangle(e[4], e[7], e[3], material)); // l2

		g.triangles.add(new AdvTriangle(e[5], e[1], e[2], material)); // r1
		g.triangles.add(new AdvTriangle(e[5], e[2], e[6], material)); // r2
	}

	private static Vector3d[] cubeEdges(double sx, double sy, double sz) {
		sx /= 2d;
		sy /= 2d;
		sz /= 2d;
		final Vector3d vul = new Vector3d(-sx, -sy, sz);
		final Vector3d vur = new Vector3d(sx, -sy, sz);
		final Vector3d hul = new Vector3d(-sx, -sy, -sz);
		final Vector3d hur = new Vector3d(sx, -sy, -sz);
		final Vector3d vol = new Vector3d(-sx, sy, sz);
		final Vector3d vor = new Vector3d(sx, sy, sz);
		final Vector3d hol = new Vector3d(-sx, sy, -sz);
		final Vector3d hor = new Vector3d(sx, sy, -sz);
		return new Vector3d[] { vul, // 0
				vur, // 1
				hur, // 2
				hul, // 3
				vol, // 4
				vor, // 5
				hor, // 6
				hol // 7
		};
	}

}