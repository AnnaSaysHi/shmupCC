package game.sccl;

import java.util.regex.Pattern;

import game.enemy.Enemy;

public interface MagicNumbers {
	//For the purpose of this class, a "Magic Number" is a number that refers to some variable. The behavior of these magic numbers is defined in this interface, and is hardcoded.
	
	public static final float RANDF = (float) -9999.0;
	public static final float RANDRAD = (float) -9998.0;
	public static final float SELF_XPOS = (float) -9997.0;
	public static final float SELF_YPOS = (float) -9996.0;
	public static final float PLAYER_XPOS = (float) -9991.0;
	public static final float PLAYER_YPOS = (float) -9990.0;
	public static final float ANGLE_TO_PLAYER = (float) -9989.0;
	public static final int SELF_TIME_SINCE_SPAWN = -9988;
	public static final float RANDF2 = (float) -9987.0;
	public static final int LOCAL_INT_VARS = -9985;
	public static final float LOCAL_FLOAT_VARS = (float) -9969.0;
	public static final int SELF_HP = -9953;

	
	public static int StringToMagicInt(String s) {
		switch(s.toUpperCase()) {
		case "SELF_TIME":
		case "SELF_TIME_SINCE_SPAWN":
		case "TIME_SINCE_SPAWN":
		case "SELFTIME":
		case "TIME":
			return -9988;
		case "SELF_HP":
		case "SELFHP":
		case "HP":
			return SELF_HP;
		default:
			int toRet = 0;
			try {
				if(Pattern.matches("[$]I[0-9]+", s)){
					s = s.substring(2);
					int index = Integer.parseInt(s);
					if(index >= 0 && index < 16) {
						toRet = LOCAL_INT_VARS + index;
					}else toRet = 0;
				} else if(Pattern.matches("[$]F[0-9]+", s)){
					s = s.substring(2);
					int index = Integer.parseInt(s);
					if(index >= 0 && index < 16) {
						toRet = (int)LOCAL_FLOAT_VARS + index;
					} else toRet = 0;
					toRet = Integer.parseInt(s);
				} else {
					toRet = Integer.parseInt(s);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return toRet;
		}
	}
	public static float StringToMagicFloat(String s) {
		switch(s.toUpperCase()) {
		case "RANDF":
		case "RANDFLOAT":
			return RANDF;
		case "RANDRAD":
			return RANDRAD;
		case "SELF_XPOS":
		case "SELF_X":
		case "XPOS":
			return SELF_XPOS;
		case "SELF_YPOS":
		case "SELF_Y":
		case "YPOS":
			return SELF_YPOS;
		case "PLAYER_XPOS":
		case "PLAYER_X":
			return PLAYER_XPOS;
		case "PLAYER_YPOS":
		case "PLAYER_Y":
			return PLAYER_YPOS;
		case "ANGLE_TO_PLAYER":
		case "ANGLETOPLAYER":
		case "ANGLE_PLAYER":
		case "ANGLEPLAYER":
			return ANGLE_TO_PLAYER;
		case "RANDF2":
		case "RANDFLOAT2":
			return RANDF2;
			
		default:
			float toRet = (float) 0.0;
			try {
				if(Pattern.matches("[$]I[0-9]+", s)){
					s = s.substring(2);
					int index = Integer.parseInt(s);
					if(index >= 0 && index < 16) {
						toRet = (float)(LOCAL_INT_VARS + index);
					}else toRet = 0;
				} else if(Pattern.matches("[$]F[0-9]+", s)){
					s = s.substring(2);
					int index = Integer.parseInt(s);
					if(index >= 0 && index < 16) {
						toRet = (float)(LOCAL_FLOAT_VARS + index);
					} else toRet = 0;
				} else {
					if(Pattern.matches("r-?[0-9]+[.][0-9]+", s) || Pattern.matches("r-?[0-9]+", s)) {
						s = s.substring(1);
						toRet = Float.parseFloat(s);
						toRet = (float) (toRet * Enemy.DEG_TO_RAD);
					}else{
						toRet = Float.parseFloat(s);
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return toRet;
		}


	}
}
