package game;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Random;
import game.stages.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

import game.BulletSpawner.Mode;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 8763681502519222609L;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int PLAYFIELDWIDTH = 384;
	public static final int PLAYFIELDHEIGHT = 448;
	public static final int PLAYFIELDXOFFSET = 16;
	public static final int PLAYFIELDYOFFSET = 16;
	public static int SCALE = 3;
	public static int numImageBuffers = 2;
	public String TITLE = "test";
	private int ticksInLastPeriod = 0;
	private double measuredFpS;
	private long lastTickPeriodMeasurement;
	public static enum STATE{
		MENU,
		PLAY,
		PAUSE,
		GAME_OVER		
	}
	
	private BulletManager BulletMGR;
	private EnemyManager EnemyMGR;
	private KBinputHandler kbh;
	private boolean running = false;
	public STATE state = STATE.MENU;
	private Thread thread;
	private Random RNG;
	private MenuGeneral menu;
	private MenuGeneral[] menuList = new MenuGeneral[2];
	private MenuPause pauseMenu;
	private MenuSceneSelect sceneMenu;
	private int activeMenu;
	private long rngInitSeed;
	
	private StageScript[] stageList = new StageScript[3];	
	private int stage = -1;
	
	private double[] playercoords;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private BufferedImage bulletSprites = null;
	private BufferedImage enemySprites = null;
	private BufferedImage player1 = null;
	private BufferedImage player0 = null;
	private BufferedImage hitbox = null;
	private BufferedImage HUD = null;
	private BufferedImage shot = null;
	private Player playerChar;
	int bulletTimer;
	
	public void init() {
		BufferedImageLoader loader = new BufferedImageLoader();
		try {
			bulletSprites = loader.loadImage("/bulletsheet.png");
			enemySprites = loader.loadImage("/EnemySheet.png");
			player0 = loader.loadImage("/ShipStationary.png");
			player1 = loader.loadImage("/ShipLeanLeft.png");
			hitbox = loader.loadImage("/HitboxIndicator.png");
			shot = loader.loadImage("/playerShot.png");
			HUD = loader.loadImage("/HUD.png");
		}catch (IOException e){
			e.printStackTrace();
		}
		
		
		RNG = new Random();
		rngInitSeed = RNG.nextInt();
		Spritesheet bullets = new Spritesheet(bulletSprites);
		Spritesheet enemies = new Spritesheet(enemySprites);
		kbh = new KBinputHandler(this);
		this.addKeyListener(kbh);
		
		
		menu = new MenuGeneral(this, kbh);
		sceneMenu = new MenuSceneSelect(this, kbh);
		pauseMenu = new MenuPause(this, kbh);
		sceneMenu.setParentMenu(menu);
		sceneMenu.setMenuLengthAndDirection(3, (byte) 0);
		pauseMenu.setMenuLengthAndDirection(3, (byte) 0);
		menuList[0] = menu;
		menuList[1] = sceneMenu;
		menuList[0].activate();		
		int pdistfromwalls = 12; //how close the player is allowed to get to the edge of the screen
		playerChar = new Player(kbh, pdistfromwalls + PLAYFIELDXOFFSET, PLAYFIELDXOFFSET + PLAYFIELDWIDTH - pdistfromwalls, pdistfromwalls + PLAYFIELDYOFFSET, PLAYFIELDYOFFSET + PLAYFIELDHEIGHT - pdistfromwalls);
		playerChar.playerInitAnim(player0, player1, 64, 64, hitbox, 8);
		playerChar.playerInitShotAndSpeed(4.5, 2, 3);
		
		playercoords = playerChar.getPosAndHitbox();
		

		BulletMGR = new BulletManager(1000, bullets);
		EnemyMGR = new EnemyManager(100, enemies, BulletMGR, playerChar, this);
		
		stageList[0] = new Script1_1(BulletMGR, this, playerChar);
		stageList[1] = new Script1_2(BulletMGR, this, playerChar);
		stageList[2] = new Script1_3(BulletMGR, this, playerChar);

	}
	
	private synchronized void start() {
		if(running){
			return;
		}
		
		running = true;
		thread = new Thread(this);
		if(this.getBufferStrategy() == null) {
			createBufferStrategy(numImageBuffers);
		}
		thread.start();
	}
	
	private synchronized void stop() {
		if(!running) {
			return;
		}
		running = false;
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	@Override
	public void run() {
		init();
		long MRT = System.nanoTime();
		lastTickPeriodMeasurement = System.nanoTime();
		int preferredFPS = 60;
		long skipTicks = (1000000000 / preferredFPS);
		
		
		while(running) {
			if(MRT + skipTicks < System.nanoTime()) {
				MRT = System.nanoTime();
				tick();
				render();
			}			
		}
		stop();
		
	}
	
	private void tick() {

		ticksInLastPeriod++;
		if(System.nanoTime() > (lastTickPeriodMeasurement + 1000000000)) {
			measuredFpS = ((double)(ticksInLastPeriod)) / (((double)(System.nanoTime() - lastTickPeriodMeasurement)) / 1000000000);
			ticksInLastPeriod = 0;
			lastTickPeriodMeasurement = System.nanoTime();
		}
		if(kbh.getHeldKeys()[9]) {
			System.out.println("b");
		}
		if(state == STATE.PLAY) {
			if(kbh.getHeldKeys()[8]) {
				state = state.PAUSE;	
				pauseMenu.activate();
			} else {
				stageList[stage].tick();
				BulletMGR.updateBullets();
				playerChar.tickPlayer();
				playercoords = playerChar.getPosAndHitbox();
				BulletMGR.checkCollision(playercoords[0], playercoords[1], playercoords[2]);
			}
		}
		else if (state == STATE.MENU) {
			for(int i = 0; i < menuList.length; i++) {
				if(menuList[i].getActive()) {
					menuList[i].tick();
				}
			}
		}
		else if (state == STATE.PAUSE || state == STATE.GAME_OVER) {
			pauseMenu.tick();
		}
		
		//System.gc();
	}
	
	
	private void render() {
		BufferStrategy bufferStrat = this.getBufferStrategy();
		if(bufferStrat == null) {
			return;
		}
		Graphics2D g = (Graphics2D)(bufferStrat.getDrawGraphics());
		AffineTransform at = new AffineTransform();
		at.scale(SCALE/ScreenResolutionConstant.res, SCALE/ScreenResolutionConstant.res);
		g.setTransform(at);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		g.setColor(Color.WHITE);

		if(state == STATE.PLAY || state == STATE.PAUSE || state == STATE.GAME_OVER) {
			playerChar.drawPlayer(g, this);
			BulletMGR.drawBullets(g, this);
			playerChar.drawHitbox(g, this);
			g.drawImage(HUD, 0, 0, this);
		}
		if(state == STATE.MENU) {
			for(int i = 0; i < menuList.length; i++) {
				if(menuList[i].getActive()) {
					menuList[i].render(g);
				}
			}
		}
		if(state == STATE.PAUSE || state == STATE.GAME_OVER) pauseMenu.render(g);

		g.drawString(Double.toString(measuredFpS), 10, 10);
		g.dispose();
		bufferStrat.show();
	}
	
	//Utility functions
	public double getAngleToPlayer(double x, double y) {
		if(playercoords[1] == y && playercoords[0] == x) return 0;
		else return Math.atan2(playercoords[1] - y, playercoords[0] - x);
	}
	public Random FetchRNG() {
		return RNG;
	}
	public boolean isOutsidePlayfield(double xpos, double ypos, double size) {
		if (xpos > size + Game.PLAYFIELDWIDTH + Game.PLAYFIELDXOFFSET) return true;
		if (xpos < Game.PLAYFIELDXOFFSET - size) return true;
		if (ypos < Game.PLAYFIELDYOFFSET - size) return true;
		if (ypos > size + Game.PLAYFIELDHEIGHT + Game.PLAYFIELDYOFFSET) return true;
		return false;
	}
	
	public void changeMenus(int changeTo) {
		menuList[changeTo].activate();
	}
	public void setStage (int i) {
		stage = i;
		stageList[stage].init();
	}
	public void restartStage() {
		stageList[stage].init();
		state = STATE.PLAY;
	}
	public void returnToMenu() {
		state = STATE.MENU;
		stage = -1;
		BulletMGR.deactivateAll();
	}
	public void nextStage() {
		stage++;
		stageList[stage].init();
	}
	public void unpause() {
		state = STATE.PLAY;
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		Dimension windowSize = new Dimension(WIDTH * SCALE / 2, HEIGHT * SCALE / 2);
		game.setPreferredSize(windowSize);
		game.setMaximumSize(windowSize);
		game.setMinimumSize(windowSize);
		
		JFrame frame = new JFrame(game.TITLE);
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		
		game.start();
	}
	
}
