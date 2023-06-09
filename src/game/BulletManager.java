package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BulletManager {
	Bullet [] bullets;
	Spritesheet bulletSprites;
	public SoundManager SoundMGR;
	Player relevantPlayer;
	BufferedImage[][] bulletSpriteReference;

	public BulletManager(int size, Spritesheet ss, SoundManager smgr, Player player) {
		bullets = new Bullet[size];
		SoundMGR = smgr;
		relevantPlayer = player;
		for(int i = 0; i < size; i++) bullets[i] = new Bullet(this, relevantPlayer);
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
		for(Bullet b : bullets) {
			if (b.isDisabled() == false) {
				if(b.checkCollision(x, y, rad)) {
					b.collideWithPlayer();
				}
			}
		}
	}
	public void checkGraze(double x, double y, double rad) {
		for(Bullet b : bullets) {
			if (b.isDisabled() == false) {
				if((b.grazed == 0) && b.checkCollision(x, y, rad)) {
					b.grazedByPlayer();
				}
			}
		}
	}
	
	public void deactivateAll() {
		for(int i = 0; i < bullets.length; i++) {
			bullets[i].disable();
		}
	}
	
	public void addBullet(double xPos, double yPos, double speed, double angle, int type, int color, int offscreenProtectionFramesNum, double distance) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i].isDisabled()) {
				bullets[i].respawnBullet(xPos, yPos, speed, angle, type, color, offscreenProtectionFramesNum);
				bullets[i].step(distance);
				break;
			}
		}
	}
}
