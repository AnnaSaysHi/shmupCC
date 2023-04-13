package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class KBinputHandler extends KeyAdapter {
	
	Game game;
	boolean bKeyHeld;
	boolean upHeld;
	boolean downHeld;
	boolean leftHeld;
	boolean rightHeld;
	boolean slowMovementHeld;
	
	
	public KBinputHandler(Game g) {
		game = g;
		bKeyHeld = false; //b key activates breakpoints and is used for debugging only by default
		upHeld = false;
		downHeld = false;
		leftHeld = false;
		rightHeld = false;
		slowMovementHeld = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_B:
			bKeyHeld = true;
			break;
		case KeyEvent.VK_UP:
			upHeld = true;
			downHeld = false;
			break;
		case KeyEvent.VK_DOWN:
			downHeld = true;
			upHeld = false;
			break;
		case KeyEvent.VK_LEFT:
			leftHeld = true;
			rightHeld = false;
			break;
		case KeyEvent.VK_RIGHT:
			rightHeld = true;
			leftHeld = false;
			break;	
		case KeyEvent.VK_SHIFT:
			slowMovementHeld = true;
		default:
			break;			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {

		case KeyEvent.VK_B:
			bKeyHeld = false;
			break;
		case KeyEvent.VK_UP:
			upHeld = false;
			break;
		case KeyEvent.VK_DOWN:
			downHeld = false;
			break;
		case KeyEvent.VK_LEFT:
			leftHeld = false;
			break;
		case KeyEvent.VK_RIGHT:
			rightHeld = false;
			break;		
		case KeyEvent.VK_SHIFT:
			slowMovementHeld = false;	
		default:
			break;			
		}
	}
	
	public boolean[] getHeldKeys() {
		boolean[] keyArray = new boolean[]{
				upHeld,
				downHeld,
				leftHeld,
				rightHeld,
				slowMovementHeld,
				bKeyHeld
		};
		return keyArray;
	}

}
