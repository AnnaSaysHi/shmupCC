package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Player {
	KBinputHandler kbh;
	PlayerShotManager ShotMGR;
	SoundManager SoundMGR;
	Game game;
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
	private BufferedImage[] deathAnimFrames;
	private int deathAnimWidth;
	private int deathAnimHeight;
	private int deathAnimFramerate = 2;
	byte[]dirs;
	int playerState;
	int stateTimer;
	int iframes;
	int deathbombWindow;
	boolean bombHeldPrevFrame;
	/*
	 * 0 = alive (with or without respawn i-frames)
	 * 1 = hit by bullet, has failed deathbomb window
	 * 2 = hit by bullet, able to deathbomb
	 * 3 = respawning 
	 * 4 = bombing
	 */
	public int lives;
	public int bombs;
	public int score;
	
	public Player(KBinputHandler k, SoundManager sndmgr, Game g, int lowXbound, int highXbound, int lowYbound, int highYbound) {
		isFocusing = false;
		bombHeldPrevFrame = true;
		kbh = k;
		SoundMGR = sndmgr;
		game = g;
		moveLimits = new int[] {lowXbound, highXbound, lowYbound, highYbound};
		playerState = 0;
		stateTimer = 0;
		x = 0;
		y = (Game.PLAYFIELDHEIGHT * 7 / 8);
		lives = 2;
		bombs = 3;
		score = 0;
		iframes = 20;
	}
	public void playerInitAnim(BufferedImage neutral, BufferedImage strafe, int width, int height, BufferedImage hitbox, int hbSize, Spritesheet deathAnim, int w, int h, int frames) {
		animIdle = neutral;
		animStrafe = strafe;
		animHitbox = hitbox;
		playerAnimWidth = width;
		playerAnimHeight = height;
		deathAnimWidth = w;
		deathAnimHeight = h;
		hitboxAnimSize = hbSize;
		deathAnimFrames = new BufferedImage[frames];
		for(int i = 0; i < frames; i++) {
			deathAnimFrames[i] = deathAnim.getSprite(i, 0, w, h);
		}
	}
	public void playerInitShotAndSpeed(double speedUF, double speedF, double size) {
		moveSpeedUF = speedUF;
		moveSpeedF = speedF;
		hitboxSize = size;
		grazeboxSize = 12;
		deathbombWindow = 8;
	}
	public void playerReInitialize() {
		x = 0;
		y = (Game.PLAYFIELDHEIGHT * 7 / 8);
		ShotMGR.deactivateAll();
		playerState = 0;
		stateTimer = 0;
		bombHeldPrevFrame = true;
		iframes = 20;
		lives = 2;
		bombs = 3;
		score = 0;
	}
	public void playerSetShotMGR(PlayerShotManager psm) {
		ShotMGR = psm;
		shotType = new ShotType((byte) 1, this);
	}

	public void tickPlayer() {

		switch(playerState) {
		case 0:
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
			if(!bombHeldPrevFrame && kbh.getHeldKeys()[9]) {
				playerState = 4;
				stateTimer = 0;
			}
			break;
		case 1:	
			if(stateTimer >= deathAnimFrames.length * deathAnimFramerate) {
				playerState = 3;
				x = 0;
				y = Game.PLAYFIELDHEIGHT + playerAnimHeight;
				stateTimer = 0;
				if(lives == 0){
					game.state = Game.STATE.GAME_OVER;
				}else lives--;
				break;
			}
			stateTimer++;
			break;
		case 2:
			stateTimer = 9;
			if(stateTimer > deathbombWindow) {
				playerState = 1;
				stateTimer = 1;
				SoundMGR.playFromArray(SoundManager.Explosion);
				break;
			}
			if(!bombHeldPrevFrame && kbh.getHeldKeys()[9]) {
				//playerState = 4;
				//stateTimer = 0;
			}
			break;
		case 3:
			y -= 2;
			if(y <= (7 * Game.PLAYFIELDHEIGHT) / 8){
				playerState = 0;
				stateTimer = 0;
				iframes = 20;
			}
			break;
		case 4:
			playerState = 0;
			break;
			
		}

		iframes--;
		bombHeldPrevFrame = kbh.getHeldKeys()[9];
	}
	public void collideWithBullet() {
		if(playerState == 0) {
			if(iframes <= 0) {
				playerState = 2;
				//SoundMGR.playFromArray(SoundManager.Pichuun);
				stateTimer = 0;
			}
		}
	}
	
	public void useBomb() {
		
	}
	
	public void respawn() {
		
	}
	
	public double[] getPosAndHitbox() {
		return new double[] {x, y, hitboxSize, grazeboxSize};
	}
	
	public void drawPlayer(Graphics2D g) {
		if(playerState != 1) {
			visXpos = (int)(x) - (playerAnimWidth / 2);
			visXpos += Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2);
			visYpos = (int)(y) - (playerAnimHeight / 2);
			visYpos += Game.PLAYFIELDYOFFSET;
			g.drawImage(animIdle, visXpos, visYpos, game);
		}
	}
	public void drawPlayerDeathAnim(Graphics2D g) {
		if(playerState == 1) {
			visXpos = (int)(x) - (deathAnimWidth / 2);
			visXpos += Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2);
			visYpos = (int)(y) - (deathAnimHeight / 2);
			visYpos += Game.PLAYFIELDYOFFSET;
			g.drawImage(deathAnimFrames[(stateTimer - 1) / deathAnimFramerate], visXpos, visYpos, game);
		}
	}
	public void drawHitbox(Graphics2D g) {
		if(playerState != 3 && playerState != 1) {
			visXpos = (int)(x) - (hitboxAnimSize / 2);
			visXpos += Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2);
			visYpos = (int)(y) - (hitboxAnimSize / 2);
			visYpos += Game.PLAYFIELDYOFFSET;
			g.drawImage(animHitbox, visXpos, visYpos, game);
		}
		
	}
	public boolean getShotHeld() {
		return kbh.getHeldKeys()[7];
	}
	public PlayerShotManager getShotMGR() {
		return ShotMGR;
	}
}
