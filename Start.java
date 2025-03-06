import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// Class to define start tile objects.
public class Start extends Rectangle2D.Double{
	double xPos;
	double yPos;
	double width;
	double height;
	Image image;
	
	// Constructor.
	Start(double xPos, double yPos, double width, double height) throws IOException{
		super(xPos, yPos, width, height);
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		if (System.console() == null) {
			image = ImageIO.read(new File("resources" + File.separator + "start.png")); //ide
		} else {
			URL imageURL = GameMain.class.getClassLoader().getResource("resources" + File.separator + "start.png");
			image = new ImageIcon(imageURL).getImage();
		}
		image = image.getScaledInstance((int)width, (int)height, Image.SCALE_DEFAULT);
	}
	
	// Draws the object to the frame.
	public void draw(Graphics graphics) throws IOException{
		graphics.drawImage(image, (int)xPos, (int)yPos, null);
	}
}// End class.