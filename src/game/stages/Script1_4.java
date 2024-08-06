package game.stages;

import game.*;
import game.audio.SoundManager;
import game.bullet.BulletColor;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.bullet.BulletTransformation;
import game.bullet.BulletType;
import game.enemy.EnemyManager;
import game.player.Player;

public class Script1_4 extends StageScript {

	public Script1_4(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initActions() {
		

		enmMgr.addEnemy(new EnmTest(), 0, -50, 10000);
	}
	
	@Override
	public void tick() {
		stageTimer++;
		//if(stageTimer % 400 == 1) enmMgr.addEnemy(new EnmTest(), 0, -50, 1000);
	}
	
	
}
class EnmTest extends game.enemy.Enemy{

	double anglenum;
	double angleIncrement;
	double angleRain;
	BulletTransformation accelTransform;
	public EnmTest() {
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
		this.setEnemySprite(1);
		this.setPosRelTime(120, 3, 0, 100);
		this.setFlag(FLAG_PERSISTENT);
		this.setFlag(FLAG_DAMAGE_IMMUNE);
		this.anglenum = 0;
		this.angleIncrement = 0;
		this.spawners[0].setRelativePos(0, 0);
		this.spawners[0].setMode(BulletSpawner.Mode_Ring_Nonaimed);
		this.spawners[0].setSound(SoundManager.EnemyShootMuted);
		this.spawners[0].setBulletCounts(5, 1);
		this.spawners[0].setSpeeds(2.5, 2.5);
		this.spawners[0].setAngles(anglenum, anglenum);
		this.spawners[0].setTypeAndColor(BulletType.RICE, BulletColor.PURPLE);
		this.spawners[0].setTransformList(accelTransform);
		//this.spawners[0].setActivationFrequency(4);
	}
	@Override
	protected void doEnemyActions() {
		if(this.enemyTimer == 100) {
			this.resetFlags();
			this.spawners[0].setActivationFrequency(4);
		}
		this.angleIncrement += (Math.PI)/2048;
		this.anglenum += angleIncrement;
		this.angleRain = (Math.sin(this.enemyTimer / 90) + (Math.PI/2));
		accelTransform.removeTransformationAtIndex(2);
		accelTransform.insertAccelDirTransform(2, 300, 0.05, this.angleRain);
		this.spawners[0].setAngles(anglenum, anglenum);
	}
	
	
	
}
