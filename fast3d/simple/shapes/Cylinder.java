package fast3d.simple.shapes;

import java.util.ArrayList;
import java.util.List;

import fast3d.complex.Group;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.renderables.Triangle;
import fast3d.util.TriangleStripGenerator;
import fast3d.util.TriangleStripGenerator2d;
import fast3d.util.math.MathUtil;

/**
 * a geometric body builder to create cylinders or general prisms
 * 
 * @author Tim Trense
 */
public class Cylinder extends GeometricBodyBuilder {

	/**
	 * how many vertices are used to approach the circular ground
	 */
	public int resolution = 30;

	/**
	 * the height of the cone
	 */
	public double height = 1;
	/**
	 * the radius of the ground
	 */
	public double radius = .5;

	/**
	 * whether to make the cone stand on the xz-plate (true) or let is center be
	 * (0,0,0) (false)
	 */
	public boolean onGround = false;

	/**
	 * sets how many polygons will be created
	 * 
	 * @param count
	 *            the amount of polygons created
	 */
	public void setPolygonCount(final int count) {
		resolution = count / 4;
	}

	/**
	 * sets resolution to 60
	 */
	public void setHighPoly() {
		resolution = 60;
	}

	/**
	 * sets resolution to 90
	 */
	public void setXHighPoly() {
		resolution = 90;
	}

	/**
	 * sets resolution to 20
	 */
	public void setMediumPoly() {
		resolution = 20;
	}

	/**
	 * sets resolution to 7
	 */
	public void setLowPoly() {
		resolution = 7;
	}

	@Override
	protected void build(Group plain, Color color) {
		final Vector3d peak = new Vector3d(0,
				onGround ? height : height / 2, 0);
		final Vector3d center = new Vector3d(0,
				onGround ? 0 : -height / 2, 0);

		final List<Vector3d> upperRing = new ArrayList<Vector3d>(
				resolution + 2);
		final List<Vector3d> lowerRing = new ArrayList<Vector3d>(
				resolution + 2);

		final double res = MathUtil.twoPi / resolution;
		for (double rad = -res; rad < MathUtil.twoPi; rad += res) {
			Vector3d v = new Vector3d(rad, 0, radius);
			v.toCartesian();
			if (!onGround)
				v.y = -height / 2;
			lowerRing.add(v);
			v = new Vector3d(rad, 0, radius);
			v.toCartesian();
			if (!onGround)
				v.y = height;
			else
				v.y = height / 2;
			upperRing.add(v);
		}

		final TriangleStripGenerator ground = new TriangleStripGenerator() {
			public Triangle makeTriangle(Vector3d edge1,
					Vector3d edge2, Vector3d edge3) {
				return new Triangle(edge1, edge2, edge3, color);
			}
		};
		ground.keepPreLast = true;
		ground.add(center);
		for (Vector3d v : lowerRing)
			ground.add(v);
		plain.triangles.addAll(ground.getTriangles());

		final TriangleStripGenerator top = new TriangleStripGenerator() {
			public Triangle makeTriangle(Vector3d edge1,
					Vector3d edge2, Vector3d edge3) {
				return new Triangle(edge1, edge2, edge3, color);
			}
		};
		top.keepPreLast = true;
		top.add(peak);
		for (Vector3d v : upperRing)
			top.add(v);
		plain.triangles.addAll(top.getTriangles());

		final TriangleStripGenerator2d side = new TriangleStripGenerator2d(
				upperRing.size()) {
			public Triangle makeTriangle(Vector3d edge1,
					Vector3d edge2, Vector3d edge3) {
				return new Triangle(edge1, edge2, edge3, color);
			}
		};
		for (Vector3d v : upperRing)
			side.add(v);
		for (Vector3d v : lowerRing)
			side.add(v);
		plain.triangles.addAll(side.getTriangles());
	}

	@Override
	protected void build(Group plain, Material material) {
		final Vector3d peak = new Vector3d(0,
				onGround ? height : height / 2, 0);
		final Vector3d center = new Vector3d(0,
				onGround ? 0 : -height / 2, 0);

		final List<Vector3d> upperRing = new ArrayList<Vector3d>(
				resolution + 2);
		final List<Vector3d> lowerRing = new ArrayList<Vector3d>(
				resolution + 2);

		final double res = MathUtil.twoPi / resolution;
		for (double rad = -res; rad < MathUtil.twoPi; rad += res) {
			Vector3d v = new Vector3d(rad, 0, radius);
			v.toCartesian();
			if (!onGround)
				v.y = -height / 2;
			lowerRing.add(v);
			v = new Vector3d(rad, 0, radius);
			v.toCartesian();
			v.y = peak.y;
			upperRing.add(v);
		}

		final TriangleStripGenerator ground = new TriangleStripGenerator() {
			public Triangle makeTriangle(Vector3d edge1,
					Vector3d edge2, Vector3d edge3) {
				return new AdvTriangle(edge1, edge2, edge3, material);
			}
		};
		ground.keepPreLast = true;
		ground.add(center);
		for (Vector3d v : lowerRing)
			ground.add(v);
		plain.triangles.addAll(ground.getTriangles());

		final TriangleStripGenerator top = new TriangleStripGenerator() {
			public Triangle makeTriangle(Vector3d edge1,
					Vector3d edge2, Vector3d edge3) {
				return new AdvTriangle(edge1, edge2, edge3, material);
			}
		};
		top.keepPreLast = true;
		top.add(peak);
		for (Vector3d v : upperRing)
			top.add(v);
		plain.triangles.addAll(top.getTriangles());

		final TriangleStripGenerator2d side = new TriangleStripGenerator2d(
				upperRing.size()) {
			public Triangle makeTriangle(Vector3d edge1,
					Vector3d edge2, Vector3d edge3) {
				return new AdvTriangle(edge1, edge2, edge3, material);
			}
		};
		for (Vector3d v : upperRing)
			side.add(v);
		for (Vector3d v : lowerRing)
			side.add(v);
		plain.triangles.addAll(side.getTriangles());
	}

}
