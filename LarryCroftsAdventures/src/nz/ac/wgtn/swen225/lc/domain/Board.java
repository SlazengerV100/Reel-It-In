package nz.ac.wgtn.swen225.lc.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import nz.ac.wgtn.swen225.lc.domain.tiles.*;
import nz.ac.wgtn.swen225.lc.persistency.AutoActor;
import nz.ac.wgtn.swen225.lc.persistency.Persistency;
import nz.ac.wgtn.swen225.lc.renderer.AudioUnit;

/**
 * Represents the board for a given level.
 * 
 * @author Anthony Kendrew (300607402)
 */
public class Board {
	private List<AutoActor> autoActors;
	private Tile[][] board;
	private final Chap chap;
	private int time;
	private final int level;
	private final int boardTreasureCount;
	private final AudioUnit audioUnit;

	/**
	 * Create a new Board. Generates a 2D array of tiles using
	 * {@link Persistency#loadGame(File) loadGame}.
	 */
	public Board(File file, AudioUnit audioUnit) {
		this.autoActors = new ArrayList<>();
		Persistency persistency = new Persistency();



		this.audioUnit = audioUnit;
		try {
			board = persistency.loadGame(file);
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("An error occurred while loading the game: " + e.getMessage());
		}
		time = persistency.timeLeft;
		boardTreasureCount = persistency.boardTreasureCount;
		level = persistency.level;
		int startX = persistency.playerX;
		int startY = persistency.playerY;
		Free playerTile = new Free(startX, startY);
		board[startX][startY] = playerTile;
		chap = new Chap(this, playerTile, persistency.playerTreasureCount);
		AutoActor autoActor1 = new AutoActor(1, 2, AutoActor.Direction.LEFT, Instant.now(), chap);
		AutoActor autoActor2 = new AutoActor(3, 4, AutoActor.Direction.LEFT, Instant.now(), chap);
		autoActors.add(autoActor1);
		autoActors.add(autoActor2);

	}

	/**
	 * Reset the given tile to a free tile.
	 * 
	 * @param tile the tile to reset
	 * @return the new tile that has been reset
	 */
	protected Free resetTile(Tile tile) {
		int x = tile.getX();
		int y = tile.getY();
		Free freeTile = new Free(x, y);
		board[x][y] = freeTile;
		return freeTile;
	}
	public List<AutoActor> getAutoActors() {
		return autoActors;
	}
	public void addAutoActor(AutoActor autoActor) {
		autoActors.add(autoActor);
	}
	/**
	 * Get a tile at the specified coordinates.
	 * 
	 * @param x position horizontally
	 * @param y position vertically
	 * @return the tile
	 */
	public Tile getTile(int x, int y) {
		return board[x][y];
	}

	/**
	 * Get all tiles on the board.
	 * 
	 * @return 2D array of the tiles
	 */
	public Tile[][] getTiles() {
		return board;
	}

	/**
	 * Get the player.
	 * 
	 * @return the player
	 */
	public Chap getChap() {
		return chap;
	}

	/**
	 * Get the current level.
	 * 
	 * @return the current level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Get the time counter of the board.
	 * 
	 * @return the current time left
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Get the number of treasures required to complete this level.
	 * 
	 * @return the number of treasures
	 */
	public int getBoardTreasureCount() {
		return boardTreasureCount;
	}

	/**
	 * Get the audio unit of the board.
	 * 
	 * @return the audio unit
	 */
	public AudioUnit getAudioUnit() {
		return audioUnit;
	}
}
