package game.enemyTypes;

import game.BulletManager;
import game.Enemy;
import game.EnemyManager;
import game.Game;
import game.Player;

public class Enm1 extends Enemy {

	public Enm1(BulletManager bmgr, Player p, Game g, EnemyManager emgr) {
		super(bmgr, p, g, emgr);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void initActions(){
		initEnemy(0, 100, 50);
		setEnemySprite(1);
		
	}
	
	@Override
	protected void doEnemyActions() {
		if(enemyTimer == 50) {
			this.setPosRelTime(0, 10, 60, 3);
		}
	}
	
	
	

}
