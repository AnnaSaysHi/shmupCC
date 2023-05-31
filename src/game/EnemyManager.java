package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class EnemyManager {

	List<Enemy> enemies;
	Spritesheet enemySprites;
	BufferedImage[][] enemySpriteReference;
	Game game;
	Player relevantPlayer;
	int maxSize;
	BulletManager bmgr;
	SoundManager smgr;

	public EnemyManager(int size, Spritesheet ss, BulletManager mgr, Player p, Game g, SoundManager smgr) {
		maxSize = size;
		enemies = new ArrayList<Enemy>();
		enemySprites = ss;
		enemySpriteReference = new BufferedImage[3][1];
		for(int i = 0; i < 3; i++) {
			enemySpriteReference[i][0] = ss.getSprite(i, 0, 48, 48);
		}
		bmgr = mgr;
		relevantPlayer = p;
		game = g;
		this.smgr = smgr;
		//TODO: initialize enemySpriteReference
	}


	public void updateEnemies() {
		for (int i = 0; i < enemies.size(); i++){
			if(enemies.get(i) != null) {
				if (!enemies.get(i).isDisabled()) {
					enemies.get(i).tickEnemy();
				}
			}
			else enemies.set(i, null);

		}	
		while(enemies.remove(null)) {
			
		}
	}
	
	public void drawEnemies(Graphics2D g) {
		for(int i = 0; i < enemies.size(); i++) {
			if(enemies.get(i) != null) {
				if(!enemies.get(i).isDisabled()) {
					enemies.get(i).renderEnemy(g, enemySpriteReference[enemies.get(i).returnEnemySprite()][0]);
				}
				
			}
		}		
	}
	public boolean hitEnemies(int x, int y, int hitbox, int damage) {
		double radSum;
		for(int i = 0; i < enemies.size(); i++) {
			if(enemies.get(i) != null) {
				if(!enemies.get(i).isDisabled()) {
					radSum = hitbox + enemies.get(i).hurtboxSize;
					if(Math.pow(enemies.get(i).xpos - x, 2) + Math.pow(enemies.get(i).ypos - y, 2) <= Math.pow(radSum, 2)) {
						enemies.get(i).addDamage(damage);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void addEnemy(Enemy e) {
		if(enemies.size() < maxSize) {
			enemies.add(e);
		}
	}
	
	public void reset() {
		enemies.clear();
	}


}