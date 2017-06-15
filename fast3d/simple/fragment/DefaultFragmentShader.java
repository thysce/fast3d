package fast3d.simple.fragment;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import fast3d.Renderable;
import fast3d.complex.Universe;
import fast3d.complex.light.Illuminatable;
import fast3d.complex.light.Light;
import fast3d.complex.light.Material;
import fast3d.complex.light.ObstructableLight;
import fast3d.fragment.FragmentShader;
import fast3d.graphics.Color;
import fast3d.math.Camera;
import fast3d.math.Vector3d;
import fast3d.math.Viewmode;
import fast3d.util.ColorGen;

/**
 * a default implementation of FragmentShader<br>
 * subclasses are permitted the possibility to override only some methods
 * described in the doc of FragmentShader<br>
 * default implementation uses the built-in phong illumination model and capable
 * of handling perspective and orthographic shading<br>
 * CameraMode.oriented and CameraMode.inscreen are implicitly considered true
 * and neither touched nor handled
 * 
 * @author Tim Trense
 */
public class DefaultFragmentShader extends FragmentShader {

	/**
	 * calls super constructor
	 * 
	 * @param uni
	 *            the universe to shade
	 * @param img
	 *            the image where to shade the universe to
	 * @param area
	 *            the area of the image to shade
	 */
	public DefaultFragmentShader(final Universe uni,
			final BufferedImage img, final Rectangle area) {
		super(uni, img, area);
	}

	/**
	 * calls super constructor
	 * 
	 * @param uni
	 *            the universe to shade
	 * @param img
	 *            the image where to shade the universe to
	 */
	public DefaultFragmentShader(final Universe uni,
			final BufferedImage img) {
		super(uni, img);
	}

	/**
	 * calls super constructor with the a plain image in the specified size of
	 * type BufferedImage.TYPE_INT_ARGB and the rectangle area being the entire
	 * image
	 * 
	 * @param uni
	 *            the universe to shade
	 * @param imagewidth
	 *            the width of the image to shade to
	 * @param imageheight
	 *            the height of the image to shade to
	 */
	public DefaultFragmentShader(final Universe uni,
			final int imagewidth, final int imageheight) {
		super(uni, new BufferedImage(imagewidth, imageheight,
				BufferedImage.TYPE_INT_ARGB));
	}

	@Override
	protected Color shadeFragment(final int x, final int y) {
		return shadeFragment((double) x / getImage().getWidth(),
				(double) y / getImage().getHeight());
	}

	@Override
	protected Color shadeFragment(final double xratio,
			double yratio) {
		// convert x,y to start-vector
		final Camera cam = getUniverse().getCam();
		final Vector3d pixel = cam.getScreenOrig().clone();
		pixel.add(cam.getScreenWidth().clone().scale(xratio));
		pixel.add(cam.getScreenHeight().clone().scale(yratio));
		// create ray
		final Vector3d start;
		final Vector3d dir;
		if (cam.mode.viewmode == Viewmode.ORTHOGONAL) {
			start = pixel.clone();
			if (!cam.mode.notincam)
				start.add(cam.getLookDir().invert());
			dir = cam.getLookDir();
		} else {
			if (cam.mode.notincam)
				start = pixel.clone();
			else
				start = cam.getPos().clone();
			dir = cam.getPos().to(pixel);
		}
		return shadeFragment(start, dir);
	}

	@Override
	protected Color shadeFragment(final Vector3d start,
			final Vector3d dir) {
		final Hashtable<Renderable, Vector3d> traced = getUniverse()
				.rayTrace(start, dir);
		if (traced == null || traced.isEmpty())
			return ColorGen.BLACK();
		else
			return shadeFragment(traced);
	}

	@Override
	protected Color shadeFragment(
			final Hashtable<Renderable, Vector3d> traced) {
		final Renderable closest = findClosestToTheCam(traced);
		if (closest != null) {
			final Vector3d puncture = traced.get(closest);
			final Color first = shadeFragment(closest, puncture);
			if (first != null && first.alpha() < 1) {
				traced.remove(closest);
				final Color second = shadeFragment(traced); // recursive
				if (second != null) {
					first.add(second);
					first.constrain(0, 1);
				}
			}
			return first;
		} else
			return null;
	}

	@Override
	protected Color shadeFragment(final Renderable closest,
			final Vector3d puncture) {
		if (closest instanceof Illuminatable) {
			return calculateCompleteIllumination(
					(Illuminatable) closest, puncture);
		} else
			return closest.getColor();
	}

	@Override
	protected Color calculateCompleteIllumination(
			final Illuminatable closest, final Vector3d puncture) {
		final List<Color> cols = new LinkedList<Color>();
		for (Light light : getUniverse().getLights())
			cols.add(calculateIllumination(closest, puncture, light));
		final Material mat = closest.getMaterial();
		Color col = (mat != null) ? mat.getEmissive() : null;
		if (col == null)
			col = ColorGen.BLACK();
		for (Color c : cols) {
			if (c == null) {
				continue;
			}
			col.add(c);
		}
		col.constrain(0, 1);
		return col;
	}

	@Override
	protected Color calculateIllumination(final Illuminatable l,
			final Vector3d puncture, final Light light) {
		if (light instanceof ObstructableLight) {
			final ObstructableLight obslight = (ObstructableLight) light;
			final Vector3d dir = obslight.directionOfLight(l).clone()
					.invert();
			final Hashtable<Renderable, Vector3d> all = getUniverse()
					.rayTrace(puncture, dir);
			all.remove(l);
			if (!all.isEmpty())
				return ColorGen.BLACK();
			else
				return light.illuminate(l);
		} else
			return light.illuminate(l);
	}

	@Override
	protected Renderable findClosestToTheCam(
			final Hashtable<Renderable, Vector3d> punctures) {
		if (punctures == null || punctures.size() == 0)
			return null;

		Renderable close = null;
		double closeDistance = Double.MAX_VALUE;
		double currentDistance;
		Vector3d v;
		for (Renderable r : punctures.keySet()) {
			v = punctures.get(r);
			if (v != null)
				currentDistance = v
						.distanceTo(getUniverse().getCam().getPos());
			else {
				currentDistance = Double.MAX_VALUE;
			}
			if (currentDistance < closeDistance) {
				closeDistance = currentDistance;
				close = r;
			}
		}
		return close;
	}

}