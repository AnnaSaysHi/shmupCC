package game;

public class PlayerShooter {
	PlayerShotManager parentManager;
	Game game;
	byte optionNum;
	double offset_x;
	double offset_y;
	double shotAngle;
	int shotDamage;
	int shotGraphic;
	double shotSize;
	double hitboxSize;
	int activationDelay;
	int activationFreq;
	int timer;

	public PlayerShooter(int activationFreq, int activationDelay, int shotDamage, 
			double offsetX, double offsetY, double shotAngle, byte optionNum, 
			int shotGraphic, double shotSize, double hitboxSize,
			Game game, PlayerShotManager parentManager) {
		timer = 0;
		this.activationFreq = activationFreq;
		this.activationDelay = activationDelay;
		this.shotDamage = shotDamage;
		this.offset_x = offsetX;
		this.offset_y = offsetY;
		this.shotAngle = shotAngle;
		this.optionNum = optionNum;
		this.shotGraphic = shotGraphic;
		this.shotSize = shotSize;
		this.hitboxSize = hitboxSize;
		this.game = game;
		this.parentManager = parentManager;
	}

}
