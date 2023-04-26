package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BulletManager {
	Bullet [] bullets;
	Spritesheet bulletSprites;
	BufferedImage[][] bulletSpriteReference;

	public BulletManager(int size, Spritesheet ss) {
		bullets = new Bullet[size];
		for(int i = 0; i < size; i++) bullets[i] = new Bullet();
		bulletSprites = ss;
		bulletSpriteReference = new BufferedImage[BulletColor.NUM_BULLET_COLORS][BulletType.NUM_BULLET_TYPES];
		for(int i = 0; i < BulletColor.NUM_BULLET_COLORS; i++) {
			for(int j = 0; j < BulletType.NUM_BULLET_TYPES; j++) {
				bulletSpriteReference[i][j] = ss.getSprite(i, j, 16, 16);
			}
		}
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
	
	public void drawBullets(Graphics2D g, Game m) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i].isDisabled() == false) {
				bullets[i].draw(g, bulletSpriteReference[bullets[i].getColor()] [bullets[i].getType()], m);
			}
		}
		
	}
	
	public void checkCollision(double x, double y, double rad) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i].isDisabled() == false) {
				bullets[i].checkCollision(x, y, rad);
			}
		}
	}
	
	public void deactivateAll() {
		for(int i = 0; i < bullets.length; i++) {
			bullets[i].disable();
		}
	}
	
	public void addBullet(double xPos, double yPos, double speed, double angle, int type, int color, int offscreenProtectionFramesNum) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i].isDisabled()) {
				bullets[i].respawnBullet(xPos, yPos, speed, angle, type, color, offscreenProtectionFramesNum);
				break;
			}
		}
	}
}
