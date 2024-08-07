package game.enemy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Game;
import game.Spritesheet;
import game.audio.SoundManager;
import game.bullet.BulletManager;
import game.player.Player;
import java.util.ArrayList;

public class EnemyManager {

	Spritesheet enemySprites;
	BufferedImage[][] enemySpriteReference;
	Game game;
	Player relevantPlayer;
	int maxSize;
	BulletManager bmgr;
	SoundManager smgr;
	ArrayList<Enemy> enemies;

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
		enemies = new ArrayList<Enemy>();
		//TODO: initialize enemySpriteReference
	}


	public void updateEnemies() {
		for(Enemy e : enemies) {
			if(!e.isDisabled()) e.tickEnemy();
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
		for(Enemy e : enemies) {
			if(!e.isDisabled()) {
				if(!(e.testFlag(Enemy.FLAG_UNHITTABLE) || e.testFlag(Enemy.FLAG_CONTROL_ENEMY))){
					radSum = hitbox + e.hurtboxSize;
					if(Math.pow(e.getXpos() - x, 2) + Math.pow(e.getYpos() - y, 2) <= Math.pow(radSum, 2)) {
						e.addDamage(damage);
						return true;
					}					
				}
			}
		}
		return false;
	}

	public void addEnemy(Enemy e, double xpos, double ypos, int HP, boolean mirrored) {
		e.initEnemy(xpos, ypos, HP, mirrored, bmgr, relevantPlayer, game, this, smgr);
		if(enemies.size() < maxSize) enemies.add(e);
		
	}
	
	public void reset() {
		enemies.clear();
	}
}