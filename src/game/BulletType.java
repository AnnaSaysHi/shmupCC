package game;

public abstract class BulletType {
	
	/*
	 * This class is only used for making enemy scripts more human-readable.
	 * If you want to use a non-default bullet spritesheet, please change the
	 * definitions in this class to match with your new bullet spritesheet.
	 */
	
	public static final int NUM_BULLET_TYPES = 11;
	
	public static final int LASER_PIECE = 0;
	public static final int ARROWHEAD = 1;
	public static final int OUTLINE = 2;
	public static final int BALL = 3;
	public static final int RICE = 4;
	public static final int KUNAI = 5;
	public static final int SHARD = 6;
	public static final int AMULET = 7;
	public static final int BULLET = 8;
	public static final int BACTERIA = 9;
	public static final int STAR_CW = 10;
}