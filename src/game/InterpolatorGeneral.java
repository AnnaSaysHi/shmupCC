package game;

public class InterpolatorGeneral {
	Object parent;
	double initialF;
	double goalF;
	double currentF;
	double diffF;
	int index;
	double[] interpolableFloats;
	int duration;
	int framesSinceStart;
	int interpMode = -1;
	public boolean disabled;

	public InterpolatorGeneral(Object p) {
		parent = p;
		disabled = true;
		if(parent instanceof Interpolable) {
			interpolableFloats = ((Interpolable) parent).getInterpolableFloats();
		}
	}
	
	public void interpFloatOverTime(int index, double goal, int time, int mode) {
		disabled = false;
		this.index = index;
		this.initialF = interpolableFloats[index];
		this.currentF = initialF;
		this.goalF = goal;
		diffF = goal - initialF;
		this.duration = time;
		framesSinceStart = 0;
		this.interpMode = mode;
	}
	
	public void tickInterp() {
		if(interpMode != -1) {
			framesSinceStart++;
			if(framesSinceStart <= duration) {
				double timeconst = framesSinceStart;
				timeconst = timeconst / duration;
				double mult = getValue(timeconst, interpMode);
				currentF = (mult * diffF) + initialF;
				((Interpolable) parent).setFloat(index, currentF);
				
			} else disabled = true;
		}
		
	}
	

	
	private double getValue(double v, int mode) {
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
		case 5:
			return v * v * v;
		case 6:
			return getFlip(v, 5);
		case 7:
			return getSplit(v, 5, 6);
		case 8:
			return getSplit(v, 6, 5);
		case 9:
			return v * v * v * v;
		case 10:
			return getFlip(v, 9);
		case 11:
			return getSplit(v, 9, 10);
		case 12:
			return getSplit(v, 10, 9);
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
