package example2;

import fast3d.complex.light.Material;
import fast3d.graphics.Color;
import fast3d.math.Camera;
import fast3d.math.Vector3d;
import fast3d.renderables.AdvTriangle;
import fast3d.simple.SimplePanel3d;
import fast3d.simple.controls.RotationControl;
import fast3d.util.ColorGen;

public class Example0 {

	public static void main(String[] args){
		
		final SimplePanel3d p3d = new SimplePanel3d();

		p3d.setSize(800, 600);
		p3d.showInFrame(true);

		final Camera cam = p3d.getCam();		//short for p3d.getUniverse().getCam()
		final Vector3d position = new Vector3d(0, 0, 5);
		cam.moveTo(position);
		final Vector3d upVector = Vector3d.up();
		final Vector3d lookDirection = Vector3d.forward();
		cam.lookInDirection(lookDirection, upVector);

		final RotationControl uc = 
				new RotationControl(p3d);
		
		p3d.setControl(uc);
		uc.activate();
		p3d.frameRate().setFPS(60);
		
		//colors are defines via their RGB-parts and alpha-component (which is opaque =1 as default)
		//the color-components are defined as in the interval [0;1]
		final Material surface=new Material(
				new Color(.9,.8,.1, .5),		//color for AmbientLight R,G,B,A (Alpha is optional)
				ColorGen.CORNFLOWER_BLUE(),		//color for diffuse lights (PointLight || DirectionalLight)
				new Color(.5,.5,.9),			//color for shininess SpecularLight
				ColorGen.BLACK(),				//emissive color (definitely darkest possible color)
				25								//shininess coefficient
				);
		final Vector3d edge1 = new Vector3d(-1	,1	, 0);
		final Vector3d edge2 = new Vector3d(1	,1	, 0);
		final Vector3d edge3 = new Vector3d(0	,-1	, 1.3);
		
		final AdvTriangle triang=new AdvTriangle(edge1, edge2, edge3, surface);
		triang.getNormal().invert();
		p3d.getUniverse().add(triang);
		
		//adds one instance of every Phong-Light to the scenery for debug
		p3d.getUni().enableDefaultLighting();
		//if changes on the lights are done p3d.getUniverse().invalidateLights() should be called
		//to refresh the visible-light-buffer (enebaleDefaultLighting does that automatically)
	}
	
}
