package com.calderagames.spacelab.tiles;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Texture;

public class TileSetManager {

	private GameContent gc;

	//transformation
	public static final int BITMASK_FLIPH = 0x80000000;
	public static final int BITMASK_FLIPV = 0x40000000;
	public static final int BITMASK_ROTATE = 0x20000000;
	public static final int FLIPH = 0;
	public static final int FLIPV = 1;
	public static final int ROTATE = 2;

	//meta data
	public static final int tileSize = 32;

	private Tile[][] tileSets;
	
	private int currentIndex;

	public TileSetManager(GameContent gc, int numTileSet) {
		this.gc = gc;
		tileSets = new Tile[numTileSet][];
	}

	public void addTileSet(int index, Tile[] tileSet) {
		tileSets[index] = tileSet;
	}
	
	public Tile[] createTileSet(Texture tileSet, int[] meta_type, int[] meta_nature, int numRow, int numCol, int offsetX, int offsetY, int spacing) {
		int index = numRow * numCol;
		Tile[] tempTS = new Tile[index + 1];

		tempTS[0] = new Tile(null, 0, 0);

		float texWidth = (float) tileSize / tileSet.getTextureWidth();
		float texHeight = (float) tileSize / tileSet.getTextureHeight();

		float x;
		float y;

		index = 1;

		for(int row = 0; row < numRow; row++) {
			for(int col = 0; col < numCol; col++) {

				x = (float) (offsetX + (tileSize + spacing) * col) / tileSet.getTextureWidth();
				y = (float) (offsetY + (tileSize + spacing) * row) / tileSet.getTextureHeight();

				float[] texCoords = { x, y, x + texWidth, y, x, y + texHeight, x + texWidth, y, x + texWidth, y + texHeight, x, y + texHeight };

				tempTS[index] = new Tile(texCoords, meta_type[index - 1], meta_nature[index - 1]);
				tempTS[index].setTexCoordsRotated(new float[] { x, y + texHeight, x, y, x + texWidth, y + texHeight, x, y, x + texWidth, y, x + texWidth,
																y + texHeight });

				x = offsetX + (tileSize + spacing) * col;
				y = offsetY + (tileSize + spacing) * row;

				index++;
			}
		}

		return tempTS;
	}
	
	public void setCurrentTileSet(int index) {
		currentIndex = index;
	}
	
	public Tile[] getCurrentTileSet() {
		return getTileSet(currentIndex);
	}
	
	public Tile[] getTileSet(int index) {
		return tileSets[index];
	}

	// return the tile raw id
	public static int getRawTileId(int tile) {
		tile <<= 3;
		tile >>>= 3;

		return tile;
	}

	public static boolean isSolidBlock(int type) {
		return type == Tile.BLOCK;
	}

	/**
	 * <p>
	 * Method to determine if the tile has been transformed.
	 * </p>
	 * 
	 * @param tile
	 *            raw id of the tile
	 * @param param
	 *            one of the following value:<br>
	 *            <ul>
	 *            <li>{@link TileSetManager#FLIPH} - flip horizontal</li>
	 *            <li>{@link TileSetManager#FLIPV} - flip vertical</li>
	 *            <li>{@link TileSetManager#ROTATE} - rotate</li>
	 *            </ul>
	 * @return boolean
	 */
	public static boolean isTileTransformed(int tile, int param) {
		if(param == FLIPH) {
			return (tile & BITMASK_FLIPH) == BITMASK_FLIPH;
		}
		else if(param == FLIPV) {
			return (tile & BITMASK_FLIPV) == BITMASK_FLIPV;
		}
		else if(param == ROTATE) {
			return (tile & BITMASK_ROTATE) == BITMASK_ROTATE;
		}
		else
			return false;
	}
}
