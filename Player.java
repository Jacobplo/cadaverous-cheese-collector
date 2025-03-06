import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// Class to define player tile objects.
public class Player extends Rectangle2D.Double{
	double xPos;
	double yPos;
	double width;
	double height;
	Image image;
	
	// Variables to be used for animation.
	int frame = 0;
	boolean facingRight = true;
	
	// Constructor.
	Player(double xPos, double yPos, double width, double height) throws IOException{
		super(xPos, yPos, width, height);
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		image = ImageIO.read(new File("resources" + File.separator + "player.png"));
		image = image.getScaledInstance((int)width, (int)height, Image.SCALE_DEFAULT);
	}
	
	// Moves the player, changing player direction based on move direction. Does not move if there has been a collision.
	public void move(int xChange, int yChange, boolean collision) {
		xPos += xChange;
		yPos += yChange;
		
		if(collision) return;
		if(xPos + xChange > xPos && !facingRight) {
			width *= -1;
			facingRight = true;
		}
		if(xPos + xChange < xPos && facingRight) {
			width *= -1;
			facingRight = false;
		}
	}
	
	// Draws the player to the screen, accounting for a flipped sprite.
	public void draw(Graphics graphics) throws IOException{
		int xPosTemp = (int)xPos;
		if(!facingRight) {
			xPosTemp -= width;
		}
		graphics.drawImage(image, xPosTemp, (int)yPos, (int)width, (int)height, null);
	}
	
	// Checks for player collision with an input tile.
	public boolean collides(Rectangle2D.Double rect) {
		if(this.xPos == rect.x && this.yPos == rect.y) {
			return true;
		}
		return false;
	}
	
	// Idle animation for the player based on frame count.
	public void animate() throws IOException{
		if(frame == 25) {
			image = ImageIO.read(new File("resources" + File.separator + "animation.png"));
			image = image.getScaledInstance((int)width, (int)height, Image.SCALE_DEFAULT);
		}
		else if(frame == 50) {
			image = ImageIO.read(new File("resources" + File.separator + "player.png"));
			image = image.getScaledInstance((int)width, (int)height, Image.SCALE_DEFAULT);
			frame = 0;
		}
		++frame;
	}
}// End class.