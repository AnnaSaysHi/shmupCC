package game;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class MenuNew {
	KBinputHandler kbh;
	int selectedOption = 0;
	private int[] UDLRCCframesHeld = new int[] {0, 0, 0, 0, 2, 2}; // up, down, left, right, confirm, cancel
	private boolean[] activeKeys;
	protected byte menuDirection; // 0 = vertical, 1 = horizontal
	protected int menuEntries;
	protected Game parent;
	protected SoundManager smgr;
	protected MenuManager mmgr;
	protected ArrayList<MenuEntry> entries;
	
	public MenuNew(Game g, KBinputHandler kbh, SoundManager smgr, MenuManager mmgr) {
		this.kbh = kbh;
		parent = g;
		this.smgr = smgr;
		this.mmgr = mmgr;
		menuEntries = 0;
		entries = new ArrayList<MenuEntry>();
	}
	
	public void addNewEntry(MenuEntry e) {
		entries.add(menuEntries, e);
		menuEntries++;
	}
	public void addNewEntry(String text, int behavior, int behaviorArg1, int behaviorArg2, int xpos, int ypos) {
		entries.add(menuEntries, new MenuEntry(parent, mmgr, text, behavior, behaviorArg1, behaviorArg2, xpos, ypos));
		menuEntries++;
	}
	//Everything that gets rendered is technically an entry,
	//so this is for anything that needs to be rendered that shouldn't be an entry.
	public void addUnselectableEntry(MenuEntry e) {
		entries.add(e);
	}
	public void addUnselectableEntry(String text, int xpos, int ypos) {
		entries.add(new MenuEntry(parent, text, xpos, ypos));
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
			entries.get(selectedOption).onSelect();
		}
	}
	
	public void render(Graphics g) {
		Font scoreFont = new Font("THbiolinum", Font.PLAIN, 24);
		g.setColor(Color.WHITE);
		g.setFont(scoreFont);
		for(MenuEntry e : entries) {
			g.drawString(e.getText(), e.getXpos(), e.getYpos());
		}
		
		g.drawString(">", entries.get(selectedOption).getXpos() - 10, entries.get(selectedOption).getYpos());
				
		//for (MenuEntry e : entries) e.render(g);
	}

}
