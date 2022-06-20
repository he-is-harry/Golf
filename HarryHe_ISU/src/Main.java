/*
 * Harry He
 * June 20 2021
 * 
 * Program Description:
 * The program will run a 2D game that uses keyboard and mouse input, that will allow
 * a player to play holes of golf with a golf cart. The game will keep 3 high scores
 * of the lowest time to complete the 5 different pars, provide instructions on
 * how to play, and give credits for the development team. More specifically, in the
 * game, the player will be able to rotate and move a golf cart to hit a ball in 
 * a randomly generated map with the assistance of a minimap and a large map that can
 * be opened when prompted, and when the ball ends up in the golf hole, the round will
 * end and the player can start again.
 */

// Importing the necessary classes to run the game
// including the rendering of images and text, mouse
// and keyboard input, random number generation, and
// storing of game stats.
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

// The main class will be a JPanel, which allows for threading,
// mouse input and keyboard input.
public class Main extends JPanel implements Runnable, MouseListener, KeyListener {
	// Class Variables
	// The WIDTH and HEIGHT variables represent the width and height
	// of the window, JPanel, which the game will be played in.
	public static final int WIDTH = 1080;
	public static final int HEIGHT = 810;
	// The screenMidX and screenMidY variables represent the middle x and y
	// of the window, JPanel, which the game will be displayed in respectively.
	public static final int screenMidX = WIDTH / 2;
	public static final int screenMidY = HEIGHT / 2;
	// The magnificationScale double represents the magnification scale
	// that will be used to multiply the size of the image.
	public static final double magnificationScale = 5;
	// The mapCornerX and mapCornerY integers represent the top left corner
	// x and y coordinates which are used to place the player and ball on the
	// prompt map.
	public static final int mapCornerX = 150;
	public static final int mapCornerY = 15;

	// Game Stats
	
	/*
	 * There will be 15 game states for the game 
	 * 0 - Menu screen 
	 * 1 - Transition
	 * screen to game, where you enter your name and then enter the game after
	 * pressing a button 
	 * 2 - Actual game 
	 * 3 - Map screen, the map of the entire hole
	 * is overlaid on top of the game, but the game is rendered underneath the map 
	 * 4 - Pause Screen 
	 * 5 - Score screen once the game is done, shows the time taken
	 * to complete the par of the hole, allows the user to go back to the main menu
	 * 6 - High score screen page 1, for pars 1 
	 * 7 - High score screen page 2, for pars 2 
	 * 8 - High score screen page 3, for pars 3 
	 * 9 - High score screen page 4, for pars 4 
	 * 10 - High score screen page 5, for pars 5
	 * 11 - Loading screen for the map generator to generate the map
	 * 12 - Instructions screen for the game
	 * 13 - Second instructions screen for the game, for more instruction
	 * 14 - Credits screen for the game
	 */
	public static int gameState = 0;

	// The inputBoxSelected represents if the name input box
	// is selected or not
	public static boolean nameBoxSelected = false;
	// The inputBoxSelected represents if the seed input box
	// is selected or not
	public static boolean seedBoxSelected = false;
	// The playerName String represents the name of the player
	// entered
	public static String playerName = "";
	// The inputSeed String represents the seed that the player entered.
	// When the inputSeed is blank, there will be a randomly generated seed
	// otherwise, the inputSeed is hashed into a long so there will be some
	// overlaps. If the inputSeed is just a number the number will be used as the
	// seed.
	public static String inputSeed = "";
	// The player variable is a variable of the custom made class
	// Player, the player variable will represent the player in the game
	// including but not limited to its rendering image, angle, velocity,
	// and collision delay.
	// Refer to the Player class file for more information on how the player
	// works.
	public static Player player;
	// The pivotX and pivotY doubles represent the pivot point for the game, which
	// allows the player to always be in the center of the screen.
	// The pivot is always rendered as the middle of the screen, but the player
	// or the ball can be the pivot point.
	public static double pivotX, pivotY;
	// The ball variable is a variable of the custom class GolfBall. The GolfBall
	// class will represent the golf ball in the game. The GolfBall class will
	// store the rendering image, x and y position, velocity, and collision delay
	// of the ball.
	// Refer to the GolfBall class file for more information on how the GolfBall
	// works.
	public static GolfBall ball;
	// The highScores variable is an arraylist of an arraylist of scores.
	// Note that the highScores variable will only contain 5 arraylists as
	// there are only 5 pars, as well as only 3 high scores for each par.
	// The highScores variable will keep the best scores for each par, keeping at
	// most 3 scores for the 5 pars.
	// The score class will keep the player name, the millisecond time taken to
	// complete the hole, and the map seed that the hole used.
	public static ArrayList<ArrayList<Score>> highScores;
	// The startTime variable is a long of the time in milliseconds,
	// when the player started
	public static long startTime;
	
	// The timeTaken long indicates the time the player took to complete the hole
	// in milliseconds.
	public static long timeTaken;
	
	// The mapGenerator variable is an object of the MapGenerator class which is used
	// to generate the maps in the game. The MapGenerator class will write in the
	// map in the map.jpeg file which is used to render in the map later.
	// Refer to the MapGenerator class file for more information on how the
	// map generator works.
	MapGenerator mapGenerator;
	
	// The holeRadius variable represents the diameter of the golf ball hole.
	// This value is derived from the real life ratio of golf ball to golf hole
	// and the in game width and height of the golf ball.
	// 4.25 in / 1.680 in * 115 pixels = Approximately 290 pixels
	public static final int holeDiameter = 290;
	
	// The hits integer is unnecessary but could be rendered to help the player,
	// showing how many times the player hit the ball
	// The hits variable keeps track of the amount of times that the player has
	// hit the golf ball
	public static int hits;

	// Rendering Resources
	// These are the images and fonts that the game will render

	// These are the images that the game will use and show the the user
	// These are all the images for the menu
	public static BufferedImage menuBackground;
	public static BufferedImage preGameBackground;
	public static BufferedImage scoreScrnBackground;
	public static BufferedImage loadingBackground;
	public static BufferedImage menuTitle;
	public static BufferedImage longMenuButton;
	public static BufferedImage enterNameHeader;
	public static BufferedImage textInputBox;
	public static BufferedImage lightedInputBox;
	public static BufferedImage pauseMenu;
	public static BufferedImage highScoreBackground;
	public static BufferedImage leftArrowButton;
	public static BufferedImage rightArrowButton;
	public static BufferedImage instructionsBackground;
	public static BufferedImage instructionsBackground2;
	public static BufferedImage creditsBackground;
	
	// Images for the game
	// The mapBackground is the BufferedImage gotten from the random generated map
	public static BufferedImage mapBackground;
	// The promptMap is the BufferedImage gotten from the random generated map
	// but this one will be rescaled to fit inside the screen, so the player can
	// see the entire map.
	public static BufferedImage promptMap;
	// The flagPole image is the BufferedImage that is used to render in the flag post
	// for the hole
	public static BufferedImage flagPole;
	// The smallFlagPole, smallGolfBall, and playerIcon BufferedImages are used 
	// to render in small versions of the player, golf ball, and flag pole on the
	// prompt map.
	public static BufferedImage smallFlagPole;
	public static BufferedImage smallGolfBall;
	public static BufferedImage playerIcon;
	// The playerIconSource BufferedImage is the image used to generate the 
	// rotated playerIcon image.
	public static BufferedImage playerIconSource;
	// The miniMapBackground BufferedImage is the background part of the minimap
	// that will help to differentiate the minimap from the background in the game
	public static BufferedImage miniMapBackground;
	// The miniMap BufferedImage is the image that will be displayed as the minimap.
	// This image will be a cropped from of the miniMapSource
	public static BufferedImage miniMap;
	// The miniMapSource BufferedImage is the image that will be used to get the
	// miniMap image, this is needed to avoid having to get the image every time
	// from the ImageIO.
	public static BufferedImage miniMapSource;
	// The indicationArrow BufferedImage is the image that will be used to 
	// point a ball or flag pole in the minimap, this image is the rotated indication
	// arrow.
	public static BufferedImage indicationArrow;
	// The indicationArrowSource BufferedImage is the image that is used to get the
	// indicationArrow image, this source image is unrotated.
	public static BufferedImage indicationArrowSource;
	
	// These are the font that are loaded in to the game which are used
	// to draw text
	// The buttonFont font is used to render the text on buttons as well
	// as input boxes
	public static Font buttonFont;
	// The headerScoreFont font is used to render the headers on the score page (after
	// you complete a hole) in the top left corner of the boxes, as well as some of the
	// scores on that page themselves.
	public static Font headerScoreFont;
	// The mediumScoreFont font is used to render the scores on the score page, as some
	// scores (name, hits, time, map) can be longer than the box
	public static Font mediumScoreFont;
	// The highScoreFont font is used to render the high scores on the high score
	// page
	public static Font highScoreFont;
	// The errorFont is used the render the error messages on the high score page
	public static Font errorFont;
	// The errorMessage is the error message rendered to the player
	// on the in between screen before the player plays the game
	// if the player doesn't enter a name or the name is too long
	public static String errorMessage = "";

	// User Input
	// mouseX and mouseY represent the
	// mouse position
	public static int mouseX;
	public static int mouseY;
	// The keyDown array represents which keys
	// the user has pressed down
	// 0 - W
	// 1 - S
	// 2 - D
	// 3 - A
	public static boolean[] keyDown;
	
	// Transition Variables
	// These variables are used to smoothly transition between
	// the game states, like the waiting delay before the score screen is shown
	
	// The winScreenDelay integer represents the amount of ticks remaining to
	// then move to display the winning screen, or the score screen.
	public static int winScreenDelay;
	
	// The castRectLength integer represents the length of the transparent rectangle
	// that comes down after the hole is completed, which transitions from the game
	// to the score screen. This will gradually increase by a constant value until
	// it completely covers the screen, then the game state will be changed.
	public static int castRectLength;

	// Constructor of main class, will initialize every
	// component of the game.
	public Main() {
		try {
			// Reading in the images of the menus
			menuBackground = ImageIO.read(new File("res/MenuBackground.png"));
			preGameBackground = ImageIO.read(new File("res/PreGameBackground.png"));
			scoreScrnBackground = ImageIO.read(new File("res/ScoreScreenBackground.png"));
			loadingBackground = ImageIO.read(new File("res/LoadingBackground.png"));
			menuTitle = ImageIO.read(new File("res/Title.png"));
			longMenuButton = ImageIO.read(new File("res/Button.png"));
			enterNameHeader = ImageIO.read(new File("res/NameHeader.png"));
			textInputBox = ImageIO.read(new File("res/TextInput.png"));
			lightedInputBox = ImageIO.read(new File("res/TextInputHighlighted.png"));
			pauseMenu = ImageIO.read(new File("res/PauseMenu.png"));
			highScoreBackground = ImageIO.read(new File("res/HighscoreBackground.png"));
			leftArrowButton = ImageIO.read(new File("res/LeftArrowButton.png"));
			rightArrowButton = ImageIO.read(new File("res/RightArrowButton.png"));
			instructionsBackground = ImageIO.read(new File("res/InstructionsBackground.png"));
			instructionsBackground2 = ImageIO.read(new File("res/InstructionsBackground2.png"));
			creditsBackground = ImageIO.read(new File("res/CreditsBackground.png"));
			
			// Reading in the images of the game
			flagPole = ImageIO.read(new File("res/FlagPole.png"));
			smallFlagPole = ImageUtilities.toBufferedImage(flagPole.getScaledInstance(32, 32, 
					java.awt.Image.SCALE_SMOOTH));
			smallGolfBall = ImageUtilities.toBufferedImage(
					ImageIO.read(new File("res/Ball.png")).getScaledInstance(32, 32, 
					java.awt.Image.SCALE_SMOOTH));
			miniMapBackground = ImageIO.read(new File("res/MinimapBackground.png"));
			indicationArrowSource = ImageIO.read(new File("res/IndicationArrow.png"));
			playerIconSource = ImageIO.read(new File("res/PlayerIcon.png"));
			
			buttonFont = loadFont("res/contm.ttf", 70);
			headerScoreFont = loadFont("res/contm.ttf", 52);
			mediumScoreFont = loadFont("res/contm.ttf", 36);
			highScoreFont = loadFont("res/contm.ttf", 26);
			errorFont = new Font("arial", Font.PLAIN, 20);
		} catch (IOException e) {
			// Error is thrown, so we have not found a file
			System.out.println("Error 404: File Not Found");
			e.printStackTrace();
		}

		// Declaration of the player class using an image of a golf cart
		player = new Player("res/GolfCart.png");
		// Declaration of the pivot coordinates which are declared here to avoid
		// null pointer exceptions
		pivotX = player.x;
		pivotY = player.y;
		// Declaration of the golf ball, which will use the image of a ball
		ball = new GolfBall("res/Ball.png");
		
		// Declaration of the map generator
		mapGenerator = new MapGenerator();

		// Declaration of the keyDown array
		keyDown = new boolean[4];

		// Declaration of the highScore arraylist, of arraylists
		highScores = new ArrayList<>();
		// Initiallizing the 5 arraylists for the 5 different pars
		for (int i = 0; i < 5; i++) {
			highScores.add(new ArrayList<>());
		}
		// Trying to read in the scores of stored in the "highscore.txt"
		// file
		try {
			readScores();
		} catch (IOException e) {
			// An error is catched, returning the error message and point in which
			// the program threw an error
			// This try catch statement is put in a different location to help
			// differentiate between the images and font input and output and the text
			// file streaming
			System.out.println("Error 405: Cannot Read Scores");
			e.printStackTrace();
		}
		
		// Declaration of the winScreenDelay and castRectLength, this is set to zero 
		// initially to avoid null pointer exceptions
		winScreenDelay = 0;
		castRectLength = 0;

		// The window is declared, with the given width and height
		// declared at the start of the file
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		// The default background color is white, however, the map image
		// should always apart be the background of the game
		setBackground(new Color(255, 255, 255));
		// Adding mouse and keyboard input
		addKeyListener(this);
		addMouseListener(this);
		this.setFocusable(true);

		// The thread is declared and started which will be used to
		// start the timer and as such the game
		// Theoretically, despite there being more commands after the
		// the declaration of the JPanel in the main, those are
		// only for the JFrame and will not cause errors if the window
		// starts being rendered on.
		Thread thread = new Thread(this);
		thread.start();
	}

	// The paintComponent method will draw all of the images and graphics
	// onto the window the game is played on. The method will draw
	// different images and graphics depending on the game state the game is
	// currently in. Refer to the gameState variable documentation and the inner
	// comments in the method to what the method will draw at each game state.
	public void paintComponent(Graphics g) {
		// Local Variables
		// These local variables are really only used when rendering the golf game
		
		// The diffX and diffY variables are used to pivot the player
		// in the game, where the rendering is always centered on the player
		double diffX, diffY;
		// The angle variable is used to draw in the indication arrows to show
		// the direction of the ball and the flag pole in the minimap
		double angle;
		// The arrowX and arrowY doubles are the coordinates of the indication
		// arrows center on the minimap.
		double arrowX, arrowY;
		// The g2d Graphics2D variable is a Graphics2D instance of the regular Graphics g
		// which allows for more advanced rendering like that of transparent rectangles
		// which the program will do.
		Graphics2D g2d;
		// Method Body
		if (gameState == 0) {
			// The main menu of the game is drawn onto the screen
			g.drawImage(menuBackground, 0, 0, null);
			g.drawImage(menuTitle, 0, 0, null);
			g.drawImage(longMenuButton, 120, 440, null);
			g.drawImage(longMenuButton, 560, 440, null);
			g.drawImage(longMenuButton, 120, 600, null);
			g.drawImage(longMenuButton, 560, 600, null);
			g.setFont(buttonFont);
			g.drawString("Play", 254, 520);
			g.drawString("Highscore", 615, 520);
			g.drawString("Instructions", 155, 685);
			g.drawString("Credits", 660, 685);
		} else if (gameState == 1) {
			// The transition menu of the game is drawn onto the screen
			// where the player will enter their name and enter the game
			g.drawImage(preGameBackground, 0, 0, null);
			g.drawImage(enterNameHeader, 0, 0, null);
			g.drawImage(longMenuButton, 70, 650, null);
			g.drawImage(longMenuButton, 610, 650, null);
			g.setFont(buttonFont);
			// Draw in the name selection box
			if (nameBoxSelected) {
				g.drawImage(lightedInputBox, 80, 300, null);
				g.drawString(playerName, 100, 380);
			} else {
				g.drawImage(textInputBox, 80, 300, null);
				if (playerName.length() == 0) {
					g.drawString("Click to Select", 100, 380);
				} else {
					g.drawString(playerName, 100, 380);
				}
			}
			
			// Draw in the seed selection box
			if(seedBoxSelected) {
				g.drawImage(lightedInputBox, 80, 460, null);
				g.drawString(inputSeed, 100, 540);
			} else {
				g.drawImage(textInputBox, 80, 460, null);
				if (inputSeed.length() == 0) {
					g.drawString("Click to Set Seed", 100, 540);
				} else {
					g.drawString(inputSeed, 100, 540);
				}
			}

			g.drawString("Menu", 185, 730);
			g.drawString("Play", 750, 730);
			
			g.setFont(errorFont);
			g.setColor(new Color(0, 0, 0));
			g.drawString("Leave Blank for Random Seed", 100, 605);
			if (errorMessage.length() > 0) {
				// An error message will be rendered if there is
				// an error that the player has encountered
				g.setColor(new Color(255, 0, 0));
				g.drawString(errorMessage, 100, 450);
			}
		} else if (gameState <= 4) {
			// The actual game part of the game is draw onto the screen
			// whenever the game is in game states 2, 3 and 4 because the
			// back part of the game should be rendered to help the user
			// orientate themselves.
			
			// Map being rendered
			diffX = -pivotX;
			diffY = -pivotY;
			g.drawImage(mapBackground, (int)(screenMidX + diffX), (int)(screenMidY + diffY), null);
			
			// Hole being rendered
			if(!ball.inHole) {
				// Ball not in hole, make it darker
				g.setColor(new Color(94, 93, 100));
			} else {
				// Ball is in hole, make it lighter
				g.setColor(new Color(130, 129, 138));
			}
			
			// Use the pivot to find the relative x and y position where the hole
			// should be rendered
			diffX = mapGenerator.holeX - holeDiameter / 2 - pivotX;
			diffY = mapGenerator.holeY - holeDiameter / 2 - pivotY;
			g.fillOval((int)(screenMidX + diffX), (int)(screenMidY + diffY), holeDiameter, holeDiameter);
			diffX = mapGenerator.holeX - (flagPole.getWidth() / 2) - pivotX;
			diffY = mapGenerator.holeY - (flagPole.getHeight() / 2) - pivotY;
			g.drawImage(flagPole, (int)(screenMidX + diffX), (int)(screenMidY + diffY), null);
			
			// Player being rendered
			g.setColor(new Color(255, 0, 0));
			diffX = player.getBounds().x - pivotX;
			diffY = player.getBounds().y - pivotY;
			diffX = player.x - player.toRender.getWidth() / 2 - pivotX;
			diffY = player.y - player.toRender.getHeight() / 2 - pivotY;
			g.drawImage(player.toRender, (int)(screenMidX + diffX), (int)(screenMidY + diffY), null);
			
			// Ball being rendered
			diffX = ball.x - ball.source.getWidth() / 2 - pivotX;
			diffY = ball.y - ball.source.getHeight() / 2 - pivotY;
			g.drawImage(ball.source, (int)(screenMidX + diffX), (int)(screenMidY + diffY), null);
			diffX = ball.getBounds().x - pivotX;
			diffY = ball.getBounds().y - pivotY;
			
			// Obstacles being rendered
			for(int i = 0; i < mapGenerator.obstacles.length; i++) {
				diffX = mapGenerator.obstacles[i].getBounds().x - pivotX;
				diffY = mapGenerator.obstacles[i].getBounds().y - pivotY;
				diffX = mapGenerator.obstacles[i].x - mapGenerator.obstacles[i].source.getWidth() / 2 - pivotX;
				diffY = mapGenerator.obstacles[i].y - mapGenerator.obstacles[i].source.getHeight() / 2 - pivotY;
				g.drawImage(mapGenerator.obstacles[i].source, (int)(screenMidX + diffX), (int)(screenMidY + diffY), null);
			}
			
			
			// Take a cropped form of the miniMapSource to get the minimap
			// We need this if statement so that we do not go out of bounds
			// in the cropping method. If the map is out of bounds, we just
			// leave the miniMap image as the previous produced.
			// We update this no matter if the map is open because if the map closes
			// and the player is out of the bounds of the minimap, we need to have the
			// latest value which was not out of bounds
			if(player.x / magnificationScale - 150 >= 0 && player.x / magnificationScale + 150 < miniMapSource.getWidth() &&
					player.y / magnificationScale - 150 >= 0 && player.y / magnificationScale + 150 < miniMapSource.getHeight()) {
				miniMap = miniMapSource.getSubimage((int)(player.x / magnificationScale - 150), 
						(int)(player.y / magnificationScale - 150), 300, 300);
			}
			if(gameState != 3) {
				// Render the minimap when the map is not active
				g.drawImage(miniMapBackground, 760, 0, null);
				// Draw the miniMap
				g.drawImage(miniMap, 770, 10, null);
				// Render in the player icon at the center of the minimap, since the minimap is
				// centered around the player
				playerIcon = ImageUtilities.rotateImageTransparent(playerIconSource, player.angle);
				g.drawImage(playerIcon, 920 - playerIcon.getWidth() / 2, 160 - playerIcon.getHeight() / 2, null);
				// Render in the ball icon when the ball is within the bounds of the
				// minimap
				if(Math.abs(ball.x - player.x) / magnificationScale <= 150 && Math.abs(ball.y - player.y) / magnificationScale <= 150) {
					g.drawImage(smallGolfBall, (int)(920 + (ball.x - player.x) / magnificationScale - smallGolfBall.getWidth() / 2), 
							(int)(160 + (ball.y - player.y) / magnificationScale - smallGolfBall.getHeight() / 2), null);
				} else {
					// Use the player's angle to orientate the arrow
					angle = getAngle(player.x, player.y, ball.x, ball.y) + 90;
					// Avoid negative angles to make if statements easier
					if(angle < 0) {
						angle += 360;
					}
					// Rotate the indication arrow
					indicationArrow = ImageUtilities.rotateImageTransparent(indicationArrowSource, angle);
					
					// For the if statements we use the nonconverted rotation's angle,
					// so 0 is top 90 is right, 180 is bottom, 270 is left
					
					// Then to convert the angle into a conventional angle we must take the angle
					// and subtract it from 90.
					// This is because conventional angles spin counter clockwise, not clockwise like the rotation,
					// and the rotation angle's 0 is equivalent to 90 in conventional angles
					// Additionally, we must use negative and positive distances to avoid using related acute angles
					// and CAST rule when doing the arrow placement
					if(angle > 315 || angle <= 45) {
						// Top Hinge
						// Edge of minimap is y = 10
						// We calculate the x coordinate as distance of arrow to center
						// vertically / tan(angle) + center of minimap
						arrowY = 10 + indicationArrow.getHeight() / 2;
						arrowX = (int)((160 - arrowY) / Math.tan(Math.toRadians(90 - angle))) + 920;
					} else if(angle <= 135) {
						// Right Hinge
						// Using the edge of the minimap being x = 1070, 
						// we calculate the y coordinate as tan(angle) * distance of 
						// arrow to center of minimap + center of minimap
						arrowX = 1070 - indicationArrow.getWidth() / 2;
						arrowY = (int)(Math.tan(Math.toRadians(90 - angle)) * (920 - arrowX)) + 160;
					} else if(angle <= 225) {
						// Bottom Hinge
						// Edge of minimap is y = 310
						// We calculate the x coordinate as distance of arrow to center
						// vertically / tan(angle) + center of minimap
						arrowY = 310 - indicationArrow.getHeight() / 2;
						arrowX = (int)((160 - arrowY) / Math.tan(Math.toRadians(90 - angle))) + 920;
					} else {
						// Left Hinge
						// Edge of minimap is x = 770
						// We calculate the y coordinate as tan(angle) * distance of 
						// arrow to center of minimap + center of minimap
						arrowX = 770 + indicationArrow.getWidth() / 2;
						arrowY = (int)(Math.tan(Math.toRadians(90 - angle)) * (920 - arrowX)) + 160;
					}
					
					g.drawImage(indicationArrow, (int)arrowX - indicationArrow.getWidth() / 2, 
							(int)arrowY - indicationArrow.getHeight() / 2, null);
					g.drawImage(smallGolfBall, (int)(arrowX - smallGolfBall.getWidth() / 2),
							(int)(arrowY - smallGolfBall.getHeight() / 2), null);
				}
				
				// Rendering in the flag pole icon when the flag pole is within the bounds
				// of the minimap
				if(Math.abs(mapGenerator.holeX - player.x) / magnificationScale <= 150 && 
						Math.abs(mapGenerator.holeY - player.y) / magnificationScale <= 150) {
					g.drawImage(smallFlagPole, (int)(920 + (mapGenerator.holeX - player.x) / magnificationScale - smallFlagPole.getWidth() / 2), 
							(int)(160 + (mapGenerator.holeY - player.y) / magnificationScale - smallFlagPole.getWidth() / 2), null);
				} else {
					// Use the player's angle to orientate the arrow
					angle = getAngle(player.x, player.y, mapGenerator.holeX, mapGenerator.holeY) + 90;
					// Avoid negative angles to make if statements easier
					if(angle < 0) {
						angle += 360;
					}
					// Rotate the indication arrow
					indicationArrow = ImageUtilities.rotateImageTransparent(indicationArrowSource, angle);
					
					// For the if statements we use the nonconverted rotation's angle, 
					// so 0 is top 90 is right, 180 is bottom, 270 is left
					
					// Then to convert the angle into a conventional angle we must take the angle
					// and subtract it from 90.
					// This is because conventional angles spin counter clockwise, not clockwise like the rotation,
					// and the rotation angle's 0 is equivalent to 90 in conventional angles
					// Additionally, we must use negative and positive distances to avoid using related acute angles
					// and CAST rule when doing the arrow placement
					if(angle > 315 || angle <= 45) {
						// Top Hinge
						// Edge of minimap is y = 10
						// We calculate the x coordinate as distance of arrow to center
						// vertically / tan(angle) + center of minimap
						arrowY = 10 + indicationArrow.getHeight() / 2;
						arrowX = (int)((160 - arrowY) / Math.tan(Math.toRadians(90 - angle))) + 920;
					} else if(angle <= 135) {
						// Right Hinge
						// Using the edge of the minimap being x = 1070, 
						// we calculate the y coordinate as tan(angle) * distance of 
						// arrow to center of minimap + center of minimap
						arrowX = 1070 - indicationArrow.getWidth() / 2;
						arrowY = (int)(Math.tan(Math.toRadians(90 - angle)) * (920 - arrowX)) + 160;
					} else if(angle <= 225) {
						// Bottom Hinge
						// Edge of minimap is y = 310
						// We calculate the x coordinate as distance of arrow to center
						// vertically / tan(angle) + center of minimap
						arrowY = 310 - indicationArrow.getHeight() / 2;
						arrowX = (int)((160 - arrowY) / Math.tan(Math.toRadians(90 - angle))) + 920;
					} else {
						// Left Hinge
						// Edge of minimap is x = 770
						// We calculate the y coordinate as tan(angle) * distance of 
						// arrow to center of minimap + center of minimap
						arrowX = 770 + indicationArrow.getWidth() / 2;
						arrowY = (int)(Math.tan(Math.toRadians(90 - angle)) * (920 - arrowX)) + 160;
					}
					
					// Draw in the flag pole and the indication arrow
					g.drawImage(indicationArrow, (int)arrowX - indicationArrow.getWidth() / 2, 
							(int)arrowY - indicationArrow.getHeight() / 2, null);
					g.drawImage(smallFlagPole, (int)(arrowX - smallFlagPole.getWidth() / 2),
							(int)(arrowY - smallFlagPole.getHeight() / 2), null);
				}
			} 
			
			if(gameState == 3) {
				// Render in the map if the large map is active
				// Draw in a background rectangle for the promptMap image
				g.setColor(new Color(255, 215, 0));
				g.fillRect(145, 10, promptMap.getWidth() + 10, promptMap.getHeight() + 10);
				// Draw in the promptMap image
				g.drawImage(promptMap, 150, 15, null);
				// Draw in the player, golf ball and flag pole to help the
				// player see where they are on the map
				g.drawImage(smallFlagPole, 
						(int)((mapGenerator.holeX * promptMap.getWidth() / magnificationScale) / 2000 + mapCornerX) - smallFlagPole.getWidth() / 2,
						(int)((mapGenerator.holeY * promptMap.getHeight() / magnificationScale) / 2000 + mapCornerY) - smallFlagPole.getHeight() / 2, null);
				g.drawImage(smallGolfBall, 
						(int)((ball.x * promptMap.getWidth() / magnificationScale) / 2000 + mapCornerX) - smallGolfBall.getWidth() / 2,
						(int)((ball.y * promptMap.getHeight() / magnificationScale) / 2000 + mapCornerY) - smallGolfBall.getHeight() / 2, null);
				
				// Rotate the player icon to be the same angle as the player
				playerIcon = ImageUtilities.rotateImageTransparent(playerIconSource, player.angle);
				g.drawImage(playerIcon, 
						(int)((player.x * promptMap.getWidth() / magnificationScale) / 2000 + mapCornerX) - playerIcon.getWidth() / 2,
						(int)((player.y * promptMap.getHeight() / magnificationScale) / 2000 + mapCornerY) - playerIcon.getHeight() / 2, null);
			} else if (gameState == 4) {
				// Render pause screen if the pause screen is active
				g.drawImage(pauseMenu, 225, 186, null);
				g.drawImage(longMenuButton, 340, 310, null);
				g.drawImage(longMenuButton, 340, 460, null);
				g.setFont(buttonFont);
				g.setColor(new Color(0, 0, 0));
				g.drawString("Resume", 415, 390);
				g.drawString("Menu", 450, 545);
			}
			
			// Rendering of the transparent rectangle which will transition the player
			// to the score screen once the hole is completed.
			if(castRectLength > 0) {
				// The Graphics2D class extends further upon the graphics class to 
				// implement more features like color management, which we need for 
				// transparent colors.
				g2d = (Graphics2D) g;
				// The AlphaComposite class implements transparency rules to help 
				// create transparent colors which can latter be rendered in
		        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4F));
		        g.setColor(new Color(255, 255, 255));
		        g.fillRect(0, 0, 1080, castRectLength);
		        // Set back to fully clear when rendering other resources
		        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
			}
			
		} else if (gameState == 5) {
			// Ending screen is being rendered, where the user will
			// have feedback on their time
			g.drawImage(scoreScrnBackground, 0, 0, null);
			g.setColor(new Color(255, 255, 255));
			g.setFont(headerScoreFont);
			g.drawString("Name", 100, 200);
			g.drawString("Hits", 595, 200);
			g.drawString("Time", 100, 440);
			g.drawString("Map", 595, 440);
			// Scores that use larger fonts are rendered first
			// so the rendering of the strings is not in order of
			// which they appear in the boxes
			g.setColor(new Color(240, 140, 70));
			// Largest font
			g.setFont(buttonFont);
			g.drawString(Integer.toString(hits), 605, 290);
			// Medium font
			g.setFont(mediumScoreFont);
			g.drawString(playerName, 110, 280);
			g.drawString(Long.toString(timeTaken) + " ms", 110, 510);
			g.drawString("Par: " + mapGenerator.par, 605, 510);
			// Smallest font
			g.setFont(highScoreFont);
			g.drawString(Score.processMilliTime(timeTaken), 110, 540);
			g.drawString("Seed: " + mapGenerator.mapSeed, 605, 540);
			
			g.drawImage(longMenuButton, 100, 640, null);
			g.drawImage(longMenuButton, 600, 640, null);
			g.setFont(buttonFont);
			g.setColor(new Color(0, 0, 0));
			g.drawString("Menu", 205, 720);
			g.drawString("Highscores", 638, 720);
		} else if (gameState <= 10) {
			// High score page is being rendered and each page
			// will have different high scores for each par but the
			// main template is the same, which is why this rendering part
			// includes game states 6, 7, 8, 9, and 10.
			
			// Template of high score screen is being rendered
			g.drawImage(highScoreBackground, 0, 0, null);
			g.setFont(buttonFont);
			g.setColor(new Color(0, 0, 0));
			g.drawString("Name", 110, 180);
			g.drawString("Time", 470, 180);
			// Par of the page is actually based on the game state, as each page
			// represents an increasing par
			g.drawString("Par " + (gameState - 5) + " Map", 730, 180);
			g.drawImage(longMenuButton, 340, 675, null);
			g.drawString("Menu", 454, 755);
			if (gameState > 6) {
				g.drawImage(leftArrowButton, 80, 703, null);
			}
			if (gameState < 10) {
				g.drawImage(rightArrowButton, 1000, 703, null);
			}

			// Draw on the high scores
			g.setFont(highScoreFont);
			for (int i = 0; i < highScores.get(gameState - 6).size(); i++) {
				// Take the scores from the highScores variable in the specific 
				// corresponding par and draw them onto the screen.
				g.drawString(highScores.get(gameState - 6).get(i).name, 60, 250 + 80 * i);
				g.drawString(highScores.get(gameState - 6).get(i).milliTimeStr, 375, 250 + 80 * i);
				g.drawString(Long.toString(highScores.get(gameState - 6).get(i).seed), 740, 250 + 80 * i);
			}
		} else if(gameState == 11) {
			// Draw in the loading screen
			g.drawImage(loadingBackground, 0, 0, null);
		} else if(gameState == 12) {
			// Render the instructions screen
			g.drawImage(instructionsBackground, 0, 0, null);
			g.setColor(new Color(255, 255, 255));
			g.setFont(mediumScoreFont);
			g.drawString("The purpose of the game is to", 115, 200);
			g.drawString("hit the ball into the hole.", 115, 235);
			g.drawString("Controls", 115, 410);
			g.fillRect(115, 414, 150, 3);
			g.drawString("Press W to accelerate", 115, 455);
			g.drawString("Press S to decelerate", 115, 490);
			g.drawString("Press A to turn left", 115, 525);
			g.drawString("Press D to turn right", 115, 560);
			g.drawString("Press M to open the map", 115, 595);
			g.drawString("- Golf ball's location", 690, 480);
			g.drawString("- Hole's location", 690, 530);
			g.drawString("- Player's direction", 690, 580);
			g.drawString("and location", 710, 615);
			g.drawImage(longMenuButton, 350, 665, null);
			g.setFont(buttonFont);
			g.setColor(new Color(0, 0, 0));
			g.drawString("Menu", 460, 745);
			g.drawImage(rightArrowButton, 1000, 693, null);
		} else if(gameState == 13) {
			// Render the second instructions screen
			g.drawImage(instructionsBackground2, 0, 0, null);
			g.setColor(new Color(255, 255, 255));
			g.setFont(mediumScoreFont);
			g.drawString("Key Shortuts", 85, 200);
			g.fillRect(85, 204, 180, 3);
			g.drawString("Press P to", 85, 241);
			g.drawString("pause the", 85, 276);
			g.drawString("game", 85, 311);
			g.drawString("Press ENTER to", 85, 381);
			g.drawString("quick play the", 85, 416);
			g.drawString("game when", 85, 451);
			g.drawString("entering name", 85, 486);
			
			g.drawString("- Fairway", 835, 260);
			g.drawString("- Rough", 835, 355);
			g.drawString("- Bunker", 835, 542);
			
			g.setFont(highScoreFont);
			g.drawString("- Extreme Rough", 835, 444);
			g.drawString("Fastest", 835, 290);
			g.setColor(new Color(252, 227, 5));
			g.drawString("Moderate Speed", 835, 385);
			g.setColor(new Color(252, 133, 5));
			g.drawString("Slower", 835, 474);
			g.setColor(new Color(252, 5, 5));
			g.drawString("Slowest", 835, 572);
			
			g.drawImage(longMenuButton, 350, 665, null);
			g.setFont(buttonFont);
			g.setColor(new Color(0, 0, 0));
			g.drawString("Menu", 460, 745);
			g.drawImage(leftArrowButton, 40, 693, null);
		} else if(gameState == 14) {
			// Render the credits screen
			g.drawImage(creditsBackground, 0, 0, null);
			
			g.setFont(mediumScoreFont);
			g.setColor(new Color(0, 0, 0));
			g.drawString("Author: Harry He", 115, 200);
			g.drawString("Many of the graphics are found online, but big thanks", 115, 280);
			g.drawString("to remove.bg for removing the backgrounds.", 115, 320);
			g.drawString("Thanks to my prime advisor and tester, Mr. Chow!", 115, 400);
			g.drawString("Thanks to my creative department, myself!", 115, 440);
			g.drawString("Thanks to my beta tester, Adrian!", 115, 480);
			g.drawString("I came up with the idea of this game when passing by", 115, 560);
			g.drawString("my local golf course.", 115, 600);
			
			g.drawImage(longMenuButton, 350, 665, null);
			g.setFont(buttonFont);
			g.setColor(new Color(0, 0, 0));
			g.drawString("Menu", 460, 745);
		}
	}

	// The the tick method is an update method that will change the
	// game stats every time the timer is is ready to
	// generate the next screen.
	public void tick() {
		// Local Variables
		// The angle double is used to store the calculated 
		// angle between the player and an obstacle, which will help know
		// which way to reflect the player or golf ball when they collide
		// with a obstacle
		double angle;
		// The ballSpeed double represents the speed of the ball, which is used
		// to calculate how fast the ball should go after collision.
		double ballSpeed;
		// Method Body
		if(gameState == 2 || gameState == 3) {
			// In game ticks, so here the player, golf ball
			// are ticked and detected for collisions
			// The absolute max speed of the player is 8 * its friction
			if (keyDown[0] && player.speed <= 8 * player.friction) {
				// W is pressed
				if(player.speed < 0) {
					// When accelerating back to zero, make the player accelerate
					// faster to allow for quick braking
					player.speed += 0.12 * player.friction;
				} else {
					player.speed += 0.06 * player.friction;
				}
			}
			if (keyDown[1] && player.speed >= -8 * player.friction) {
				// S is pressed
				if(player.speed > 0) {
					// When decelerating back to zero, make the player accelerate
					// faster to allow for quick braking
					player.speed -= 0.12 * player.friction;
				} else {
					player.speed -= 0.06 * player.friction;
				}
			}
			
			if (keyDown[2]) {
				// D is pressed, rotate the player right
				player.angle += 0.8;
				// Avoid negative angles
				if (player.angle >= 360) {
					player.angle -= 360;
				}
			}
			if (keyDown[3]) {
				// A is pressed, rotate the player left
				player.angle -= 0.8;
				// Avoid negative angles
				if (player.angle < 0) {
					player.angle += 360;
				}
			}
			
			// Check if the player has collided with obstacles, when the player
			// does collide with them, bounce the player back
			for (int i = 0; i < mapGenerator.obstacles.length; i++) {
				if (player.collision(mapGenerator.obstacles[i]) && player.collisionDelay == 0) {
					// Flip the angle to face the opposite direction of the obstacle
					angle = getAngle(player.x, player.y, 
							mapGenerator.obstacles[i].x, mapGenerator.obstacles[i].y) - 180;
					player.collideVelX = Math.cos(Math.toRadians(angle)) * Math.abs(player.speed);
					player.collideVelY = Math.sin(Math.toRadians(angle)) * Math.abs(player.speed);
					player.speed *= 0.25;

					player.startDelay();
					break;
				}
			}
			
			// Check if the player has collided with the ball, when the player does
			// collide with the ball, bounce the ball forwards, according to the
			// player's direction
			if (!ball.inHole && player.collision(ball) && player.collisionDelay == 0 
					&& ball.collisionDelay == 0) {
				ball.velX += player.velX * (1.8 * ball.friction);
				ball.velY += player.velY * (1.8 * ball.friction);
				hits++;

				player.startDelay();
				ball.startDelay();
			}

			// Check if the ball has collided with any obstacles, if it has, then
			// bounce the ball back. Collision delay is also accounted for to allow
			// time for the ball to actually bounce away from the obstacle.
			for (int i = 0; i < mapGenerator.obstacles.length; i++) {
				if (!ball.inHole && ball.collision(mapGenerator.obstacles[i]) && ball.collisionDelay == 0) {
					// Flip the angle to face the opposite direction of the obstacle
					angle = getAngle(ball.x, ball.y, mapGenerator.obstacles[i].x, mapGenerator.obstacles[i].y) - 180;
					ballSpeed = Math.sqrt(ball.velX * ball.velX + ball.velY * ball.velY);
					ball.velX = Math.cos(Math.toRadians(angle)) * Math.abs(ballSpeed * 0.8);
					ball.velY = Math.sin(Math.toRadians(angle)) * Math.abs(ballSpeed * 0.8);
					ball.startDelay();
					break;
				}
			}
			// If the player or the golf ball is out of bounds, reflect them back
			// with a bit of a buffer before the bounds, to avoid index out of bounds
			// Note that 2000 * magnificationScale is equal to the dimensions of 
			// the map
			if((player.x < 100 || player.x >= 2000 * magnificationScale - 100 || 
					player.y < 100 || player.y >= 2000 * magnificationScale - 100) && player.collisionDelay == 0) {
				angle = player.angle + 90;
				player.collideVelX = Math.cos(Math.toRadians(angle)) * Math.abs(player.speed);
				player.collideVelY = Math.sin(Math.toRadians(angle)) * Math.abs(player.speed);
				player.speed *= 0.25;
				player.startDelay();
			}
			
			// If the ball is out of bounds, reflect the ball back
			if(ball.x < 100 || ball.x >= 2000 * magnificationScale - 100){
				ball.velX *= -0.8;
			}
			if(ball.y < 100 || ball.y >= 2000 * magnificationScale - 100) {
				ball.velY *= -0.8;
			}

			// Move the player and the ball, as well as make sure that the
			// pivot stays on the player.
			// The y and x are inverted as the values array is based on
			// rows first then columns, so y then x.
			player.tick(mapGenerator.values
					[(int)(player.y / magnificationScale)]
							[(int)(player.x / magnificationScale)]);
			pivotX = player.x;
			pivotY = player.y;
			ball.tick(mapGenerator.values
					[(int)(ball.y / magnificationScale)]
							[(int)(ball.x / magnificationScale)]);
			
			// Check if the ball has entered the hole
			// This is based upon the formula for a circle, where we set an
			// inequality to check if the ball lies within the area
			if(!ball.inHole && (ball.x - mapGenerator.holeX) * (ball.x - mapGenerator.holeX) + 
					(ball.y - mapGenerator.holeY) * (ball.y - mapGenerator.holeY) <= (holeDiameter) * (holeDiameter) / 4) {
				angle = getAngle(ball.x, ball.y, mapGenerator.holeX, mapGenerator.holeY);
				ballSpeed = Math.sqrt(ball.velX * ball.velX + ball.velY * ball.velY);
				ball.velX = Math.cos(Math.toRadians(angle)) * Math.abs(ballSpeed * 1.4);
				ball.velY = Math.sin(Math.toRadians(angle)) * Math.abs(ballSpeed * 1.4);
				ball.inHole = true;
				timeTaken = System.currentTimeMillis() - startTime;
			} else if(ball.inHole) {
				// When the ball is in the hole, we now pivot on the ball
				// and continue to move it but with a higher
				// velocity fall off to avoid vibration of screen.
				pivotX = ball.x;
				pivotY = ball.y;
				if(winScreenDelay > 0) {
					winScreenDelay--;
					angle = getAngle(ball.x, ball.y, mapGenerator.holeX, mapGenerator.holeY);
					ballSpeed = Math.sqrt(ball.velX * ball.velX + ball.velY * ball.velY);
					ball.velX = Math.cos(Math.toRadians(angle)) * Math.abs(ballSpeed);
					ball.velY = Math.sin(Math.toRadians(angle)) * Math.abs(ballSpeed);
					ball.velX *= 0.93;
					ball.velY *= 0.93;
				} else {
					// Although the length of the screen is 810,
					// the increased value will give an extra buffer for the player
					// to expect the change.
					if(castRectLength < 880) {
						castRectLength += 7;
					} else {
						// Update the high scores with the new score, refer to
						// the updateScore method for more information on how
						// this works
						updateScore(mapGenerator.par, new Score(playerName, timeTaken, mapGenerator.mapSeed));
						try {
							printScores();
						} catch (IOException e) {
							// An error is thrown, so the scores could not be printed
							System.out.println("Error 407: Cannot Print Scores");
							e.printStackTrace();
						}
						// Move the game to the score screen to give the player
						// feed back on their score
						gameState = 5;
					}
				}
			}
		} else if(gameState == 11) {
			// Loading screen
			// The mapGenerator will generate a new Random map
			if(inputSeed.length() == 0) {
				mapGenerator.generateMap(new Random().nextLong(), magnificationScale, playerName);
			} else {
				mapGenerator.generateMap(hashSeed(inputSeed), magnificationScale, playerName);
			}
			
			// Load in the image generated, at a scale of the magnficationScale, which is set
			// up at the top with the class variables, which allows the scale to be changed easier.
			// This will result in a larger image to help make a better scale for the game
			try {
				// Read in the map image file.
				miniMapSource = ImageIO.read(new File("map.jpeg"));
				// Create a promptMap scaled down from the original map.jpeg hardcoded at
				// 780 by 780 which allows the image to fit in the screen.
				promptMap = ImageUtilities.toBufferedImage(miniMapSource.getScaledInstance(780, 780, java.awt.Image.SCALE_SMOOTH));
				// Scale up the mapBackground from the original map.jpeg
				mapBackground = ImageUtilities.toBufferedImage(miniMapSource.getScaledInstance(
						(int)(miniMapSource.getWidth() * magnificationScale), 
						(int)(miniMapSource.getHeight() * magnificationScale), java.awt.Image.SCALE_SMOOTH));
				// Declare in the miniMap to avoid a null pointer exception.
				// If the player spawns on the exterior of the map, which may
				// cause the miniMap image not to be created due to the avoidance of
				// index out of bounds.
				miniMap = miniMapSource.getSubimage(0, 0, 300, 300);
			} catch (IOException e) {
				System.out.println("Error 406: Map Import Error");
				e.printStackTrace();
			}
			// Reset the game stats
			hits = 0;
			ball.inHole = false;
			startTime = System.currentTimeMillis();
			player.speed = 0;
			// Put the player facing the direction of the fairway behind the ball
			// for convenience of the player
			player.x = mapGenerator.trueStartX * magnificationScale - 200 * Math.cos(Math.toRadians(mapGenerator.firstAngle));
			player.y = mapGenerator.trueStartY * magnificationScale - 200 * Math.sin(Math.toRadians(mapGenerator.firstAngle));
			// Put the pivot onto the player
			pivotX = player.x;
			pivotY = player.y;
			// The player's angle is set to face the ball, it is added by 90
			// because the player's 0 is normally at vertical so we must convert
			// from conventional angle to the player's angle.
			player.angle = mapGenerator.firstAngle + 90;
			ball.x = mapGenerator.trueStartX * magnificationScale;
			ball.y = mapGenerator.trueStartY * magnificationScale;
			ball.velX = 0;
			ball.velY = 0;
			winScreenDelay = 180;
			castRectLength = 0;
			// Reset the key's down, so that the player will not move upon spawn
			for(int i = 0; i < keyDown.length; i++) {
				keyDown[i] = false;
			}
			
			// Move the player into the game, once all the loading
			// and resetting has been done
			gameState = 2;
		}
		
	}

	// Returns the angle between two points where the point at x1, y1 will
	// be the origin and x2, y2 will determine the angle.
	public double getAngle(double x1, double y1, double x2, double y2) {
		return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
	}

	// Threading method, allows the game to be updated every
	// 15 milliseconds, to allow for about 60 frames per second,
	// as time taken to render must be accounted for.
	public void run() {
		while (true) {
			repaint();
			try {
				tick();
				Thread.sleep(15);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	// The keyPressed method is a method that will be activated whenever
	// a key on the keyboard is pressed. This method is predominantly used when
	// the player is entering their name on the transition screen before the game,
	// when the player is actually playing the game, and when the player is on the
	// pause screen to then perform some action.
	// Refer the the inner contents of the method for more information on what each
	// key press does.
	public void keyPressed(KeyEvent e) {
		// Local Variables
		// The key integer is the key code of the key that
		// was pressed. It is used to check which key the user pressed.
		int key = e.getKeyCode();

		// Method Body
		if (gameState == 1) {
			// Game is in the transition phase before the game
			// and player must enter their name
			if(nameBoxSelected) {
				if (key == KeyEvent.VK_ENTER) {
					// A hot key, enter, is pressed. If the player has entered
					// a name, allow the player to quickly move to the game.
					// Set up all of the game states, like the fact that the ball is
					// in the hole and the starting time.
					if(playerName.length() > 0) {
						gameState = 11;
					} else {
						errorMessage = "Enter a name";
					}
					
				} else if (key == KeyEvent.VK_BACK_SPACE) {
					// Backspace is pressed. If the name is longer than 0
					// delete the last character from the name.
					if (playerName.length() > 0) {
						playerName = playerName.substring(0, playerName.length() - 1);
					}
				} else {
					if (playerName.length() >= 16) {
						// Player has tried to enter more characters but the name
						// limit is exceeded, so the player cannot enter more and an error
						// message will be displayed
						// The limit is hard coded at 16 characters, the character limit
						// in Minecraft
						errorMessage = "Names cannot be more than 16 characters";
					} else if (e.getKeyChar() != '￿' && key != KeyEvent.VK_ESCAPE) {
						// The player still has more characters they can enter and has
						// entered a valid key, so the name adds the character they pressed
						playerName += e.getKeyChar();
					}
				}
			} else if(seedBoxSelected) {
				if(key == KeyEvent.VK_ENTER) {
					// A hot key, enter, is pressed. If the player has entered
					// a name, allow the player to quickly move to the game.
					// Set up all of the game states, like the fact that the ball is
					// in the hole and the starting time.
					if(playerName.length() > 0) {
						gameState = 11;
					} else {
						errorMessage = "Enter a name";
					}
				} else if (key == KeyEvent.VK_BACK_SPACE) {
					// Backspace is pressed. If the name is longer than 0
					// delete the last character from the name.
					if (inputSeed.length() > 0) {
						inputSeed = inputSeed.substring(0, inputSeed.length() - 1);
					}
				} else {
					if (e.getKeyChar() != '￿' && key != KeyEvent.VK_ESCAPE && inputSeed.length() < 20 && key != KeyEvent.VK_ENTER) {
						// The player still has more characters they can enter and has
						// entered a valid key, so the name adds the character they pressed
						// Maximum input seed length is 20
						inputSeed += e.getKeyChar();
					}
				}
			}
			
		} else if (gameState == 2 || gameState == 3) {
			// Player is currently in the game
			
			// When the user presses a key in the game
			// the keyDown array is updated for primary controls
			// This prevents the delay before the key is constantly
			// rendered as being pressed, to allow for smoother movement.
			// These keyDown values are later used in the tick method to update
			// the player
			if (key == KeyEvent.VK_W) {
				keyDown[0] = true;
			} else if (key == KeyEvent.VK_S) {
				keyDown[1] = true;
			} else if (key == KeyEvent.VK_D) {
				keyDown[2] = true;
			} else if (key == KeyEvent.VK_A) {
				keyDown[3] = true;
			} else if (key == KeyEvent.VK_P) {
				// P is pressed to pause the game, this immediately
				// will switch the game to be paused, to prevent further
				// updates to the game
				gameState = 4;
			} else if(key == KeyEvent.VK_M) {
				// M is pressed to open the map in the game
				// this will still allow the player to move, but the map
				// will be open. If M is pressed when the map is open
				// the map will be closed.
				if(gameState == 2) {
					gameState = 3;
				} else {
					gameState = 2;
				}
				
			}
		} else if (gameState == 4) {
			// Player is on the pause screen
			if (key == KeyEvent.VK_P) {
				// P can be pressed to stop the pausing of the game
				// and return back to the normal game.
				gameState = 2;
			}
		}
	}

	// The keyRelased method is a method that is activated whenever
	// a key is released. This is only used to stop primary controls
	// in the game, and update the keyDown method to prevent the corresponding
	// action from acting upon on the player.
	public void keyReleased(KeyEvent e) {
		// Local Variables
		// The key integer is the key code of the key that
		// was released. It is used to check which key the user released.
		int key = e.getKeyCode();
		// Method Body
		if (gameState == 2 || gameState == 3) {
			// Only in the game are the keyDown controls updated
			// as the only purpose of the keyDown array is to
			// have smoother movement in the game
			
			// The corresponding position of the key in the keyDown
			// array is set to false as the key is no longer being
			// pressed
			if (key == KeyEvent.VK_W) {
				keyDown[0] = false;
			} else if (key == KeyEvent.VK_S) {
				keyDown[1] = false;
			} else if (key == KeyEvent.VK_D) {
				keyDown[2] = false;
			} else if (key == KeyEvent.VK_A) {
				keyDown[3] = false;
			}
		}
	}

	// The mousePressed method will activate whenever the user presses the mouse.
	// This method will be predominantly used to move the user throughout the game
	// states, like through the menu, the high score pages, and the pause screen.
	public void mousePressed(MouseEvent e) {
		// X and Y position of the mouse press is recorded
		mouseX = e.getX();
		mouseY = e.getY();
		if (gameState == 0) {
			// Game is in the main menu state
			if (mouseX >= 120 && mouseX <= 520 && mouseY >= 440 && mouseY <= 560) {
				// Play button is pressed, player is moved to the transition screen
				// Reset the error message as a courtesy, as the user probably
				// knows what the error is if they have gone back to the menu
				gameState = 1;
				errorMessage = "";
			} else if (mouseX >= 560 && mouseX <= 960 && mouseY >= 440 && mouseY <= 560) {
				// High score button is pressed, player is moved to the highscore page
				gameState = 6;
			} else if (mouseX >= 120 && mouseX <= 520 && mouseY >= 600 && mouseY <= 720) {
				// User presses the instructions button
				gameState = 12;
			} else if(mouseX >= 560 && mouseX <= 960 && mouseY >= 600 && mouseY <= 720) {
				// User presses the credits button
				gameState = 14;
			}
		} else if (gameState == 1) {
			// Game is in the transition page
			if (mouseX >= 80 && mouseX <= 1000 && mouseY >= 300 && mouseY <= 400) {
				// Enter name box is selected, so the user can now enter their name
				nameBoxSelected = true;
				seedBoxSelected = false;
			} else if (mouseX >= 80 && mouseX <= 1000 && mouseY >= 460 && mouseY <= 560) {
				// Enter seed box is selected, so the user can now enter the seed they want
				seedBoxSelected = true;
				nameBoxSelected = false;
			} else {
				// Deselect the name box, so that the user cannot enter again and
				// unknowingly enter more characters in their name
				nameBoxSelected = false;
				seedBoxSelected = false;

				if (mouseX >= 70 && mouseX <= 470 && mouseY >= 650 && mouseY <= 750) {
					// Back to main menu button is pressed, return the the main menu
					gameState = 0;
				} else if (mouseX >= 610 && mouseX <= 1010 && mouseY >= 650 && mouseY <= 750) {
					// Play button is pressed
					if (playerName.length() > 0) {
						// If the user has entered a name, then move them to the game
						// Set up all of the game states, like the fact that the ball is
						// in the hole and the starting time.
						gameState = 11;
					} else {
						// If they have not entered a name, display an error message
						errorMessage = "Enter a name";
					}
				}
			}
		} else if (gameState == 4) {
			// Game is in the pause state
			if (mouseX >= 340 && mouseX <= 740 && mouseY >= 310 && mouseY <= 430) {
				// If resume button is pressed, move the player back to the game
				gameState = 2;
			} else if (mouseX >= 340 && mouseY <= 740 && mouseY >= 460 && mouseY <= 580) {
				// If the back to menu button is pressed, move the player back to the
				// main menu
				gameState = 0;
			}
		} else if (gameState == 5) {
			// Game is in the ending screen after the hole has been completed
			if(mouseX >= 100 && mouseX <= 500 && mouseY >= 640 && mouseY <= 760) {
				// The menu button has been pressed, so move the player back to the
				// menu
				gameState = 0;
			} else if(mouseX >= 600 && mouseX <= 1000 && mouseY >= 640 && mouseY <= 760) {
				// The high scores button has been pressed so move the player back to the
				// high scores page
				gameState = 6;
			}
		} else if (gameState <= 10) {
			// Game is in the high score state
			if (mouseX >= 340 && mouseX <= 740 && mouseY >= 675 && mouseY <= 795) {
				// Menu button is pressed, so move the player back to the menu
				gameState = 0;
			} else {
				if (gameState > 6) {
					// When the high score page is past the first one, allow the user
					// to move back to the previous page, for a lower par.
					if (mouseX >= 90 && mouseX <= 144 && mouseY >= 703 && mouseY <= 767) {
						gameState--;
					}
				}
				if (gameState < 10) {
					// When the high score page is before the last one, allow the user
					// to move forwards to the next page, for a higher par.
					if (mouseX >= 1000 && mouseX <= 1054 && mouseY >= 703 && mouseY <= 767) {
						gameState++;
					}
				}
			}
		} else if(gameState == 12 || gameState == 13 || gameState == 14) {
			// Put the menu button in the center bottom of instructions and credit page
			if(mouseX >= 350 && mouseX <= 750 && mouseY >= 665 && mouseY <= 785) {
				// Menu button is pressed
				gameState = 0;
			}
			if(gameState == 12) {
				if (mouseX >= 1000 && mouseX <= 1054 && mouseY >= 693 && mouseY <= 757) {
					gameState = 13;
				}
			} else if(gameState == 13) {
				if (mouseX >= 50 && mouseX <= 104 && mouseY >= 693 && mouseY <= 757) {
					gameState = 12;
				}
			}
		}
	}

	// The loadFont method will return a font of the given
	// font at the resource path and the font size. This method
	// can throw IOExceptions, which will not terminate the entire
	// program but will result in a default font being loaded.
	public Font loadFont(String path, int fontSize) {
		// Local Variables
		// Load a font just in case, so that the return will
		// not be null
		Font f = new Font("arial", 1, 30);
		
		// Method Body
		try {
			// Create the font at the given path, and use the regular plain style
			// of the font, as the fonts loaded into the game will be primitive
			// having only one style.
			f = Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(Font.PLAIN, fontSize);
		} catch (Exception e) {
			// Error is thrown, and output the error location
			e.printStackTrace();
		}
		return f;
	}

	// The readScores method will be used to load in the high scores
	// from the highscore.txt file into the RAM space as apart of the 
	// arraylist of arraylist variable, highScores. This method can throw
	// an IOException which will not terminate the game, but will result in 
	// a blank high score page and highScores arraylist, which will destroy
	// all existing high scores, but this should never happen unless the text file
	// is altered by the user.
	
	// Note that there is a specific format of the highscore.txt
	// Each par of high scores are separated with a blank line, 
	// the high scores are in ascending order of time, each group
	// of high scores is in ascending order of par, and there are only
	// 3 high scores per group.
	public void readScores() throws IOException {
		// Local Variables
		// Scanner is declared to read in the highscore.txt
		Scanner inputFile = new Scanner(new File("highscore.txt"));
		// The par variable is the index of the par in the highScores variable
		int par = 0;
		// The inputLine variable is the String input of each line in the text file
		String inputLine;
		// The inputs String array is an array of the different components of the input line
		// at index 0, is the name, index 1 is the millisecond time, and index 2 is the
		// seed.
		String[] inputs;

		// Method Body
		while (par < 5 && inputFile.hasNextLine()) {
			// Read in the input line
			inputLine = inputFile.nextLine();
			if (inputLine.length() == 0) {
				// Blank lines will indicate a new group
				par++;
			} else {
				// Scores are processed using the score class.
				// Refer to the Score class file for more information on
				// how scores are processed from a single String.
				inputs = Score.processScore(inputLine);
				// Scores are stored into the highScore variable.
				highScores.get(par).add(new Score(inputs[0], Long.parseLong(inputs[1]), Long.parseLong(inputs[2])));
			}
		}
		inputFile.close();
	}
	
	// The updateScore method will take in a integer par and the Score value
	// that the player got on a certain map, with the par, and insert it into the
	// highScores arraylist of high scores. If the arraylist of high scores at a
	// certain par is greater than 3 as a result of adding one more high score
	// remove the last element, which has the worst score.
	public void updateScore(int par, Score score) {
		// Local Variables
		// The index integer is the index where the score should be added.
		// Initially it is set at the end of the par arraylist, which means
		// that it is not a high score.
		int index = highScores.get(par - 1).size();
		// Method Body
		// Find where the score should be put in respect to the other high scores
		for(int i = 0; i < highScores.get(par - 1).size(); i++) {
			if(highScores.get(par - 1).get(i).milliTime > score.milliTime) {
				index = i;
				break;
			}
		}
		highScores.get(par - 1).add(index, score);
		// When the highScores arraylist at the certain par
		// is greater than 3, remove the last element, to keep the
		// high scores array with only 3 elements, 3 highest scores.
		if(highScores.get(par - 1).size() == 4) {
			highScores.get(par - 1).remove(3);
		}
	}
	
	// The printScores method will print out the high scores saved in the RAM
	// as apart of the arraylist highScores into the highscore.txt text file
	// this will then allow the game to save the high scores gotten.
	public void printScores() throws IOException {
		// Local Variables
		// The outputFile is the highscore.txt which stores the highscores.
		PrintWriter outputFile = new PrintWriter(new FileWriter("highscore.txt"));
		// Method Body
		for(int i = 0; i < highScores.size(); i++) {
			for(int j = 0; j < highScores.get(i).size(); j++) {
				outputFile.printf("%s %d %d%n", highScores.get(i).get(j).name, 
						highScores.get(i).get(j).milliTime, highScores.get(i).get(j).seed);
			}
			outputFile.println();
		}
		outputFile.close();
	}
	
	// The hashSeed method will take in a String seed.
	// If the seed is a number, then the hash will just
	// be the number, otherwise then the seed will actually
	// be hashed, using polynomial hashing with a base of
	// 131 since there are 128 valid ascii values that can be entered in the game.
	public long hashSeed(String seed) {
		// Local Variables
		// The base is the number that will be raised to the power for the hash
		long base = 131;
		// The output long is the number that is the hash of the String
		// which will be returned
		long output = 0;
		// Method Body
		if(isNumber(seed)) {
			// It is possible that some of the longs will be too
			// large as Long.MAX_VALUE is 19 digits, in that case, just cap
			// it off
			if(seed.length() == 20) {
				if(seed.charAt(0) == '-') {
					if(seed.compareTo(Long.toString(Long.MIN_VALUE)) > 0){
						return Long.MIN_VALUE;
					} else {
						return Long.parseLong(seed);
					}
				} else {
					return Long.MAX_VALUE;
				}
			} else if(seed.length() == 19) {
				if(seed.charAt(0) == '-') {
					return Long.parseLong(seed);
				} else {
					if(seed.compareTo(Long.toString(Long.MAX_VALUE)) > 0){
						return Long.MAX_VALUE;
					} else {
						return Long.parseLong(seed);
					}
				}
			}
			return Long.parseLong(seed);
		} else {
			// We use a rolling hash to hash the seed to make it faster
			// we take the pre-existing values and multiply it by the base
			// to then raise all of their powers.
			// Ex. The hash of a string of length 3 will be
			// char1 x 131^2 + char2 x 131^1 + char3
			for(int i = 0; i < seed.length(); i++) {
				output = seed.charAt(i) + output * base;
			}
		}
		return output;
	}
	
	// The isNumber method will take in a String and
	// check if the String can be parsed into a number, positive or negative.
	// The method returns true if the String is a number, false otherwise.
	public boolean isNumber(String str) {
		if(str.charAt(0) == '-') {
			if(str.length() == 1) {
				return false;
			} else {
				for(int i = 1; i < str.length(); i++) {
					if(!Character.isDigit(str.charAt(i))) {
						return false;
					}
				}
				return true;
			}
		} else {
			for(int i = 0; i < str.length(); i++) {
				if(!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
			return true;
		}
	}

	public static void main(String[] args) {
		// Global Variables
		JFrame frame = new JFrame("Golf Cart Golf");
		Main panel = new Main();
		
		// Run Code
		// Game is put inside the frame and shown to the player
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	// Unused Methods
	// These methods are necessary for the class to implement
	// KeyListener and MouseListener, but these methods are unused
	// and will not do anything.
	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

}
