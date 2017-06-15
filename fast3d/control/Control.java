package fast3d.control;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import fast3d.Panel3d;

/**
 * offers the possibility to react on user input<br>
 * a control implements all AWT-event-listeners which can be (un)bound using
 * activate() or deactivate()<br>
 * a panel3d should call on preRender() the performInput() method<br>
 * subclasses have to implement performInput() to do what they intend to change
 * on the panel3d<br>
 * subclasses should call activate(boolean, boolean, boolean, boolean, boolean)
 * in onActivate() to let the control register the desired listeners - same
 * procedure in onDeactivate()<br>
 * subclasses can easily override the AWT-event-methods to save the user action
 * until performInput is called<br>
 * <b> it is highly recommended do only do changes on the panel3d in
 * performInput() </b><br>
 * the backing Panel3d can be accessed by the protected field p3d
 * 
 * @see #performInput()
 * @see #activate()
 * @see #deactivate()
 * @see #activate(boolean, boolean, boolean, boolean, boolean)
 * @see #deactivate(boolean, boolean, boolean, boolean, boolean)
 * 
 * @author Tim Trense
 */
public abstract class Control
		implements MouseListener, MouseMotionListener,
		MouseWheelListener, KeyListener, ComponentListener,
		FocusListener, PropertyChangeListener, ContainerListener {

	protected final Panel3d p3d;
	private boolean isActivated = false;

	/**
	 * creates a new (not yet activated) control on the panel3d
	 * 
	 * @param p3d
	 *            the panel3d to be controlled
	 */
	public Control(final Panel3d p3d) {
		super();
		this.p3d = p3d;
	}

	/**
	 * applies the changes caused by the input to the universe if the control is
	 * activated
	 */
	public final void applyChanges() {
		if (isActivated())
			performInput();
	}

	/**
	 * modifies the panel3d
	 */
	protected abstract void performInput();

	/**
	 * sets the activation state to true and calls onActivate() to lets
	 * subclasses determine what listeners to register<br>
	 * 
	 * @see #isActivated()
	 * @see #onActivate()
	 */
	public void activate() {
		isActivated = true;
		onActivate();
	}

	/**
	 * subclasses should do init-stuff here and call activate(boolean, boolean,
	 * boolean, boolean, boolean)<br>
	 * 
	 * @see #activate(boolean, boolean, boolean, boolean, boolean)
	 */
	protected abstract void onActivate();

	/**
	 * sets the activation state to false and calls onDeactivate() to lets
	 * subclasses determine what listeners to unregister<br>
	 * 
	 * @see #isActivated()
	 * @see #onDeactivate()
	 */
	public void deactivate() {
		isActivated = false;
		onDeactivate();
	}

	/**
	 * subclasses should call deactivate(boolean, boolean, boolean, boolean,
	 * boolean)<br>
	 * 
	 * @see #deactivate(boolean, boolean, boolean, boolean, boolean)
	 */
	protected abstract void onDeactivate();

	/**
	 * @return whether activate() was called but deactivate() was not called yet
	 */
	public boolean isActivated() {
		return isActivated;
	}

	/**
	 * registers the listeners where the argument is true
	 * 
	 * @param key
	 *            keyListener
	 * @param mouse
	 *            mouseListener, mouseMotionListener, mouseWheelListener
	 * @param comp
	 *            componentListener, containerListener
	 * @param focus
	 *            focusListener
	 * @param prop
	 *            propertyChangeListener
	 */
	protected void activate(final boolean key, final boolean mouse,
			final boolean comp, final boolean focus,
			final boolean prop) {
		p3d.setFocusable(true);
		if (key)
			p3d.addKeyListener(this);
		if (mouse) {
			p3d.addMouseListener(this);
			p3d.addMouseMotionListener(this);
			p3d.addMouseWheelListener(this);
		}
		if (focus)
			p3d.addFocusListener(this);
		if (comp) {
			p3d.addComponentListener(this);
			p3d.addContainerListener(this);
		}
		if (prop)
			p3d.addPropertyChangeListener(this);
	}

	/**
	 * wrapper to the general activate-method - registers the KeyListener
	 */
	public void activateKeys() {
		activate(true, false, false, false, false);
	}

	/**
	 * wrapper to the general activate-method - registers all mouse related
	 * listeners
	 */
	public void activateMouse() {
		activate(false, true, false, false, false);
	}

	/**
	 * wrapper to the general activate-method - registers all component and
	 * container related listeners
	 */
	public void activateComponent() {
		activate(false, false, true, false, false);
	}

	/**
	 * wrapper to the general activate-method - registers the FocusListener
	 */
	public void activateFocus() {
		activate(false, false, false, true, false);
	}
	
	/**
	 * wrapper to the general activate-method - registers the PropertyChangeListener
	 */
	public void activateProperty(){
		activate(false, false, false, false, true);
	}

	/**
	 * wrapper to the general deactivate-method - registers the KeyListener
	 */
	public void deactivateKeys() {
		deactivate(true, false, false, false, false);
	}

	/**
	 * wrapper to the general deactivate-method - registers all mouse related
	 * listeners
	 */
	public void deactivateMouse() {
		deactivate(false, true, false, false, false);
	}

	/**
	 * wrapper to the general deactivate-method - registers all component and
	 * container related listeners
	 */
	public void deactivateComponent() {
		deactivate(false, false, true, false, false);
	}

	/**
	 * wrapper to the general deactivate-method - registers the FocusListener
	 */
	public void deactivateFocus() {
		deactivate(false, false, false, true, false);
	}
	
	/**
	 * wrapper to the general deactivate-method - registers the PropertyChangeListener
	 */
	public void deactivateProperty(){
		deactivate(false, false, false, false, true);
	}

	
	/**
	 * unregisters the listeners where the argument is true
	 * 
	 * @param key
	 *            keyListener
	 * @param mouse
	 *            mouseListener, mouseMotionListener, mouseWheelListener
	 * @param comp
	 *            componentListener, containerListener
	 * @param focus
	 *            focusListener
	 * @param prop
	 *            propertyChangeListener
	 */
	protected void deactivate(final boolean key, final boolean mouse,
			final boolean comp, final boolean focus,
			final boolean prop) {
		p3d.setFocusable(true);
		if (key)
			p3d.removeKeyListener(this);
		if (mouse) {
			p3d.removeMouseListener(this);
			p3d.removeMouseMotionListener(this);
			p3d.removeMouseWheelListener(this);
		}
		if (focus)
			p3d.removeFocusListener(this);
		if (comp) {
			p3d.removeComponentListener(this);
			p3d.removeContainerListener(this);
		}
		if (prop)
			p3d.removePropertyChangeListener(this);
	}

	public void keyPressed(KeyEvent event) {
	}

	public void keyReleased(KeyEvent event) {
	}

	public void keyTyped(KeyEvent event) {
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
	}

	public void mouseDragged(MouseEvent event) {
	}

	public void mouseMoved(MouseEvent event) {
	}

	public void mouseClicked(MouseEvent event) {
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void componentHidden(ComponentEvent event) {
	}

	public void componentMoved(ComponentEvent event) {
	}

	public void componentResized(ComponentEvent event) {
	}

	public void componentShown(ComponentEvent event) {
	}

	public void focusGained(FocusEvent event) {
	}

	public void focusLost(FocusEvent event) {
	}

	public void propertyChange(PropertyChangeEvent event) {
	}

	public void componentAdded(ContainerEvent event) {
	}

	public void componentRemoved(ContainerEvent event) {
	}
}