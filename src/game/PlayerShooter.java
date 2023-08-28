package game;

public class PlayerShooter {
	PlayerShotManager parentManager;
	//Game game;
	Player parentPlayer;

	short fireRate;
	short startDelay;
	int shotDamage;
	
	double offset_x;
	double offset_y;
	double hitboxSize;
	double shotAngle;
	double shotSpeed;
	
	double shotSize;
	byte optionNum;
	byte shotGraphic;
	byte shotAnimHit;
	byte sfx_on_shoot;
	int func_on_init;
	int func_on_tick;
	int func_on_draw;
	int func_on_hit;


	public PlayerShooter(short activationFreq, short activationDelay, int shotDamage, 
			double offsetX, double offsetY, double hitboxSize, double shotAngle, double shotSpeed, double shotSize, byte optionNum, 
			byte shotGraphic) {

		fireRate = activationFreq;
		startDelay = activationDelay;
		this.shotDamage = shotDamage;
		this.offset_x = offsetX;
		this.offset_y = offsetY;
		this.shotAngle = shotAngle;
		this.shotSpeed = shotSpeed;
		this.optionNum = optionNum;
		this.shotGraphic = shotGraphic;
		this.shotSize = shotSize;
		this.hitboxSize = hitboxSize;
		//this.game = game;
	}
	public void assignPlayerAndManager(PlayerShotManager parentManager, Player parentPlayer) {
		this.parentManager = parentManager;
		this.parentPlayer = parentPlayer;
	}
	
	public void tickShooter(int timer) {
		if((timer - startDelay) % fireRate == 0 && timer >= startDelay) {
			shoot();
		}
	}
	private double[] getAdjustedCoords() {
		double[] toRet = null;
		if(optionNum == 0) {
			toRet = parentPlayer.getPosAndHitbox();
		} else {
			toRet = parentPlayer.getOptionCoords(optionNum - 1);
		}
		toRet[0] = toRet[0] + offset_x;
		toRet[1] = toRet[1] + offset_y;
		return toRet;
	}
	
	
	private void shoot() {
		double[] newCoords = getAdjustedCoords();
		parentManager.addShot(newCoords[0], newCoords[1], shotSpeed, shotAngle, shotDamage, shotGraphic, shotSize, hitboxSize);
	}

}
