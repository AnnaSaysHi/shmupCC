package game;

public abstract class Opcodes {
	
	//opcodes 0 thru 100
	public static final int nop = 0;
	public static final int ret = 10;
	public static final int call = 11;
	public static final int wait = 23;
	
	//opcodes 300 thru 399
	public static final int enemyCreateRel = 300;
	public static final int enemyCreateAbs = 301;
	public static final int enemySetSprite = 302;
	
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
