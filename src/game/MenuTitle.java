package game;

import java.awt.Color;
import java.awt.Graphics;

public class MenuTitle {

	KBinputHandler kbh;
	int selectedOption = 0;
	private boolean isActive = false;
	private int[] UDLRCCframesHeld = new int[] {0, 0, 0, 0, 2, 2}; // up, down, left, right, confirm, cancel
	private boolean[] activeKeys;
	private byte menuDirection; // 0 = vertical, 1 = horizontal
	private int menuEntries;
	private Game parent;
	
	public MenuTitle(Game g, KBinputHandler kbh) {
		this.kbh = kbh;
		parent = g;
		menuEntries = 3;
		menuDirection = 0;
	}
	
	public void tick() {
		activeKeys = kbh.getHeldKeys();
		for(int i = 0; i < 6; i++) {
			if(activeKeys[i]) UDLRCCframesHeld[i] = UDLRCCframesHeld[i] + 1;
			else UDLRCCframesHeld[i] = 0;
		}
		if(menuDirection == 0) {
			if(UDLRCCframesHeld[0] == 1 || (UDLRCCframesHeld[0] >= 60 && UDLRCCframesHeld[0] % 5 == 0)) {
				if(selectedOption == 0) selectedOption = menuEntries - 1;
				else selectedOption--;
			}
			if(UDLRCCframesHeld[1] == 1 || (UDLRCCframesHeld[1] >= 60 && UDLRCCframesHeld[1] % 5 == 0)) {
				if(selectedOption == menuEntries - 1) selectedOption = 0;
				else selectedOption++;
			}
		}
		if(menuDirection == 1) {
			if(UDLRCCframesHeld[2] == 1 || (UDLRCCframesHeld[2] >= 60 && UDLRCCframesHeld[2] % 5 == 0)) {
				if(selectedOption == 0) selectedOption = menuEntries - 1;
				else selectedOption--;
			}
			if(UDLRCCframesHeld[3] == 1 || (UDLRCCframesHeld[3] >= 60 && UDLRCCframesHeld[3] % 5 == 0)) {
				if(selectedOption == menuEntries - 1) selectedOption = 0;
				else selectedOption++;
			}			
		}
		if(UDLRCCframesHeld[4] == 1) {
			doSelectedOption();
		}
	}
	
	
	public void doSelectedOption () {
		switch(selectedOption) {
		case 0:
			parent.state = Game.STATE.PLAY;
			break;
		case 2:
			System.exit(1);
		default:
			break;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString("Start", 100, 100);
		g.drawString("????", 100, 150);
		g.drawString("Exit", 100, 200);
		g.drawString(">", 90, 100 + 50 * selectedOption);
	}
}
