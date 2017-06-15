package fast3d.util;

import fast3d.math.Vector3d;
import fast3d.simple.FrameRate;

/**
 * a very simple call providing the basics to simulate accelerated movement
 * 
 * @author Tim Trense
 */
public class AVec {

	/**
	 * the location of what is to be moved
	 */
	public final Vector3d position;
	/**
	 * the current oriented speed
	 */
	public final Vector3d velocity;
	/**
	 * the current oriented accumulation of applied forces
	 */
	public final Vector3d acceleration;
	/**
	 * the mass of what is to move (the higher the mass, the less strong forces
	 * result in speed) - only for new added forces
	 */
	public double mass;
	/**
	 * the maximum length of the acceleration vector
	 */
	public double maximumAcceleration = Double.POSITIVE_INFINITY;
	/**
	 * the maximum length of the velocity vector
	 */
	public double maximumVelocity = Double.POSITIVE_INFINITY;

	/**
	 * constructs a new accelerated movement at position (0,0,0) with mass 1
	 * standing still
	 * 
	 */
	public AVec() {
		this(Vector3d.zero(), 1);
	}

	/**
	 * constructs a new accelerated movement at position (0,0,0) standing still
	 * 
	 * @param mass
	 *            the mass field data
	 */
	public AVec(final double mass) {
		this(Vector3d.zero(), mass);
	}

	/**
	 * constructs a new accelerated movement with mass of 1d standing still
	 * 
	 * @param position
	 *            where to be initially
	 */
	public AVec(final Vector3d position) {
		this(position, 1);
	}

	/**
	 * constructs a new accelerated movement standing still
	 * 
	 * @param position
	 *            where to be initially
	 * @param mass
	 *            the mass field data
	 */
	public AVec(final Vector3d position, final double mass) {
		super();
		this.position = position;
		this.mass = mass;
		this.velocity = Vector3d.zero();
		this.acceleration = Vector3d.zero();
		if (mass == 0)
			throw new IllegalArgumentException("Mass cannot be 0");
	}

	/**
	 * should be called every frame to apply changes to the position and
	 * velocity
	 * 
	 * @param fr
	 *            information about how long a frame last
	 */
	public void update(final FrameRate fr) {
		acceleration.constrainLength(0, maximumAcceleration);
		velocity.add(fr.vectorPerFrame(acceleration));
		velocity.constrainLength(0, maximumVelocity);
		position.add(fr.vectorPerFrame(velocity));
	}

	/**
	 * adds the force to what is to move
	 * 
	 * @param force
	 *            direction and strength (length) of the pulling force
	 */
	public void applyForce(final Vector3d force) {
		acceleration.add(force.clone().scale(1d / mass));
	}

	/**
	 * sets acceleration to zero
	 */
	public void resetForces() {
		acceleration.setToZero();
	}

	/**
	 * adds the acceleration not considering the mass of this
	 * 
	 * @param acceleration
	 *            a vector to add to this.acceleration
	 */
	public void applyAcceleration(final Vector3d acceleration) {
		this.acceleration.add(acceleration);
	}

	/**
	 * changes the mass of this considering it's acceleration
	 * 
	 * @param nm
	 *            the new mass
	 */
	public void changeMass(final double nm) {
		acceleration.scale(nm / mass);
		velocity.scale(nm / mass);
		mass = nm;
	}
}