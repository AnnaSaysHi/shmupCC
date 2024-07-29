package game.sccl;

public abstract class Opcodes {
	
	//opcodes 0 thru 40
	public static final int nop = 0;
	public static final int delete = 1;
	public static final int ret = 10;
	public static final int call = 11;
	public static final int jump = 12;
	public static final int jumpTrue = 13;
	public static final int jumpFalse = 14;
	public static final int callAsync = 15;
	public static final int killAllAsync = 21;
	public static final int wait = 23;
	
	
	//opcodes 41 thru 100
	public static final int setVarInt = 43;
	public static final int setVarFloat = 45;
	public static final int addInts = 50;
	public static final int addFloats = 51;
	public static final int subtractInts = 52;
	public static final int subtractFloats = 53;
	public static final int multInts = 54;
	public static final int multFloats = 55;
	public static final int divInts = 56;
	public static final int divFloats = 57;
	public static final int modInts = 58;
	public static final int equalsInts = 59;
	public static final int equalsFloats = 60;
	public static final int notEqualsInts = 61;
	public static final int notEqualsFloats = 62;
	public static final int lessThanInts = 63;
	public static final int lessThanFloats = 64;
	public static final int lessEqualsInts = 65;
	public static final int lessEqualsFloats = 66;
	public static final int greaterThanInts = 67;
	public static final int greaterThanFloats = 68;
	public static final int greaterEqualsInts = 69;
	public static final int greaterEqualsFloats = 70;
	public static final int bitwiseXorInts = 75;
	public static final int bitwiseOrInts = 76;
	public static final int bitwiseAndInts = 77;
	public static final int decrementVariable = 78;
	public static final int sineArg = 79;
	public static final int cosineArg = 80;
	public static final int circlePos = 81;
	public static final int normalizeAngle = 82;
	public static final int absInt = 83;
	public static final int absFloat = 84;
	public static final int angleFromPoints = 87;
	public static final int sqrt = 88;
	public static final int angleToPlayerSelf = 89;
	public static final int floatChangeOverTime = 91;
	public static final int getRandomInt = 94;
	public static final int getRandomFloat = 95;
	public static final int getRandomAngle = 96;
	
	
	//opcodes 300 thru 399
	public static final int enemyCreateRel = 300;
	public static final int enemyCreateAbs = 301;
	public static final int enemySetSprite = 303;
	
	
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
	public static final int angleToPlayerArgs = 623;
	public static final int setShootDistance = 627;
	public static final int setShotFrequency = 642;
	
	
	public static String OpcodeNameToString(String s) {
		switch(s) {
		case "nop":
			return "0";
		case "delete":
			return "1";
		case "call":
			return "11";
		case "jump":
			return "12";
		case "jumpTrue":
			return "13";
		case "jumpFalse":
			return "14";
		case "callAsync":
			return "15";
		case "killAllAsync":
			return "21";
		case "wait":
			return "23";
		case "setVarInt":
			return "43";
		case "setVarFloat":
			return "45";
		case "addInts":
			return "50";
		case "addFloats":
			return "51";
		case "subtractInts":
			return "52";
		case "subtractFloats":
			return "53";
		case "multInts":
			return "54";
		case "multFloats":
			return "55";
		case "divInts":
			return "56";
		case "divFloats":
			return "57";
		case "modInts":
			return "58";
		case "equalsInts":
			return "59";
		case "equalsFloats":
			return "60";
		case "notEqualsInts":
			return "61";
		case "notEqualsFloats":
			return "62";
		case "lessThanInts":
			return "63";
		case "lessThanFloats":
			return "64";
		case "lessEqualsInts":
			return "65";
		case "lessEqualsFloats":
			return "66";
		case "greaterThanInts":
			return "67";
		case "greaterThanFloats":
			return "68";
		
		default:
			return s;
		}
	}
	
	
}
