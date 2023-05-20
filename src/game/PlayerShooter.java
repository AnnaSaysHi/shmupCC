package game;

public class PlayerShooter {
	PlayerShotManager parentManager;
	//Game game;
	Player parentPlayer;
	byte optionNum;
	double offset_x;
	double offset_y;
	double shotAngle;
	double shotSpeed;
	int shotDamage;
	int shotGraphic;
	double shotSize;
	double hitboxSize;
	int activationDelay;
	int activationFreq;
	int timer;

	public PlayerShooter(int activationFreq, int activationDelay, int shotDamage, 
			double offsetX, double offsetY, double shotAngle, double shotSpeed, byte optionNum, 
			int shotGraphic, double shotSize, double hitboxSize,
			PlayerShotManager parentManager, Player parentPlayer) {
		timer = 0;
		this.activationFreq = activationFreq;
		this.activationDelay = activationDelay;
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
		this.parentManager = parentManager;
		this.parentPlayer = parentPlayer;
	}
	
	public void tickShooter() {
		
		if(timer != 0 || parentPlayer.getShotHeld()) {
			if((timer - activationDelay) % activationFreq == 0 && timer >= activationDelay) {
				shoot();
			}
			timer++;
			if(timer == 15) timer = 0;
			
		}
	}
	private double getAdjustedX() {
		return	parentPlayer.getPosAndHitbox()[0] + offset_x;
	}
	private double getAdjustedY() {
		return	parentPlayer.getPosAndHitbox()[1] + offset_y;
	}
	
	
	private void shoot() {
		parentManager.addShot(getAdjustedX(), getAdjustedY(), shotSpeed, shotAngle, shotDamage, shotGraphic, shotSize, hitboxSize);
	}

}
