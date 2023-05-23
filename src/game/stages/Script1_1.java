package game.stages;

import game.*;
import game.BulletSpawner.Mode;

public class Script1_1 extends StageScript {
	private BulletSpawner testSpawner;
	
	public Script1_1(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr) {
		super(mgr, g, playerChar, enmMgr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initActions() {
		testSpawner = new BulletSpawner(mgr, playerChar, parentGame);
		testSpawner.setSpawnerPos(192, 192);
		testSpawner.setMode(Mode.Meek);
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
