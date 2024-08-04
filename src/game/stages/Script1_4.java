package game.stages;

import game.*;
import game.audio.SoundManager;
import game.bullet.BulletManager;
import game.enemy.EnemyManager;
import game.player.Player;

public class Script1_4 extends StageScript {

	public Script1_4(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		super(mgr, g, playerChar, enmMgr, smgr);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initActions() {
		
	}
	
	@Override
	public void tick() {
		
	}
	
	
}
class EnmTest extends game.enemy.Enemy{

	public EnmTest() {
		super();
	}
	
}
