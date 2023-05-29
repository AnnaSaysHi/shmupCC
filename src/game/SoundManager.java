package game;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundManager implements LineListener {
	public static final int numSFX = 26;

	public SoundManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(LineEvent event) {
        if (LineEvent.Type.START == event.getType()) {
            System.out.println("Playback started.");
        } else if (LineEvent.Type.STOP == event.getType()) {
            System.out.println("Playback completed.");
        }
		
	}

}
