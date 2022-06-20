/*
 * The MetaBall class is used to represent a meta ball, which is essentially
 * a gloopy circle which blends with other meta balls. This is used to generate
 * the map and produce more realistic fairways.
 */

public class MetaBall {
	// Class Variables
	// The x and y floats represent the center of the circle as a coordinate pair
	public float x, y;
	// The r float represents the radius of the metaball, which is an arbitrary value that
	// doesn't actually relate to the size of the metaball in pixels. It is a
	// proportionality constant which as it increase will increase the size of the metaball.
	// The reason for this is because metaballs work by taking the reciprocal distance
	// and changing pixels based on that, which is very hard to calculate the pixels from
	// 1 / (x2 - x1) * (y2 - y1)
	public float r;
	
	// The MetaBall constructor takes in the x, y, and radius of the metaball and passes
	// in the class variables.
	public MetaBall(float x, float y, float r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}
	
}
