package game.player;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import game.Game;
import game.KBinputHandler;
import game.Spritesheet;
import game.audio.SoundManager;
import game.bullet.BulletManager;

public class Player {
	KBinputHandler kbh;
	PlayerShotManager ShotMGR;
	SoundManager SoundMGR;
	public static final int STARTING_LIVES = 4;
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
	int numActiveOptions;
	int optionMoveTime;
	int flashbombFull = 40;
	int flashbombCharge;
	ShotType shotType;
	ShotData shotData;
	PlayerOption[] optionArray;
	
	ArrayList<Double> flashbombsX;
	ArrayList<Double> flashbombsY;
	ArrayList<Integer> flashbombsTimeLeft;
	
	
	
	
	//graphics
	AffineTransform optionRenderTransform;
	private BufferedImage animIdle;
	@SuppressWarnings("unused")
	private BufferedImage animStrafe; // left movement
	private BufferedImage animHitbox;
	private BufferedImage animOption;
	private BufferedImage animFlashbomb;
	private int optionAnimWidth;
	private int optionAnimHeight;
	private BufferedImage[] deathAnimFrames;
	private int deathAnimWidth;
	private int deathAnimHeight;
	private int deathAnimFramerate = 2;
	private int flashbombAnimWidth;
	
	//input
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
	 * 5 = flashbombing
	 */
	public static final int STATE_NORMAL = 0;
	public static final int STATE_DYING = 1;
	public static final int STATE_DEATHBOMBWINDOW = 2;
	public static final int STATE_RESPAWN = 3;
	public static final int STATE_BOMB = 4;
	
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
		lives = (game.getGvar(2) == 1 ? 0 : STARTING_LIVES);
		bombs = 3;
		score = 0;
		iframes = 20;
		shotData = new ShotData();
		optionRenderTransform = new AffineTransform();
		flashbombCharge = 0;
		flashbombsX = new ArrayList<Double>();
		flashbombsY = new ArrayList<Double>();
		flashbombsTimeLeft = new ArrayList<Integer>();
	}
	public void playerInitAnim(BufferedImage neutral, BufferedImage strafe, int width, int height,
			BufferedImage hitbox, int hbSize,
			Spritesheet deathAnim, int w, int h, int frames,
			BufferedImage option, int optionW, int optionH,
			BufferedImage flashbomb, int fbW) {
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
		animOption = option;
		optionAnimWidth = optionW;
		optionAnimHeight = optionH;
		animFlashbomb = flashbomb;
		flashbombAnimWidth = fbW;
	}
	public void playerInitShotAndSpeed(String shot) {
		shotData.getShotInfoFromFile(shot);
		double[] attrsF = shotData.getPlayerAttributesFloat();
		moveSpeedUF = attrsF[0];
		moveSpeedF = attrsF[1];
		hitboxSize = attrsF[2];
		grazeboxSize = attrsF[3];
		int[] attrsI = shotData.getPlayerAttributesInt();
		deathbombWindow = attrsI[2];
		numActiveOptions = attrsI[4];
		optionMoveTime = attrsI[5];
		
		shotType = new ShotType(shotData, this);
		shotType.switchShootersets(0, 1);
		playerInitOptions(numActiveOptions);
		changeNumOptions(numActiveOptions);
	}
	public void playerInitOptions(int numMaxOptions) {
		optionArray = new PlayerOption[numMaxOptions];
		for(int i = 0; i < numMaxOptions; i++) {
			optionArray[i] = new PlayerOption(this);
		}
	}
	public void playerReInitialize() {
		x = 0;
		y = (Game.PLAYFIELDHEIGHT * 7 / 8);
		ShotMGR.deactivateAll();
		playerState = 0;
		stateTimer = 0;
		bombHeldPrevFrame = true;
		iframes = 20;
		lives = (game.getGvar(2) == 1 ? 0 : STARTING_LIVES);
		bombs = 3;
		score = 0;
		flashbombCharge = 0;
		flashbombsX.clear();
		flashbombsY.clear();
		flashbombsTimeLeft.clear();
	}
	public void playerSetShotMGR(PlayerShotManager psm) {
		ShotMGR = psm;
	}

	public void tickPlayer() {

		switch(playerState) {
		case STATE_NORMAL:
			boolean focusingPrevFrame = isFocusing;
			isFocusing = kbh.getHeldKeys()[6];
			if(focusingPrevFrame != isFocusing) {
				switchOptionConfig();
			}
			manageOptions();
			shotType.tickShooters();
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
			if(!bombHeldPrevFrame && kbh.getHeldKeys()[9] && flashbombCharge == flashbombFull) {
				useFlashbomb();
			}
			break;
		case STATE_DYING:	
			if(stateTimer >= deathAnimFrames.length * deathAnimFramerate) {
				playerState = 3;
				x = 0;
				y = Game.PLAYFIELDHEIGHT + playerAnimHeight;
				stateTimer = 0;
				if(game.getGvar(2) == 0) {
					if(lives == 0){
						game.state = Game.STATE.GAME_OVER;
					}else lives--;
				}else {
					lives++;
				}
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
		case STATE_RESPAWN:
			manageOptions();
			y -= 2;
			if(y <= (7 * Game.PLAYFIELDHEIGHT) / 8){
				playerState = 0;
				stateTimer = 0;
				iframes = 60;
			}
			break;
		case STATE_BOMB:
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
	public void tickFlashbombs(BulletManager b) {
		for(int i = flashbombsX.size() - 1; i >= 0; i--) {
			if(flashbombsTimeLeft.get(i) <= 0) {
				flashbombsTimeLeft.remove(i);
				flashbombsX.remove(i);
				flashbombsY.remove(i);
			}
		}
		for(int i = 0; i < flashbombsX.size(); i++) {
			b.cancelInRadius(flashbombsX.get(i), flashbombsY.get(i), 24);
		}
		for (int i = 0; i < flashbombsTimeLeft.size(); i++) flashbombsTimeLeft.set(i, flashbombsTimeLeft.get(i) - 1);
		
	}
	public void useFlashbomb() {
		flashbombCharge = 0;
		SoundMGR.playFromArray(SoundManager.Flashbomb);
		iframes = Math.max(iframes, 20);
		flashbombsX.add(this.x);
		flashbombsY.add(this.y);
		flashbombsTimeLeft.add(60);
	}
	public void addGraze() {
		if(flashbombCharge < flashbombFull) {
			flashbombCharge ++;
			if(game.getGvar(Game.GVAR_DIFFICULTY) == 0) flashbombCharge++;
			if(flashbombCharge == flashbombFull) SoundMGR.playFromArray(SoundManager.GaugeFull);
		}
	}
	
	public void respawn() {
		
	}
	public void switchOptionConfig(){
		int newConfig = isFocusing ? 1 : 0;
		double[]newOptionPos = shotData.getOptionPositions(0, newConfig);
		for(int i = 0; i < numActiveOptions; i++) {
			optionArray[i].shiftToPos(newOptionPos[2*i], newOptionPos[(2*i) + 1], optionMoveTime);
		}
	}
	public void changeNumOptions(int num) {
		numActiveOptions = num;
		for(int i = 0; i < numActiveOptions; i++) {
			optionArray[i].disabled = false;
		}
		for(int i = num; i < optionArray.length; i++) {
			optionArray[i].disabled = true;
		}
		switchOptionConfig();
	}
	public void manageOptions() {
		for (PlayerOption o : optionArray) {
			if(!(o.disabled)) o.tick();

		}
	}
	
	public double[] getPosAndHitbox() {
		return new double[] {x, y, hitboxSize, grazeboxSize};
	}
	public double[] getOptionCoords(int optionNum) {
		return optionArray[optionNum].getPosAndAngle();
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
	public void drawPlayerOptions(Graphics2D g) {
		if(playerState == 2) return;
		double[] optionDrawInfo;
		for(PlayerOption o : optionArray) {
			if(!(o.disabled)){

				optionDrawInfo = o.returnRenderCoords(optionAnimWidth, optionAnimHeight);
				optionRenderTransform.setToIdentity();
				optionRenderTransform.translate(Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2), Game.PLAYFIELDYOFFSET);
				optionRenderTransform.translate(optionDrawInfo[0], optionDrawInfo[1]);
				optionRenderTransform.rotate(optionDrawInfo[2], optionAnimWidth, optionAnimHeight);
				g.drawImage(animOption, optionRenderTransform, game);
			}

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
	public void drawFlashbomb(Graphics2D g) {
		AffineTransform renderTransform = new AffineTransform();
		for(int i = 0; i < flashbombsTimeLeft.size(); i++) {
			renderTransform.setToIdentity();
			renderTransform.translate(Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2), Game.PLAYFIELDYOFFSET);
			renderTransform.translate(flashbombsX.get(i) - flashbombAnimWidth,
					flashbombsY.get(i) - flashbombAnimWidth);
			renderTransform.rotate(flashbombsTimeLeft.get(i),
					flashbombAnimWidth,
					flashbombAnimWidth);
			g.drawImage(animFlashbomb, renderTransform, game);
		}
	}
	public void drawFlashbombGauge(Graphics2D g) {
		Color oldColor = g.getColor();
		g.drawString("Flashbomb", 440, 85);
		g.setColor(new Color(0x7F7F7F));
		g.fillRect(440, 90, 90, 16);
		g.setColor(Color.BLACK);
		g.fillRect(445, 94, 80, 8);
		g.setColor(flashbombCharge == flashbombFull ? Color.WHITE : Color.LIGHT_GRAY);
		g.fillRect(445, 94, flashbombCharge * 2, 8);
		g.setColor(oldColor);
		
	}
	public boolean getShotHeld() {
		return kbh.getHeldKeys()[7];
	}
	public PlayerShotManager getShotMGR() {
		return ShotMGR;
	}
}
