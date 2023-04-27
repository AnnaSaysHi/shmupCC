package game;

public class StageScript {

	protected BulletManager mgr;
	protected Game parentGame;
	protected Player playerChar;
	
	public StageScript(BulletManager mgr, Game g, Player playerChar) {
		this.mgr = mgr;
		parentGame = g;
		this.playerChar = playerChar;
	}
	
	public void init() {
		mgr.deactivateAll();
		initActions();
	}
	
	public void initActions() {
		
	}
	
	public void tick() {
		
	}
}
