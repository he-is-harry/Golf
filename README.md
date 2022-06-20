# Golf Cart Golf

Author: Harry He

## Project Description

	The application will be able to create a 2D game that uses keyboard and mouse input.
	The game will be about hitting a ball into a hole on a map, similar to golf.
	All of the technologies used in the game are apart of the Java Standard Library, and
	there are no external libraries that were used in the creation of the game. In the
	game some of the features of the Java Standard Library used were graphics, to make 
	the game, text file streaming, to keep high scores and array to image conversion, 
	to make the image of the randomly generated maps. The challenges faced when creating
	the game were using trigonometry, random map generation, and image rotation.
	
## Installation and Running

	1. Open the zip file of the project, inside there should be a folder called 
	"HarryHe_ISU". Move the folder outside of the zip package.
	Note: If you see a folder called "_MACOSX" you can ignore this.

	Since the game is a java application, at least a JRE, Java Runtime Environment, is
	required to run the game. However, I would recommend that the JDK, Java Development
	Kit, should be downloaded for ease of use.
	
	If you do not have a JDK, follow these steps
	2. Go to https://www.oracle.com/java/technologies/downloads/ and download
	the version of the JDK that matches your operating system and your device specifics.
	The installer may be the easiest way to down load the JDK.
	
	3. Once downloaded, open the installer and follow the instructions to install the
	JDK.

	From here you have two options of running the java application
	- Using the command line (line 35)
	- Using an IDE (line 134)

### Using the command line to run the application
	4. To correctly format the java project for command line use, open the project folder
	which should be named "HarryHe_ISU", and move the folder "res" into the "src" folder.
	Then, you must move the files, "highscore.txt", "map.jpeg", and "Lines.jpeg" into the
	"src" folder.
	
	5. Find the directory in which the "HarryHe_ISU" folder is found, should
	look similar to "C:\Users\username\Desktop" but will be different due to a different
	user name and download location. This path should not include the "HarryHe_ISU" folder,
	just the part before.
	
	6. Open the command prompt, this can be done by searching "cmd" in the start window, 
	or searching "terminal" on mac.
	
	7. Change your directory to the path found before.
		
		cd C:\Users\username\Desktop
	
	8. Change your directory to the src folder found within the "HarryHe_ISU" folder
		
		cd HarryHe_ISU\src
	
	9. Compile the Main.java file to allow the game to be run
	
		javac Main.java
		
	10. Run the Main class file to run the project
		
		java Main

### Troubleshooting

	If you see "javac is not recognized as an internal or external command", here are
	some steps to solve your issue
	
#### Windows

	1. Locate where the JDK is installed, for guidance it should look something like
	"C:\Program Files\Java\jdk-18.0.1.1".
	
	2. Then add upon the sub folder bin to the path, this is where the javac application
	is held. It should look like "C:\Program Files\Java\jdk-18.0.1.1\bin".
	
	3. In the start window search "environment variable" and click the "Edit the
	system environment variables" option.
	
	4. Click on the environment variables button. Then you will find a list
	of your user and system variables.
	
	5. If you do not have a "Path" variable under your user, you can add a new variable
	named "PATH" with the path of the JDK's bin, i.e. "C:\Program Files\Java\jdk-18.0.1.1\bin".
	
	6. If you do have a "Path" variable, you should edit the "Path" variable
	by adding a semicolon to the end of the pre-existing paths, then add the
	path of the JDK's bin. For instance the change would look like,
	
	"C:\Users\jack\AppData\Local\Programs\Python\Python39\Scripts\
	 C:\Users\jack\AppData\Local\Programs\Python\Python39\
	 %USERPROFILE%\AppData\Local\Microsoft\WindowsApps"
	 
	 to
	 
	"C:\Users\jack\AppData\Local\Programs\Python\Python39\Scripts\
	 C:\Users\jack\AppData\Local\Programs\Python\Python39\
	 %USERPROFILE%\AppData\Local\Microsoft\WindowsApps; C:\Program Files\Java\jdk-18.0.1.1\bin"
	 
	7. Save your changes by pressing "OK".

#### Mac

	1. Locate where the JDK is installed, for guidance it should look something like 
	"/Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home"
	
	2. Add upon the subfolder bin to the path, which is where the javac application
	is held. It should look like 
	"/Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home/bin".
	
	3. Open the terminal, by searching "terminal" in the search bar.
	
	4. Open the paths file, by running the command 
		
		sudo nano /etc/paths
		
	5. Go to the bottom of the path, then add on the path of the JDK bin.
	  	
	  	UW PICO 5.09                        File: /etc/paths                          

		/usr/local/bin
		/usr/bin
		/bin
		/usr/sbin
		/sbin
		/Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home/bin
		
	6. Press Control-X to save quit the file editor, and press "Y" to save the new
	path file.
	
	7. To test, you can refresh the terminal and run the command below to see the new
	edited paths.
	
		echo $PATH
		

### Using an IDE, Eclipse

	If you do not have Eclipse or another IDE that can run Java, follow steps 4 - 5,
	otherwise you can skip to step 6.

	4. Download the Eclipse installer from https://www.eclipse.org/downloads/
	
	5. Run the Eclipse installer and follow the instructions to install the Eclipse
	application.
	Note: Select Eclipse for Java Developers since this project is a java application.
	
	6. Open the Eclipse application.
	
	7. Create a new Java Project and enter a name for the project.
	
	8. Right click the project and press import.
	
	9. Select the General option, and then the File System.
	
	10. Select the "HarryHe_ISU folder" to import from, which will be in the directory
	in which you copied the folder to. Check off the HarryHe_ISU box to import all 
	content within.
	Note: You may need to overwrite the existing default settings, just click yes
	to allow the entire java application to copy over.
	
	11. From there run Main.java as a java application or you can select the entire
	project and run it as a java application.
	
## Using the Project

	The project will accept mouse and keyboard input. To play the game, refer to the
	instructions screen to learn about keyboard controls and the goal of the game.
	To control the application use the mouse and cursor as input, pressing the
	buttons to navigate the game.
	
## Credits

	Author: Harry He
	Advisor and Tester: Mr. Chow
	Beta Tester: Adrian
	
	Thanks to The Coding Train for providing guidance on how to create metaballs
	and their helpful blending for creating the map. The tutorial followed was
	https://www.youtube.com/watch?v=ccYLb7cLB1I&ab_channel=TheCodingTrain
	
	Thanks to the testers for this project who helps provide insight into how the game
	should be improved and helped remove bugs.
	
	Finally, thanks to the creative team for the idea of this game, Harry He, who came
	up with this idea when passing by his local golf course and wondered if he could
	play golf with a golf cart.
	
## License

MIT License

Copyright (c) 2022 Harry He

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


This license however, does not apply to the resources associated with the game
(images, font), many of which are copyright and cannot be distributed or used
for commercial use. The resources in the game are under exclusive copyright, so
one cannot copy, distribute, or modify the work without being at risk of take-downs, 
shake-downs, or litigation. These resources are only in use because the project
is of an educational purpose, not being published into the public nor for commercial
gain.
	
