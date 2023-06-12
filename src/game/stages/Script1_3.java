package game.stages;
import game.*;


public class Script1_3 extends StageScript{
	private BulletSpawner testSpawner;
	
	//private double m_x = 0.0, m_y = 0.0;

	final int RING_BULLET_CNT = 12;
	final double SCR_OX = 0.0;
	final double SCR_OY = 224.0;
	double ang = 0.0f;
	
	private int m_Time = 0;
	public Script1_3(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
	}
	
	@Override
	public void initActions() {
		testSpawner = new BulletSpawner(mgr, playerChar, parentGame);
		//parentGame.FetchRNG().nextDouble();
		testSpawner.setTypeAndColor(BulletType.RICE, BulletColor.BRIGHT_RED);
		testSpawner.setBulletCounts(8, 1);
		testSpawner.setSpeeds(3.0, 0.0);
		testSpawner.setMode(BulletSpawner.Mode_Ring_Nonaimed);
		//testSpawner.setActivationFrequency(5);
	}
	
	@Override
	public void tick() {
		
		if((m_Time % 20 == 0)) {
			double ang_ex = 0.0 + ang;
			testSpawner.setAngles(parentGame.getAngleToPlayer(SCR_OX, SCR_OY), 0); 
			for(int i = 0; i < RING_BULLET_CNT;i++) {
				testSpawner.setSpawnerPos(SCR_OX + Math.cos(ang_ex) * 24.0f, SCR_OY + Math.sin(ang_ex) * 24.0f);
				testSpawner.activate();
				ang_ex += (2.0 * Math.PI) / RING_BULLET_CNT;
			}
			/*for(int j = 0; j < 8; j++) {
				double ang = 0.0;
				testSpawner.setAngles(ang_ex,  1);
				for(int i = 0; i < RING_BULLET_CNT;i++) {
					testSpawner.setSpawnerPos(SCR_OX + Math.cos(ang) * 24.0f, SCR_OY + Math.sin(ang) * 24.0f);
					testSpawner.activate();
					ang += (2.0 * Math.PI) / RING_BULLET_CNT;
				}
				ang_ex += (2.0 * Math.PI) / 8;
			}*/
			ang +=7.0 * (Math.PI / 180.0f);
		}
		testSpawner.tickSpawner();
		this.m_Time++;
	}
}
