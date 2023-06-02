package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PlayerShotManager {
	PlayerShot[] shots;
	Spritesheet shotSprites;
	BufferedImage[] shotSpriteReference;
	SoundManager soundMGR;
	EnemyManager enmMGR;
	
	public PlayerShotManager(int size, Spritesheet ss, EnemyManager enmMGR, SoundManager soundMGR) {
		shots = new PlayerShot[size];
		for(int i = 0; i < size; i++) shots[i] = new PlayerShot();
		shotSprites = ss;
		shotSpriteReference = new BufferedImage[1];
		shotSpriteReference[0] = ss.getSprite(0, 0, 16, 16);
		this.enmMGR = enmMGR;
		this.soundMGR = soundMGR;
	}
	
	public void updateShots() {
		for (int i = 0; i < shots.length; i++) {
			if(shots[i].isDisabled() == false) {
				shots[i].update();
			}
		}
	}
	
	public void drawShots(Graphics2D g, Game m) {
		for (int i = 0; i < shots.length; i++) {
			if(shots[i].isDisabled() == false) {
				shots[i].draw(g, shotSpriteReference[shots[i].getGraphic()], m);
			}
		}		
	}
	public void deactivateAll() {
		for(int i = 0; i < shots.length; i++) {
			shots[i].disable();
		}
	}
	public void addShot(double xPos, double yPos, double speed, double angle, int damage, int graphic, double renderSize, int hitboxSize) {
		for(int i = 0; i < shots.length; i++) {
			if (shots[i].isDisabled()) {
				shots[i].respawnShot(xPos, yPos, speed, angle, damage, graphic, renderSize, hitboxSize);
				break;
			}
		}
	}
	
	public void enemyHitDetect() {
		int[] shotInfo;
		for(int i = 0; i < shots.length; i++) {
			if(shots[i].isDisabled() == false) {
				shotInfo = shots[i].getShotInfo();
				if(enmMGR.hitEnemies(shotInfo[0], shotInfo[1], shotInfo[2], shotInfo[3])) {
					shots[i].onHit();
				}
			}
		}
	}

}
