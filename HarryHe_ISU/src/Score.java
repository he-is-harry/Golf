/*
 * The Score class is used to help model a score that a player
 * could have gotten in the game. This includes the player's name,
 * their time to complete the hole, as well as what map and type of
 * hole they got their high score on.
 * 
 * The Score will also store common methods which will be used to process
 * scores and help reduce clutter in the main program.
 * Refer to the methods below for more information on what they do.
 */

public class Score {
	// Class Variables
	// The name represents the player name entered
	String name;
	// The milliTime represents the player's time
	// to complete the hole in milliseconds
	long milliTime;
	// The seed represents the long value that is
	// used as the seed to randomly generate numbers
	long seed;
	// The milliTimeStr represents the String representation
	// of the millisecond time taken to complete the hole
	String milliTimeStr;
	
	// Constructor for the score class, declares Score class
	// whenever a new object is called
	public Score(String name, long milliTime, long seed) {
		// Values read in will be passed into the score
		// variables
		this.name = name;
		this.milliTime = milliTime;
		this.seed = seed;
		this.milliTimeStr = processMilliTime(milliTime);
	}
	
	// The processMilliTime method will return the 
	// String representation of the millisecond time,
	// to make it easier to read the time taken. This
	// will be expressed in hours, minutes, seconds, and
	// milliseconds taken to complete the level.
	public static String processMilliTime(long milliTime) {
		// Local Variables
		// The ans String is the return value
		String ans = "";
		// The timeLeft long is remainder amount time left
		// after a some of the time units are taken off (like
		// after removing hours, how many minutes were used)
		long timeLeft = milliTime;
		
		// Method Body
		if(timeLeft >= 86400000) {
			// A day was taken to complete a level
			return "Way Too Long";
		} 
		if(timeLeft >= 3600000) {
			// More than an hour was taken to complete a level
			ans += " " + (timeLeft / 3600000) + " hr";
			timeLeft = timeLeft % 3600000;
		}
		if(timeLeft >= 60000) {
			// A minute is left over to complete a level
			ans += " " + (timeLeft / 60000) + " min";
			timeLeft = timeLeft % 60000;
		}
		if(timeLeft >= 1000) {
			// A second is left over to complete a level
			ans += " " + (timeLeft / 1000) + " sec";
			timeLeft = timeLeft % 1000;
		}
		if(timeLeft > 0) {
			// A millisecond is left over to complete a level
			ans += " " + timeLeft + " ms";
		}
		return ans.trim();
	}
	
	// The String input of a score from the text file, will
	// be output as a String array, which will have split the
	// name, millisecond time to complete the level, and the seed
	// used to generate the level based on the spaces separating them.
	public static String[] processScore(String str) {
		// Local Variables
		// The output String array is the return value of the method
		// which stores the name, millisecond time, and the seed.
		String [] output = new String[3];
		
		// Method Body
		output[2] = str.substring(str.lastIndexOf(' ') + 1);
		str = str.substring(0, str.lastIndexOf(' '));
		output[1] = str.substring(str.lastIndexOf(' ') + 1);
		str = str.substring(0, str.lastIndexOf(' '));
		output[0] = str;
		return output;
	}
}
