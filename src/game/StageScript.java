package game;

public class StageScript {

	protected BulletManager mgr;
	protected Game parentGame;
	protected Player playerChar;
	protected EnemyManager enmMgr;
	
	public StageScript(BulletManager mgr, Game g, Player playerChar, EnemyManager enmMgr) {
		this.mgr = mgr;
		parentGame = g;
		this.playerChar = playerChar;
		this.enmMgr = enmMgr;
	}
	
	public void init() {
		mgr.deactivateAll();
		enmMgr.reset();
		initActions();
	}
	
	public void initActions() {
		
	}
	
	public void tick() {
		
	}
}
