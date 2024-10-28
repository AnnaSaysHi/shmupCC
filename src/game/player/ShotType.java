package game.player;

public class ShotType {
	short numShooters;
	Player parentPlayer;
	double shotTimer;
	int UFoffset;
	int Foffset;
	ShotData shotData;
	
	
	
	public ShotType(ShotData data, Player parentPlayer) {
		this.parentPlayer = parentPlayer;
		shotData = data;
		shotData.assignShootersToPlayer(parentPlayer.getShotMGR(), parentPlayer);
	}
	public void switchShootersets(int UF, int F) {
		UFoffset = UF;
		Foffset = F;
	}
	
	public void tickShooters(double dt) {
		if(shotTimer >= 1 || parentPlayer.getShotHeld()) {
			int set = parentPlayer.isFocusing ? Foffset : UFoffset;
			shotData.tickShooterSet(set, (int)(shotTimer));
			shotTimer += dt;
			if(shotTimer >= 15) shotTimer -= 15;
		}
	}
}
