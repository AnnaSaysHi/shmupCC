package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class EnemyManager {

	Enemy[] enemies;
	Spritesheet enemySprites;
	BufferedImage[][] enemySpriteReference;
	Game game;
	Player relevantPlayer;
	int maxSize;
	BulletManager bmgr;
	SoundManager smgr;
	EnemyScript scriptObject;

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
				e.renderEnemy(g, enemySpriteReference[e.returnEnemySprite()][0]);
			}
		}		
	}
	public boolean hitEnemies(int x, int y, int hitbox, int damage) {
		double radSum;
		for(int i = 0; i < maxSize; i++) {
			if(!enemies[i].isDisabled()) {
				radSum = hitbox + enemies[i].hurtboxSize;
				if(Math.pow(enemies[i].xpos - x, 2) + Math.pow(enemies[i].ypos - y, 2) <= Math.pow(radSum, 2)) {
					enemies[i].addDamage(damage);
					return true;
				}
			}
		}
		return false;
	}

	public void addEnemy(String subName, double xpos, double ypos, int HP) {
		for(int i = 0; i < maxSize; i++) {
			if(enemies[i].isDisabled()){
				enemies[i].initEnemy(xpos, ypos, HP);
				enemies[i].setEnemyScript(scriptObject, subName);
				enemies[i].setEnemySprite(1);
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
		scriptObject = new EnemyScript(scriptPath);
		addEnemy("main", 0, 0, 100);
	}


}