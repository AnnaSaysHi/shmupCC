package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Pattern;

public class Enemy {
	BulletManager bulletMGR;
	Player targetPlayer;
	Game game;
	SoundManager SoundMGR;
	EnemyManager parentMGR;
	EnemyMovementInterpolator interpolator;
	
	
	EnemyScript script;
	int workingScriptPosition;
	int opcode;
	HashMap<String, String> variables;
	String workingSubName;
	ArrayList<Stack<String>> asyncCallStack;
	ArrayList<Integer> asyncScriptPosition;
	int asyncSlotNum;
	ArrayList<Integer> asyncWaitTimer;
	
	final int numSpawners = 16;
	private static final double DEG_TO_RAD = 0.017453292519943295; 
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
	protected int HP;
	protected int maxHP;
	int damageToTake;
	int movementTimer1 = -1;
	int movementTimer2 = -1;
	
	int flags;
	/* flag definitions
	 * 
	 * 0: Enemy has no hurtbox (shots pass through enemy)
	 * 1: Enemy has no hitbox (cannot kill player via contact)
	 * 2: Enemy does not despawn offscreen
	 * 3: Enemy becomes a control enemy. Combines the effects of flags 0, 1, 2, and 4.
	 * 4: Enemy cannot be deleted by dialogue or EnmKillAll. (not implemented yet)
	 * 5: Enemy retains its hurtbox, but becomes invincible. (not implemented yet)
	 * 
	 * 
	 * */
	
	
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
		workingScriptPosition = 0;
		variables = new HashMap<String, String>();
		workingSubName = "";
		asyncCallStack = new ArrayList<Stack<String>>();
		asyncScriptPosition = new ArrayList<Integer>();
		asyncWaitTimer = new ArrayList<Integer>();
		asyncSlotNum = 0;
		
		
		disabled = true;
		sprite = -1;
		xpos = -1;
		ypos = -1;
		flags = 0x00000000;
		
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
		
	}
	
	public void initEnemy(double x, double y, int health, EnemyScript scriptStruct, String subName) {
		disabled = false;
		sprite = -1;
		xpos = x;
		ypos = y;
		HP = health;
		maxHP = health;
		workingScriptPosition = 0;
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].reInit();
		}
		variables.clear();
		script = scriptStruct;
		workingSubName = subName;
		asyncCallStack.clear();
		asyncScriptPosition.clear();
		asyncWaitTimer.clear();
		
		asyncCallStack.add(new Stack<String>());
		asyncScriptPosition.add(0);
		asyncWaitTimer.add(0);
		
		asyncCallStack.get(0).push("0");
		asyncCallStack.get(0).push(subName);
		
		flags = 0x00000000;
		
		enemyTimer = 0;
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
	
	public void setEnemyScript(EnemyScript scriptStruct, String subName) {
		script = scriptStruct;
		asyncCallStack.get(0).push("0");
		asyncCallStack.get(0).push(subName);
		this.workingSubName = subName;
	}
	public void setEnemySprite(int spr) {
		sprite = spr;
	}
	public void swapCallStack(int stackSlot) {
		Integer i = asyncWaitTimer.get(asyncSlotNum);
		if(i != null) {
			asyncCallStack.get(asyncSlotNum).push(Integer.toString(workingScriptPosition));
			asyncCallStack.get(asyncSlotNum).push(workingSubName);
		}
		asyncSlotNum = stackSlot;
		workingSubName = asyncCallStack.get(asyncSlotNum).pop();
		workingScriptPosition = Integer.parseInt(asyncCallStack.get(asyncSlotNum).pop());
	}
	
	public void tickEnemy() {
		enemyTimer++;
		takeDamage();
		this.doEnemyActions();
		this.processEnemyMovement();
		for(int i = 0; i < numSpawners; i++) {
			spawners[i].tickSpawner();
		}
		boolean diesOffscreen = !(testFlag(2) || testFlag(3));
		
		if(diesOffscreen && game.isOutsidePlayfield(xpos, ypos, size)) {
			disabled = true;
		}
	}
	
	protected void doEnemyActions() {
		int n = asyncCallStack.size();
		for(int j = 0; j < n; j++) {
			swapCallStack(j);
			Integer k = asyncWaitTimer.get(asyncSlotNum);
			if(!(k.equals(null))) {
				while(true) {
					Integer l = asyncWaitTimer.get(asyncSlotNum);
					if(l == null) break;
					if(l > enemyTimer || disabled) break;
					
					try{
						executeScript();
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				
			}
			
		}
		swapCallStack(0);
		while(asyncCallStack.remove(null)) {
			int i = 1;
		}
		while(asyncWaitTimer.remove(null)) {
			
		}
		while(asyncScriptPosition.remove(null)) {
			
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
	public void disable() {
		disabled = true;
	}
	

	public boolean testFlag(int flagNum) {
		int bitmask = 0x00000001 << flagNum;
		return (!((flags & bitmask) == 0));
	}
	protected void terminateAsync(int slot) {
		if(slot == 0) {
			disabled = true;
		}else {
			asyncCallStack.set(slot, null);
			asyncScriptPosition.set(slot, null);
			asyncWaitTimer.set(slot, null);
		}
		
	}
	
	
	public void setPosAbs(double x, double y) {
		xpos = x;
		ypos = y;
	}
	public void setPosRel(double x, double y) {
		xpos += x;
		ypos += y;
	}
	public void setPosAbsTime(int t, int mode, double x, double y) {
		interpolator.moveOverTime(x, y, t, mode);
	}
	public void setPosRelTime(int t, int mode, double x, double y) {
		interpolator.moveOverTime(x + xpos, y + ypos, t, mode);
	}
	
	protected int getIntFromScript(int pos) {
		int toRet = 0;
		String s = script.getValueAtPos(workingSubName, pos);
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
	protected double getDoubleFromScript(int pos) {
		double toRet = 0;
		String s = script.getValueAtPos(workingSubName, pos);
		try {
			if(variables.containsKey(s)) {
				toRet = Double.parseDouble(variables.get(s));
			}else {
				if(Pattern.matches("r-?[0-9]+[.][0-9]+", s) || Pattern.matches("r-?[0-9]+", s)) {
					s = s.substring(1);
					toRet = Double.parseDouble(s);
					toRet = toRet * Enemy.DEG_TO_RAD;
				}else{
					toRet = Double.parseDouble(s);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return toRet;
	}
	
	//GIANT SWITCH STATEMENT OF DOOM
	public void executeScript() throws SCCLexception {
		if(workingScriptPosition >= script.getSubLength(workingSubName)) opcode = Opcodes.ret;
		else opcode = getIntFromScript(workingScriptPosition);
		String stringArg1;
		String stringArg2;
		int intArg1;
		int intArg2;
		int intArg3;
		double doubleArg1;
		double doubleArg2;
		switch(opcode) {
		
		
		//OPCODES 000-100, CONTROL FLOW AND MISCELLANEOUS STUFF
		case Opcodes.nop:
			//System.out.println("nop executed");
			workingScriptPosition++;
			break;
		case Opcodes.ret:
			if(asyncCallStack.get(asyncSlotNum).isEmpty()) {
				terminateAsync(asyncSlotNum);
			}
			else {
				workingSubName = asyncCallStack.get(asyncSlotNum).pop();
				workingScriptPosition = Integer.parseInt(asyncCallStack.get(asyncSlotNum).pop());
			}
			break;
		case Opcodes.declareVariable:
			stringArg1 = script.getValueAtPos(workingSubName, workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(variables.containsKey(stringArg1)) {
				throw new SCCLexception("Duplicate declaration of variable " + stringArg1 + " at "+ (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}else {
				variables.put(stringArg1, "");
			}
			break;
		case Opcodes.declareAndInitialize:
			stringArg1 = script.getValueAtPos(workingSubName, workingScriptPosition + 1);
			stringArg2 = script.getValueAtPos(workingSubName, workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(variables.containsKey(stringArg1)) {
				throw new SCCLexception("Duplicate declaration of variable " + stringArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				if(Pattern.matches("r-?[0-9]+[.][0-9]+", stringArg2) || Pattern.matches("r-?[0-9]+", stringArg2)) {
					stringArg2 = stringArg2.substring(1);
					double toStore = Double.parseDouble(stringArg2);
					toStore = toStore * Enemy.DEG_TO_RAD;
					variables.put(stringArg1, Double.toString(toStore));
				}else{
					variables.put(stringArg1, stringArg2);
				}
			}
			break;
		case Opcodes.closeVariable:
			stringArg1 = script.getValueAtPos(workingSubName, workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(!variables.containsKey(stringArg1)) {
				throw new SCCLexception("Could not close undeclared variable " + stringArg1 + " at "+ (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}else {
				variables.remove(stringArg1);
			}
			break;
			
		case Opcodes.call:
			workingScriptPosition += 2;
			asyncCallStack.get(asyncSlotNum).push(Integer.toString(workingScriptPosition));
			asyncCallStack.get(asyncSlotNum).push(workingSubName);
			workingSubName = script.getValueAtPos(workingSubName, workingScriptPosition - 1);
			workingScriptPosition = 0;
			break;
		case Opcodes.callAsync:
			workingScriptPosition += 2;
			Stack<String> newSub = new Stack<String>();
			newSub.push("0");
			newSub.push(script.getValueAtPos(workingSubName, workingScriptPosition - 1));
			asyncCallStack.add(newSub);
			asyncCallStack.get(asyncSlotNum).pop();
			asyncCallStack.get(asyncSlotNum).pop();
			asyncCallStack.get(asyncSlotNum).push(Integer.toString(workingScriptPosition));
			asyncCallStack.get(asyncSlotNum).push(workingSubName);
			asyncScriptPosition.set(asyncSlotNum, workingScriptPosition);
			asyncScriptPosition.add(0);
			asyncWaitTimer.add(enemyTimer);
			break;
			
		case Opcodes.wait:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			asyncWaitTimer.set(asyncSlotNum, enemyTimer + intArg1);
			break;
			
		case Opcodes.setVarString:
			stringArg1 = script.getValueAtPos(workingSubName, workingScriptPosition + 1);
			stringArg2 = script.getValueAtPos(workingSubName, workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(!variables.containsKey(stringArg1)) {
				throw new SCCLexception("Attempted assignment of undeclared variable " + stringArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				variables.replace(stringArg1, stringArg2);
			}
			break;
		case Opcodes.setVarInt:
			stringArg1 = script.getValueAtPos(workingSubName, workingScriptPosition + 1);
			intArg1 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(!variables.containsKey(stringArg1)) {
				throw new SCCLexception("Attempted assignment of undeclared variable " + stringArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				variables.replace(stringArg1, Integer.toString(intArg1));
			}
			break;
		case Opcodes.setVarFloat:
			stringArg1 = script.getValueAtPos(workingSubName, workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(!variables.containsKey(stringArg1)) {
				throw new SCCLexception("Attempted assignment of undeclared variable " + stringArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				variables.replace(stringArg1, Double.toString(doubleArg1));
			}
			break;
			
		//OPCODES 300-399, ENEMY CREATION
		case Opcodes.enemyCreateRel:
			stringArg1 = script.getValueAtPos(workingSubName, workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			intArg1 = getIntFromScript(workingScriptPosition + 4);
			parentMGR.addEnemy(stringArg1, doubleArg1 + xpos, doubleArg2 + ypos, intArg1);
			workingScriptPosition += 5;
			break;
		case Opcodes.enemyCreateAbs:
			stringArg1 = script.getValueAtPos(workingSubName, workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			intArg1 = getIntFromScript(workingScriptPosition + 4);
			parentMGR.addEnemy(stringArg1, doubleArg1 + xpos, doubleArg2 + ypos, intArg1);
			workingScriptPosition += 5;
			break;
		case Opcodes.enemySetSprite:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			sprite = intArg1;
			workingScriptPosition += 2;
			break;
			
			
		//OPCODES 400-499, ENEMY MOVEMENT
		case Opcodes.setPosAbs:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			xpos = doubleArg1;
			ypos = doubleArg2;
			workingScriptPosition += 3;
			break;
		case Opcodes.setPosAbsTime:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 3);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 4);
			setPosAbsTime(intArg1, intArg2, doubleArg1, doubleArg2);
			workingScriptPosition += 5;
			break;
		case Opcodes.setPosRel:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			xpos += doubleArg1;
			ypos += doubleArg2;
			workingScriptPosition += 3;
			break;
		case Opcodes.setPosRelTime:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 3);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 4);
			setPosRelTime(intArg1, intArg2, doubleArg1, doubleArg2);
			workingScriptPosition += 5;
			break;
			
			
			
		//OPCODES 500-599, ENEMY PROPERTY MANAGEMENT
		case Opcodes.flagSet:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			flags = flags | intArg1;
			break;
		case Opcodes.flagClear:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			flags = flags & ~intArg1;
			break;
			
					
			
		//OPCODES 600-699, BULLET-RELATED STUFF
		case Opcodes.resetShooter:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].reInit();
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}
			break;
		case Opcodes.activate:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].activate();
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}
			break;
		case Opcodes.setSprites:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setTypeAndColor(intArg2, intArg3);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}
			break;
		case Opcodes.setRelativeShotOffset:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setRelativePos(doubleArg1, doubleArg2);
				spawners[intArg1].setParentEnemy(this);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}
			break;
			
		case Opcodes.setAngles:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setAngles(doubleArg1, doubleArg2);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}
			break;
		case Opcodes.setSpeeds:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setSpeeds(doubleArg1, doubleArg2);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}
			break;
		case Opcodes.setCounts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setBulletCounts(intArg2, intArg3);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}
			break;
		case Opcodes.setAimMode:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setMode(intArg2);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}
			break;
		case Opcodes.setShotFrequency:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setActivationFrequency(intArg2);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}
			break;
		case Opcodes.setShootDistance:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setSpawnDistance(doubleArg1);
			}else {
				throw new SCCLexception("Spawner index out of range at position " + (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}
			break;
		default:
			workingScriptPosition++;
			throw new SCCLexception("Unknown opcode at position " + (workingScriptPosition - 1) + " in subroutine " + workingSubName);
		}
	}

}
