package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Enemy {
	BulletManager bulletMGR;
	Player targetPlayer;
	Game game;
	EnemyManager parentMGR;
	EnemyMovementInterpolator interpolator;
	
	final int numSpawners = 16;
	int sprite;
	public double xpos;
	public double ypos;
	
	public double angle;
	public double speed;
	public double accel;
	
	public double xvel;
	public double yvel;
	public double xaccel;
	public double yaccel;
	int movementType; //0 = angle and speed, 1 = xSpeed and ySpeed
	
	int HP;
	int maxHP;
	int framesTillDespawnOffscreen = 0;
	int movementTimer1 = -1;
	int movementTimer2 = -1;
	
	
	int renderSize; //radius
	int size;
	double hitboxSize; //radius
	double hurtboxSize; //radius
	boolean disabled;
	BulletSpawner[] spawners = new BulletSpawner[numSpawners];
	

	public Enemy(BulletManager bmgr, Player p, Game g, EnemyManager emgr) {
		bulletMGR = bmgr;
		targetPlayer = p;
		game = g;
		parentMGR = emgr;
		interpolator = new EnemyMovementInterpolator(this);
		
		disabled = true;
		sprite = 0;
		xpos = -1;
		ypos = -1;
		
		angle = 0;
		speed = 0;
		accel = 0;
		
		xvel = 0;
		yvel = 0;
		xaccel = 0;
		yaccel = 0;
		
		HP = 1;
		maxHP = 1;
		
		renderSize = 24;
		size = 2 * renderSize;
		for(int i = 0; i < numSpawners; i++) {
			spawners[i] = new BulletSpawner(bulletMGR, targetPlayer, game);
		}		
		initActions();
	}
	
	public void initActions() {
		
	}
	
	public void initEnemy(double x, double y, int health) {
		disabled = false;
		
		xpos = x;
		ypos = y;
		HP = health;
		maxHP = health;
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].reInit();
		}
		framesTillDespawnOffscreen = 50;
		
		movementTimer1 = -1;
		movementTimer2 = -1;
		
		angle = 0;
		speed = 0;
		accel = 0;
		
		xvel = 0;
		yvel = 0;
		xaccel = 0;
		yaccel = 0;
		
	}
	public void setEnemySprite(int spr) {
		sprite = spr;
	}
	
	public void tickEnemy() {
		this.doEnemyActions();
		this.processEnemyMovement();
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].tickSpawner();
		}
		framesTillDespawnOffscreen--;
		if(HP <= 0) {
			onDeath();
			disabled = true;
		}
		if(framesTillDespawnOffscreen <= 0 && game.isOutsidePlayfield(xpos, ypos, size)) {
			disabled = true;
		}
	}
	
	private void doEnemyActions() {
		
	}
	private void processEnemyMovement() {
		speed += accel;
		xvel += xaccel;
		yvel += yaccel;
		
		switch(movementType) {
		case 0:
			xpos += Math.cos(angle) * speed;
			ypos += Math.sin(angle) * speed;
			break;
		case 1:
			xpos += xvel;
			ypos += yvel;
			break;
		default:
			break;
		}
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
	
	public void setPosAbs(double x, double y) {
		xpos = x;
		ypos = y;
	}
	public void setPosRel(double x, double y) {
		xpos += x;
		ypos += y;
	}
	
}
