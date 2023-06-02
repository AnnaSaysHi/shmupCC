package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Player {
	KBinputHandler kbh;
	PlayerShotManager ShotMGR;
	double x;
	double y;
	int[] moveLimits;
	double moveSpeedUF;
	double moveSpeedF;
	private double speed; //don't actually set this
	boolean isFocusing;
	int hitboxAnimSize;
	double hitboxSize;
	double grazeboxSize;
	int playerAnimWidth;
	int playerAnimHeight;
	int visXpos;
	int visYpos;
	ShotType shotType;
	private BufferedImage animIdle;
	private BufferedImage animStrafe; // left movement
	private BufferedImage animHitbox;
	byte[]dirs;
	
	public Player(KBinputHandler k, PlayerShotManager mgr, int lowXbound, int highXbound, int lowYbound, int highYbound) {
		isFocusing = false;
		kbh = k;
		ShotMGR = mgr;
		shotType = new ShotType((byte) 1, this);
		moveLimits = new int[] {lowXbound, highXbound, lowYbound, highYbound};
		playerReInitialize();
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
		grazeboxSize = 12;
	}
	public void playerReInitialize() {
		x = 0;
		y = (Game.PLAYFIELDHEIGHT * 7 / 8);
		ShotMGR.deactivateAll();
	}

	public void tickPlayer() {
		shotType.tickShooters();
		isFocusing = kbh.getHeldKeys()[6];
		speed = isFocusing ? moveSpeedF : moveSpeedUF;
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
	public void collideWithBullet() {
		
	}
	
	public void useBomb() {
		
	}
	
	public void respawn() {
		
	}
	
	public double[] getPosAndHitbox() {
		return new double[] {x, y, hitboxSize};
	}
	
	public void drawPlayer(Graphics2D g, Game m) {
		visXpos = (int)(x) - (playerAnimWidth / 2);
		visXpos += Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2);
		visYpos = (int)(y) - (playerAnimHeight / 2);
		visYpos += Game.PLAYFIELDYOFFSET;
		g.drawImage(animIdle, visXpos, visYpos, m);
	}
	public void drawHitbox(Graphics2D g, Game m) {
		visXpos = (int)(x) - (hitboxAnimSize / 2);
		visXpos += Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2);
		visYpos = (int)(y) - (hitboxAnimSize / 2);
		visYpos += Game.PLAYFIELDYOFFSET;
		g.drawImage(animHitbox, visXpos, visYpos, m);
		
	}
	public boolean getShotHeld() {
		return kbh.getHeldKeys()[7];
	}
	public PlayerShotManager getShotMGR() {
		return ShotMGR;
	}
}
