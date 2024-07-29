package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import game.Game.STATE;

public class MenuEntry {
	Game parent;
	MenuManager mmgr;
	boolean textOrImage; //0 = text, 1 = image
	String selfText;
	int behavior; // 0 = go to linked menu, 1 = set some global var
	int behaviorArg1; // depends on behavior
	int behaviorArg2; //
	int xpos;
	int ypos;
	
	
	public MenuEntry(Game parent, MenuManager mmgr, String text, int behavior, int behaviorArg1, int behaviorArg2, int x, int y) {
		this.parent = parent;
		this.mmgr = mmgr;
		this.selfText = text;
		this.behavior = behavior;
		this.behaviorArg1 = behaviorArg1;
		this.behaviorArg2 = behaviorArg2;
		this.xpos = x;
		this.ypos = y;
	}
	
	//For non-selectable entries only (to help with rendering).
	public MenuEntry(Game parent, String text, int x, int y) {
		this.parent = parent;
		this.selfText = text;
		this.xpos = x;
		this.ypos = y;
	}
	
	public int getXpos() {
		return xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public String getText() {
		return selfText;
	}
	
	public void onSelect() {
		switch(behavior) {
		case BHV_CHANGE_MENU:
			mmgr.switchActiveMenu(behaviorArg1);
			break;
		case BHV_EXIT_GAME:
			System.exit(0);
			break;
		case BHV_START_SCENE:
			parent.setStage(behaviorArg1);
			this.parent.state = Game.STATE.PLAY;
			break;
		case BHV_UNPAUSE:
			if(parent.state != STATE.GAME_OVER)parent.unpause();
			break;
		case BHV_START_OVER:
			parent.restartStage();
			break;
		case BHV_RETURN_TO_MENU:
			parent.returnToMenu();
			break;
		default:
			
		}
	}
	
	public void render(Graphics g) {
		Font scoreFont = new Font("THbiolinum", Font.PLAIN, 24);
		g.setColor(Color.WHITE);
		g.setFont(scoreFont);
		g.drawString(selfText, xpos, ypos);
	}
	
	/*
	 ********************************* 
	 *  ~~LIST OF BEHAVIORS BELOW~~  *
	 *********************************
	 */
	
	public static final int BHV_CHANGE_MENU = 0;
	public static final int BHV_SET_GVAR = 1;
	public static final int BHV_EXIT_GAME = 2;
	public static final int BHV_START_SCENE = 3;
	public static final int BHV_UNPAUSE = 4;
	public static final int BHV_START_OVER = 5;
	public static final int BHV_RETURN_TO_MENU = 6;

}
