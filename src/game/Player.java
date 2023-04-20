package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Player {
	KBinputHandler kbh;
	double x;
	double y;
	int[] moveLimits;
	double moveSpeedUF;
	double moveSpeedF;
	boolean isFocusing;
	int hitboxAnimSize;
	double hitboxSize;
	int playerAnimWidth;
	int playerAnimHeight;
	private BufferedImage animIdle;
	private BufferedImage animStrafe; // left movement
	private BufferedImage animHitbox;
	byte[]dirs;
	
	public Player(KBinputHandler k, int lowXbound, int highXbound, int lowYbound, int highYbound) {
		x = 480;
		y = 640;
		isFocusing = false;
		kbh = k;
		moveLimits = new int[] { lowXbound, highXbound, lowYbound, highYbound};
	}
	public void playerInitAnim(BufferedImage neutral, BufferedImage strafe, int width, int height, BufferedImage hitbox, int hbSize) {
		animIdle = neutral;
		animStrafe = strafe;
		animHitbox = hitbox;
		playerAnimWidth = width;
		playerAnimHeight = height;
		hitboxAnimSize = hbSize;
	}
	public void playerInitShotAndSpeed(double speedUF, double speedF, double size) {
		moveSpeedUF = speedUF;
		moveSpeedF = speedF;
		hitboxSize = size;
	}

	public void tickPlayer() {
		isFocusing = kbh.getHeldKeys()[4];
		double speed = isFocusing ? moveSpeedF : moveSpeedUF;
		dirs = kbh.getDirections();
		if(dirs[0] != 0 && dirs[1] != 0) speed = speed / Math.sqrt(2);
		switch(dirs[0]) {
		case 1:
			y -= speed;
			break;
		case 2:
			y += speed;
			break;
		default:
			break;
		}
		switch(dirs[1]) {
		case 1:
			x -= speed;
			break;
		case 2:
			x += speed;
			break;
		default:
			break;
		}
		x = Math.max(x, moveLimits[0]);
		x = Math.min(x, moveLimits[1]);
		y = Math.max(y, moveLimits[2]);
		y = Math.min(y, moveLimits[3]);
		
		
		
	}
	public double[] getPosAndHitbox() {
		return new double[] {x, y, hitboxSize};
	}
	
	public void drawPlayer(Graphics2D g, Game m) {
		int visXpos = (int)(x) - (playerAnimWidth / 2);
		int visYpos = (int)(y) - (playerAnimHeight / 2);
		g.drawImage(animIdle, visXpos, visYpos, m);
	}
	public void drawHitbox(Graphics2D g, Game m) {
		int visXpos = (int)(x) - (hitboxAnimSize / 2);
		int visYpos = (int)(y) - (hitboxAnimSize / 2);
		g.drawImage(animHitbox, visXpos, visYpos, m);
		
	}
}
