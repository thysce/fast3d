package fast3d.simple.fragment;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import fast3d.complex.Universe;
import fast3d.fragment.FragmentShader;
import fast3d.fragment.FragmentShaderManager;

/**
 * a default implementation of FragmentShaderManager using DefaultFragmentShader
 * 
 * @author Tim Trense
 */
public class DefaultFragmentShaderManager extends FragmentShaderManager {

	/**
	 * calls super constructor
	 * 
	 * @param uni
	 *            the universe to render
	 * @param threadcount
	 *            the count of shader to split the process up to, to accelerate
	 *            it
	 */
	public DefaultFragmentShaderManager(final Universe uni, final int threadcount) {
		super(uni, threadcount);
	}

	@Override
	protected FragmentShader createShader(final Universe uni, final BufferedImage img,
			final Rectangle rect) {
		return new DefaultFragmentShader(uni, img, rect);
	}

}