package game.stages;

import game.Game;
import game.StageScript;
import game.audio.SoundManager;
import game.bullet.BulletColor;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.bullet.BulletTransformation;
import game.bullet.BulletType;
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
				enmMgr.addEnemy(new EnmBoss(), 0, -50, 4000, false);
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
		this.spawners[0].setTypeAndColor(BulletType.BALL, BulletColor.BRIGHT_RED);
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
	double starAngle;
	BulletTransformation starExplodeTransformCW;
	BulletTransformation starExplodeTransformCCW;
	BulletTransformation accelTransform;
	public EnmBoss() {
		super();
	}
	@Override
	protected void initActions() {
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
		this.attackStarSetup();
	}
	@Override
	protected void doEnemyActions() {
		if(this.enemyTimer == 100) {
			this.resetFlags();
			this.setFlag(FLAG_BOSS);
		}
		switch(this.patternNum) {
		case 0:
			attackStarTick();
			break;
		case 1:
			attackChaseTick();
			break;
		default:
			break;
		}
	}
	protected void attackStarTick() {
		if(this.enemyTimer % 120 == 0) {
			double randang1 = game.randRad();
			double randang2 = game.randRad();
			double randang3 = game.randRad();
			if(this.enemyTimer % 240 == 0) {
				starExplodeTransformCW.removeTransformationAtIndex(2);
				starExplodeTransformCW.insertShootPrepareTransform(2, 5, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, randang1, randang2, s1, -s1 * (c1 - 1));
				this.spawners[0].setAngles(randang3, starAngle);
				this.spawners[0].activate();
			}else {
				starExplodeTransformCCW.removeTransformationAtIndex(2);
				starExplodeTransformCCW.insertShootPrepareTransform(2, 5, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, randang1, randang2, s1, -s1 * (c1 - 1));
				this.spawners[1].setAngles(randang3, starAngle);
				this.spawners[1].activate();
			}
		}
	}
	
	protected void attackStarSetup() {
		this.setPosRelTime(60, EnemyMovementInterpolator.INTERPOLATION_EASE_IN2, 0, 150);
		starAngle = Math.PI / 5;
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setTypeAndColor(BulletType.STAR_CW, BulletColor.BRIGHT_RED);
		this.spawners[0].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[0].setBulletCounts(5, 2);
		this.spawners[0].setAngles(Math.PI/2, 0);
		this.spawners[0].setSpeeds(2, 1);
		this.spawners[1].reInit();
		this.spawners[1].setRelativePos(0, 0);
		this.spawners[1].setTypeAndColor(BulletType.STAR_CW, BulletColor.BRIGHT_RED);
		this.spawners[1].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[1].setBulletCounts(5, 2);
		this.spawners[1].setAngles(Math.PI/2, 0);
		this.spawners[1].setSpeeds(2, 1);
		starExplodeTransformCW = new BulletTransformation();
		starExplodeTransformCW.queueOffscreenTransform(35);
		starExplodeTransformCW.queueWaitTransform(35);
		starExplodeTransformCW.queueShootPrepareTransform(5, BulletSpawner.Mode_Ring_Nonaimed, c1, 5, 0, 0, s1, -s1 * (c1 - 1));
		starExplodeTransformCW.queueShootActivateTransform(BulletType.OUTLINE, BulletColor.BRIGHT_RED, 1);
		starExplodeTransformCW.queueOffscreenTransform(300);
		starExplodeTransformCW.queueWaitTransform(10);
		starExplodeTransformCCW = starExplodeTransformCW.clone();
		starExplodeTransformCW.queueAccelAngleVelTransform(180, 0.02, 0.05);
		starExplodeTransformCCW.queueAccelAngleVelTransform(180, 0.02, -0.05);
		this.spawners[0].setTransformList(starExplodeTransformCW);
		this.spawners[1].setTransformList(starExplodeTransformCCW);
	}
	
	protected void attackChaseTick() {
		this.angleRain = (Math.sin(this.enemyTimer / 90) + (Math.PI/2));
		accelTransform.removeTransformationAtIndex(2);
		accelTransform.insertAccelDirTransform(2, 300, 0.05, this.angleRain);
	}
	protected void attackChaseSetup() {
		this.spawners[0].reInit();
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Meek);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(5, 1);
		this.spawners[0].setSpeeds(2.5, 1.0);
		this.spawners[0].setAngles(Math.PI /4, -Math.PI * 5.0/4.0);
		this.spawners[0].setTypeAndColor(BulletType.OUTLINE, BulletColor.BLUE);
		this.spawners[0].setTransformList(accelTransform);
		this.spawners[0].setActivationFrequency(4);
	}
	@Override
	protected void doHPCallback() {
		switch(patternNum) {
		case 0:
			patternNum++;
			this.maxHP = 5000;
			this.HP = 5000;
			attackChaseSetup();
			this.hpCallbackThreshold = -1;
			this.numHealthbars = 0;
			break;
		default:
			this.numHealthbars = 0;
			break;
		
		}
	}
	
	
	
}
