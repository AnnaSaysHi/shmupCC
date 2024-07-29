package game;

import java.awt.Graphics;
import java.util.Stack;

public class MenuManager {

	Game game;
	KBinputHandler kbh;
	protected SoundManager smgr;
	Stack<Integer> menuStack;
	public final int MAX_MENU_DEPTH = 10;
	private int menuCurrentDepth = 0;
	private int[] menuCallStack = new int[MAX_MENU_DEPTH];
	private int[] storedMenuPositions = new int[MAX_MENU_DEPTH];
	private int currentMenu = 0;
	private MenuGeneral menu;
	private MenuGeneral[] menuList = new MenuGeneral[2];
	private MenuNew[] menuList2;
	private MenuPause pauseMenuOld;
	private MenuNew pauseMenu;
	private MenuSceneSelect sceneMenu;
	
	public MenuManager(Game game, KBinputHandler kbh, SoundManager SoundMGR) {
		this.game = game;
		this.kbh = kbh;
		smgr = SoundMGR;
		
		/*menu = new MenuGeneral(this.game, kbh, smgr);
		sceneMenu = new MenuSceneSelect(this.game, kbh, smgr);
		pauseMenuOld = new MenuPause(this.game, kbh, smgr);
		sceneMenu.setMenuLengthAndDirection(game.SCRIPT_MAX, (byte) 0);
		pauseMenuOld.setMenuLengthAndDirection(3, (byte) 0);
		menuList[0] = menu;
		menuList[1] = sceneMenu;
		menuList[0].activate();*/
		menuList2 = new MenuNew[2];
	}
	
	public void initMenusFromTextFile() {
		//TODO
	}
	
	public void initMenusHardCoded() {
		pauseMenu = new MenuNew(this.game, kbh, smgr, this);
		pauseMenu.addNewEntry("Unpause", MenuEntry.BHV_UNPAUSE, 0, 0, 100, 100);
		pauseMenu.addNewEntry("Restart", MenuEntry.BHV_START_OVER, 0, 0, 100, 150);
		pauseMenu.addNewEntry("Return to Title", MenuEntry.BHV_RETURN_TO_MENU, 0, 0, 100, 200);
		menuList2[0] = new MenuNew(this.game, kbh, smgr, this);
		menuList2[0].addNewEntry("Start", MenuEntry.BHV_START_SCENE, 1, 0, 100, 100);
		menuList2[0].addNewEntry("Exit", MenuEntry.BHV_EXIT_GAME, 0, 0, 100, 200);
	}
	
	public void tick() {
		menuList2[currentMenu].tick();
	}
	
	public void switchActiveMenu(int switchTo) {
		//TODO
	}
	
	public void tickPauseMenu() {
		//pauseMenuOld.tick();
		pauseMenu.tick();
	}
	public void renderCurrentMenu(Graphics g) {
		menuList2[currentMenu].render(g);
	}
	public void renderPauseMenu(Graphics g) {
		pauseMenu.render(g);
	}
	

}
