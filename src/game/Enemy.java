package game;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Enemy {
	BulletManager bulletMGR;
	Player targetPlayer;
	Game game;
	final int numSpawners = 16;
	
	double xpos;
	double ypos;
	boolean disabled;
	BulletSpawner[] spawners = new BulletSpawner[numSpawners];
	

	public Enemy(BulletManager bmgr, Player p, Game g) {
		bulletMGR = bmgr;
		targetPlayer = p;
		game = g;
		disabled = true;
		xpos = -1;
		ypos = -1;
		for(int i = 0; i < numSpawners; i++) {
			spawners[i] = new BulletSpawner(bulletMGR, targetPlayer, game);
		}		
	}
	
	public void tickEnemy() {
		this.doEnemyActions();
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].tickSpawner();
		}
	}
	
	private void doEnemyActions() {
		
	}
	
	public void renderEnemy(Graphics2D g, BufferedImage b) {
		
	}
	
	private void onDeath() {
		
	}
	public boolean isDisabled() {
		return disabled;
	}
}
