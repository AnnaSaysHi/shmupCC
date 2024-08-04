package game.stages;

import game.*;
import game.audio.SoundManager;
import game.bullet.BulletColor;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.bullet.BulletType;
import game.enemy.EnemyManager;
import game.player.Player;

public class Script_necropotence extends StageScript {
	private BulletSpawner testSpawner;
	private BulletSpawner testSpawner2;
	private double anglenum;
	private double angleIncrement;
	
	public Script_necropotence(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initActions() {
		anglenum = 0;
		angleIncrement = 0;
		testSpawner = new BulletSpawner(mgr, playerChar, parentGame);
		testSpawner.setSpawnerPos(-100, 224);
		testSpawner.setMode(BulletSpawner.Mode_Ring_Nonaimed);
		testSpawner.setBulletCounts(6, 1);
		testSpawner.setSpeeds(2, 2);
		testSpawner.setAngles(anglenum, anglenum);
		testSpawner.setTypeAndColor(BulletType.RICE, BulletColor.PURPLE);
		testSpawner.setActivationFrequency(6);
		testSpawner.setSpawnDistance(-380);
		testSpawner.setSpawnProtectionFrames(400);
		testSpawner2 = new BulletSpawner(mgr, playerChar, parentGame);
		testSpawner2.setSpawnerPos(100, 224);
		testSpawner2.setMode(BulletSpawner.Mode_Ring_Nonaimed);
		testSpawner2.setBulletCounts(6, 1);
		testSpawner2.setSpeeds(2, 2);
		testSpawner2.setAngles(anglenum, anglenum);
		testSpawner2.setTypeAndColor(BulletType.RICE, BulletColor.PURPLE);
		testSpawner2.setActivationFrequency(6);
		testSpawner2.setSpawnDistance(-380);
		testSpawner2.setSpawnProtectionFrames(400);
	}
	@Override
	public void tick() {

		angleIncrement += (Math.PI)/4096;
		anglenum += angleIncrement;
		testSpawner.setAngles(anglenum, anglenum);
		testSpawner2.setAngles(-anglenum, -anglenum);
		testSpawner.tickSpawner();
		testSpawner2.tickSpawner();
	}
	
	
}
