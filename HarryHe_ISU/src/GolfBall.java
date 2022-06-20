/*
 * The GolfBall class represents the golf ball in the game.
 * It stores the image that the golf ball is rendered as and other necessary
 * variables that will allow the golf ball to be hit around and move.
 * 
 * The GolfBall class will also store common methods that the golf ball will
 * need to interact with the game.
 * Refer to the methods below for more information on what they do.
 */

// Importing the necessary classes to implement features of the
// golf ball, like its rendering image and its hit box.
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GolfBall {
	// The source variable is the image that the 
	// golf ball is rendered as
	BufferedImage source;
	// The x and y doubles represent the coordinates of the centre of
	// the image, or the obstacle.
	double x, y;
	// The velX and velY double represent the velocity
	// in pixels / tick, on the horizontal axis and vertical
	// axis respectively.
	double velX, velY;
	
	// The collisionDelay integer represents the ticks remaining
	// in which the golf ball cannot be hit, or influenced, to 
	// prevent glitching in a obstacle or the player
	int collisionDelay;
	
	// The imageWidth and imageHeight integers represent the width and height of the
	// image of the ball. These are variables to allow easier changing of the
	// dimensions of the image.
	final int imageWidth = 115, imageHeight = 115;
	
	// The hitWidth and hitHeight integers represent the hit box width and 
	// height of the ball
	final int hitWidth = 100, hitHeight = 100;
	
	// The inHole boolean represents if the ball is in the final hole
	boolean inHole;
	
	// The friction double is the multiplicative value that will restrict the
	// hitting speed of the ball, and will increase the deceleration.
	double friction;
	
	// The constructor for the GolfBall type. The method will
	// take in the path of the image that is used to render the golf
	// ball, as well as the golf ball's x and y position. Then will
	// set up the class variables, like the x and y coordinates, the
	// velocity, as well as the collisionDelay.
	public GolfBall(String path) {
		// Local Variable
		// The readImage variable is used to store the image is read in directly
		// from the ImageIO. This variable is then converted, by rescaling, into the source
		// image that is then rendered in the game.
		BufferedImage readImage;
		
		// Method Body
		try {
			readImage = ImageIO.read(new File(path));
			// The source image is set to a scaled version, which is smaller
			// by 1.5 times
			source = ImageUtilities.toBufferedImage(readImage.getScaledInstance(
					imageWidth, imageHeight, java.awt.Image.SCALE_SMOOTH));
			// Angles are limited to 0, 90, 180, 270 because of hitbox issues
			
		} catch (IOException e) {
			System.out.println("Error 404: File Not Found");
			e.printStackTrace();
		}
		
		// Declare the x, y, and velocity to all be zero to avoid null pointer
		// exceptions, although this may be unnecessary.
		this.x = 0;
		this.y = 0;
		this.velX = 0;
		this.velY = 0;
		// Collision delay is set to zero to allow the golf
		// ball to be hit immediately
		this.collisionDelay = 0;
		// The inHole should be false initially as the game should
		// not be already completed
		inHole = false;
		// Initially set the friction to be initially nothing 
		// to avoid null pointer exceptions.
		friction = 1;
	}
	
	// The tick method will move the golf ball every tick
	// according to the velocity of the golf ball. It will also
	// decrease the velocity of the golf ball to represent friction
	// slowing it down.
	
	// This method is similar to the tick method in the Main class, but
	// is just here to help separate movement of the golf ball from other
	// classes to make it easier to read and change.
	public void tick(double biome) {
		// The x and y position of the player is updated with the velocity the player
		// is moving at.
		x += velX;
		y += velY;
		// Take the reciprocal friction and multiply
		// it by some constant value to increase deceleration
		// You have to subtract back the constant as when friction is 1
		// there should be no effect.
		if(Math.abs(velX) > 0) velX *= 0.985 - (0.05 * 1 / friction - 0.05);
		if(Math.abs(velY) > 0) velY *= 0.985 - (0.05 * 1 / friction - 0.05);
		
		// The collisionDelay is updated so that the player can be hit after the
		// collision delay runs out.
		if(collisionDelay > 0) collisionDelay--;
		// When the player is in certain terrain, set up different frictions
		if(biome >= 240) {
			// Fairway
			friction = 1;
		} else if(biome >= 200) {
			// Rough
			friction = 0.8;
		} else if(biome == -1) {
			// Bunker
			friction = 0.4;
		} else {
			// Extreme Rough
			friction = 0.5;
		}
	}
	
	// The startDelay method will be used to set up collision
	// delay which prevents the golf ball from being hit. It is put
	// into a method to avoid hard coding and allow changes to the number
	// to be put in a local position.
	public void startDelay() {
		collisionDelay = 2;
	}
	
	// The getBounds method will return a Rectangle class representing the
	// rectangular hit box of the golf ball which will be used to check if the
	// golf ball has been hit or not by obstacles, or the player.
	public Rectangle getBounds() {
		return new Rectangle((int)x - hitWidth / 2, (int)y - hitHeight / 2, hitWidth, hitHeight);
	}
	
	// The collision method will return a boolean value. The method will
	// return true if the golf ball has been hit by an obstacle, and false
	// if the golf ball has not been hit.
	public boolean collision(Obstacle obstacle) {
		return getBounds().intersects(obstacle.getBounds());
	}
}
