package nz.ac.wgtn.swen225.lc.app.input;

import nz.ac.wgtn.swen225.lc.app.App;
import nz.ac.wgtn.swen225.lc.app.RecorderPanel;
import nz.ac.wgtn.swen225.lc.domain.Chap;
import nz.ac.wgtn.swen225.lc.domain.IllegalMove;
import nz.ac.wgtn.swen225.lc.renderer.Renderer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * KeyboardInputHandler is a class responsible for handling keyboard input in the Larry Croft's Adventures game.
 * It sets up key bindings and actions for various game-related controls and interactions.
 */

public class KeyboardInputHandler {

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    private static final String MOVE_UP = "move up";
    private static final String MOVE_DOWN = "move down";
    private static final String MOVE_LEFT = "move left";
    private static final String MOVE_RIGHT = "move right";

    private static final String EXIT_GAME = "exit game";
    private static final String SAVE_GAME = "save game";
    private static final String RESUME_GAME = "resume game";
    private static final String START_LEVEL_1 = "start level 1";
    private static final String START_LEVEL_2 = "start level 2";
    private static final String PAUSE_GAME = "pause game";
    private static final String CLOSE_PAUSE_DIALOG = "close pause dialog";



    private App component;



    /**
     * Constructs a KeyboardInputHandler for the specified component.
     *
     * @param component The App component to handle keyboard input for.
     */
    public KeyboardInputHandler(App component) {
        this.component = component;
        setupKeyBindings();
    }

    /**
     * Sets up key bindings and associated actions for various game-related controls.
     */
    private void setupKeyBindings() {
        // Arrow keys for moving Chap within the maze
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), MOVE_UP);
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), MOVE_DOWN);
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), MOVE_LEFT);
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), MOVE_RIGHT);

        // WASD keys for moving Chap within the maze
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), MOVE_UP);
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), MOVE_DOWN);
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), MOVE_LEFT);
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), MOVE_RIGHT);

        // CTRL-X to exit the game
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), EXIT_GAME);

        // CTRL-S to save the game state
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), SAVE_GAME);

        // CTRL-R to resume a saved game
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), RESUME_GAME);

        // CTRL-1 to start a new game at level 1
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK), START_LEVEL_1);

        // CTRL-2 to start a new game at level 2
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_DOWN_MASK), START_LEVEL_2);

        // SPACE to pause the game
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), PAUSE_GAME);

        // ESC to close the "game is paused" dialog and resume the game
        component.getInputMap(IFW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE_PAUSE_DIALOG);

        // Define actions for each keybinding
        component.getActionMap().put(MOVE_UP, new MoveAction("UP"));
        component.getActionMap().put(MOVE_DOWN, new MoveAction("DOWN"));
        component.getActionMap().put(MOVE_LEFT, new MoveAction("LEFT"));
        component.getActionMap().put(MOVE_RIGHT, new MoveAction("RIGHT"));
        component.getActionMap().put(EXIT_GAME, new ExitAction());
        component.getActionMap().put(SAVE_GAME, new SaveGameAction());
        component.getActionMap().put(RESUME_GAME, new ResumeGameAction());
        component.getActionMap().put(START_LEVEL_1, new StartGameAction(1));
        component.getActionMap().put(START_LEVEL_2, new StartGameAction(2));
        component.getActionMap().put(PAUSE_GAME, new PauseGameAction());
        component.getActionMap().put(CLOSE_PAUSE_DIALOG, new ClosePauseDialogAction());
    }


    /**
     * Class for movement keypresses
     */
    private class MoveAction extends AbstractAction {
        private String direction;

        public MoveAction(String direction) {
            this.direction = direction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle movement based on 'direction'
            // Implement your move logic here
            System.out.println(direction);
            Chap chap = component.getBoard().getChap();
            Renderer renderer  = component.getRenderer();

            if (renderer.getState() == Renderer.State.IDLE){
                switch (direction) {
                    case "UP":
                        try {
                            if(chap.move(Chap.Direction.UP)){
                                renderer.setState(Renderer.State.UP);
                                if (RecorderPanel.recording){
                                    RecorderPanel.moves.add("UP");
                                }
                            };
                        } catch (IllegalMove ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case "DOWN":
                        try {
                            if(chap.move(Chap.Direction.DOWN)){
                                renderer.setState(Renderer.State.DOWN);
                                if (RecorderPanel.recording){
                                    RecorderPanel.moves.add("DOWN");
                                }
                            }
                        } catch (IllegalMove ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case "LEFT":
                        try {
                            if(chap.move(Chap.Direction.LEFT)){
                                renderer.setState(Renderer.State.LEFT);
                                if (RecorderPanel.recording){
                                    RecorderPanel.moves.add("LEFT");
                                }
                            }
                        } catch (IllegalMove ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case "RIGHT":
                        try {
                            if(chap.move(Chap.Direction.RIGHT)){
                                renderer.setState(Renderer.State.RIGHT);
                                if (RecorderPanel.recording){
                                    RecorderPanel.moves.add("RIGHT");
                                }
                            }
                        } catch (IllegalMove ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    default:
                        break;
                }
            }

            component.repaint();



        }
        public String getDirection(){
            return direction;
        }

    }

    /**
     * Class for handling the exit keybind
     */

    private class ExitAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle CTRL-X action to exit the game and lose the current state
            int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to leave the game?", "Closing game", JOptionPane.YES_NO_OPTION);
            if (response == 0) { System.exit(0); }
        }
    }


    /**
     * class for handling the save keybind
     */
    private class SaveGameAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle CTRL-S action to save the game state
            System.out.println("Saving game");
        }
    }

    /**
     * class for handling the resume keybind
     */
    private class ResumeGameAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle CTRL-R action to resume a saved game
            System.out.println("Resuming game");
        }
    }


    /**
     * class for handling the level loading start keybinds
     */
    private class StartGameAction extends AbstractAction {
        private int level;

        public StartGameAction(int level) {
            this.level = level;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle CTRL-1 and CTRL-2 to start a new game at the specified level
            System.out.println("Loading level " + level);
        }
    }

    /**
     * class for handling the pause keybind
     */

    private class PauseGameAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle SPACE action to pause the game and display a "game is paused" dialog
            System.out.println("Game paused");
        }
    }

    /**
     * class for handling the exiting pause keybind
     */
    private class ClosePauseDialogAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle ESC action to close the "game is paused" dialog and resume the game
            System.out.println("Exiting pause");
        }
    }



}
