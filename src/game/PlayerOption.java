package game;

public class PlayerOption implements Interpolable {

	double[] interpolables;
	
	Player parentPlayer;
	double xpos;
	double ypos;
	double visXpos;
	double visYpos;
	double currentOffX;
	double currentOffY;
	
	double angle;
	double renderAngle;
	double behavior;
	InterpolatorGeneral[] interpolators;
	
	public PlayerOption(Player parent) {
		parentPlayer = parent;
		interpolables = new double[6];
		interpolators = new InterpolatorGeneral[2];
		for(int i = 0; i < interpolators.length; i++) {
			interpolators[i] = new InterpolatorGeneral(this);
		}
	}

	public void tick() {
		doInterpolation();
		double[] playercoords = parentPlayer.getPosAndHitbox();
		xpos = playercoords[0] + interpolables[4];
		ypos = playercoords[1] + interpolables[5];
	}
	public void shiftToPos(double newTargetOffX, double newTargetOffY, int time) {
		interpolators[0].interpFloatOverTime(4, newTargetOffX, time, 11);
		interpolators[1].interpFloatOverTime(5, newTargetOffY, time, 11);
	}
	public void snapToPos(double newOffX, double newOffY) {
		interpolators[0].disabled = true;
		interpolators[1].disabled = true;
		interpolables[4] = newOffX;
		interpolables[5] = newOffY;
		
	}
	
	
	private void doInterpolation() {
		for(InterpolatorGeneral interp : interpolators) {
			if (!interp.disabled) {
				interp.tickInterp();
			}
		}
	}
	
	public double[] getPosAndAngle() {
		return new double[] {
				xpos,
				ypos,
				angle
		};
	}
	
	public double[] getInterpolableFloats() {
		return interpolables;
	}
	public void setFloat(int index, double value) {
		interpolables[index] = value;
	}

}
