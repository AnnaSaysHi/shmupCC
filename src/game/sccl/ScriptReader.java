package game.sccl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ScriptReader {

	final short script_reader_version = 1;
	short scriptVersion;
	short numberSubs;
	int[] subStartIndices;
	ByteBuffer scriptBytes;
	
	
	public ScriptReader() {
		// TODO Auto-generated constructor stub
	}
	public void getScriptFromFile(String fileName) {
		try {
			InputStream scriptFile = null;
			try {
				scriptFile = this.getClass().getResourceAsStream(fileName);
				readHeader(scriptFile);
				scriptBytes = ByteBuffer.wrap(scriptFile.readAllBytes());
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				scriptFile.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void readHeader(InputStream scriptFile) throws IOException, SCCLexception{
		byte[] byteBuf4 = new byte[4];
		scriptFile.readNBytes(byteBuf4, 0, 4);
		ByteBuffer bb4 = ByteBuffer.wrap(byteBuf4);
		scriptVersion = bb4.getShort(0);
		if(scriptVersion != script_reader_version) throw new SCCLexception (".sccl file version " + scriptVersion + " does not match reader version " + script_reader_version);
		numberSubs = bb4.getShort(2);
		if(numberSubs == 0) throw new SCCLexception("Zero subroutines, possible corrupted script file");
		subStartIndices = new int[numberSubs];
		for(short i = 0; i < numberSubs; i++) {
			scriptFile.readNBytes(byteBuf4, 0, 4);
			subStartIndices[i] = ByteBuffer.wrap(byteBuf4).getInt();
		}
	}
	public int getIntAtPos(int subID, int position) {
		if(subID >= numberSubs) return 0;
		int adjustedPosition = (subStartIndices[subID] + position) * 4;
		int toRet = 0;
		try {
			toRet = scriptBytes.getInt(adjustedPosition);
		}catch(IndexOutOfBoundsException e) {
			toRet = 0;
			e.printStackTrace();			
		}
		return toRet;
	}
	public float getFloatAtPos(int subID, int position) {
		if(subID >= numberSubs) return 0;
		int adjustedPosition = (subStartIndices[subID] + position) * 4;
		float toRet = 0;
		try {
			toRet = scriptBytes.getFloat(adjustedPosition);
		}catch(IndexOutOfBoundsException e) {
			toRet = 0;
			e.printStackTrace();			
		}
		return toRet;
	}
}
