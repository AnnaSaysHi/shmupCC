package game.audio;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class SoundManager{
	public static final int numSFX = 26;
	public static final int No_Sound = -1;
	public static final int Select = 1;
	public static final int Cancel = 2;
	public static final int Confirm = 3;
	public static final int Graze = 4;
	public static final int Pichuun = 5;
	public static final int Explosion = 6;
	public static final int Flashbomb = 7;
	public static final int GaugeFull = 8;
	public static final int BulletDelete = 9;
	public static final int EnemyShootLoud = 10;
	public static final int EnemyShootMed = 11;
	public static final int EnemyShootMuted = 12;
	public static final int Sparkle = 13;
	AudioInputStream audioStream;
	AudioCue[] sfxArray;

	public SoundManager() {
		sfxArray = new AudioCue[numSFX];
	}
	
	public void init() {
		try {
			loadIntoArray(1, "/sfx/select00.wav");
			loadIntoArray(2, "/sfx/cancel00.wav");
			loadIntoArray(3, "/sfx/ok00.wav");
			loadIntoArray(4, "/sfx/graze.wav");
			loadIntoArray(5, "/sfx/pldead00.wav");
			loadIntoArray(6, "/sfx/deltaexplosion00.wav");
			loadIntoArray(7, "/sfx/bom_flash_01.wav");
			loadIntoArray(8, "/sfx/charge_up.wav");
			loadIntoArray(9, "/sfx/bullet_del.wav");
			loadIntoArray(10, "/sfx/tan00.wav");
			loadIntoArray(11, "/sfx/tan01.wav");
			loadIntoArray(12, "/sfx/tan02.wav");
			loadIntoArray(13, "/sfx/kira00.wav");
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
	void loadIntoArray(int slot, String audioFilePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		URL url = this.getClass().getResource(audioFilePath);
		try {
			sfxArray[slot] = AudioCue.makeStereoCue(url, 1);
			sfxArray[slot].open();
			sfxArray[slot].obtainInstance();
			sfxArray[slot].setVolume(0, 0.7);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	public void playFromArray(int slot){
		if(slot == No_Sound) return;
		try {
			sfxArray[slot].stop(0);
			sfxArray[slot].setFractionalPosition(0, 0);
			sfxArray[slot].start(0);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	

}
