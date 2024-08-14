package game.bullet;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Game;
import game.Spritesheet;
import game.audio.SoundManager;
import game.player.Player;
/**
 * A class used for managing Bullets.
 * Stores an arbitrary number of Bullets, the graphics that each Bullet uses, and automates updating/rendering Bullets.
 * The amount of Bullets stored in this BulletManager is set during construction.
 * If more bullets than this number are attempted to be spawned, then the excess bullets will simply fail to spawn and no error will be thrown.
 */
public class BulletManager {
	Bullet [] bullets;
	Spritesheet bulletSprites;
	public SoundManager SoundMGR;
	Player relevantPlayer;
	BufferedImage[][] bulletSpriteReference;
	Game game;

	/**
	 * The BulletManager's constructor function.
	 * @param size the amount of Bullets this BulletManager can store
	 * @param ss the spritesheet that has all the Bullet graphics
	 * @param smgr 
	 * @param player the Player that these Bullets will interact with
	 */
	public BulletManager(int size, Spritesheet ss, SoundManager smgr, Player player, Game game) {
		bullets = new Bullet[size];
		SoundMGR = smgr;
		relevantPlayer = player;
		for(int i = 0; i < size; i++) bullets[i] = new Bullet(this, relevantPlayer);
		bulletSprites = ss;
		this.game = game;
		bulletSpriteReference = new BufferedImage[BulletColor.NUM_BULLET_COLORS][BulletType.NUM_BULLET_TYPES];
		for(int i = 0; i < BulletColor.NUM_BULLET_COLORS; i++) {
			for(int j = 0; j < BulletType.NUM_BULLET_TYPES; j++) {
				bulletSpriteReference[i][j] = ss.getSprite(i, j, 16, 16);
			}
		}
	}
	/**
	 * Iteratively calls each Bullet's update method, while also disabling them if they are outside of the screen.
	 */
	public void updateBullets() {
		for (int i = 0; i < bullets.length; i++){
			if (bullets[i].isDisabled() == false) {
				if(bullets[i].update()) {
					bullets[i].disable();
				}
			}			
		}
	}
	
	/**
	 * Iteratively calls each Bullet's draw method.
	 * 
	 * @param g 
	 * @param m
	 */
	public void drawBullets(Graphics2D g) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i].isDisabled() == false) {
				bullets[i].draw(g, bulletSpriteReference[bullets[i].getColor()] [bullets[i].getType()], game);
			}
		}
		
	}
	
	/**
	 * Iteratively checks if each Bullet should have collided with a Player using the given coordinates.
	 * ...Looking at this code now, this seems like kind of a hack-y way to do this.
	 * 
	 * @param x
	 * @param y
	 * @param rad
	 */
	public void checkCollision(double x, double y, double rad) {
		for(Bullet b : bullets) {
			if (b.isDisabled() == false) {
				if(b.checkCollision(x, y, rad)) {
					b.collideWithPlayer();
				}
			}
		}
	}
	/**
	 * Iteratively checks if each Bullet should have grazed a Player using the given coordinates.
	 * ...Looking at this code now, this seems like kind of a hack-y way to do this.
	 * 
	 * @param x
	 * @param y
	 * @param rad
	 */
	public void checkGraze(double x, double y, double rad) {
		for(Bullet b : bullets) {
			if (b.isDisabled() == false) {
				if((b.grazed == 0) && b.checkCollision(x, y, rad)) {
					b.grazedByPlayer();
				}
			}
		}
	}
	/**
	 * Deactivates all Bullets.
	 * Called when switching stages.
	 */
	public void deactivateAll() {
		for(int i = 0; i < bullets.length; i++) {
			bullets[i].disable();
		}
	}
	/**
	 * A method used for shooting a Bullet.
	 * This method should only ever be invoked by a BulletSpawner's shootOneWay, shootRingLayer, or shootPR_Bullet methods.
	 * 
	 * @param xPos
	 * @param yPos
	 * @param speed
	 * @param angle
	 * @param type
	 * @param color
	 * @param offscreenProtectionFramesNum
	 * @param distance
	 * @param transformationQueue
	 */
	public void addBullet(double xPos, double yPos, double speed, double angle, int type, int color, int offscreenProtectionFramesNum, double distance, BulletTransformation transformationQueue, int transformationStartingIndex) {
		for(int i = 0; i < bullets.length; i++) {
			if (bullets[i].isDisabled()) {
				bullets[i].respawnBullet(xPos, yPos, speed, angle, type, color, offscreenProtectionFramesNum, transformationQueue, transformationStartingIndex);
				bullets[i].step(distance);
				break;
			}
		}
	}
}
