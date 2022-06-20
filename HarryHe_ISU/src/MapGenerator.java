/*
 * The MapGeneration class is used to generate the map in the game, using a given seed
 * that is used to declare the Random class. The MapGeneration class holds some class
 * variables that are used to generate the map, as well as many other local variables.
 * The class variables used to generate the map include all of the viable points
 * of the metaballs, the beginning and ending points of the metaballs, and the bunker
 * points of the metaballs. Class variables also include the maximum and minimum 
 * distance of the lines and the constant value indicating the end of a line.
 * 
 * This class also has several methods to generate the map and make it into an image,
 * like the generateMap method which is the main method used to make maps, as well as
 * its helper methods like the generateLine method and the draw line method which are
 * used to make a line path of the fairway. The methods to make the image are ones like
 * the createImage method used to prepare the pixels and arrayToImage method used
 * to actually create the image. There are some helper methods also not included
 * which are further documented below.
 * 
 */

// Importing the necessary classes to create the map and to 
// render the map as an image
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class MapGenerator {
	// Class Variables
	// The points arraylist is used to store the coordinates of
	// all of the possible positions for metaballs to be. This includes ALL
	// possible positions including the begin and end points.
	ArrayList<Pair> points;
	// The beginPoints and endPoints arraylist are used to store the coordinates
	// of the possible positions for metaballs to be, which are at the beginning and
	// ending of the course respectively.
	// This end and beginning point arraylists are necessary to ensure that the
	// entire path is fully fleshed out, as often the probability for metaballs
	// to be placed on the ends is low leading to excess metaballs in the center
	// of the course.
	ArrayList<Pair> beginPoints;
	ArrayList<Pair> endPoints;
	// The bunkerPoints arraylist stores all of the possible coordinates for bunkers,
	// more specifically the metaballs that represent them.
	ArrayList<Pair> bunkerPoints;
	// The lineBlueprint 2D array represents the pixels of the image used to show the
	// lines of the hole. This variable may not be necessary as it is only used to
	// visualize the lines.
	double [][] lineBlueprint;
	// The minLineSize and maxLineSize integer are the minimum and maximum length of a
	// line respectively
	final int minLineSize = 300;
	final int maxLineSize = 1100;
	// The endDist double represents the percentage of the line distance that counts
	// as the beginning or the end, which is used when adding points to the beginPoints
	// and endPoints arraylist.
	final double endDist = 0.10;
	// The par integer measures the par of the hole, which will be later used for the
	// high scores.
	public int par;
	// The mapSeed long is the seed of the map, which will be later used for the high
	// scores.
	public long mapSeed;
	
	// The holeX and holeY variables represent the centre x and y coordinates of
	// hole, where the golf ball should go into
	public int holeX;
	public int holeY;
	
	// The trueStartX and trueStartY integers are the coordinates of the true
	// start of the line. This value is later used to calculate the actual
	// distance of the hole, which is used to calculate the par.
	public int trueStartX;
	public int trueStartY;
	
	// The firstAngle integer is the angle of the first line, in degrees,
	// which is used to place the player later when the map is generated as the
	// player should face the golf ball when they spawn in.
	public double firstAngle;
	
	// The values 2D array is the value of each pixel, where each double element in the array
	// will be used to later determine if it is extreme rough, rough, fairway, or
	// bunker. This array is later used when generating obstacles and for the map.
	public double [][] values;
	
	// The obstacles variable is an array of a custom made class Obstacle.
	// It will allow the player and golf ball to iterate through the array
	// to check if they collide with the obstacle.
	// The Obstacle class will store hit box information of the obstacle, its
	// x and y position as well as its rendering image.
	// Refer to the Obstacle class file for more information on how obstacles
	// work.
	Obstacle [] obstacles;
	
	// The generateMap method is used to generate a new map based on the seed passed in.
	// The method uses a series of lines to represent the fairway, then will overlay those
	// lines with metaballs, that will blend together to form a more organic path.
	// Then more obstacles and bunkers will be put into the along and in the path of the 
	// fairway which will be more likely to be placed on the rough or extreme rough, 
	// incentivizing the player to use the fairway, like in regular golf.
	// The seed can by any long number, including negatives.
	public void generateMap(long seed, double magnificationScale, String name) {
		// Local Variables
		// The rand variable is used to generate all of the random numbers given a 
		// certain seed, so the seed is fully in control of the map the player will
		// get
    	Random rand = new Random(seed);
    	// The width and height are declared, which doens't represent the real width
    	// and height in the game, but helps to make a square image with enough quality
    	int width = 2000;
    	int height = 2000;
    	
    	// The percentBuffer is the buffer space from the edge of the map where
    	// lines are not allowed to go to, measured in percent of the dimensions.
    	double percentBuffer = 0.10;
    	
    	// The numLines is declared, with a 20% chance of 1 line, 30% chance of 2 lines
    	// 20% chance of 3 lines, 20% chance of 4 lines, and 10% chance of 5 lines.
    	// The getNumLines method uses the Random declared in this method to allow all
    	// randomness to be based upon the seed.
    	int numLines = getNumLines(rand);
    	
    	// The Line object l is used to generate the map of the hole using
    	// the a series of lines. This object holds the start and end of the line, 
    	// in coordinates.
    	Line l;
    	
    	// The lineStartX and lineStartY integers are the coordinates in which the
    	// line will start, which will be used as the starting point of the next
    	// line.
    	int lineStartX = (int)(rand.nextInt((int)(width - width * 2 * percentBuffer)) + width * percentBuffer);
    	int lineStartY = (int)(rand.nextInt((int)(height - height * 2 * percentBuffer)) + height * percentBuffer);
    	
    	// The color variable is used to help visualize the lines that are used to
    	// generate the map. This variable may be unnecessary as it doens't
    	// affect the generation of the map.
    	// Refer to the getLineBlueprint method for more information on what color the
    	// value of the variable relates to.
    	int color = 1;
    	// The dist double is used to measure the absolute distance sum of all of the
    	// lines. This value is primarily used to determine the number of bunkers.
    	double dist = 0;
    	
    	// The randomIndex integer is used to save a random index generated that
    	// will be later used when using the random index, this is often used when
    	// randomly inserting in metaballs.
    	int randomIndex;
    	
    	// The balls array is an array of MetaBalls which represent the path of 
    	// the golf course, this is used to generate the map.
    	// Refer to the MetaBall class file for more information on what a metaball
    	// is.
    	MetaBall [] balls;
    	// The placementFreedom integer is a constant value that is used to determine
    	// how far a metaball can be placed beyond their designated point on the line.
    	// This helps make the path less straight and more organic.
    	// This constant is primarily used to avoid hard-coding of the placementFreedom
    	// to allow easier changes.
    	int placementFreedom = 10;
    	// The minBallSize and maxBallSize integer is a constant value that indicates
    	// the minimum and maximum radius of a metaball placed.
    	// This will help provide even more variance in the size of the path, allowing
    	// for a more organic path.
    	int minBallSize = 20;
    	int maxBallSize = 45;
    	
    	// The percentageEnd double is a percentage of all metaballs that must be placed
    	// in the beginning and end. It is split into two when placing the metaballs
    	// with half being in the beginning and half being in the end.
    	double percentageEnd = 0.15;
    	
    	// The bunkers arraylist is an ararylist of metaballs, which all represent
    	// the bunkers in the golf course.
    	ArrayList<MetaBall> bunkers = new ArrayList<>();
    	// The bunkerFreedom variable is the placement freedom of the bunkers, which
    	// allow for more variance in the placement of the bunker's metaballs to avoid
    	// making only circular bunkers.
    	int bunkerFreedom = 5;
    	// The numBunkers integer is later defined as the number of bunkers in the course.
    	// It is defined as the total distance  / 400.
    	int numBunkers;
    	// The randBunkSize integer is used as a reusable variable to store the
    	// number of metaballs that will be placed in a bunker, which is randomly generated.
    	int randBunkSize;
    	// The minBunkBalls and maxBunkBalls integers are constant values that are used
    	// to help set the bounds for the minimum and maximum amount of metaballs in a
    	// bunker respectively.
    	int minBunkBalls = 6;
    	int maxBunkBalls = 16;
    	// The minBunkBallSize and maxBunkBallSize integers are constant values that 
    	// are the bounds for the minimum and maximum sizes of metaballs in bunker
    	// respectively.
    	int minBunkBallSize = 5;
    	int maxBunkBallSize = 8;
    	
    	// The pixels array is a reusable variable used to convert the values in the
    	// map generated, to a 1D array of colors which will be used to draw an image.
    	int [] pixels;
    	
    	// The sum variable is used to generate the map as the value, and later the 
    	// biome, is based on the sum of the reciprocal distance to each metaball
    	double sum;
    	
    	// The d variable is the reciprocal distance of each pixel for each metaball
    	// which is used to determine the sum variable.
    	double d;
    	
    	// The fairwayPoints, roughPoints, and extremePoints store the two indices
    	// of a point of all of the fairway, rough and extreme rough, points
    	// respectively.
    	// These points are later used to generate in the obstacles.
    	// Note that these points don't include the bunker points
    	// since, obstacles should not generate in bunkers.
    	ArrayList<Pair> fairwayPoints = new ArrayList<>();
    	ArrayList<Pair> roughPoints = new ArrayList<>();
    	ArrayList<Pair> extremePoints = new ArrayList<>();
    	
    	// Method Body
    	// Re-declaration of the class variables, as new maps call for different
    	// points of the metaballs and the lines.
    	points = new ArrayList<>();
    	beginPoints = new ArrayList<>();
    	endPoints = new ArrayList<>();
    	bunkerPoints = new ArrayList<>();
    	lineBlueprint = new double[height][width];
    	values = new double[height][width];
    	mapSeed = seed;
    	
    	// Declaration of the trueStartX and trueStartY at the starting position
    	// of the line.
    	trueStartX = lineStartX;
    	trueStartY = lineStartY;
    	
    	// The lines of the map are generated, with the end of one line
    	// being the start of another
    	for(int i = 0; i < numLines; i++) {
    		l = generateLine(lineStartX, lineStartY, width, height, percentBuffer, color, rand);
    		lineStartX = l.x2;
    		lineStartY = l.y2;
    		// When the lines are the first line, we use the beginning mode
    		// and it is the last, we use the ending mode
    		// The drawLine method will put in all of the line's points into an arraylist
    		drawLine(l, width, height, i == 0 ? 1 : i == numLines - 1 ? 2 : 0, rand);
    		// The color variable is used here to allow the visualization of the lines
    		// in a later drawn image.
    		color++;
    		dist += getDist(l.x1, l.y1, l.x2, l.y2);
    		if(i == 0) {
    			// Find the firstAngle, the angle in which the first fairway line
    			// faces to help orientate the player in the game
    			firstAngle = Math.toDegrees(Math.atan2(l.y2 - l.y1, l.x2 - l.x1));
    		}
    	}
    	
    	// The number of metaballs that will be generated is calculated
    	// as the distance x a multipler (which increases amount of balls if a lower par)
    	// divided by 14. 14 is used as it is a bit lower than the radiuses
    	// allowing a bit of overlap between the metaballs.
    	balls = new MetaBall[(int)Math.round(dist * (1 + 0.1 * (5 - numLines)) / 14)];
    	// The number of bunkers is calculated as the total distance / 400.
    	numBunkers = (int)Math.round(dist / 400);
    	// Par is calculated as the absolute distance between the start and finish, with a minimum
    	// of 1 and a maximum of 5.
    	par = (int)Math.max(Math.min(getDist(trueStartX, trueStartY, lineStartX, lineStartY) / 250, 5), 1);
    	// The lineStartX and lineStartY value now represent the end of the lines
    	// because the lines have all been created.
    	holeX = (int)(lineStartX * magnificationScale);
    	holeY = (int)(lineStartY * magnificationScale);
    	
    	// Declaring in the metaballs based on the points the lines are apart of.
    	// So metaballs are placed at a random point on the lines.
    	// For the first 15% of the metaballs we must use
    	// end and beginning points to allow for a more balanced
    	// golf course
    	// These are later used to gneerate the values array, or the terrain of each
    	// pixel
    	for(int i = 0; i < balls.length; i++) {
    		if(i < balls.length * percentageEnd / 2 && beginPoints.size() > 0) {
    			randomIndex = rand.nextInt(beginPoints.size());
        		balls[i] = new MetaBall(beginPoints.get(randomIndex).x + rand.nextInt(placementFreedom * 2 + 1) - placementFreedom, 
        				beginPoints.get(randomIndex).y + rand.nextInt(placementFreedom * 2 + 1) - placementFreedom, 
        				rand.nextInt(maxBallSize - minBallSize + 1) + minBallSize);
        		// Remove the point to allow for less overlap
        		beginPoints.remove(randomIndex);
    		} else if(i < balls.length * percentageEnd && endPoints.size() > 0) {
    			randomIndex = rand.nextInt(endPoints.size());
        		balls[i] = new MetaBall(endPoints.get(randomIndex).x + rand.nextInt(placementFreedom * 2 + 1) - placementFreedom, 
        				endPoints.get(randomIndex).y + rand.nextInt(placementFreedom * 2 + 1) - placementFreedom, 
        				rand.nextInt(maxBallSize - minBallSize + 1) + minBallSize);
        		// Remove the point to allow for less overlap
        		endPoints.remove(randomIndex);
    		} else {
    			randomIndex = rand.nextInt(points.size());
        		balls[i] = new MetaBall(points.get(randomIndex).x + rand.nextInt(placementFreedom * 2 + 1) - placementFreedom, 
        				points.get(randomIndex).y + rand.nextInt(placementFreedom * 2 + 1) - placementFreedom, 
        				rand.nextInt(maxBallSize - minBallSize + 1) + minBallSize);
        		// Remove the point to allow for less overlap
        		points.remove(randomIndex);
    		}
    		
    	}
    	
    	// Rendering in the metaballs and representing them into a 2D values array
    	// which is used later to draw the image. 
    	// The closer a point is to a metaball, the higher the value, the more likely
    	// to be a fairway.
    	for(int i = 0; i < height; i++) {
    		for(int j = 0; j < width; j++) {
    			sum = 0;
    			for(MetaBall m: balls) {
    				// The reciprocal distance can be used to measure how close a 
    				// point is to a metaball at an exponential rate.
    				// Note that the d can be zero, and Java will not return
    				// an error as dividing by zero will only result in Infinity
    				d = Math.sqrt((i - m.y) * (i - m.y) + (j - m.x) * (j - m.x));
    				sum += 15 * m.r / d;
    			}
    			values[i][j] = Math.min(sum, 255);
    		}
    	}
    	
    	// Bunkers are chosen to be around the rough, so that the fairway is clear
    	// but over shooting can result in a poor outcome.
    	for(int i = 0; i < height; i++) {
    		for(int j = 0; j < width; j++) {
    			if(values[i][j] >= 234 && values[i][j] <= 241) {
    				bunkerPoints.add(new Pair(j, i));
    			}
    		}
    	}
    	
    	// Bunker metaballs are chosen to be in some of the viable locations
    	// that were found in the nested for loops before.
    	for(int i = 0; i < numBunkers; i++) {
    		// The randomIndex indicates the point where the bunker should be
    		randomIndex = rand.nextInt(bunkerPoints.size());
    		// The randBunkSize indicates the size of the bunkers, the amount of 
    		// metaballs used to make a bunker
    		randBunkSize = rand.nextInt(maxBunkBalls - minBunkBalls + 1) + minBunkBalls;
    		for(int j = 0; j < randBunkSize; j++) {
    			// Add in the metaball to the bunkers arraylist
    			bunkers.add(new MetaBall(bunkerPoints.get(randomIndex).x + rand.nextInt(bunkerFreedom * 2 + 1) - bunkerFreedom, 
        				bunkerPoints.get(randomIndex).y + rand.nextInt(bunkerFreedom * 2 + 1) - bunkerFreedom, 
        				rand.nextInt(maxBunkBallSize - minBunkBallSize + 1) + minBunkBallSize));
    		}
    		// Point is removed to avoid overlaps of bunkers
    		bunkerPoints.remove(randomIndex);
    	}
    	
    	// Areas close to the bunker metaballs will become bunkers.
    	// We take the reciprocal distance so the closer you are to a bunker
    	// the higher your value will be and the more likely you are to be a bunker
    	for(int i = 0; i < height; i++) {
    		for(int j = 0; j < width; j++) {
    			sum = 0;
    			for(MetaBall m: bunkers) {
    				d = Math.sqrt((i - m.y) * (i - m.y) + (j - m.x) * (j - m.x));
    				sum += 15 * m.r / d;
    			}
    			if(sum >= 50) {
    				// -1 represents that the value is of a bunker
    				values[i][j] = -1;
    			}
    		}
    	}
    	
		// The try and catch block will prevent errors from breaking
		// the entire game if the map fails to load.
        try {
    		// Actual drawing of the images
        	if(name.equals("Harry")) {
        		// For an easter egg, if the name of the player is Harry, then
            	// the map will become rainbow.
        		pixels = createImageEasterEgg(values, width, height);
        	} else {
        		pixels = createImage(values, width, height);
        	}
            arrayToImage("map.jpeg", width, height, pixels);
            
            pixels = createLineBlueprint(lineBlueprint, width, height);
            arrayToImage("Lines.jpeg", width, height, pixels);

        } catch (Exception exc) {
            System.out.println("Interrupted: " + exc.getMessage());
            exc.printStackTrace();
        }
        
        // At minimum there are 8 obstacles and there are at most 55 obstacles
        obstacles = new Obstacle[rand.nextInt(48) + 8];
        
        // Entering in the points to the fairway, rough, and extreme rough points
        // array lists.
        for(int i = 0; i < height; i++) {
        	for(int j = 0; j < width; j++) {
        		if(values[i][j] >= 240) {
        			fairwayPoints.add(new Pair(i, j));
        		} else if(values[i][j] >= 200) {
        			roughPoints.add(new Pair(i, j));
        		} else if(values[i][j] != -1){
        			extremePoints.add(new Pair(i, j));
        		}
        	}
        }
        
        // Generate the obstacles
        for(int i = 0; i < obstacles.length; i++) {
        	// Here the randomIndex is used as a random number to check 
        	// probability
        	// We use a weighted constant and give rough points 3x chance to get
        	// an obstacle, and 5x for extreme rough.
        	randomIndex = rand.nextInt(fairwayPoints.size() + 3 * roughPoints.size() + 
        			5 * extremePoints.size());
        	// The randomIndex will determine if the obstacle should be at the
        	// fairway, rough or the extreme rough.
        	// The obstacle image will be generated by the getObstaclePath method
        	// and as such the relative dimensions. The absolute dimensions
        	// will take the relative dimensions and multiply them by a random double
        	// from 0.5 to 2.0, then the hit box is always 85% of these dimensions.
        	// The angle will also be generated as either 0, 90, 180, or 270.
        	if(randomIndex < fairwayPoints.size()) {
        		// Take a random fairway point and put an obstacle there
        		randomIndex = rand.nextInt(fairwayPoints.size());
        		obstacles[i] = new Obstacle(getObstaclePath(rand), 
        				fairwayPoints.get(randomIndex).x * magnificationScale, fairwayPoints.get(randomIndex).y * magnificationScale, 
        				rand.nextDouble() * 1.5 + 0.5, 0.85, rand.nextInt(4) * 90);
        	} else if(randomIndex < fairwayPoints.size() + 3 * roughPoints.size()) {
        		// Take a random rough point and put an obstacle there
        		randomIndex = rand.nextInt(roughPoints.size());
        		obstacles[i] = new Obstacle(getObstaclePath(rand), 
        				roughPoints.get(randomIndex).x * magnificationScale, roughPoints.get(randomIndex).y * magnificationScale, 
        				rand.nextDouble() * 1.5 + 0.5, 0.85, rand.nextInt(4) * 90);
        	} else {
        		// Take a random extreme rough point and put an obstacle there
        		randomIndex = rand.nextInt(extremePoints.size());
        		obstacles[i] = new Obstacle(getObstaclePath(rand), 
        				extremePoints.get(randomIndex).x * magnificationScale, extremePoints.get(randomIndex).y * magnificationScale, 
        				rand.nextDouble() * 1.5 + 0.5, 0.85, rand.nextInt(4) * 90);
        	}
        }
    }
	
	// The getDist method will taken in two points and return the absolute distance
	// between them, using the Pythagorean theorem.
	public double getDist(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	// The drawLine method will take in a line, width, height, and a mode
	// to enter in the points of the line. This will be then used to generate in the
	// positions of the metaballs. The different modes are used to add in the beginning
	// and ending points.
	// Note that the method will always add a different series of points
	// as the way the line is drawn is random, however, they will always go in the
	// same direction.
	public void drawLine(Line l, int width, int height, int mode, Random rand) {
		// 0 - Regular mode, just put in points
		// 1 - Beginning mode, put in pairs close to the beginning into the
		// end points arraylist
		// 2 - Ending mode, put in pairs close to the end into the end points
		// arraylist

		// Local Variables
		// The diffX and diffY integers measure the amount of distance left to 
		// the final destination of the hole in the horizontal and vertical plane
		// respectively.
		int diffX = l.x2 - l.x1;
		int diffY = l.y2 - l.y1;

		// The curX and curY integers indicate the current element in the values
		// array, or the current pixel.
		int curX = l.x1;
		int curY = l.y1;
		// The boolean isMoveX is used to check if the x or y should be moved which
		// is randomly generated.
		boolean isMoveX;

		// The line will continue to be drawn when the line is in bounds.
		while (inBounds(curX, curY, width, height, 0) && (curX != l.x2 || curY != l.y2)) {
			// The edge points of the line are added into the beginning and end 
			// points array.
			if(mode == 1 && Math.sqrt(diffX * diffX + diffY * diffY) / getDist(l.x1, l.y1, l.x2, l.y2) <= (1 - endDist)) {
				// 90% of distance to go, meaning at the beginning
				beginPoints.add(new Pair(curX, curY));
			} else if(mode == 2 && Math.sqrt(diffX * diffX + diffY * diffY) / getDist(l.x1, l.y1, l.x2, l.y2) <= endDist) {
				// 10% of distance to go, meaning at the end
				endPoints.add(new Pair(curX, curY));
			}
			
			// The point of the line is added to the generic points array
			points.add(new Pair(curX, curY));
			// The lineBlueprint is added into, but this may not be necessary,
			// as it is only used to visualize the lines.
			lineBlueprint[curY][curX] = l.color;
			// Take a weighted probability to move in the x or y direction
			// For example if you have 5 changes in x to go to the final destination
			// and 7 changes in y to go, you have a 5 / 12 chance of moving in the
			// x direction and a 7 / 12 chance of moving in the y direction
			// This hopefully, will result in more straight lines.
			isMoveX = rand.nextInt(Math.abs(diffX) + Math.abs(diffY)) < Math.abs(diffX) ? true : false;
			if (isMoveX) {
				// Move the curX
				curX += diffX > 0 ? 1 : -1;
				if(diffX > 0) {
					diffX--;
				} else if(diffX < 0) {
					diffX++;
				}
			} else {
				// Move the curY
				curY += diffY > 0 ? 1 : -1;
				if(diffY > 0) {
					diffY--;
				} else if(diffY < 0) {
					diffY++;
				}
			}
		}
	}
	
	// The createLineBlueprint method will take in some values of the lines blueprint
	// which will be used to create the 1D pixel array of colors which will be used
	// to generate the line blueprint image. This method may not be necessary because
	// it is only used to visualize the lines, and has no impact on the actual
	// map generation.
	public int[] createLineBlueprint(double [][] values, int width, int height) {
		// Local Variables
		// The newPixels array is an 1D array of pixels used to create an image
        int[] newPixels = new int[width * height];
        // The red, green, and blue integers are used to make the colors
        // of the map, where red, green and blue are the components of the colors
        // of an RGB type.
        int red, green, blue;
        // The value integer is the integer conversion of the colors of RGB
        // the value is actually the value that is put into the data to image
        // conversion method.
        int value;
        // Method Body
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            	// The specific color variables are used to generate the different
            	// lines, these are in order in which the lines are supposed to move
            	if(values[i][j] == 1) {
            		// Black for the first line
            		red = 0;
                	green = 0;
                	blue = 0;
            	} else if(values[i][j] == 2) {
            		// Red for the second line
            		red = 255;
                	green = 0;
                	blue = 0;
            	} else if(values[i][j] == 3) {
            		// Green for the third line
            		red = 0;
                	green = 255;
                	blue = 0;
            	} else if(values[i][j] == 4) {
            		// Blue for the fourth line
            		red = 0;
                	green = 0;
                	blue = 255;
            	} else if(values[i][j] == 5) {
            		// Orange for the fifth line
            		red = 255;
                	green = 165;
                	blue = 0;
            	} else {
            		// Otherwise the pixel is not a line, it is white
            		red = 255;
                	green = 255;
                	blue = 255;
            	}
            	
                // Colors in Java are represented as integers, with the
            	// 2nd 8 bit group being red, 3rd green, and 4th is blue.
            	// The << operator is a bit shift operator, which will move the
            	// bits 8 to the left.
            	// The value should be set to opaque, so the first 8 bits
            	// should be all filled, getting 255 for opaque colors.
                value = 0xff;
                value = (value << 8) + red;
                value = (value << 8) + green;
                value = (value << 8) + blue;
                // Update the 1D pixels array that will be used to generate the image
                newPixels[i * width + j] = value;
            }
        }
        return newPixels;
    }
	
	// The getNumLines method is used to generate the number of lines for a certain
	// map, with a 20% chance of 1, 30% chance of 2, 20% chance of 3, 20% chance of 4
	// and a 10% chance of 5. The Random of the game is passed in to ensure that
	// all random numbers are generated from the seed.
	public int getNumLines(Random rand) {
		// Local Variable
		// This x variable must be declared to store random number generated.
		int x = rand.nextInt(10);
		// Method Body
		if(x < 2) {
			return 1;
		} else if(x < 5) {
			return 2;
		} else if(x < 7) {
			return 3;
		} else if(x < 9) {
			return 4;
		} else {
			return 5;
		}
	}
	
	// The generateLine method is used to generate the lines used to generate the course.
	// This method will generate a Line that will avoid the edges of the map, trying
	// to get into a different quadrant than its own.
	// This method is the only time that the minLineSize and maxLineSize class variables
	// are used, this to avoid excessive parameters, that may confuse a reader and can
	// cause compilation issues.
	public Line generateLine(int startX, int startY, int width, int height, double percentBuffer, double color, Random rand) {
		// Local Variables
		// The minAngle variable determines the minimum angle in which the line
		// must face, to avoid going back into its quadrant.
		// The angle integer determines the angle of the line in degrees.
		// The dist integer determines the distance of the line, in absolute distance
		int minAngle, angle, dist;
		// The diffX and diffY integers are used to take in the components of the vector
		// with angle, angle, and magnitude of distance to then make the line.
		int diffX, diffY;
		if(startX < width / 2) {
			// Left Half
			if(startY < height / 2) {
				// Left Upper
				minAngle = 60;
				// As such the max angle is 210, as you have 150 degree leeway
			} else {
				// Left Lower
				minAngle = 330;
				// As such the max angle is 480, as you have 150 degree leeway
			}
		} else {
			// Right Half
			if(startY < height / 2) {
				// Right Upper
				minAngle = 150;
				// As such the max angle is 300, as you have 150 degree leeway
			} else {
				// Right Lower
				minAngle = 240;
				// As such the max angle is 390, as you have 150 degree leeway
			}
		}
		// The vector is chosen
		angle = (rand.nextInt(150) + minAngle) % 360;
		dist = rand.nextInt(maxLineSize - minLineSize + 1) + minLineSize;
		// Components are taken
		diffX = (int) Math.round(dist * Math.cos(Math.toRadians(angle)));
		diffY = (int) Math.round(dist * Math.sin(Math.toRadians(angle)));
		
		// The vector is continually chosen until the line is within the bounds of the
		// map.
		while(!inBounds(startX + diffX, startY + diffY, width, height, percentBuffer)) {
			angle = (rand.nextInt(150) + minAngle) % 360;
			dist = rand.nextInt(maxLineSize - minLineSize + 1) + minLineSize;
			
			diffX = (int) Math.round(dist * Math.cos(Math.toRadians(angle)));
			diffY = (int) Math.round(dist * Math.sin(Math.toRadians(angle)));
		}
		// Return the Line instance.
		return new Line(startX, startY, startX + diffX, startY + diffY, color);
	}
	
	// The inBounds method will return a boolean value, true if the x and y
	// position is within the bounds of the game, while also accounting for the
	// percent buffer.
	public boolean inBounds(int x, int y, int width, int height, double percentBuffer) {
		return (x >= width * percentBuffer && x < width * (1 - percentBuffer) 
				&& y >= height * percentBuffer && y < height * (1 - percentBuffer));
	}
	
	
	// The createImage method prepares a 1D array of colors, as represented as an integer,
	// from a 2D array of double values. This is used to basically differentiate
	// between the different terrain by declaring a hard cut off for the double
	// values.
	public int[] createImage(double [][] values, int width, int height) {
		// Local Variables
		// The newPixels array is an 1D array of pixels used to create an image
        int[] newPixels = new int[width * height];
        // The red, green, and blue integers are used to make the colors
        // of the map, where red, green and blue are the components of the colors
        // of an RGB type.
        int red, green, blue;
        // The value integer is the integer conversion of the colors of RGB
        // the value is actually the value that is put into the data to image
        // conversion method.
        int value;
        // Method Body
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            	// >= 240, is the fairway, keep in mind that 255 is the maximum
            	// value that is in the double array (although the sum is floored)
            	// so it is possible that this could be changed to make the fairway
            	// smaller.
                if(values[i][j] >= 240) {
                	// Fairway
                	red = 193;
                    green = 230;
                    blue = 149;
                } else if(values[i][j] >= 200) {
                	// >= 200 is the rough.
                	// Rough
                	red = 86;
                    green = 158;
                    blue = 70;
                } else if (values[i][j] == -1){
                	// As stated before, -1 indicates a bunker since that pixel
                	// is too close to a bunker metaball.
                	// Bunker
                	red = 237;
                    green = 210;
                    blue = 174;
                } else {
                	// Extreme Rough
                	red = 58;
                    green = 77;
                    blue = 29;
                }
                
                // Colors in Java are represented as integers, with the
            	// 2nd 8 bit group being red, 3rd green, and 4th is blue.
                // The << is a left shift operator, which shifts all the bits
                // to the left.
                // The value should be set to opaque, so the first 8 bits
            	// should be all filled, getting 255 for opaque colors.
                value = 0xff;
                value = (value << 8) + red;
                value = (value << 8) + green;
                value = (value << 8) + blue;
             // Update the 1D pixels array that will be used to generate the image
                newPixels[i * width + j] = value;
            }
        }
        return newPixels;
	}

	// The getObstaclePath method is used to generate a random 
	// obstacle. There is an equal chance to get each type of obstacle,
	// which are Barriers, Oak Trees, Pine Trees, Small Rocks, Medium Rocks,
	// and Large Rocks.
	// Note that this method only returns the path, but this is important
	// to knowing which type of obstacle the obstacle will be.
	public String getObstaclePath(Random rand) {
		// Local Variables
		// The randomNumber is just a random number used to 
		// check which obstacle path should be returned.
		int randomNum = rand.nextInt(6);
		// Method Body
		if(randomNum == 0) {
			return "res/Barrier.png";
		} else if(randomNum == 1) {
			return "res/OakTree.png";
		} else if(randomNum == 2) {
			return "res/PineTree.png";
		} else if(randomNum == 3) {
			return "res/SmallRock.png";
		} else if(randomNum == 4) {
			return "res/MediumRock.png";
		} else {
			return "res/LargeRock.png";
		}
	}
	
	// The HSVtoRGB method will take in the HSV values, of hue, saturation, and
	// value (brightness) and convert it to a integer array of 3 elements
	// with the elements being R, G, B. The calculation formula is taken from
	// https://www.rapidtables.com/convert/color/hsv-to-rgb.html
	public static int[] HSVtoRGB(double H, double S, double V) {
		// Local Variables
		// The output array is an integer array of the 3 values
		// representing R, G, and B.
		int [] output = new int[3];
		// These doubles are used in the calculation of HSV to RGB
		double C = V * S;
		double X = C * (1 - Math.abs(H / 60 % 2 - 1));
		double m = V - C;
		// The rPrime, gPrime, and bPrime are the RGB values
		// that are the ones used to return in the output array
		double rPrime = 0, gPrime = 0, bPrime = 0;
		// Method Body
		if(H < 60) {
			rPrime = C;
			gPrime = X;
			bPrime = 0;
		} else if(H < 120) {
			rPrime = X;
			gPrime = C;
			bPrime = 0;
		} else if(H < 180) {
			rPrime = 0;
			gPrime = C;
			bPrime = X;
		} else if(H < 240) {
			rPrime = 0;
			gPrime = X;
			bPrime = C;
		} else if(H < 300) {
			rPrime = X;
			gPrime = 0;
			bPrime = C;
		} else if(H < 360) {
			rPrime = C;
			gPrime = 0;
			bPrime = X;
		}
		output[0] = (int)Math.round((rPrime + m) * 255);
		output[1] = (int)Math.round((gPrime + m) * 255);
		output[2] = (int)Math.round((bPrime + m) * 255);
		return output;
		
	}

	// The createImageEasterEgg method is an alternative method to the createImage
	// method, this method is only used when the player's name is Harry.
	// This method prepares a 1D array of colors, as represented as an integer,
	// from a 2D array of double values. Note that unlike the createImage method
	// the method doesn't differentiate terrain, making this map image created
	// harder to play on.
    public static int[] createImageEasterEgg(double [][] values, int width, int height) {
    	// Local Variables
    	// The newPixels are the color 1D array of the values array
    	// which is the last step before being converted to an image
        int[] newPixels = new int[width * height];
        // The value integer is used to store the color for the
        // specific pixel
        int value;
        // The output variable is used to take in the R, G, B values that are
        // converted using the HSVtoRGB method.
        int [] output;
        // Method Body
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            	// Take the values array and convert the value to a color
            	// where those less than 50 are just black, the rest are
            	// converted using the HSV color circle, not cylinder, since 
            	// saturation and brightness are always max.
            	// In the HSV color circle, 0 is red, 120 is green, and 240 is blue
            	// the other degrees are blends of the colors around them, ex. 60 is 
            	// yellow.
            	if(values[i][j] / 255 * 359 < 50) {
                	value = 0;
                } else {
                	output = HSVtoRGB(values[i][j] / 255 * 359, 1, 1);
                    
                    // Colors in Java are represented as integers, with the
                	// 2nd 8 bit group being red, 3rd green, and 4th is blue.
                    // The << is a left shift operator, which shifts all the bits
                    // to the left.
                	// The value should be set to opaque, so the first 8 bits
                	// should be all filled, getting 255 for opaque colors.
                	value = 0xff;
                    value = (value << 8) + output[0];
                    value = (value << 8) + output[1];
                    value = (value << 8) + output[2];
                }
            	// Update the 1D pixels array that will be used to generate the image
                newPixels[i * width + j] = value;
            }
        }
        return newPixels;
    }

	// The arrayToImage method will take in a 1D array of pixel colors, then draw
	// a jpeg image with size width and height, at the given path. The path includes the 
	// complete name of the file. The method throws IOException, as when writing
	// the image, an error can be thrown.
    public void arrayToImage(String path, int width, int height, int[] data) throws IOException {
    	// Local Variables
    	// The MemoryImageSource is a class that will be used to take in a 1D array
    	// of pixels and produce an ImageProducer object. The MemoryImageSource class
    	// is actually a polymorph of the ImageProducer class to provide more
    	// ways to produce an ImageProducer, like an array of pixels.
        MemoryImageSource mis = new MemoryImageSource(width, height, data, 0, width);
        // The ImageProducer, the mis, will then produce the image data for the
        // image, which is then processed by the Toolkit class to produce an image
        // object.
        Image im = Toolkit.getDefaultToolkit().createImage(mis);
        
        // The image is then drawn onto a BufferedImage to allow it to be written.
        // BufferedImages are a direct parent of a RenderedImage, which allows it
        // to be written onto the hard drive.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Method Body
        bufferedImage.getGraphics().drawImage(im, 0, 0, null);
        ImageIO.write(bufferedImage, "jpg", new File(path));
    }
}
