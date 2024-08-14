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
		chapter = 0;
		
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
			if(stageTimer == 60 * 4) {
				enmMgr.addEnemy(new EnmBoss(), 0, -50, 10000, false);
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
		angleRain = 0;
		//accelTransform.queueAccelAngleVelTransform(160, 0.05, (Math.PI)/160);
		this.setEnemySprite(2);
		this.setPosRelTime(120, 3, 0, 100);
		this.setFlag(FLAG_PERSISTENT);
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.anglenum = 0;
		this.angleIncrement = 0;
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Meek);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(5, 1);
		this.spawners[0].setSpeeds(2.5, 1.0);
		this.spawners[0].setAngles(Math.PI /4, -Math.PI * 5.0/4.0);
		this.spawners[0].setTypeAndColor(BulletType.OUTLINE, BulletColor.BLUE);
		this.spawners[0].setTransformList(accelTransform);
		//this.spawners[0].setActivationFrequency(4);
	}
	@Override
	protected void doEnemyActions() {
		if(this.enemyTimer == 100) {
			this.resetFlags();
			this.setFlag(FLAG_BOSS);
			this.spawners[0].setActivationFrequency(4);
		}
		this.angleRain = (Math.sin(this.enemyTimer / 90) + (Math.PI/2));
		accelTransform.removeTransformationAtIndex(2);
		accelTransform.insertAccelDirTransform(2, 300, 0.05, this.angleRain);
	}
	
	
	
}
