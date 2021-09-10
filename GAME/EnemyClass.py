# Harry He - December 27 2020
# This python file houses the enemy class, go to the Main.py
# file to see the primary code and to run the game
# This class represents the enemy in the game, it can hit,
# serve, move, and check if the ball will go out
# The class relies on randomness to ensure that the enemy can
# be beat. Note: It is possible to hit past the enemies even
# if they are at their peak speed

# Importing some of the assets needed to run the class
import pygame
import os
from pygame.compat import geterror
import math
from random import randint

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

# The Enemy Class
# This class is used to be the enemy of the player. It represents
# the opponent you face whenever you play a match of tennis. It can
# can hit, serve, move, and check if the ball will go out. The speed,
# accuracy, and smarts of the enemy depend on the difficulty.

# The init function, initializes the variables used to render the enemy
# and determine where the enemy can hit.

# The move function moves the enemy depending on the dx and dy parameters
# if the enemy is too close to the net, he will not move down any
# further. Otherwise, the enemy will move properly.

# The run function will move the enemy towards the ball or towards
# an intercept point. Depending on the enemy's difficulty it will move
# faster and smarter. Additionally, depending on the enemies fatigue
# it will move slower.
# - For Basic and Moderate enemies they will move directly at the ball,
# and the Moderate enemy will move faster
# - For the Advanced enemy it will move towards the closest intercept point,
# this is the point when the ball is closest to the enemy

# The relocate method will move the enemy towards a point where they can
# return the ball better. Depending on the enemy's difficulty it will move
# faster. Again, if depending on the fatigue it will move slower.
# - For the Basic Enemy it is not smart enough to move back to a point
# where it can hit the ball easier
# - For the Moderate and Advanced enemy, they will move towards the center
# of the court to make hitting the next shot easier. The Advanced enemy
# moves faster than the Moderate.

# The hit method will determine where the enemy will hit and return the
# velocity in which the ball moves on the x and y axis. Depending on the
# difficulty of the enemy they will hit in less or more difficult spots.
# Depending on the fatigue it will hit less accurately, increasing the
# likelihood that they get it out.
# - The Basic Enemy will hit towards the player and add random values
# to mix up the positions and possibly get it out.
# - The Moderate Enemy will hit on the opposite side of the player,
# left or right, and add random values to mix up the positions and
# possibly get it out
# - The Advanced Enemy will hit in the most difficult spots to recieve
# the ball. Additionally, the Advanced Enemy will avoid hitting the ball
# in the same quadrant as the player, to make it even harder.

# The serve method will determine where the enemy will serve and return
# the velocity in which the ball moves on the x and y axis. Depending on the
# difficulty of the enemy they will hit in less or more difficult spots and
# be more or less consistent in their serving.
# - The Basic Enemy will hit in the general vicinity of the serving box.
# However, like many beginner players, the enemy will be highly inconsistent
# and hit the ball out most of the time.
# - The Moderate Enemy will hit in the serving box most of the time. However,
# while more consistent than the Basic Enemy, it can still hit the ball out.
# - The Advanced Enemy will hit in the serving box 100% of the time. While
# this is not possible in real life, the Advanced Enemy emulates many
# professionals in the ability to serve practically perfectly. Additionally,
# the Advanced Enemy will hit in two of the most difficult spots to receive.

# The willGoOut method will tell the enemy whether to hit the ball or not.
# Depending on the difficulty the enemy will be able to more or less
# accurately tell if the ball will go out.
# - The Basic Enemy is not smart enough to know if the ball will go out.
# As a result, the method will always return False, indicating that the ball
# will remain in bounds.
# - The Moderate Enemy will be able to tell if the ball is going out.
# However, to ensure it is not as smart as the Advanced Enemy, it will have
# less accurate results as a large random value will be added to the guessed
# resulting position of the ball. Although the enemy is not extremely accurate,
# if the ball is very evidently not going in, it will be able to tell.
# - The Advanced Enemy will be able to tell if the ball is going out. However,
# it is not 100% accurate as there is a small random value added to the estimated
# position of the ball that can make the enemy inaccurate. This random value
# helps to add some realism in the game, as not even professionals can know if the
# ball is going out 100% of the time.
class Enemy(pygame.sprite.Sprite):
    def __init__(self, pos):
        pygame.sprite.Sprite.__init__(self)
        self.image, self.rect = load_image("MiiFront.bmp", (255, 255, 255))
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
        self.difficulty = 1
        self.fatigue = 0

        # This range is equivalent to the hard difficulty for players
        # This is most realistic
        self.outerRange = 100
        self.innerRange = 25
    def move(self, dx, dy):
        newpos = self.rect.move((dx, dy))
        if (newpos.centery >= 490):
            newpos = self.rect.move((dx, 0))
        self.rect = newpos
        self.shadow_screen = pygame.Surface((self.area.w, self.area.h))
        pygame.draw.circle(self.shadow_screen, (0, 0, 0),
                           (self.rect.centerx,
                            self.rect.centery + self.height), 20)
    def run(self, ball, averageX, averageY):
        ballX = ball.rect.centerx
        ballY = ball.rect.centery
        if(self.difficulty == 1):
            diffX = self.rect.centerx - ballX - 30
            diffY = self.rect.centery - ballY - 30
            distance = math.sqrt((self.rect.centerx - ballX) * (self.rect.centerx - ballX) + (self.rect.centery - ballY) * (self.rect.centery - ballY))
            if(distance <= 50):
                return 0, 0
            velX = (int)(((-1.5 - (0.1 * self.fatigue)) / distance) * diffX)
            velY = (int)(((-1.5 - (0.1 * self.fatigue)) / distance) * diffY)
        elif(self.difficulty == 2):
            diffX = self.rect.centerx - ballX - 30
            diffY = self.rect.centery - ballY - 30
            distance = math.sqrt(
                (self.rect.centerx - ballX) * (self.rect.centerx - ballX) + (self.rect.centery - ballY) * (
                            self.rect.centery - ballY))
            if (distance <= 70):
                return 0, 0
            velX = (int)(((-2.5 - (0.1 * self.fatigue)) / distance) * diffX)
            velY = (int)(((-2.5 - (0.1 * self.fatigue)) / distance) * diffY)
        else:
            bestPosX = ballY
            bestPosY = ballX
            bestDist = math.sqrt(
                (self.rect.centerx - bestPosX) * (self.rect.centerx - bestPosX) + (self.rect.centery - bestPosY) * (
                        self.rect.centery - bestPosY))
            temp = ball.height
            curX = ballX
            curY = ballY
            while (temp >= 10):
                temp -= 3
                curX += averageX
                curY += averageY
                distanceFromCur = math.sqrt((curX - ballX) * (curX - ballX) + (curY - ballY) * (curY - ballY))
                if(distanceFromCur < bestDist):
                    bestDist = distanceFromCur
                    bestPosX = curX
                    bestPosY = curY
            diffX = self.rect.centerx - bestPosX - 30
            diffY = self.rect.centery - bestPosY - 30
            distance = math.sqrt(
                (self.rect.centerx - bestPosX) * (self.rect.centerx - bestPosX) + (self.rect.centery - bestPosY) * (
                        self.rect.centery - bestPosY))
            if (distance <= 70):
                return 0, 0
            velX = (int)(((-3.5 - (0.1 * self.fatigue)) / distance) * diffX)
            velY = (int)(((-3.5 - (0.1 * self.fatigue)) / distance) * diffY)

        return velX, velY
    def relocate(self):
        # This method is not valid for the BasicEnemy class, as it
        # is not smart enough
        # However, in better enemies, this method will need to be used
        # to make the enemies return to a better position for the next
        # shot
        if(self.difficulty == 1):
            return 0, 0
        else:
            diffX = self.rect.centerx - 400 - 30
            diffY = self.rect.centery - 150 - 30
            distance = math.sqrt((self.rect.centerx - 400) * (self.rect.centerx - 400) + (self.rect.centery - 150) * (
                    self.rect.centery - 150))
            if(self.difficulty == 2):
                velX = (int)(((-2.5 - (0.1 * self.fatigue)) / distance) * diffX)
                velY = (int)(((-2.5 - (0.1 * self.fatigue)) / distance) * diffY)
            else:
                velX = (int)(((-3.5 - (0.1 * self.fatigue)) / distance) * diffX)
                velY = (int)(((-3.5 - (0.1 * self.fatigue)) / distance) * diffY)

            return velX, velY
    def hit(self, playerX, playerY, tennis_ball, enemy_racket):
        if(self.difficulty == 1):
            velX = (playerX - self.rect.centerx + randint(-100 - (5 * self.fatigue), 100 + (5 * self.fatigue))) / randint(130, 170)
            velY = (playerY - self.rect.centery + randint(-100 - (5 * self.fatigue), 100 + (5 * self.fatigue))) / randint(130, 170)
        elif(self.difficulty == 2):
            if (playerX <= 400):
                velX = (550 - self.rect.centerx + randint(-100 - (5 * self.fatigue), 100 + (5 * self.fatigue))) / randint(80, 120)
                velY = (playerY - self.rect.centery + randint(-100 - (5 * self.fatigue), 100 + (5 * self.fatigue))) / randint(100, 140)
            else:
                velX = (250 - self.rect.centerx + randint(-100 - (5 * self.fatigue), 100 + (5 * self.fatigue))) / randint(80, 120)
                velY = (playerY - self.rect.centery + randint(-100 - (5 * self.fatigue), 100 + (5 * self.fatigue))) / randint(100, 140)
        else:
            diffX = tennis_ball.rect.centerx - (enemy_racket.pos[0] + enemy_racket.offset.x) - 30
            diffY = tennis_ball.rect.centery - (enemy_racket.pos[1] + enemy_racket.offset.y) - 30
            distance = math.sqrt(diffX * diffX + diffY * diffY)
            bounceHeight = (int)(130 - 0.2 * distance) + (0.5 * tennis_ball.height)
            ticksTaken = (int)((bounceHeight - tennis_ball.height) / 2 + 1 + (bounceHeight - 10) / 3 + 1)
            favHitPos = [(130, 650), (130, 920), (670, 650), (670, 920)]
            if(playerX <= 400):
                if(playerY <= 725):
                    favHitPos.pop(0)
                else:
                    favHitPos.pop(1)
            else:
                if (playerY <= 725):
                    favHitPos.pop(2)
                else:
                    favHitPos.pop(3)
            selectedPos = randint(0, 2)
            velX = (favHitPos[selectedPos][0] - tennis_ball.rect.centerx
                   + randint(-5 * self.fatigue, 5 * self.fatigue)) / ticksTaken
            velY = (favHitPos[selectedPos][1] - (tennis_ball.rect.centery + tennis_ball.height)
                    + randint(-5 * self.fatigue, 5 * self.fatigue)) / ticksTaken
        if(self.fatigue < 10):
            self.fatigue += 1
        return velX, velY
    def serve(self, isRightSide, tennis_ball, enemy_racket):
        if(self.difficulty == 1):
            if(isRightSide):
                velX = (550 - self.rect.centerx + randint(-100, 100)) / randint(60, 100)
                velY = (612 - self.rect.centery + randint(-100, 150)) / randint(60, 100)
                return velX, velY
            else:
                velX = (250 - self.rect.centerx + randint(-100, 100)) / randint(60, 100)
                velY = (612 - self.rect.centery + randint(-100, 150)) / randint(60, 100)
                return velX, velY
        elif(self.difficulty == 2):
            if (isRightSide):
                velX = (550 - self.rect.centerx + randint(-50, 50)) / randint(80, 100)
                velY = (612 - self.rect.centery + randint(-50, 70)) / randint(80, 100)
                return velX, velY
            else:
                velX = (250 - self.rect.centerx + randint(-50, 50)) / randint(80, 100)
                velY = (612 - self.rect.centery + randint(-50, 70)) / randint(80, 100)
                return velX, velY
        else:
            diffX = tennis_ball.rect.centerx - (enemy_racket.pos[0] + enemy_racket.offset.x) - 30
            diffY = tennis_ball.rect.centery - (enemy_racket.pos[1] + enemy_racket.offset.y) - 30
            distance = math.sqrt(diffX * diffX + diffY * diffY)
            bounceHeight = (int)(130 - 0.2 * distance) + (0.5 * tennis_ball.height)
            ticksTaken = (int)((bounceHeight - tennis_ball.height) / 2 + 1 + (bounceHeight - 10) / 3 + 1)
            favHitPos = [(120, 695), (380, 695), (420, 695), (680, 695)]
            if(isRightSide):
                favHitPos.pop(0)
                favHitPos.pop(0)
            else:
                favHitPos.pop()
                favHitPos.pop()
            selectedPos = randint(0, 1)
            ballX = 550
            if(isRightSide):
                ballX = 250
            velX = (favHitPos[selectedPos][0] - ballX) / ticksTaken
            velY = (favHitPos[selectedPos][1] - 150) / ticksTaken
            return velX, velY

    def willGoOut(self, averageX, averageY, ball, isServe, isRightSide):
        # This method is not valid for the BasicEnemy class, as it
        # is not smart enough
        # However, in better enemies, this method will need to be used
        # to make the enemies know whether to hit a shot or not
        if(self.difficulty == 1):
            return False
        else:
            predictAcc = 30
            if(self.difficulty == 3):
                predictAcc = 10
            if (ball.numBounces < 1):
                temp = ball.height
                curX = ball.rect.centerx
                curY = ball.rect.centery
                while (temp >= 10):
                    temp -= 3
                    curX += averageX
                    curY += averageY
                if (isServe):
                    if (isRightSide and (curX < 100 - 12 + randint(-predictAcc, predictAcc) or curX > 400 + 12 + randint(-predictAcc, predictAcc) or
                                         curY - temp < 275 - 12 + randint(-predictAcc, predictAcc) or curY - temp >= 500)):
                        return True
                    elif (not isRightSide and (
                            curX < 400 - 12 + randint(-predictAcc, predictAcc) or curX > 700 + 12 + randint(-predictAcc, predictAcc) or
                            curY - temp < 275 - 12 + randint(-predictAcc, predictAcc) or curY - temp >= 500)):
                        return True
                elif ((curX < 100 - 12 + randint(-predictAcc, predictAcc) or curX > 700 + 12 + randint(-predictAcc, predictAcc) or
                       curY - temp < 50 - 12 + randint(-predictAcc, predictAcc) or curY - temp >= 500)):
                    return True
                return False