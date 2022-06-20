/*
 * The ImageUtilities class is used to house methods that are used
 * often in different other class files, including the rotation of the images
 * and the conversion of regular Image classes to BufferedImage classes.
 */

// Importing the necessary classes for image
// transformation.
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtilities {
	// The rotateImageTransparent method will take in a source image
	// and rotate it to the angle. The angle is based upon the top of the image.
	// Note that the method will spin clockwise so the higher the 
	// angle the higher the hour the image faces.
	
	// For example, 45 degrees will turn an image 45 degrees clockwise and imagining
	// the image like a clock, the top of the image will be halfway between the 1
	// and the 2
	public static BufferedImage rotateImageTransparent(BufferedImage sourceImage, double angle) {
		// Local Variables
		// The sin and cos doubles are the sine and cosine of the angle of the 
		// image rotation which will be used to help make the new dimensions of the rotated image.
		double sin = Math.abs(Math.sin(Math.toRadians(angle)));
		double cos = Math.abs(Math.cos(Math.toRadians(angle)));
		// We calculate the new image to have the necessary width and height
		// to help with better hit boxes
		// To calculate the new dimensions we imagine that the rectangle representing the
		// source image is rotated. Then we can see that the new width is the original width
		// times the cosine, plus the sine of the height. The new height has the sine and
		// cosine switched around, since it is the height now.
		int width = (int) Math.round(sourceImage.getWidth() * cos + sourceImage.getHeight() * sin);
		int height = (int) Math.round(sourceImage.getWidth() * sin + sourceImage.getHeight() * cos);
		
		// Method Body
		// The type of TYPE_INT_ARGB is a transparent image type, or a png
		BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		// Here we actually rotate the image by moving it to the center
		// then rotating it, then moving it back to the regular top left corner
		AffineTransform at = new AffineTransform();
		at.translate(width / 2, height / 2);
		at.rotate(Math.toRadians(angle),0, 0);
		at.translate(-sourceImage.getWidth() / 2, -sourceImage.getHeight() / 2);
		AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		// Here we are writing the source image that has been rotated into the new
		// rotated image with the new diemsions that we have calculated.
		rotateOp.filter(sourceImage, rotatedImage);
		return rotatedImage;
	}
	
	// The toBufferedImage method will take in a regular Image class
	// and convert it to a BufferedImage class. It will declare a Graphics2D
	// class which will draw the pre-existing image onto a blank BufferedImage
	// essentially copying over the image and converting it to a BufferdImage.
	public static BufferedImage toBufferedImage(Image img) {
		// Local Variables
		// The convertImage is the BufferedImage with the same dimensions of the Image
		// used as the return value.
		BufferedImage convertImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		// The Graphics2D is just used to draw on the Image onto the blank BufferedImage.
		Graphics2D bufferedGraphics = convertImage.createGraphics();
		
		// Method Body
		bufferedGraphics.drawImage(img, 0, 0, null);
		bufferedGraphics.dispose();
		return convertImage;
	}
}
