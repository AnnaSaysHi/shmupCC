package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class BulletManager {
	Bullet [] bullets;
	Spritesheet bulletSprites;

	public BulletManager(int size, Spritesheet ss) {
		bullets = new Bullet[size];
		for(int i = 0; i < size; i++) bullets[i] = new Bullet();
		bulletSprites = ss;
	}
	
	public void updateBullets() {
		for (int i = 0; i < bullets.length; i++){
			if (bullets[i].isDisabled() == false) {
				if(bullets[i].update()) {
					bullets[i].disable();
				}
			}			
		}
	}
	
	public void drawBullets(Graphics g, BufferedImage b, Game m) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i].isDisabled() == false) {
				bullets[i].draw(g, b, m);
			}
		}
		
	}
	
	public void addBullet(double xPos, double yPos, double speed, double angle, int type, int offscreenProtectionFramesNum) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i].isDisabled()) {
				bullets[i].respawnBullet(xPos, yPos, speed, angle, type, offscreenProtectionFramesNum);
				break;
			}
		}
	}
}
