package game;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {
	double xpos;
	double ypos;
	double speed;
	int type;
	int color;
	int grazed;
	double angle;
	int transform1type; // These three variables are used for having bullets act in ways other than
	double transform1arg1; // moving in a straight line at a constant speed. Currently, they have not
	double transform1arg2; // been implemented yet.
	double size; // Diameter, not radius
	double hitboxSize; // Radius, not diameter
	int framesTillDespawnOffscreen = 0; // Amount of protection this bullet gets from immediately despawning due to being offscreen after it spawns
	byte renderRotationMode; // 0 = rendered in the direction it's traveling, 1 = no rotation, 2 = CW rotation, 3 = CCW rotation
	AffineTransform renderTransform;
	boolean disabled;

	
	
	public Bullet() {
		xpos = -1;
		ypos = -1;
		speed = -1;
		angle = -1;
		type = -1;
		size = 1;
		hitboxSize = 1;
		grazed = 0;
		disabled = true;
		renderTransform = new AffineTransform();
	}
	
	public void respawnBullet(double newXpos, double newYpos, double newSpeed, double newAngle, int newType, int newColor, int offscreenProtectionFramesNum) {
		xpos = newXpos;
		ypos = newYpos;
		speed = newSpeed;
		angle = newAngle;
		type = newType;	
		color = newColor;
		size = BulletType.BULLET_RENDER_SIZE[type];
		hitboxSize = BulletType.BULLET_HITBOX_SIZE[type];
		grazed = 0;
		framesTillDespawnOffscreen = offscreenProtectionFramesNum;
		disabled = false;
		renderTransform.setToIdentity();
	}
	
	public void draw(Graphics2D g, BufferedImage b, Game m) {
		renderTransform.setToIdentity();
		renderTransform.translate(xpos - size/2, ypos - size/2);
		renderTransform.rotate(angle + Math.PI/2, size/2, size/2);
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
		xpos += Math.cos(angle) * speed;
		ypos += Math.sin(angle) * speed;
		framesTillDespawnOffscreen--;
		return ((framesTillDespawnOffscreen <= 0) && isOffscreen());
	}
	
	public boolean checkCollision(double xCompare, double yCompare, double radCompare) {
		
		if(((Math.pow(xCompare - xpos, 2) + Math.pow(yCompare - ypos, 2) < Math.pow(radCompare + hitboxSize, 2)))) {
			this.color = BulletColor.LIGHT_GREY;
		}
		return ((Math.pow(xCompare - xpos, 2) + Math.pow(yCompare - ypos, 2) < Math.pow(radCompare + hitboxSize, 2)));
	}
	
	private boolean isOffscreen() {
		if (xpos > size + 960) return true;
		if (xpos < -size) return true;
		if (ypos < -size) return true;
		if (ypos > size + 720) return true;
		return false;
	}
	


}
