package example0;

import fast3d.Panel3d;
import fast3d.complex.Universe;
import fast3d.graphics.Color;
import fast3d.math.Camera;
import fast3d.math.Vector3d;
import fast3d.renderables.Line;
import fast3d.simple.SimplePanel3d;
import fast3d.util.ColorGen;

public class Example0_1 {

	public static void main(String[] args) {
		
		final Panel3d p3d=new SimplePanel3d();
		
		p3d.setSize(800,600);
		((SimplePanel3d)p3d).showInFrame(false, true);
		
		final Vector3d start = new Vector3d(-1,0,0);
		final Vector3d end = new Vector3d(1,1,0);
		final Color col = ColorGen.YELLOW();
		final Line line = new Line(start, end, col);
		
		final Universe uni = p3d.getUniverse();
		uni.add(line);
		
		final Camera cam = uni.getCam();
		final Vector3d position = new Vector3d(0,0,5);
		cam.moveTo(position);
		final Vector3d upVector = Vector3d.up();
		final Vector3d lookDirection = Vector3d.forward();
		cam.lookInDirection(lookDirection, upVector);
		
		p3d.repaint();
	}

}
