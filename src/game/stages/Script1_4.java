package game.stages;

import game.*;
import java.util.ArrayList;
import game.enemyTypes.Enm1;

public class Script1_4 extends StageScript {

	public Script1_4(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initActions() {
		//enmMgr.addEnemy(new Enm1(mgr, playerChar, parentGame, enmMgr, smgr));
		ArrayList<String> al = new ArrayList<String>();
		al.add("0");
		al.add("600");
		al.add("12");
		al.add("23");
		al.add("100000");
		Enemy enemyA = new Enemy(mgr, playerChar, parentGame, enmMgr, smgr);
		enemyA.setEnemyScript(new EnemyScript(al));
		enmMgr.addEnemy(enemyA);
	}

}
