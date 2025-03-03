# Reel It In

Reel It In is a top-down style game in which you control a fishing boat. Your goal is to catch all the fish and return them to the dock to allow you through to the whirlpool portal that teleports you to the next level. Sections of the islands are blocked off by locks, which can be unlocked by collecting the correct-coloured key.

## Running the game
1. Open the root folder of the project in your IDE (VS Code or IntelliJ recommended)
2. Add both libraries located in `LarryCroftsAdventures/lib` to the class path
3. Compile and run the project using the main method located in `LarryCroftsAdventures/src/nz/ac/wgtn/swen225/lc/app/main/Main.java`

## Recording and replaying the game
The game has the ability to record and replay game sessions.

Press **Record** to start recording the game and decide when you want to stop.

Press **Load recorded game** to load in a recorded game, traversing through the gameplay with the **Step by step** and **Auto replay** buttons. 

## Game controls
CTRL + X: exit the game, the current game state will be lost, the next time the game is started, it will resume from the last unfinished level

CTRL + S: exit the game, saves the game state, game will resume next time the application will be started

CTRL + R: resume a saved game (this will pop up a file selector to select a saved game to be loaded)

CTRL + 1: start a new game at level 1

CTRL + 2: start a new game at level 2

SPACE: pause the game and display a **Game is paused** dialog

ESC: close the **Game is paused** dialog and resume the game

UP, DOWN, LEFT, RIGHT ARROWS or W, D, A, S: move the boat within the maze
