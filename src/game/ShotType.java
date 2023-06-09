package game;

public class ShotType {
	byte numShooters;
	Player parentPlayer;
	PlayerShooter[] shooters;
	
	
	public ShotType(byte shooterCnt, Player parentPlayer) {
		numShooters = shooterCnt;
		this.parentPlayer = parentPlayer;
		shooters = new PlayerShooter[numShooters];
		shooters[0] = new PlayerShooter(5, 0, 16, 0.0, 0.0, -Math.PI/2, 10, (byte)0, 0, 16.0, 8, parentPlayer.getShotMGR(), parentPlayer);
		shooters[1] = new PlayerShooter(5, 0, 8, 0.0, 0.0, -Math.PI/2 + Math.PI/36, 10, (byte)0, 0, 16.0, 8, parentPlayer.getShotMGR(), parentPlayer);
		shooters[2] = new PlayerShooter(5, 0, 8, 0.0, 0.0, -Math.PI/2 - Math.PI/36, 10, (byte)0, 0, 16.0, 8, parentPlayer.getShotMGR(), parentPlayer);
	}
	
	public void tickShooters() {
		for(byte i = 0; i < numShooters; i++) {
			shooters[i].tickShooter();
		}
	}

}
