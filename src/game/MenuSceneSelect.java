package game;

import java.awt.Color;
import java.awt.Graphics;

public class MenuSceneSelect extends MenuGeneral {

	public MenuSceneSelect(Game g, KBinputHandler kbh, SoundManager smgr) {
		super(g, kbh, smgr);
		menuEntries = g.SCRIPT_MAX;
	}
	
	@Override
	public void doSelectedOption() {
		parent.setStage(selectedOption);
		this.parent.state = Game.STATE.PLAY;
	}
	@Override
	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString("Meek [Original pattern by: ZUN, adapted to ShmupCC by Anna says hi]", 100, 100);
		g.drawString("BoWaP [Original pattern by: ZUN, adapted to ShmupCC by Anna says hi]", 100, 150);
		g.drawString("VIV_test [Original pattern by: pbg, adapted to ShmupCC by clb_184]", 100, 200);
		g.drawString("Enemy Test [test pattern]", 100, 250);
		g.drawString("Showcase Pattern 1 [Original pattern by: pbg, adapted to ShmupCC by clb_184]", 100, 300);
		g.drawString("Showcase_2 [Pattern by: Anna says hi]", 100, 350);
		g.drawString(">", 90, 100 + 50 * selectedOption);
	}

}
