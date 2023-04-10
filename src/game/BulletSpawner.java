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
		switch(modeNum) {
		case Fan:
			shootFan(angle1);
			break;
		
		default:
			break;
		}
	}
	
	private void shootFan(double angleAim) {
		angleAim = angleAim - ((double)(ways - 1)*angle2)/2;
		for(int i = 0; i < ways; i++) {
			shootOneWay(angleAim);
			angleAim += angle2;
		}
		
	}
	
	private void shootOneWay(double angleAim) {
		for(int i = 0; i < layers; i++) {
			double shotSpeed;
			if(layers == 0) shotSpeed = speed1;
			else shotSpeed = speed1 + (((double)(i) / (layers - 1)) * (speed2 - speed1));
			parentManager.addBullet(spawnerX, spawnerY, shotSpeed, angleAim, 0, protectFrames);
		}
	}
	
	private void shootRing(double angleAim) {
		
	}

}
