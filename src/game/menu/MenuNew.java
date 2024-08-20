package game.menu;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import game.Game;
import game.KBinputHandler;
import game.audio.SoundManager;

public class MenuNew {
	KBinputHandler kbh;
	int selectedOption = 0;
	private int[] UDLRCCframesHeld = new int[] {0, 0, 0, 0, 2, 2}; // up, down, left, right, confirm, cancel
	private boolean[] activeKeys;
	protected byte menuDirection; // 0 = vertical, 1 = horizontal
	protected int menuEntries;
	protected int activeSubmenu;
	protected ArrayList<OptionSelector> submenus;
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
		submenus = new ArrayList<OptionSelector>();
		activeSubmenu = -1;
	}
	
	
	public void addNewEntry(String text, int behavior, int behaviorArg1, int xpos, int ypos) {
		entries.add(menuEntries, new MenuEntry(parent, mmgr, text, behavior, behaviorArg1, xpos, ypos));
		menuEntries++;
	}
	public void addSubMenu(String text, OptionSelector subMenu, int xpos, int ypos) {
		entries.add(menuEntries, new MenuEntry(parent, mmgr, text, MenuEntry.BHV_SUBMENU, submenus.size(), xpos, ypos));
		submenus.add(subMenu);
		menuEntries++;
	}
	//Everything that gets rendered is technically an entry,
	//so this is for anything that needs to be rendered that shouldn't be an entry.

	public void addUnselectableEntry(String text, int xpos, int ypos) {
		entries.add(new MenuEntry(parent, text, xpos, ypos));
	}
	public void setArgsOfEntry(int arg2, int arg3, int arg4) {
		entries.get(menuEntries - 1).setExtendedArguments(arg2, arg3, arg4);
	}
	
	public void tick() {
		if (activeSubmenu == -1) {
			activeKeys = kbh.getHeldKeys();
			for (int i = 0; i < 6; i++) {
				if (activeKeys[i])
					UDLRCCframesHeld[i] = UDLRCCframesHeld[i] + 1;
				else
					UDLRCCframesHeld[i] = 0;
			}
			if (menuDirection == 0) {
				if (UDLRCCframesHeld[0] == 1 || (UDLRCCframesHeld[0] >= 60 && UDLRCCframesHeld[0] % 5 == 0)) {
					if (selectedOption == 0)
						selectedOption = menuEntries - 1;
					else
						selectedOption--;
					smgr.playFromArray(1);
				}
				if (UDLRCCframesHeld[1] == 1 || (UDLRCCframesHeld[1] >= 60 && UDLRCCframesHeld[1] % 5 == 0)) {
					if (selectedOption == menuEntries - 1)
						selectedOption = 0;
					else
						selectedOption++;
					smgr.playFromArray(1);
				}
			}
			if (menuDirection == 1) {
				if (UDLRCCframesHeld[2] == 1 || (UDLRCCframesHeld[2] >= 60 && UDLRCCframesHeld[2] % 5 == 0)) {
					if (selectedOption == 0)
						selectedOption = menuEntries - 1;
					else
						selectedOption--;
					smgr.playFromArray(1);
				}
				if (UDLRCCframesHeld[3] == 1 || (UDLRCCframesHeld[3] >= 60 && UDLRCCframesHeld[3] % 5 == 0)) {
					if (selectedOption == menuEntries - 1)
						selectedOption = 0;
					else
						selectedOption++;
					smgr.playFromArray(1);
				}
			}
			if (UDLRCCframesHeld[4] == 1) {
				smgr.playFromArray(3);
				if(entries.get(selectedOption).getBehavior() != MenuEntry.BHV_SUBMENU) {
					entries.get(selectedOption).onSelect();
				}else {
					activeSubmenu = entries.get(selectedOption).behaviorArg1;
					submenus.get(activeSubmenu).reactivate();
				}
			}
			//cancel routine
			if (UDLRCCframesHeld[5] == 1) {
				smgr.playFromArray(3);

				//Check if we are the pause menu before proceeding; this is done by checking if option 0 is "Unpause"
				if (entries.get(0).getBehavior() == MenuEntry.BHV_UNPAUSE) {
					selectedOption = 0;
					entries.get(selectedOption).onSelect();
				} //Next, check if we are in a menu that lets us quit the game and we aren't already on the "quit" option
				else if (entries.get(menuEntries - 1).getBehavior() == MenuEntry.BHV_EXIT_GAME
						&& selectedOption != menuEntries - 1) {
					selectedOption = menuEntries - 1;
				} //Return to previous menu
				else
					mmgr.switchActiveMenu(-1);
			} 
		} else {
			submenus.get(activeSubmenu).tick();
		}
	}
	protected void reactivate() {
		for (int i = 0; i < 6; i++) {
			UDLRCCframesHeld[i] = 2;
		}
		activeSubmenu = -1;
	}
	protected void reactivate(int index) {
		for (int i = 0; i < 6; i++) {
			UDLRCCframesHeld[i] = 2;
		}
		try {
			setSelectedEntry(index);			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		activeSubmenu = -1;
	}
	
	//Used by MenuManager to restore or reset the cursor position of a menu that was deactivated.
	public void setSelectedEntry(int index) throws Exception{
		if(index >= menuEntries || index < 0) throw new Exception("Attempted out of bounds access");
		else selectedOption = index;
	}
	
	public void render(Graphics g) {
		Font scoreFont = new Font("THbiolinum", Font.PLAIN, 24);
		g.setColor(Color.WHITE);
		g.setFont(scoreFont);
		for(MenuEntry e : entries) {
			g.drawString(e.getText(), e.getXpos(), e.getYpos());
		}
		for(OptionSelector e : submenus) {
			e.render(g);
		}
		
		if(activeSubmenu == -1)g.drawString(">", entries.get(selectedOption).getXpos() - 10, entries.get(selectedOption).getYpos());
	}

}
