# Harry He - December 21 2020
# This program runs a computer game, similar to Wii Sports' Tennis
# Note you will need pygame to run the game

# Importing the assets needed to run the game
import pygame
import os
import math
import threading
import time
import sys
for path in sys.path:
    print(path)
from random import randint
from GAME import Racket
from GAME import Ball
from GAME import Player
from GAME import Enemy
from GAME import Menu
from GAME import Effects

# Initializing pygame
pygame.init()

# Initializing some of the variables needed to run the game
# General Game Information
isServing = True
isPlayerServe = True
isServeThrow = False
isRightSide = True
checkedFirst = False
checkedSecond = False
isPlayerTurn = True
clock = pygame.time.Clock()
quitted = False
waiting = False
isResetting = False
isSecondServe = False
isGameOver = False
winBarWidth = 0
gameState = 0
# 0 - Main Menu or Other Pages within Menu, includes title screen
# 1 - Game
playerScore = 0
compScore = 0
numRounds = 3
compRounds = 0
playerRounds = 0
backToMenuDelay = 300

# Ball Information
needReset = False
firstEntry = True
moveBall = False
changeX = 0
changeY = 0
totalX = 0
totalY = 0
averageSpeedX = 0
averageSpeedY = 0
mouseSensitivity = 0.2

# Racket & Player Information
isUp = False
isDown = False
isRight = False
isLeft = False
playerOuterRange = 175
playerInnerRange = 20
# Easy 175 outer 20 inner
# Medium 150 outer 20 inner
# Hard 100 outer 25 inner

# Enemy Information
serveDelay = randint(50, 200)

# Sounds
soundEffects = Effects()

# Initializing the window that the game will be run in
screen = pygame.display.set_mode((800, 1000))
pygame.display.set_caption('Game')

# Defining the tennis court that the game will
# be played on
tennis_court = pygame.Surface(screen.get_size())
tennis_court = tennis_court.convert()
tennis_court.fill((181, 66, 46))

# Tennis Court
# Drawing the tennis court onto the tennis_court surface
pygame.draw.rect(tennis_court, (50, 109, 60), pygame.Rect(100, 50, 600, 900))
pygame.draw.line(tennis_court, (255, 255, 255), (95, 50), (705, 50), 11)
pygame.draw.line(tennis_court, (255, 255, 255), (100, 50), (100, 950), 11)
pygame.draw.line(tennis_court, (255, 255, 255), (700, 50), (700, 950), 11)
pygame.draw.line(tennis_court, (255, 255, 255), (95, 950), (705, 950), 11)
pygame.draw.line(tennis_court, (255, 255, 255), (95, 275), (705, 275), 11)
pygame.draw.line(tennis_court, (255, 255, 255), (95, 725), (705, 725), 11)
pygame.draw.line(tennis_court, (255, 255, 255), (400, 275), (400, 725), 11)

# Net
# Drawing the net onto the tennis_court
pygame.draw.line(tennis_court, (0, 0, 0), (95, 500), (705, 500), 5)

# Score
# Initializing the scoreboard system
# Including the score, and the number or rounds each side has won
scoreBoard = pygame.Surface(screen.get_size())
scoreBoard = scoreBoard.convert()
for i in range(numRounds):
    pygame.draw.circle(scoreBoard, (255, 255, 255), (25, 55 + 50 * i), 25, width = 5)
for i in range(numRounds):
    pygame.draw.circle(scoreBoard, (255, 255, 255), (775, 55 + 50 * i), 25, width = 5)
scoreBoard.set_alpha(100)

wiiFont = pygame.font.Font("contm.ttf", 50)
scoreDisplay = wiiFont.render(str(playerScore) + " : " + str(compScore), True, (255, 255, 255))
scoreRect = scoreDisplay.get_rect(center=(400, 20))
screen.blit(scoreDisplay, scoreRect)

smallerWiiFont = pygame.font.Font("contm.ttf", 25)
playerScoreIcon = smallerWiiFont.render("Player", True, (255, 255, 255))
playerScoreRect = playerScoreIcon.get_rect(topleft=(0, 0))
compScoreIcon = smallerWiiFont.render("Computer", True, (255, 255, 255))
compScoreRect = compScoreIcon.get_rect(topright=(795, 0))

# Initializing the ending message strip
# This is the message that is shown after the game is over
endingFont = pygame.font.Font("contb.ttf", 100)
endMessage = endingFont.render("YOU WIN", True, (255, 215, 0))
endRect = endMessage.get_rect(center=(400, 500))
strip_screen = pygame.Surface(screen.get_size())
strip_screen = strip_screen.convert()
strip_screen.set_alpha(160)

# Defining the primary classes in the game. These
# classes are very important to the running of the game
# and make up a vast majority of content in the game.
# Go to each classes' own python file to
# see more specifics.
# The sprite class is a collection of all of the sprites
# in the game, which makes it easier to render them in the game.
menu = Menu()
tennis_racket = Racket((550, 950), 50)
tennis_ball = Ball((tennis_racket.pos[0], tennis_racket.pos[1] - 100))
player = Player((550, 950))
compEnemy = Enemy((250, 50))
enemy_racket = Racket((250, 50), 50)
sprites = pygame.sprite.RenderPlain(compEnemy, enemy_racket, tennis_ball, tennis_racket, player)

# The thread that handles all of the player interaction
# includes movement, hitting the ball, and checking for
# point conditions after a player hit.
class PlayerThread(threading.Thread):
    # This function just initializes the thread
    def __init__(self):
        threading.Thread.__init__(self)
    def run(self):
        # Getting all the variables needed in the player thread
        # This is almost every single variable in the game
        global clock
        global wiiFont
        global screen
        global quitted
        global waiting
        global isResetting
        global gameState
        global soundEffects

        global player
        global tennis_racket
        global tennis_ball
        global compEnemy
        global enemy_racket

        global isUp
        global isDown
        global isLeft
        global isRight
        global playerOuterRange
        global playerInnerRange

        global changeX
        global changeY
        global totalX
        global totalY
        global firstEntry
        global needReset
        global moveBall
        global averageSpeedX
        global averageSpeedY
        global mouseSensitivity

        global checkedFirst
        global checkedSecond
        global isPlayerTurn
        global playerScore
        global compScore
        global isRightSide
        global scoreDisplay
        global scoreRect
        global isServeThrow
        global isServing

        # This thread is run until the user chooses to
        # quit the entire program
        while not quitted:
            # This clock ensures that the game never runs
            # more than 120 frames per second.
            # This keeps updates standard and allows each thread to
            # run in sync.
            clock.tick(120)
            # This if statement makes it so that the
            # thread only ever runs during games
            if(gameState == 1):
                # The waiting variable makes it so that
                # after each point, there is a waiting period
                # so that the user can recognize that they have either won
                # or lost the last point.
                if(waiting):
                    time.sleep(1)
                    waiting = False

                # Whenever the game is not waiting, the player
                # and racket will move if the user presses
                # a movement key.
                if(not waiting):
                    if (isUp):
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
                # Whenever the user holds down the mouse button, the
                # program will recognize it as a new swing.
                # While the mouse is held, the program will track the movement
                # of the mouse to determine how fast the ball will move later.
                if (pygame.mouse.get_pressed()[0]):
                    if (firstEntry):
                        pygame.mouse.get_rel()
                        changeX = 0
                        changeY = 0
                        totalX = 0
                        totalY = 0
                        firstEntry = False
                    else:
                        twin = pygame.mouse.get_rel()
                        changeX += twin[0]
                        changeY += twin[1]
                        totalX += 1
                        totalY += 1
                # When the mouse is no longer pressed, the needReset variable
                # turns true and this if statement is activated
                elif (needReset):
                    # Checks if the player is close enough
                    # to hit the ball
                    if (tennis_ball.rect.centery > 500 and
                            abs(tennis_ball.rect.centerx - (tennis_racket.pos[0] + tennis_racket.offset.x)) <= playerOuterRange and
                            abs(tennis_ball.rect.centery - (tennis_racket.pos[1] + tennis_racket.offset.y)) <= playerOuterRange and
                            (abs(tennis_ball.rect.centerx - (tennis_racket.pos[0] + tennis_racket.offset.x)) >= playerInnerRange or
                             abs(tennis_ball.rect.centery - (tennis_racket.pos[1] + tennis_racket.offset.y)) >= playerInnerRange) and
                            tennis_ball.height >= 20 and tennis_ball.height <= 150):
                        # These temp variables check if the swing
                        # was too short, and there is zero seconds in
                        # the swing
                        temp1 = 0
                        temp2 = 0
                        try:
                            temp1 = (changeX / totalX) * mouseSensitivity
                            temp2 = (changeY / totalY) * mouseSensitivity
                        except(ZeroDivisionError):
                            print("Swung too fast, the ball phased through your racket")
                        # If the ball moves fast enough, the
                        # speed in which the ball moves is changed to the mouse's
                        # average speed and the ball is set to a point where it will
                        # start to go back up, like the ball has been hit
                        # into the air. Additionally a hit sound will be played.
                        # The if statement is to prevent accidental quick clicks
                        # that aren't the real swing
                        if (temp1 >= 1 or temp1 <= -1 or temp2 >= 1 or temp2 <= -1):
                            moveBall = True
                            averageSpeedX = (changeX / totalX) * mouseSensitivity
                            averageSpeedY = (changeY / totalY) * mouseSensitivity

                            soundEffects.hit.play()
                            checkedFirst = False
                            checkedSecond = False
                            isServeThrow = False
                            if(not isPlayerServe and not isResetting):
                                isServing = False

                            tennis_ball.directionX = 1
                            tennis_ball.directionY = 1
                            diffX = tennis_ball.rect.centerx - (tennis_racket.pos[0] + tennis_racket.offset.x) - 30
                            diffY = tennis_ball.rect.centery - (tennis_racket.pos[1] + tennis_racket.offset.y) - 30
                            distance = math.sqrt(diffX * diffX + diffY * diffY)
                            tennis_ball.bounceHeight = (int)(130 - 0.2 * distance) + (0.5 * tennis_ball.height)
                            tennis_ball.numBounces = 0
                            tennis_ball.isUp = True
                            tennis_ball.stop = False
                            isPlayerTurn = False
                    needReset = False
                    firstEntry = True
                # Player Hit Checks
                # If it is not the players turn,
                # (the player has just hit and
                # the receiver is now the computer, so its the computer's turn)
                # Then we need to check if the ball that they have hit is out
                # or if it has bounced twice, then we give points to the appropriate
                # side.
                if (not isPlayerTurn):
                    # Check if the ball is out on first bounce, and if serving
                    # see if the ball bounced in the appropriate serving box
                    # If the ball is out, then give points to the computer
                    if (not checkedFirst and tennis_ball.numBounces >= 1):
                        if (isServing):
                            if(not isResetting):
                                isServing = False
                            if(isRightSide and (tennis_ball.rect.centerx < 100 - 17 or tennis_ball.rect.centerx > 400 + 17 or
                                tennis_ball.rect.centery - tennis_ball.height < 275 - 17 or tennis_ball.rect.centery - tennis_ball.height >= 500) and not isResetting):
                                resetToServe(False, True)
                            elif(not isRightSide and (tennis_ball.rect.centerx < 400 - 17 or tennis_ball.rect.centerx > 700 + 17 or
                                tennis_ball.rect.centery - tennis_ball.height < 275 - 17 or tennis_ball.rect.centery - tennis_ball.height >= 500) and not isResetting):
                                resetToServe(False, True)
                        elif ((tennis_ball.rect.centerx < 100 - 17 or tennis_ball.rect.centerx > 700 + 17 or
                                tennis_ball.rect.centery - tennis_ball.height < 50 - 17 or tennis_ball.rect.centery - tennis_ball.height >= 500) and not isResetting):
                            resetToServe(False, False)
                        checkedFirst = True
                    # If the ball is on the second bounce, then just give points
                    # to the player if the ball is on the opponent's side or to the enemy
                    # if the ball is on the player's side.
                    # Note: The ball will most likely never bounce twice on the players side,
                    # because the first bounce would have considered it out already.
                    elif (not checkedSecond and checkedFirst and tennis_ball.numBounces >= 2):
                        if (tennis_ball.rect.centery + tennis_ball.height < 500 and not isResetting):
                            resetToServe(True, False)
                        elif(not isResetting):
                            resetToServe(False, False)
                        checkedSecond = True
                # Make the racket face the cursor
                tennis_racket.update()

# The thread that handles all of the ball movement
# and bouncing. This needs its own thread because the
# ball should not hitch in mid-air, and this thread
# ensures that the ball will move smoothly
class BallThread(threading.Thread):
    # This function just initializes the thread
    def __init__(self):
        threading.Thread.__init__(self)
    def run(self):
        # Getting the variables needed to run the ball thread
        # it is far less than the other threads
        global averageSpeedX
        global averageSpeedY
        global tennis_ball
        global moveBall
        global clock
        global quitted
        global isServeThrow
        global gameState
        global soundEffects

        # Thread runs until the player quits the game
        while not quitted:
            # This clock ensures that the ball isn't updated
            # a significant amount more times than other threads
            clock.tick(120)
            # The thread can only run in the game part of the program
            # and this if statement makes sure of that
            if(gameState == 1):
                # If the ball has stopped bouncing, the ball
                # will gradually roll to a stop
                if (tennis_ball.stop == True):
                    if ((abs(averageSpeedX) > 0.5 and abs(averageSpeedY) > 0.5)):
                        averageSpeedX *= 0.99
                        averageSpeedY *= 0.99
                    else:
                        moveBall = False
                # If the ball should move, then move the ball
                if (moveBall):
                    tennis_ball.move(averageSpeedX, averageSpeedY, soundEffects)
                # When the ball is thrown, make sure that it is only going
                # to go up and down once using the serveThrow method. Otherwise,
                # use the bounce method. To see the difference, see the BallClass.py
                # file
                if(isServeThrow):
                    tennis_ball.serveThrow()
                    if(tennis_ball.stop):
                        isServeThrow = False
                else:
                    tennis_ball.bounce(soundEffects)

# The thread that handles all of the enemy's hits,
# movement, and checks all of the point conditions
# after an enemy hit.
class EnemyThread(threading.Thread):
    # This function just initializes the thread
    def __init__(self):
        threading.Thread.__init__(self)
    def run(self):
        # Getting all of the variables needed to
        # run the enemy thread. It is also quite
        # a lot like the player thread.
        global clock
        global wiiFont
        global quitted
        global waiting
        global isResetting
        global gameState
        global soundEffects

        global enemy_racket
        global tennis_ball
        global compEnemy
        global player
        global tennis_racket

        global averageSpeedX
        global averageSpeedY
        global checkedFirst
        global checkedSecond
        global moveBall

        global scoreDisplay
        global scoreRect
        global isRightSide
        global isPlayerTurn
        global playerScore
        global compScore
        global isServing
        global isServeThrow
        global isPlayerServe
        global serveDelay

        # This thread will run until the
        # user quits the game
        while not quitted:
            # This clock ensures that the thread
            # will only run at a max frame rate of 120
            # This ensures that there are not too many
            # updates per second.
            clock.tick(120)

            # This thread only runs when the game
            # is in the game state.
            if(gameState == 1):
                # This makes the enemy wait a second
                # before playing the next point. This ensures
                # that the user has enough time to recognize
                # that the previous point is over, and that
                # it is new serve and point.
                if (waiting):
                    time.sleep(1)
                    waiting = False

                # Makes the racket face the ball
                enemy_racket.compUpdate(tennis_ball.rect.centerx, tennis_ball.rect.centery)
                # When it is not the players turn, the enemy will try to hit
                # the ball
                if (not isPlayerTurn):
                    # The enemy will run towards the ball or to a intercept point
                    # depending on the difficulty. See the EnemyClass.py file to see
                    # specifics on each difficulty's specifics.
                    enemyVelX, enemyVelY = compEnemy.run(tennis_ball, averageSpeedX, averageSpeedY)
                    # When the program is not waiting the 1 second
                    # the enemy and racket will move properly, but when it is
                    # waiting the enemy will stop
                    if(not waiting):
                        compEnemy.move(enemyVelX, enemyVelY)
                        enemy_racket.move(enemyVelX, enemyVelY)

                    # If the enemy is supposed to serve, then it will
                    # try to serve after a certain random serve delay
                    if(isServing and not isPlayerServe):
                        if(serveDelay <= 0 and tennis_ball.rect.centery + tennis_ball.height < 500 and
                                abs(tennis_ball.rect.centerx - (
                                        enemy_racket.pos[0] + enemy_racket.offset.x)) <= compEnemy.outerRange and
                                abs(tennis_ball.rect.centery - (
                                        enemy_racket.pos[1] + enemy_racket.offset.y)) <= compEnemy.outerRange and
                                (abs(tennis_ball.rect.centerx - (
                                        enemy_racket.pos[0] + enemy_racket.offset.x)) >= compEnemy.innerRange or
                                 abs(tennis_ball.rect.centery - (
                                         enemy_racket.pos[1] + enemy_racket.offset.y)) >= compEnemy.innerRange) and
                                tennis_ball.height >= 20 and tennis_ball.height <= 150):
                            # If the enemy is close enough it will try to serve
                            # the ball, otherwise, it is not in range.
                            # See EnemyClass.py for each difficulty's specifics

                            # The ball's speed will be changed to the values the
                            # enemy wants to hit it. The ball will also go back up
                            # imitating hitting the ball back into the air again.
                            # Additionally a hit sound will be played.
                            averageSpeedX, averageSpeedY = compEnemy.serve(isRightSide, tennis_ball, enemy_racket)
                            moveBall = True
                            isPlayerTurn = True
                            checkedFirst = False
                            checkedSecond = False
                            isServeThrow = False
                            soundEffects.hit.play()

                            tennis_ball.directionX = 1
                            tennis_ball.directionY = 1

                            diffX = tennis_ball.rect.centerx - (enemy_racket.pos[0] + enemy_racket.offset.x) - 30
                            diffY = tennis_ball.rect.centery - (enemy_racket.pos[1] + enemy_racket.offset.y) - 30
                            distance = math.sqrt(diffX * diffX + diffY * diffY)
                            tennis_ball.bounceHeight = (int)(130 - 0.2 * distance) + (0.5 * tennis_ball.height)
                            tennis_ball.numBounces = 0
                            tennis_ball.isUp = True
                            tennis_ball.stop = False
                        else:
                            # If the enemy is not in range or the serve delay is not over,
                            # it will continue throwing the ball into the air
                            # and making the serveDelay variable go down.
                            if(not isServeThrow and isServing and not isPlayerServe and not isPlayerTurn and not waiting):
                                isServeThrow = True
                                tennis_ball.setHeight(10)
                                tennis_ball.bounceHeight = 150
                                tennis_ball.stop = False
                                tennis_ball.isUp = True
                            serveDelay -= 1
                    else:
                        # This part of the if statement means that the
                        # enemy is not serving, but is still going to hit
                        # or receive the ball
                        if (tennis_ball.rect.centery < 500 and
                                abs(tennis_ball.rect.centerx - (
                                        enemy_racket.pos[0] + enemy_racket.offset.x)) <= compEnemy.outerRange and
                                abs(tennis_ball.rect.centery - (
                                        enemy_racket.pos[1] + enemy_racket.offset.y)) <= compEnemy.outerRange and
                                (abs(tennis_ball.rect.centerx - (
                                        enemy_racket.pos[0] + enemy_racket.offset.x)) >= compEnemy.innerRange or
                                 abs(tennis_ball.rect.centery - (
                                         enemy_racket.pos[1] + enemy_racket.offset.y)) >= compEnemy.innerRange) and
                                tennis_ball.height >= 20 and tennis_ball.height <= 150 and
                                not compEnemy.willGoOut(averageSpeedX, averageSpeedY, tennis_ball, isServing, isRightSide)):
                            # If the enemy is close enough it will try to hit
                            # the ball, otherwise, it is not in range or has decided
                            # that the ball will go out.
                            # See EnemyClass.py for each difficulty's specifics

                            # The ball's speed will be changed to the values the
                            # enemy wants to hit it. The ball will also go back up
                            # imitating hitting the ball back into the air again.
                            # Additionally a hit sound will be played.
                            averageSpeedX, averageSpeedY = compEnemy.hit(player.rect.centerx, player.rect.centery,
                                                                         tennis_ball, enemy_racket)

                            moveBall = True
                            isPlayerTurn = True
                            checkedFirst = False
                            checkedSecond = False
                            isServeThrow = False
                            soundEffects.hit.play()

                            tennis_ball.directionX = 1
                            tennis_ball.directionY = 1
                            diffX = tennis_ball.rect.centerx - (enemy_racket.pos[0] + enemy_racket.offset.x) - 30
                            diffY = tennis_ball.rect.centery - (enemy_racket.pos[1] + enemy_racket.offset.y) - 30
                            distance = math.sqrt(diffX * diffX + diffY * diffY)
                            tennis_ball.bounceHeight = (int)(130 - 0.2 * distance) + (0.5 * tennis_ball.height)
                            tennis_ball.numBounces = 0
                            tennis_ball.isUp = True
                            tennis_ball.stop = False

                            # When the computer hits a serve before its first
                            # bounce, the ball they hit back must be changed
                            # to a non-serve hit, normally after the first
                            # bounce, it already does this.
                            # This ensures that it won't be considered out when it isn't
                            # in the serving box.
                            if (isPlayerServe and not isResetting):
                                isServing = False
                elif(not isServing and isPlayerTurn):
                    # This part of the if statement means that the
                    # enemy has just hit and must relocate to a better
                    # spot to receive the player's hit. The exception is
                    # that whenever the player is serving the computer doesn't
                    # have to move which is why we add the condition above.
                    # See EnemyClass.py to see each difficulty's specifics
                    enemyVelX, enemyVelY = compEnemy.relocate()
                    compEnemy.move(enemyVelX, enemyVelY)
                    enemy_racket.move(enemyVelX, enemyVelY)

                # When it is the player's turn to receive, the enemy has just hit
                # and this thread must check for point conditions. Ex. The
                # ball is out or the ball bounced twice.
                if(isPlayerTurn):
                    # Check if the ball is out on first bounce, and if serving
                    # see if the ball bounced in the appropriate serving box
                    # If the ball is out, then give points to the computer
                    if (not checkedFirst and tennis_ball.numBounces >= 1):
                        if (isServing):
                            if(not isResetting):
                                isServing = False
                            if (isRightSide and (tennis_ball.rect.centerx < 400 - 12 or tennis_ball.rect.centerx > 700 + 12 or
                                    tennis_ball.rect.centery + tennis_ball.height > 725 + 12 or tennis_ball.rect.centery + tennis_ball.height <= 500) and not isResetting):
                                resetToServe(True, True)
                            elif (not isRightSide and (tennis_ball.rect.centerx < 100 - 12 or tennis_ball.rect.centerx > 400 + 12 or
                                  tennis_ball.rect.centery + tennis_ball.height > 725 + 12 or tennis_ball.rect.centery + tennis_ball.height <= 500) and not isResetting):
                                resetToServe(True, True)
                        elif ((tennis_ball.rect.centerx < 100 - 12 or tennis_ball.rect.centerx > 700 + 12 or
                                tennis_ball.rect.centery + tennis_ball.height > 950 + 12 or tennis_ball.rect.centery + tennis_ball.height <= 500) and not isResetting):
                            resetToServe(True, False)
                        checkedFirst = True
                    # If the ball is on the second bounce, then just give points
                    # to the computer if the ball is on the player's side or to the player
                    # if the ball is on the computer's side.
                    # Note: The ball will most likely never bounce twice on the computer's side,
                    # because the first bounce would have considered it out already.
                    elif (not checkedSecond and checkedFirst and tennis_ball.numBounces >= 2):
                        if (tennis_ball.rect.centery + tennis_ball.height >= 500 and not isResetting):
                            resetToServe(False, False)
                        elif(not isResetting):
                            resetToServe(True, False)
                        checkedSecond = True

# A function to reset the ball, player, enemy and their
# rackets to a serving state.
def resetToServe(isPlayerWin, isServeHit):
    # Getting the variables needed to run the method
    global compScore
    global playerScore
    global scoreDisplay
    global scoreRect
    global isRightSide
    global isPlayerTurn
    global tennis_ball
    global tennis_racket
    global player
    global compEnemy
    global enemy_racket
    global averageSpeedX
    global averageSpeedY
    global isPlayerServe
    global isServing
    global isServeThrow
    global waiting
    global serveDelay
    global isResetting
    global isSecondServe
    global checkedFirst
    global checkedSecond
    global soundEffects
    global firstEntry

    # The isResetting variable ensures that when this method
    # is running the main thread will not show the sprites
    # If it shows the sprites while this is running, pygame
    # will throw an error.
    isResetting = True
    # When the game is not over, we give points. This ensures
    # that we don't update the score after the winning or
    # losing message shows up.
    if(not isGameOver):
        # When the player wins, we check if the ball was
        # a first serve, and if so we make the ball go back
        # and the server must try a second serve.
        # The points are also given in order: 15, 30, 40 then add 10
        # after. Technically in tennis they use Adv. and Deuce when both
        # the player and the computer are above 40, but this allows the program
        # to check who has won the round easier
        if(isPlayerWin):
            if (playerScore < 30):
                if((isServeHit and isSecondServe) or not isServeHit):
                    playerScore += 15
                    isSecondServe = False
                    soundEffects.score.play()
                else:
                    isSecondServe = True
            else:
                if ((isServeHit and isSecondServe) or not isServeHit):
                    playerScore += 10
                    isSecondServe = False
                    soundEffects.score.play()
                else:
                    isSecondServe = True
        # Then when the computer wins, we give points to the computer
        else:
            if (compScore < 30):
                if ((isServeHit and isSecondServe) or not isServeHit):
                    compScore += 15
                    isSecondServe = False
                    soundEffects.score.play()
                else:
                    isSecondServe = True
            else:
                if ((isServeHit and isSecondServe) or not isServeHit):
                    compScore += 10
                    isSecondServe = False
                    soundEffects.score.play()
                else:
                    isSecondServe = True

    # This section of if statements are so that whenever a
    # round is won, the serving side resets back to the right side
    # If a round is not won, then the serving side just switches
    # as long as it is not a second serve next.
    if (playerScore > 40 and compScore < 40 and not waiting):
        isPlayerServe = not isPlayerServe
        isRightSide = True
    elif (playerScore > 40 and compScore >= 40 and playerScore - compScore >= 20 and not waiting):
        isPlayerServe = not isPlayerServe
        isRightSide = True
    elif (compScore > 40 and playerScore < 40 and not waiting):
        isPlayerServe = not isPlayerServe
        isRightSide = True
    elif (compScore > 40 and playerScore >= 40 and compScore - playerScore >= 20 and not waiting):
        isPlayerServe = not isPlayerServe
        isRightSide = True
    elif (not isSecondServe):
        isRightSide = not isRightSide

    # Updating the score board to show the new score
    scoreDisplay = wiiFont.render(str(playerScore) + " : " + str(compScore), True, (255, 255, 255))
    scoreRect = scoreDisplay.get_rect(center=(400, 20))
    # If it is the player's turn to serve, the ball
    # will be given them. Otherwise, the ball will be
    # given to the computer
    if(isPlayerServe):
        isPlayerTurn = True
    else:
        isPlayerTurn = False
        serveDelay = randint(50, 200)
    # Resetting the game to know that the
    # next hit is a serve, and that the
    # ball should checked for bounces
    isServing = True
    isServeThrow = False
    checkedFirst = False
    checkedSecond = False

    # This will move all of the sprites
    # to their appropriate position for the next serve
    if (isRightSide):
        player.move(550 - player.rect.centerx, 950 - player.rect.centery)
        compEnemy.move(250 - compEnemy.rect.centerx, 50 - compEnemy.rect.centery)
        tennis_racket.setAngle(0)
        tennis_racket.move(550 - tennis_racket.pos[0], 950 - tennis_racket.pos[1])
        enemy_racket.setAngle(0)
        enemy_racket.move(250 - enemy_racket.pos[0], 50 - enemy_racket.pos[1])
    else:
        player.move(250 - player.rect.centerx, 950 - player.rect.centery)
        compEnemy.move(550 - compEnemy.rect.centerx, 50 - compEnemy.rect.centery)
        tennis_racket.setAngle(0)
        tennis_racket.move(250 - tennis_racket.pos[0], 950 - tennis_racket.pos[1])
        enemy_racket.setAngle(0)
        enemy_racket.move(550 - enemy_racket.pos[0], 50 - enemy_racket.pos[1])
    if(isPlayerServe):
        tennis_ball.set_pos((tennis_racket.pos[0], tennis_racket.pos[1] - 100))
    else:
        tennis_ball.set_pos((enemy_racket.pos[0], enemy_racket.pos[1] + 100))

    # Resetting all the variables to be set
    # to a serving state, where they are not moving
    # and the computer is not longer tired
    averageSpeedX = 0
    averageSpeedY = 0
    firstEntry = True
    tennis_ball.reset()
    if (soundEffects.isPlayingNet):
        soundEffects.isPlayingNet = False
    compEnemy.fatigue = 0
    # Then we make the threads wait a little
    # and allow the main thread to display the
    # sprites again.
    waiting = True
    isResetting = False

# This function helps to convert keys
# that the user has pressed into a string
# which we can put over a button
def eventToString(event):
    if(event.key >= 97 and event.key <= 122):
        return chr(event.key).upper()
    elif(event.key >= 48 and event.key <= 57):
        return chr(event.key)
    elif(event.key == 32):
        return "SPACE"
    elif (event.key == 1073742049):
        return "LSHIFT"
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

# Initializing and starting up the thread
thread1 = PlayerThread()
thread2 = BallThread()
thread3 = EnemyThread()

thread1.start()
thread2.start()
thread3.start()

# Making the game run until the user quits
while(not quitted):
    # Ensures that the game will run at a max
    # 120 frames per second, so that there is
    # not too many updates. If you have a higher
    # monitor frame rate, this game is too simple
    # for you.
    clock.tick(120)
    # print(clock.get_fps())
    # Inputting
    # Checking the user's inputs and responding to thier
    # inputs
    for event in pygame.event.get():
        # If the user quits the game
        # we don't exit the program immediately
        # but the the other threads to quit
        # by using the quitted variable
        if event.type == pygame.QUIT:
            quitted = True
        # The user has clicked down
        elif(event.type == pygame.MOUSEBUTTONDOWN):
            # If the program is currently on the menu
            if(gameState == 0):
                # We get the position of the mouse clicked position
                # and see what page the user is on
                mousePos = pygame.mouse.get_pos()
                # Main Menu
                if (menu.curPage == 0):
                    # Check for all of the buttons
                    # and direct them to the appropriate
                    # page, or quit the game
                    for i in range(len(menu.mainButtons)):
                        if (menu.mainButtons[i].collision(mousePos)):
                            if(i == 4):
                                quitted = True
                            else:
                                soundEffects.select.play()
                                menu.prevPages.append(menu.curPage)
                                menu.curPage = i + 1
                            break
                # Play
                elif(menu.curPage == 1):
                    # Check if the user pressed for all the buttons
                    # and do the what the button is supposed to do
                    for i in range(len(menu.playButtons)):
                        if (menu.playButtons[i].collision(mousePos)):
                            # If the user clicks on the serving
                            # customizations, make either the computer
                            # or player serve, depending on the button
                            # pressed and highlight that button. Then
                            # unclick the other button to make the
                            # user able to recognize their choice has changed
                            if(i < 2):
                                soundEffects.select.play()
                                menu.serveSelected = True
                                menu.playButtons[i].isClicked = True
                                if(i == 0):
                                    isPlayerServe = False
                                else:
                                    isPlayerServe = True
                                for j in range(2):
                                    if(j != i):
                                        menu.playButtons[j].isClicked = False
                            # If the user clicks on the difficulty
                            # customizations, change the difficulty and
                            # range of the player depending on the button
                            # pressed and highlight that button. Then
                            # unclick the other button to make the
                            # user able to recognize their choice has changed
                            elif(i < 5):
                                soundEffects.select.play()
                                compEnemy.difficulty = i - 1
                                if(i - 1 == 1):
                                    playerOuterRange = 175
                                    playerInnerRange = 20
                                elif(i - 1 == 2):
                                    playerOuterRange = 150
                                    playerInnerRange = 20
                                else:
                                    playerOuterRange = 125
                                    playerInnerRange = 25
                                menu.difficultySelected = True
                                menu.playButtons[i].isClicked = True
                                for j in range(2, 5):
                                    if(j != i):
                                        menu.playButtons[j].isClicked = False
                            # If the user clicks on the rounds
                            # customizations, change the number or rounds
                            # required to win depending on the button
                            # pressed and highlight that button. Then
                            # unclick the other button to make the
                            # user able to recognize their choice has changed
                            elif (i < 8):
                                soundEffects.select.play()
                                numRounds = i - 4
                                menu.roundsSelected = True
                                menu.playButtons[i].isClicked = True
                                for j in range(5, 8):
                                    if (j != i):
                                        menu.playButtons[j].isClicked = False
                            # If the user presses on the back button go back
                            # to the previous page.
                            elif(i == 8):
                                menu.curPage = menu.prevPages.pop()
                                soundEffects.back.play()
                            # If the user presses on the play button
                            else:
                                if(menu.serveSelected and menu.difficultySelected and menu.roundsSelected):
                                    # Change the game to a new game
                                    # Make all of the scores reset to 0,
                                    # make either the player serve or the
                                    # computer, move everything to a serving
                                    # state with the resetToServe, reset
                                    # the scores and rounds won, make the game's
                                    # ending message go away, play the start sound
                                    # and change the program to the game part.
                                    isRightSide = True
                                    playerScore = 0
                                    compScore = 0
                                    if(menu.playButtons[0].isClicked):
                                        isPlayerServe = False
                                    else:
                                        isPlayerServe = True
                                    isGameOver = False
                                    isSecondServe = False
                                    # You set the second serve to be false and the
                                    # serving to be true to avoid
                                    # playing the ding sound when the game starts
                                    resetToServe(True, True)
                                    isSecondServe = False
                                    firstEntry = True
                                    winBarWidth = 0
                                    playerScore = 0
                                    compScore = 0
                                    scoreDisplay = wiiFont.render(str(playerScore) + " : " + str(compScore), True,
                                                                  (255, 255, 255))
                                    scoreRect = scoreDisplay.get_rect(center=(400, 20))
                                    playerRounds = 0
                                    compRounds = 0
                                    backToMenuDelay = 300
                                    scoreBoard = pygame.Surface(screen.get_size())
                                    scoreBoard = scoreBoard.convert()
                                    for i in range(numRounds):
                                        pygame.draw.circle(scoreBoard, (255, 255, 255), (25, 55 + 50 * i), 25, width=5)
                                    for i in range(numRounds):
                                        pygame.draw.circle(scoreBoard, (255, 255, 255), (775, 55 + 50 * i), 25, width=5)
                                    scoreBoard.set_alpha(255)
                                    soundEffects.isPlayingWinLose = False
                                    soundEffects.start.play()
                                    gameState = 1
                # How to Play
                elif (menu.curPage == 2):
                    # If the user presses the back button go back
                    # to the previous page
                    if (menu.backButton.collision(mousePos)):
                        menu.curPage = menu.prevPages.pop()
                        soundEffects.back.play()
                # Controls
                elif(menu.curPage == 3):
                    # If the user has clicked, make the pressed
                    # button variable false, until you know they did
                    # press a button. Then check, if they pressed
                    # a button, make it selected, unhighlight
                    # the other buttons, make the pressed button
                    # variable true, and play the select sound.
                    # If they pressed the back button, play
                    # the back sound and go back. When they
                    # don't press a button, make all of the
                    # buttons deselected.
                    pressedButton = False
                    for i in range(len(menu.controlButtons)):
                        if (menu.controlButtons[i].collision(mousePos)):
                            if (i < 6):
                                menu.controlButtons[i].isClicked = True
                                pressedButton = True
                                soundEffects.select.play()
                                for j in range(6):
                                    if (j != i):
                                        menu.controlButtons[j].isClicked = False
                            elif (i == 6):
                                for j in range(6):
                                    menu.controlButtons[j].isClicked = False
                                menu.curPage = menu.prevPages.pop()
                                soundEffects.back.play()
                    if (not pressedButton):
                        for j in range(5):
                            menu.controlButtons[j].isClicked = False
                # About
                elif(menu.curPage == 4):
                    # If the user presses the back button go back
                    # to the previous page
                    if (menu.backButton.collision(mousePos)):
                        menu.curPage = menu.prevPages.pop()
                        soundEffects.back.play()
                # Pause
                elif(menu.curPage == 5):
                    for i in range(len(menu.pauseButtons)):
                        if (menu.pauseButtons[i].collision(mousePos)):
                            # If the user presses the resume button
                            # put the game back into play
                            # If the user presses the controls
                            # button, put go to the controls screen
                            # If the user presses the main menu
                            # button go to the main menu
                            soundEffects.select.play()
                            if(i == 0):
                                gameState = 1
                            elif(i == 1):
                                gameState = 0
                                menu.prevPages.append(menu.curPage)
                                menu.curPage = 3
                            else:
                                gameState = 0
                                menu.curPage = menu.prevPages.pop()
        # When the user stops pressing the mouse button
        # if the game is currently being played, make the
        # swing complete and the player thread will later
        # try to hit the ball
        elif (event.type == pygame.MOUSEBUTTONUP):
            if(gameState == 1):
                needReset = True
        elif (event.type == pygame.KEYDOWN):
            # If the user presses down a key
            # check the current state of the game
            # and do accordingly

            # Menu
            if(gameState == 0):
                # If the user is in the controls page
                if (menu.curPage == 3):
                    # If they have selected a control to change
                    # If the key the pressed is escape, deselect the control
                    # Otherwise see if the control can be valid as a control
                    # through the eventToString method and is not already
                    # another control. If it can be registered change the
                    # control the user selected. Otherwise don't do anything.
                    for i in range(len(menu.controlButtons) - 1):
                        if (menu.controlButtons[i].isClicked):
                            if (event.key == pygame.K_ESCAPE):
                                menu.controlButtons[i].isClicked = False
                            else:
                                alreadyExists = False
                                for exist in menu.eventControls:
                                    if (event.key == exist.key):
                                        alreadyExists = True
                                buttonCaption = eventToString(event)
                                if (not alreadyExists and buttonCaption != ""):
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
            # Game
            elif(gameState == 1):
                # When the user presses a movement key set their
                # corresponding movement variable to True, so that the
                # player thread will later move the player
                if (event.key == menu.eventControls[2].key):
                    isUp = True
                elif (event.key == menu.eventControls[3].key):
                    isDown = True
                elif (event.key == menu.eventControls[0].key):
                    isLeft = True
                elif (event.key == menu.eventControls[1].key):
                    isRight = True
                elif (event.key == menu.eventControls[4].key):
                    # print(str(isServing) + " " + str(not isServeThrow) + " " + str(not waiting) + " " + str(isPlayerServe))
                    if(isServing and not isServeThrow and not waiting and isPlayerServe):
                        isServeThrow = True
                        tennis_ball.setHeight(10)
                        tennis_ball.bounceHeight = 150
                        tennis_ball.stop = False
                        tennis_ball.isUp = True
                # If the user presses their pause key, pause the game
                elif (event.key == menu.eventControls[5].key):
                    gameState = 0
                    menu.curPage = 5
        elif (event.type == pygame.KEYUP):
            if(gameState == 0):
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
            # When the user stops pressing a movement key set their
            # corresponding movement variable to False, so that the
            # player thread will stop moving the player in that direction
            else:
                if (event.key == menu.eventControls[2].key):
                    isUp = False
                elif (event.key == menu.eventControls[3].key):
                    isDown = False
                elif (event.key == menu.eventControls[0].key):
                    isLeft = False
                elif (event.key == menu.eventControls[1].key):
                    isRight = False

    # When the game is currently in the menu state
    if(gameState == 0):
        # Loop the Wii Theme
        if(not soundEffects.isPlayingTheme):
            soundEffects.playTheme()
        # Get the mouse position
        mousePos = pygame.mouse.get_pos()
        # When in a certain page
        # check if the buttons are hovered over
        # or selected and make them highlighted.
        # Otherwise, just make them their original
        # state. This is the code for all of the
        # pages. Look in each if statement to see
        # if there is any additional functionality
        # Main Menu
        if (menu.curPage == 0):
            # In addition to highlighting the buttons
            # the menu will change its current slide
            # to the last highlighted button.
            for i in range(len(menu.mainButtons)):
                if (menu.mainButtons[i].collision(mousePos)):
                    menu.mainButtons[i].highlight()
                    menu.curSlide = i
                else:
                    menu.mainButtons[i].unhighlight()
                menu.mainButtons[i].sizeify()
        # Play
        elif (menu.curPage == 1):
            # Here we make sure to highlight both hovered over
            # buttons, and selected ones so that the user can
            # see what they chose
            for i in range(len(menu.playButtons)):
                if (menu.playButtons[i].collision(mousePos) or menu.playButtons[i].isClicked):
                    menu.playButtons[i].highlight()
                elif (not menu.playButtons[i].isClicked):
                    menu.playButtons[i].unhighlight()
                menu.playButtons[i].sizeify()
        # How to Play
        elif (menu.curPage == 2):
            if (menu.backButton.collision(mousePos)):
                menu.backButton.highlight()
            else:
                menu.backButton.unhighlight()
            menu.backButton.sizeify()
        # Controls
        elif (menu.curPage == 3):
            # Here we also make sure to highlight both hovered over
            # buttons, and selected ones
            # We also change the mouse sensitivity here to the
            # notches current position in relation to the big rectangle
            # the notch is in
            mouseSensitivity = round((menu.mouseSensNotch.centerx - 94) / 600, 2)
            for i in range(len(menu.controlButtons)):
                if (menu.controlButtons[i].collision(mousePos) or menu.controlButtons[i].isClicked):
                    menu.controlButtons[i].highlight()
                elif (not menu.controlButtons[i].isClicked):
                    menu.controlButtons[i].unhighlight()
                menu.controlButtons[i].sizeify()
        # About
        elif (menu.curPage == 4):
            if (menu.backButton.collision(mousePos)):
                menu.backButton.highlight()
            else:
                menu.backButton.unhighlight()
            menu.backButton.sizeify()
        # Pause
        elif(menu.curPage == 5):
            for i in range(len(menu.pauseButtons)):
                if (menu.pauseButtons[i].collision(mousePos)):
                    menu.pauseButtons[i].highlight()
                else:
                    menu.pauseButtons[i].unhighlight()
                menu.pauseButtons[i].sizeify()
        # Title Screen
        else:
            # In the title page, if the user presses both the
            # a and d key at the same time, the menu will count down
            # a delay and go to the main menu
            if (menu.keyDisplays[1].isPressed and menu.keyDisplays[3].isPressed):
                if (menu.mainMenuDelay <= 0):
                    soundEffects.select.play()
                    menu.curPage = 0
                    menu.prevPages = []
                else:
                    menu.mainMenuDelay -= 1
            else:
                menu.mainMenuDelay = 50
    else:
        # Here the program is in the game state
        # We stop the Wii Theme from playing
        if (soundEffects.isPlayingTheme):
            soundEffects.stopTheme()
        # Check if the player or computer has won the round
        # or entire match, and does appropriately
        if((playerScore > 40 and compScore < 40) or (playerScore > 40 and compScore >= 40 and playerScore - compScore >= 20)):
            # The player has won
            if (not waiting):
                # After the waiting time, we draw a new circle
                # on the player's side to represent their won
                # a round
                pygame.draw.circle(scoreBoard, (235, 229, 52), (25, 55 + 50 * playerRounds), 20)
                playerRounds += 1
                playerScore = 0
                compScore = 0
                scoreDisplay = wiiFont.render(str(playerScore) + " : " + str(compScore), True, (255, 255, 255))
                scoreRect = scoreDisplay.get_rect(center=(400, 20))
                soundEffects.isPlayingDing = False
            # Check if the player has won enough rounds
            # to win the match, if so it displays the ending message
            # and makes the game over.
            if (playerRounds >= numRounds):
                isGameOver = True
                endMessage = endingFont.render("YOU WIN", True, (255, 215, 0))
                endRect = endMessage.get_rect(center=(400, 500))
            # When the player has not won, we play a dinging sound to
            # indicate that a round is over
            elif(playerRounds + 1 < numRounds and playerScore):
                if(not soundEffects.isPlayingDing):
                    soundEffects.ding.play()
                    soundEffects.isPlayingDing = True
        elif((compScore > 40 and playerScore < 40) or (compScore > 40 and playerScore >= 40 and compScore - playerScore >= 20)):
            # The computer has won
            if (not waiting):
                # After the waiting time, we draw a new circle
                # on the computer's side to represent their won
                # a round
                pygame.draw.circle(scoreBoard, (235, 229, 52), (775, 55 + 50 * compRounds), 20)
                compRounds += 1
                playerScore = 0
                compScore = 0
                scoreDisplay = wiiFont.render(str(playerScore) + " : " + str(compScore), True, (255, 255, 255))
                scoreRect = scoreDisplay.get_rect(center=(400, 20))
                soundEffects.isPlayingDing = False
            # Check if the computer has won enough rounds
            # to win the match, if so it displays the ending message
            # and makes the game over.
            if (compRounds >= numRounds):
                isGameOver = True
                endMessage = endingFont.render("YOU LOSE", True, (115, 36, 131))
                endRect = endMessage.get_rect(center=(400, 500))
            # When the computer has not won, we play a dinging sound to
            # indicate that a round is over
            elif(compRounds + 1 < numRounds):
                if (not soundEffects.isPlayingDing and compScore > 40):
                    soundEffects.ding.play()
                    soundEffects.isPlayingDing = True

    # Whenever the window is not locked
    # we display our images, this ensures
    # that we don't try displaying images
    # before the screen has been fully set up
    if(not screen.get_locked()):
        # If the program is in the menu state
        # we render the menu, see MenuClass.py to see
        # specifics on how we render the menu
        if(gameState == 0):
            menu.render(screen)
        # If the program is in the game state, we
        # render the game, with its sprites, shadows
        # and tennis court.
        elif(gameState == 1):
            screen.blit(tennis_court, (0, 0))
            screen.blit(scoreDisplay, scoreRect)
            screen.blit(playerScoreIcon, playerScoreRect)
            screen.blit(compScoreIcon, compScoreRect)
            screen.blit(scoreBoard, (0, 0))
            screen.blit(tennis_ball.shadow_screen, (0, 0))
            screen.blit(player.shadow_screen, (0, 0))
            screen.blit(compEnemy.shadow_screen, (0, 0))
            sprites.draw(screen)
            # When the game is over, we play the winning or losing
            # sound effect, depending on who won, and we put
            # up a strip with a ending message on it, either "You Win"
            # or "You Lose"
            if(isGameOver):
                if(not soundEffects.isPlayingWinLose):
                    soundEffects.isPlayingWinLose = True
                    if(compRounds > playerRounds):
                        soundEffects.losing.play()
                    else:
                        soundEffects.winning.play()
                if(winBarWidth < 800):
                    strip_screen = pygame.Surface(screen.get_size())
                    strip_screen.set_alpha(200)
                    winBarWidth += 20
                    pygame.draw.rect(strip_screen, (255, 255, 255), pygame.Rect(0, 425, winBarWidth, 150))
                screen.blit(strip_screen, (0, 0))
                # When ever the strip is at full length
                # we can then display the message. This
                # ensures that the message isn't randomly
                # hanging outside of the strip.

                # If the back to menu delay is zero, we go back
                # to the menu, otherwise we continue counting
                # down to make the delay back down to 0.
                if(winBarWidth >= 800):
                    screen.blit(endMessage, endRect)
                    if(backToMenuDelay <= 0):
                        menu.curPage = 0
                        menu.prevPages.clear()
                        menu.prevPages.append(0)
                        gameState = 0
                    else:
                        backToMenuDelay -= 1
        # Updating the window at the end.
        # This helps prevent flickering.
        pygame.display.flip()

# When the user has quit, pygame
# will close and the program will exit
pygame.display.quit()
pygame.quit()
raise SystemExit()

