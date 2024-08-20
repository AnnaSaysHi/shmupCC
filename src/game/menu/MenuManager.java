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
		menuStack = new Stack<>();
	}
	
	public void initMenus() {
		menuList = new MenuNew[3];
		pauseMenu = new MenuNew(this.game, kbh, smgr, this);
		pauseMenu.addNewEntry("Unpause", MenuEntry.BHV_UNPAUSE, 0, 100, 100);
		pauseMenu.addNewEntry("Restart", MenuEntry.BHV_START_OVER, 0, 100, 150);
		pauseMenu.addNewEntry("Return to Title", MenuEntry.BHV_RETURN_TO_MENU, 0, 100, 200);
		menuList[0] = new MenuNew(this.game, kbh, smgr, this);
		menuList[0].addNewEntry("Level Select", MenuEntry.BHV_CHANGE_MENU, 1, 100, 100);
		menuList[0].addNewEntry("Options", MenuEntry.BHV_CHANGE_MENU, 2, 100, 150);
		menuList[0].addNewEntry("Exit", MenuEntry.BHV_EXIT_GAME, 0, 100, 200);
		
		//SCENE SELECT MENU
		menuList[1] = new MenuNew(this.game, kbh, smgr, this);
		menuList[1].addNewEntry("Meek", MenuEntry.BHV_START_SCENE, 0, 100, 100);
		menuList[1].addNewEntry("BoWaP", MenuEntry.BHV_START_SCENE, 1, 100, 150);
		menuList[1].addNewEntry("VIV_test", MenuEntry.BHV_START_SCENE, 2, 100, 200);
		menuList[1].addNewEntry("test1-4", MenuEntry.BHV_START_SCENE, 3, 100, 250);
		menuList[1].addNewEntry("Necropotence", MenuEntry.BHV_START_SCENE, 4, 100, 300);
		menuList[1].addNewEntry("bha8 test", MenuEntry.BHV_START_SCENE, 5, 100, 350);
		
		//OPTIONS MENU
		
		menuList[2] = new MenuNew(this.game, kbh, smgr, this);
		OptionSelector infLivesSubmenu = new OptionSelector(this.game, kbh, smgr, menuList[2]);
		infLivesSubmenu.setgvarIndex(MenuEntry.GVAR_INFINITE_LIVES);
		infLivesSubmenu.setYpos(130);
		infLivesSubmenu.addSubEntry("Off", 100);
		infLivesSubmenu.addSubEntry("On", 150);
		menuList[2].addSubMenu("Infinite Lives", infLivesSubmenu, 100, 100);
		OptionSelector difficultySubmenu = new OptionSelector(this.game, kbh, smgr, menuList[2]);
		difficultySubmenu.setgvarIndex(MenuEntry.GVAR_DIFFICULTY);
		difficultySubmenu.setYpos(205);
		difficultySubmenu.addSubEntry("Nerfed", 100);
		difficultySubmenu.addSubEntry("Original", 175);
		menuList[2].addSubMenu("Difficulty", difficultySubmenu, 100, 175);
		menuList[2].addNewEntry("Back", MenuEntry.BHV_CHANGE_MENU, -1, 100, 250);
	}
	
	public void tick() {
		menuList[currentMenu].tick();
	}
	
	public void switchActiveMenu(int switchTo) {
		if(switchTo == -1) {
			if(menuStack.isEmpty()) System.exit(0);
			else currentMenu = menuStack.pop();
		}else if(!menuStack.isEmpty() && switchTo == menuStack.peek()) {
			currentMenu = menuStack.pop();
		}
		else {
			menuStack.push(currentMenu);
			currentMenu = switchTo;
		}
		menuList[currentMenu].reactivate();
	}
	public void switchActiveMenu(int switchTo, int newCursorPos) {
		//TODO
	}
	
	public void tickPauseMenu() {
		if(kbh.getHeldKeys()[10]) { game.restartStage(); pauseMenu.reactivate();}
		else if (kbh.getHeldKeys()[11]) game.returnToMenu();
		else pauseMenu.tick();
	}
	public void renderCurrentMenu(Graphics g) {
		menuList[currentMenu].render(g);
	}
	public void renderPauseMenu(Graphics g) {
		pauseMenu.render(g);
	}
	

}
