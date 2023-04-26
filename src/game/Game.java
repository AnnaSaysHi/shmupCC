package game;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Random;
import game.stages.*;
import javax.swing.*;

import game.BulletSpawner.Mode;

public class Game extends Canvas implements Runnable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8763681502519222609L;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static int SCALE = 3;
	public static int numImageBuffers = 2;
	public String TITLE = "test";
	private int ticksInLastPeriod = 0;
	private double measuredFpS;
	private long lastTickPeriodMeasurement;
	public static enum STATE{
		MENU,
		PLAY
	}
	
	private BulletManager BulletMGR;
	private BulletSpawner testSpawner;
	private KBinputHandler kbh;
	private double anglenum = Math.PI/2;
	private double angleIncrement = 0;
	private boolean running = false;
	public STATE state = STATE.MENU;
	private Thread thread;
	private Random RNG;
	private MenuGeneral menu;
	private MenuGeneral[] menuList = new MenuGeneral[2];
	private MenuSceneSelect sceneMenu;
	private int activeMenu;
	private long rngInitSeed;
	
	private StageScript[] stageList = new StageScript[2];	
	private int stage = -1;
	
	private double[] playercoords;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private BufferedImage sprites = null;
	private BufferedImage player1 = null;
	private BufferedImage player0 = null;
	private BufferedImage hitbox = null;
	private Player playerChar;
	int bulletTimer;
	
	public void init() {
		BufferedImageLoader loader = new BufferedImageLoader();
		try {
			sprites = loader.loadImage("/bulletsheet.png");
			player0 = loader.loadImage("/ShipStationary.png");
			player1 = loader.loadImage("/ShipLeanLeft.png");
			hitbox = loader.loadImage("/HitboxIndicator.png");
		}catch (IOException e){
			e.printStackTrace();
		}
		
		
		RNG = new Random();
		rngInitSeed = RNG.nextInt();
		Spritesheet ss = new Spritesheet(sprites);
		BulletMGR = new BulletManager(1000, ss);
		kbh = new KBinputHandler(this);
		this.addKeyListener(kbh);
		
		
		menu = new MenuGeneral(this, kbh);
		sceneMenu = new MenuSceneSelect(this, kbh);
		sceneMenu.setParentMenu(menu);
		sceneMenu.setMenuLengthAndDirection(2, (byte) 0);
		menuList[0] = menu;
		menuList[1] = sceneMenu;
		menuList[0].activate();		
		
		playerChar = new Player(kbh, 32, 928, 32, 700);
		playerChar.playerInitAnim(player0, player1, 64, 64, hitbox, 8);
		playerChar.playerInitShotAndSpeed(4.5, 2, 3);
		
		stageList[0] = new Script1_1(BulletMGR, this, playerChar);
		stageList[1] = new Script1_2(BulletMGR, this, playerChar);

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
				returnToMenu();
				BulletMGR.deactivateAll();
			} else {
				stageList[stage].tick();
				BulletMGR.updateBullets();
				playerChar.tickPlayer();
				playercoords = playerChar.getPosAndHitbox();
				BulletMGR.checkCollision(playercoords[0], playercoords[1], playercoords[2]);
			}
		}
		if (state == STATE.MENU) {
			for(int i = 0; i < menuList.length; i++) {
				if(menuList[i].getActive()) {
					menuList[i].tick();
				}
			}
		}
		
		//System.gc();
	}
	
	
	private void render() {
		BufferStrategy bufferStrat = this.getBufferStrategy();
		if(bufferStrat == null) {
			return;
		}
		Graphics2D g = (Graphics2D)(bufferStrat.getDrawGraphics());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		g.setColor(Color.WHITE);
		g.drawString(Double.toString(measuredFpS), 10, 10);

		if(state == STATE.PLAY) {
			playerChar.drawPlayer(g, this);
			BulletMGR.drawBullets(g, this);
			playerChar.drawHitbox(g, this);
		}
		if(state == STATE.MENU) {
			for(int i = 0; i < menuList.length; i++) {
				if(menuList[i].getActive()) {
					menuList[i].render(g);
				}
			}
		}
		
		g.dispose();
		bufferStrat.show();
	}
	
	//Utility functions
	public double getAngleToPlayer(double x, double y) {
		return Math.atan2(playercoords[1] - y, playercoords[0] - x);
	}
	public Random FetchRNG() {
		return RNG;
	}
	public void changeMenus(int changeTo) {
		menuList[changeTo].activate();
	}
	public void setStage (int i) {
		stage = i;
		stageList[stage].init();
	}
	public void returnToMenu() {
		state = STATE.MENU;
		stage = -1;
	}
	public void nextStage() {
		stage++;
		stageList[stage].init();
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
