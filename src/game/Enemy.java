package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Enemy {
	BulletManager bulletMGR;
	Player targetPlayer;
	Game game;
	SoundManager SoundMGR;
	EnemyManager parentMGR;
	EnemyMovementInterpolator interpolator;
	
	
	EnemyScript script;
	int scriptPosition;
	int opcode;
	HashMap<String, String> variables;
	
	final int numSpawners = 16;
	int sprite;
	public double xpos;
	public double ypos;
	
	public double angle;
	public double speed;
	public double accel;
	
	public double xvel;
	public double yvel;
	public double xaccel;
	public double yaccel;
	protected int movementType; //0 = angle and speed, 1 = xSpeed and ySpeed
	protected int enemyTimer;
	protected int waitTimer;
	protected int HP;
	protected int maxHP;
	int damageToTake;
	protected int framesTillDespawnOffscreen = 0;
	int movementTimer1 = -1;
	int movementTimer2 = -1;
	
	
	protected int renderSize; //radius
	protected int size;
	protected double hitboxSize; //radius
	public int hurtboxSize; //radius
	boolean disabled;
	BulletSpawner[] spawners = new BulletSpawner[numSpawners];
	

	public Enemy(BulletManager bmgr, Player p, Game g, EnemyManager emgr, SoundManager smgr) {
		bulletMGR = bmgr;
		SoundMGR = smgr;
		targetPlayer = p;
		game = g;
		parentMGR = emgr;
		interpolator = new EnemyMovementInterpolator(this);
		scriptPosition = 0;
		variables = new HashMap<String, String>();
		
		disabled = true;
		sprite = 0;
		xpos = -1;
		ypos = -1;
		
		angle = 0;
		speed = 0;
		accel = 0;
		
		xvel = 0;
		yvel = 0;
		xaccel = 0;
		yaccel = 0;
		
		HP = 1;
		maxHP = 1;
		
		renderSize = 24;
		size = 2 * renderSize;
		for(int i = 0; i < numSpawners; i++) {
			spawners[i] = new BulletSpawner(bulletMGR, targetPlayer, game);
		}		
		hitboxSize = renderSize;
		hurtboxSize = size / 2;
		initActions();
	}
	
	public void initActions() {
		initEnemy(0, 100, 50);
		setEnemySprite(1);
	}
	
	public void initEnemy(double x, double y, int health) {
		disabled = false;
		
		xpos = x;
		ypos = y;
		HP = health;
		maxHP = health;
		scriptPosition = 0;
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].reInit();
		}
		variables.clear();
		framesTillDespawnOffscreen = 50;
		
		enemyTimer = 0;
		waitTimer = 0;
		movementTimer1 = -1;
		movementTimer2 = -1;
		
		angle = 0;
		speed = 0;
		accel = 0;
		
		xvel = 0;
		yvel = 0;
		xaccel = 0;
		yaccel = 0;
		
	}
	
	public void setEnemyScript(EnemyScript es) {
		script = es;
	}
	public void setEnemySprite(int spr) {
		sprite = spr;
	}
	
	public void tickEnemy() {
		enemyTimer++;
		takeDamage();
		this.doEnemyActions();
		this.processEnemyMovement();
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].tickSpawner();
		}
		framesTillDespawnOffscreen--;
		if(framesTillDespawnOffscreen <= 0 && game.isOutsidePlayfield(xpos, ypos, size)) {
			disabled = true;
		}
	}
	
	protected void doEnemyActions() {
		while(waitTimer <= enemyTimer) {
			try{
				executeScript();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void processEnemyMovement() {
		speed += accel;
		xvel += xaccel;
		yvel += yaccel;
		interpolator.handleMovement();
		
		switch(movementType) {
		case 0:
			xpos += Math.cos(angle) * speed;
			ypos += Math.sin(angle) * speed;
			break;
		case 1:
			xpos += xvel;
			ypos += yvel;
			break;
		default:
			break;
		}
	}
	
	public int returnEnemySprite() {
		return sprite;
	}
	private void takeDamage() {
		HP -= damageToTake;
		damageToTake = 0;
		if(HP <= 0) {
			onDeath();
			disabled = true;
		}
		
	}
	public void addDamage(int damage) {
		damageToTake += damage;
	}
	
	public void renderEnemy(Graphics g, BufferedImage b) {
		g.drawImage(b, (int)(xpos - renderSize + Game.PLAYFIELDXOFFSET + (Game.PLAYFIELDWIDTH / 2)),
				(int)(ypos - renderSize + Game.PLAYFIELDYOFFSET), game);
	}
	
	private void onDeath() {
		
	}
	public boolean isDisabled() {
		return disabled;
	}
	

	
	
	
	public void setPosAbs(double x, double y) {
		xpos = x;
		ypos = y;
	}
	public void setPosRel(double x, double y) {
		xpos += x;
		ypos += y;
	}
	public void setPosAbsTime(double x, double y, int t, int mode) {
		interpolator.moveOverTime(x, y, t, mode);
	}
	public void setPosRelTime(double x, double y, int t, int mode) {
		interpolator.moveOverTime(x + xpos, y + ypos, t, mode);
	}
	
	protected int getIntFromScript(int pos) {
		int toRet = 0;
		String s = script.getValueAtPos(pos);
		try {
			if(variables.containsKey(s)) {
				toRet = Integer.parseInt(variables.get(s));
			}else {
				toRet = Integer.parseInt(s);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return toRet;
	}
	protected double getFloatFromScript(int pos) {
		double toRet = 0;
		String s = script.getValueAtPos(pos);
		try {
			if(variables.containsKey(s)) {
				toRet = Double.parseDouble(variables.get(s));
			}else {
				toRet = Double.parseDouble(s);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return toRet;
	}
	
	public void executeScript() throws SCCLexception {
		if(scriptPosition >= script.getScriptLength()) {
			disabled = true;
			return;
		}
		opcode = getIntFromScript(scriptPosition);
		int intArg1;
		int intArg2;
		int intArg3;
		double doubleArg1;
		double doubleArg2;
		switch(opcode) {
		case Opcodes.nop:
			//System.out.println("nop executed");
			scriptPosition++;
			break;
		case Opcodes.wait:
			intArg1 = getIntFromScript(scriptPosition + 1);
			scriptPosition += 2;
			waitTimer += intArg1;
			break;
		case Opcodes.resetShooter:
			intArg1 = getIntFromScript(scriptPosition + 1);
			scriptPosition += 2;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].reInit();
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 2));
			}
			break;
		case Opcodes.activate:
			intArg1 = getIntFromScript(scriptPosition + 1);
			scriptPosition += 2;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].activate();
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 2));
			}
			break;
		case Opcodes.setSprites:
			intArg1 = getIntFromScript(scriptPosition + 1);
			intArg2 = getIntFromScript(scriptPosition + 2);
			intArg3 = getIntFromScript(scriptPosition + 3);
			scriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setTypeAndColor(intArg2, intArg3);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 4));
			}
			break;
		case Opcodes.setRelativeShotOffset:
			intArg1 = getIntFromScript(scriptPosition + 1);
			doubleArg1 = getFloatFromScript(scriptPosition + 2);
			doubleArg2 = getFloatFromScript(scriptPosition + 3);
			scriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setRelativePos(doubleArg1, doubleArg2);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 4));
			}
			break;
			
		case Opcodes.setAngles:
			intArg1 = getIntFromScript(scriptPosition + 1);
			doubleArg1 = getFloatFromScript(scriptPosition + 2);
			doubleArg2 = getFloatFromScript(scriptPosition + 3);
			scriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setAngles(doubleArg1, doubleArg2);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 4));
			}
			break;
		case Opcodes.setSpeeds:
			intArg1 = getIntFromScript(scriptPosition + 1);
			doubleArg1 = getFloatFromScript(scriptPosition + 2);
			doubleArg2 = getFloatFromScript(scriptPosition + 3);
			scriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setSpeeds(doubleArg1, doubleArg2);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 4));
			}
			break;
		case Opcodes.setCounts:
			intArg1 = getIntFromScript(scriptPosition + 1);
			intArg2 = getIntFromScript(scriptPosition + 2);
			intArg3 = getIntFromScript(scriptPosition + 3);
			scriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setBulletCounts(intArg2, intArg3);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 4));
			}
			break;
		case Opcodes.setAimMode:
			intArg1 = getIntFromScript(scriptPosition + 1);
			intArg2 = getIntFromScript(scriptPosition + 2);
			scriptPosition += 3;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setMode(intArg2);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 3));
			}
			break;
		case Opcodes.setShootDistance:
			intArg1 = getIntFromScript(scriptPosition + 1);
			doubleArg1 = getFloatFromScript(scriptPosition + 2);
			scriptPosition += 3;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setSpawnDistance(doubleArg1);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (scriptPosition - 3));
			}
		default:
			scriptPosition++;
			throw new SCCLexception("Unknown opcode at position " + (scriptPosition - 1));
		}
	}

}
