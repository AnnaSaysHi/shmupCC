package game.bullet;
/**
 * This class is only used for making enemy scripts more human-readable.
 * If you want to use a non-default bullet spritesheet, please change the
 * definitions in this class to match with your new bullet spritesheet.
 */
public abstract class BulletDefs {
	
	public static final int NUM_BULLET_TYPES = 11;
	
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
			10
	};
	
	public static final double[] BULLET_RENDER_SIZE = new double[] {
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
			16
	};
	public static final double[] BULLET_HITBOX_SIZE = new double[] {
			8,
			1.5,
			3.5,
			3.75,
			2.5,
			3,
			2,
			3,
			2.5,
			2.5,
			2,
			2
	};
	public static final byte[] BULLET_ROTATION_MODE = new byte[] {
			0,
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
			3
	};
	
}
