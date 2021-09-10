# Harry He - January 5 2020
# This python file houses the menu class and its helper classes,
# go to the Main.py file to see the primary code and to run the
# game.
# This class is the menu screen and all of the subsequent pages.
# This class allows the user customize their game, read about
# how to play, read about some of the things that went into making
# the game, and change their controls.

# Importing some of the assets needed to run the class
import pygame
import os
from pygame.compat import geterror

# This variable stores the path in which the game is currently
# in. This is used to load images later.
main_dir = os.path.split(os.path.abspath(__file__))[0]

# Helper function to load images by filename and to make
# certain colors transparent. Helps with making backgrounds
# transparent
def load_image(name, colorkey=None):
    fullname = os.path.join(main_dir, name)
    try:
        image = pygame.image.load(fullname)
    except pygame.error:
        print("Cannot load image:", fullname)
        raise SystemExit(str(geterror()))
    image = image.convert()
    if colorkey is not None:
        if colorkey == -1:
            colorkey = image.get_at((0, 0))
        image.set_colorkey(colorkey, pygame.RLEACCEL)
    return image, image.get_rect()

# The Menu Class
# This class is essentially the menu of the game. This class allows
# the user to customize their game, read information, and change controls.
# Additionally, if the user pauses the game, the menu class will be used
# to see if the user wants to go back to the main menu, change controls
# or resume.
# Note: This class doesn't really house any of the menu's functionality
# you can look at the Main.py file or the main() function at the bottom
#  of this file to see the functionality. However, the main() function
#  doesn't have all the functionality and was primarily used for testing.

# The init function defines the many variables used in the display of the
# menu. There are also many buttons defined here which are used to select
# options in the menu, including game customization. If you want to see
# how the different buttons change the game or controls, go to the Main.py
# file.

# The render function will render the current page which the menu is
# on. Depending on the current page, there will be different buttons
# and text rendered on the screen.
class Menu():
    def __init__(self):
        self.curPage = 6
        self.prevPages = []
        self.prevPages.append(0)
        # Main Menu
        # The main menu holds the buttons to access
        # the different pages within the menu. It allows the user
        # to go to the play, how to play, controls, and about page.
        # It also allows the user to quit the game.

        self.mainBackground, self.backRect = load_image("WiiBackground.bmp")
        self.menuMessageFont = pygame.font.Font("contm.ttf", 30)
        self.menuMessage = self.menuMessageFont.render("Main Menu", True, (255, 255, 255))
        self.menuMessageRect = self.menuMessage.get_rect(center=(85, 72))

        self.menuCaptionFont = pygame.font.Font("contb.ttf", 40)
        self.menuCaptionLeft = self.menuCaptionFont.render("Wii", True, (102, 102, 102))
        self.menuCaptionLeftRect = self.menuCaptionLeft.get_rect(center=(646, 72))
        self.menuCaptionRight = self.menuCaptionFont.render("Tennis", True, (100, 168, 199))
        self.menuCaptionRightRect = self.menuCaptionLeft.get_rect(center=(700, 72))

        self.smallWiiFont = pygame.font.Font("contm.ttf", 25)
        self.tinyWiiFont = pygame.font.Font("contm.ttf", 20)
        self.mainButtons = []
        self.mainButtons.append(Button((160, 200), 300, 120, self.smallWiiFont, "Play"))
        self.mainButtons.append(Button((160, 325), 300, 120, self.smallWiiFont, "How To Play"))
        self.mainButtons.append(Button((160, 450), 300, 120, self.smallWiiFont, "Controls"))
        self.mainButtons.append(Button((160, 575), 300, 120, self.smallWiiFont, "About"))
        self.mainButtons.append(Button((160, 700), 300, 120, self.smallWiiFont, "Quit"))

        # A cool thing in the main menu is the slideshow that is defined
        # here. According to whatever button the user hovered over last will
        # be the slide shown in the display.
        self.curSlide = 0
        self.slideshow = []
        self.slideshow.append(Slide((550, 300), "Slide1.bmp"))
        self.slideshow.append(Slide((550, 300), "Slide2.bmp"))
        self.slideshow.append(Slide((550, 300), "Slide3.bmp"))
        self.slideshow.append(Slide((550, 300), "Slide4.bmp"))
        self.slideshow.append(Slide((550, 300), "Slide5.bmp"))

        # Play Page
        # The play page holds the buttons to customize the user's
        # game. It allows the user to play the game when ever they
        # press the play button.
        self.serveSelected = False
        self.difficultySelected = False
        self.roundsSelected = False
        self.plainBackground = pygame.Surface(pygame.display.get_window_size())
        self.plainBackground.fill((76, 164, 201))
        self.playTopCaption = self.menuCaptionFont.render("Play", True, (255, 255, 255))
        self.playTopCaptionRect = self.playTopCaption.get_rect(center=(400, 20))
        self.serveCaption = self.menuMessageFont.render("Server", True, (255, 255, 255))
        self.serveCaptionRect = self.serveCaption.get_rect(center=(400, 60))
        self.difficultyCaption = self.menuMessageFont.render("Difficulty", True, (255, 255, 255))
        self.difficultyCaptionRect = self.difficultyCaption.get_rect(center=(400, 600))
        self.roundsCaption = self.menuMessageFont.render("Rounds", True, (255, 255, 255))
        self.roundsCaptionRect = self.roundsCaption.get_rect(center=(400, 750))

        self.playButtons = []
        self.playButtons.append(Button((315, 140), 120, 75, self.smallWiiFont, "Computer"))
        self.playButtons.append(Button((485, 510), 120, 75, self.smallWiiFont, "Player"))
        self.playButtons.append(Button((100, 670), 180, 100, self.smallWiiFont, "Basic"))
        self.playButtons.append(Button((400, 670), 180, 100, self.smallWiiFont, "Moderate"))
        self.playButtons.append(Button((700, 670), 180, 100, self.smallWiiFont, "Advanced"))
        self.playButtons.append(Button((100, 825), 180, 100, self.smallWiiFont, "Best of 1"))
        self.playButtons.append(Button((400, 825), 180, 100, self.smallWiiFont, "Best of 3"))
        self.playButtons.append(Button((700, 825), 180, 100, self.smallWiiFont, "Best of 5"))
        self.playButtons.append(Button((200, 930), 240, 120, self.smallWiiFont, "Back"))
        self.playButtons.append(Button((600, 930), 240, 120, self.smallWiiFont, "Play"))

        # How to Play Page
        # The how to play page tells the user how to play the game,
        # it has some instructions as well as rules that the user
        # will have to know while playing the game.
        self.infoTopCaption = self.menuCaptionFont.render("How to Play", True, (255, 255, 255))
        self.infoTopCaptionRect = self.infoTopCaption.get_rect(center=(400, 20))
        self.instCaption = self.menuMessageFont.render("Instructions", True, (255, 255, 255))
        self.instCaptionRect = self.instCaption.get_rect(center=(400, 60))
        self.infoText = []
        self.infoText.append(TextLine((30, 80), self.smallWiiFont, "The objective of this game is to have the ball bounce twice on your"))
        self.infoText.append(TextLine((30, 110), self.smallWiiFont,"opponent's side of the net. To hit the ball, you must hold down left"))
        self.infoText.append(TextLine((30, 140), self.smallWiiFont,"click and move your mouse in the direction you want to hit. Once you"))
        self.infoText.append(TextLine((30, 170), self.smallWiiFont, "let go, if you are close enough to the ball, you will hit the ball."))
        self.infoText.append(TextLine((30, 230), self.smallWiiFont, "To move you character around, you can use WASD, or what you"))
        self.infoText.append(TextLine((30, 260), self.smallWiiFont, "have set in the controls page."))
        self.infoText.append(TextLine((30, 320), self.smallWiiFont, "If you are serving, you must throw the ball to serve it. Do"))
        self.infoText.append(TextLine((30, 350), self.smallWiiFont, "this by pressing Q, or what you have set in the controls page."))
        self.infoText.append(TextLine((30, 410), self.smallWiiFont, "Note: It may take some time to get used to hitting the ball."))
        self.ruleCaption = self.menuMessageFont.render("Rules", True, (255, 255, 255))
        self.ruleCaptionRect = self.ruleCaption.get_rect(center=(400, 470))
        self.infoText.append(TextLine((30, 500), self.smallWiiFont, "1. You cannot hit the ball outside of the exterior white lines. This"))
        self.infoText.append(TextLine((30, 530), self.smallWiiFont, "will cause you to lose the point or have to serve again."))
        self.infoText.append(TextLine((30, 560), self.smallWiiFont, "2. When serving you must hit in the opposite serving box. This is the"))
        self.infoText.append(TextLine((30, 590), self.smallWiiFont, "small rectangle right behind the net diagonal to your serving position."))
        self.infoText.append(TextLine((30, 620), self.smallWiiFont, "If you miss the box, you are given a second serve. Failing to serve"))
        self.infoText.append(TextLine((30, 650), self.smallWiiFont, "twice will make you lose the point."))
        self.infoText.append(TextLine((30, 680), self.smallWiiFont, "3. If the ball bounces twice, the side opposite of the ball wins points."))
        self.infoText.append(TextLine((30, 710), self.smallWiiFont, "4. The first side to reach 50 points wins the round, but after a 40-40"))
        self.infoText.append(TextLine((30, 740), self.smallWiiFont, "tie, someone must win by a 20 point margin."))
        self.infoText.append(TextLine((30, 770), self.smallWiiFont, "5. An entire match is won after a certain number of rounds are won. You"))
        self.infoText.append(TextLine((30, 800), self.smallWiiFont, "can choose either 1, 2, or 3 rounds."))
        self.backButton = Button((400, 900), 300, 100, self.smallWiiFont, "Back")

        # Controls Page
        # The controls page shows the current controls of the user
        # it also will allow the user to choose and change controls.
        # The controls page is not only for the menu, as the controls
        # here in the menu class are used to move the character in the
        # actual game.
        self.contTopCaption = self.menuCaptionFont.render("Controls", True, (255, 255, 255))
        self.contTopCaptionRect = self.contTopCaption.get_rect(center=(400, 20))
        self.mouseSensNotch = pygame.Rect(190, 50, 50, 75)
        self.mouseSensCaption = self.menuMessageFont.render("Mouse Sensitivity: " + str(round((self.mouseSensNotch.centerx - 94) / 600, 2)), True, (255, 255, 255))
        self.mouseSensCaptionRect = self.mouseSensCaption.get_rect(topleft=(75, 130))
        self.leftMessage = self.menuMessageFont.render("Move Left:", True, (255, 255, 255))
        self.leftMessageRect = self.leftMessage.get_rect(topleft=(75, 200))
        self.rightMessage = self.menuMessageFont.render("Move Right:", True, (255, 255, 255))
        self.rightMessageRect = self.rightMessage.get_rect(topleft=(75, 300))
        self.forwMessage = self.menuMessageFont.render("Move Forwards:", True, (255, 255, 255))
        self.forwMessageRect = self.forwMessage.get_rect(topleft=(75, 400))
        self.downMessage = self.menuMessageFont.render("Move Downwards:", True, (255, 255, 255))
        self.downMessageRect = self.downMessage.get_rect(topleft=(75, 500))
        self.serveMessage = self.menuMessageFont.render("Serve Throw:", True, (255, 255, 255))
        self.serveMessageRect = self.serveMessage.get_rect(topleft=(75, 600))
        self.pauseMessage = self.menuMessageFont.render("Pause:", True, (255, 255, 255))
        self.pauseMessageRect = self.pauseMessage.get_rect(topleft=(75, 700))
        self.controlHelpText = []
        self.controlHelpText.append(TextLine((75, 765), self.tinyWiiFont, "Help: Press the button of the control you want to change, Then"))
        self.controlHelpText.append(TextLine((75, 785), self.tinyWiiFont, "press the key you want to change it too. Press Escape to cancel."))
        self.controlHelpText.append(TextLine((75, 805), self.tinyWiiFont, "Note: Some keys cannot be set as a control and you cannot duplicate"))
        self.controlHelpText.append(TextLine((75, 825), self.tinyWiiFont, "existing controls."))

        self.eventControls = []
        self.eventControls.append(pygame.event.Event(pygame.KEYDOWN))
        self.eventControls[0].key = pygame.K_a
        self.eventControls.append(pygame.event.Event(pygame.KEYDOWN))
        self.eventControls[1].key = pygame.K_d
        self.eventControls.append(pygame.event.Event(pygame.KEYDOWN))
        self.eventControls[2].key = pygame.K_w
        self.eventControls.append(pygame.event.Event(pygame.KEYDOWN))
        self.eventControls[3].key = pygame.K_s
        self.eventControls.append(pygame.event.Event(pygame.KEYDOWN))
        self.eventControls[4].key = pygame.K_q
        self.eventControls.append(pygame.event.Event(pygame.KEYDOWN))
        self.eventControls[5].key = pygame.K_p
        self.controlButtons = []
        self.controlButtons.append(Button((600, 220), 200, 70, self.smallWiiFont, chr(self.eventControls[0].key).upper()))
        self.controlButtons.append(Button((600, 320), 200, 70, self.smallWiiFont, chr(self.eventControls[1].key).upper()))
        self.controlButtons.append(Button((600, 420), 200, 70, self.smallWiiFont, chr(self.eventControls[2].key).upper()))
        self.controlButtons.append(Button((600, 520), 200, 70, self.smallWiiFont, chr(self.eventControls[3].key).upper()))
        self.controlButtons.append(Button((600, 620), 200, 70, self.smallWiiFont, chr(self.eventControls[4].key).upper()))
        self.controlButtons.append(Button((600, 720), 200, 70, self.smallWiiFont, chr(self.eventControls[5].key).upper()))
        self.controlButtons.append(Button((400, 900), 300, 100, self.smallWiiFont, "Back"))

        # About
        # The about menu tells the user some information about
        # the making of the game and my name
        self.abutTopCaption = self.menuCaptionFont.render("About", True, (255, 255, 255))
        self.abutTopCaptionRect = self.abutTopCaption.get_rect(center=(400, 20))
        self.abutText = []
        self.abutText.append(TextLine((30, 60), self.smallWiiFont,"Created By Harry He"))
        self.abutText.append(TextLine((30, 90), self.smallWiiFont,"Dec 2020 - Jan 2021"))
        self.abutText.append(TextLine((30, 150), self.smallWiiFont,"Inspired by Nintendo's Wii Sports"))
        self.abutText.append(TextLine((30, 210), self.smallWiiFont,"I got this idea during work. So shout out to ---------(classified)"))
        self.abutText.append(TextLine((30, 240), self.smallWiiFont,"This game took about 20 hours to make, so please give 5 star"))
        self.abutText.append(TextLine((30, 270), self.smallWiiFont,"on ----(classified)"))
        self.abutText.append(TextLine((30, 300), self.smallWiiFont,"Join my Patreon: --------(classified)"))
        self.abutText.append(TextLine((30, 360), self.smallWiiFont,"Some graphics for the game are designed by me, however many aren't."))
        self.abutText.append(TextLine((30, 390), self.smallWiiFont,"I couldn't find transparent images on Google so I actually photoshoped"))
        self.abutText.append(TextLine((30, 420), self.smallWiiFont,"only one image of Guest C into the characters in game. So yeah, the"))
        self.abutText.append(TextLine((30, 450), self.smallWiiFont,"hair is actually me painting with the help of the heal tool."))
        self.abutText.append(TextLine((30, 540), self.smallWiiFont,"Note: Many of the resources used in this game are copyright,"))
        self.abutText.append(TextLine((30, 570), self.smallWiiFont,"public monetization will result in a copyright infringement."))

        # Pause
        # The pause screen shows the options
        # to go to the controls page, go back to the main
        # menu, and to resume the game. The pause screen is helpful
        # to quit a match and restart, or to change and test controls.
        self.pauseTopCaption = self.menuCaptionFont.render("Pause", True, (255, 255, 255))
        self.pauseTopCaptionRect = self.pauseTopCaption.get_rect(center=(400, 20))
        self.pauseButtons = []
        self.pauseButtons.append(Button((400, 300), 300, 200, self.smallWiiFont, "Resume"))
        self.pauseButtons.append(Button((400, 510), 300, 200, self.smallWiiFont, "Controls"))
        self.pauseButtons.append(Button((400, 720), 300, 200, self.smallWiiFont, "Main Menu"))

        # Title Screen
        # The title screen introduces the game to the user
        # It also allows the user to press the A and D buttons
        # like in the real game where you press the A and B buttons to access
        # the real main menu
        self.titleBackground, self.titleRect = load_image("TitleBackground.bmp")
        self.titleCaptionFont = pygame.font.Font("contb.ttf", 170)
        self.titleCaptionLeft = self.titleCaptionFont.render("Wii", True, (102, 102, 102))
        self.titleCaptionLeftRect = self.titleCaptionLeft.get_rect(center=(172, 390))
        self.titleCaptionRight = self.titleCaptionFont.render("Tennis", True, (101, 168, 199))
        self.titleCaptionRightRect = self.titleCaptionRight.get_rect(center=(528, 390))
        self.checkCaptionFont = pygame.font.Font("contm.ttf", 40)
        self.checkMessage = self.checkCaptionFont.render("Please press   A   and   D  .", True, (102, 102, 102))
        self.checkMessageRect = self.checkMessage.get_rect(center=(330, 760))
        self.keyDisplays = []
        self.keyDisplays.append(KeyDisplay(655, 760, 50, 50, "W", self.smallWiiFont))
        self.keyDisplays.append(KeyDisplay(605, 810, 50, 50, "A", self.smallWiiFont))
        self.keyDisplays.append(KeyDisplay(655, 810, 50, 50, "S", self.smallWiiFont))
        self.keyDisplays.append(KeyDisplay(705, 810, 50, 50, "D", self.smallWiiFont))
        self.mainMenuDelay = 50

    def render(self, screen):
        # The render function will only render one page
        # at a time
        if(self.curPage == 0):
            # Main Menu
            # Rendering the background, text, and buttons
            # of the main menu
            screen.blit(self.mainBackground, self.backRect)
            screen.blit(self.menuMessage, self.menuMessageRect)
            screen.blit(self.menuCaptionLeft, self.menuCaptionLeftRect)
            screen.blit(self.menuCaptionRight, self.menuCaptionRightRect)
            for button in self.mainButtons:
                button.render(screen)
            pygame.draw.rect(screen, (255, 255, 255), pygame.Rect(350, 150, 400, 300), width=9)
            self.slideshow[self.curSlide].render(screen)
        elif(self.curPage == 1):
            # Play Page
            # Rendering the plain background, text, lines
            # and buttons of the play page.
            screen.blit(self.plainBackground, (0, 0))
            screen.blit(self.playTopCaption, self.playTopCaptionRect)
            pygame.draw.rect(screen, (50, 109, 60), pygame.Rect(250, 100, 300, 450))
            pygame.draw.line(screen, (255, 255, 255), (250, 100), (550, 100), 5)
            pygame.draw.line(screen, (255, 255, 255), (250, 100), (250, 550), 5)
            pygame.draw.line(screen, (255, 255, 255), (250, 550), (550, 550), 5)
            pygame.draw.line(screen, (255, 255, 255), (550, 100), (550, 550), 5)
            pygame.draw.line(screen, (255, 255, 255), (250, 212), (550, 212), 5)
            pygame.draw.line(screen, (255, 255, 255), (250, 437), (550, 437), 5)
            pygame.draw.line(screen, (255, 255, 255), (400, 212), (400, 437), 5)
            pygame.draw.line(screen, (0, 0, 0), (250, 325), (550, 325), 3)
            screen.blit(self.serveCaption, self.serveCaptionRect)
            pygame.draw.line(screen, (255, 255, 255), (0, 60), (350, 60), 5)
            pygame.draw.line(screen, (255, 255, 255), (450, 60), (800, 60), 5)
            screen.blit(self.difficultyCaption, self.difficultyCaptionRect)
            pygame.draw.line(screen, (255, 255, 255), (0, 600), (330, 600), 5)
            pygame.draw.line(screen, (255, 255, 255), (470, 600), (800, 600), 5)
            screen.blit(self.roundsCaption, self.roundsCaptionRect)
            pygame.draw.line(screen, (255, 255, 255), (0, 750), (330, 750), 5)
            pygame.draw.line(screen, (255, 255, 255), (470, 750), (800, 750), 5)
            for button in self.playButtons:
                button.render(screen)
        elif(self.curPage == 2):
            # How to Play
            # Rendering the plain background, button and
            # many lines of text in the instructions / rules
            # page
            screen.blit(self.plainBackground, (0, 0))
            screen.blit(self.infoTopCaption, self.infoTopCaptionRect)
            screen.blit(self.instCaption, self.instCaptionRect)
            pygame.draw.line(screen, (255, 255, 255), (0, 60), (320, 60), 5)
            pygame.draw.line(screen, (255, 255, 255), (480, 60), (800, 60), 5)
            screen.blit(self.ruleCaption, self.ruleCaptionRect)
            pygame.draw.line(screen, (255, 255, 255), (0, 470), (340, 470), 5)
            pygame.draw.line(screen, (255, 255, 255), (460, 470), (800, 470), 5)
            for line in self.infoText:
                line.render(screen)
            self.backButton.render(screen)
        elif(self.curPage == 3):
            # Control Page
            # Rendering the background, text, buttons, and mouse
            # sensitivity notch system of the controls page.
            screen.blit(self.plainBackground, (0, 0))
            screen.blit(self.contTopCaption, self.contTopCaptionRect)
            pygame.draw.rect(screen, (255, 255, 255), pygame.Rect(78, 50, 644, 75))
            if(pygame.mouse.get_pressed()[0]):
                mousePos = pygame.mouse.get_pos()
                if(mousePos[0] >= 78 and mousePos[0] <= 725 and mousePos[1] >= 50 and mousePos[1] <= 125):
                    self.mouseSensNotch.centerx = mousePos[0]
                    if(self.mouseSensNotch.centerx < 100):
                        self.mouseSensNotch.centerx = 100
                    elif(self.mouseSensNotch.centerx > 697):
                        self.mouseSensNotch.centerx = 697
            pygame.draw.rect(screen, (207, 226, 243), self.mouseSensNotch)
            self.mouseSensCaption = self.menuMessageFont.render(
                "Mouse Sensitivity: " + str(round((self.mouseSensNotch.centerx - 94) / 600, 2)), True, (255, 255, 255))
            self.mouseSensCaptionRect = self.mouseSensCaption.get_rect(topleft=(75, 130))
            screen.blit(self.mouseSensCaption, self.mouseSensCaptionRect)
            screen.blit(self.leftMessage, self.leftMessageRect)
            screen.blit(self.rightMessage, self.rightMessageRect)
            screen.blit(self.forwMessage, self.forwMessageRect)
            screen.blit(self.downMessage, self.downMessageRect)
            screen.blit(self.serveMessage, self.serveMessageRect)
            screen.blit(self.pauseMessage, self.pauseMessageRect)
            for line in self.controlHelpText:
                line.render(screen)
            for button in self.controlButtons:
                button.render(screen)
        elif(self.curPage == 4):
            # About Page
            # Rendering the text, background and button
            # of the about page
            screen.blit(self.plainBackground, (0, 0))
            screen.blit(self.abutTopCaption, self.abutTopCaptionRect)
            for line in self.abutText:
                line.render(screen)
            self.backButton.render(screen)
        elif(self.curPage == 5):
            # Pause Screen
            # Rendering the background, buttons,
            # and top caption of the pause screen
            screen.blit(self.plainBackground, (0, 0))
            screen.blit(self.pauseTopCaption, self.pauseTopCaptionRect)
            for button in self.pauseButtons:
                button.render(screen)
        elif(self.curPage == 6):
            # Title Screen
            # Rendering the cool background, the title, the lines
            # and the keyboard display of the pause screen
            screen.blit(self.titleBackground, self.titleRect)
            screen.blit(self.titleCaptionLeft, self.titleCaptionLeftRect)
            screen.blit(self.titleCaptionRight, self.titleCaptionRightRect)
            screen.blit(self.checkMessage, self.checkMessageRect)
            pygame.draw.rect(screen, (102, 102, 102), pygame.Rect(340, 738, 50, 50), width=5)
            pygame.draw.rect(screen, (102, 102, 102), pygame.Rect(496, 738, 50, 50), width=5)
            if(self.keyDisplays[1].isPressed):
                pygame.draw.line(screen, (237, 192, 123), (0, 810), (800, 810), 21)
            else:
                pygame.draw.line(screen, (165, 165, 165), (0, 810), (800, 810), 21)
            if (self.keyDisplays[3].isPressed):
                pygame.draw.line(screen, (101, 168, 199), (0, 830), (800, 830), 21)
            else:
                pygame.draw.line(screen, (187, 187, 187), (0, 830), (800, 830), 21)
            pygame.draw.rect(screen, (102, 102, 102), pygame.Rect(600, 740, 160, 160))
            for display in self.keyDisplays:
                display.render(screen)

# The Button Class
# This class is a helper class for the Menu Class, which houses many arrays
# of buttons which are easier to check clicks with. This class is represents
# the buttons in the Wii Sports Menu. It can widen and highlight whenever
# hovered over, it can also stay highlighted and widened when selected, but
# if it is not hovered over it will go back to its original size and color.

# The init function declares some of the variables needed to display
# and to highlight and widen the button whenever necessary. It will also
# set the position, original width and height and message, with a certain
# font, of the button.

# The collision function is a helpful function that allows the program
# to tell when the user has clicked or is hovering over the button

# The highlight function will make the button highlighted
# and set the button to a highlighted state

# The unhighlight function will make the button back to its
# original color and set the button to a unhighlighted state.

# The sizeify function will make the button larger or smaller
# depending on if it is highlighted. The button can only go to
# 1.1 times original base and height and will only shrink to its
# original base and height.

# The setCaption function will change the message and font of the
# message on the button. This is used when changing controls.

# The render function will render the button onto the screen,
# including its message.
class Button():
    def __init__(self, pos, width, height, font, message):
        self.org_image, self.rect = load_image("BaseRect.bmp", (255, 255, 255))
        self.hig_image, self.rect = load_image("HighlightedRect.bmp", (255, 255, 255))
        self.image = pygame.transform.scale(self.org_image, (width, height))
        self.rect = self.image.get_rect(center=pos)
        self.caption = font.render(message, True, (0, 0, 0))
        self.capRect = self.caption.get_rect(center=pos)
        self.isHighlighted = False
        self.curWidth = width
        self.curHeight = height
        self.baseWidth = width
        self.baseHeight = height
        self.isClicked = False
    def collision(self, mouseClick):
        if(mouseClick[0] >= self.rect.left and mouseClick[0] <= self.rect.right and
        mouseClick[1] >= self.rect.top and mouseClick[1] <= self.rect.bottom):
            return True
        return False
    def highlight(self):
        self.isHighlighted = True
        self.image = pygame.transform.scale(self.hig_image, (int(self.curWidth), int(self.curHeight)))
        self.rect = self.image.get_rect(center=self.rect.center)
    def unhighlight(self):
        self.isHighlighted = False
        self.image = pygame.transform.scale(self.org_image, (int(self.curWidth), int(self.curHeight)))
        self.rect = self.image.get_rect(center=self.rect.center)
    def sizeify(self):
        if(self.isHighlighted):
            if(self.curWidth <= 1.1 * self.baseWidth and self.curHeight <= 1.1 * self.baseHeight):
                self.curWidth += 0.01 * self.baseWidth
                self.curHeight += 0.01 * self.baseHeight
                self.image = pygame.transform.scale(self.hig_image, (int(self.curWidth), int(self.curHeight)))
                self.rect = self.image.get_rect(center=self.rect.center)
        else:
            if (self.curWidth > self.baseWidth and self.curHeight > self.baseHeight):
                self.curWidth -= 0.01 * self.baseWidth
                self.curHeight -= 0.01 * self.baseHeight
                self.image = pygame.transform.scale(self.org_image, (int(self.curWidth), int(self.curHeight)))
                self.rect = self.image.get_rect(center=self.rect.center)
    def setCaption(self, font, message):
        self.caption = font.render(message, True, (0, 0, 0))
        self.capRect = self.caption.get_rect(center=self.capRect.center)
    def render(self, screen):
        screen.blit(self.image, self.rect)
        screen.blit(self.caption, self.capRect)

# The Slide Class
# This class is a helper class for the Menu Class. This class is used
# in the slideshow on the main page of the menu. It allows the Menu
# Class to hold an array of slides, making it easier to render the
# image that corresponds to the last button the user highlighted.

# The init function declares the image, sets it at a certain size
# and the rectangle that bounds the image.

# The render function will render the slide onto the screen.
class Slide():
    def __init__(self, pos, image):
        self.image, self.rect = load_image(image)
        self.image = pygame.transform.scale(self.image, (400, 300))
        self.rect = self.image.get_rect(center=pos)
    def render(self, screen):
        screen.blit(self.image, self.rect)

# The TextLine Class
# This class is a helper class for the Menu Class. Since pygame doesn't
# support multiline string displays, this class allows the Menu to
# hold an array of text, making it easy to loop through and display
# all of the text.

# The init function declares the text and the rectangle that bounds the
# text.

# The render function will render the text onto the screen
class TextLine():
    def __init__(self, pos, font, message):
        self.text = font.render(message, True, (255, 255, 255))
        self.textRect = self.text.get_rect(topleft=pos)
    def render(self, screen):
        screen.blit(self.text, self.textRect)

# The KeyDisplay Class
# This class is a helper class for the Menu Class, really only for the title
# screen. This class allows the user to see what buttons they have pressed
# on the title screen. This class is really just to make the title screen
# look nicer.

# The init function declares some of the variables needed to display
# and to press the key display whenever necessary. It will display
# the key in a certain font and in a rectangle.

# The render function will render the key display onto the screen,
# including its respective key.

# The press function will make the button pressed by changing its colors
# and set the display to a pressed state

# The unpress function will make the button unpressed by changing its colors
# and set the display to a unpressed state
class KeyDisplay():
    def __init__(self, cornerX, cornerY, width, height, key, font):
        self.isPressed = False
        self.innerRect = pygame.Rect(cornerX, cornerY, width, height)
        self.textFont = font
        self.textKey = key
        self.text = font.render(key, True, (102, 102, 102))
        self.textRect = self.text.get_rect(center=self.innerRect.center)
    def render(self, screen):
        if(self.isPressed):
            pygame.draw.rect(screen, (102, 102, 102), self.innerRect)
            pygame.draw.rect(screen, (255, 255, 255), self.innerRect, width=3)
            screen.blit(self.text, self.textRect)
        else:
            pygame.draw.rect(screen, (255, 255, 255), self.innerRect)
            pygame.draw.rect(screen, (102, 102, 102), self.innerRect, width=3)
            screen.blit(self.text, self.textRect)
    def press(self):
        if(not self.isPressed):
            self.isPressed = True
            self.text = self.textFont.render(self.textKey, True, (255, 255, 255))
            self.textRect = self.text.get_rect(center=self.innerRect.center)
    def unpress(self):
        if(self.isPressed):
            self.isPressed = False
            self.text = self.textFont.render(self.textKey, True, (102, 102, 102))
            self.textRect = self.text.get_rect(center=self.innerRect.center)

# This method allows an event to be converted into a string
# so that it can be displayed onto a button
# This method isn't very expansive and only allows the user
# to press a select few keys
def eventToString(event):
    if(event.key >= 97 and event.key <= 122):
        return chr(event.key).upper()
    elif(event.key >= 48 and event.key <= 57):
        return chr(event.key)
    elif(event.key == 32):
        return "SPACE"
    elif (event.key == 1073742049):
        return "SHIFT"
    elif (event.key == 1073742051):
        return "CTRL"
    elif (event.key == 1073742050):
        return "ALT"
    elif (event.key == 1073741906):
        return "UP"
    elif (event.key == 1073741905):
        return "DOWN"
    elif (event.key == 1073741903):
        return "LEFT"
    elif (event.key == 1073741904):
        return "RIGHT"
    return ""

# This is the main method for this file. This doesn't
# actually run the game. However you can run this file and
# this method will allow you to go through the menu.
# Go to Main.py to see the entirety of the menu.
# Note: This method was used for testing primarily
# and it doesn't have all of the functionality as the game
def main():
    # Initializing pygame and the window
    pygame.init()
    screen = pygame.display.set_mode((800, 1000))
    pygame.display.set_caption('Menu Class')

    # Declaring the menu class, which is the menu displayed
    # on the screen
    menu = Menu()

    # Looping forever until the user quits
    while(True):
        # Looping through the events that may affect the menu
        for event in pygame.event.get():
            # If the user quits, the program will exit
            if(event.type == pygame.QUIT):
                raise SystemExit("Quitted Successfully")
            # If the user has clicked, check what page the menu is on
            # and check if the user has clicked on any buttons
            # If so, then do what the button indicates
            if(event.type == pygame.MOUSEBUTTONDOWN):
                mousePos = pygame.mouse.get_pos()
                # Main Menu
                if (menu.curPage == 0):
                    # Direct the user to the appropriate page if they click
                    # on a button. If they press the "quit" button the program
                    # will quit. Then add the current page, or the main page, into
                    # the prev pages stack.
                    for i in range(len(menu.mainButtons)):
                        if (menu.mainButtons[i].collision(mousePos)):
                            if(i == 4):
                                raise SystemExit("Quitted Successfully")
                            menu.prevPages.append(menu.curPage)
                            menu.curPage = i + 1
                            break
                elif(menu.curPage == 1):
                    # If the user presses on any of the buttons select it and
                    # keep it highlighted and deselect all of the other buttons
                    # In the same type as it. If they click on the "back" button
                    # go back to the previous page.
                    # Note: This has very limited functionality, and it is missing
                    # the "play" button's functions. This is because this file
                    # is only for the menu, go to the Main.py file to see the
                    # full functionality
                    for i in range(len(menu.playButtons)):
                        if (menu.playButtons[i].collision(mousePos)):
                            if(i < 2):
                                menu.playButtons[i].isClicked = True
                                for j in range(2):
                                    if(j != i):
                                        menu.playButtons[j].isClicked = False
                            elif(i < 5):
                                menu.playButtons[i].isClicked = True
                                for j in range(2, 5):
                                    if(j != i):
                                        menu.playButtons[j].isClicked = False
                            elif (i < 8):
                                menu.playButtons[i].isClicked = True
                                for j in range(5, 8):
                                    if (j != i):
                                        menu.playButtons[j].isClicked = False
                            elif(i == 8):
                                menu.curPage = menu.prevPages.pop()
                            # else:
                            #
                elif(menu.curPage == 2):
                    # Check if the user pressed "back" and go back
                    # to the previous page if they did.
                    if(menu.backButton.collision(mousePos)):
                        menu.curPage = menu.prevPages.pop()
                elif(menu.curPage == 3):
                    # Check if the user pressed on any buttons
                    # If they selected a control button and wait
                    # till the user presses a key to change the control
                    # or press another button.
                    # If the press the back button deselect everything and
                    # return to the previous page.
                    # If they didn't press any buttons, deselect everything
                    pressedButton = False
                    for i in range(len(menu.controlButtons)):
                        if (menu.controlButtons[i].collision(mousePos)):
                            if(i < 6):
                                menu.controlButtons[i].isClicked = True
                                pressedButton = True
                                for j in range(6):
                                    if(j != i):
                                        menu.controlButtons[j].isClicked = False
                            elif(i == 6):
                                for j in range(6):
                                    menu.controlButtons[j].isClicked = False
                                menu.curPage = menu.prevPages.pop()
                    if(not pressedButton):
                        for j in range(5):
                            menu.controlButtons[j].isClicked = False
            elif(event.type == pygame.KEYDOWN):
                # If the user is on the controls page
                # See if the key they pressed is escape, if so
                # the deselect the selected control button.
                # Otherwise, see if the key can be converted to a string
                # in the eventToString method. If so set the control
                # to the key, otherwise don't change anything.
                if(menu.curPage == 3):
                    for i in range(len(menu.controlButtons) - 1):
                        if(menu.controlButtons[i].isClicked):
                            if (event.key == pygame.K_ESCAPE):
                                menu.controlButtons[i].isClicked = False
                            else:
                                alreadyExists = False
                                for exist in menu.eventControls:
                                    if(event.key == exist.key):
                                        alreadyExists = True
                                buttonCaption = eventToString(event)
                                if(not alreadyExists and buttonCaption != ""):
                                    menu.eventControls[i] = event
                                    menu.controlButtons[i].setCaption(menu.smallWiiFont, buttonCaption)
                elif(menu.curPage == 6):
                    # If the user is on the title page
                    # and if they press any of the WASD
                    # keys, the corresponding display will
                    # get pressed
                    if (event.key == pygame.K_w):
                        menu.keyDisplays[0].press()
                    elif (event.key == pygame.K_a):
                        menu.keyDisplays[1].press()
                    elif (event.key == pygame.K_s):
                        menu.keyDisplays[2].press()
                    elif (event.key == pygame.K_d):
                        menu.keyDisplays[3].press()
            elif(event.type == pygame.KEYUP):
                if (menu.curPage == 6):
                    # If the user is on the title page
                    # and if they stop pressing one of the WASD
                    # keys, the corresponding display will
                    # get uppressed
                    if (event.key == pygame.K_w):
                        menu.keyDisplays[0].unpress()
                    elif (event.key == pygame.K_a):
                        menu.keyDisplays[1].unpress()
                    elif (event.key == pygame.K_s):
                        menu.keyDisplays[2].unpress()
                    elif (event.key == pygame.K_d):
                        menu.keyDisplays[3].unpress()

        # This block of code is used to see if the user
        # has hovered over a button. If so then highlight
        # and widen that button.
        mousePos = pygame.mouse.get_pos()
        if(menu.curPage == 0):
            # In the main menu, if the mouse hovers over a button
            # then highlight it. Unhighlight the buttons
            # that are not hovered over, this can be all of them.
            for i in range(len(menu.mainButtons)):
                if(menu.mainButtons[i].collision(mousePos)):
                    menu.mainButtons[i].highlight()
                    menu.curSlide = i
                else:
                    menu.mainButtons[i].unhighlight()
                menu.mainButtons[i].sizeify()
        elif(menu.curPage == 1):
            # In the play page, if the mouse hovers over a button
            # highlight and widen it. However, buttons that have
            # been selected will also be highlighted and widened.
            # Other buttons will be unhighlighted and shrinked to
            # their original size.
            for i in range(len(menu.playButtons)):
                if(menu.playButtons[i].collision(mousePos) or menu.playButtons[i].isClicked):
                    menu.playButtons[i].highlight()
                elif(not menu.playButtons[i].isClicked):
                    menu.playButtons[i].unhighlight()
                menu.playButtons[i].sizeify()
        elif(menu.curPage == 2):
            # In the how to play page, if the user hovers
            # over the back button, highlight and widen it.
            # Otherwise, shrink it to its original size.
            if(menu.backButton.collision(mousePos)):
                menu.backButton.highlight()
            else:
                menu.backButton.unhighlight()
            menu.backButton.sizeify()
        elif(menu.curPage == 3):
            # In the controls page, if the user highlights over some
            # button, highlight it. Buttons that are also selected will
            # also be highlighted
            for i in range(len(menu.controlButtons)):
                if(menu.controlButtons[i].collision(mousePos) or menu.controlButtons[i].isClicked):
                    menu.controlButtons[i].highlight()
                elif(not menu.controlButtons[i].isClicked):
                    menu.controlButtons[i].unhighlight()
                menu.controlButtons[i].sizeify()
        elif(menu.curPage == 6):
            # In the title page, if the user presses both the
            # a and d key at the same time, the menu will count down
            # a delay and go to the main menu
            if(menu.keyDisplays[1].isPressed and menu.keyDisplays[3].isPressed):
                if(menu.mainMenuDelay <= 0):
                    menu.curPage = 0
                    menu.prevPages = []
                else:
                    menu.mainMenuDelay -= 1
            else:
                menu.mainMenuDelay = 50
        # Render the menu, see the Menu Class's
        # render method to see the specifics.
        menu.render(screen)
        pygame.display.flip()

# When the file is run, run the main class
# These lines are necessary as otherwise, this
# file will also run when you run the Main.py
# file.
if __name__ == "__main__":
    main()