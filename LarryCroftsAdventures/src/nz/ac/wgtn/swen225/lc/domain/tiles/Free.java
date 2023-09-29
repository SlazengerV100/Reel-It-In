package nz.ac.wgtn.swen225.lc.domain.tiles;

import nz.ac.wgtn.swen225.lc.domain.Chap;

/**
 * Represents a tile that the player can stand on or move in the direction of.
 */
public class Free implements Tile {
    protected int x;
    protected int y;

    /**
     * Create a new free tile.
     * @param x the x position of the tile
     * @param y the y position of the tile
     */
    public Free(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean traversable() {
        return true;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}