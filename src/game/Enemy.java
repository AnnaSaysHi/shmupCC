package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
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
	boolean recentEval;
	String workingSubName;
	ArrayList<Stack<String>> asyncCallStack;
	ArrayList<Integer> asyncScriptPosition;
	int asyncSlotNum;
	ArrayList<Integer> asyncWaitTimer;
	int[] intVariables;
	double[] doubleVariables;
	private static final int NUM_INT_VARIABLES = 16;
	private static final int NUM_DOUBLE_VARIABLES = 16;
	
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
		workingSubName = "";
		asyncCallStack = new ArrayList<Stack<String>>();
		asyncScriptPosition = new ArrayList<Integer>();
		asyncWaitTimer = new ArrayList<Integer>();
		asyncSlotNum = 0;
		intVariables = new int[Enemy.NUM_INT_VARIABLES];
		doubleVariables = new double[Enemy.NUM_DOUBLE_VARIABLES];
		recentEval = false;
		
		
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
		

		movementType = 1;
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
		Arrays.fill(intVariables, 0);
		Arrays.fill(doubleVariables, 0);
		script = scriptStruct;
		workingSubName = subName;
		asyncCallStack.clear();
		asyncScriptPosition.clear();
		asyncWaitTimer.clear();
		recentEval = false;
		
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
		
		movementType = 1;
		
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
			if(Pattern.matches("[$]I[0-9]+", s)){
				s = s.substring(2);
				int index = Integer.parseInt(s);
				if(index >= 0 && index < Enemy.NUM_INT_VARIABLES) {
					toRet = intVariables[index];
				}else toRet = 0;
			} else if(Pattern.matches("[$]F[0-9]+", s)){
				s = s.substring(2);
				int index = Integer.parseInt(s);
				if(index >= 0 && index < Enemy.NUM_DOUBLE_VARIABLES) {
					toRet = (int) doubleVariables[index];
				} else toRet = 0;
				toRet = Integer.parseInt(s);
			} else {
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
			if(Pattern.matches("[$]I[0-9]+", s)){
				s = s.substring(2);
				int index = Integer.parseInt(s);
				if(index >= 0 && index < Enemy.NUM_INT_VARIABLES) {
					toRet = intVariables[index];
				}else toRet = 0;
			} else if(Pattern.matches("[$]F[0-9]+", s)){
				s = s.substring(2);
				int index = Integer.parseInt(s);
				if(index >= 0 && index < Enemy.NUM_DOUBLE_VARIABLES) {
					toRet = doubleVariables[index];
				} else toRet = 0;
			} else {
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
		int intArg1;
		int intArg2;
		int intArg3;
		double doubleArg1;
		double doubleArg2;
		double doubleArg3;
		double doubleArg4;
		switch(opcode) {
		
		
		//OPCODES 000-100, CONTROL FLOW AND VARIABLE-RELATED STUFF
		case Opcodes.nop:
			System.out.println("nop executed");
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
			
		case Opcodes.call:
			workingScriptPosition += 2;
			asyncCallStack.get(asyncSlotNum).push(Integer.toString(workingScriptPosition));
			asyncCallStack.get(asyncSlotNum).push(workingSubName);
			workingSubName = script.getValueAtPos(workingSubName, workingScriptPosition - 1);
			workingScriptPosition = 0;
			break;
			
		case Opcodes.jump:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += intArg1;
			break;
		case Opcodes.jumpTrue:
			if(recentEval) {
				intArg1 = getIntFromScript(workingScriptPosition + 1);
				workingScriptPosition += intArg1;
			}else {
				workingScriptPosition += 2;
			}
			break;
		case Opcodes.jumpFalse:
			if(!recentEval) {
				intArg1 = getIntFromScript(workingScriptPosition + 1);
				workingScriptPosition += intArg1;
			}else {
				workingScriptPosition += 2;
			}
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
			

		case Opcodes.setVarInt:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2;
			}
			break;
		case Opcodes.setVarFloat:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = doubleArg1;
			}
			break;
		case Opcodes.addInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2 + intArg3;
			}
			break;
		case Opcodes.addFloats:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = doubleArg1 + doubleArg2;
			}
			break;
		case Opcodes.subtractInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2 - intArg3;
			}
			break;
		case Opcodes.subtractFloats:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = doubleArg1 - doubleArg2;
			}
			break;
		case Opcodes.multInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2 * intArg3;
			}
			break;
		case Opcodes.multFloats:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = doubleArg1 * doubleArg2;
			}
			break;
		case Opcodes.divInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else if(intArg3 == 0) {
				throw new SCCLexception("Attempted division by 0 at " + (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2 / intArg3;
			}
			break;
		case Opcodes.divFloats:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else if (doubleArg2 == 0.0){
				throw new SCCLexception("Attempted division by 0 at " + (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = doubleArg1 + doubleArg2;
			}
			break;
		case Opcodes.modInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else if(intArg3 == 0) {
				throw new SCCLexception("Attempted modulo by 0 at " + (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2 % intArg3;
			}
			break;
			
			
		case Opcodes.equalsInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (intArg1 == intArg2);
			break;
		case Opcodes.equalsFloats:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (doubleArg1 == doubleArg2);
			break;
		case Opcodes.notEqualsInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (intArg1 != intArg2);
			break;
		case Opcodes.notEqualsFloats:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (doubleArg1 != doubleArg2);
			break;
		case Opcodes.lessThanInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (intArg1 < intArg2);
			break;
		case Opcodes.lessThanFloats:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (doubleArg1 < doubleArg2);
			break;
		case Opcodes.lessEqualsInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (intArg1 <= intArg2);
			break;
		case Opcodes.lessEqualsFloats:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (doubleArg1 <= doubleArg2);
			break;
		case Opcodes.greaterThanInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (intArg1 > intArg2);
			break;
		case Opcodes.greaterThanFloats:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (doubleArg1 > doubleArg2);
			break;
		case Opcodes.greaterEqualsInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (intArg1 >= intArg2);
			break;
		case Opcodes.greaterEqualsFloats:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			recentEval = (doubleArg1 >= doubleArg2);
			break;
			
		case Opcodes.bitwiseXorInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2 ^ intArg3;
			}
			break;
		case Opcodes.bitwiseOrInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2 | intArg3;
			}
			break;
		case Opcodes.bitwiseAndInts:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			intArg3 = getIntFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intArg2 & intArg3;
			}
			break;
		case Opcodes.decrementVariable:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = intVariables[intArg1] - 1;
			}
			break;
		case Opcodes.sineArg:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = Math.sin(doubleArg1);
			}
			break;
		case Opcodes.cosineArg:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = Math.cos(doubleArg1);
			}
			break;
		case Opcodes.circlePos:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 3);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 4);
			workingScriptPosition += 5;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 5) + " in subroutine " + workingSubName);
			}else if (intArg2 < 0 || intArg2 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg2 + " at "+ (workingScriptPosition - 5) + " in subroutine " + workingSubName);
			}else{
				doubleVariables[intArg1] = (Math.cos(doubleArg1) * doubleArg2);
				doubleVariables[intArg2] = (Math.sin(doubleArg1) * doubleArg2);
			}
			break;
		case Opcodes.normalizeAngle:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = doubleVariables[intArg1] % (Math.PI * 2);
			}
			break;
		case Opcodes.absInt:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range integer " + intArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = Math.abs(intArg2);
			}
			break;
		case Opcodes.absFloat:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = Math.abs(doubleArg1);
			}
			break;
		case Opcodes.angleFromPoints:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			doubleArg3 = getDoubleFromScript(workingScriptPosition + 4);
			doubleArg4 = getDoubleFromScript(workingScriptPosition + 5);
			workingScriptPosition += 6;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 6) + " in subroutine " + workingSubName);
			}else{
				doubleVariables[intArg1] = Math.atan2(doubleArg1 - doubleArg3, doubleArg2 - doubleArg4);
			}
			break;
		case Opcodes.sqrt:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 3) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = Math.sqrt(doubleArg1);
			}
			break;
		case Opcodes.angleToPlayerSelf:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = game.getAngleToPlayer(xpos, ypos);
			}
			break;
		case Opcodes.getRandomInt:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_INT_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range int " + intArg1 + " at "+ (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}else {
				intVariables[intArg1] = game.FetchRNG().nextInt();
			}
			break;
		case Opcodes.getRandomFloat:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = game.FetchRNG().nextDouble();
			}
			break;
		case Opcodes.getRandomAngle:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			workingScriptPosition += 2;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 2) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = game.FetchRNG().nextDouble(Math.PI * -1, Math.PI);
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
		case Opcodes.setSpeedAndAngle:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			speed = doubleArg1;
			angle = doubleArg2;
			movementType = 0;
			workingScriptPosition += 3;
			break;
		case Opcodes.setXVelAndYVel:
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 1);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 2);
			xvel = doubleArg1;
			yvel = doubleArg2;
			movementType = 1;
			workingScriptPosition += 3;
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
		case Opcodes.setShotSound:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			intArg2 = getIntFromScript(workingScriptPosition + 2);
			workingScriptPosition += 3;
			if(intArg1 >= 0 && intArg1 < numSpawners) {
				spawners[intArg1].setSound(intArg2);
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
		case Opcodes.angleToPlayerArgs:
			intArg1 = getIntFromScript(workingScriptPosition + 1);
			doubleArg1 = getDoubleFromScript(workingScriptPosition + 2);
			doubleArg2 = getDoubleFromScript(workingScriptPosition + 3);
			workingScriptPosition += 4;
			if(intArg1 < 0 || intArg1 >= Enemy.NUM_DOUBLE_VARIABLES) {
				throw new SCCLexception("Attempted assignment of out of range double " + intArg1 + " at "+ (workingScriptPosition - 4) + " in subroutine " + workingSubName);
			}else {
				doubleVariables[intArg1] = game.getAngleToPlayer(doubleArg1, doubleArg2);
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
