/*
 * The Pair class represents two values, a x and a y value, which 
 * are the coordinates of a point. This is used in map generation
 * to avoid excess variables.
 */

public class Pair {
	// Class Variables
	// The x and y integers represent the x and y value of a point
	public int x, y;
	// The Pair constructor takes in two integers, which are then passed
	// into the corresponding class variable.
	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
