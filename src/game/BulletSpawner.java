package game;

public class BulletSpawner {
	BulletManager parentManager;
	Mode modeNum;
	int layers;
	int ways;
	double spawnerX;
	double spawnerY;
	double speed1;
	double speed2;
	double angle1;
	double angle2;
	int protectFrames = 10;
	public enum Mode{
		Fan_Aimed,
		Fan,
		Ring_Aimed_Direct,
		Ring_Aimed_Around,
		Ring_Nonaimed,
		Ring_Mode5,
		Random_Angle,
		Random_Speed,
		Meek
	}
	
	public BulletSpawner(BulletManager parent, Mode mode, double initXpos, double initYpos, int numLayers, int numWays, double initSpeedBase, double initSpeedMod, double angleBase, double angleMod) {
		parentManager = parent;
		modeNum = mode;
		spawnerX = initXpos;
		spawnerY = initYpos;
		layers = numLayers;
		ways = numWays;
		speed1 = initSpeedBase;
		speed2 = initSpeedMod;
		angle1 = angleBase;
		angle2 = angleMod;
	}
	
	//Accessor methods
	public double getAngle1() {
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
	}
	public double[] getSpawnerPos(){
		return new double[] {spawnerX, spawnerY};
	}
	
	//Mutator methods
	public void setAngle1(double angle) {
		angle1 = angle;
	}
	public void setAngle2(double angle) {
		angle2 = angle;
	}
	public void setSpeed1(double speed) {
		speed1 = speed;
	}
	public void setSpeed2(double speed) {
		speed2 = speed;
	}
	public void setSpawnerPos(double xPos, double yPos) {
		spawnerX = xPos;
		spawnerY = yPos;
	}
	public void setNumLayers(int numLayers) {
		layers = numLayers;
	}
	public void setNumWays(int numWays) {
		ways = numWays;
	}
	public void setMode(Mode mode) {
		modeNum = mode;
	}
	
	
	public void activate() {
		double angleAim = angle1;
		switch(modeNum) {
		case Fan:
			shootFan(angleAim);
			break;
		case Ring_Mode5:
			angleAim += Math.PI / ways;
		case Ring_Nonaimed:
			shootRing(angleAim);
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
		else speedIncrement = (speed2 - speed1) / layers;
		for(int i = 0; i < layers; i++) {
			parentManager.addBullet(spawnerX, spawnerY, shotSpeed, angleAim, 0, protectFrames);
			shotSpeed += speedIncrement;
		}
	}
	
	private void shootRing(double angleAim) {
		double ringSpeed = speed1;
		double speedIncrement;
		if (layers == 1) speedIncrement = 0;
		else speedIncrement = (speed2 - speed1) / layers;
		for(int i = 0; i < layers; i++) {
			shootRingLayer(angleAim, ringSpeed);
			ringSpeed += speedIncrement;
			angleAim += angle2;
		}
		
	}
	private void shootRingLayer(double angleAim, double ringSpeed) {
		double angleIncrement = (2 * Math.PI) / ways;
		for(int i = 0; i < ways; i++) {
			parentManager.addBullet(spawnerX, spawnerY, ringSpeed, angleAim, 0, protectFrames);
			angleAim += angleIncrement;
		}
	}

}
