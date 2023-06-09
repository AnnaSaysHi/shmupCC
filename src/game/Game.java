package game;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Random;
import game.stages.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;

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
	private PlayerShotManager ShotMGR;
	private EnemyManager EnemyMGR;
	private SoundManager SoundMGR;
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
	
	private StageScript[] stageList = new StageScript[5];	
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
	private BufferedImage explosionSheet = null;
	private BufferedImage lifeIcon = null;
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
		}catch (IOException e){
			e.printStackTrace();
		}
		
		
		RNG = new Random();
		rngInitSeed = RNG.nextInt();
		Spritesheet bullets = new Spritesheet(bulletSprites);
		Spritesheet enemies = new Spritesheet(enemySprites);
		Spritesheet shots = new Spritesheet(shot);
		Spritesheet explosion = new Spritesheet(explosionSheet);
		kbh = new KBinputHandler(this);
		this.addKeyListener(kbh);


		SoundMGR = new SoundManager();
		SoundMGR.init();
		
		menu = new MenuGeneral(this, kbh, SoundMGR);
		sceneMenu = new MenuSceneSelect(this, kbh, SoundMGR);
		pauseMenu = new MenuPause(this, kbh, SoundMGR);
		sceneMenu.setParentMenu(menu);
		sceneMenu.setMenuLengthAndDirection(5, (byte) 0);
		pauseMenu.setMenuLengthAndDirection(3, (byte) 0);
		menuList[0] = menu;
		menuList[1] = sceneMenu;
		menuList[0].activate();
		
		int pdistfromwalls = 12; //how close the player is allowed to get to the edge of the screen
		playerChar = new Player(kbh, SoundMGR, this,
				pdistfromwalls - (PLAYFIELDWIDTH / 2),
				(PLAYFIELDWIDTH / 2) - pdistfromwalls,
				pdistfromwalls,
				PLAYFIELDHEIGHT - pdistfromwalls);
		
		BulletMGR = new BulletManager(1000, bullets, SoundMGR, playerChar);
		EnemyMGR = new EnemyManager(100, enemies, BulletMGR, playerChar, this, SoundMGR);
		ShotMGR = new PlayerShotManager(100, shots, EnemyMGR, SoundMGR);
		playerChar.playerSetShotMGR(ShotMGR);
		

		playerChar.playerInitAnim(player0, player1, 48, 48, hitbox, 8, explosion, 70, 100, 17);
		playerChar.playerInitShotAndSpeed(4.5, 2, 3);
		


		
		playercoords = playerChar.getPosAndHitbox();
		
		stageList[0] = new Script1_1(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[1] = new Script1_2(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[2] = new Script1_3(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[3] = new Script1_4(BulletMGR, this, playerChar, EnemyMGR, SoundMGR);
		stageList[4] = new StageScript(BulletMGR, this, playerChar, EnemyMGR, SoundMGR, "resources/scripts/scene15.sccl");

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
		if(kbh.getHeldKeys()[10]) {
			System.out.println("b");
		}
		if(state == STATE.PLAY) {
			if(kbh.getHeldKeys()[8]) {
				state = STATE.PAUSE;	
				pauseMenu.activate();
			} else {
				stageList[stage].tick();
				BulletMGR.updateBullets();
				ShotMGR.updateShots();
				ShotMGR.enemyHitDetect();
				EnemyMGR.updateEnemies();
				playerChar.tickPlayer();
				playercoords = playerChar.getPosAndHitbox();
				BulletMGR.checkCollision(playercoords[0], playercoords[1], playercoords[2]);
				BulletMGR.checkGraze(playercoords[0], playercoords[1], playercoords[3]);
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
		Font fpsfont = g.getFont();

		if(state == STATE.PLAY || state == STATE.PAUSE || state == STATE.GAME_OVER) {
			playerChar.drawPlayer(g);
			EnemyMGR.drawEnemies(g);
			ShotMGR.drawShots(g, this);
			BulletMGR.drawBullets(g, this);
			playerChar.drawHitbox(g);
			playerChar.drawPlayerDeathAnim(g);
			g.drawImage(HUD, 0, 0, this);
			Font scoreFont = new Font("THBiolinum", Font.PLAIN, 24);
			g.setColor(new Color(0x4f4f4f));
			g.setFont(scoreFont);
			g.drawString("Score: ", 440, 60);
			g.drawString("Lives: ", 440, 92);
			for(int i = 0; i < playerChar.lives; i++) {
				g.drawImage(lifeIcon, 490 + (20 * i), 80, this);
			}
		}
		if(state == STATE.MENU) {
			for(int i = 0; i < menuList.length; i++) {
				if(menuList[i].getActive()) {
					menuList[i].render(g);
				}
			}
		}
		if(state == STATE.PAUSE || state == STATE.GAME_OVER) pauseMenu.render(g);
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
	public boolean isOutsidePlayfield(double xpos, double ypos, double size) {
		if (xpos > size + (Game.PLAYFIELDWIDTH / 2)) return true;
		if (xpos < (-size - (Game.PLAYFIELDWIDTH / 2))) return true;
		if (ypos < -size) return true;
		if (ypos > size + Game.PLAYFIELDHEIGHT) return true;
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
