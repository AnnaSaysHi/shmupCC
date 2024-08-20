package game.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import game.Game;
import game.KBinputHandler;
import game.audio.SoundManager;

public class OptionSelector {
	KBinputHandler kbh;
	int selectedOption = 0;
	private int[] UDLRCCframesHeld = new int[] {0, 0, 2, 2}; // left, right, confirm, cancel
	private boolean[] activeKeys;
	protected int gvarIndex;
	protected int ypos;
	protected Game game;
	protected SoundManager smgr;
	protected MenuNew parentMenu;
	protected boolean active;
	protected ArrayList<Integer> entryXpos;
	protected ArrayList<String> entryNames;
	public OptionSelector(Game g, KBinputHandler kbh, SoundManager smgr, MenuNew parentMenu) {
		game = g;
		this.kbh = kbh;
		this.smgr = smgr;
		this.parentMenu = parentMenu;
		gvarIndex = 0;
		entryNames = new ArrayList<String>();
		entryXpos = new ArrayList<Integer>();
		ypos = 0;
		active = false;
	}
	public void setgvarIndex(int newIndex) {
		gvarIndex = newIndex;
		selectedOption = game.getGvar(gvarIndex);
	}
	public void setYpos(int newY) {
	ypos = newY;
	}
	public void addSubEntry(String entryText, int xpos) {
		entryNames.add(entryText);
		entryXpos.add(xpos);
	}
	public void tick() {
		activeKeys = kbh.getHeldKeys();
		for(int i = 0; i < 4; i++) {
			if(activeKeys[i + 2]) UDLRCCframesHeld[i] = UDLRCCframesHeld[i] + 1;
			else UDLRCCframesHeld[i] = 0;
		}
		if(UDLRCCframesHeld[0] == 1 || (UDLRCCframesHeld[0] >= 60 && UDLRCCframesHeld[0] % 5 == 0)) {
			if(selectedOption > 0) selectedOption--;
			smgr.playFromArray(1);
		}
		if(UDLRCCframesHeld[1] == 1 || (UDLRCCframesHeld[1] >= 60 && UDLRCCframesHeld[1] % 5 == 0)) {
			if(selectedOption < entryNames.size() - 1) selectedOption++;
			smgr.playFromArray(1);
		}	
		if(UDLRCCframesHeld[2] == 1) {
			smgr.playFromArray(3);
			game.setGvar(gvarIndex, selectedOption);
			parentMenu.reactivate();
			active = false;
		}
		if(UDLRCCframesHeld[3] == 1) {
			smgr.playFromArray(3);
			parentMenu.reactivate();
			active = false;
		}
	}
	protected void reactivate() {
		active = true;
		selectedOption = game.getGvar(gvarIndex);
		for (int i = 0; i < 4; i++) {
			UDLRCCframesHeld[i] = 2;
		}
	}
	
	public void render(Graphics g) {
		Font scoreFont = new Font("THbiolinum", Font.PLAIN, 24);
		Color oldColor = g.getColor();
		g.setFont(scoreFont);
		for(int i = 0; i < entryNames.size(); i++) {
			g.setColor(i == game.getGvar(gvarIndex) ? Color.WHITE : Color.GRAY);
			g.drawString(entryNames.get(i), entryXpos.get(i), ypos);
		}
		
		if(active) {
			g.setColor(Color.WHITE);
			g.drawString(">", entryXpos.get(selectedOption) - 10, ypos);
		}
		g.setColor(oldColor);
	}

}
