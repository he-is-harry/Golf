/*
 * The Obstacles class represents obstacles in the game. This
 * class will store the images that the obstacle will be rendered as,
 * the image width and height, and the hit width and height (hit box dimensions)
 * 
 * The Obstacle class will also store common methods that an obstacle will
 * need to help detect collisions with the player and golf ball.
 * Refer to the methods below for more information on what they do.
 */

// Importing the necessary classes to implement features
// of the golf ball, like its hit box and its image render.
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Obstacle {
	// Class Variables
	
	// The source variable is a BufferedImage variable
	// that represents the image that the obstacle is rendered as
	BufferedImage source;
	// The x and y coordinates represent the centre of
	// the image, or the obstacle.
	double x, y;
	// The imageWidth and imageHeight are integers that are the
	// width and height of the image of the obstacle respectively.
	int imageWidth, imageHeight;
	// The hitWidth and hitHeight are integers that are the
	// width and height of the hit box of the obstacle.
	int hitWidth, hitHeight;
	
	// The constructor of the Obstacle class. The method will take in the
	// resource path of the image of the obstacle, its x and y position, the image
	// width and height, the hit box width and height, and the angle of the image.
	public Obstacle(String path, double x, double y, double imageMagnifier, double hitMagnifier, double angle) {
		// Local Variables
		// The readImage variable is used to store the image is read in directly
		// from the ImageIO. This variable is then converted, by rescaling, into the source
		// image that is then rotated, which is then rendered.
		BufferedImage readImage;
		
		// Method Body
		try {
			// The source image of the obstacle is rendered in and rotated according
			// to the angle given.
			readImage = ImageIO.read(new File(path));
			source = ImageUtilities.toBufferedImage(readImage.getScaledInstance(
					(int)(readImage.getWidth() * imageMagnifier), 
					(int)(readImage.getHeight() * imageMagnifier), java.awt.Image.SCALE_SMOOTH));
			
			source = ImageUtilities.rotateImageTransparent(source, angle);
			// Angles are limited to 0, 90, 180, 270 because of hitbox issues
			
		} catch (IOException e) {
			// The image is not found and throws and error
			System.out.println("Error 404: File Not Found");
			e.printStackTrace();
		}
		
		// The class variables are declared according to the values given into the
		// method
		this.x = x;
		this.y = y;
		this.imageWidth = source.getWidth();
		this.imageHeight = source.getHeight();
		// The hitMagnifier is the multiplicative value used to determine
		// the hit box size. So it is the factor that determines how much smaller
		// a hit box is compared to the image.
		this.hitWidth = (int)(imageWidth * hitMagnifier);
		this.hitHeight = (int)(imageHeight * hitMagnifier);
	}
	
	// The getBounds method will return a Rectangle object which represents
	// the hit box of the obstacle
	public Rectangle getBounds() {
		return new Rectangle((int)x - hitWidth / 2, (int)y - hitHeight / 2, hitWidth, hitHeight);
	}
}
