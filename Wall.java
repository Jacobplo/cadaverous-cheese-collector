import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// Class to define wall tile objects.
public class Wall extends Rectangle2D.Double{
	double xPos;
	double yPos;
	double width;
	double height;
	int wallType;
	Image image;
	
	// Constructor.
	Wall(double xPos, double yPos, double width, double height, int wallType) throws IOException{
		super(xPos, yPos, width, height);
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.wallType = wallType;

		// Determines the type of wall (front view or top view) before applying sprites.
		if(wallType == 1) {
			if (System.console() == null) {
				image = ImageIO.read(new File("resources" + File.separator + "wall1.png")); //ide
			} else {
				URL imageURL = GameMain.class.getClassLoader().getResource("resources" + File.separator + "wall1.png");
				image = new ImageIcon(imageURL).getImage();
			}
		}
		else {
			if (System.console() == null) {
				image = ImageIO.read(new File("resources" + File.separator + "wall2.png")); //ide
			} else {
				URL imageURL = GameMain.class.getClassLoader().getResource("resources" + File.separator + "wall2.png");
				image = new ImageIcon(imageURL).getImage();
			}
		}
		image = image.getScaledInstance((int)width, (int)height, Image.SCALE_DEFAULT);
	}
	
	// Draws the object to the frame.
	public void draw(Graphics graphics) throws IOException{
		graphics.drawImage(image, (int)xPos, (int)yPos, null);
	}
}// End class.