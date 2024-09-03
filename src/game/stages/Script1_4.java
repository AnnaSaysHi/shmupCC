package game.stages;

import game.*;
import game.audio.SoundManager;
import game.bullet.Bullet;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.bullet.BulletTransformation;
import game.enemy.EnemyManager;
import game.player.Player;

public class Script1_4 extends StageScript {

	public Script1_4(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initActions() {
		

		enmMgr.addEnemy(new Enm1_4(), 0, -50, 10000, false);
	}
	
	@Override
	public void tick() {
		stageTimer++;
		//if(stageTimer % 400 == 1) enmMgr.addEnemy(new EnmTest(), 0, -50, 1000);
	}
	
	
}
class Enm1_4 extends game.enemy.Enemy{

	double anglenum;
	double angleIncrement;
	double angleRain;
	BulletTransformation accelTransform;
	public Enm1_4() {
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
		this.newSpawner();
		spawners.get(0).setRelativePos(0, 0);
		spawners.get(0).setMode(BulletSpawner.Mode_Ring_Nonaimed);
		spawners.get(0).setSound(SoundManager.EnemyShootMuted);
		spawners.get(0).setBulletCounts(5, 1);
		spawners.get(0).setSpeeds(2.5, 2.5);
		spawners.get(0).setAngles(anglenum, anglenum);
		spawners.get(0).setTypeAndColor(Bullet.RICE, Bullet.COLOR16_PURPLE);
		spawners.get(0).setTransformList(accelTransform);
	}
	@Override
	protected void doEnemyActions() {
		if(this.enemyTimer == 100) {
			this.resetFlags();
			this.setFlag(FLAG_BOSS);
			spawners.get(0).setActivationFrequency(4);
		}
		this.angleIncrement += (Math.PI)/2048;
		this.anglenum += angleIncrement;
		this.angleRain = (Math.sin(this.enemyTimer / 90) + (Math.PI/2));
		accelTransform.removeTransformationAtIndex(2);
		accelTransform.insertAccelDirTransform(2, 300, 0.05, this.angleRain);
		spawners.get(0).setAngles(anglenum, anglenum);
	}
	
	
	
}
