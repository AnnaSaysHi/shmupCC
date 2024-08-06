package game.bullet;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import game.Game;
import game.audio.SoundManager;
import game.player.Player;

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
	
	BulletTransformation transformQueue;
	int transformIndex;
	int transformTimer;
	
	
	int type;
	int color;
	public int grazed; //Amount of frames until this bullet becomes grazeable.
	int timer;
	double size; // Diameter, not radius
	double hitboxSize; // Radius, not diameter
	int framesTillDespawnOffscreen = 0; // Amount of protection this bullet gets from immediately despawning due to being offscreen after it spawns
	byte renderRotationMode; // 0 = rendered in the direction it's traveling, 1 = no rotation, 2 = CW rotation, 3 = CCW rotation
	double renderRotationAngle;
	AffineTransform renderTransform;
	boolean disabled;

	
	
	public Bullet(BulletManager mgr, Player p) {
		xpos = -1;
		ypos = -1;
		speed = -1;
		angle = -1;
		xvel = -1;
		yvel = -1;
		type = -1;
		size = 1;
		hitboxSize = 1;
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
	}
	
	public void respawnBullet(double newXpos, double newYpos,
			double newSpeed, double newAngle,
			int newType, int newColor,
			int offscreenProtectionFramesNum, BulletTransformation newTransformQueue) {
		xpos = newXpos;
		ypos = newYpos;
		speed = newSpeed;
		angle = newAngle;
		type = newType;	
		color = newColor;
		size = BulletType.BULLET_RENDER_SIZE[type];
		hitboxSize = BulletType.BULLET_HITBOX_SIZE[type];
		renderRotationMode = BulletType.BULLET_ROTATION_MODE[type];
		renderRotationAngle = Math.PI/2;
		grazed = 0;
		timer = 0;
		if(newTransformQueue != null)transformQueue = newTransformQueue;
		transformIndex = 0;
		transformTimer = 0;
		framesTillDespawnOffscreen = offscreenProtectionFramesNum;
		disabled = false;
		renderTransform.setToIdentity();
		if(velMode == 1) {
			xvel = Math.cos(angle) * speed;
			yvel = Math.sin(angle) * speed;
		}
	}
	
	public void draw(Graphics2D g, BufferedImage b, Game m) {
		
		switch(renderRotationMode) {
		case 0:
			renderRotationAngle = angle + Math.PI/2;
			break;
		case 2:
			renderRotationAngle += Math.PI/30;
			break;
		case 3:
			renderRotationAngle -= Math.PI/30;
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
	
	public boolean isDisabled() {
		return disabled;
	}
	public void disable() {
		disabled = true;
	}
	public int getType() {
		return type;
	}
	public int getColor() {
		return color;
	}
	
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
		return ((framesTillDespawnOffscreen <= timer) && isOffscreen());
	}
	public void step(double dist) {
		xpos += Math.cos(angle) * dist;
		ypos += Math.sin(angle) * dist;
		
	}
	
	public boolean checkCollision(double xCompare, double yCompare, double radCompare) {
		
		/*if(((Math.pow(xCompare - xpos, 2) + Math.pow(yCompare - ypos, 2) < Math.pow(radCompare + hitboxSize, 2)))) {
			this.color = BulletColor.LIGHT_GREY;
		}*/
		return ((Math.pow(xCompare - xpos, 2) + Math.pow(yCompare - ypos, 2) < Math.pow(radCompare + hitboxSize, 2)));
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
		//TODO: implement this
	}
	
	double getAngleFromVelocity() {
		if (xvel == 0 && yvel == 0) return angle;
		else return Math.atan2(yvel, xvel);
	}
	
	private boolean isOffscreen() {
		if (xpos > size + (Game.PLAYFIELDWIDTH / 2)) return true;
		if (xpos < -(Game.PLAYFIELDWIDTH / 2) - size) return true;
		if (ypos < - size) return true;
		if (ypos > size + Game.PLAYFIELDHEIGHT) return true;
		return false;
	}
	
	private void doBulletTransformations() {
		if(this.transformQueue.getTransformAtIndex(transformIndex) == BulletTransformation.TRANSFORM_NO_TRANSFORM) return;
		switch(this.transformQueue.getTransformAtIndex(transformIndex)) {
		case BulletTransformation.TRANSFORM_WAIT:
			if(transformTimer >= transformQueue.getDurationAtIndex(transformIndex)) nextTransform();
			break;
			
		}
	}
	
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
