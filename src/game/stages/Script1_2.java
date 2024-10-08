package game.stages;

import game.*;
import game.audio.SoundManager;
import game.bullet.Bullet;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.enemy.EnemyManager;
import game.player.Player;

public class Script1_2 extends StageScript {
	private BulletSpawner testSpawner;
	private double anglenum;
	private double angleIncrement;
	
	public Script1_2(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initActions() {
		anglenum = 0;
		angleIncrement = 0;
		testSpawner = new BulletSpawner(mgr, playerChar, parentGame);
		testSpawner.setSpawnerPos(0, 224);
		testSpawner.setMode(BulletSpawner.Mode_Ring_Nonaimed);
		testSpawner.setBulletCounts(5, 1);
		testSpawner.setSpeeds(3, 3);
		testSpawner.setAngles(anglenum, anglenum);
		testSpawner.setTypeAndColor(Bullet.RICE, Bullet.COLOR16_PURPLE);
		testSpawner.setActivationFrequency(2);
	}
	@Override
	public void tick() {

		angleIncrement += (Math.PI)/2048;
		anglenum += angleIncrement;
		testSpawner.setAngles(anglenum, anglenum);
		testSpawner.tickSpawner();
	}
	
	
}
