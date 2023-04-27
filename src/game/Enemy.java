package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Enemy {
	BulletManager bulletMGR;
	Player targetPlayer;
	Game game;
	final int numSpawners = 16;
	int sprite;
	double xpos;
	double ypos;
	int HP;
	int maxHP;
	int framesTillDespawnOffscreen = 0;
	
	
	int renderSize; //radius
	int size;
	double hitboxSize; //radius
	double hurtboxSize; //radius
	boolean disabled;
	BulletSpawner[] spawners = new BulletSpawner[numSpawners];
	

	public Enemy(BulletManager bmgr, Player p, Game g) {
		bulletMGR = bmgr;
		targetPlayer = p;
		game = g;
		disabled = true;
		sprite = 0;
		xpos = -1;
		ypos = -1;
		renderSize = 24;
		size = 2 * renderSize;
		for(int i = 0; i < numSpawners; i++) {
			spawners[i] = new BulletSpawner(bulletMGR, targetPlayer, game);
		}		
	}
	
	public void initEnemy(double x, double y, int health) {
		xpos = x;
		ypos = y;
		HP = health;
		maxHP = health;
		framesTillDespawnOffscreen = 50;
	}
	public void setEnemySprite(int spr) {
		sprite = spr;
	}
	
	public void tickEnemy() {
		this.doEnemyActions();
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].tickSpawner();
		}
		framesTillDespawnOffscreen--;
		if(HP <= 0) {
			onDeath();
		}
		disabled = true;
		if(framesTillDespawnOffscreen <= 0 && isOffscreen()) {
			disabled = true;
		}
	}
	
	private boolean isOffscreen() {
		if (xpos > size + 960) return true;
		if (xpos < -size) return true;
		if (ypos < -size) return true;
		if (ypos > size + 720) return true;
		return false;
	}
	
	private void doEnemyActions() {
		
	}
	
	public int returnEnemySprite() {
		return sprite;
	}
	
	public void renderEnemy(Graphics g, BufferedImage b) {
		g.drawImage(b, (int)(xpos), (int)(ypos), game);
	}
	
	private void onDeath() {
		
	}
	public boolean isDisabled() {
		return disabled;
	}
}
