package _tutorial.src.Fast3dTutorial.example5;

import javax.swing.JFrame;

import fast3d.complex.Scene;
import fast3d.complex.light.AmbientLight;
import fast3d.complex.light.DirectionalLight;
import fast3d.complex.light.Light;
import fast3d.fragment.FragPanel3d;
import fast3d.graphics.Color;
import fast3d.math.Camera;
import fast3d.math.Vector3d;
import fast3d.simple.SimpleUniverse;
import fast3d.util.serial.ObjFileLoader;

public class Example0 {

	public static void main(String[] args) {

		// panel that can render raytracing-images
		final FragPanel3d fp3d = new FragPanel3d();

		final Scene scn = ObjFileLoader.load("example5/box.obj");
		if (scn != null)
			scn.addToUniverse(fp3d.getUniverse());

		Light light;
		light = new AmbientLight(new Color(.1, .1, .1));
		fp3d.getUniverse().addLight(light);

		// give universe for shadowing calculations
		// color and direction of light-rays
		light = new DirectionalLight(fp3d.getUniverse(), new Color(.9, 1, 0),
				new Vector3d(-1, -1, -1));
		fp3d.getUniverse().addLight(light);
		((SimpleUniverse)fp3d.getUniverse()).enableDefaultLighting();

		showInFrame(fp3d);

		// position and align camera
		final Camera cam = fp3d.getUniverse().getCam();
		//cam.setNominalView(); // position = (0,1,3) look to zero
		cam.moveTo(new Vector3d(-3,1,10));
		cam.lookTo(Vector3d.zero(), Vector3d.up());

		//starts the shader-manager and produces a new image- may take a moment
		fp3d.startRender();
	}

	private static void showInFrame(FragPanel3d fp3d) {
		final JFrame frame = new JFrame("achieved with fast3d");
		frame.setContentPane(fp3d);
		frame.setSize(1800, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}