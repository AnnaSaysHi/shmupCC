package game;

import game.audio.SoundManager;
import game.bullet.BulletManager;
import game.enemy.EnemyManager;
import game.player.Player;

public abstract class StageScript {

	protected BulletManager mgr;
	protected Game parentGame;
	protected Player playerChar;
	protected EnemyManager enmMgr;
	protected int stageTimer;
	protected SoundManager smgr;
	protected String script;
	
	public StageScript(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr, SoundManager smgr) {
		this.mgr = mgr;
		this.smgr = smgr;
		parentGame = g;
		stageTimer = 0;
		this.playerChar = playerChar;
		this.enmMgr = enmMgr;
	}
	
	public void init() {
		mgr.deactivateAll();
		enmMgr.reset();
		initActions();
	}
	
	public abstract void initActions();
	
	public abstract void tick();
}
