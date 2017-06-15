package _tutorial.src.Fast3dTutorial.example8;

import java.io.File;

import fast3d.complex.Scene;
import fast3d.simple.SimplePanel3d;
import fast3d.util.serial.F3ObjExporter;
import fast3d.util.serial.F3ObjImporter;

public class F3ObjExample {

	public static void detailed() {

		boolean success;

		{// should be done before release
			final File objfile = new File("myobj.obj");
			success = F3ObjExporter.convert(objfile, true);
			// true for use-compression
			// myobj.obj --> myobj.f3obj
		}

		final F3ObjImporter imp = new F3ObjImporter(
				new File("myobj.f3obj"));

		success = imp.loadSave();

		if (success) {

			final Scene scene = imp.scene;

			final SimplePanel3d p3d = new SimplePanel3d();
			p3d.getUniverse().add(scene);

			// ...

		} else
			System.err.println(imp.exception);
	}

	public static void fast() {
		
		// should be done before release
		F3ObjExporter.convert(new File("myobj.obj"), true); 
		
		final Scene scene = F3ObjImporter.load("myobj.f3obj");
		final SimplePanel3d p3d = new SimplePanel3d();
		p3d.getUniverse().add(scene);

		// ...
	}
}