package game;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

public class Spritesheet {

	private BufferedImage image;
	
	public Spritesheet(BufferedImage ss) {
		this.image = ss;
	}
	
	
	public BufferedImage getSprite(int column, int row, int width, int height) throws RasterFormatException {
		
		BufferedImage sprite = image.getSubimage(column * width, row * height, width, height);
		return sprite;
		
	}
	public BufferedImage getSpriteFixedCoords(int xOrigin, int yOrigin, int size) throws RasterFormatException{
		return image.getSubimage(xOrigin, yOrigin, size, size);
	}
}
