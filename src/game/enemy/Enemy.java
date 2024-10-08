package game.enemy;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.geom.Arc2D;

import game.Game;
import game.audio.SoundManager;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.player.Player;

public abstract class Enemy {
	protected BulletManager bulletMGR;
	protected Player targetPlayer;
	protected Game game;
	protected SoundManager SoundMGR;
	protected EnemyManager parentMGR;
	EnemyMovementInterpolator interpolator;
	 
	int sprite;
	protected double xpos;
	protected double ypos;
	
	protected double angle;
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
	protected int numHealthbars;
	protected int hpCallbackThreshold;
	int movementTimer1 = -1;
	int movementTimer2 = -1;
	
	protected double leftMovementBound;
	protected double rightMovementBound;
	protected double topMovementBound;
	protected double bottomMovementBound;
	
	protected int subtype;
	
	int flags;
	public static final int FLAG_UNHITTABLE = 0;		// 0: Enemy has no hurtbox (shots pass through enemy)
	public static final int FLAG_GROUNDED = 1;			// 1: Enemy has no hitbox (cannot kill player via contact)
	public static final int FLAG_PERSISTENT = 2;		// 2:Enemy does not despawn offscreen
	public static final int FLAG_CONTROL_ENEMY = 3;		// 3: Enemy becomes a control enemy. Combines the effects of flags 0, 1, 2, and 4.
	public static final int FLAG_DIALOGUE_IMMUNE = 4;	// 4: Enemy cannot be deleted by dialogue or EnmKillAll. (not implemented yet)
	public static final int FLAG_DAMAGE_IMMUNE = 5;		// 5: Enemy retains its hurtbox, but becomes does not take damage.
	public static final int FLAG_MIRROR = 6;			// 6: Enemy's actions are flipped across the Y-axis, including angles.
	public static final int FLAG_BOSS = 7;				// 7: Enemy becomes a boss, complete with visible healthbar.
	
	
	protected int renderSize; //radius
	protected int size; //I don't even remember if this is radius or diameter
	protected double hitboxSize; //radius
	public int hurtboxSize; //radius
	boolean disabled; // If this is true, then the enemy will not be processed.
	protected ArrayList<BulletSpawner> spawners;
	

	public Enemy() {

		interpolator = new EnemyMovementInterpolator(this);
		
		
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
		hitboxSize = renderSize;
		hurtboxSize = size / 2;
		numHealthbars = 0;
		hpCallbackThreshold = -1;
		

		movementType = 1;
		subtype = -1;
	}
	
	protected abstract void initActions();
	
	public void initEnemy(double x, double y, int health, boolean mirrored, BulletManager bmgr, Player p, Game g, EnemyManager emgr, SoundManager smgr) {
		bulletMGR = bmgr;
		SoundMGR = smgr;
		targetPlayer = p;
		game = g;
		parentMGR = emgr;
		spawners = new ArrayList<BulletSpawner>();		
		disabled = false;
		sprite = -1;
		xpos = x;
		ypos = y;
		HP = health;
		maxHP = health;
		numHealthbars = 0;
		hpCallbackThreshold = -1;
		
		
		flags = 0x00000000;
		if(mirrored) this.setFlag(FLAG_MIRROR);
		else this.clearFlag(FLAG_MIRROR);
		
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
		this.leftMovementBound = -1;
		this.rightMovementBound = -1;
		this.topMovementBound = -1;
		this.bottomMovementBound = -1;
		
		movementType = 1;
		this.subtype = -1;
		
	}
	public void initEnemyWithSubtype(double x, double y, int health, boolean mirrored, int subtype, BulletManager bmgr, Player p, Game g, EnemyManager emgr, SoundManager smgr) {
		this.initEnemy(x, y, health, mirrored, bmgr, p, g, emgr, smgr);
		this.subtype = subtype;
	}
	public void setEnemySprite(int spr) {
		sprite = spr;
	}
	
	public void tickEnemy() {
		enemyTimer++;
		takeDamage();
		this.doEnemyActions();
		for(BulletSpawner s : spawners) {
			s.tickSpawner();
		}
		this.processEnemyMovement();
		boolean diesOffscreen = !(testFlag(FLAG_PERSISTENT) || testFlag(FLAG_CONTROL_ENEMY));
		
		if(diesOffscreen && game.isOutsidePlayfield(xpos, ypos, size)) {
			disabled = true;
		}
	}

	
	protected abstract void doEnemyActions();
	
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
		if(hpCallbackThreshold != -1 && HP <= hpCallbackThreshold) doHPCallback();
		if(HP <= 0) {
			if(numHealthbars <= 0) {
				onDeath();
				disabled = true;				
			}else {
				numHealthbars -= 1;
			}
		}
		
	}
	protected void doHPCallback() {
		// TODO Auto-generated method stub
		
	}

	public void addDamage(int damage) {
		if(!testFlag(FLAG_DAMAGE_IMMUNE)) damageToTake += damage;
	}
	
	public void renderEnemy(Graphics g, BufferedImage b) {
		g.drawImage(b, (int)(this.getXpos() - renderSize + Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2)),
				(int)(ypos - renderSize + Game.PLAYFIELDYOFFSET), game);
	}
	public void renderHPbar(Graphics2D g) {
		Stroke previousStroke = g.getStroke();
		double hpbarFrac = (double)this.HP / (double)this.maxHP;
		Arc2D hpArcCenter = new Arc2D.Double(this.getXpos() - (renderSize * 2) + Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH/2), this.getYpos()- (renderSize * 2) + Game.PLAYFIELDYOFFSET, renderSize * 4, renderSize * 4, 90, hpbarFrac * 360.0, Arc2D.OPEN);
		Arc2D hpArcInner = new Arc2D.Double(this.getXpos() - (renderSize * 2) + Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH/2) + 2, this.getYpos()- (renderSize * 2) + Game.PLAYFIELDYOFFSET + 2, (renderSize * 4) - 4, (renderSize * 4) - 4, 90, 360.0, Arc2D.OPEN);
		Arc2D hpArcOuter = new Arc2D.Double(this.getXpos() - (renderSize * 2) + Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH/2) - 2, this.getYpos()- (renderSize * 2) + Game.PLAYFIELDYOFFSET - 2, (renderSize * 4) + 4, (renderSize * 4) + 4, 90, 360.0, Arc2D.OPEN);
		g.setColor(new Color(255, 196, 196));
		g.setStroke(new BasicStroke(4));
		g.draw(hpArcCenter);
		g.setStroke(new BasicStroke(1));
		g.setStroke(previousStroke);
		g.setColor(new Color(255,72,72));	
		g.draw(hpArcInner);
		g.draw(hpArcOuter);
	}
	
	protected void onDeath() {
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
	public void setFlag(int flagNum) {
		int bitmask = 0x00000001 << flagNum;
		flags |= bitmask;
	}
	public void clearFlag(int flagNum) {
		int bitmask = 0x00000001 << flagNum;
		flags &= ~bitmask;
	}
	public void resetFlags() {
		flags = 0;
	}
		
	
	public double getXpos() {
		return this.testFlag(6) ? (this.xpos * -1.0) : this.xpos;
	}
	public void setXpos(double newX) {
		this.xpos = newX;
	}
	public double getYpos() {
		return this.ypos;
	}
	public void setYpos(double newY) {
		this.ypos = newY;
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
	public void setMovementBounds(double leftBound, double rightBound, double topBound, double bottomBound) {
		this.leftMovementBound = leftBound;
		this.rightMovementBound = rightBound;
		this.topMovementBound = topBound;
		this.bottomMovementBound = bottomBound;
	}
	public void moveRandomWithinBounds(int t, int mode) {
		double targetX = ((game.FetchRNG().nextDouble()) * (this.rightMovementBound - this.leftMovementBound)) + this.leftMovementBound;
		double targetY = ((game.FetchRNG().nextDouble()) * (this.bottomMovementBound - this.topMovementBound)) + this.topMovementBound;
		interpolator.moveOverTime(targetX, targetY, t, mode);
	}
	protected void clearSpawners() {
		spawners.clear();
	}
	protected int newSpawner() {
		int toRet = spawners.size();
		spawners.add(new BulletSpawner(bulletMGR, targetPlayer, game));
		spawners.get(toRet).reInit();
		spawners.get(toRet).setParentEnemy(this);
		spawners.get(toRet).setRelativePos(0, 0);
		return toRet;
	}
}
