package game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ShotData {
	short version_num;
	short numOffsets;
	short dmg_cap_type; //for games that intend to support different damage cap implementations per shot type.
	short dmg_cap_val;
	double hitboxSize;
	double grazeboxSize;
	double itemboxSize;
	double move_UF;
	double move_F;
	short num_power_levels;
	short option_behavior; //each bit set in this field will enable a special behavior for the shot's options (e.g. tracing, no-render, etc)
	byte deathbomb_window;
	byte optionMoveTime;
	byte num_static_options; // See notes below
	byte unused1; //purely here to make the header take up a multiple of 64 bits.
	/* 
	 * format notes for static options byte
	 * This byte is of format ANNNNNNN
	 * A = options are active at 0 power
	 * N = number of options, unsigned byte
	 */
	int numOptionOffsets;
	double[] optionPos;
	
	public ShotData() {
		// TODO Auto-generated constructor stub
	}
	
	public void getShotInfoFromFile(String fileName) throws IOException{
		InputStream shtFile = this.getClass().getResourceAsStream(fileName);
		readHeader(shtFile);
		int a = num_power_levels * (num_power_levels + 1) * 2;
		int b = (num_static_options & 127);
		b *= 4;
		numOptionOffsets = (num_static_options == 0) ? a : b;
		optionPos = new double[numOptionOffsets];
		readOptionPositions(shtFile);
		shtFile.close();
	}
	private void readHeader(InputStream shtFile) throws IOException {
		byte[] byteBuf8 = new byte[8];
		shtFile.readNBytes(byteBuf8, 0, 8);
		ByteBuffer bb8 = ByteBuffer.wrap(byteBuf8);
		version_num = bb8.getShort(0);
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
		num_power_levels = bb8.getShort(0);	
		option_behavior = bb8.getShort(2);
		deathbomb_window = bb8.get(4);
		optionMoveTime = bb8.get(5);
		num_static_options = bb8.get(6);
		unused1 = bb8.get(7);
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
	
}
