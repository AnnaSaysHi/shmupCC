package game;

import java.awt.Color;
import java.awt.Graphics;

public class MenuSceneSelect extends MenuGeneral {

	public MenuSceneSelect(Game g, KBinputHandler kbh) {
		super(g, kbh);
		menuEntries = 3;
	}
	
	@Override
	public void doSelectedOption() {
		switch(selectedOption) {
		case 0:
			parent.setStage(0);
			this.parent.state = Game.STATE.PLAY;
			break;
		case 1:
			parent.setStage(1);
			this.parent.state = Game.STATE.PLAY;
			break;
		case 2:
			parent.setStage(2);
			this.parent.state = Game.STATE.PLAY;
			break;
		default:
			break;
		}
	}
	@Override
	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString("Meek", 100, 100);
		g.drawString("BoWaP", 100, 150);
		g.drawString("VIV_test", 100, 200);
		g.drawString(">", 90, 100 + 50 * selectedOption);
	}

}
