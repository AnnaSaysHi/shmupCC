package game.player;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import game.Game;

public class PlayerShot {

	double xpos;
	double ypos;
	double speed;
	double angle;
	double size;
	double hitboxSize; // Radius, not diameter
	
	int damage;
	
	
	int graphic;
	byte renderRotationMode = 1; // 0 = rendered in the direction it's traveling, 1 = no rotation, 2 = CW rotation, 3 = CCW rotation
	double renderRotationAngle = 0;
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
		renderTransform = new AffineTransform();
	}
	
	public void respawnShot(double x, double y,
			double speed, double angle, int damage,
			int graphic, double renderSize, double hitboxSize) {
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
		xpos += Math.cos(angle) * speed;
		ypos += Math.sin(angle) * speed;
		if(isOffscreen()) disabled = true;
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
		renderTransform.translate(xpos - size, ypos - size);
		if(renderRotationMode != (byte)(1)) {
			renderTransform.rotate(renderRotationAngle, size, size);
		}
		g.drawImage(b, renderTransform, m);
	}
	
	//on-hit and on-tick methods
	public void onHit() {
		disable();
	}
	
	
	
	//utility methods
	private boolean isOffscreen() {
		if (xpos > size + (Game.PLAYFIELDWIDTH / 2)) return true;
		if (xpos < -(Game.PLAYFIELDWIDTH / 2) - size) return true;
		if (ypos < - size) return true;
		if (ypos > size + Game.PLAYFIELDHEIGHT) return true;
		return false;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void disable() {
		disabled = true;
	}
	public int getGraphic() {
		return graphic;
	}
	public int[] getShotInfo() {
		return new int[] {
				(int)xpos,
				(int)ypos,
				(int)hitboxSize,
				damage
		};
	}
	

}
