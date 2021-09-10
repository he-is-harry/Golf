# Harry He - December 30 2020
# This file allows you to display the tennis court without anything on it
# go to the Main.py file to see the primary code and to run the game.
# Note: This class is primarily for testing and doesn't provide any
# code in the actual game.

# Importing pygame so we can display our court
import pygame as pg

# Initializing pygame and the window
pg.init()
screen = pg.display.set_mode((800, 1000))
pg.display.set_caption('Courts')

# Setting up the surface which the tennis court will be drawn
tennis_court = pg.Surface(screen.get_size())
tennis_court = tennis_court.convert()
tennis_court.fill((181, 66, 46))
# Drawing the tennis court
pg.draw.rect(tennis_court, (50, 109, 60), pg.Rect(100, 50, 600, 900))
pg.draw.line(tennis_court, (255, 255, 255), (95, 50), (705, 50), 11)
pg.draw.line(tennis_court, (255, 255, 255), (100, 50), (100, 950), 11)
pg.draw.line(tennis_court, (255, 255, 255), (700, 50), (700, 950), 11)
pg.draw.line(tennis_court, (255, 255, 255), (95, 950), (705, 950), 11)

pg.draw.line(tennis_court, (255, 255, 255), (95, 275), (705, 275), 11)
pg.draw.line(tennis_court, (255, 255, 255), (95, 725), (705, 725), 11)
pg.draw.line(tennis_court, (255, 255, 255), (400, 275), (400, 725), 11)

pg.draw.line(tennis_court, (0, 0, 0), (95, 500), (705, 500), 5)

# Displaying the tennis court until the user quits the window
while(True):
    for event in pg.event.get():
        if(event.type == pg.QUIT):
            raise SystemExit("Quitted Successfully")

    screen.blit(tennis_court, (0, 0))
    pg.display.flip()