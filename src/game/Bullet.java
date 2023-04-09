package game;

import java.awt.Graphics;
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
	boolean disabled = false;

	
	
	public Bullet(double spawnXpos, double spawnYpos, double spawnSpeed, double spawnAngle, int spawnType, int offscreenProtectionFramesNum) {
		xpos = spawnXpos;
		ypos = spawnYpos;
		speed = spawnSpeed;
		angle = spawnAngle;
		type = spawnType;	
		size = 16;
		hitboxSize = 5;
		grazed = 0;
		framesTillDespawnOffscreen = offscreenProtectionFramesNum;
	}
	
	public void draw(Graphics g, BufferedImage b, Game m) {
		g.drawImage(b, (int)(xpos), (int)(ypos), m);
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean a) {
		disabled = a;
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
