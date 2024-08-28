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
	int bossStartPattern;

	public Script1_6(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
	}

	@Override
	public void initActions(int startingChapter) {
		
		stageTimer = 0;
		chapter = startingChapter % 256;
		bossStartPattern = startingChapter / 256;
	}
	@Override
	public void initActions() {
		this.initActions(0);
	}
	

	@Override
	public void tick() {
		stageTimer++;
		switch(chapter) {
		case 0:
			chapter1actions();
			break;
		case 1:
			chapter2actions();
			break;
		case 2:
			if(stageTimer == 60 * 1) {
				if(bossStartPattern == 2) {
					enmMgr.addEnemy(new EnmMboss(), -100, -50, 4500, false, 2);
					bossStartPattern = 0;
				}else{
					enmMgr.addEnemy(new EnmMboss(), -100, -50, 4500, false, 0);
					enmMgr.addEnemy(new EnmMboss(), 100, -50, 4500, false, 1);
				}
		
			}
			if(enmMgr.getIntVar(0) == 2) {
				chapter++;
				stageTimer = 0;
				mgr.deactivateAll();
				chapter3init();
			}
			break;
		case 3:
			chapter3actions();
			break;
		case 4:
			if(stageTimer == 60 * 1) {
				enmMgr.clearEnemies();
				enmMgr.addEnemy(new EnmBoss(), 0, -50, 7500, false, bossStartPattern);
			}
			break;
		default:
			break;
		}


	}
	void chapter1actions() {
		if(stageTimer % 10 == 0) {
			boolean toMirror = (stageTimer % 20 == 0);
			double xSpawn = (parentGame.FetchRNG()).nextDouble();
			xSpawn = xSpawn * 240;
			enmMgr.addEnemy(new Enm2(), xSpawn - 120, -32, 100, toMirror);
		}
		if(stageTimer == 60 * 12) {
			stageTimer = 0;
			chapter++;
			chapter2init();
		}
	}
	void chapter2actions() {
		if(stageTimer < 300) {
			if(stageTimer > 119 && stageTimer % 60 < 2) {
				boolean toMirror = stageTimer % 2 == 0;
				double xSpawn = (parentGame.FetchRNG()).nextDouble();
				xSpawn = xSpawn * 90 + 15;
				enmMgr.addEnemy(new Enm3(), xSpawn, -32, 400, toMirror);
			}
		}else if (stageTimer <= 900){
			if(stageTimer % 15 == 0) {
				boolean toMirror = (stageTimer % 30 == 0);
				double xSpawn = (parentGame.FetchRNG()).nextDouble();
				xSpawn = xSpawn * 240;
				enmMgr.addEnemy(new Enm2(), xSpawn - 120, -32, 100, toMirror);
			}
			if(stageTimer % 40 == 39) {
				double xSpawn = (parentGame.FetchRNG()).nextDouble();
				xSpawn = xSpawn * 240;
				enmMgr.addEnemy(new Enm3(), xSpawn - 120, -32, 400, false);
			}
		}
		if (stageTimer == 17 * 60) {
			stageTimer = 0;
			chapter++;
		}
	}
	void chapter2init() {
		this.stageTimer = 0;
	}
	void chapter3init() {
		this.stageTimer = 0;
	}
	void chapter3actions() {
		if(stageTimer >= 60 * 2) {
			if(stageTimer % 60 == 0) {
				boolean toMirror = (stageTimer % 120 == 0);
				double ySpawn = (parentGame.FetchRNG()).nextDouble();
				ySpawn = ySpawn * 60;
				ySpawn += 40;
				double xvariance = (parentGame.FetchRNG()).nextDouble();
				xvariance *= 40;
				xvariance -= 290;
				enmMgr.addEnemy(new Enm4(), xvariance, ySpawn, 170, toMirror);
			}
			if (stageTimer >= 60 * 10) {
				if (stageTimer % 15 == 7) {
					boolean toMirror = (stageTimer % 30 == 7);
					double xSpawn = (parentGame.FetchRNG()).nextDouble();
					xSpawn = xSpawn * 240;
					double yvariance = (parentGame.FetchRNG()).nextDouble();
					yvariance *= 20;
					yvariance += 40;
					enmMgr.addEnemy(new Enm2(), -250, yvariance, 100, toMirror, 1);

				} 
			}
			if(stageTimer == 60 * 20) {
				stageTimer = 0;
				chapter++;
			}
		}
	}
}
class EnmMboss extends game.enemy.Enemy{
	int pattern;
	double angleRain;
	BulletTransformation boomTransform;
	BulletTransformation AccelTransform;
	BulletTransformation chaseTransform;
	public EnmMboss() {
		super();
	}
	@Override
	protected void initActions() {
		this.pattern = 0;
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.setFlag(FLAG_PERSISTENT);
		this.setEnemySprite(2);
		switch(this.subtype) {
		case 2:
		case 0:
			this.setMovementBounds(-150, -75, 60, 180);
			this.setPosAbsTime(90, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, -150, 120);
			break;
		case 1:
			this.setMovementBounds(75, 150, 60, 180);
			this.setPosAbsTime(90, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 150, 120);
			break;
		
		default:
			break;
		}
	}
	@Override
	protected void doEnemyActions() {
		switch(this.pattern) {
		case 0:
			if(this.enemyTimer == 75) {
				this.clearFlag(FLAG_PERSISTENT);
				this.setFlag(FLAG_BOSS);
				pattern++;
				if(this.subtype == 2) {
					this.attackChaseSetup();
					break;
				}
				attackBoomSetup();
			}
			break;
		case 1:
			if(parentMGR.getIntVar(0) == 1) {
				attackChaseSetup();
			}else attackBoomTick();
			break;
		case 2:
			attackChaseTick();
			break;
		}
	}
	@Override
	protected void doHPCallback() {
		switch(this.pattern) {
		case 1:
			if(parentMGR.getIntVar(0) == 1) {
				attackChaseSetup();
			}else {
				parentMGR.setIntVar(0, 1);
				this.disable();
			}
			break;
		case 2:
			bulletMGR.deactivateAll();
			SoundMGR.playFromArray(SoundManager.Explosion);
			parentMGR.setIntVar(0, 2);
			this.disable();
		}
	}
	protected void attackChaseSetup() {
		this.spawners[0].reInit();
		this.spawners[1].reInit();
		this.pattern++;
		bulletMGR.deactivateAll();
		this.HP = 4000;
		this.maxHP = 4000;
		this.hpCallbackThreshold = 0;
		this.setPosAbsTime(30, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 120);
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Meek);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(5, 1);
		this.spawners[0].setSpeeds(2.5, 1.0);
		this.spawners[0].setAngles(Math.PI /4, -Math.PI * 5.0/4.0);
		this.spawners[0].setTypeAndColor(Bullet.OUTLINE, Bullet.COLOR16_BLUE);
		chaseTransform = new BulletTransformation();
		chaseTransform.queueOffscreenTransform(300);
		chaseTransform.queueWaitTransform(10);
		chaseTransform.queueAccelDirTransform(300, game.floatDiff(0.035, 0.05), Math.PI/2);
		this.spawners[0].setTransformList(chaseTransform);
		this.setMovementBounds(-120, 120, 50, 120);
		this.enemyTimer = 0;
		this.setFlag(FLAG_DAMAGE_IMMUNE);
	}
	protected void attackChaseTick() {
		if(this.enemyTimer == 30) {
			this.clearFlag(FLAG_DAMAGE_IMMUNE);
			this.spawners[0].setActivationFrequency(game.intDiff(7, 5));
		}
		this.angleRain = (Math.sin(this.enemyTimer / 60) + (Math.PI/2));
		chaseTransform.removeTransformationAtIndex(2);
		chaseTransform.insertAccelDirTransform(2, 300, game.floatDiff(0.035, 0.05), this.angleRain);
		if(this.enemyTimer % 120 == 119) {
			this.moveRandomWithinBounds(120, EnemyMovementInterpolator.INTERPOLATION_EASE_OUT_IN_2);
		}
	}
	void attackBoomTick() {
		if(this.enemyTimer == 135) this.clearFlag(FLAG_DAMAGE_IMMUNE);
		if(this.enemyTimer % 120 == 1) {
			this.moveRandomWithinBounds(60, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2);
		}
		if(this.enemyTimer % 120 == 61 || this.enemyTimer % 120 == 62) {
			this.setupShot();
		}
		if(this.enemyTimer % 120 == 63) {
			this.moveRandomWithinBounds(55, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2);
		}
		if(this.enemyTimer % 120 < 63 && this.enemyTimer % 20 == 4) {
			streamingAttack();
		}
		if(this.enemyTimer % 120 < 63 && this.enemyTimer % 20 == 8) this.spawners[1].setActivationFrequency(-1);
	}
	void streamingAttack() {
		this.spawners[1].setAngles(game.getAngleToPlayer(this.getXpos(), this.getYpos()), Math.PI / 60);
		this.spawners[1].setActivationFrequency(1);
		SoundMGR.playFromArray(SoundManager.EnemyShootMed);
	}
	void setupShot() {
		if (this.enemyTimer % 120 == 61) {
			if (this.subtype == 0) {
				parentMGR.setFloatVar(0, this.xpos);
				parentMGR.setFloatVar(1, this.ypos);
			}
			if (this.subtype == 1) {
				double partnerX = parentMGR.getFloatVar(0);
				double partnerY = parentMGR.getFloatVar(1);
				parentMGR.setFloatVar(0, (this.xpos + partnerX) / 2);
				parentMGR.setFloatVar(1, (this.ypos + partnerY) / 2);
			} 
		}else {
			double targetX = parentMGR.getFloatVar(0);
			double targetY = parentMGR.getFloatVar(1);
			double angleTarget = Math.atan2(targetY - this.ypos, targetX - this.xpos);
			double distTarget = Math.pow(Math.pow((targetY - this.ypos), 2) + Math.pow((targetX - this.xpos), 2), 0.5);

			int boomBulletCount = game.intDiff(9, 13);
			this.spawners[0].setAngles(angleTarget, 0);
			this.spawners[0].setSpeeds(distTarget / 75, distTarget / 75);
			this.boomTransform.removeTransformationAtIndex(2);
			this.boomTransform.insertShootPrepareTransform(2, 7, BulletSpawner.Mode_Fan, boomBulletCount, 3, Math.PI + angleTarget, Math.PI / boomBulletCount, 1, 3);
			this.boomTransform.removeTransformationAtIndex(4);
			this.boomTransform.insertShootPrepareTransform(4, 7, BulletSpawner.Mode_Fan, boomBulletCount, 2, (Math.PI + angleTarget) + ((Math.PI / boomBulletCount) / 2), Math.PI / boomBulletCount, 1.5, 2.5);
			this.spawners[0].activate();
		}
	}
	void attackBoomSetup() {
		parentMGR.setIntVar(0, 0);
		int boomhealth = 3000;
		this.maxHP = boomhealth;
		this.HP = boomhealth;
		this.enemyTimer = 0;
		this.hpCallbackThreshold = 0;
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Fan);
		this.spawners[0].setBulletCounts(1, 1);
		this.spawners[0].setAngles(Math.PI/2, 0);
		this.spawners[0].setSpeeds(2, 2);
		this.spawners[1].reInit();
		this.spawners[1].setRelativePos(0, 0);
		this.spawners[1].setMode(BulletSpawner.Mode_Fan);
		this.spawners[1].setBulletCounts(game.intDiff(1, 4), 1);
		this.spawners[1].setAngles(0, 0);
		this.spawners[1].setSpeeds(0.5, 0.5);
		this.spawners[1].setTypeAndColor(Bullet.BALL, Bullet.COLOR16_LIGHT_GREY);
		this.boomTransform = new BulletTransformation();
		this.boomTransform.queueWaitTransform(75);
		this.boomTransform.queueSoundTransform(SoundManager.Explosion);
		int boomBulletCount = game.intDiff(9, 13);
		switch(this.subtype) {
		case 0:
			bulletMGR.deactivateAll();
			this.spawners[0].setTypeAndColor(Bullet.MENTOS, Bullet.COLOR8_YELLOW);
			this.spawners[0].setAngles(0, 0);
			this.boomTransform.queueShootPrepareTransform(5, BulletSpawner.Mode_Fan, boomBulletCount, 3, Math.PI, Math.PI / boomBulletCount, 1, 3);
			this.boomTransform.queueShootActivateTransform(Bullet.STAR_CCW, Bullet.COLOR16_PINK, 0);
			this.boomTransform.queueShootPrepareTransform(5, BulletSpawner.Mode_Fan, boomBulletCount, 2, Math.PI + (Math.PI / boomBulletCount)/2, Math.PI / boomBulletCount, 1.5, 2.5);
			this.boomTransform.queueShootActivateTransform(Bullet.STAR_CCW, Bullet.COLOR16_PINK, 1);
			break;
		case 1:
			this.spawners[0].setTypeAndColor(Bullet.MENTOS, Bullet.COLOR8_PINK);
			this.spawners[0].setAngles(Math.PI, 0);
			this.boomTransform.queueShootPrepareTransform(7, BulletSpawner.Mode_Fan, boomBulletCount, 3, 0, Math.PI / boomBulletCount, 1, 3);
			this.boomTransform.queueShootActivateTransform(Bullet.STAR_CCW, Bullet.COLOR16_YELLOW, 0);
			this.boomTransform.queueShootPrepareTransform(7, BulletSpawner.Mode_Fan, boomBulletCount, 2, (Math.PI / boomBulletCount) / 2, Math.PI / boomBulletCount, 1.5, 2.5);
			this.boomTransform.queueShootActivateTransform(Bullet.STAR_CCW, Bullet.COLOR16_YELLOW, 1);
			break;
		}
		this.spawners[0].setTransformList(this.boomTransform);
		this.AccelTransform = new BulletTransformation();
		this.AccelTransform.queueWaitTransform(24);
		this.AccelTransform.queueAccelAngleVelTransform(35, 0.2, 0);
		this.spawners[1].setTransformList(AccelTransform);
	}
}
class Enm4 extends game.enemy.Enemy{
	BulletTransformation pauseAccel;
	double ang1;
	public Enm4() {
		super();
	}
	@Override
	protected void initActions() {
		pauseAccel = new BulletTransformation();
		this.setEnemySprite(0);
		this.setPosRelTime(150, EnemyMovementInterpolator.INTERPOLATION_LINEAR, 600, 0);
		this.setFlag(FLAG_PERSISTENT);
		//this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(game.intDiff(2, 3), 1);
		this.spawners[0].setSpeeds(10, 1);
		ang1 = game.randRad();
		this.spawners[0].setAngles(ang1, 0);
		int bullType = (this.testFlag(FLAG_MIRROR) ? Bullet.STAR_CCW : Bullet.STAR_CW);
		int bullColor = (this.testFlag(FLAG_MIRROR) ? Bullet.COLOR16_CYAN : Bullet.COLOR16_LIGHT_GREEN);
		this.spawners[0].setTypeAndColor(bullType, bullColor);
		pauseAccel.queueAccelAngleVelTransform(10, -1, 0);
		pauseAccel.queueWaitTransform(30);
		pauseAccel.queueAccelAngleVelTransform(100, 0.025, 0);
		this.spawners[0].setTransformList(pauseAccel);
		this.spawners[0].setSpawnDistance(10);
		this.spawners[0].setActivationFrequency(4);
	}
	@Override
	protected void doEnemyActions() {
		this.ang1 += (Math.PI / 25);
		this.spawners[0].setAngles(ang1, 0);
		if(this.enemyTimer == 160) {
			this.clearFlag(FLAG_PERSISTENT);
		}
	}
}


class Enm3 extends game.enemy.Enemy{
	BulletTransformation explodeTransform;
	public Enm3() {
		super();
	}

	@Override
	protected void initActions() {
		this.setEnemySprite(0);
		this.setPosRelTime(60, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 100);
		this.setFlag(FLAG_PERSISTENT);
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Fan);
		this.spawners[0].setSound(SoundManager.Sparkle);
		this.spawners[0].setAngles(Math.PI / 2, 0);
		this.spawners[0].setTypeAndColor(Bullet.STAR_BIG_CW, Bullet.COLOR8_LIGHT_BLUE);
		this.spawners[0].setSpeeds(1.5, 1);
		explodeTransform = new BulletTransformation();
		explodeTransform.queueWaitTransform(40);
		explodeTransform.queueSoundTransform(SoundManager.EnemyShootLoud);
		explodeTransform.queueShootPrepareTransform(4, BulletSpawner.Mode_Ring_Nonaimed, 5, 2, BulletTransformation.RAND_ANGLE, (Math.PI / 5.0), 1.5, 0.75);
		explodeTransform.queueShootActivateTransform(Bullet.STAR_CW, Bullet.COLOR16_CYAN, 1);
		this.spawners[0].setTransformList(explodeTransform);
	}
	@Override
	protected void doEnemyActions() {
		if(this.enemyTimer == 60) {
			this.clearFlag(FLAG_PERSISTENT);
		}
		if(this.enemyTimer == game.intDiff(75, 65)) this.spawners[0].activate();
		if(this.enemyTimer == 90) {
			if(game.getGvar(Game.GVAR_DIFFICULTY) == 1)  this.spawners[0].activate();
			this.setPosRelTime(60, EnemyMovementInterpolator.INTERPOLATION_EASE_OUT2, 0, -100);
		}
	}
}


class Enm2 extends game.enemy.Enemy{
	int fireStart;
	BulletTransformation AccelTransform;
	public Enm2() {
		super();
	}
	@Override
	protected void initActions() {
		this.setEnemySprite(1);
		switch(this.subtype) {
		case 1:
			double heightVar = game.FetchRNG().nextDouble();
			heightVar *= 40;
			heightVar -= 20;
			this.setPosRelTime(game.intDiff(60, 105), EnemyMovementInterpolator.INTERPOLATION_LINEAR, 500, heightVar);
			break;
		default:
			this.setPosAbsTime(120, EnemyMovementInterpolator.INTERPOLATION_EASE_OUT2, 350, 500);
		}
		this.setFlag(FLAG_PERSISTENT);
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Fan);
		this.spawners[0].setSpeeds(0, 0);
		this.spawners[0].setTypeAndColor(Bullet.BALL, Bullet.COLOR16_ORANGE);
		this.fireStart = game.FetchRNG().nextInt(20) + 25;
		double accelStrength = game.floatDiff(0.03, 0.07);
		AccelTransform = new BulletTransformation();
		AccelTransform.queueWaitTransform(24);
		AccelTransform.queueAccelAngleVelTransform(100, accelStrength, 0);
		this.spawners[0].setTransformList(AccelTransform);
	}
	@Override
	protected void doEnemyActions() {
		if(this.enemyTimer == 30) {
			this.clearFlag(FLAG_PERSISTENT);
		}
		if(this.enemyTimer > this.fireStart) {
			if((this.enemyTimer - this.fireStart) % 20 == 0) Enm2attack();
			if((this.enemyTimer - this.fireStart) % 20 == 4) this.spawners[0].setActivationFrequency(-1);
		}
	}
	protected void Enm2attack() {
		double angAim = game.getAngleToPlayer(this.getXpos(), this.getYpos());
		if(this.testFlag(FLAG_MIRROR)) angAim = Math.PI - angAim;
		this.spawners[0].setAngles(angAim, 0);
		this.spawners[0].setActivationFrequency(1);
		SoundMGR.playFromArray(SoundManager.EnemyShootMuted);
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

	double anglenum1;
	double anglenum2;
	double angleIncrement1;
	double angleIncrement2;
	double angleRain;
	double shotSpeed;
	int attackTimer;
	int patternNum;
	static final int c1 = 5;
	static final double s1 = 0.5;
	final int RING_BULLET_CNT = 9;
	int aimedRingInterval;
	int starSwirlInterval;
	double ang;
	int starCurrentColor;
	int startingPattern;
	double starAngle;
	BulletTransformation starExplodeTransformCW;
	BulletTransformation starExplodeTransformCCW;
	BulletTransformation swirlTransformCW;
	BulletTransformation swirlTransformCCW;
	BulletTransformation accelTransform;
	BulletTransformation fireballTrail;
	double moveBoundsX;
	double moveUpperY;
	double moveLowerY;
	public EnmBoss() {
		super();
	}
	@Override
	protected void initActions() {
		bulletMGR.deactivateAll();
		moveBoundsX = 120.0;
		moveUpperY = 50;
		moveLowerY = 120;
		this.hpCallbackThreshold = 0;
		//accelTransform.queueAccelAngleVelTransform(160, 0.05, (Math.PI)/160);
		this.setEnemySprite(2);
		this.setPosRelTime(120, 3, 0, 100);
		this.setFlag(FLAG_PERSISTENT);
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.angleIncrement1 = 0;
		this.angleIncrement2 = 0;
		this.startingPattern = Math.max(this.subtype, 1);
		this.patternNum = 0;
	}
	@Override
	protected void doEnemyActions() {
		switch(this.patternNum) {
		case 0:
			if(this.enemyTimer == 100) {
				this.clearFlag(FLAG_PERSISTENT);
				this.setFlag(FLAG_BOSS);
				this.patternNum += startingPattern;
				setupNextPattern();
			}
			break;
		case 1:
			attackNon1Tick();
			break;
		case 2:
			attackMancTick();
			break;
		case 3:
			attackNon2Tick();
			break;
		case 4:
			attackStarTick();
			break;
		case 5:
			attackSwirlTick();
			break;
		default:
			break;
		}
	}
	
	protected void attackNon1Tick() {
		if(this.enemyTimer > 30) attackTimer++;
		if(this.enemyTimer == 180) this.clearFlag(FLAG_DAMAGE_IMMUNE);
		if(attackTimer > 0 && attackTimer % 6 == 1) {
			if(attackTimer > 20) {
				anglenum1 += angleIncrement1;
				anglenum2 += angleIncrement2;
			}
			this.spawners[0].setAngles(anglenum1, 0);
			this.spawners[1].setAngles(anglenum2, 0);
			this.spawners[0].activate();
			this.spawners[1].activate();
			if(attackTimer > 360) {
				attackTimer = -40;
				this.moveRandomWithinBounds(40, EnemyMovementInterpolator.INTERPOLATION_EASE_OUT_IN_2);
				anglenum1 = game.randRad();
				anglenum2 = anglenum1;
			}
		}
	}
	protected void attackNon1Setup() {
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.setMovementBounds(-60, 60, 40, 80);
		this.setPosAbsTime(30, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 80);
		this.HP = 7500;
		this.maxHP = 7500;
		this.enemyTimer = 0;
		attackTimer = 0;
		this.accelTransform = new BulletTransformation();
		this.accelTransform.queueWaitTransform(20);
		this.accelTransform.queueAccelAngleVelTransform(50, game.floatDiff(0.03, 0.045), 0);
		anglenum1 = game.randRad();
		anglenum2 = anglenum1;
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(game.intDiff(10, 13), 1);
		this.spawners[0].setSpeeds(1, 1);
		this.spawners[0].setAngles(anglenum1, anglenum1);
		this.spawners[0].setSpawnDistance(20);
		this.spawners[0].setTypeAndColor(Bullet.RICE, Bullet.COLOR16_PURPLE);
		this.spawners[0].setTransformList(accelTransform);
		this.angleIncrement1 = Math.toRadians(Math.E + 1.5);
		this.angleIncrement2 = Math.toRadians(-Math.E - 1.5);
		this.spawners[1].reInit();
		this.spawners[1].setRelativePos(0, 0);
		this.spawners[1].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[1].setSound(SoundManager.EnemyShootMuted);
		this.spawners[1].setBulletCounts(game.intDiff(10, 13), 1);
		this.spawners[1].setSpeeds(1, 1);
		this.spawners[1].setAngles(anglenum2, anglenum2);
		this.spawners[1].setSpawnDistance(20);
		this.spawners[1].setTypeAndColor(Bullet.RICE, Bullet.COLOR16_CYAN);
		this.spawners[1].setTransformList(accelTransform);
	}
	
	protected void attackMancTick() {
		if(this.enemyTimer > 30) attackTimer++;
		if(this.enemyTimer == 140) this.clearFlag(FLAG_DAMAGE_IMMUNE);
		if(attackTimer == -50) this.moveRandomWithinBounds(80, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2);
		if(attackTimer >= 30) {
			attackMancVolley();
		}
	}
	protected void attackMancVolley() {
		if(attackTimer == 30) {
			if(game.getGvar(Game.GVAR_DIFFICULTY) == 1) this.spawners[0].setAngles(-anglenum1 / 2, anglenum1);
			fireballTrail.removeTransformationAtIndex(0);
			fireballTrail.insertWaitTransform(0, 0);
			this.spawners[0].activate();
		}
		else if (attackTimer == 53) {
			if(game.getGvar(Game.GVAR_DIFFICULTY) == 1) this.spawners[0].setAngles(anglenum1 / 2, anglenum1);
			fireballTrail.removeTransformationAtIndex(0);
			fireballTrail.insertWaitTransform(0, 2);
			this.spawners[0].activate();
		}else if(this.attackTimer == 76) {
			if(game.getGvar(Game.GVAR_DIFFICULTY) == 1) this.spawners[0].setAngles(0, anglenum1 * 3 / 4);
			fireballTrail.removeTransformationAtIndex(0);
			fireballTrail.insertWaitTransform(0, 4);
			this.spawners[0].activate();
			this.attackTimer = game.intDiff(-240, -210);
		}
		
	}
	protected void attackMancSetup() {
		this.enemyTimer = 0;
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.setMovementBounds(-60, 60, 40, 80);
		this.setPosAbsTime(30, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 90);
		this.HP = game.intDiff(8000, 6750);
		this.maxHP = game.intDiff(8000, 6750);
		attackTimer = 0;
		fireballTrail = new BulletTransformation();
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setTypeAndColor(Bullet.MENTOS, Bullet.COLOR8_RED);
		shotSpeed = game.floatDiff(5, 5);
		anglenum1 = Math.PI / 5;
		this.spawners[0].setSpeeds(shotSpeed, 0);
		this.spawners[0].setBulletCounts(2, 1);
		this.spawners[0].setMode(BulletSpawner.Mode_Fan_Aimed);
		this.spawners[0].setAngles(0, anglenum1 * 4 / 3);
		this.spawners[0].setSound(SoundManager.EnemyShootLoud);
		this.spawners[0].setSpawnDistance(24);
		fireballTrail.queueWaitTransform(1);
		fireballTrail.queueWaitTransform(game.intDiff(7, 5));
		fireballTrail.queueShootPrepareTransform(5, BulletSpawner.Mode_Ring_Mode5, 2, 1, BulletTransformation.ANGLE_SELF, 0, 3, 0);
		fireballTrail.queueShootActivateTransform(Bullet.ARROWHEAD, Bullet.COLOR16_ORANGE, 0);
		fireballTrail.queueGotoTransform(1, -1);
		fireballTrail.queueAccelAngleVelTransform(15, -0.2, 0);
		fireballTrail.queueAccelAngleVelTransform(1, 0, BulletTransformation.RAND_ANGLE);
		fireballTrail.queueWaitTransform(60);
		fireballTrail.queueAccelAngleVelTransform(40, game.floatDiff(0.03, 0.035), 0);
		this.spawners[0].setTransformList(fireballTrail);
	}

	protected void attackNon2Tick() {
		if(this.enemyTimer > 30) attackTimer++;
		if(this.enemyTimer == 180) this.clearFlag(FLAG_DAMAGE_IMMUNE);
		if(attackTimer > 0 && attackTimer % 5 == 1) {
			anglenum1 += angleIncrement1;
			anglenum2 += angleIncrement2;
			this.spawners[0].setAngles(anglenum1, 0);
			this.spawners[1].setAngles(anglenum2, 0);
			this.spawners[0].activate();
			this.spawners[1].activate();
			if(attackTimer > 360) {
				attackTimer = -40;
				this.moveRandomWithinBounds(40, EnemyMovementInterpolator.INTERPOLATION_EASE_OUT_IN_2);
				anglenum1 = game.randRad();
				anglenum2 = anglenum1;
			}
		}
	}
	protected void attackNon2Setup() {
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.setMovementBounds(-60, 60, 40, 80);
		this.setPosAbsTime(30, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 80);
		this.HP = 7500;
		this.maxHP = 7500;
		this.enemyTimer = 0;
		attackTimer = 0;
		this.accelTransform = new BulletTransformation();
		this.accelTransform.queueWaitTransform(20);
		this.accelTransform.queueAccelAngleVelTransform(50, game.floatDiff(0.035, 0.045), 0);
		anglenum1 = game.randRad();
		anglenum2 = anglenum1;
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(game.intDiff(5, 8), 1);
		this.spawners[0].setSpeeds(1, 1);
		this.spawners[0].setAngles(anglenum1, anglenum1);
		this.spawners[0].setSpawnDistance(20);
		this.spawners[0].setTypeAndColor(Bullet.RICE, Bullet.COLOR16_GREEN);
		this.spawners[0].setTransformList(accelTransform);
		this.angleIncrement1 = Math.toRadians(Math.E + 4);
		this.angleIncrement2 = Math.toRadians(-Math.PI - 1);
		this.spawners[1].reInit();
		this.spawners[1].setRelativePos(0, 0);
		this.spawners[1].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[1].setSound(SoundManager.EnemyShootMuted);
		this.spawners[1].setBulletCounts(game.intDiff(8, 13), 1);
		this.spawners[1].setSpeeds(1, 1);
		this.spawners[1].setAngles(anglenum2, anglenum2);
		this.spawners[1].setSpawnDistance(20);
		this.spawners[1].setTypeAndColor(Bullet.RICE, Bullet.COLOR16_YELLOW);
		this.spawners[1].setTransformList(accelTransform);
	}
	
	
	protected void attackStarTick() {
		if(this.enemyTimer % game.intDiff(180, 120) == 40) {
			double randang1 = game.randRad();
			double randang2;
			randang2 = (game.FetchRNG().nextDouble() / 10) + 0.9;
			//randang2 = game.FetchRNG().nextDouble() / 5;
			//randang2 = game.randRad();
			double randang3 = game.randRad();
			if(this.enemyTimer % game.intDiff(360, 240) == 40) {
				starExplodeTransformCW.removeTransformationAtIndex(3);
				starExplodeTransformCW.insertShootPrepareTransform(3, 5, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, randang1, -randang2, s1, -s1 * (c1 - 1));
				this.spawners[0].setAngles(randang3, starAngle);
				this.spawners[0].activate();
			}else {
				starExplodeTransformCCW.removeTransformationAtIndex(3);
				starExplodeTransformCCW.insertShootPrepareTransform(3, 5, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, randang1, randang2, s1, -s1 * (c1 - 1));
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
		this.maxHP = 6000;
		this.HP = 6000;
		starAngle = Math.PI / 5;
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setTypeAndColor(Bullet.STAR_BIG_CW, Bullet.COLOR8_RED);
		this.spawners[0].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[0].setBulletCounts(5, 2);
		this.spawners[0].setAngles(Math.PI/2, 0);
		this.spawners[0].setSpeeds(2, 1);
		this.spawners[0].setSound(SoundManager.Sparkle);
		this.spawners[1].reInit();
		this.spawners[1].setRelativePos(0, 0);
		this.spawners[1].setTypeAndColor(Bullet.STAR_BIG_CCW, Bullet.COLOR8_YELLOW);
		this.spawners[1].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[1].setBulletCounts(5, 2);
		this.spawners[1].setAngles(Math.PI/2, 0);
		this.spawners[1].setSpeeds(2, 1);
		this.spawners[1].setSound(SoundManager.Sparkle);
		starExplodeTransformCW = new BulletTransformation();
		starExplodeTransformCW.queueOffscreenTransform(35);
		starExplodeTransformCW.queueWaitTransform(35);
		starExplodeTransformCW.queueSoundTransform(SoundManager.EnemyShootLoud);
		starExplodeTransformCW.queueShootPrepareTransform(5, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, 0, 0, s1, -s1 * (1.0/c1));
		starExplodeTransformCW.queueShootActivateTransform(Bullet.STAR_CW, Bullet.COLOR16_BRIGHT_RED, 1);
		starExplodeTransformCW.queueOffscreenTransform(230);
		starExplodeTransformCW.queueWaitTransform(10);
		starExplodeTransformCCW = starExplodeTransformCW.clone();
		starExplodeTransformCCW.removeTransformationAtIndex(4);
		starExplodeTransformCCW.insertShootActivateTransform(4, Bullet.STAR_CCW, Bullet.COLOR16_YELLOW, 1);
		double accel = game.floatDiff(0.015, 0.02);
		double angvel = game.floatDiff(0.05, 0.05);
		starExplodeTransformCW.queueAccelAngleVelTransform(180, accel, angvel);
		starExplodeTransformCCW.queueAccelAngleVelTransform(180, accel, -angvel);
		this.spawners[0].setTransformList(starExplodeTransformCW);
		this.spawners[1].setTransformList(starExplodeTransformCCW);
	}
	
	protected void attackSwirlTick() {	
		if (this.enemyTimer > 30) {
			if (this.enemyTimer % aimedRingInterval == 0) {
				double ang_ex = 0.0 + ang;
				double ang_player = game.getAngleToPlayer(this.getXpos(), this.getYpos());
				this.spawners[0].setAngles(ang_player, Math.PI * 2 / 7);
				for (int i = 0; i < RING_BULLET_CNT; i++) {
					this.spawners[0].setRelativePos(Math.cos(ang_ex) * 24.0, Math.sin(ang_ex) * 24.0);
					this.spawners[0].activate();
					ang_ex += (2.0 * Math.PI) / RING_BULLET_CNT;
				}
			}
			if (this.enemyTimer % (2 * starSwirlInterval) == 0) {
				this.spawners[1].setAngles(game.randRad(), 0);
				starCurrentColor = (starCurrentColor > 13 ? 1 : starCurrentColor + 1);
				this.spawners[1].setTypeAndColor(Bullet.STAR_CW, starCurrentColor);
				this.spawners[1].activate();
			} else if (this.enemyTimer % starSwirlInterval == 0) {
				this.spawners[2].setAngles(game.randRad(), 0);
				starCurrentColor = (starCurrentColor > 13 ? 1 : starCurrentColor + 1);
				this.spawners[2].setTypeAndColor(Bullet.STAR_CCW, starCurrentColor);
				this.spawners[2].activate();
			} 
		}
		if(this.enemyTimer == 180) this.clearFlag(FLAG_DAMAGE_IMMUNE);
	}
	protected void attackSwirlSetup() {
		this.setPosAbsTime(30, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 150);
		this.HP = 5500;
		this.maxHP = 5500;
		this.enemyTimer = 0;
		ang = 0;
		aimedRingInterval = game.intDiff(47, 27);
		starSwirlInterval = 15;
		int starRingCount = game.intDiff(13, 17);
		starCurrentColor = 1;
		this.spawners[0].reInit();
		this.spawners[0].setTypeAndColor(Bullet.OUTLINE, Bullet.COLOR16_DARK_GREY);
		this.spawners[0].setBulletCounts(3, 1);
		this.spawners[0].setMode(BulletSpawner.Mode_Fan);
		this.spawners[0].setSpeeds(2, 2);
		this.spawners[0].setSound(SoundManager.EnemyShootLoud);
		this.spawners[1].reInit();
		this.spawners[1].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[1].setTypeAndColor(Bullet.STAR_CW, Bullet.COLOR16_PINK);
		this.spawners[1].setBulletCounts(starRingCount, 1);
		this.spawners[1].setSpeeds(0.2, 0.2);
		this.spawners[1].setRelativePos(0, 0);
		this.spawners[1].setSound(SoundManager.EnemyShootMuted);
		this.spawners[2].reInit();
		this.spawners[2].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[2].setTypeAndColor(Bullet.STAR_CW, Bullet.COLOR16_PINK);
		this.spawners[2].setBulletCounts(starRingCount, 1);
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
	protected void setupNextPattern() {
		switch(patternNum) {
		case 1:
			attackNon1Setup();
			this.hpCallbackThreshold = 0;
			bulletMGR.deactivateAll();
			break;
		case 2:
			attackMancSetup();
			this.hpCallbackThreshold = 0;
			bulletMGR.deactivateAll();
			break;
		case 3:
			attackNon2Setup();
			this.hpCallbackThreshold = 0;
			bulletMGR.deactivateAll();
			break;
		case 4:
			attackStarSetup();
			this.hpCallbackThreshold = 0;
			bulletMGR.deactivateAll();
			break;
		case 5:
			attackSwirlSetup();
			this.hpCallbackThreshold = -1;
			this.numHealthbars = 0;
			bulletMGR.deactivateAll();
			break;
		default:
			this.numHealthbars = 0;
			break;
		}
	}


	@Override
	protected void doHPCallback() {
		patternNum++;
		setupNextPattern();
		
	}
}
