import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// Class to define end tile objects.
public class End extends Rectangle2D.Double{
	double xPos;
	double yPos;
	double width;
	double height;
	Image image;
	Image endOff;
	Image end;
	boolean activated = false;
	
	// Constructor.
	End(double xPos, double yPos, double width, double height) throws IOException{
		super(xPos, yPos, width, height);
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;

		image = ImageIO.read(new File("resources" + File.separator + "end_off.png"));
		image = image.getScaledInstance((int)width, (int)height, Image.SCALE_DEFAULT);
	}

	// End activation method for if all objectives have been collected.
	public void activate() throws IOException{
		image = ImageIO.read(new File("resources" + File.separator + "end.png"));
		image = image.getScaledInstance((int)width, (int)height, Image.SCALE_DEFAULT);
		activated = true;
	}
	
	// Draws the tile to the screen.
	public void draw(Graphics graphics) throws IOException{
		graphics.drawImage(image, (int)xPos, (int)yPos, null);
	}
}// End class.