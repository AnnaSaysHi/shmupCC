package game.enemy;

public class EnemyMovementInterpolator {
	Enemy parent;
	double initialX;
	double initialY;
	double goalX;
	double goalY;
	double currentX;
	double currentY;
	double diffX;
	double diffY;
	int duration;
	int framesSinceStart;
	int interpMode = -1;

	public EnemyMovementInterpolator(Enemy p) {
		parent = p;
	}
	
	public void moveOverTime(double goalX, double goalY, int time, int mode) {
		this.initialX = parent.xpos;
		this.initialY = parent.ypos;
		this.currentX = initialX;
		this.currentY = initialY;
		this.goalX = goalX;
		this.goalY = goalY;
		diffX = goalX - initialX;
		diffY = goalY - initialY;
		this.duration = time;
		framesSinceStart = 0;
		this.interpMode = mode;
	}
	
	public void handleMovement() {
		if(interpMode != -1) {
			framesSinceStart++;
			if(framesSinceStart <= duration) {
				double timeconst = framesSinceStart;
				timeconst = timeconst / duration;
				double mult = getValue(timeconst, interpMode);
				currentX = (mult * diffX) + initialX;
				currentY = (mult * diffY) + initialY;
				parent.xpos = currentX;
				parent.ypos = currentY;
				
			}
		}
		
	}
	
	public double getValue(double v, int mode) {
		switch(mode) {
		case 0:
			return v;
		case 1:
			return v * v;
		case 2:
			return getFlip(v, 1);
		case 3:
			return getSplit(v, 1, 2);
		case 4:
			return getSplit(v, 2, 1);
		default:
			return 0;
		}
	}
	public double getFlip(double v, int mode) {
		return 1 - (getValue(1 - v, mode));
	}
	public double getSplit(double v, int mode1, int mode2) {
		return (v < 0.5 ? 0.5 * getValue(2 * v, mode1) : 0.5 * (1 + getValue(2 * v - 1, mode2)));
	}

}
