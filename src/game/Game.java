package game;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;

import javax.swing.*;

import game.BulletSpawner.Mode;

public class Game extends Canvas implements Runnable{
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static int SCALE = 3;
	public static int numImageBuffers = 2;
	public String TITLE = "test";
	private int ticksInLastPeriod = 0;
	private double measuredFpS;
	private long lastTickPeriodMeasurement;
	private enum STATE{
		MENU,
		PLAY
	}
	
	private BulletManager BulletMGR;
	private BulletSpawner testSpawner;
	private KBinputHandler KBH;
	private double anglenum = Math.PI/2;
	private double angleIncrement = 0;
	private boolean running = false;
	private STATE state = STATE.PLAY;
	private Thread thread;
	
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
		
		

		Spritesheet ss = new Spritesheet(sprites);
		BulletMGR = new BulletManager(1000, ss);
		KBH = new KBinputHandler(this);
		this.addKeyListener(KBH);
		playerChar = new Player(KBH, 32, 928, 32, 700);
		playerChar.playerInitAnim(player0, player1, 64, 64, hitbox, 8);
		playerChar.playerInitShotAndSpeed(4.5, 2, 3);
		
		
		
		//debug bullet shooting test things
		bulletTimer = 0;
		testSpawner = new BulletSpawner(BulletMGR);
		testSpawner.setSpawnerPos(480, 360);
		testSpawner.setMode(Mode.Ring_Nonaimed);
		testSpawner.setBulletCounts(16, 8);
		testSpawner.setSpeeds(0.5, 1.5);
		testSpawner.setAngles(anglenum, Math.PI/64);
		testSpawner.setTypeAndColor(BulletType.BULLET, BulletColor.PURPLE);
		//testSpawner = new BulletSpawner(BulletMGR, Mode.Ring_Nonaimed, 480, 360, 16, 8, 1, 3, anglenum, Math.PI/64, 0, 0);
		//testSpawner = new BulletSpawner(BulletMGR, Mode.Fan, 480, 360, 4, 8, 0.5, 1.5, anglenum, Math.PI/16, 0, 0);
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
		angleIncrement += (Math.PI)/1024;
		anglenum += angleIncrement;
		//testSpawner.setAngles(anglenum, anglenum);
		BulletMGR.updateBullets();
		
		bulletTimer++;
		if(bulletTimer >= 60) {
			testSpawner.activate();
			bulletTimer = 0;
		}
		
		playerChar.tickPlayer();
		
		
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
		
		
		playerChar.drawPlayer(g, this);
		BulletMGR.drawBullets(g, this);
		playerChar.drawHitbox(g, this);
		
		
		g.dispose();
		bufferStrat.show();
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
