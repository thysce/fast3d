package example7;

import fast3d.graphics.Turtle3d;
import fast3d.math.Vector3d;
import fast3d.simple.TurtlePanel3d;
import fast3d.simple.controls.RotationControl;
import fast3d.util.ColorGen;
import fast3d.util.math.MathUtil;

@SuppressWarnings("serial")
public class TurtleTree extends TurtlePanel3d {

	@Override
	public void render(final Turtle3d t3d) {
		t3d.setColor(ColorGen.YELLOW());
		renderBranch(t3d, 1);
	}

	// recursive
	private void renderBranch(final Turtle3d t, double size) {
		if (size < 0.01)
			return;
		t.tmove(Vector3d.up().scale(size));
		size /= 2;

		// branch to left
		t.tpush();
		t.trotZ(MathUtil.piOver4);
		renderBranch(t, size);
		t.tpop();

		// branch to right
		t.tpush();
		t.trotZ(-MathUtil.piOver4);
		renderBranch(t, size);
		t.tpop();

		// branch to front
		t.tpush();
		t.trotX(MathUtil.piOver4);
		renderBranch(t, size);
		t.tpop();

		// branch to back
		t.tpush();
		t.trotX(-MathUtil.piOver4);
		renderBranch(t, size);
		t.tpop();
	}

	public static void main(final String[] args) {
		final TurtleTree tree = new TurtleTree();

		final RotationControl uc = new RotationControl(
				tree);
		uc.allowNegativeRotX = true;
		uc.activate();
		tree.frameRate().setFPS(30);
		tree.setControl(uc);

		tree.showInFrame(true);
	}
}
