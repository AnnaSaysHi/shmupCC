package game.enemy;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Game;
import game.Spritesheet;
import game.audio.SoundManager;
import game.bullet.BulletManager;
import game.player.Player;
import java.util.ArrayList;
import java.util.Arrays;

public class EnemyManager {

	Spritesheet enemySprites;
	BufferedImage[][] enemySpriteReference;
	Game game;
	Player relevantPlayer;
	int maxSize;
	BulletManager bmgr;
	SoundManager smgr;
	private BufferedImage[] explosionAnimFrames;
	private int explosionAnimWidth;
	private int explosionAnimHeight;
	private int explosionAnimFramerate = 2;
	private ArrayList<Double> explosionXcoords;
	private ArrayList<Double> explosionYcoords;
	private ArrayList<Integer> explosionTimers;
	ArrayList<Enemy> enemies;
	int[] intVars;
	double[] floatVars;

	public EnemyManager(int size, Spritesheet ss, BulletManager mgr, Player p, Game g, SoundManager smgr) {
		maxSize = size;
		enemySprites = ss;
		intVars = new int[16];
		floatVars = new double[16];
		enemySpriteReference = new BufferedImage[3][1];
		for(int i = 0; i < 3; i++) {
			enemySpriteReference[i][0] = ss.getSprite(i, 0, 48, 48);
		}
		bmgr = mgr;
		relevantPlayer = p;
		game = g;
		this.smgr = smgr;
		enemies = new ArrayList<Enemy>();
		//TODO: initialize enemySpriteReference
	}
	public void initExplosionAnim(Spritesheet boomAnim, int w, int h, int frames) {
		explosionTimers = new ArrayList<Integer>();
		explosionXcoords = new ArrayList<Double>();
		explosionYcoords = new ArrayList<Double>();
		explosionAnimFrames = new BufferedImage[frames];
		explosionAnimWidth = w;
		explosionAnimHeight = h;
		for(int i = 0; i < frames; i++) {
			explosionAnimFrames[i] = boomAnim.getSprite(i, 0, w, h);
		}
	}


	public void updateEnemies() {
		for(Enemy e : enemies) {
			if(!e.isDisabled()) e.tickEnemy();
		}
		enemies.removeIf(e -> e.isDisabled());
	}
	
	public boolean drawEnemies(Graphics2D g) {
		boolean shouldDrawHP = false;
		for(Enemy e : enemies) {
			if(!e.isDisabled()) {
				int s = e.returnEnemySprite();
				if(s != -1) {
					e.renderEnemy(g, enemySpriteReference[s][0]);
					if(e.testFlag(Enemy.FLAG_BOSS)) shouldDrawHP = true;
				}
			}
		}
		drawExplosions(g);
		return shouldDrawHP;
	}
	public void newExplosionAnim(double x, double y) {
		explosionTimers.add(0);
		explosionXcoords.add(x);
		explosionYcoords.add(y);
	}
	
	private void drawExplosions(Graphics2D g) {
		for(int i = explosionTimers.size() - 1; i >= 0; i--) {
			explosionTimers.set(i, explosionTimers.get(i) + 1);
			int visXpos = explosionXcoords.get(i).intValue() - (explosionAnimWidth / 2);
			visXpos += Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2);
			int visYpos = explosionYcoords.get(i).intValue() - (explosionAnimHeight / 2);
			visYpos += Game.PLAYFIELDYOFFSET;
			g.drawImage(explosionAnimFrames[(explosionTimers.get(i) - 1) / explosionAnimFramerate], visXpos, visYpos, game);
			if(explosionTimers.get(i) >= explosionAnimFramerate * explosionAnimFrames.length) {
				explosionTimers.remove(i);
				explosionXcoords.remove(i);
				explosionYcoords.remove(i);
			}
		}
	}
	
	public void drawHPbars(Graphics2D g) {
		for(Enemy e : enemies) {
			if(!e.isDisabled()) {
				if(e.testFlag(Enemy.FLAG_BOSS)) {
					e.renderHPbar(g);
				}
			}
		}
	}
	public boolean hitEnemies(int x, int y, int hitbox, int damage) {
		double radSum;
		for(Enemy e : enemies) {
			if(!e.isDisabled()) {
				if(!(e.testFlag(Enemy.FLAG_UNHITTABLE) || e.testFlag(Enemy.FLAG_CONTROL_ENEMY))){
					radSum = hitbox + e.hurtboxSize;
					if(Math.pow(e.getXpos() - x, 2) + Math.pow(e.getYpos() - y, 2) <= Math.pow(radSum, 2)) {
						e.addDamage(damage);
						return true;
					}					
				}
			}
		}
		return false;
	}
	public void checkCollision(double x, double y, double rad) {
		double radSum;
		for(Enemy e : enemies) {
			if(!e.isDisabled()) {
				if(!(e.testFlag(Enemy.FLAG_GROUNDED) || e.testFlag(Enemy.FLAG_CONTROL_ENEMY))){
					radSum = rad + e.hitboxSize;
					if(Math.pow(e.getXpos() - x, 2) + Math.pow(e.getYpos() - y, 2) <= Math.pow(radSum, 2)) {
						relevantPlayer.collideWithBullet();
					}
				}
			}
		}
	}

	public void addEnemy(Enemy e, double xpos, double ypos, int HP, boolean mirrored) {
		if(enemies.size() < maxSize) {
			enemies.add(e);
			e.initEnemy(xpos, ypos, HP, mirrored, bmgr, relevantPlayer, game, this, smgr);
			e.initActions();
		}
		
	}
	public void addEnemy(Enemy e, double xpos, double ypos, int HP, boolean mirrored, int subtype) {
		if(enemies.size() < maxSize) {
			enemies.add(e);
			e.initEnemyWithSubtype(xpos, ypos, HP, mirrored, subtype, bmgr, relevantPlayer, game, this, smgr);
			e.initActions();
		}
	}
	public int getIntVar(int index) {
		return intVars[index];
	}
	public void setIntVar(int index, int value) {
		intVars[index] = value;
	}
	public double getFloatVar(int index) {
		return floatVars[index];
	}
	public void setFloatVar(int index, double value) {
		floatVars[index] = value;
	}
	public void clearEnemies() {
		enemies.removeIf(
			e -> !e.testFlag(Enemy.FLAG_CONTROL_ENEMY) 
				&& !e.testFlag(Enemy.FLAG_DIALOGUE_IMMUNE)
				);
	}
	public void reset() {
		enemies.clear();
		Arrays.fill(intVars, 0);
		Arrays.fill(floatVars, 0);
	}
}