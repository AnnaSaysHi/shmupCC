package game;

public class BulletSpawner {
	BulletManager parentManager;
	int modeNum;
	int layers;
	int ways;
	double spawnerX;
	double spawnerY;
	double speed1;
	double speed2;
	double angle1;
	double angle2;
	public enum Mode{
		Mode_Fan_Aimed,
		Mode_Fan,
		Mode_Ring_Aimed_Direct,
		Mode_Ring_Aimed_Around,
		Mode_Ring_Nonaimed,
		Mode_Ring_Mode5,
		Mode_Random_Angle,
		Mode_Random_Speed,
		Mode_Meek
	}
	
	public BulletSpawner(BulletManager parent, int mode, double initXpos, double initYpos, int numLayers, int numWays, double initSpeedMin, double initSpeedDiff, double angleBase) {
		parentManager = parent;
		modeNum = mode;
		spawnerX = initXpos;
		spawnerY = initYpos;
		layers = numLayers;
		ways = numWays;
		speed1 = initSpeedMin;
		speed2 = initSpeedDiff;
		angle1 = angleBase;
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
	public void setMode(int mode) {
		modeNum = mode;
	}
	
	
	public void activate() {
		parentManager.addBullet(spawnerX, spawnerY, speed1, angle1, 0, 10);
	}

}
