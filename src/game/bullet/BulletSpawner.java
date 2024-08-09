package game.bullet;

import game.Game;
import game.audio.SoundManager;
import game.enemy.Enemy;
import game.player.Player;
/**
 * A class that is used to automate the process of shooting Bullets.
 * Each BulletManager stores all the information required to shoot a Bullet, and does all the calculations required for shooting Bullets.
 * Instantiated Enemy subclasses will come equipped with 16 BulletSpawners that will automatically update;
 * manually instantiated BulletSpawners will have to be updated manually as well.
 */
public class BulletSpawner {
	BulletManager parentManager;
	Game game;
	Enemy parentEnemy;
	int modeNum;
	int layers;
	int ways;
	private boolean followEnemy;
	double spawnerX;
	double spawnerY;
	double relativeX;
	double relativeY;
	double speed1;
	double speed2;
	double angle1;
	double angle2;
	double spawnDistance;
	int type;
	int color;
	int countdown;
	int soundOnActivate;
	int activationFreq;
	int protectFrames = 10;
	BulletTransformation transformsList;
	int transformsStartingIndex;
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
	/**
	 * Constructs and initializes a BulletSpawner. The initialized BulletSpawner, if activated without
	 * further modifying its attributes, will shoot a single aimed pellet directly at the player's position with speed 1.
	 * @param parent
	 * @param player
	 * @param game
	 */
	public BulletSpawner(BulletManager parent, Player player, Game game) {
		parentManager = parent;
		targetPlayer = player;
		this.game = game;
		modeNum = Mode_Fan_Aimed;
		spawnerX = 0;
		spawnerY = 0;
		relativeX = 0;
		relativeY = 0;
		spawnDistance = 0;
		soundOnActivate = SoundManager.No_Sound;
		layers = 1;
		ways = 1;
		speed1 = 1;
		speed2 = 1;
		angle1 = 0;
		angle2 = 0;
		countdown = -1;
		activationFreq = -1;
		followEnemy = false;
		transformsList = null;
		transformsStartingIndex = 0;
	}
	/**
	 * Tells this BulletSpawner which Enemy it should take its position from.
	 * @param e
	 */
	public void setParentEnemy(Enemy e) {
		parentEnemy = e;
		followEnemy = true;
	}
	/**
	 * There is currently nothing that calls this method.
	 * @return the absolute coordinates this BulletSpawner will shoot from.
	 */
	public double[] getSpawnerPos(){
		return new double[] {spawnerX, spawnerY};
	}
	
	/**
	 * Sets the angle1 and angle2 fields. These fields mean different things in different shooting modes.
	 * @param newAngle1
	 * @param newAngle2
	 */
	public void setAngles(double newAngle1, double newAngle2) {		
		angle1 = newAngle1;
		angle2 = newAngle2;					
	}
	/**
	 * Sets the speed1 and speed2 fields. While the two fields are interchangeable, convention is for the speed1 field to be
	 * the lower-bound of the speed of shot Bullets and for speed2 to be the upper bound.
	 * @param newSpeed1
	 * @param newSpeed2
	 */
	public void setSpeeds(double newSpeed1, double newSpeed2) {
		speed1 = newSpeed1;
		speed2 = newSpeed2;
	}
	/**
	 * Sets this spawner's position to the given absolute position. Following invocation of this method, the spawner will also stop
	 * tracking its parent Enemy's movement.
	 * @param xPos
	 * @param yPos
	 */
	public void setSpawnerPos(double xPos, double yPos) {
		followEnemy = false;
		spawnerX = xPos;
		spawnerY = yPos;
	}
	/**
	 * Sets this spawner's relative position. Every tickSpawner call, it will move to this position summed with parentEnemy's position.
	 * If this BulletSpawner has no parentEnemy, nothing will happen.
	 * @param xPos
	 * @param yPos
	 */
	public void setRelativePos(double xPos, double yPos) {
		if(parentEnemy != null) followEnemy = true;
		relativeX = xPos;
		relativeY = yPos;
	}
	/**
	 * Sets this spawner's bullet count attributes. "Layers" refers to how many layers thick a spawned pattern will be, while "ways"
	 * refers to how many bullets are in each layer.
	 * @param numWays
	 * @param numLayers
	 */
	public void setBulletCounts(int numWays, int numLayers) {
		layers = numLayers;
		ways = numWays;
	}
	/**
	 * Sets the spawn distance attribute. This determines how far bullets will move along their trajectory as soon as they are spawned.
	 * @param distance
	 */
	public void setSpawnDistance(double distance) {
		spawnDistance = distance;
	}
	/**
	 * Sets the shooting mode. Each mode works differently.
	 * @param mode
	 */
	public void setMode(int mode) {
		modeNum = mode;
	}
	public void setTypeAndColor(int bulletType, int bulletColor) {
		type = bulletType;
		color = bulletColor;
	}
	public void setSound(int sound) {
		soundOnActivate = sound;
	}
	public void setActivationFrequency(int frequency) {
		activationFreq = frequency;
		countdown = frequency;
	}
	
	public void setTransformList(BulletTransformation transformsList) {
		this.transformsList = transformsList;
	}
	public void setTransformStartingIndex(int index) {
		transformsStartingIndex = index;
	}
	
	public void setSpawnProtectionFrames(int protectionFramesCount) {
		protectFrames = protectionFramesCount;
	}
	
	
	public void tickSpawner() {
		if(followEnemy) {
			spawnerX = parentEnemy.getXpos() + relativeX;
			spawnerY = parentEnemy.getYpos() + relativeY;
		}
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
		if(followEnemy) {
			spawnerX = parentEnemy.getXpos() + relativeX;
			spawnerY = parentEnemy.getYpos() + relativeY;
		}
		boolean mirrored = followEnemy && parentEnemy.testFlag(6);
		double angleAim;
		if(mirrored) angleAim = Math.PI - angle1;
		else angleAim = angle1;
		parentManager.SoundMGR.playFromArray(soundOnActivate);
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
		boolean mirrored = followEnemy && parentEnemy.testFlag(6);
		double angleMod = mirrored ? -angle2 : angle2;
		angleAim = angleAim - ((double)(ways - 1)*angleMod)/2.0;
		for(int i = 0; i < ways; i++) {
			shootOneWay(angleAim);
			angleAim += angleMod;
		}
		
	}
	
	private void shootOneWay(double angleAim) {
		double shotSpeed = speed1;
		double speedIncrement;
		if (layers == 1) speedIncrement = 0;
		else speedIncrement = (speed2 - speed1) / (layers-1);
		for(int i = 0; i < layers; i++) {
			parentManager.addBullet(spawnerX, spawnerY, shotSpeed, angleAim, type, color, protectFrames, spawnDistance, transformsList);
			shotSpeed += speedIncrement;
		}
	}
	
	private void shootRing(double angleAim) {
		double ringSpeed = speed1;
		double speedIncrement;
		boolean mirrored = followEnemy && parentEnemy.testFlag(6);
		if (layers == 1) speedIncrement = 0;
		else speedIncrement = (speed2 - speed1) / (layers-1);
		for(int i = 0; i < layers; i++) {
			shootRingLayer(angleAim, ringSpeed);
			ringSpeed += speedIncrement;
			angleAim += mirrored ? -angle2 : angle2;
		}
		
	}
	private void shootRingLayer(double angleAim, double ringSpeed) {
		double angleIncrement = (2 * Math.PI) / ways;
		for(int i = 0; i < ways; i++) {
			parentManager.addBullet(spawnerX, spawnerY, ringSpeed, angleAim, type, color, protectFrames, spawnDistance, transformsList);
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
			parentManager.addBullet(spawnerX, spawnerY, chosenSpeed, chosenAngle, type, color, protectFrames, spawnDistance, transformsList);
		}
	}
	public void reInit() {
		modeNum = Mode_Fan_Aimed;
		followEnemy = false;
		spawnerX = 0;
		spawnerY = 0;
		relativeX = 0;
		relativeY = 0;
		spawnDistance = 0;
		soundOnActivate = SoundManager.No_Sound;
		layers = 1;
		ways = 1;
		speed1 = 1;
		speed2 = 1;
		angle1 = 0;
		angle2 = 0;
		countdown = -1;
		activationFreq = -1;
		transformsList = null;
		transformsStartingIndex = 0;
	}

}
