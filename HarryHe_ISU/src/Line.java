/*
 * The Line class represents a line, with a starting point and an
 * ending point. This class helps to render the map, as common fairways
 * have straight lines that lead to the hole.
 * 
 * The Line class will also store common methods that may be used during
 * map generation such as drawing the line to help visualize the line in an
 * image, getting the Manhattan distance of the line, and checking if the
 * line is in the bounds of the array.
 */

// Importing the Random class which is used to 
// draw the line, may not be necessary
import java.util.Random;

public class Line {
	// Class Variables
	// The x1, y1 integers represent the starting location of the line
	// The x2, y2 integers represent the ending location of the line
	public int x1, y1, x2, y2;
	// The color variable may be unnecessary, it is only used to help visualize
	// the lines and the order they go into.
	public double color;

	// The constructor for the Line class is overloaded, with one including the 
	// color and one without. The constructor class is used to define the line, including
	// the starting and ending position. Here it also has the color, which is just used
	// to visualize the line, refer to the createLineBlueprint method in the MapGeneration
	// class file for more information on what each color is.
	public Line(int x1, int y1, int x2, int y2, double color) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
	}
	
	// A constructor for the line class, without the color specified. This constructor
	// is the predominant one used for map generation. This constructor is used to 
	// define the starting and ending position of the line.
	public Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	// The getManhattanDist method is used to return the Manhattan distance of the 
	// line. Manhattan distance is the difference in the x positions plus the distance
	// in the y positions.
	public int getManhattanDist() {
		return Math.abs(x2 - x1) + Math.abs(y2 - y1);
	}
	
	// The inBounds method will be used to check if the x and y position passed in
	// is within the bounds of the array given. This method is used to check if the
	// line should be stopped or continued to be drawn.
	public static boolean inBounds(int x, int y, double [][] arr) {
		// Local Variables
		int height = arr.length;
		int width = arr[0].length;
		// Method Body
		return (x >= 0 && x < width && y >= 0 && y < height);
	}
}
