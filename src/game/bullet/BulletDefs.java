package game.bullet;
/**
 * This class is used to define the properties of bullets.
 * If you want to use a non-default bullet spritesheet, please change the
 * definitions in this class to match with your new bullet spritesheet.
 */
public abstract class BulletDefs {
	
	public static final int NUM_BULLET_SPRITES = 16;
	public static final int[] BULLET_SPRITESHEET_16x16_START_POSITIONS_SIZE = new int[] {
			0, 240, 8,		//PELLET
			0, 16, 16,		//ARROWHEAD
			0, 32, 16,		//OUTLINE
			0, 48, 16,		//BALL
			0, 64, 16,		//RICE
			0, 80, 16,		//KUNAI
			0, 96, 16,		//SHARD
			0, 112, 16,		//AMULET
			0, 128, 16,		//BULLET
			0, 144, 16,		//BACTERIA
			0, 160, 16,		//STAR_SMALL
			0, 176, 16,		//DROPLET		
			0, 192, 8,		//POPCORN
			256, 32, 32,	//MENTOS
			256, 0, 32,		//STAR_BIG
			256, 128, 32	//JELLYBEAN
	};
	
	public static final int[] BULLET_SPRITESHEET_INDEX = new int[] {
			0,
			1,
			2,
			3,
			4,
			5,
			6,
			7,
			8,
			9,
			10,
			10,
			11,
			12,
			12,
			13,
			14,
			14,
			15
	};
	
	public static final double[] BULLET_RENDER_SIZE = new double[] {
			8,
			16,
			16,
			16,
			16,
			16,
			16,
			16,
			16,
			16,
			16,
			16,
			16,
			8,
			8,
			32,
			32,
			32,
			32
	};
	
	public static final double[] BULLET_HITBOX_SIZE = new double[] {
			1.5,
			1.5,
			3.5,
			3.75,
			2.25,
			3,
			2,
			3,
			2.5,
			2.5,
			2,
			2,
			2.25,
			1.5,
			1.5,
			7,
			5,
			5,
			4.5
	};
	
	public static final byte[] BULLET_ROTATION_MODE = new byte[] {
			1,
			0,
			1,
			1,
			0,
			0,
			0,
			0,
			0,
			0,
			2,
			3,
			0,
			2,
			3,
			1,
			2,
			3,
			0	
	};
	
}
