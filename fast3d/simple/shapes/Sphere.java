package fast3d.simple.shapes;

import fast3d.complex.Group;
import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.renderables.Triangle;
import fast3d.util.TriangleStripGenerator2d;
import fast3d.util.math.MathUtil;

/**
 * a geometric body builder to create spheres (no ellipses)<br>
 * ellipses can be achieved by calling g.forEveryVertex(v lambda v.scale(Vector3d));
 * on the resulting Group
 * 
 * @author Tim Trense
 */
public class Sphere extends GeometricBodyBuilder {

	/**
	 * the size of the sphere
	 */
	public double radius = .5;
	/**
	 * how many polygons will be created for latitude
	 */
	public int resolutionLatitude = 20;
	/**
	 * how many polygons will be created for longitude
	 */
	public int resolutionLongitude = 50;

	/**
	 * sets how many polygons will be created to approach a curved latitude or
	 * longitude
	 * 
	 * @param res
	 *            the square root of the count of polygons
	 */
	public void setResolution(final int res) {
		resolutionLatitude = res;
		resolutionLongitude = res;
	}

	/**
	 * sets how many polygons will be created
	 * 
	 * @param count
	 *            the amount of polygons created
	 */
	public void setPolygonCount(final int count) {
		setResolution((int) Math.sqrt(count / 2));
	}

	/**
	 * sets resolution to 50
	 */
	public void setHighPoly() {
		setResolution(50);
	}

	/**
	 * sets resolution longitude to 150 and resolution latitude to 90
	 */
	public void setXHighPoly() {
		resolutionLongitude = 150;
		resolutionLatitude = 90;
	}

	/**
	 * sets resolution longitude to 32 and resolution latitude to 8
	 */
	public void setMediumPoly() {
		resolutionLongitude = 32;
		resolutionLatitude = 8;
	}

	/**
	 * sets resolution to 16 for longitude and 5 for latitude
	 */
	public void setLowPoly() {
		resolutionLongitude = 16;
		resolutionLatitude = 4;
	}

	@Override
	protected void build(final Group plain, final Color color) {
		final TriangleStripGenerator2d tsg = new TriangleStripGenerator2d(
				resolutionLatitude + 1) {
			@Override
			public Triangle makeTriangle(Vector3d edge1,
					Vector3d edge2, Vector3d edge3) {
				return new Triangle(edge1, edge2, edge3, color);
			}
		};

		buildSphere(tsg);

		plain.triangles.addAll(tsg.getTriangles());
	}

	@Override
	protected void build(Group plain, Material material) {
		final TriangleStripGenerator2d tsg = new TriangleStripGenerator2d(
				resolutionLatitude + 1) {
			@Override
			public Triangle makeTriangle(Vector3d edge1,
					Vector3d edge2, Vector3d edge3) {
				return new AdvTriangle(edge1, edge2, edge3, material);
			}
		};

		buildSphere(tsg);

		plain.triangles.addAll(tsg.getTriangles());
	}

	private void buildSphere(final TriangleStripGenerator2d tsg) {
		final double degPerVexLon = MathUtil.twoPi
				/ resolutionLongitude;
		final double degPerVexLat = MathUtil.pi / resolutionLatitude;
		for (double b = 0; b < MathUtil.twoPi
				+ degPerVexLon; b += degPerVexLon) {
			for (double h = -MathUtil.piOver2
					- degPerVexLat; h < MathUtil.piOver2; h += degPerVexLat) {
				final Vector3d vertex = new Vector3d(b, h, radius);
				vertex.toCartesian();
				tsg.add(vertex);
			}
			tsg.reverseOrder = b < MathUtil.pi;
		}
	}

}