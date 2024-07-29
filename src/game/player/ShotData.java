package game.player;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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
		try {
			InputStream shtFile = null;
			try {			
				shtFile = this.getClass().getResourceAsStream(fileName);
				readHeader(shtFile);
				int a = (int)(num_power_levels) * ((int)(num_power_levels) + 1) * 2;
				int b = (num_static_options & 127);
				numMaxOptions = (num_static_options == 0) ? (num_power_levels - 1) : b;
				b *= 4;
				numOptionOffsets = (num_static_options == 0) ? a : b;
				optionPos = new double[numOptionOffsets];
				readOptionPositions(shtFile);	
				readOffsetTable(shtFile);
				readAllShooters(shtFile);
			}catch(IOException e) {
				e.printStackTrace();
			}finally {
				shtFile.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private void readHeader(InputStream shtFile) throws IOException {
		byte[] byteBuf8 = new byte[8];
		shtFile.readNBytes(byteBuf8, 0, 8);
		ByteBuffer bb8 = ByteBuffer.wrap(byteBuf8);
		version_num = bb8.getShort(0);
		if(version_num != shot_reader_version) throw new IOException (".sht file version does not match reader version " + shot_reader_version);
		numOffsets = bb8.getShort(2);
		dmg_cap_type = bb8.getShort(4);
		dmg_cap_val = bb8.getShort(6);
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		hitboxSize = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		grazeboxSize = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		itemboxSize = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		move_UF = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		move_F = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		option_behavior = bb8.getShort(0);
		num_power_levels = bb8.get(2);
		deathbomb_window = bb8.get(3);
		optionMoveTime = bb8.get(4);
		num_static_options = bb8.get(5);
		unused1 = bb8.get(6);
		unused2 = bb8.get(7);
	}
	private void readOptionPositions(InputStream shtFile) throws IOException{
		byte[] byteBuf8 = new byte[8];
		ByteBuffer bb8;
		for(int i = 0; i < numOptionOffsets; i++) {
			shtFile.readNBytes(byteBuf8, 0, 8);
			bb8 = ByteBuffer.wrap(byteBuf8);
			optionPos[i] = bb8.getDouble();
		}
	}
	private void readOffsetTable(InputStream shtFile) throws IOException{
		byte[] byteBuf2 = new byte[2];
		ByteBuffer bb2;
		shootersetOffsetList = new short[numOffsets];
		for(int i = 0; i < numOffsets; i++) {
			shtFile.readNBytes(byteBuf2, 0, 2);
			bb2 = ByteBuffer.wrap(byteBuf2);
			shootersetOffsetList[i] = bb2.getShort();
		}
	}
	private void readAllShooters(InputStream shtFile) throws IOException{
		shooterSetArray = new PlayerShooter[numOffsets][];
		for(int i = 0; i < numOffsets; i++) {
			int numShootersInSet = shootersetOffsetList[i];
			PlayerShooter[] toRet = new PlayerShooter[numShootersInSet];
			for(int j = 0; j < numShootersInSet; j++) {
				toRet[j] = readShooter(shtFile);
			}
			shooterSetArray[i] = toRet;
		}
		int a = 0;
	}
	private PlayerShooter readShooter(InputStream shtFile) throws IOException{
		byte[] byteBuf8 = new byte[8];
		ByteBuffer bb8;
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		short fireRate = bb8.getShort(0);
		short startDelay = bb8.getShort(2);
		int damage = bb8.getInt(4);
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		double off_x = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		double off_y = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		double hitbox = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		double angle = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		double speed = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		double size = bb8.getDouble();
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		byte option = bb8.get(0);
		byte anim = bb8.get(1);
		byte animHit = bb8.get(2);
		byte sfx = bb8.get(3);
		int func_init = bb8.getInt(4);
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		int func_tick = bb8.getInt(0);
		int func_draw = bb8.getInt(4);
		shtFile.readNBytes(byteBuf8, 0, 8);
		bb8 = ByteBuffer.wrap(byteBuf8);
		int func_hit = bb8.getInt(0);
		int unusedSht = bb8.getInt(4);
		return new PlayerShooter(fireRate, startDelay, damage, off_x, off_y, hitbox, angle, speed, size, option, anim);
	}
}
