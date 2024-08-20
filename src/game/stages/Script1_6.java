package game.stages;

import game.Game;
import game.StageScript;
import game.audio.SoundManager;
import game.bullet.Bullet;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.bullet.BulletTransformation;
import game.enemy.EnemyManager;
import game.enemy.EnemyMovementInterpolator;
import game.player.Player;

public class Script1_6 extends StageScript {
	int chapter;

	public Script1_6(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
	}

	@Override
	public void initActions() {
		stageTimer = 0;
		chapter = 1;
		
	}

	@Override
	public void tick() {
		stageTimer++;
		switch(chapter) {
		case 0:
			if(stageTimer % 60 == 0) {
				boolean toMirror = (stageTimer % 120 == 0);
				double xSpawn = (parentGame.FetchRNG()).nextDouble();
				xSpawn = xSpawn * 120;
				enmMgr.addEnemy(new Enm1(), xSpawn - 60, -32, 300, toMirror);
			}
			if(stageTimer == 60 * 12) {
				stageTimer = 0;
				chapter++;
			}
			break;
		case 1:
			if(stageTimer == 60 * 1) {
				enmMgr.addEnemy(new EnmBoss(), 0, -50, 7500, false);
			}
			break;
		default:
			break;
		}

		
	}
	

}
class Enm1 extends game.enemy.Enemy{

	public Enm1() {
		super();
	}
	@Override
	protected void initActions() {
		this.setEnemySprite(1);
		this.setPosRelTime(120, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 50, 150);
		this.setFlag(FLAG_PERSISTENT);
		//this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Fan);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(5, 2);
		this.spawners[0].setSpeeds(2.5, 1);
		double ang1 = Math.toRadians(100);
		double ang2 = Math.toRadians(10);
		this.spawners[0].setAngles(ang1, ang2);
		this.spawners[0].setTypeAndColor(Bullet.BALL, Bullet.COLOR16_BRIGHT_RED);
		this.spawners[0].setActivationFrequency(50);
	}
	@Override
	protected void doEnemyActions() {
		if(this.enemyTimer == 160) {
			this.clearFlag(FLAG_PERSISTENT);
			this.setPosRelTime(300, EnemyMovementInterpolator.INTERPOLATION_LINEAR, 600, 50);
		}
	}
}
class EnmBoss extends game.enemy.Enemy{

	double anglenum;
	double angleIncrement;
	double angleRain;
	int patternNum;
	static final int c1 = 5;
	static final double s1 = 0.5;
	final int RING_BULLET_CNT = 9;
	int aimedRingInterval;
	int starSwirlInterval;
	double ang;
	int starCurrentColor;
	double starAngle;
	BulletTransformation starExplodeTransformCW;
	BulletTransformation starExplodeTransformCCW;
	BulletTransformation accelTransform;
	BulletTransformation swirlTransformCW;
	BulletTransformation swirlTransformCCW;
	double moveBoundsX;
	double moveUpperY;
	double moveLowerY;
	public EnmBoss() {
		super();
	}
	@Override
	protected void initActions() {
		moveBoundsX = 120.0;
		moveUpperY = 50;
		moveLowerY = 120;
		accelTransform = new BulletTransformation();
		accelTransform.queueOffscreenTransform(300);
		accelTransform.queueWaitTransform(10);
		accelTransform.queueAccelDirTransform(300, 0.05, Math.PI/2);
		this.hpCallbackThreshold = 0;
		angleRain = 0;
		//accelTransform.queueAccelAngleVelTransform(160, 0.05, (Math.PI)/160);
		this.setEnemySprite(2);
		this.setPosRelTime(120, 3, 0, 100);
		this.setFlag(FLAG_PERSISTENT);
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.anglenum = 0;
		this.angleIncrement = 0;
		this.patternNum = 0;
		attackSwirlSetup();
	}
	@Override
	protected void doEnemyActions() {
		switch(this.patternNum) {
		case 0:
			if(this.enemyTimer == 100) {
				this.clearFlag(FLAG_PERSISTENT);
				this.setFlag(FLAG_BOSS);
				this.enemyTimer = 0;
				this.patternNum++;
			}
			break;
		case 1:
			attackSwirlTick();
			break;
		case 2:
			attackStarTick();
			break;
		case 3:
			attackChaseTick();
			break;
		default:
			break;
		}
	}
	protected void attackSwirlTick() {
		if(this.enemyTimer % aimedRingInterval == 0) {
			double ang_ex = 0.0 + ang;
			double ang_player = game.getAngleToPlayer(this.getXpos(), this.getYpos());
			this.spawners[0].setAngles(ang_player, 0);
			for(int i = 0; i < RING_BULLET_CNT; i++) {
				this.spawners[0].setRelativePos(Math.cos(ang_ex) * 24.0, Math.sin(ang_ex) * 24.0);
				this.spawners[0].activate();
				ang_ex += (2.0 * Math.PI) / RING_BULLET_CNT;
			}
		}
		if(this.enemyTimer % (2 * starSwirlInterval) == 0) {
			this.spawners[1].setAngles(game.randRad(), 0);
			starCurrentColor = (starCurrentColor > 13 ? 1 : starCurrentColor + 1);
			this.spawners[1].setTypeAndColor(Bullet.STAR_CW, starCurrentColor);
			this.spawners[1].activate();
		}else if (this.enemyTimer % starSwirlInterval == 0) {
			this.spawners[2].setAngles(game.randRad(), 0);
			starCurrentColor = (starCurrentColor > 13 ? 1 : starCurrentColor + 1);
			this.spawners[2].setTypeAndColor(Bullet.STAR_CCW, starCurrentColor);
			this.spawners[2].activate();
		}
		if(this.enemyTimer == 180) this.clearFlag(FLAG_DAMAGE_IMMUNE);
	}
	protected void attackSwirlSetup() {
		this.setPosAbsTime(30, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 150);
		ang = 0;
		aimedRingInterval = 27;
		starSwirlInterval = 15;
		starCurrentColor = 1;
		this.spawners[0].reInit();
		this.spawners[0].setTypeAndColor(Bullet.OUTLINE, Bullet.COLOR16_DARK_GREY);
		this.spawners[0].setBulletCounts(7, 1);
		this.spawners[0].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[0].setSpeeds(2, 2);
		this.spawners[0].setSound(SoundManager.EnemyShootLoud);
		this.spawners[1].reInit();
		this.spawners[1].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[1].setTypeAndColor(Bullet.STAR_CW, Bullet.COLOR16_PINK);
		this.spawners[1].setBulletCounts(17, 1);
		this.spawners[1].setSpeeds(0.2, 0.2);
		this.spawners[1].setRelativePos(0, 0);
		this.spawners[1].setSound(SoundManager.EnemyShootMuted);
		this.spawners[2].reInit();
		this.spawners[2].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[2].setTypeAndColor(Bullet.STAR_CW, Bullet.COLOR16_PINK);
		this.spawners[2].setBulletCounts(17, 1);
		this.spawners[2].setSpeeds(0.2, 0.2);
		this.spawners[2].setRelativePos(0, 0);
		this.spawners[2].setSound(SoundManager.EnemyShootMuted);
		swirlTransformCW = new BulletTransformation();
		swirlTransformCCW = new BulletTransformation();
		double b = 0.0075;
		swirlTransformCW.queueAccelAngleVelTransform(400, 0.01, b);
		swirlTransformCCW.queueAccelAngleVelTransform(400, 0.01, -b);
		this.spawners[1].setTransformList(swirlTransformCW);
		this.spawners[2].setTransformList(swirlTransformCCW);
	}
	protected void attackStarTick() {
		if(this.enemyTimer % 120 == 0) {
			double randang1 = game.randRad();
			double randang2;
			randang2 = (game.FetchRNG().nextDouble() / 10) + 0.9;
			//randang2 = game.FetchRNG().nextDouble() / 5;
			//randang2 = game.randRad();
			double randang3 = game.randRad();
			if(this.enemyTimer % 240 == 0) {
				starExplodeTransformCW.removeTransformationAtIndex(2);
				starExplodeTransformCW.insertShootPrepareTransform(2, 4, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, randang1, -randang2, s1, -s1 * (c1 - 1));
				this.spawners[0].setAngles(randang3, starAngle);
				this.spawners[0].activate();
			}else {
				starExplodeTransformCCW.removeTransformationAtIndex(2);
				starExplodeTransformCCW.insertShootPrepareTransform(2, 4, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, randang1, randang2, s1, -s1 * (c1 - 1));
				this.spawners[1].setAngles(randang3, starAngle);
				this.spawners[1].activate();
			}
		}
		else if (this.enemyTimer == 230) this.clearFlag(FLAG_DAMAGE_IMMUNE);
	}
	
	protected void attackStarSetup() {
		this.enemyTimer = 0;
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.setPosAbsTime(60, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 100);
		starAngle = Math.PI / 5;
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setTypeAndColor(Bullet.STAR_CW, Bullet.COLOR16_BRIGHT_RED);
		this.spawners[0].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[0].setBulletCounts(5, 2);
		this.spawners[0].setAngles(Math.PI/2, 0);
		this.spawners[0].setSpeeds(2, 1);
		this.spawners[1].reInit();
		this.spawners[1].setRelativePos(0, 0);
		this.spawners[1].setTypeAndColor(Bullet.STAR_CCW, Bullet.COLOR16_BRIGHT_RED);
		this.spawners[1].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[1].setBulletCounts(5, 2);
		this.spawners[1].setAngles(Math.PI/2, 0);
		this.spawners[1].setSpeeds(2, 1);
		starExplodeTransformCW = new BulletTransformation();
		starExplodeTransformCW.queueOffscreenTransform(35);
		starExplodeTransformCW.queueWaitTransform(35);
		starExplodeTransformCW.queueShootPrepareTransform(4, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, 0, 0, s1, -s1 * (1.0/c1));
		starExplodeTransformCW.queueShootActivateTransform(Bullet.STAR_CW, Bullet.COLOR16_BRIGHT_RED, 1);
		starExplodeTransformCW.queueOffscreenTransform(230);
		starExplodeTransformCW.queueWaitTransform(10);
		starExplodeTransformCCW = starExplodeTransformCW.clone();
		starExplodeTransformCCW.removeTransformationAtIndex(3);
		starExplodeTransformCCW.insertShootActivateTransform(3, Bullet.STAR_CCW, Bullet.COLOR16_DARK_RED, 1);
		starExplodeTransformCW.queueAccelAngleVelTransform(180, 0.02, 0.05);
		starExplodeTransformCCW.queueAccelAngleVelTransform(180, 0.02, -0.05);
		this.spawners[0].setTransformList(starExplodeTransformCW);
		this.spawners[1].setTransformList(starExplodeTransformCCW);
	}
	
	protected void attackChaseTick() {
		if(this.enemyTimer == 30) {
			this.clearFlag(FLAG_DAMAGE_IMMUNE);
			this.spawners[0].setActivationFrequency(4);
		}
		this.angleRain = (Math.sin(this.enemyTimer / 90) + (Math.PI/2));
		accelTransform.removeTransformationAtIndex(2);
		accelTransform.insertAccelDirTransform(2, 300, 0.05, this.angleRain);
		if(this.enemyTimer % 120 == 119) {
			double newX = (game.FetchRNG().nextDouble() * 2 * this.moveBoundsX) - this.moveBoundsX;
			double newY = (game.FetchRNG().nextDouble() * (this.moveLowerY - this.moveUpperY)) + this.moveUpperY;
			this.setPosAbsTime(120, EnemyMovementInterpolator.INTERPOLATION_EASE_OUT_IN_2, newX, newY);
		}
	}
	protected void attackChaseSetup() {
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Meek);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(5, 1);
		this.spawners[0].setSpeeds(2.5, 1.0);
		this.spawners[0].setAngles(Math.PI /4, -Math.PI * 5.0/4.0);
		this.spawners[0].setTypeAndColor(Bullet.OUTLINE, Bullet.COLOR16_BLUE);
		this.spawners[0].setTransformList(accelTransform);
		this.setPosRelTime(30, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, -50);
		this.enemyTimer = 0;
		this.setFlag(FLAG_DAMAGE_IMMUNE);
	}
	@Override
	protected void doHPCallback() {
		switch(patternNum) {
		case 1:
			patternNum++;
			this.maxHP = 7500;
			this.HP = 7500;
			attackStarSetup();
			this.hpCallbackThreshold = 0;
			bulletMGR.deactivateAll();
			break;
		case 2:
			patternNum++;
			this.maxHP = 6000;
			this.HP = 6000;
			attackChaseSetup();
			this.hpCallbackThreshold = -1;
			this.numHealthbars = 0;
			bulletMGR.deactivateAll();
			break;
		default:
			this.numHealthbars = 0;
			break;
		
		}
	}
	
	
	
}
