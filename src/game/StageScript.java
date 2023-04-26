package game;

public class StageScript {

	BulletManager mgr;
	Game parentGame;
	
	public StageScript(BulletManager mgr, Game g) {
		this.mgr = mgr;
		parentGame = g;
	}
}
