package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Player {
	double x;
	double y;
	double moveSpeed;
	int hitboxAnimSize;
	double hitboxSize;
	int playerAnimWidth;
	int playerAnimHeight;
	private BufferedImage animIdle;
	private BufferedImage animStrafe; // left movement
	private BufferedImage animHitbox;
	
	public Player() {
		x = 480;
		y = 640;
	}
	public void playerInitAnim(BufferedImage neutral, BufferedImage strafe, int width, int height, BufferedImage hitbox, int hbSize) {
		animIdle = neutral;
		animStrafe = strafe;
		animHitbox = hitbox;
		playerAnimWidth = width;
		playerAnimHeight = height;
		hitboxAnimSize = hbSize;
	}
	public void playerInitShotAndSpeed(double speed, double size) {
		moveSpeed = speed;
		hitboxSize = size;
	}

	public void movePlayer() {
		
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
