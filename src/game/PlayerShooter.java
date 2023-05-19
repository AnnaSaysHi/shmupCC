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

	public PlayerShooter() {
		timer = 0;
	}

}
