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

	public EnemyManager(int size, Spritesheet ss, BulletManager mgr, Player p, Game g) {
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
	
	public void addEnemy(Enemy e) {
		if(enemies.size() < maxSize) {
			enemies.add(e);
		}
	}
	
	public void reset() {
		enemies.clear();
	}


}