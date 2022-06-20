/*
 * The Player class file represents the player in the game. 
 * 
 * This class will store the images of the player, the source and the rotated one,
 * the angle that the player is currently at, the previous angle the player was at
 * which will help to reduced computational strain of rotation, the x and y position
 * of the player, the velocity of the player, the colliding velocity which moves the
 * player back after collisions, the speed the player is moving at, the collision delay,
 * and the hit box factor which lowers the hitbox size to match the actual size of the 
 * golf cart.
 * 
 * The Player class will also store common methods that the player will
 * need to interact with the game.
 * Refer to the methods below for more information on what they do.
 */

// Importing the necessary classes to allow the Player to
// use its features, like the use and rotation of the player's image
// and the collision detection using its hit box.
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player{
	// The source variable stores the BufferedImage which represents
	// the player's golf cart
	BufferedImage source;
	// The toRender variable stores the BufferedImage which stores the
	// actual rendering image of the golf cart, which will be rotated
	// according to the player's angle
	BufferedImage toRender;
	
	// The angle double will store the angle that the player is currently at, in degrees
	// Note that 0 degrees is straight vertical
	double angle;
	// The previousAngle double will store the previous angle the player was at, last
	// tick, so that the toRender image will only be rotated when the angle actually
	// changes
	double previousAngle;
	// The x and y doubles store the x and y coordinates of the player
	double x, y;
	// The velX and velY doubles stores the velocity of the player, on the horizontal
	// and vertical axis respectively.
	// Note: These variables are not constant and do not actually reflect the movement
	// of the player, so changing them will not continually affect the movement the player.
	// Each tick, these variables are rewritten as the x and y component of a vector
	// with magnitude of the variable speed, and a direction of the angle variable.
	double velX, velY;
	
	// The collideVelX and collideVelY doubles store the extra velocity of the player
	// as a result of a collision. These doubles will add upon the velocity of the
	// player, effectively being apart of the velocity.
	double collideVelX;
	double collideVelY;
	
	// The speed double represents the speed of the player, in pixels per tick, and
	// when it is negative, the player is going backwards, and when it is positive
	// the player is going forwards.
	double speed;
	
	// The collisionDelay integer is the amount of ticks before the player can be hit
	// again, this is to help stop glitching of the player in obstacles and the golf
	// ball
	int collisionDelay;
	
	// The hitBoxFactor double is the multiplicative value that reduces the hit box's
	// with and height to make a more realistic collision of the player so that the
	// player doesn't collide when the rendered cart has not yet.
	double hitBoxFactor;
	
	// The friction double is the multiplicative value that will restrict the
	// acceleration of the player.
	double friction;

	// The constructor for the player type which sets up the image the player will be
	// rendered as, as well as the other variables of the player class, like the x and y
	// position, the velocity, and the collision delay.
	public Player(String path) {
		// The readImage variable is used to store the image is read in directly
		// from the ImageIO. This variable is then converted, by rescaling, into the source
		// image that is then rotated, into the toRender image, which is then rendered.
		BufferedImage readImage;
		try {
			// Reading in the golf cart image, and resizing it to a smaller size
			// to make it fit in the game
			readImage = ImageIO.read(new File(path));
			source = ImageUtilities.toBufferedImage(readImage.getScaledInstance(readImage.getWidth() / 10, 
					readImage.getHeight() / 10, java.awt.Image.SCALE_SMOOTH));
		} catch (IOException e) {
			// The image is not found and an error is thrown
			System.out.println("Error 404: File Not Found");
			e.printStackTrace();
		}
		
		// The angle is set to 0 as the player is by default set vertical
		angle = 0;
		// The previousAngle is set to the angle as the player has not rotated
		previousAngle = angle;
		// The x and y position are set to 0 by default to avoid null pointer
		// exceptions, all though this may not be fully necessary.
		x = 0;
		y = 0;
		// The speed, collideVelX, collideVelY, velX, and velY are set to 0 as a
		// default as the player should be still
		speed = 0;
		collideVelX = 0;
		collideVelY = 0;
		velX = 0;
		velY = 0;
		
		// The collisionDelay is set to 0 as the player should be primed to 
		// collide with an obstacle or the golf ball immediately
		collisionDelay = 0;
		// The hitBoxFactor is set to be smaller when the angle is less straight
		// and larger when the angle is more straight.
		// The hitBoxFactor will at most shrink the hit box by a factor of 0.6.
		hitBoxFactor = 1 - 0.4 * (45 - diffTo45()) / 45;
		
		// The actual rendering image of the player, toRender, is set to the angle
		// in which the player is facing.
		toRender = ImageUtilities.rotateImageTransparent(source, angle);
		
		// Set up the friction to be initially nothing, so that the player
		// can move smoothly.
		friction = 1;
	}
	
	// The tick method is the method used to separate movement and rotation, as well
	// as other updates of the player, from the main tick method. This method will
	// update the rendering image of the player, the hitbox, the velocity according to
	// the speed and angle, the x and y position from velocity, the collisionDelay
	// of the player, and the colliding velocity to slow down so that the player regains
	// control of the golf cart.
	public void tick(double biome) {
		// Only update the rendering image when the angle has changed to save
		// computation
		if(previousAngle != angle) {
			previousAngle = angle;
			toRender = ImageUtilities.rotateImageTransparent(source, angle);
			// Change the hit box to be smaller when the angle is less straight
			// closer to 45, so that there are more realistic collisions, and
			// the golf cart actually hits the obstacles or ball.
			hitBoxFactor = 1 - 0.4 * (45 - diffTo45()) / 45;
		}
		
		// The velX and velX are taken as components of the vector with magnitude speed
		// and direciton of the angle
		velX = Math.cos(Math.toRadians(angle - 90)) * (speed) + collideVelX;
		velY = Math.sin(Math.toRadians(angle - 90)) * (speed) + collideVelY;
		// The x and y position of the player is updated with the velocity the player
		// is moving at.
		x += velX;
		y += velY;
		
		// The collisionDelay is updated so that the player can be hit after the
		// collision delay runs out.
		if(collisionDelay > 0) collisionDelay--;
		// The collideVelX and collideVelY are updated so that the player will stop
		// being reflected after the collision and will be able to control the movement
		// again.
		if(Math.abs(collideVelX) >= 0) collideVelX *= 0.9;
		if(Math.abs(collideVelY) >= 0) collideVelY *= 0.9;
		// When the player is in certain terrain, set up different frictions
		// If the player is above the speed allowed at the terrain
		// set their speed lower
		if(biome >= 240) {
			// Fairway
			friction = 1;
		} else if(biome >= 200) {
			// Rough
			friction = 0.8;
			if(Math.abs(speed) > 8 * friction) {
				speed = speed / Math.abs(speed) * 8 * friction;
			}
		} else if(biome == -1) {
			// Bunker
			friction = 0.4;
			if(Math.abs(speed) > 8 * friction) {
				speed = speed / Math.abs(speed) * 8 * friction;
			}
		} else {
			// Extreme Rough
			friction = 0.5;
			if(Math.abs(speed) > 8 * friction) {
				speed = speed / Math.abs(speed) * 8 * friction;
			}
		}
	}
	
	// The startDelay method is used to set up the collision delay after the player
	// is hit, this method is used to help avoid multiple changes of the delay in 
	// the code when it is changed.
	public void startDelay() {
		collisionDelay = 5;
	}
	
	// The diffTo45 method will return the minimum distance of the player's angle to 
	// one of the angles with related acute angle of 45 (45, 135, 225, 315). This
	// value is then used to calculate the hitBoxFactor variable. The maximum value
	// this method should return is 45 and the minimum is 0.
	public int diffTo45() {
		// Local Variables
		int minDist = 45;
		// Method Body
		minDist = (int)(Math.round(Math.min(Math.abs(angle - 45), minDist)));
		minDist = (int)(Math.round(Math.min(Math.abs(angle - 135), minDist)));
		minDist = (int)(Math.round(Math.min(Math.abs(angle - 225), minDist)));
		minDist = (int)(Math.round(Math.min(Math.abs(angle - 315), minDist)));
		return minDist;
	}
	
	// The collision method is overloaded with two instances, with an obstacle and
	// a ball. This method will return true if the player has collided with the object
	// passed in and false if the player has not.
	public boolean collision(Obstacle obstacle) {
		return getBounds().intersects(obstacle.getBounds());
	}
	
	// The overloaded version of the collision method with a GolfBall class, refer to the
	// previous method for more information.
	public boolean collision(GolfBall ball) {
		return getBounds().intersects(ball.getBounds());
	}
	
	// The getBounds method will return the hit box of the player. The method will
	// take into account the hit box factor, which will shrink the hit box by a factor
	// of the hit box factor for more realistic collisions.
	public Rectangle getBounds() {
		return new Rectangle((int)(x - toRender.getWidth() * hitBoxFactor / 2), 
				(int)(y - toRender.getHeight() * hitBoxFactor / 2), 
				(int)(toRender.getWidth() * hitBoxFactor), 
				(int)(toRender.getHeight() * hitBoxFactor));
	}
}
