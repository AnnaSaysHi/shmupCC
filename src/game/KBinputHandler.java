package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class KBinputHandler extends KeyAdapter {
	
	Game game;
	boolean bKeyHeld;
	
	
	public KBinputHandler(Game g) {
		game = g;
		bKeyHeld = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int kp = e.getKeyCode();
		if(kp == KeyEvent.VK_B) {
			bKeyHeld = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_B) {
			bKeyHeld = false;
		}

	}
	
	public boolean[] getHeldKeys() {
		boolean[] keyArray = new boolean[]{
				bKeyHeld
		};
		return keyArray;
	}

}
