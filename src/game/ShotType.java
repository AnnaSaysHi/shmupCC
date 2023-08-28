package game;

public class ShotType {
	short numShooters;
	Player parentPlayer;
	PlayerShooter[] shooters;
	int shotTimer;
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
	
	public void tickShooters() {
		if(shotTimer != 0 || parentPlayer.getShotHeld()) {
			int set = parentPlayer.isFocusing ? Foffset : UFoffset;
			shotData.tickShooterSet(set, shotTimer);
			shotTimer++;
			if(shotTimer == 15) shotTimer = 0;
		}
	}
}
