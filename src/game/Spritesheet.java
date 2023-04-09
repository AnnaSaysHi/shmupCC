package game;

import java.awt.image.BufferedImage;

public class Spritesheet {

	private BufferedImage image;
	
	public Spritesheet(BufferedImage ss) {
		this.image = ss;
	}
	
	
	public BufferedImage getSprite(int column, int row, int width, int height) {
		
		BufferedImage sprite = image.getSubimage(column * width, row * height, width, height);
		return sprite;
		
	}
}
