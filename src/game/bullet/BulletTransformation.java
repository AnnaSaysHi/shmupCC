package game.bullet;

import java.util.ArrayList;

/**
 * This class is a helper class, blah blah blah. //todo finish documenting this
 */
public class BulletTransformation implements Cloneable {

	public static final double ANGLE_SELF = -9999;
	public static final int TRANSFORM_NO_TRANSFORM = 0;
	public static final int TRANSFORM_WAIT = 1;
	public static final int TRANSFORM_GOTO = 2;
	public static final int TRANSFORM_ACCEL_ANGVEL = 3;
	public static final int TRANSFORM_ACCEL_DIR = 4;
	public static final int TRANSFORM_OFFSCREEN = 5;
	public static final int TRANSFORM_DELETE = 6;
	public static final int TRANSFORM_SOUND = 7;
	public static final int TRANSFORM_SHOOT_PREPARE = 8;
	public static final int TRANSFORM_SHOOT_ACTIVATE = 9;
	ArrayList<Integer> transformIDs;
	ArrayList<Integer> intArgs1;
	ArrayList<Integer> intArgs2;
	ArrayList<Integer> intArgs3;
	ArrayList<Integer> intArgs4;
	ArrayList<Double> floatArgs1;
	ArrayList<Double> floatArgs2;
	ArrayList<Double> floatArgs3;
	ArrayList<Double> floatArgs4;
	
	
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
		this.queueTransformation(TRANSFORM_NO_TRANSFORM, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts a no-transform at the specified index.
	 * If a bullet executes a no-transform, it will stop executing further transformations, even if it has not yet reached the end of the queue.
	 * 
	 * @param index index to insert the no-transform
	 */
	public void insertNoTransform(int index) {
		this.insertTransformation(index, TRANSFORM_NO_TRANSFORM, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Queues a wait transform.
	 * The bullet will wait for the specified amount of frames before executing further transformations.
	 * 
	 * @param duration the duration, in frames, to wait before executing the next transformation
	 */
	public void queueWaitTransform(int duration) {
		this.queueTransformation(TRANSFORM_WAIT, duration, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts a wait transform at the specified index.
	 * The bullet will wait for the specified amount of frames before executing further transformations.
	 * 
	 * @param index index to insert the wait transform
	 * @param duration the duration, in frames, to wait before executing the next transformation
	 */
	public void insertWaitTransform(int index, int duration) {
		this.insertTransformation(index, TRANSFORM_WAIT, duration, 0, 0, 0, 0, 0, 0, 0);
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
		this.queueTransformation(TRANSFORM_GOTO, destinationIndex, numLoops, 0, 0, 0, 0, 0, 0);
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
		this.insertTransformation(index, TRANSFORM_GOTO, destinationIndex, numLoops, 0, 0, 0, 0, 0, 0);
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
		this.queueTransformation(TRANSFORM_ACCEL_ANGVEL, duration, 0, 0, 0, accel, angleVel, 0, 0);
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
		this.insertTransformation(index, TRANSFORM_ACCEL_ANGVEL, duration, 0, 0, 0, accel, angleVel, 0, 0);
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
		this.queueTransformation(TRANSFORM_ACCEL_DIR, duration, 0, 0, 0, xaccel, yaccel, 0, 0);
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
		this.insertTransformation(index, TRANSFORM_ACCEL_DIR, duration, 0, 0, 0, xaccel, yaccel, 0, 0);
	}
	
	/**
	 * Queues a transformation that allows the bullet to persist offscreen for a certain number of frames.
	 * The next transformation in the queue will immediately execute, regardless of the duration of persistence.
	 * 
	 * @param time amount of time, in frames, the bullet should persist offscreen
	 */
	public void queueOffscreenTransform(int time) {
		this.queueTransformation(TRANSFORM_OFFSCREEN, time, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts a transformation, at the specified index, that allows the bullet to persist offscreen for a certain number of frames.
	 * The next transformation in the queue will immediately execute, regardless of the duration of persistence.
	 * 
	 * @param index index to insert this transform
	 * @param time amount of time, in frames, the bullet should persist offscreen
	 */
	public void insertOffscreenTransform(int index, int time) {
		this.insertTransformation(index, TRANSFORM_OFFSCREEN, time, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Queues a transformation that will cause the bullet to immediately delete itself.
	 */
	public void queueDeleteTransform() {
		this.queueTransformation(TRANSFORM_DELETE, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts a transformation, at the specified index, that will cause the bullet to immediately delete itself.
	 * @param index index to insert this transform
	 */
	public void insertDeleteTransform(int index) {
		this.insertTransformation(index, TRANSFORM_DELETE, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Queues a transformation that plays the given SFX. The next transformation will immediately execute.
	 * 
	 * @param sound the ID of the sound effect to be played
	 */
	public void queueSoundTransform(int sound) {
			this.queueTransformation(TRANSFORM_SOUND, sound, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts a transformation that plays the given SFX. The next transformation will immediately execute.
	 * @param index index to insert this transform
	 * @param sound the ID of the sound effect to be played
	 */
	public void insertSoundTransform(int index, int sound) {
		this.insertTransformation(index, TRANSFORM_SOUND, sound, 0, 0, 0, 0, 0, 0, 0);
	}
	/**
	 * Queues the first part of the transform to have bullets shoot bullets. Sets properties related to the bullet manager.
	 * Bullets spawned via this transformation will use the same transform queue as the bullet it spawned from, but they will start from
	 * the index provided by transformIndex. After executing this transformation, the next transform will execute immediately.
	 * 
	 * @param transformIndex Index of the spawned bullets to execute transforms from
	 * @param aim Aim mode of the spawner
	 * @param ways the spawner's ways parameter
	 * @param layers the spawner's layers parameter
	 * @param angle1 the spawner's angle1 parameter
	 * @param angle2 the spawner's angle2 parameter
	 * @param speed1 the spawner's speed1 parameter
	 * @param speed2 the spawner's speed2 parameter
	 */
	public void queueShootPrepareTransform(int transformIndex, int aim, int ways, int layers, double angle1, double angle2, double speed1, double speed2) {
		this.queueTransformation(TRANSFORM_SHOOT_PREPARE, transformIndex, aim, ways, layers, angle1, angle2, speed1, speed2);
	}
	/**
	 * Inserts the first part of the transform to have bullets shoot bullets. Sets properties related to the bullet manager.
	 * Bullets spawned via this transformation will use the same transform queue as the bullet it spawned from, but they will start from
	 * the index provided by transformIndex. After executing this transformation, the next transform will execute immediately.
	 * 
	 * @param index the index to insert this transform
	 * @param transformIndex Index of the spawned bullets to execute transforms from
	 * @param aim Aim mode of the spawner
	 * @param ways the spawner's ways parameter
	 * @param layers the spawner's layers parameter
	 * @param angle1 the spawner's angle1 parameter
	 * @param angle2 the spawner's angle2 parameter
	 * @param speed1 the spawner's speed1 parameter
	 * @param speed2 the spawner's speed2 parameter
	 */
	public void insertShootPrepareTransform(int index, int transformIndex, int aim, int ways, int layers, double angle1, double angle2, double speed1, double speed2) {
		this.insertTransformation(index, TRANSFORM_SHOOT_PREPARE, transformIndex, aim, ways, layers, angle1, angle2, speed1, speed2);
	}
	/**
	 * Queues the second part of the transform to have bullets shoot bullets. Upon executing this transformation, the bullet will immediately
	 * shoot bullets. The velocity and number/formation of the newly-shot bullets is set by the SHOOT_PREPARE transform.
	 * It is not necessary to have the SHOOT_PREPARE transform be directly before this transform, and in fact, this transformation can be activated
	 * multiple times in sequence without needing to re-execute the SHOOT_PREPARE transform.
	 * However, if this transformation is used without re-initializing the bullet's spawner via the SHOOT_PREPARE transform, undefined behavior will occur.
	 * @param type the spawner's type parameter
	 * @param color the spawner's color parameter
	 * @param delete if non-zero, deletes this bullet immediately after executing this transform
	 */
	public void queueShootActivateTransform(int type, int color, int delete) {
		this.queueTransformation(TRANSFORM_SHOOT_ACTIVATE, type, color, delete, 0, 0, 0, 0, 0);
	}
	/**
	 * Inserts the second part of the transform to have bullets shoot bullets. Upon executing this transformation, the bullet will immediately
	 * shoot bullets. The velocity and number/formation of the newly-shot bullets is set by the SHOOT_PREPARE transform.
	 * It is not necessary to have the SHOOT_PREPARE transform be directly before this transform, and in fact, this transformation can be activated
	 * multiple times in sequence without needing to re-execute the SHOOT_PREPARE transform.
	 * However, if this transformation is used without re-initializing the bullet's spawner via the SHOOT_PREPARE transform, undefined behavior will occur.
	 * @param index the index to insert this transform
	 * @param type the spawner's type parameter
	 * @param color the spawner's color parameter
	 * @param delete if non-zero, deletes this bullet immediately after executing this transform
	 */
	public void insertShootActivateTransform(int index, int type, int color, int delete) {
		this.insertTransformation(index, TRANSFORM_SHOOT_ACTIVATE, type, color, delete, 0, 0, 0, 0, 0);
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
	 * @param int1
	 * @param int2
	 * @param int3
	 * @param int4
	 * @param double1
	 * @param double2
	 * @param double3
	 * @param double4
	 */
	public void queueTransformation(int transformType, int int1, int int2, int int3, int int4, double double1, double double2, double double3, double double4) {
		transformIDs.add(transformType);
		intArgs1.add(int1);
		intArgs2.add(int2);
		intArgs3.add(int3);
		intArgs4.add(int4);
		floatArgs1.add(double1);
		floatArgs2.add(double2);
		floatArgs3.add(double3);
		floatArgs4.add(double4);
	}
	/**
	 * Edits the transformation at the index specified, to have the new values specified.
	 * Returns immediately without throwing an exception if the index is outside the boundaries of the transformation queue.
	 * 
	 * @param index index of transformation to edit
	 * @param transformType
	 * @param int1
	 * @param int2
	 * @param int3
	 * @param int4
	 * @param double1
	 * @param double2
	 * @param double3
	 * @param double4
	 */
	public void editTransformationAtIndex(int index, int transformType, int int1, int int2, int int3, int int4, double double1, double double2, double double3, double double4) {
		if(index >= transformIDs.size()) return;
		if(transformType == TRANSFORM_ACCEL_DIR) {
			double xaccel = Math.cos(double2) * double1;
			double2 = Math.sin(double2) * double1;
			double1 = xaccel;
		}
		
		transformIDs.set(index, transformType);
		intArgs1.set(index, int1);
		intArgs2.set(index, int2);
		intArgs3.set(index, int3);
		intArgs4.set(index, int4);
		floatArgs1.set(index, double1);
		floatArgs2.set(index, double2);
		floatArgs3.set(index, double3);
		floatArgs4.set(index, double4);
	}
	/**
	 * Inserts a new transformation in the middle of the queue, at the index specified, with the arguments specified.
	 * Shifts the transformation at this index (if any) and any subsequent transformations one later in the queue.
	 * Returns immediately without throwing an exception if the index is greater than the length of the transformation queue.
	 * 
	 * @param index index where the new transformation will go
	 * @param transformType
	 * @param int1
	 * @param int2
	 * @param int3
	 * @param int4
	 * @param double1
	 * @param double2
	 * @param double3
	 * @param double4
	 */
	public void insertTransformation(int index, int transformType, int int1, int int2, int int3, int int4, double double1, double double2, double double3, double double4) {
		if(index > transformIDs.size()) return;
		transformIDs.add(index, transformType);
		intArgs1.add(index, int1);
		intArgs2.add(index, int2);
		intArgs3.add(index, int3);
		intArgs4.add(index, int4);
		floatArgs1.add(index, double1);
		floatArgs2.add(index, double2);
		floatArgs3.add(index, double3);
		floatArgs4.add(index, double4);
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
		intArgs1.remove(index);
		intArgs2.remove(index);
		intArgs3.remove(index);
		intArgs4.remove(index);
		floatArgs1.remove(index);
		floatArgs2.remove(index);
		floatArgs3.remove(index);
		floatArgs4.remove(index);
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
	public int getIntArg4AtIndex(int index) {
		if(index >= intArgs4.size()) return -1;
		else return intArgs4.get(index);
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
	
	}public double getFloatArg4AtIndex(int index) {
		if(index >= floatArgs4.size()) return -1;
		else return floatArgs4.get(index);
	}
	
	/* ==============================
	 * =							=
	 * =	CONSTRUCTOR METHODS		=
	 * =							=
	 * ==============================
	 */
	public BulletTransformation() {
		transformIDs = new ArrayList<Integer>();
		intArgs1 = new ArrayList<Integer>();
		intArgs2 = new ArrayList<Integer>();
		intArgs3 = new ArrayList<Integer>();
		intArgs4 = new ArrayList<Integer>();
		floatArgs1 = new ArrayList<Double>();
		floatArgs2 = new ArrayList<Double>();
		floatArgs3 = new ArrayList<Double>();
		floatArgs4 = new ArrayList<Double>();
	}
	protected BulletTransformation(ArrayList<Integer> transforms, ArrayList<Integer> iarg1,
			ArrayList<Integer> iarg2, ArrayList<Integer> iarg3, ArrayList<Integer> iarg4,
			ArrayList<Double> farg1, ArrayList<Double> farg2, ArrayList<Double> farg3, ArrayList<Double> farg4) {
		transformIDs = transforms;
		intArgs1 = iarg1;
		intArgs2 = iarg2;
		intArgs3 = iarg3;
		intArgs4 = iarg4;
		floatArgs1 = farg1;
		floatArgs2 = farg2;
		floatArgs3 = farg3;
		floatArgs4 = farg4;
	}
	@SuppressWarnings("unchecked")
	@Override
	/**
	 * Creates and returns a copy of this object. This copy is not a "shallow copy"; i.e. changes made
	 *  to this copy do not affect the original object in any way.
	 */
	public BulletTransformation clone() {
		ArrayList<Integer> transforms = (ArrayList<Integer>) transformIDs.clone();
		ArrayList<Integer> iarg1 = (ArrayList<Integer>) intArgs1.clone();
		ArrayList<Integer> iarg2 = (ArrayList<Integer>) intArgs2.clone();
		ArrayList<Integer> iarg3 = (ArrayList<Integer>) intArgs3.clone();
		ArrayList<Integer> iarg4 = (ArrayList<Integer>) intArgs4.clone();
		ArrayList<Double> farg1 = (ArrayList<Double>) floatArgs1.clone();
		ArrayList<Double> farg2 = (ArrayList<Double>) floatArgs2.clone();
		ArrayList<Double> farg3 = (ArrayList<Double>) floatArgs3.clone();
		ArrayList<Double> farg4 = (ArrayList<Double>) floatArgs4.clone();
		
		return new BulletTransformation(transforms, iarg1, iarg2, iarg3, iarg4, farg1, farg2, farg3, farg4);
	}
	
	

}
