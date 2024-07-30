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
	boolean shotHeld;
	boolean confirmHeld;
	boolean cancelHeld;
	boolean pauseHeld;
	boolean bombHeld;
	boolean restartHeld;
	boolean quitToMenuHeld;
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
		shotHeld = false;
		confirmHeld = false;
		cancelHeld = false;
		pauseHeld = false;
		bombHeld = false;
		restartHeld = false;
		quitToMenuHeld = false;
		LRdir = 0;
		UDdir = 0;
		
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
			break;
		case KeyEvent.VK_Z:
			confirmHeld = true;
			shotHeld = true;
			break;
		case KeyEvent.VK_X:
			cancelHeld = true;
			bombHeld = true;
			break;
		case KeyEvent.VK_R:
			restartHeld = true;
			break;
		case KeyEvent.VK_Q:
			quitToMenuHeld = true;
			break;
		case KeyEvent.VK_ESCAPE:
			pauseHeld = true;
			break;

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
			break;
		case KeyEvent.VK_Z:
			confirmHeld = false;
			shotHeld = false;
			break;
		case KeyEvent.VK_X:
			cancelHeld = false;
			bombHeld = false;
			break;
		case KeyEvent.VK_R:
			restartHeld = false;
			break;
		case KeyEvent.VK_Q:
			quitToMenuHeld = false;
			break;
		case KeyEvent.VK_ESCAPE:
			pauseHeld = false;
		default:
			break;			
		}
	}
	
	public boolean[] getHeldKeys() {
		boolean[] keyArray = new boolean[]{
				upHeld,				//0
				downHeld,			//1
				leftHeld,			//2
				rightHeld,			//3
				confirmHeld,		//4
				cancelHeld,			//5
				slowMovementHeld,	//6
				shotHeld,			//7
				pauseHeld,			//8
				bombHeld,			//9
				restartHeld,		//10
				quitToMenuHeld,		//11
				bKeyHeld			//12
		};
		return keyArray;
	}
	
	/**
	 * 
	 * @returns index 0: U/D, index 1: L/R
	 */
	public byte[] getDirections() {
		byte[] dirs = new byte[] {UDdir, LRdir};
		return dirs;
	}
}
