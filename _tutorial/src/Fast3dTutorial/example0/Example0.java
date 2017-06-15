package _tutorial.src.Fast3dTutorial.example0;

import fast3d.simple.SimplePanel3d;

public class Example0 {

	public static void main(String[] args) {

		// new swing-component for a blank 3d-view
		final SimplePanel3d p3d = new SimplePanel3d();

		// for debug : construct a JFrame and set the SimplePanel3d as it's
		// content pane
		p3d.showInFrame(false // show in fullscreen - no - size: 500x500 or the
								// SimplePanel3ds size if set
				, true // exit on close
		);
	}

}
