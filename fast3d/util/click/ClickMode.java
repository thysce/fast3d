package fast3d.util.click;

/**
 * specifies how to interpret a click on a panel3d
 * 
 * @see fast3d.util.click.ClickManager
 * @author Tim Trense
 */
public enum ClickMode {

	/**
	 * a single ray through the panel3d's center is generated on a user click
	 */
	ON_CENTER,

	/**
	 * a single ray through the click-position of the awt-click on the panel3d
	 * is generated on a user click
	 */
	ON_MOUSE_POS,

	/**
	 * if the user drags the mouse over an area of the screen, for every pixel
	 * in that rectangle a ray is generated through it and all rayTraced
	 * Renderables for every ray are considered as being clicked, for every ray
	 * through the renderable it will appear one time in the array passed to the
	 * clickListener3d
	 */
	IN_BOUNDS,

	/**
	 * if the user drags the mouse over an area of the screen, for every pixel
	 * in that rectangle a ray is generated through it and all rayTraced
	 * Renderables for every ray are considered as being clicked<br>
	 * every renderable will appear only once in the array passed to the
	 * clickListener3d
	 */
	IN_BOUNDS_ONCE

}