# Harry He - Dec 16 2020
# This python file houses the racket and player class,
# go to the Main.py file to see the primary code and to run the
# game.
# The Player class represents the player in game. This class
# can move around to according to what the user presses.

# The Racket class represents the racket in the game. This class
# can be used in both the player and enemies hand. This class
# can point to either the cursor or the ball, depending
# on which method is run during the actual game. Go to the Main.py
# file to see when either rotation method is run.

# Importing some of the assets needed to run the classes
import pygame
import os
from pygame.compat import geterror
from pygame.math import Vector2
import math

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

# The Player Class
# This class is the player within the game. It can move around
# but is mainly just help the user know where their avatar is.

# The init method defines some of the things needed to display the
# player as well as its shadow.

# The move method moves the player's avatar depending on the dx and
# dy parameters. If the player get too close to the net or the other
# side, it will stop moving closer. Otherwise, the player will move
# regularly.
class Player(pygame.sprite.Sprite):
    def __init__(self, pos):
        pygame.sprite.Sprite.__init__(self)
        self.image, self.rect = load_image("MiiBack.bmp", (255, 255, 255))
        self.image = pygame.transform.scale(self.image, (100, 100))
        self.rect = self.image.get_rect()
        self.rect.center = pos
        screen = pygame.display.get_surface()
        self.area = screen.get_rect()
        self.shadow_screen = pygame.Surface((screen.get_size()))
        self.shadow_screen = self.shadow_screen.convert()
        self.height = 35
        pygame.draw.circle(self.shadow_screen, (0, 0, 0),
                           (self.rect.centerx,
                            self.rect.centery + self.height), 20)
        self.shadow_screen.set_alpha(160)
    def move(self, dx, dy):
        newpos = self.rect.move((dx, dy))
        if (newpos.centery <= 510):
            newpos = self.rect.move((dx, 0))
        self.rect = newpos
        self.shadow_screen = pygame.Surface((self.area.w, self.area.h))
        pygame.draw.circle(self.shadow_screen, (0, 0, 0),
                           (self.rect.centerx,
                            self.rect.centery + self.height), 20)

# The Racket Class
# This class represents the racket in the game. It can point
# towards either the ball or the cursor, depending on whose side's
# racket it is. See the Main.py file to see when the rotation
# methods are used for either side.

# The init function defines the variables needed to display the racket
# as well as rotate it around a pivot point.

# The move function will move racket depending on the dx and dy parameters.
# If the racket is too close to the net it will stop getting closer. Otherwise,
# it will move regularly. Note: The racket can be on either side of the court,
# making its necessary to check if it is too close to the net on either side.

# The setAngle function will set the racket's current angle to a certain angle.
# It will also rotate the racket around a pivot point so that it is at that
# specific angle.

# The findAngle function will use trigonometry to figure out what angle the
# racket should face according to its triangles side lengths.
# See the latter two functions to see what the triangles side lengths
# are derived from.

# The update function will make the racket's angle face the cursor.
# It passes the difference from the cursor's position to the racket's
# position to the findAngle function to figure out what angle the racket
# should be.

# The compUpdate function will make the racket's angle face the ball.
# It passes the difference from the ball's position to the racket's position
# to the findAngle function to figure out what the angle the racket should
# be.
class Racket(pygame.sprite.Sprite):
    def __init__(self, pos, distance):
        pygame.sprite.Sprite.__init__(self)
        # Or use super().__init__()
        self.org_image, self.rect = load_image("TennisRacket.bmp", (255, 255, 255))
        #self.org_w, self.org_h = self.org_image.get_size()
        self.org_image = pygame.transform.scale(self.org_image, (100, 100))
        self.image = self.org_image
        self.rect = self.image.get_rect(center=pos)
        self.pos = Vector2(pos)
        self.offset = Vector2(0, -distance)
        self.curAngle = 0
    def move(self, dx, dy):
        newpos = self.rect.move((int(dx), int(dy)))
        if (self.pos[1] + int(dy) <= 510 and self.pos[1] + int(dy) >= 490):
            newpos = self.rect.move((int(dx), 0))
            self.rect = newpos
            self.pos += (int(dx), 0)
        else:
            self.rect = newpos
            self.pos += (int(dx), int(dy))
    def setAngle(self, angle):
        self.curAngle = angle % 360
        self.image = pygame.transform.rotate(self.org_image, self.curAngle)
        offset_rotated = self.offset.rotate(-self.curAngle)
        self.rect = self.image.get_rect(center=(int(self.pos[0] + offset_rotated.x), int(self.pos[1] + offset_rotated.y)))
    def findAngle(self, opp, adj):
        return math.atan2(opp, adj) * 180 / math.pi - 180
    def update(self):
        posX, posY = pygame.mouse.get_pos()
        angle = self.findAngle(posX - self.pos.x, posY - self.pos.y)
        self.setAngle(angle)
    def compUpdate(self, ballX, ballY):
        angle = self.findAngle(ballX - self.pos.x, ballY - self.pos.y)
        self.setAngle(angle)

# This is the main method for this file. This doesn't
# actually run the game. However you can run this file and
# see some of the very first testing of the game.
# Go to Main.py to see the entirety of the menu.
# Note: This method was used for testing primarily
# and actually no longer works due to updates
# in the classes above.
def main():
    # Initializing pygame and the window
    pygame.init()
    screen = pygame.display.set_mode((800, 1000))
    pygame.display.set_caption('Racket Class')

    # Initializing a background to contrast some
    # of the graphics to see how low quality it is.
    background = pygame.Surface(screen.get_size())
    background = background.convert()
    background.fill((0, 255, 0))

    # Declaring the classes need to run the game.
    # The sprites variable is just a collection of the two
    # classes so that we can render them simply later.
    distance = 50
    pos = (400, 400)
    tennis_racket = Racket(pos, distance)
    player = Player(pos)
    sprites = pygame.sprite.RenderPlain(tennis_racket, player)

    # Declaring the variables to help move the character smoothly
    # These variables allow us to hold down a button and move the
    # characters accordingly.
    isUp = False
    isDown = False
    isRight = False
    isLeft = False

    # Looping until the user quits.
    while 1:
        # Checking for events that will impact the
        # game.
        for event in pygame.event.get():
            # If the user has pressed quit
            # the program will exit
            if event.type == pygame.QUIT:
                raise SystemExit
            # Check if the user has pressed any
            # of the movement keys to move the
            # character. If so then set the
            # movement variable for that key to
            # be true.
            elif (event.type == pygame.KEYDOWN):
                if (event.key == pygame.K_w):
                    isUp = True
                elif (event.key == pygame.K_s):
                    isDown = True
                elif (event.key == pygame.K_a):
                    isLeft = True
                elif (event.key == pygame.K_d):
                    isRight = True
            # If the users has stopped pressing
            # a movement key, make that movement
            # variable false.
            elif (event.type == pygame.KEYUP):
                if (event.key == pygame.K_w):
                    isUp = False
                elif (event.key == pygame.K_s):
                    isDown = False
                elif (event.key == pygame.K_a):
                    isLeft = False
                elif (event.key == pygame.K_d):
                    isRight = False

        # If the movement variables are true
        # meaning that the user is holding down a
        # key, move the player and the racket in
        # that direction.
        if(isUp):
            tennis_racket.move(0, -5)
            player.move(0, -5)
        if (isDown):
            tennis_racket.move(0, 5)
            player.move(0, 5)
        if (isLeft):
            tennis_racket.move(-5, 0)
            player.move(-5, 0)
        if (isRight):
            tennis_racket.move(5, 0)
            player.move(5, 0)

        # Rotate the racket to face the cursor
        posX, posY = pygame.mouse.get_pos()
        angle = tennis_racket.findAngle(posX - tennis_racket.pos.x, posY - tennis_racket.pos.y)
        tennis_racket.setAngle(angle)

        # Render all of the graphics onto the screen
        screen.blit(background, (0, 0))
        sprites.draw(screen)
        pygame.display.flip()

# When the file is run, run the main class
# These lines are necessary as otherwise, this
# file will also run when you run the Main.py
# file.
if __name__ == "__main__":
    main()
    pygame.quit()