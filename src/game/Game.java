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
	int bulletTimer;
	private BufferedImage redBullet;
	
	public void init() {
		BufferedImageLoader loader = new BufferedImageLoader();
		try {
			sprites = loader.loadImage("/bulletsheet.png");
		}catch (IOException e){
			e.printStackTrace();
		}
		
		

		Spritesheet ss = new Spritesheet(sprites);
		redBullet = ss.getSprite(8, 1, 16, 16);
		BulletMGR = new BulletManager(1000, ss);
		KBH = new KBinputHandler(this);
		this.addKeyListener(KBH);
		bulletTimer = 0;
		testSpawner = new BulletSpawner(BulletMGR, Mode.Ring_Nonaimed, 480, 360, 16, 8, 1, 3, anglenum, Math.PI/64);
		//testSpawner = new BulletSpawner(BulletMGR, Mode.Fan, 480, 360, 4, 8, 0.5, 1.5, anglenum, Math.PI/16);
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
		//testSpawner.setAngle1(anglenum);
		BulletMGR.updateBullets();
		
		bulletTimer++;
		if(bulletTimer >= 60) {
			testSpawner.activate();
			bulletTimer = 0;
		}
		
		if(KBH.getHeldKeys()[0]) {
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
		
		BulletMGR.drawBullets(g, redBullet, this);
		
		
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
