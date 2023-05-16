package game;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class PlayerShot {

	double xpos;
	double ypos;
	double speed;
	double angle;
	double size; // Diameter, not radius
	double hitboxSize; // Radius, not diameter
	
	int damage;
	
	
	int graphic;
	byte renderRotationMode; // 0 = rendered in the direction it's traveling, 1 = no rotation, 2 = CW rotation, 3 = CCW rotation
	double renderRotationAngle;
	AffineTransform renderTransform;
	boolean disabled;
	
	
	public PlayerShot() {
		xpos = -1;
		ypos = -1;
		speed = -1;
		angle = -1;
		graphic = -1;
		size = 1;
		hitboxSize = 1;
		damage = -1;
		disabled = true;
	}
	
	public void respawnShot(double x, double y, double speed, double angle, int graphic, double renderSize, double hitboxSize, int damage) {
		this.xpos = x;
		this.ypos = y;
		this.speed = speed;
		this.angle = angle;
		
		this.graphic = graphic;
		this.size = renderSize;
		this.hitboxSize = hitboxSize;
		this.damage = damage;
		disabled = false;
	}
	
	public void update() {
		
	}

}
