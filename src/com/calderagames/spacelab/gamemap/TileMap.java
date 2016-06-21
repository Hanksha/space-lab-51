package com.calderagames.spacelab.gamemap;

import static com.calderagames.spacelab.tiles.Tile.tileSize;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.tiles.AnimatedTileManager;
import com.calderagames.spacelab.tiles.Tile;
import com.calderagames.spacelab.tiles.TileSetManager;
import com.calderagames.spacelab.util.Timer;

public class TileMap {

	private GameContent gc;

	//tile set
	private Tile[] tileset;

	//map
	private int numRowMap;
	private int numColMap;
	private int mapWidth;
	private int mapHeight;
	private ArrayList<MapTile[][]> layers;
	private AnimatedTileManager animTileManager;
	private int collisionLayerIndex;

	//rendering
	private boolean isCurrentMap;

	public class MapTile {

		public int id;
		public int x1, y1, x2, y2, x3, y3, x4, y4;
		public TextureRegion texRegion;

		public MapTile(int id) {
			this.id = id;
		}

		public TextureRegion getTexRegion() {
			return texRegion;
		}

		public void setNotified() {
		}
	}

	private class AnimatedMapTile extends MapTile {

		protected AnimatedTileManager atm;
		protected int index;

		public AnimatedMapTile(int id, int index, AnimatedTileManager atm) {
			super(id);
			this.index = index;
			this.atm = atm;
		}

		@Override
		public TextureRegion getTexRegion() {
			return atm.getTextureRegion(index);
		}
	}

	private class NotifiedMapTile extends AnimatedMapTile {

		public Timer timer;

		public NotifiedMapTile(int id, int index, int delay, AnimatedTileManager atm) {
			super(id, index, atm);
			timer = new Timer(delay);
		}

		@Override
		public TextureRegion getTexRegion() {
			if(!timer.tick())
				return atm.getTextureRegion(index);
			else
				return texRegion;
		}

		@Override
		public void setNotified() {
			if(timer.tick())
				timer.update();
		}
	}

	public TileMap(GameContent gc, Element tileMap, Tile[] tileset, int collisionLayerIndex) {
		this.gc = gc;
		this.tileset = tileset;
		this.collisionLayerIndex = collisionLayerIndex;
		animTileManager = new AnimatedTileManager();
		layers = new ArrayList<MapTile[][]>();
		loadMap(tileMap);
	}

	private void loadMap(Element tileMap) {
		numColMap = Integer.parseInt(tileMap.getAttribute("width"));
		numRowMap = Integer.parseInt(tileMap.getAttribute("height"));

		mapWidth = numColMap * tileSize;
		mapHeight = numRowMap * tileSize;

		NodeList layerList = tileMap.getElementsByTagName("Layer");

		boolean flipH = false;
		boolean flipV = false;
		boolean rotate = false;
		int currentTile;
		int rawTile;

		for(int layer = 0; layer < layerList.getLength(); layer++) {
			layers.add(new MapTile[numRowMap][numColMap]);
			Element currentLayer = (Element) layerList.item(layer);
			NodeList rowList = currentLayer.getElementsByTagName("Row");

			for(int row = 0; row < numRowMap; row++) {
				Element currentRow = (Element) rowList.item(row);

				String line = currentRow.getTextContent();
				String[] tokens = line.split(",");
				for(int col = 0; col < numColMap; col++) {

					currentTile = Integer.parseInt(tokens[col]);
					rawTile = TileSetManager.getRawTileId(currentTile);

					if(currentTile == 0) {
						layers.get(layer)[row][col] = new MapTile(0);
						continue;
					}

					flipH = TileSetManager.isTileTransformed(currentTile, TileSetManager.FLIPH);
					flipV = TileSetManager.isTileTransformed(currentTile, TileSetManager.FLIPV);
					rotate = TileSetManager.isTileTransformed(currentTile, TileSetManager.ROTATE);

					//check if the tile is animated
					Element tileElem = (Element) gc.getRM().tilesetMeta.getElementsByTagName("Tile").item(rawTile - 1);

					if(tileElem.hasAttribute("animated") && tileElem.getAttribute("animated").contains("true")) {

						int indexAnim = animTileManager.contains(rawTile);
						if(indexAnim == -1) {
							//if not add it to a collection
							int delay = Integer.parseInt(tileElem.getAttribute("delay").split(",")[0]);
							String[] strFrames = tileElem.getAttribute("frames").split(",");
							TextureRegion[] frames = new TextureRegion[strFrames.length];
							for(int i = 0; i < frames.length; i++) {
								frames[i] = new TextureRegion(tileset[Integer.parseInt(strFrames[i])].getTexCoords());
							}

							indexAnim = animTileManager.addAnimatedTile(rawTile, delay, frames);
						}

						if(tileElem.getAttribute("animated").contains("notify"))
							layers.get(layer)[row][col] = new NotifiedMapTile(currentTile, indexAnim,
																			  Integer.parseInt(tileElem.getAttribute("delay").split(",")[1]), animTileManager);
						else
							layers.get(layer)[row][col] = new AnimatedMapTile(currentTile, indexAnim, animTileManager);
					}
					else
						layers.get(layer)[row][col] = new MapTile(currentTile);

					if(rotate)
						layers.get(layer)[row][col].texRegion = new TextureRegion(tileset[rawTile].getTexCoordsRotated());
					else
						layers.get(layer)[row][col].texRegion = new TextureRegion(tileset[rawTile].getTexCoords());

					layers.get(layer)[row][col].x1 = col * tileSize + (flipH ? tileSize : 0);
					layers.get(layer)[row][col].y1 = row * tileSize + (flipV ? tileSize : 0);
					layers.get(layer)[row][col].x2 = col * tileSize + tileSize * (flipH ? -1 : 1) + (flipH ? tileSize : 0);
					layers.get(layer)[row][col].y2 = row * tileSize + (flipV ? tileSize : 0);
					layers.get(layer)[row][col].x3 = col * tileSize + tileSize * (flipH ? -1 : 1) + (flipH ? tileSize : 0);
					layers.get(layer)[row][col].y3 = row * tileSize + tileSize * (flipV ? -1 : 1) + (flipV ? tileSize : 0);
					layers.get(layer)[row][col].x4 = col * tileSize + (flipH ? tileSize : 0);
					layers.get(layer)[row][col].y4 = row * tileSize + tileSize * (flipV ? -1 : 1) + (flipV ? tileSize : 0);
				}
			}
		}
	}

	public void update() {
		animTileManager.update();
	}

	public void render(SpriteBatch sb, int layer, boolean z_ordering) {

		int startRow = Math.abs((int) (gc.getSPM().getCamY() / tileSize));
		int startCol = Math.abs((int) (gc.getSPM().getCamX() / tileSize));

		sb.setColor(1f, 1f, 1f, 1f);
		for(int row = startRow; row < numRowMap; row++) {
			for(int col = startCol; col < numRowMap; col++) {
				if(layers.get(layer)[row][col].id == 0)
					continue;

				sb.draw(layers.get(layer)[row][col].x1, layers.get(layer)[row][col].y1, layers.get(layer)[row][col].x2, layers.get(layer)[row][col].y2,
						layers.get(layer)[row][col].x3, layers.get(layer)[row][col].y3, layers.get(layer)[row][col].x4, layers.get(layer)[row][col].y4,
						layers.get(layer)[row][col].getTexRegion(), z_ordering ? layers.get(layer)[row][col].y4 : 0);
			}
		}
	}

	public void setTile(int row, int col, int layer, int id) {
		layers.get(layer)[row][col].id = id;
	}
	
	
	public void setTile(float x, float y, int layer, int id) {
		layers.get(layer)[Map.toRow(y)][Map.toCol(x)].id = id;
	}
	
	public int getNumRowMap() {
		return numRowMap;
	}

	public int getNumColMap() {
		return numColMap;
	}

	public int getWidth() {
		return mapWidth;
	}

	public int getHeight() {
		return mapHeight;
	}

	public int getType(double x, double y, int layer) {

		int row = (int) Math.floor(y / tileSize);
		int col = (int) Math.floor(x / tileSize);

		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		if(row >= numRowMap)
			row = numRowMap - 1;
		if(col >= numColMap)
			col = numColMap - 1;

		int id = layers.get(layer)[row][col].id;
		id = TileSetManager.getRawTileId(id);

		return tileset[id].getType();
	}

	public int getNature(double x, double y, int layer) {

		int row = (int) Math.floor(y / tileSize);
		int col = (int) Math.floor(x / tileSize);

		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		if(row >= numRowMap)
			row = numRowMap - 1;
		if(col >= numColMap)
			col = numColMap - 1;

		int id = layers.get(layer)[row][col].id;
		id = TileSetManager.getRawTileId(id);

		return tileset[id].getNature();
	}

	public MapTile getMapTile(int row, int col, int layer) {
		return layers.get(layer)[row][col];
	}

	public MapTile getMapTile(float x, float y, int layer) {
		return getMapTile(getRow(y), getCol(x), layer);
	}

	public int getTypei(int row, int col, int layer) {

		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		if(row >= numRowMap)
			row = numRowMap - 1;
		if(col >= numColMap)
			col = numColMap - 1;

		return tileset[TileSetManager.getRawTileId(layers.get(layer)[row][col].id)].getType();
	}

	public int getNaturei(int row, int col, int layer) {

		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		if(row >= numRowMap)
			row = numRowMap - 1;
		if(col >= numColMap)
			col = numColMap - 1;

		return tileset[TileSetManager.getRawTileId(layers.get(layer)[row][col].id)].getNature();
	}

	public int getID(double x, double y, int layer) {

		int row = (int) Math.floor(y / tileSize);
		int col = (int) Math.floor(x / tileSize);

		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		if(row >= numRowMap)
			row = numRowMap - 1;
		if(col >= numColMap)
			col = numColMap - 1;

		return layers.get(layer)[row][col].id;
	}

	public int getTileID(int row, int col, int layer) {

		if(row < 0)
			row = 0;
		if(col < 0)
			col = 0;
		if(row >= numRowMap)
			row = numRowMap - 1;
		if(col >= numColMap)
			col = numColMap - 1;

		return layers.get(layer)[row][col].id;
	}

	public MapTile[][] getCollisionLayer() {
		return layers.get(collisionLayerIndex);
	}

	public int getCollisionLayerIndex() {
		return collisionLayerIndex;
	}

	public int getCol(double x) {
		return (int) x / tileSize;
	}

	public int getRow(double y) {
		return (int) y / tileSize;
	}

	public boolean isCurrentMap() {
		return isCurrentMap;
	}

	public void setCurrentMap(boolean b) {
		isCurrentMap = b;
	}

	public void dispose() {
		layers.clear();
	}
}
