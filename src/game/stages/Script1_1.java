package game.stages;

import game.*;
import game.audio.SoundManager;
import game.bullet.BulletColor;
import game.bullet.BulletManager;
import game.bullet.BulletSpawner;
import game.bullet.BulletType;
import game.enemy.EnemyManager;
import game.player.Player;

public class Script1_1 extends StageScript {
	private BulletSpawner testSpawner;
	
	public Script1_1(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initActions() {
		testSpawner = new BulletSpawner(mgr, playerChar, parentGame);
		testSpawner.setSpawnerPos(0, 40);
		testSpawner.setMode(BulletSpawner.Mode_Meek);
		testSpawner.setBulletCounts(3, 1);
		testSpawner.setSpeeds(5, 1);
		testSpawner.setAngles(0, 2 * Math.PI);
		testSpawner.setTypeAndColor(BulletType.BALL, BulletColor.BLUE);
		testSpawner.setActivationFrequency(1);
	}
	@Override
	public void tick() {
		testSpawner.tickSpawner();
	}
	
	
}
