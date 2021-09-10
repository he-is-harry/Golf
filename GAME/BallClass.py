# Harry He - December 22 2020
# This python file houses the ball class, go to the Main.py
# file to see the primary code and to run the game
# This class represents the ball in the game, it can bounce
# and move

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

# The Ball Class
# This class represents the tennis ball in the game, it can move, bounce
# and uses has a shadow to make it look like it is bouncing

# The init function, initializes the variables used to move and bounce
# the ball.

# The move function moves the ball depending on the dx and dy parameters
# if the ball is too low that it hits the net, the ball will stop and it
# will play the net sound effect. Otherwise it will move properly

# The bounce function will move the ball higher or lower but not the shadow
# to emulate a bouncing effect. After each bounce, the ball will increase
# its numBounces variable, proportionately decrease the bounce height and
# go back up. Otherwise it will go up until it reaches the bounce height or
# go down if it has reached the bounce height already

# The serveThrow function will make the ball go up and down, but will not
# make the ball bounce back up, emulating a throw in the air, and then
# catching it.

# The setHeight function sets the height of the ball to a specific height,
# however it will never move the shadow.

# The set_pos function sets the ball to a specific position

# The reset function will reset all of the ball's components back to
# a non-bouncing state. It allows the ball to return to a serving position.
class Ball(pygame.sprite.Sprite):
    def __init__(self, pos):
        pygame.sprite.Sprite.__init__(self)
        self.image, self.rect = load_image("TennisBall.bmp", (0, 0, 0))
        self.image = pygame.transform.scale(self.image, (25, 25))
        self.rect = self.image.get_rect(center=(int(pos[0]), int(pos[1])))
        # self.rect.center = (int(pos[0]), int(pos[1]))
        self.directionX = 1
        self.directionY = 1
        screen = pygame.display.get_surface()
        self.area = screen.get_rect()
        self.shadow_screen = pygame.Surface((screen.get_size()))
        self.shadow_screen = self.shadow_screen.convert()
        self.height = 10
        pygame.draw.circle(self.shadow_screen, (0, 0, 0),
                           (self.rect.centerx,
                            self.rect.centery + self.height), 10)
        self.shadow_screen.set_alpha(160)
        self.isUp = False
        self.bounceHeight = 0
        self.stop = True
        self.numBounces = 0
        self.trueCenter = list(pos)

    def move(self, dx, dy, soundEffects):
        self.trueCenter[0] += dx * self.directionX
        self.trueCenter[1] += dy * self.directionY
        newpos = self.image.get_rect(center=(self.trueCenter[0], self.trueCenter[1] - self.height))
        # if not self.area.contains(newpos):
        #     if self.rect.left < self.area.left or self.rect.right > self.area.right:
        #         self.directionX *= -1
        #     if self.rect.top < self.area.top or self.rect.bottom > self.area.bottom:
        #         self.directionY *= -1
        #     newpos = self.rect.move((dx * self.directionX, dy * self.directionY))

        # Note: 500 is a hard coded number representing the net's y value
        if(((newpos.centery + self.height >= 490 and self.rect.centery + self.height <= 490) or
            (newpos.centery + self.height <= 510 and self.rect.centery + self.height >= 510)) and self.height < 50):
            self.directionY *= -0.01
            self.directionX *= 0.01
            self.bounceHeight *= 0.1
            newpos = self.rect.move((dx * self.directionX, dy * self.directionY))
            if(not soundEffects.isPlayingNet):
                soundEffects.net.play()
                soundEffects.isPlayingNet = True
        self.rect = newpos
        self.shadow_screen = pygame.Surface((self.area.w, self.area.h))
        # self.shadow_screen.fill((255, 255, 255))
        pygame.draw.circle(self.shadow_screen, (0, 0, 0),
                           (self.rect.centerx,
                            self.rect.centery + self.height), 10)
    def bounce(self, soundEffects):
        if (self.isUp and not self.stop):
            self.setHeight(self.height + 2)
        elif (not self.stop):
            self.setHeight(self.height - 3)
        if (self.height >= self.bounceHeight and not self.stop):
            self.isUp = False
        elif (self.height < 10 and not self.stop):
            if(not self.isUp):
                self.numBounces += 1
                soundEffects.bounce.play()
            self.isUp = True
            if (self.bounceHeight >= 10):
                self.bounceHeight *= 0.8
            else:
                self.stop = True
    def serveThrow(self):
        if (self.isUp and not self.stop):
            self.setHeight(self.height + 2)
        elif (not self.stop):
            self.setHeight(self.height - 3)
        if (self.height >= self.bounceHeight and not self.stop):
            self.isUp = False
        elif (self.height < 10 and not self.stop):
            self.isUp = True
            self.stop = True
    def setHeight(self, h):
        newpos = self.rect.move((0, self.height - h))
        self.rect = newpos
        self.height = h
    def set_pos(self, pos):
        self.rect = self.image.get_rect(center=(int(pos[0]), int(pos[1])))
        self.shadow_screen = pygame.Surface((self.area.w, self.area.h))
        self.shadow_screen = self.shadow_screen.convert()
        self.height = 10
        pygame.draw.circle(self.shadow_screen, (0, 0, 0),
                           (self.rect.centerx,
                            self.rect.centery + self.height), 10)
        self.shadow_screen.set_alpha(160)
        self.trueCenter = list(pos)
    def reset(self):
        self.directionX = 1
        self.directionY = 1
        self.isUp = False
        self.bounceHeight = 0
        self.stop = True
        self.numBounces = 0

