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
		enemySpriteReference = new BufferedImage[3][1];
		for(int i = 0; i < 3; i++) {
			enemySpriteReference[i][0] = ss.getSprite(i, 0, 48, 48);
		}
		bmgr = mgr;
		relevantPlayer = p;
		game = g;
		for(int i = 0; i < size; i++) enemies[i] = new Enemy(mgr, p, g);
		//TODO: initialize enemySpriteReference
	}


	public void updateEnemies() {
		for (int i = 0; i < enemies.length; i++){
			if (!enemies[i].disabled) {
				enemies[i].tickEnemy();
			}
		}			
	}
	
	public void drawEnemies(Graphics2D g) {
		for(int i = 0; i < enemies.length; i++) {
			if(!enemies[i].disabled) {
				enemies[i].renderEnemy(g, enemySpriteReference[enemies[i].returnEnemySprite()][0]);
			}
		}
		
	}


}