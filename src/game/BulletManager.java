package game;

import java.awt.Graphics;
import java.util.Arrays;

public class BulletManager {
	Bullet [] bullets;
	Spritesheet bulletSprites;

	public BulletManager(int size, Spritesheet ss) {
		bullets = new Bullet[size];
		Arrays.fill(bullets, null);
		bulletSprites = ss;
	}
	
	public void updateBullets() {
		for (int i = 0; i < bullets.length; i++){
			if (bullets[i] != null) {
				if(bullets[i].update()) {
					bullets[i] = null;
				}
			}			
		}
	}
	
	public void drawBullets(Graphics g, Game m) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i] != null) {
				bullets[i].draw(g, bulletSprites, m);
			}
		}
		
	}
	
	public void addBullet(double spawnXpos, double spawnYpos, double spawnSpeed, double spawnAngle, int spawnType, int offscreenProtectionFramesNum) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i] == null) {
				bullets[i] = new Bullet(spawnXpos, spawnYpos, spawnSpeed, spawnAngle, spawnType, offscreenProtectionFramesNum);
				break;
			}
		}
	}
}
