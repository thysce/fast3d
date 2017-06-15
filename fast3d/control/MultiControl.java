package fast3d.control;

import java.util.ArrayList;
import java.util.List;

/**
 * a very easy to use control: add all the controls you want to apply to a
 * Panel3d to the filed subControls and than add this control to the Panel3d<br>
 * at onActivate() all subControls are activated (equivalent at onDeactivate())
 * <br>
 * at performInput() all controls are asked to perform their input<br>
 * the call to the subControls is in the order, specified by the list<br>
 * by call activate() you activate all subControls (similar to deactivate())<br>
 * the built-in simple controls do not work together because they override the
 * entire camera-positioning every frame -- controls used by MulitControl should
 * be designed to work together
 * 
 * @author Tim Trense
 */
public class MultiControl extends Control {

	/**
	 * add all controls that should be applied to the Panel3d here
	 */
	public final List<Control> subControls;

	/**
	 * constructs a plain MultiControl that does nothing but call it's
	 * subControls
	 * 
	 * @param init
	 *            the initially added controls
	 */
	public MultiControl(final Control... init) {
		super(null);
		subControls = new ArrayList<Control>(3);
		if (init != null && init.length > 0)
			for (Control c : init)
				subControls.add(c);
	}

	/**
	 * constructs a plain MultiControl that does nothing but call it's
	 * subControls
	 * 
	 * @param init
	 *            the initially added controls
	 * @param reference
	 *            whether to use the given list by reference(true) or copy all
	 *            it's data to a new array-list at init's size
	 */
	public MultiControl(final List<Control> init,
			final boolean reference) {
		super(null);
		if (reference)
			subControls = init;
		else {
			subControls = new ArrayList<Control>(init.size());
			subControls.addAll(init);
		}
	}

	@Override
	protected void performInput() {
		for (Control c : subControls)
			if (c != null)
				c.applyChanges();
	}

	@Override
	protected void onActivate() {
		for (Control c : subControls)
			if (c != null)
				c.activate();
	}

	@Override
	protected void onDeactivate() {
		for (Control c : subControls)
			if (c != null)
				c.deactivate();
	}

}