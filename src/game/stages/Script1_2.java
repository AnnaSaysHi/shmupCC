package game.stages;

import game.*;
import game.BulletSpawner.Mode;

public class Script1_2 extends StageScript {
	private BulletSpawner testSpawner;
	private double anglenum;
	private double angleIncrement;
	
	public Script1_2(BulletManager mgr, Game g, Player playerChar) {
		super(mgr, g, playerChar);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initActions() {
		anglenum = 0;
		angleIncrement = 0;
		testSpawner = new BulletSpawner(mgr, playerChar, parentGame);
		testSpawner.setSpawnerPos(480, 360);
		testSpawner.setMode(Mode.Ring_Nonaimed);
		testSpawner.setBulletCounts(5, 1);
		testSpawner.setSpeeds(3, 3);
		testSpawner.setAngles(anglenum, anglenum);
		testSpawner.setTypeAndColor(BulletType.RICE, BulletColor.PURPLE);
		testSpawner.setActivationFrequency(1);
	}
	@Override
	public void tick() {

		angleIncrement += (Math.PI)/1024;
		anglenum += angleIncrement;
		testSpawner.setAngles(anglenum, anglenum);
		testSpawner.tickSpawner();
	}
	
	
}
