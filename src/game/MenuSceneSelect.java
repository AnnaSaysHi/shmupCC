package game;

import java.awt.Color;
import java.awt.Graphics;

public class MenuSceneSelect extends MenuGeneral {

	public MenuSceneSelect(Game g, KBinputHandler kbh) {
		super(g, kbh);
		menuEntries = 4;
	}
	
	@Override
	public void doSelectedOption() {
		parent.setStage(selectedOption);
		this.parent.state = Game.STATE.PLAY;
	}
	@Override
	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString("Meek", 100, 100);
		g.drawString("BoWaP", 100, 150);
		g.drawString("VIV_test", 100, 200);
		g.drawString("Enemy Test", 100, 250);
		g.drawString(">", 90, 100 + 50 * selectedOption);
	}

}
