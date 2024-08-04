package game.enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import game.Game;
import game.audio.SoundManager;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.player.Player;

public class Enemy {
	BulletManager bulletMGR;
	Player targetPlayer;
	Game game;
	SoundManager SoundMGR;
	EnemyManager parentMGR;
	EnemyMovementInterpolator interpolator;
	boolean recentEval;
	int[] intVariables;
	double[] doubleVariables;
	private static final int NUM_INT_VARIABLES = 16;
	private static final int NUM_DOUBLE_VARIABLES = 16;
	
	final int numSpawners = 16;
	public static final double DEG_TO_RAD = 0.017453292519943295; 
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
	protected int movementType; //0 = angle and speed, 1 = xSpeed and ySpeed
	protected int enemyTimer;
	protected int HP;
	protected int maxHP;
	protected int damageToTake;
	int movementTimer1 = -1;
	int movementTimer2 = -1;
	
	int flags;
	public static final int FLAG_UNHITTABLE = 1;		// 0: Enemy has no hurtbox (shots pass through enemy)
	public static final int FLAG_GROUNDED = 2;			// 1: Enemy has no hitbox (cannot kill player via contact)
	public static final int FLAG_PERSISTENT = 4;		// 2:Enemy does not despawn offscreen
	public static final int FLAG_CONTROL_ENEMY = 8;		// 3: Enemy becomes a control enemy. Combines the effects of flags 0, 1, 2, and 4.
	public static final int FLAG_DIALOGUE_IMMUNE = 16;	// 4: Enemy cannot be deleted by dialogue or EnmKillAll. (not implemented yet)
	public static final int FLAG_DAMAGE_IMMUNE = 32;	// 5: Enemy retains its hurtbox, but becomes invincible. (not implemented yet)
	
	
	protected int renderSize; //radius
	protected int size; //I don't even remember if this is radius or diameter
	protected double hitboxSize; //radius
	public int hurtboxSize; //radius
	boolean disabled; // If this is true, then the enemy will not be processed.
	BulletSpawner[] spawners = new BulletSpawner[numSpawners];
	

	public Enemy(BulletManager bmgr, Player p, Game g, EnemyManager emgr, SoundManager smgr) {
		bulletMGR = bmgr;
		SoundMGR = smgr;
		targetPlayer = p;
		game = g;
		parentMGR = emgr;
		interpolator = new EnemyMovementInterpolator(this);
		intVariables = new int[Enemy.NUM_INT_VARIABLES];
		doubleVariables = new double[Enemy.NUM_DOUBLE_VARIABLES];
		recentEval = false;
		
		
		disabled = true;
		sprite = -1;
		xpos = -1;
		ypos = -1;
		flags = 0x00000000;
		
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
		hitboxSize = renderSize;
		hurtboxSize = size / 2;
		

		movementType = 1;
	}
	
	protected void initActions() {
		//To be overridden by custom enemy types.
	}
	
	public void initEnemy(double x, double y, int health) {
		disabled = false;
		sprite = -1;
		xpos = x;
		ypos = y;
		HP = health;
		maxHP = health;
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].reInit();
		}
		Arrays.fill(intVariables, 0);
		Arrays.fill(doubleVariables, 0);
		recentEval = false;
		
		flags = 0x00000000;
		
		enemyTimer = 0;
		movementTimer1 = -1;
		movementTimer2 = -1;
		
		angle = 0;
		speed = 0;
		accel = 0;
		
		xvel = 0;
		yvel = 0;
		xaccel = 0;
		yaccel = 0;
		
		movementType = 1;
		this.initActions();
		
	}
	public void setEnemySprite(int spr) {
		sprite = spr;
	}
	
	public void tickEnemy() {
		enemyTimer++;
		takeDamage();
		this.doEnemyActions();
		this.processEnemyMovement();
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].tickSpawner();
		}
		boolean diesOffscreen = !(testFlag(2) || testFlag(3));
		
		if(diesOffscreen && game.isOutsidePlayfield(xpos, ypos, size)) {
			disabled = true;
		}
	}
	
	protected void doEnemyActions() {
		//To be overridden by custom enemy types.
	}
	private void processEnemyMovement() {
		speed += accel;
		xvel += xaccel;
		yvel += yaccel;
		interpolator.handleMovement();
		
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
	private void takeDamage() {
		HP -= damageToTake;
		damageToTake = 0;
		if(HP <= 0) {
			onDeath();
			disabled = true;
		}
		
	}
	public void addDamage(int damage) {
		damageToTake += damage;
	}
	
	public void renderEnemy(Graphics g, BufferedImage b) {
		g.drawImage(b, (int)(xpos - renderSize + Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2)),
				(int)(ypos - renderSize + Game.PLAYFIELDYOFFSET), game);
	}
	
	private void onDeath() {
		//To be overridden by custom enemy types.
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void disable() {
		disabled = true;
	}
	

	public boolean testFlag(int flagNum) {
		int bitmask = 0x00000001 << flagNum;
		return (!((flags & bitmask) == 0));
	}
		
	
	
	public void setPosAbs(double x, double y) {
		xpos = x;
		ypos = y;
	}
	public void setPosRel(double x, double y) {
		xpos += x;
		ypos += y;
	}
	public void setPosAbsTime(int t, int mode, double x, double y) {
		interpolator.moveOverTime(x, y, t, mode);
	}
	public void setPosRelTime(int t, int mode, double x, double y) {
		interpolator.moveOverTime(x + xpos, y + ypos, t, mode);
	}
}
