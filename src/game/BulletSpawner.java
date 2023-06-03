package game;

public class BulletSpawner {
	BulletManager parentManager;
	Game game;
	int modeNum;
	int layers;
	int ways;
	double spawnerX;
	double spawnerY;
	double speed1;
	double speed2;
	double angle1;
	double angle2;
	int type;
	int color;
	int countdown;
	int activationFreq;
	int protectFrames = 10;
	Player targetPlayer;
	double[] playercoords;
	
	public static final int Mode_Fan_Aimed = 0;
	public static final int Mode_Fan = 1;
	public static final int Mode_Ring_Aimed_Direct = 2;
	public static final int Mode_Ring_Aimed_Around = 3;
	public static final int Mode_Ring_Nonaimed = 4;
	public static final int Mode_Ring_Mode5 = 5;
	public static final int Mode_Fan_Random_Angle = 6;
	public static final int Mode_Ring_Random_Speed = 7;
	public static final int Mode_Meek = 8;
	//constructors
	public BulletSpawner(BulletManager parent, Player player, Game game) {
		parentManager = parent;
		targetPlayer = player;
		this.game = game;
		modeNum = Mode_Fan;
		spawnerX = 0;
		spawnerY = 0;
		layers = 1;
		ways = 1;
		speed1 = 1;
		speed2 = 1;
		angle1 = 0;
		angle2 = 0;
		countdown = -1;
		activationFreq = -1;
	}
	
	//Accessor methods
	/*public double getAngle1() {
		return angle1;
	}
	public double getAngle2() {
		return angle2;
	}
	public double getSpeed1() {
		return speed1;
	}
	public double getSpeed2() {
		return speed2;
	}*/
	public double[] getSpawnerPos(){
		return new double[] {spawnerX, spawnerY};
	}
	
	//Mutator methods
	public void setAngles(double newAngle1, double newAngle2) {
		angle1 = newAngle1;
		angle2 = newAngle2;
	}
	public void setSpeeds(double newSpeed1, double newSpeed2) {
		speed1 = newSpeed1;
		speed2 = newSpeed2;
	}
	public void setSpawnerPos(double xPos, double yPos) {
		spawnerX = xPos;
		spawnerY = yPos;
	}
	public void setBulletCounts(int numWays, int numLayers) {
		layers = numLayers;
		ways = numWays;
	}
	public void setMode(int mode) {
		modeNum = mode;
	}
	public void setTypeAndColor(int bulletType, int bulletColor) {
		type = bulletType;
		color = bulletColor;
	}
	public void setActivationFrequency(int frequency) {
		activationFreq = frequency;
		countdown = frequency;
	}
	
	public void setSpawnProtectionFrames(int protectionFramesCount) {
		protectFrames = protectionFramesCount;
	}
	
	
	public void tickSpawner() {
		playercoords = targetPlayer.getPosAndHitbox();
		countdown--;
		if(countdown == 0) {
			this.activate();
			countdown = activationFreq;
		}
	}
	
	private double getAngleToPlayer() {
		return Math.atan2(playercoords[1] - spawnerY, playercoords[0] - spawnerX);
	}
	
	
	
	
	public void activate() {
		double angleAim = angle1;
		switch(modeNum) {
		case Mode_Fan_Aimed:
			angleAim += this.getAngleToPlayer();
		case Mode_Fan:
			shootFan(angleAim);
			break;
		case Mode_Ring_Aimed_Direct:
			angleAim += this.getAngleToPlayer();
			shootRing(angleAim);
			break;
		case Mode_Ring_Aimed_Around:
			angleAim += this.getAngleToPlayer();
		case Mode_Ring_Mode5:
			angleAim += Math.PI / ways;
		case Mode_Ring_Nonaimed:
			shootRing(angleAim);
			break;
		case Mode_Fan_Random_Angle:
			shootRandomFan();
			break;
		case Mode_Ring_Random_Speed:
			shootRandomRing(angleAim);
			break;
		case Mode_Meek:
			shootPR_Bullet(angle1, angle2, speed1, speed2, layers * ways);
			break;
		default:
			break;
		}
	}
	
	private void shootFan(double angleAim) {
		angleAim = angleAim - ((double)(ways - 1)*angle2)/2.0;
		for(int i = 0; i < ways; i++) {
			shootOneWay(angleAim);
			angleAim += angle2;
		}
		
	}
	
	private void shootOneWay(double angleAim) {
		double shotSpeed = speed1;
		double speedIncrement;
		if (layers == 1) speedIncrement = 0;
		else speedIncrement = (speed2 - speed1) / (layers-1);
		for(int i = 0; i < layers; i++) {
			parentManager.addBullet(spawnerX, spawnerY, shotSpeed, angleAim, type, color, protectFrames);
			shotSpeed += speedIncrement;
		}
	}
	
	private void shootRing(double angleAim) {
		double ringSpeed = speed1;
		double speedIncrement;
		if (layers == 1) speedIncrement = 0;
		else speedIncrement = (speed2 - speed1) / (layers-1);
		for(int i = 0; i < layers; i++) {
			shootRingLayer(angleAim, ringSpeed);
			ringSpeed += speedIncrement;
			angleAim += angle2;
		}
		
	}
	private void shootRingLayer(double angleAim, double ringSpeed) {
		double angleIncrement = (2 * Math.PI) / ways;
		for(int i = 0; i < ways; i++) {
			parentManager.addBullet(spawnerX, spawnerY, ringSpeed, angleAim, type, color, protectFrames);
			angleAim += angleIncrement;
		}
	}
	private void shootRandomFan() {
		double shotSpeed = speed1;
		double speedIncrement;
		if (layers == 1) speedIncrement = 0;
		else speedIncrement = (speed2 - speed1) / (layers-1);
		for(int i = 0; i < layers; i++) {
			shootPR_Bullet(angle1, angle2, shotSpeed, shotSpeed, ways);
			shotSpeed += speedIncrement;
		}
	}
	private void shootRandomRing(double angleAim) {
		double angleIncrement = (2 * Math.PI) / ways;
		for(int i = 0; i < ways; i++) {
			shootPR_Bullet(angleAim, angleAim, speed1, speed2, layers);
			angleAim += angleIncrement;
		}
	}
	
	
	private void shootPR_Bullet(double angleMin, double angleMax, double speedMin, double speedMax, int count) {
		double chosenSpeed;
		double chosenAngle;
		for(int i = 0; i < count; i++) {
			chosenSpeed = (game.FetchRNG().nextDouble() * (speedMax - speedMin)) + speedMin;
			chosenAngle = (game.FetchRNG().nextDouble() * (angleMax - angleMin)) + angleMin;
			parentManager.addBullet(spawnerX, spawnerY, chosenSpeed, chosenAngle, type, color, protectFrames);
		}
	}
	public void reInit() {
		layers = 1;
		ways = 1;
		spawnerX = 0;
		spawnerY = 0;
		speed1 = 0;
		speed2 = 0;
		angle1 = 0;
		angle2 = 0;
		type = 0;
		color = 0;
		countdown = -1;
		activationFreq = -1;
		
	}

}
