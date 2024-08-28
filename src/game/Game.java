package game;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Random;

import game.audio.SoundManager;
import game.bullet.BulletManager;
import game.enemy.EnemyManager;
import game.menu.MenuManager;
import game.player.Player;
import game.player.PlayerShotManager;
import game.stages.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

public class Game extends Canvas implements Runnable{
	
	public final int SCRIPT_MAX = 6;
	public final int MAX_MENU_DEPTH = 10;

	public static final int GVAR_DIFFICULTY = 1;
	public static final int GVAR_INFINITE_LIVES = 2;
	
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
	int[] gvars;
	private BulletManager BulletMGR;
	private PlayerShotManager ShotMGR;
	private EnemyManager EnemyMGR;
	private SoundManager SoundMGR;
	private MenuManager MenuMGR;
	private KBinputHandler kbh;
	private boolean running = false;
	public STATE state = STATE.MENU;
	private Thread thread;
	private Random RNG;
	
	@SuppressWarnings("unused")
	private long rngInitSeed;
	
	private StageScript[] stageList = new StageScript[SCRIPT_MAX];	
	private int stage = -1;
	
	private double[] playercoords;
	
	public int[] globalsInt;
	public double[] globalsFloat;
	
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private BufferedImage bulletSprites = null;
	private BufferedImage enemySprites = null;
	private BufferedImage player1 = null;
	private BufferedImage player0 = null;
	private BufferedImage hitbox = null;
	private BufferedImage HUD = null;
	private BufferedImage shot = null;
	private BufferedImage explosionSheet = null;
	private BufferedImage lifeIcon = null;
	private BufferedImage skullIcon = null;
	private BufferedImage option = null;
	private BufferedImage loadingBG = null;
	private BufferedImage flashbomb = null;
	@SuppressWarnings("unused")
	private BufferedImage bombIcon = null;
	private Player playerChar;
	int bulletTimer;

	
	public void init() {
		BufferedImageLoader loader = new BufferedImageLoader();

		try {
			bulletSprites = loader.loadImage("/images/bulletsheet.png");
			enemySprites = loader.loadImage("/images/EnemySheet.png");
			player0 = loader.loadImage("/images/ShipStationary.png");
			player1 = loader.loadImage("/images/ShipLeanLeft.png");
			hitbox = loader.loadImage("/images/HitboxIndicator.png");
			shot = loader.loadImage("/images/playerShot.png");
			HUD = loader.loadImage("/images/HUD.png");
			explosionSheet = loader.loadImage("/images/ExplosionSheet.png");
			lifeIcon = loader.loadImage("/images/LifeIcon.png");
			skullIcon = loader.loadImage("/images/skull.png");
			option = loader.loadImage("/images/optionTypeA.png");
			flashbomb = loader.loadImage("/images/flashbomb.png");
		}catch (IOException e){
			e.printStackTrace();
		}
		
		
		RNG = new Random();
		gvars = new int[] {
				0,	// UNUSED
				1,	// DIFFICULTY
				0	// INFINITE LIVES
		};
		rngInitSeed = RNG.nextInt();
		Spritesheet bullets = new Spritesheet(bulletSprites);
		Spritesheet enemies = new Spritesheet(enemySprites);
		Spritesheet shots = new Spritesheet(shot);
		Spritesheet explosion = new Spritesheet(explosionSheet);
		kbh = new KBinputHandler(this);
		this.addKeyListener(kbh);
		bulletSprites.flush();


		SoundMGR = new SoundManager();
		SoundMGR.init();

		
		int pdistfromwalls = 12; //how close the player is allowed to get to the edge of the screen
		playerChar = new Player(kbh, SoundMGR, this,
				pdistfromwalls - (PLAYFIELDWIDTH / 2),
				(PLAYFIELDWIDTH / 2) - pdistfromwalls,
				pdistfromwalls,
				PLAYFIELDHEIGHT - pdistfromwalls);

		MenuMGR = new MenuManager(this, kbh, SoundMGR);
		MenuMGR.initMenus();
		BulletMGR = new BulletManager(1000, bullets, SoundMGR, playerChar, this);
		EnemyMGR = new EnemyManager(100, enemies, BulletMGR, playerChar, this, SoundMGR);
		ShotMGR = new PlayerShotManager(100, shots, EnemyMGR, SoundMGR);
		playerChar.playerSetShotMGR(ShotMGR);
		

		playerChar.playerInitAnim(player0, player1, 48, 48, 
				hitbox, 8, 
				explosion, 70, 100, 17,
				option, 24, 24,
				flashbomb, 24);
		playerChar.playerInitShotAndSpeed("/player/pl00.sht");
		


		
		playercoords = playerChar.getPosAndHitbox();
		
		stageList[0] = new Script1_1(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[1] = new Script1_2(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[2] = new Script1_3(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[3] = new Script1_4(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[4] = new Script_necropotence(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[5] = new Script1_6(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);

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
		renderLoadingBG();
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
		long nextTick = MRT + skipTicks;
		
		
		while(running) {
			if(nextTick <= System.nanoTime()) {
				MRT = System.nanoTime();
				tick();
				render();
				nextTick = MRT + skipTicks;
				long sleepdur = (nextTick - MRT) / 1000000;
				if(sleepdur > 6) {
					try {
						Thread.sleep(Math.max(0, sleepdur - 6));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
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
		if(kbh.getHeldKeys()[12]) {
			System.out.println("b");
		}
		if(state == STATE.PLAY) {
			if(kbh.getHeldKeys()[8]) {
				state = STATE.PAUSE;
			} else {
				stageList[stage].tick();
				BulletMGR.updateBullets();
				ShotMGR.updateShots();
				ShotMGR.enemyHitDetect();
				EnemyMGR.updateEnemies();
				playerChar.tickPlayer();
				playercoords = playerChar.getPosAndHitbox();
				BulletMGR.checkGraze(playercoords[0], playercoords[1], playercoords[3]);
				playerChar.tickFlashbombs(BulletMGR);
				BulletMGR.checkCollision(playercoords[0], playercoords[1], playercoords[2]);
				EnemyMGR.checkCollision(playercoords[0], playercoords[1], playercoords[2]);
			}
		}
		else if (state == STATE.MENU) {
				MenuMGR.tick();
		}
		else if (state == STATE.PAUSE || state == STATE.GAME_OVER) MenuMGR.tickPauseMenu();
		
		//System.gc();
	}
	
	
	private void render() {
		BufferStrategy bufferStrat = this.getBufferStrategy();
		if(bufferStrat == null) {
			return;
		}
		Graphics2D g = (Graphics2D)(bufferStrat.getDrawGraphics());
		AffineTransform at = new AffineTransform();
		at.scale(SCALE/2.0, SCALE/2.0);
		g.setTransform(at);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		Font fpsfont = g.getFont();

		if(state == STATE.PLAY || state == STATE.PAUSE || state == STATE.GAME_OVER) {
			playerChar.drawFlashbomb(g);
			ShotMGR.drawShots(g, this);
			playerChar.drawPlayer(g);
			playerChar.drawPlayerOptions(g);
			boolean toDrawHP = EnemyMGR.drawEnemies(g);
			BulletMGR.drawBullets(g);
			if(toDrawHP) EnemyMGR.drawHPbars(g);
			playerChar.drawHitbox(g);
			playerChar.drawPlayerDeathAnim(g);
			g.drawImage(HUD, 0, 0, this);
			Font scoreFont = new Font("THBiolinum", Font.PLAIN, 24);
			g.setColor(new Color(0x4f4f4f));
			g.setFont(scoreFont);
			//g.drawString("Score: ", 440, 60);
			if(gvars[2] == 0) {
				g.drawString("Lives: ", 440, 60);
				for(int i = 0; i < playerChar.lives; i++) {
					g.drawImage(lifeIcon, 500 + (20 * i), 48, this);
				}
			}else {
				g.drawImage(skullIcon, 440, 48, this);
				g.drawString(":   " + playerChar.lives, 475, 70);
			}
			playerChar.drawFlashbombGauge(g);
		}
		if(state == STATE.MENU) MenuMGR.renderCurrentMenu(g);
		if(state == STATE.PAUSE || state == STATE.GAME_OVER) MenuMGR.renderPauseMenu(g);
		g.setFont(fpsfont);
		g.setColor(Color.WHITE);
		g.drawString(String.format("%.2f", measuredFpS), 10, 10);
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
	public double randRad() {
		double angle = RNG.nextDouble();
		angle = angle * Math.PI * 2;
		return angle;		
	}
	public int intDiff(int a, int b) {
		return (gvars[1] == 0 ? a : b);
	}
	public double floatDiff(double a, double b) {
		return (gvars[1] == 0 ? a : b);
	}
	public int getGvar(int index) {
		return gvars[index];
	}
	public void setGvar(int index, int value) {
		gvars[index] = value;
	}
	public boolean isOutsidePlayfield(double xpos, double ypos, double size) {
		if (xpos > size + (Game.PLAYFIELDWIDTH / 2)) return true;
		if (xpos < (-size - (Game.PLAYFIELDWIDTH / 2))) return true;
		if (ypos < -size) return true;
		if (ypos > size + Game.PLAYFIELDHEIGHT) return true;
		return false;
	}
	
	public void changeMenus(int changeTo) {
		//TODO
	}
	public void setStage (int i) {
		stage = i;
		playerChar.playerReInitialize();
		stageList[stage].init();
	}
	public void restartStage() {
		playerChar.playerReInitialize();
		stageList[stage].init();
		state = STATE.PLAY;
	}
	public void returnToMenu() {
		state = STATE.MENU;
		stage = -1;
		playerChar.playerReInitialize();
		BulletMGR.deactivateAll();
	}
	public void nextStage() {
		stage++;
		stageList[stage].init();
	}
	public void unpause() {
		state = STATE.PLAY;
	}
	public void renderLoadingBG() {
		BufferedImageLoader loader = new BufferedImageLoader();
		try {
			loadingBG = loader.loadImage("/images/loadingBG.png");
			BufferStrategy bufferStrat = this.getBufferStrategy();
			if(bufferStrat == null) {
				return;
			}
			Graphics2D g = (Graphics2D)(bufferStrat.getDrawGraphics());
			AffineTransform at = new AffineTransform();
			at.scale(SCALE/2.0, SCALE/2.0);
			g.setTransform(at);
			g.drawImage(loadingBG, 0, 0, this);
			g.dispose();
			bufferStrat.show();

		} catch (IOException e) {
			e.printStackTrace();			
		}
		
	}
	
	public static void main(String[] args) {
		System.setProperty("sun.java2d.uiScale", "1");
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
