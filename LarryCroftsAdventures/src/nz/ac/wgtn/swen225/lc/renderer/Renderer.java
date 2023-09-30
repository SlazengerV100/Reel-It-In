package nz.ac.wgtn.swen225.lc.renderer;

import nz.ac.wgtn.swen225.lc.domain.Board;
import nz.ac.wgtn.swen225.lc.domain.Chap;
import nz.ac.wgtn.swen225.lc.domain.items.Key;
import nz.ac.wgtn.swen225.lc.domain.tiles.*;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The Renderer class is responsible for rendering the game board and characters on the JPanel.
 */
public class Renderer {

    Board board;
    Tile[][] grid;
    public Camera camera;
    private JPanel boardPanel;
    public enum State {
        IDLE, UP, DOWN, LEFT, RIGHT
    }
    private enum Images {
        CHAP, DOOR_BLUE, DOOR_GREEN, DOOR_RED, DOOR_YELLOW, EXIT, FREE, INFOBOX, KEY_BLUE, KEY_GREEN, KEY_RED, KEY_YELLOW, TREASURE, WALL
    }
    private State state = State.IDLE;
    private Timer timer;
    private double distanceTotal = 0;
    private double distance = 0.125;
    private HashMap<Images, Image> images = new HashMap<>();
    /**
     * Constructor for the Renderer class.
     *
     * @param board The game board to render.
     */
    public Renderer(Board board, JPanel panel, int focusAreaSize) throws IOException {
        panel = new JPanel(){
            @Override
            public void paint(Graphics g) {
                try {
                    draw(g);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        this.board = board;
        grid = board.getTiles();
        this.boardPanel = panel;
        this.camera = new Camera(board.getChap().getTile().getX() - (focusAreaSize/2), board.getChap().getTile().getY() - (focusAreaSize/2), focusAreaSize, focusAreaSize);
        loadImages();
        startTimer();
    }

    private void startTimer(){
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {;
                boardPanel.repaint();
            }
        });
        timer.start();
    }

    private void loadImages() throws IOException {
        String path = "LarryCroftsAdventures/assets/";
        images.put(Images.CHAP, ImageIO.read(new File(path + "Chap.png")));
        images.put(Images.DOOR_BLUE, ImageIO.read(new File(path + "Door_BLue.png")));
        images.put(Images.DOOR_GREEN, ImageIO.read(new File(path + "Door_Green.png")));
        images.put(Images.DOOR_RED, ImageIO.read(new File(path + "Door_Red.png")));
        images.put(Images.DOOR_YELLOW, ImageIO.read(new File(path + "Door_Yellow.png")));
        images.put(Images.EXIT, ImageIO.read(new File(path + "Exit.png")));
        images.put(Images.FREE, ImageIO.read(new File(path + "Free.png")));
        images.put(Images.INFOBOX, ImageIO.read(new File(path + "InfoBox.png")));
        images.put(Images.KEY_BLUE, ImageIO.read(new File(path + "Key_Blue.png")));
        images.put(Images.KEY_GREEN, ImageIO.read(new File(path + "Key_Green.png")));
        images.put(Images.KEY_RED, ImageIO.read(new File(path + "Key_Red.png")));
        images.put(Images.KEY_YELLOW, ImageIO.read(new File(path + "Key_Yellow.png")));
        images.put(Images.TREASURE, ImageIO.read(new File(path + "Treasure.png")));
        images.put(Images.WALL, ImageIO.read(new File(path + "Wall.png")));
    }


    /**
     * Draws the game board, characters and items on the provided JPanel.
     *
     * @param g         The Graphics object to use for rendering.
     * @throws IOException If there is an error loading image files.
     */
    public void draw(Graphics g) throws IOException {
        // Dimensions
        int mazePanelWidth = boardPanel.getWidth();
        int mazePanelHeight = boardPanel.getHeight();
        double tileWidth = mazePanelWidth/ camera.getWidth();
        double tileHeight = mazePanelHeight/camera.getHeight();
        int clampedValue = (int)Math.max(0, Math.min(tileWidth, tileHeight));
        int distanceFromLeftBorder = (int)(mazePanelWidth/2 - (clampedValue* camera.getWidth()/2));
        int distanceFromTopBorder = (int)(mazePanelHeight/2 - (clampedValue* camera.getHeight()/2));

        // Go through all tiles visible to camera and draw them on the JPanel
        for (int x = (int)camera.getX()-1; x < camera.getX() + camera.getWidth()+1; x++){
            for (int y = (int)camera.getY()-1; y < camera.getY() + camera.getHeight()+1; y++){
                Tile tile;
                // If coordinates trying to be drawn is out of bounds of board then just draw a wall tile.
                if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length){
                    tile = new Wall(0,0);
                }
                else {
                    tile = grid[x][y];
                }

                Image image = getTileImage(tile);
                //drawImageAt(image, x, y, g);
                double cameraX = camera.worldXToCameraX(x);
                double cameraY = camera.worldYToCameraY(y);
                g.drawImage(image, (int) (cameraX*clampedValue + distanceFromLeftBorder), (int) (cameraY*clampedValue + distanceFromTopBorder), (int) (clampedValue), (int) (clampedValue), null);
            }
        }

        // Draw border
        g.setColor(new Color(232, 220, 202));
        g.fillRect(0, 0, mazePanelWidth, distanceFromTopBorder);
        g.fillRect(0, mazePanelHeight-distanceFromTopBorder, mazePanelWidth, distanceFromTopBorder);
        g.fillRect(0, 0, distanceFromLeftBorder, mazePanelHeight);
        g.fillRect(mazePanelWidth-distanceFromLeftBorder, 0, distanceFromLeftBorder, mazePanelHeight);

        // Draw Chap at Chap's coordinates
        File chapFile;
        chapFile = new File("LarryCroftsAdventures" + File.separator + "assets" + File.separator + "Chap.png");
        Image chapImage = ImageIO.read(chapFile);
        double chapX = camera.getX() + (clampedValue * (int)(camera.getWidth()/2));
        double chapY = camera.getY() + (clampedValue * (int)(camera.getWidth()/2));
        g.drawImage(chapImage, (int) (chapX + distanceFromLeftBorder), (int) (chapY + distanceFromTopBorder), (int) (clampedValue), (int) (clampedValue), null);

        switch (state){
            case IDLE:
                break;
            case UP:
                camera.setY(camera.getY() - distance);
                break;
            case DOWN:
                camera.setY(camera.getY() + distance);
                break;
            case LEFT:
                camera.setX(camera.getX() - distance);
                break;
            case RIGHT:
                camera.setX(camera.getX() + distance);
                break;
            default:
                break;
        }
        if((camera.getX() == board.getChap().getTile().getX() - (int)(camera.getWidth()/2)) && (camera.getY() == board.getChap().getTile().getY() - (int)(camera.getWidth()/2))){
            state = State.IDLE;
        }

    }

    private Image getTileImage(Tile tile) throws IOException {
        Image img = null;
        // Assign filename depending on tile type and tile item
        switch (tile.getClass().getSimpleName()) {
            case "Door" -> {
                Key.Colour doorColour = ((Door) tile).getColour();
                if (doorColour == Key.Colour.RED){ img = images.get(Images.DOOR_RED);}
                if (doorColour == Key.Colour.BLUE){ img = images.get(Images.DOOR_BLUE);}
                if (doorColour == Key.Colour.GREEN){ img = images.get(Images.DOOR_GREEN);}
                if (doorColour == Key.Colour.YELLOW){ img = images.get(Images.DOOR_YELLOW);}
            }
            case "Exit", "ExitLock" -> img = images.get(Images.EXIT);
            case "Treasure" -> img = images.get(Images.TREASURE);
            case "InfoField" -> img = images.get(Images.INFOBOX);
            case "KeyTile" -> {
                Key.Colour keyColour = ((KeyTile) tile).getColour();
                if (keyColour == Key.Colour.RED){ img = images.get(Images.KEY_RED);}
                if (keyColour == Key.Colour.BLUE){ img = images.get(Images.KEY_BLUE);}
                if (keyColour == Key.Colour.GREEN){ img = images.get(Images.KEY_GREEN);}
                if (keyColour == Key.Colour.YELLOW){ img = images.get(Images.KEY_YELLOW);}
            }
            case "Free" -> {
                //if (((Free) tile).getItem() != null) fileName += ((Free) tile).getItem();
                //else
                img = images.get(Images.FREE);
            }
            case "Wall" -> img = images.get(Images.WALL);
            default -> {
                return img; // Unknown tile type
            }
        }
        return img;
    }

    public State getState(){ return state; }
    public void setState(State state){this.state = state;}
    public JPanel getBoardPanel(){ return boardPanel; }
}
