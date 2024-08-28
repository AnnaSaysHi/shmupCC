package game.player;

public class ShotData {
	final short shot_reader_version = 1; //for comparison
	
	//Header
	short version_num;
	short numOffsets;
	short dmg_cap_type; //for games that intend to support different damage cap implementations per shot type.
	short dmg_cap_val;
	double hitboxSize;
	double grazeboxSize;
	double itemboxSize;
	double move_UF;
	double move_F;
	short option_behavior; //each bit set in this field will enable a special behavior for the shot's options (e.g. tracing, no-render, etc)
	byte num_power_levels;
	byte deathbomb_window;
	byte optionMoveTime;
	byte num_static_options; // See notes below
	byte unused1; // Purely here to make the header take up a multiple of 64 bits.
	byte unused2; // These fields may end up used later on but I couldn't think of a use for them.
	/* 
	 * format notes for static options byte
	 * This byte is of format ANNNNNNN
	 * A = options are active at 0 power
	 * N = number of options, unsigned byte
	 */
	int numOptionOffsets;
	int numMaxOptions;
	double[] optionPos;
	short[] shootersetOffsetList;
	PlayerShooter[][] shooterSetArray;
	
	public ShotData() {
		// TODO Auto-generated constructor stub
	}
	public double[] getPlayerAttributesFloat(){
		return new double[] {
				move_UF,
				move_F,
				hitboxSize,
				grazeboxSize,
				itemboxSize
		};
	}
	public int[] getPlayerAttributesInt() {
		return new int[] {
				dmg_cap_type,
				dmg_cap_val,
				deathbomb_window,
				option_behavior,
				numMaxOptions,
				optionMoveTime
		};
	}
	
	public int getMaxOptions() {
		return numMaxOptions;
	}
	public double[] getOptionPositions(int powerLevel, int config) {

		if(num_static_options == 0) {
			int a = powerLevel * 2;
			int b = num_power_levels;
			b = b * (b + 1);
			int c = powerLevel * (powerLevel + 1);
			int offset = (b * config) + c;
			double[] toReturn = new double[a];
			for(int i = 0; i < a; i++) {
				toReturn[i] = optionPos[i + offset];
			}
			return toReturn;	
		}else {
			if((num_static_options & 0x80) == 0 && powerLevel == 0) return new double[0];
			int a = num_static_options & 127;
			a *= 2;
			double[] toReturn = new double[a];
			int offset = config * a;
			for(int i = 0; i < a; i++) {
				toReturn[i] = optionPos[i + offset];
			}
			return toReturn;
		}
	}
	
	public void assignShootersToPlayer(PlayerShotManager parentManager, Player parentPlayer) {
		for(PlayerShooter[] s : shooterSetArray) {
			for(PlayerShooter t : s) t.assignPlayerAndManager(parentManager, parentPlayer);
		}
	}


	public void tickShooterSet(int setNum, int timer) {
		for(PlayerShooter s : shooterSetArray[setNum]) s.tickShooter(timer);
	}
	
	
	public void getShotInfoFromFile(String fileName){
		
		initHeaderHardCoded();
		int a = (int)(num_power_levels) * ((int)(num_power_levels) + 1) * 2;
		int b = (num_static_options & 127);
		numMaxOptions = (num_static_options == 0) ? (num_power_levels - 1) : b;
		b *= 4;
		numOptionOffsets = (num_static_options == 0) ? a : b;
		optionPos = new double[numOptionOffsets];
		initOptionPositionsHardCoded();
		initAllShootersHardCoded();
	}

	private void initHeaderHardCoded() {
		version_num = 1;
		numOffsets = 2;
		dmg_cap_type = 1;
		dmg_cap_val = 80;
		hitboxSize = 3;
		grazeboxSize = 20;
		itemboxSize = 28;
		move_UF = 4.5;
		move_F = 2;
		option_behavior = 0;
		num_power_levels = 1;
		deathbomb_window = 8;
		optionMoveTime = 10;
		num_static_options = -126;
		unused1 = 0;
		unused2 = 0;
	}

	private void initOptionPositionsHardCoded() {
		optionPos = new double[]{
			-32, 0,			
			32, 0,
			-16, -20,
			16, -20
		};
	}


	private void initAllShootersHardCoded() {
		shooterSetArray = new PlayerShooter[2][5];
		shooterSetArray[0][0] = new PlayerShooter((short)5, (short)0, 16, 0, 0, 16, Math.toRadians(-90), 10, 8, (byte)0, (byte)0);
		shooterSetArray[0][1] = new PlayerShooter((short)5, (short)0, 8, 0, 0, 16, Math.toRadians(-85), 10, 8, (byte)0, (byte)0);
		shooterSetArray[0][2] = new PlayerShooter((short)5, (short)0, 8, 0, 0, 16, Math.toRadians(-95), 10, 8, (byte)0, (byte)0);
		shooterSetArray[0][3] = new PlayerShooter((short)5, (short)0, 8, 0, 0, 16, Math.toRadians(-100), 10, 8, (byte)1, (byte)0);
		shooterSetArray[0][4] = new PlayerShooter((short)5, (short)0, 8, 0, 0, 16, Math.toRadians(-80), 10, 8, (byte)2, (byte)0);
		shooterSetArray[1][0] = new PlayerShooter((short)5, (short)0, 16, 0, 0, 16, Math.toRadians(-90), 10, 8, (byte)0, (byte)0);
		shooterSetArray[1][1] = new PlayerShooter((short)5, (short)0, 8, 0, 0, 16, Math.toRadians(-85), 10, 8, (byte)0, (byte)0);
		shooterSetArray[1][2] = new PlayerShooter((short)5, (short)0, 8, 0, 0, 16, Math.toRadians(-95), 10, 8, (byte)0, (byte)0);
		shooterSetArray[1][3] = new PlayerShooter((short)5, (short)0, 8, 0, 0, 16, Math.toRadians(-90), 10, 8, (byte)1, (byte)0);
		shooterSetArray[1][4] = new PlayerShooter((short)5, (short)0, 8, 0, 0, 16, Math.toRadians(-90), 10, 8, (byte)2, (byte)0);
		
	}

}
