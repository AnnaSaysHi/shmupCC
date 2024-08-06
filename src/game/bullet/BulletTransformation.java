package game.bullet;

import java.util.ArrayList;

/**
 * This class is a helper class, blah blah blah. //todo finish documenting this
 */
public class BulletTransformation implements Cloneable {

	public static final int TRANSFORM_NO_TRANSFORM = 0;
	public static final int TRANSFORM_WAIT = 1;
	public static final int TRANSFORM_GOTO = 2;
	public static final int TRANSFORM_ACCEL_ANGVEL = 3;
	public static final int TRANSFORM_ACCEL_DIR = 4;
	public static final int TRANSFORM_OFFSCREEN = 5;
	ArrayList<Integer> transformIDs;
	ArrayList<Integer> transformDurations;
	ArrayList<Integer> intArgs1;
	ArrayList<Integer> intArgs2;
	ArrayList<Integer> intArgs3;
	ArrayList<Double> floatArgs1;
	ArrayList<Double> floatArgs2;
	ArrayList<Double> floatArgs3;
	
	
	/* ==============================
	 * =					   		=
	 * =	TRANSFORMATION MACROS	=
	 * =							=
	 * ==============================
	 */
	/**
	 * Queues a no-transform.
	 * If a bullet executes a no-transform, it will stop executing further transformations, even if it has not yet reached the end of the queue.
	 */
	public void queueNoTransform() {
		this.queueTransformation(TRANSFORM_NO_TRANSFORM, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts a no-transform at the specified index.
	 * If a bullet executes a no-transform, it will stop executing further transformations, even if it has not yet reached the end of the queue.
	 * 
	 * @param index index to insert the no-transform
	 */
	public void insertNoTransform(int index) {
		this.insertTransformation(index, TRANSFORM_NO_TRANSFORM, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Queues a wait transform.
	 * The bullet will wait for the specified amount of frames before executing further transformations.
	 * 
	 * @param duration the duration, in frames, to wait before executing the next transformation
	 */
	public void queueWaitTransform(int duration) {
		this.queueTransformation(TRANSFORM_WAIT, duration, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts a wait transform at the specified index.
	 * The bullet will wait for the specified amount of frames before executing further transformations.
	 * 
	 * @param index index to insert the wait transform
	 * @param duration the duration, in frames, to wait before executing the next transformation
	 */
	public void insertWaitTransform(int index, int duration) {
		this.insertTransformation(index, TRANSFORM_WAIT, duration, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Queues a control flow transformation that changes the bullet's transformation index to destinationIndex, then immediately executes the transformation at that index.
	 * If the bullet has executed a GOTO transformation numLoops or more times, this transformation will not execute. The number of times a bullet has executed a GOTO is shared across different GOTO transforms in that same queue.
	 * If numLoops is set to -1, this transformation will always execute. If numLoops is set to 0, this transformation will never execute.
	 * 
	 * @param destinationIndex the index of the next transform to be executed
	 * @param numLoops number of jumps before this transform will stop executing
	 */
	public void queueGotoTransform(int destinationIndex, int numLoops) {
		this.queueTransformation(TRANSFORM_GOTO, 0, destinationIndex, numLoops, 0, 0, 0, 0);
	}
	/**
	 * Queues a control flow transformation that changes the bullet's transformation index to destinationIndex, then immediately executes the transformation at that index.
	 * If the bullet has executed a GOTO transformation numLoops or more times, this transformation will not execute. The number of times a bullet has executed a GOTO is shared across different GOTO transforms in that same queue.
	 * If numLoops is set to -1, this transformation will always execute. If numLoops is set to 0, this transformation will never execute.
	 * 
	 * @param index index to insert the GOTO transform
	 * @param destinationIndex the index of the next transform to be executed
	 * @param numLoops number of jumps before this transform will stop executing
	 */
	public void insertGotoTransform(int index, int destinationIndex, int numLoops) {
		this.insertTransformation(index, TRANSFORM_GOTO, 0, destinationIndex, numLoops, 0, 0, 0, 0);
	}
	
	/**
	 * Queues a transformation that simultaneously gives the bullet angular velocity, as well as accelerates the bullet in the angle it's traveling.
	 * The bullet will not execute any further transformations until this transformation has finished executing.
	 * 
	 * @param duration the duration, in frames, of this transformation
	 * @param accel the amount of speed this bullet should gain per frame
	 * @param angleVel the change in angle applied to this bullet every frame
	 */
	public void queueAccelAngleVelTransform(int duration, double accel, double angleVel) {
		this.queueTransformation(TRANSFORM_ACCEL_ANGVEL, duration, 0, 0, 0, accel, angleVel, 0);
	}
	/**
	 * Inserts a transformation, at the specified index, that simultaneously gives the bullet angular velocity, as well as accelerates the bullet in the angle it's traveling.
	 * The bullet will not execute any further transformations until this transformation has finished executing.
	 * 
	 * @param index index to insert this transform
	 * @param duration the duration, in frames, of this transformation
	 * @param accel the amount of speed this bullet should gain per frame
	 * @param angleVel the change in angle applied to this bullet every frame
	 */
	public void insertAccelAngleVelTransform(int index, int duration, double accel, double angleVel) {
		this.insertTransformation(index, TRANSFORM_ACCEL_ANGVEL, duration, 0, 0, 0, accel, angleVel, 0);
	}
	
	/**
	 * Queues a transformation that accelerates the bullet in the given angle.
	 * The bullet will not execute any further transformations until this transformation has finished executing.
	 * 
	 * @param duration the duration, in frames, of this transformation
	 * @param accel the amount of speed applied to the bullet per frame
	 * @param angle the direction the speed is applied in
	 */
	public void queueAccelDirTransform(int duration, double accel, double angle) {
		double xaccel = Math.cos(angle) * accel;
		double yaccel = Math.sin(angle) * accel;
		this.queueTransformation(TRANSFORM_ACCEL_DIR, duration, 0, 0, 0, xaccel, yaccel, 0);
	}
	/**
	 * Inserts a transformation, at the specified index, that accelerates the bullet in the given angle.
	 * The bullet will not execute any further transformations until this transformation has finished executing.
	 * 
	 * @param index index to insert this transformation
	 * @param duration the duration, in frames, of this transformation
	 * @param accel the amount of speed applied to the bullet per frame
	 * @param angle the direction the speed is applied in
	 */
	public void insertAccelDirTransform(int index, int duration, double accel, double angle) {
		double xaccel = Math.cos(angle) * accel;
		double yaccel = Math.sin(angle) * accel;
		this.insertTransformation(index, TRANSFORM_ACCEL_DIR, duration, 0, 0, 0, xaccel, yaccel, 0);
	}
	
	/**
	 * Queues a transformation that allows the bullet to persist offscreen for a certain number of frames.
	 * The next transformation in the queue will immediately execute, regardless of the duration of persistence.
	 * 
	 * @param time amount of time, in frames, the bullet should persist offscreen
	 */
	public void queueOffscreenTransform(int time) {
		this.queueTransformation(TRANSFORM_OFFSCREEN, 0, time, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts a transformation, at the specified index, that allows the bullet to persist offscreen for a certain number of frames.
	 * The next transformation in the queue will immediately execute, regardless of the duration of persistence.
	 * 
	 * @param index index to insert this transform
	 * @param time amount of time, in frames, the bullet should persist offscreen
	 */
	public void insertOffscreenTransform(int index, int time) {
		this.insertTransformation(index, TRANSFORM_OFFSCREEN, 0, time, 0, 0, 0, 0, 0);
	}
	
	
	
	
	
	
	
	
	
	
	/* =========================
	 * =					   =
	 * =	MUTATOR METHODS	   =
	 * =					   =
	 * =========================
	 */
	/**
	 * Appends a new transformation to the end of the queue, with the arguments given.
	 * Not all transformation types will use all arguments.
	 * 
	 * @param transformType
	 * @param transformDur
	 * @param int1
	 * @param int2
	 * @param int3
	 * @param double1
	 * @param double2
	 * @param double3
	 */
	public void queueTransformation(int transformType, int transformDur, int int1, int int2, int int3, double double1, double double2, double double3) {
		transformIDs.add(transformType);
		transformDurations.add(transformDur);
		intArgs1.add(int1);
		intArgs2.add(int2);
		intArgs3.add(int3);
		floatArgs1.add(double1);
		floatArgs2.add(double2);
		floatArgs3.add(double3);
	}
	/**
	 * Edits the transformation at the index specified, to have the new values specified.
	 * Returns immediately without throwing an exception if the index is outside the boundaries of the transformation queue.
	 * 
	 * @param index index of transformation to edit
	 * @param transformType
	 * @param transformDur
	 * @param int1
	 * @param int2
	 * @param int3
	 * @param double1
	 * @param double2
	 * @param double3
	 */
	public void editTransformationAtIndex(int index, int transformType, int transformDur, int int1, int int2, int int3, double double1, double double2, double double3) {
		if(index >= transformIDs.size()) return;
		transformIDs.set(index, transformType);
		transformDurations.set(index, transformDur);
		intArgs1.set(index, int1);
		intArgs2.set(index, int2);
		intArgs3.set(index, int3);
		floatArgs1.set(index, double1);
		floatArgs2.set(index, double2);
		floatArgs3.set(index, double3);
	}
	/**
	 * Inserts a new transformation in the middle of the queue, at the index specified, with the arguments specified.
	 * Shifts the transformation at this index (if any) and any subsequent transformations one later in the queue.
	 * Returns immediately without throwing an exception if the index is greater than the length of the transformation queue.
	 * 
	 * @param index index where the new transformation will go
	 * @param transformType
	 * @param transformDur
	 * @param int1
	 * @param int2
	 * @param int3
	 * @param double1
	 * @param double2
	 * @param double3
	 */
	public void insertTransformation(int index, int transformType, int transformDur, int int1, int int2, int int3, double double1, double double2, double double3) {
		if(index > transformIDs.size()) return;
		transformIDs.add(index, transformType);
		transformDurations.add(index, transformDur);
		intArgs1.add(index, int1);
		intArgs2.add(index, int2);
		intArgs3.add(index, int3);
		floatArgs1.add(index, double1);
		floatArgs2.add(index, double2);
		floatArgs3.add(index, double3);
	}
	/**
	 * Removes the transformation at the index given from the queue.
	 * Returns immediately without throwing an exception if the index is outside the boundaries of the transformation queue.
	 * 
	 * @param index index of the transformation to remove
	 */
	public void removeTransformationAtIndex(int index) {
		if(index >= transformIDs.size()) return;
		transformIDs.remove(index);
		transformDurations.remove(index);
		intArgs1.remove(index);
		intArgs2.remove(index);
		intArgs3.remove(index);
		floatArgs1.remove(index);
		floatArgs2.remove(index);
		floatArgs3.remove(index);
	}
	
	
	
	/* ==========================
	 * =						=
	 * =	ACCESSOR METHODS	=
	 * =						=
	 * ==========================
	 */
	public int getTransformAtIndex(int index) {
		if(index >= transformIDs.size()) return TRANSFORM_NO_TRANSFORM;
		else return transformIDs.get(index);
	}
	public int getDurationAtIndex(int index) {
		if(index >= transformDurations.size()) return -1;
		else return transformDurations.get(index);
	}
	public int getIntArg1AtIndex(int index) {
		if(index >= intArgs1.size()) return -1;
		else return intArgs1.get(index);
	}
	public int getIntArg2AtIndex(int index) {
		if(index >= intArgs2.size()) return -1;
		else return intArgs2.get(index);
	}
	public int getIntArg3AtIndex(int index) {
		if(index >= intArgs3.size()) return -1;
		else return intArgs3.get(index);
	}
	public double getFloatArg1AtIndex(int index) {
		if(index >= floatArgs1.size()) return -1;
		else return floatArgs1.get(index);
	}
	public double getFloatArg2AtIndex(int index) {
		if(index >= floatArgs2.size()) return -1;
		else return floatArgs2.get(index);
	}
	public double getFloatArg3AtIndex(int index) {
		if(index >= floatArgs3.size()) return -1;
		else return floatArgs3.get(index);
	}
	
	/* ==============================
	 * =							=
	 * =	CONSTRUCTOR METHODS		=
	 * =							=
	 * ==============================
	 */
	public BulletTransformation() {
		transformIDs = new ArrayList<Integer>();
		transformDurations = new ArrayList<Integer>();
		intArgs1 = new ArrayList<Integer>();
		intArgs2 = new ArrayList<Integer>();
		intArgs3 = new ArrayList<Integer>();
		floatArgs1 = new ArrayList<Double>();
		floatArgs2 = new ArrayList<Double>();
		floatArgs3 = new ArrayList<Double>();
	}
	protected BulletTransformation(ArrayList<Integer> transforms, ArrayList<Integer> durs,
			ArrayList<Integer> iarg1, ArrayList<Integer> iarg2, ArrayList<Integer> iarg3,
			ArrayList<Double> farg1, ArrayList<Double> farg2, ArrayList<Double> farg3) {
		transformIDs = transforms;
		transformDurations = durs;
		intArgs1 = iarg1;
		intArgs2 = iarg2;
		intArgs3 = iarg3;
		floatArgs1 = farg1;
		floatArgs2 = farg2;
		floatArgs3 = farg3;
	}
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * Creates and returns a copy of this object. This copy is not a "shallow copy"; i.e. changes made
	 *  to this copy do not affect the original object in any way.
	 */
	public BulletTransformation clone() {
		ArrayList<Integer> transforms = (ArrayList<Integer>) transformIDs.clone();
		ArrayList<Integer> durs = (ArrayList<Integer>) transformDurations.clone();
		ArrayList<Integer> iarg1 = (ArrayList<Integer>) intArgs1.clone();
		ArrayList<Integer> iarg2 = (ArrayList<Integer>) intArgs1.clone();
		ArrayList<Integer> iarg3 = (ArrayList<Integer>) intArgs1.clone();
		ArrayList<Double> farg1 = (ArrayList<Double>) floatArgs1.clone();
		ArrayList<Double> farg2 = (ArrayList<Double>) floatArgs2.clone();
		ArrayList<Double> farg3 = (ArrayList<Double>) floatArgs3.clone();
		
		return new BulletTransformation(transforms, durs, iarg1, iarg2, iarg3, farg1, farg2, farg3);
	}
	
	

}
