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
import java.awt.image.BufferedImage;
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
    private State state = State.IDLE;
    private enum Images {
        CHAP, DOOR_BLUE, DOOR_GREEN, DOOR_RED, DOOR_YELLOW, EXIT, FREE, INFOBOX, KEY_BLUE, KEY_GREEN, KEY_RED, KEY_YELLOW, TREASURE, WALL, BOAT, SEAGULL_LEFT, SEAGULL_RIGHT
    }

    private final HashMap<Images, Image> images = new HashMap<>();
    private int count = 0;
    private final Random random = new Random();
    private boolean seagullActivated = false;
    private double seagullX = 0;
    private double seagullY;
    private int cellSize;
    private AudioUnit audioUnit;
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

        this.audioUnit = new AudioUnit();
        audioUnit.startBackgroundMusic();
        audioUnit.startAmbience();
    }

    private void startTimer(){
        Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ;
                boardPanel.repaint();
            }
        });
        timer.start();
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
        cellSize = (int)Math.max(0, Math.min(tileWidth, tileHeight));
        int distanceFromLeftBorder = (int)(mazePanelWidth/2 - (cellSize* camera.getWidth()/2));
        int distanceFromTopBorder = (int)(mazePanelHeight/2 - (cellSize* camera.getHeight()/2));
        int frame = (count/16) % 8;

        drawBoard(g);
        drawChap(mazePanelWidth, mazePanelHeight, frame, g);
        if (!seagullActivated && random.nextInt(0, 1000) == 0){
            seagullActivated = true;
            audioUnit.playSeagullSFX();
            int lowerBound = (int)camera.getY();
            int upperBound = (int)(camera.getY() + camera.getHeight());
            seagullY = random.nextInt(lowerBound, upperBound + 1);
        }
        if (seagullActivated){
            seagullAnimation(frame, g);
            seagullX += 0.05;
            if (seagullX > grid.length){
                seagullActivated = false;
                seagullX = 0;
            }
        }
        drawBorder(new Color(232, 220, 202), mazePanelWidth, mazePanelHeight, distanceFromTopBorder, distanceFromLeftBorder, g);
        updateCameraPosition();

        count++;

    }

    /**
     * Draws all tiles that make up board
     * @param g
     * @throws IOException
     */
    private void drawBoard(Graphics g) throws IOException {
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
                g.drawImage(image, (int)worldXToCameraX(x), (int)worldYToCameraY(y), cellSize, cellSize, null);
            }
        }
    }

    /**
     * Draws a seagull flying across screen at random position
     * @param frame
     * @param g
     */
    private void seagullAnimation(int frame, Graphics g){
        BufferedImage seagullSpriteSheet = (BufferedImage) images.get(Images.SEAGULL_RIGHT);
        int frameWidth = seagullSpriteSheet.getWidth()/9;
        int frameHeight = seagullSpriteSheet.getHeight();
        BufferedImage chapImage = seagullSpriteSheet.getSubimage(frame * frameWidth,0,frameWidth,frameHeight);
        int x = (int)worldXToCameraX(seagullX);
        int y = (int)worldYToCameraY(seagullY);
        g.drawImage(chapImage, x, y, cellSize, cellSize, null);
    }
    /**
     * Draws border around game board
     * @param c
     * @param width
     * @param height
     * @param top
     * @param left
     * @param g
     */
    private void drawBorder(Color c, int width, int height, int top, int left, Graphics g){
        // Draw border
        g.setColor(c);
        g.fillRect(0, 0, width, top);
        g.fillRect(0, height-top, width, top);
        g.fillRect(0, 0, left, height);
        g.fillRect(width-left, 0, left, height);
    }

    /**
     * Draws chap at center of board
     * @param mazePanelWidth
     * @param mazePanelHeight
     * @param frame
     * @param g
     */
    private void drawChap(int mazePanelWidth, int mazePanelHeight, int frame, Graphics g){
        BufferedImage boatSpriteSheet= (BufferedImage) images.get(Images.BOAT);
        int frameWidth = boatSpriteSheet.getWidth()/8;
        int frameHeight = boatSpriteSheet.getHeight();
        BufferedImage chapImage = boatSpriteSheet.getSubimage(frame * frameWidth,0,frameWidth,frameHeight);
        int chapX = (mazePanelWidth /2)  - cellSize/2;
        int chapY = (mazePanelHeight /2)  - cellSize/2;
        g.drawImage(chapImage, chapX, chapY, cellSize, cellSize, null);
    }

    /**
     * Update camera position depending on Renderer state
     */
    private void updateCameraPosition(){
        double distance = 0.125;
        switch (state) {
            case IDLE -> {}
            case UP -> camera.setY(camera.getY() - distance);
            case DOWN -> camera.setY(camera.getY() + distance);
            case LEFT -> camera.setX(camera.getX() - distance);
            case RIGHT -> camera.setX(camera.getX() + distance);
            default -> {
            }
        }
        if((camera.getX() == board.getChap().getTile().getX() - (int)(camera.getWidth()/2)) && (camera.getY() == board.getChap().getTile().getY() - (int)(camera.getWidth()/2))){
            state = State.IDLE;
        }
    }

    /**
     * Returns image of tile
     * @param tile
     * @return
     * @throws IOException
     */
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

    /**
     * Loads game images into a map for easy access
     * @throws IOException
     */
    private void loadImages() throws IOException {
        String path = "LarryCroftsAdventures/assets/";
        images.put(Images.BOAT, ImageIO.read(new File(path + "Boat.png")));
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
        images.put(Images.SEAGULL_LEFT, ImageIO.read(new File(path + "SeagullLeft.png")));
        images.put(Images.SEAGULL_RIGHT, ImageIO.read(new File(path + "SeagullRight.png")));
    }
    public State getState(){ return state; }
    public void setState(State state){this.state = state;}
    public JPanel getBoardPanel(){ return boardPanel; }

    /**
     *  Converts world x coord to camera x coord
     */
    private double worldXToCameraX(double worldX){
        double tileWidth = boardPanel.getWidth()/ camera.getWidth();
        double tileHeight = boardPanel.getHeight()/camera.getHeight();
        int clampedValue = (int)Math.max(0, Math.min(tileWidth, tileHeight));
        int distanceFromLeftBorder = (int)(boardPanel.getWidth()/2 - (clampedValue* camera.getWidth()/2));
        return (worldX - camera.getX())*clampedValue + distanceFromLeftBorder;
    }

    /**
     *  Converts world y coord to camera y coord
     */
    private double worldYToCameraY(double worldY){
        double tileWidth = boardPanel.getWidth()/ camera.getWidth();
        double tileHeight = boardPanel.getHeight()/camera.getHeight();
        int clampedValue = (int)Math.max(0, Math.min(tileWidth, tileHeight));
        int distanceFromTopBorder = (int)(boardPanel.getHeight()/2 - (clampedValue* camera.getHeight()/2));
        return (worldY - camera.getY())*clampedValue + distanceFromTopBorder;
    }
}
