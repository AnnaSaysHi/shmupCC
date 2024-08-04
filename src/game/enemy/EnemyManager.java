package game.enemy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Game;
import game.Spritesheet;
import game.audio.SoundManager;
import game.bullet.BulletManager;
import game.player.Player;

public class EnemyManager {

	Enemy[] enemies;
	Spritesheet enemySprites;
	BufferedImage[][] enemySpriteReference;
	Game game;
	Player relevantPlayer;
	int maxSize;
	BulletManager bmgr;
	SoundManager smgr;

	public EnemyManager(int size, Spritesheet ss, BulletManager mgr, Player p, Game g, SoundManager smgr) {
		maxSize = size;
		enemySprites = ss;
		enemySpriteReference = new BufferedImage[3][1];
		for(int i = 0; i < 3; i++) {
			enemySpriteReference[i][0] = ss.getSprite(i, 0, 48, 48);
		}
		bmgr = mgr;
		relevantPlayer = p;
		game = g;
		this.smgr = smgr;
		

		enemies = new Enemy[maxSize];
		for(int i = 0; i < maxSize; i++) {
			enemies[i] = new Enemy(mgr, relevantPlayer, game, this, this.smgr);
		}
		//TODO: initialize enemySpriteReference
	}


	public void updateEnemies() {
		for (int i = 0; i < maxSize; i++){
			if (!enemies[i].isDisabled()) {
				enemies[i].tickEnemy();
			}
		}
	}
	
	public void drawEnemies(Graphics2D g) {
		for(Enemy e : enemies) {
			if(!e.isDisabled()) {
				int s = e.returnEnemySprite();
				if(s != -1) {
					e.renderEnemy(g, enemySpriteReference[s][0]);
					
				}
			}
		}		
	}
	public boolean hitEnemies(int x, int y, int hitbox, int damage) {
		double radSum;
		for(int i = 0; i < maxSize; i++) {
			if(!enemies[i].isDisabled()) {
				if(!(enemies[i].testFlag(0) || enemies[i].testFlag(3))){
					radSum = hitbox + enemies[i].hurtboxSize;
					if(Math.pow(enemies[i].xpos - x, 2) + Math.pow(enemies[i].ypos - y, 2) <= Math.pow(radSum, 2)) {
						enemies[i].addDamage(damage);
						return true;
					}					
				}
			}
		}
		return false;
	}

	public void addEnemy(String subName, double xpos, double ypos, int HP) {
		for(int i = 0; i < maxSize; i++) {
			if(enemies[i].isDisabled()){
				enemies[i].initEnemy(xpos, ypos, HP);
				break;
			}
		}
	}
	
	public void reset() {
		for(Enemy e : enemies) {
			e.disable();
		}
	}
	public void loadStage(String scriptPath) {
		reset();
	}


}