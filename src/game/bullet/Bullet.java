package game.bullet;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import game.Game;
import game.audio.SoundManager;
import game.player.Player;
/**
 * A class to store all of a bullet's information, and do calculations on that information.
 * This class should not be instantiated on its own.
 * If you want to spawn in a Bullet, use the BulletSpawner class and its various methods.
 */
public class Bullet {
	int velMode; //0 = angle / speed, 1 = xspeed/yspeed
	
	double xpos;
	double ypos;
	double speed;
	double angle;
	double xvel;
	double yvel;
	
	BulletManager parentMGR;
	Player relevantPlayer;
	BulletSpawner transformSpawner;
	
	BulletTransformation transformQueue;
	int transformIndex;
	int transformTimer;
	int numJumps;
	double storedAngle;
	
	
	int type;
	int color;
	/**
	 * Amount of frames until this Bullet becomes grazeable.
	 */
	public int grazed;
	int timer;
	/**
	 * Refers to the Bullet's rendering size. Represents diameter, not radius.
	 */
	double size;
	double hitboxRadius;
	int framesTillDespawnOffscreen = 0;
	/**
	 * Rotates the Bullet's graphic during rendering.
	 * Modes
	 * 0: Rotates to match direction it's traveling
	 * 1: No rotation
	 * 2: Gradual clockwise rotation over time
	 * 3: Gradual counterclockwise rotation over time
	 */
	byte renderRotationMode;
	double renderRotationAngle;
	AffineTransform renderTransform;
	boolean disabled;
	
	public static final int PELLET = 0;
	public static final int ARROWHEAD = 1;
	public static final int OUTLINE = 2;
	public static final int BALL = 3;
	public static final int RICE = 4;
	public static final int KUNAI = 5;
	public static final int SHARD = 6;
	public static final int AMULET = 7;
	public static final int BULLET = 8;
	public static final int BACTERIA = 9;
	public static final int STAR_CW = 10;
	public static final int STAR_CCW = 11;
	public static final int DROPLET = 12;
	public static final int POPCORN_CW = 13;
	public static final int POPCORN_CCW = 14;
	public static final int MENTOS = 15;
	public static final int STAR_BIG_CW = 16;
	public static final int STAR_BIG_CCW = 17;
	public static final int JELLYBEAN = 18;
	
	public static final int COLOR8_GREY = 0;
	public static final int COLOR8_RED = 1;
	public static final int COLOR8_PINK = 2;
	public static final int COLOR8_INDIGO = 3;
	public static final int COLOR8_LIGHT_BLUE = 4;
	public static final int COLOR8_GREEN = 5;
	public static final int COLOR8_YELLOW = 6;
	public static final int COLOR8_ORANGE = 7;

	public static final int COLOR16_DARK_GREY = 0;
	public static final int COLOR16_DARK_RED = 1;
	public static final int COLOR16_BRIGHT_RED = 2;
	public static final int COLOR16_PURPLE = 3;
	public static final int COLOR16_PINK = 4;
	public static final int COLOR16_INDIGO = 5;
	public static final int COLOR16_BLUE = 6;
	public static final int COLOR16_CYAN = 7;
	public static final int COLOR16_LIGHT_BLUE = 8;
	public static final int COLOR16_GREEN = 9;
	public static final int COLOR16_LIGHT_GREEN = 10;
	public static final int COLOR16_LIME_GREEN = 11;
	public static final int COLOR16_YELLOW = 12;
	public static final int COLOR16_BRIGHT_YELLOW = 13;
	public static final int COLOR16_ORANGE = 14;
	public static final int COLOR16_LIGHT_GREY = 15;

	/**
	 * This constructor should only ever be called by a BulletManager, during initialization of the manager.
	 * 
	 * @param mgr
	 * @param p
	 */
	public Bullet(BulletManager mgr, Player p) {
		xpos = -1;
		ypos = -1;
		speed = -1;
		angle = -1;
		xvel = -1;
		yvel = -1;
		type = -1;
		size = 1;
		hitboxRadius = 1;
		timer = 0;
		grazed = 0;
		disabled = true;
		parentMGR = mgr;
		relevantPlayer = p;
		renderTransform = new AffineTransform();
		velMode = 0;
		transformQueue = null;
		transformIndex = 0;
		transformTimer = 0;
		numJumps = 0;
		transformSpawner = null;
	}
	/**
	 * This method should only ever be invoked by the BulletManager's addBullet method,
	 * which in turn should only ever be invoked by a BulletSpawner's private shootOneWay, shootRingLayer, or shootPR_Bullet methods.
	 * The proper way to respawn a Bullet is via a BulletSpawner's activate method.
	 * 
	 * @param newXpos
	 * @param newYpos
	 * @param newSpeed
	 * @param newAngle
	 * @param newType
	 * @param newColor
	 * @param offscreenProtectionFramesNum
	 * @param newTransformQueue
	 */
	public void respawnBullet(double newXpos, double newYpos,
			double newSpeed, double newAngle,
			int newType, int newColor,
			int offscreenProtectionFramesNum,
			BulletTransformation newTransformQueue, int startingTransformIndex) {
		xpos = newXpos;
		ypos = newYpos;
		speed = newSpeed;
		angle = newAngle;
		type = newType;	
		color = newColor;
		storedAngle = BulletTransformation.ANGLE_NULL;
		size = BulletDefs.BULLET_RENDER_SIZE[type];
		hitboxRadius = BulletDefs.BULLET_HITBOX_SIZE[type];
		renderRotationMode = BulletDefs.BULLET_ROTATION_MODE[type];
		renderRotationAngle = Math.PI/2;
		grazed = 0;
		timer = 0;
		transformQueue = null;
		if(newTransformQueue != null)transformQueue = newTransformQueue;
		transformIndex = startingTransformIndex;
		transformTimer = 0;
		numJumps = 0;
		framesTillDespawnOffscreen = offscreenProtectionFramesNum;
		disabled = false;
		renderTransform.setToIdentity();
		if(velMode == 1) {
			xvel = Math.cos(angle) * speed;
			yvel = Math.sin(angle) * speed;
		}
		transformSpawner = null;
	}

	/**
	 * All code related to rendering a Bullet.
	 * This method should only ever be invoked via a BulletManager's drawBullets method.
	 * 
	 * @param g
	 * @param b
	 * @param m
	 */
	public void draw(Graphics2D g, BufferedImage b, Game m) {
		
		switch(renderRotationMode) {
		case 0:
			renderRotationAngle = angle + Math.PI/2;
			break;
		case 2:
			if(parentMGR.game.state == Game.STATE.PLAY) renderRotationAngle += Math.PI/30;
			break;
		case 3:
			if(parentMGR.game.state == Game.STATE.PLAY)renderRotationAngle -= Math.PI/30;
			break;
		default:
			break;
		}
		
		renderTransform.setToIdentity();
		renderTransform.translate(Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2), Game.PLAYFIELDYOFFSET);
		renderTransform.translate(xpos - size/2, ypos - size/2);
		if(renderRotationMode != (byte)(1)) {
			renderTransform.rotate(renderRotationAngle, size/2, size/2);
		}
		g.drawImage(b, renderTransform, m);
	}
	
	/**
	 * An accessor method, used by BulletManager
	 * @return this Bullet's disabled field
	 */
	public boolean isDisabled() {
		return disabled;
	}
	public void deleteBullet() {
		parentMGR.SoundMGR.playFromArray(SoundManager.BulletDelete);
		this.disable();
	}
	
	/**
	 * A mutator method, used by BulletManager.
	 * Sets this Bullet's disabled field to true.
	 */
	public void disable() {
		transformSpawner = null;
		disabled = true;
	}
	/**
	 * An accessor method, used by BulletManager.
	 * @return this Bullet's type field
	 */
	public int getType() {
		return type;
	}
	/**
	 * An accessor method, used by BulletManager.
	 * @return this Bullet's color field
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * This method should only ever be invoked by the BulletManager's updateBullets method.
	 * Does all calculations relating to updating a Bullet's new position.
	 * 
	 * @return whether this Bullet is both offscreen and is vulnerable to despawning offscreen
	 */
	public boolean update() {
		if(this.transformQueue != null) doBulletTransformations();
		switch(velMode) {
		case 0:
			step(speed);
			break;
		case 1:
			xpos += xvel;
			ypos += yvel;
			if(renderRotationMode == 0) angle = getAngleFromVelocity();
			break;
		default:
			break;
		}
		timer++;
		framesTillDespawnOffscreen--;
		return ((framesTillDespawnOffscreen <= 1) && isOffscreen());
	}
	
	/**
	 * A utility function that moves the Bullet forward in its current angle.
	 * Has undefined behavior if velMode is not 0.
	 * @param dist the distance to move this Bullet by
	 */
	public void step(double dist) {
		xpos += Math.cos(angle) * dist;
		ypos += Math.sin(angle) * dist;
		
	}
	/**
	 * Checks if this Bullet has collided with a given circle.
	 * 
	 * @param xCompare the X-value of the circle's center
	 * @param yCompare the Y-value of the circle's center
	 * @param radCompare the radius of the circle
	 * @return whether the given circle and the circle representing this Bullet's position and hitbox intersect
	 */
	public boolean checkCollision(double xCompare, double yCompare, double radCompare) {
		
		/*if(((Math.pow(xCompare - xpos, 2) + Math.pow(yCompare - ypos, 2) < Math.pow(radCompare + hitboxSize, 2)))) {
			this.color = BulletColor.LIGHT_GREY;
		}*/
		return ((Math.pow(xCompare - xpos, 2) + Math.pow(yCompare - ypos, 2) < Math.pow(radCompare + hitboxRadius, 2)));
	}
	public void collideWithPlayer() {
		relevantPlayer.collideWithBullet();
		eraseSelf();
	}
	public void grazedByPlayer() {
		parentMGR.SoundMGR.playFromArray(SoundManager.Graze);
		grazed = -1;
	}
	public void eraseSelf() {
		disabled = true;
	}
	
	/**
	 * A utility method, used during rendering of Bullets with velMode 1 and renderRotationMode 0.
	 * @return the angle this Bullet is traveling in
	 */
	double getAngleFromVelocity() {
		if (xvel == 0 && yvel == 0) return angle;
		else return Math.atan2(yvel, xvel);
	}
	
	/**
	 * A utility method used for determining whether this Bullet is entirely offscreen.
	 * @return true if this Bullet is offscreen, and false otherwise
	 */
	private boolean isOffscreen() {
		if (xpos > size + (Game.PLAYFIELDWIDTH / 2)) return true;
		if (xpos < -(Game.PLAYFIELDWIDTH / 2) - size) return true;
		if (ypos < - size) return true;
		if (ypos > size + Game.PLAYFIELDHEIGHT) return true;
		return false;
	}
	/**
	 * A function that does all calculations relating to a Bullet's transformations.
	 * If one wishes to add a new type of transformation, then this method should be edited.
	 * Refer to BulletTransformation's documentation for an explanation of what each transformation type does.
	 */
	private void doBulletTransformations() {
		if(this.transformQueue.getTransformAtIndex(transformIndex) == BulletTransformation.TRANSFORM_NO_TRANSFORM) return;

		transformTimer++;
		switch(this.transformQueue.getTransformAtIndex(transformIndex)) {
		case BulletTransformation.TRANSFORM_WAIT:
			if(transformTimer > transformQueue.getIntArg1AtIndex(transformIndex)) nextTransform();
			break;
		case BulletTransformation.TRANSFORM_GOTO:
			if(transformQueue.getIntArg2AtIndex(transformIndex) == -1
			|| this.numJumps < transformQueue.getIntArg2AtIndex(transformIndex)) {
				numJumps++;
				gotoTransform(transformQueue.getIntArg1AtIndex(transformIndex));
				break;
			}else {
				nextTransform();
				break;
			}
		case BulletTransformation.TRANSFORM_ACCEL_ANGVEL:
			if(transformTimer > transformQueue.getIntArg1AtIndex(transformIndex)) {
				nextTransform();
				this.storedAngle = BulletTransformation.ANGLE_NULL;
				break;
			}
			if(this.velMode != 0) changeVelMode(0);
			this.speed += transformQueue.getFloatArg1AtIndex(transformIndex);
			if(transformQueue.getFloatArg2AtIndex(transformIndex) == BulletTransformation.RAND_ANGLE) {
				if(this.storedAngle == BulletTransformation.ANGLE_NULL) this.storedAngle = parentMGR.game.randRad();
				this.angle += storedAngle;
			}else {
				this.angle += transformQueue.getFloatArg2AtIndex(transformIndex);
			}
			break;
		case BulletTransformation.TRANSFORM_ACCEL_DIR:
			if(transformTimer > transformQueue.getIntArg1AtIndex(transformIndex)) {
				nextTransform();
				break;
			}
			if(this.velMode != 1) changeVelMode(1);
			this.xvel += transformQueue.getFloatArg1AtIndex(transformIndex);
			this.yvel += transformQueue.getFloatArg2AtIndex(transformIndex);
			break;
		case BulletTransformation.TRANSFORM_OFFSCREEN:
			this.framesTillDespawnOffscreen = transformQueue.getIntArg1AtIndex(transformIndex);
			nextTransform();
			break;
		case BulletTransformation.TRANSFORM_DELETE:
			this.disable();
			break;	
		case BulletTransformation.TRANSFORM_SOUND:
			parentMGR.SoundMGR.playFromArray(transformQueue.getIntArg1AtIndex(transformIndex));
			nextTransform();
			break;
		case BulletTransformation.TRANSFORM_SHOOT_PREPARE:
			transformSpawner = new BulletSpawner(parentMGR, relevantPlayer, parentMGR.game);
			transformSpawner.setTransformStartingIndex(transformQueue.getIntArg1AtIndex(transformIndex));
			transformSpawner.setMode(transformQueue.getIntArg2AtIndex(transformIndex));
			transformSpawner.setBulletCounts(transformQueue.getIntArg3AtIndex(transformIndex), transformQueue.getIntArg4AtIndex(transformIndex));
			double ang1 = getRealSpawnerAngle(transformQueue.getFloatArg1AtIndex(transformIndex));
			double ang2 = getRealSpawnerAngle(transformQueue.getFloatArg2AtIndex(transformIndex));
			transformSpawner.setAngles(ang1, ang2);
			transformSpawner.setSpeeds(transformQueue.getFloatArg3AtIndex(transformIndex), transformQueue.getFloatArg4AtIndex(transformIndex));
			transformSpawner.setTransformList(transformQueue);
			nextTransform();
			break;
		case BulletTransformation.TRANSFORM_SHOOT_ACTIVATE:
			if(transformSpawner == null) break;
			transformSpawner.setTypeAndColor(transformQueue.getIntArg1AtIndex(transformIndex), transformQueue.getIntArg2AtIndex(transformIndex));
			transformSpawner.setSpawnerPos(xpos, ypos);
			transformSpawner.activate();
			if(transformQueue.getIntArg3AtIndex(transformIndex) != 0) {
				this.disable();
				break;
			} else {
				nextTransform();
				break;
			}
		}
	}
	
	private double getRealSpawnerAngle(double rawAngle) {
		if(rawAngle == BulletTransformation.RAND_ANGLE) return parentMGR.game.randRad();
		else if(rawAngle == BulletTransformation.ANGLE_SELF) return (velMode == 0 ? this.angle : this.getAngleFromVelocity());
		else return rawAngle;
	}
	
	/**
	 * A utility method for changing between velModes without causing unwanted side-effects.
	 * @param newMode
	 */
	private void changeVelMode(int newMode) {
		if(newMode == 0 && velMode == 1) {
			angle = Math.atan2(yvel, xvel);
			speed = Math.hypot(xvel, yvel);
			velMode = 0;
		}else if(newMode == 1 && velMode == 0) {
			xvel = Math.cos(angle) * speed;
			yvel = Math.sin(angle) * speed;
			velMode = 1;
		}
	}
	/**
	 * A utility method that is called when a BulletTransformation has finished executing.
	 */
	private void nextTransform() {
		this.transformIndex++;
		this.transformTimer = 0;
		doBulletTransformations();
	}
	private void gotoTransform(int index) {
		this.transformIndex = index;
		this.transformTimer = 0;
	}
	


}
