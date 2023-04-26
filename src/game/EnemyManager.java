package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class EnemyManager {

	Enemy[] enemies;
	Spritesheet enemySprites;
	BufferedImage[][] enemySpriteReference;
	Game game;
	Player relevantPlayer;
	BulletManager bmgr;

	public EnemyManager(int size, Spritesheet ss, BulletManager mgr, Player p, Game g) {
		enemies = new Enemy[size];
		enemySprites = ss;
		bmgr = mgr;
		relevantPlayer = p;
		game = g;
		for(int i = 0; i < size; i++) enemies[i] = new Enemy(mgr, p, g);
		//TODO: initialize enemySpriteReference
	}


	public void updateEnemies() {
		for (int i = 0; i < enemies.length; i++){
			if (enemies[i].isDisabled() == false) {
				enemies[i].tickEnemy();
			}
		}			
	}


}