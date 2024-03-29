package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class MenuGeneral {

	KBinputHandler kbh;
	int selectedOption = 0;
	private int[] UDLRCCframesHeld = new int[] {0, 0, 0, 0, 2, 2}; // up, down, left, right, confirm, cancel
	private boolean[] activeKeys;
	protected byte menuDirection; // 0 = vertical, 1 = horizontal
	protected int menuEntries;
	protected Game parent;
	protected SoundManager smgr;
	
	public MenuGeneral(Game g, KBinputHandler kbh, SoundManager smgr) {
		this.kbh = kbh;
		parent = g;
		this.smgr = smgr;
		menuEntries = 3;
		menuDirection = 0;
	}
	
	
	public void setMenuLengthAndDirection(int len, byte direction) {
		menuEntries = len;
		menuDirection = direction;
	}
	public void activate() {
		selectedOption = 0;
		for(int i = 0; i < 6; i++) UDLRCCframesHeld[i] = 2;		
	}
	public void activate(int newOption) {
		selectedOption = newOption;
		for(int i = 0; i < 6; i++) UDLRCCframesHeld[i] = 2;		
	}
	public int getCurrOption() {
		return selectedOption;
	}
	public boolean isOnLastEntry() {
		return (selectedOption == (menuEntries - 1));
	}
	public void setLastEntry() {
		selectedOption = menuEntries - 1;
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
				smgr.playFromArray(1);
			}
			if(UDLRCCframesHeld[1] == 1 || (UDLRCCframesHeld[1] >= 60 && UDLRCCframesHeld[1] % 5 == 0)) {
				if(selectedOption == menuEntries - 1) selectedOption = 0;
				else selectedOption++;
				smgr.playFromArray(1);
			}
		}
		if(menuDirection == 1) {
			if(UDLRCCframesHeld[2] == 1 || (UDLRCCframesHeld[2] >= 60 && UDLRCCframesHeld[2] % 5 == 0)) {
				if(selectedOption == 0) selectedOption = menuEntries - 1;
				else selectedOption--;
				smgr.playFromArray(1);
			}
			if(UDLRCCframesHeld[3] == 1 || (UDLRCCframesHeld[3] >= 60 && UDLRCCframesHeld[3] % 5 == 0)) {
				if(selectedOption == menuEntries - 1) selectedOption = 0;
				else selectedOption++;
				smgr.playFromArray(1);
			}			
		}
		if(UDLRCCframesHeld[4] == 1) {
			smgr.playFromArray(3);
			doSelectedOption();
		}
		if(UDLRCCframesHeld[5] == 1) {
			smgr.playFromArray(2);
			onCancel();
		}
	}
	
	protected void onCancel() {
		parent.changeMenus(-1);		
	}
	
	
	public void doSelectedOption () {
		switch(selectedOption) {
		case 0:
			parent.changeMenus(1);
			break;
		case 2:
			System.exit(1);
		default:
			break;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		Font scoreFont = new Font("THBiolinum", Font.PLAIN, 48);
		g.setColor(Color.WHITE);
		g.setFont(scoreFont);
		g.drawString("ShmupCC showcase build", 50, 50);
		scoreFont = new Font("THbiolinum", Font.PLAIN, 24);
		g.setFont(scoreFont);
		g.drawString("Start", 100, 100);
		g.drawString("????", 100, 150);
		g.drawString("Exit", 100, 200);
		g.drawString(">", 90, 100 + 50 * selectedOption);
	}
}
