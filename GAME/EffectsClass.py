# Harry He - January 19 2021
# This python file houses the effects class, go to the Main.py
# file to see the primary code and to run the game
# This class is a collection of all of the sounds in the game
# it also contains a function to allow the main theme to be looped,
# paused, and unpaused

# Importing some of the assets needed to run the class
import os
import pygame
from pygame.compat import geterror

# This variable stores the path in which the game is currently
# in. This is used to load sounds later.
main_dir = os.path.split(os.path.abspath(__file__))[0]

# A helper function to load sounds based on filename
# Since the effects class is a collection of sounds, we use
# this method often in the initialization.
def load_sound(name):
    class NoneSound:
        def play(self):
            pass

    if not pygame.mixer or not pygame.mixer.get_init():
        return NoneSound()
    fullname = os.path.join(main_dir, name)
    try:
        sound = pygame.mixer.Sound(fullname)
    except pygame.error:
        print("Cannot load sound: %s" % fullname)
        raise SystemExit(str(geterror()))
    return sound

# The Effects Class
# This class is helpful to play the sounds in the game, the class
# also holds every sound that will be used in the game

# The init function loads up all of the sounds, sets some quieter,
# and also loads variables used to ensure that a sound is not played
# multiple times in the same instance
# See some of the times when sounds are played and these variables are used
# in the BallClass and Main python files

# The playTheme function plays the theme with a tag that makes it loop
# forever. If it is the first time playing the theme, the program will
# queue in the theme music, otherwise it will just unpause the music

# The stopTheme function will stop the music from playing. However, it
# doesn't end it entirely, but rather just pauses it allowing the current
# progress in the song to be kept.
class Effects:
    def __init__(self):
        pygame.mixer.music.load("WiiTheme.wav")
        pygame.mixer.music.set_volume(0.15)
        self.ding = load_sound("DingSounds.wav")
        self.hit = load_sound("HitSound.wav")
        self.net = load_sound("NetSound.wav")
        self.winning = load_sound("WinningSound.wav")
        self.losing = load_sound("LosingSound.wav")
        self.back = load_sound("BackSound.wav")
        self.back.set_volume(0.2)
        self.select = load_sound("SelectSound.wav")
        self.select.set_volume(0.5)
        self.score = load_sound("ScoreSound.wav")
        self.start = load_sound("StartSound.wav")
        self.bounce = load_sound("BounceSound.wav")
        self.isPlayingTheme = False
        self.isPlayingWinLose = False
        self.isPlayingNet = False
        self.isPlayingDing = False
        self.firstTime = True
    def playTheme(self):
        self.isPlayingTheme = True
        if(self.firstTime):
            pygame.mixer.music.queue("WiiTheme.wav")
            pygame.mixer.music.play(-1)
            self.firstTime = False
        else:
            pygame.mixer.music.unpause()
    def stopTheme(self):
        self.isPlayingTheme = False
        pygame.mixer.music.pause()