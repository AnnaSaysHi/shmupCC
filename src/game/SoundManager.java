package game;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class SoundManager{
	public static final int numSFX = 26;
	AudioInputStream audioStream;
	AudioCue[] sfxArray;

	public SoundManager() {
		sfxArray = new AudioCue[numSFX];
	}
	
	void init() {
		try {
			loadIntoArray(0, "/sfx/graze.wav");
			loadIntoArray(1, "/sfx/select00.wav");
			loadIntoArray(2, "/sfx/cancel00.wav");
			loadIntoArray(3, "/sfx/ok00.wav");
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}
	void loadIntoArray(int slot, String audioFilePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		URL url = this.getClass().getResource(audioFilePath);
		try {
			sfxArray[slot] = AudioCue.makeStereoCue(url, 1);
			sfxArray[slot].open();
			System.out.println(sfxArray[slot].getMicrosecondLength());
			sfxArray[slot].obtainInstance();
			sfxArray[slot].setVolume(0, 1);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	void playFromArray(int slot){
		try {
			sfxArray[slot].stop(0);
			sfxArray[slot].setFractionalPosition(0, 0);
			sfxArray[slot].start(0);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	

}
