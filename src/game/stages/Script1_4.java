package game.stages;

import game.BulletManager;
import game.EnemyManager;
import game.Game;
import game.Player;
import game.SoundManager;
import game.StageScript;
import game.enemyTypes.Enm1;

public class Script1_4 extends StageScript {

	public Script1_4(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initActions() {
		enmMgr.addEnemy(new Enm1(mgr, playerChar, parentGame, enmMgr, smgr));
	}

}
