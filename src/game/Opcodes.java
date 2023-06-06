package game;

public abstract class Opcodes {
	
	//opcodes 0 thru 40
	public static final int nop = 0;
	public static final int delete = 1;
	public static final int declareVariable = 2;
	public static final int declareAndInitialize = 3;
	public static final int closeVariable = 4;
	public static final int ret = 10;
	public static final int call = 11;
	public static final int callAsync = 15;
	public static final int killAllAsync = 21;
	public static final int wait = 23;
	
	
	//opcodes 41 thru 100
	public static final int setVarString = 41;
	public static final int setVarInt = 43;
	public static final int setVarFloat = 45;
	
	//opcodes 300 thru 399
	public static final int enemyCreateRel = 300;
	public static final int enemyCreateAbs = 301;
	public static final int enemySetSprite = 302;
	
	
	//opcodes 400 thru 499
	public static final int setPosAbs = 400;
	public static final int setPosAbsTime = 401;
	public static final int setPosRel = 402;
	public static final int setPosRelTime = 403;
	public static final int setSpeedAndAngle = 404;
	public static final int setXVelAndYVel = 405;
	
	//opcodes 500 thru 599
	public static final int flagSet = 502;
	public static final int flagClear = 503;
	
	
	//opcodes 600 thru 699
	public static final int resetShooter = 600;
	public static final int activate = 601;
	public static final int setSprites = 602;
	public static final int setRelativeShotOffset = 603;
	public static final int setAngles = 604;
	public static final int setSpeeds = 605;
	public static final int setCounts = 606;
	public static final int setAimMode = 607;
	public static final int setShotSound = 608;
	public static final int setShootDistance = 627;
	public static final int setShotFrequency = 642;
	
	
}
