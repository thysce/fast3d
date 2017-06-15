package fast3d.renderables;

import fast3d.graphics.Color;
import fast3d.graphics.Graphics3d;
import fast3d.util.ColorGen;

/**
 * a simple crosshair in the middle of the screen
 * 
 * @author Tim Trense
 *
 */
public class CrosshairHUD extends HUD {

	private final java.awt.Color color;
	private final int size;

	/**
	 * constructs a new crosshair with the stroke-length of sizePX and the given
	 * color
	 * 
	 * @param sizePX
	 *            the length of the two strokes making the crosshair
	 * @param color
	 *            the color of the crosshair
	 */
	public CrosshairHUD(final int sizePX, final Color color) {
		super();
		this.size = sizePX;
		this.color = color.awtColor();
	}
	
	@Override
	public void render(final Graphics3d g) {
		final int width2 = g.getShader().screenWidthPX / 2;
		final int height2 = g.getShader().screenHeightPX / 2;
		final int size2 = size / 2;

		g.getGraphics2d().setColor(color);
		g.getGraphics2d().drawLine(width2 - size2, height2 - size2, width2 + size2, height2 + size2);
		g.getGraphics2d().drawLine(width2 - size2, height2 + size2, width2 + size2, height2 - size2);
	}

	/**
	 * returns a clone
	 */
	public Color getColor() {
		return ColorGen.fromAWTColor(color);
	}

	/**
	 * @return the length of the two strokes making the crosshair
	 */
	public int getSize() {
		return size;
	}

}