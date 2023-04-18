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
	byte LRdir; // 0 = neutral; 1 = left; 2 = right
	byte UDdir; // 0 = neutral; 1 = up; 2 = down
	
	
	public KBinputHandler(Game g) {
		game = g;
		bKeyHeld = false; //b key activates breakpoints and is used for debugging only by default
		upHeld = false;
		downHeld = false;
		leftHeld = false;
		rightHeld = false;
		slowMovementHeld = false;
		LRdir = 0;
		UDdir = 1;
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_B:
			bKeyHeld = true;
			break;
		case KeyEvent.VK_UP:
			upHeld = true;
			UDdir = 1;
			break;
		case KeyEvent.VK_DOWN:
			downHeld = true;
			UDdir = 2;
			break;
		case KeyEvent.VK_LEFT:
			leftHeld = true;
			LRdir = 1;
			break;
		case KeyEvent.VK_RIGHT:
			rightHeld = true;
			LRdir = 2;
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
			UDdir = (byte) ((downHeld) ? 2 : 0);
			break;
		case KeyEvent.VK_DOWN:
			downHeld = false;
			UDdir = (byte) ((upHeld) ? 1 : 0);
			break;
		case KeyEvent.VK_LEFT:
			leftHeld = false;
			LRdir = (byte) ((rightHeld) ? 2 : 0);
			break;
		case KeyEvent.VK_RIGHT:
			rightHeld = false;
			LRdir = (byte) ((leftHeld) ? 1 : 0);
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
	public byte[] getDirections() {
		byte[] dirs = new byte[] {UDdir, LRdir};
		return dirs;
	}
}
