package game;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {
	double xpos;
	double ypos;
	double speed;
	int type;
	int grazed;
	double angle;
	int transform1type;
	double transform1arg1;
	double transform1arg2;
	double size;
	double hitboxSize;
	int framesTillDespawnOffscreen = 0;
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
	
	public void respawnBullet(double newXpos, double newYpos, double newSpeed, double newAngle, int newType, int offscreenProtectionFramesNum) {
		xpos = newXpos;
		ypos = newYpos;
		speed = newSpeed;
		angle = newAngle;
		type = newType;	
		size = 16;
		hitboxSize = 5;
		grazed = 0;
		framesTillDespawnOffscreen = offscreenProtectionFramesNum;
		disabled = false;
		renderTransform.setToIdentity();
	}
	
	public void draw(Graphics2D g, BufferedImage b, Game m) {
		renderTransform.setToIdentity();
		renderTransform.translate(xpos, ypos);
		renderTransform.rotate(angle + Math.PI/2, size/2, size/2);
		g.drawImage(b, renderTransform, m);
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	public void disable() {
		disabled = true;
	}
	
	public boolean update() {
		xpos += Math.cos(angle) * speed;
		ypos += Math.sin(angle) * speed;
		framesTillDespawnOffscreen--;
		return ((framesTillDespawnOffscreen <= 0) && isOffscreen());
	}
	
	private boolean isOffscreen() {
		if (xpos > size + 960) return true;
		if (xpos < -size) return true;
		if (ypos < -size) return true;
		if (ypos > size + 720) return true;
		return false;
	}
	


}
