package game;

import java.awt.Color;
import java.awt.Graphics;

public class MenuPause extends MenuGeneral {

	public MenuPause(Game g, KBinputHandler kbh) {
		super(g, kbh);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void doSelectedOption() {
		switch(selectedOption) {
		case 0:
			parent.unpause();
			break;
		case 1:
			parent.restartStage();
			break;
		case 2:
			parent.returnToMenu();
			break;
		}
	}
	
	@Override
	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString("Unpause", 100, 100);
		g.drawString("Restart", 100, 150);
		g.drawString("Quit", 100, 200);
		g.drawString(">", 90, 100 + 50 * selectedOption);
	}

}
