package game.menu;

import java.awt.Graphics;
import java.util.Stack;

import game.Game;
import game.KBinputHandler;
import game.audio.SoundManager;

public class MenuManager {

	Game game;
	KBinputHandler kbh;
	protected SoundManager smgr;
	Stack<Integer> menuStack;
	public final int MAX_MENU_DEPTH = 10;
	private int currentMenu = 0;
	private MenuNew[] menuList;
	private MenuNew pauseMenu;
	
	public MenuManager(Game game, KBinputHandler kbh, SoundManager SoundMGR) {
		this.game = game;
		this.kbh = kbh;
		smgr = SoundMGR;
		menuList = new MenuNew[2];
	}
	
	public void initMenusFromTextFile() {
		//TODO
	}
	
	public void initMenusHardCoded() {
		pauseMenu = new MenuNew(this.game, kbh, smgr, this);
		pauseMenu.addNewEntry("Unpause", MenuEntry.BHV_UNPAUSE, 0, 0, 100, 100);
		pauseMenu.addNewEntry("Restart", MenuEntry.BHV_START_OVER, 0, 0, 100, 150);
		pauseMenu.addNewEntry("Return to Title", MenuEntry.BHV_RETURN_TO_MENU, 0, 0, 100, 200);
		menuList[0] = new MenuNew(this.game, kbh, smgr, this);
		menuList[0].addNewEntry("Start", MenuEntry.BHV_START_SCENE, 1, 0, 100, 100);
		menuList[0].addNewEntry("Exit", MenuEntry.BHV_EXIT_GAME, 0, 0, 100, 200);
	}
	
	public void tick() {
		menuList[currentMenu].tick();
	}
	
	public void switchActiveMenu(int switchTo) {
		//TODO
	}
	
	public void tickPauseMenu() {
		//pauseMenuOld.tick();
		pauseMenu.tick();
	}
	public void renderCurrentMenu(Graphics g) {
		menuList[currentMenu].render(g);
	}
	public void renderPauseMenu(Graphics g) {
		pauseMenu.render(g);
	}
	

}
