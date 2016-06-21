package com.calderagames.spacelab.gamemap;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.calderagames.spacelab.entities.BattleManager;
import com.calderagames.spacelab.entities.Container;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.entities.EntityGroup;
import com.calderagames.spacelab.entities.FloatingText;
import com.calderagames.spacelab.entities.Mutant;
import com.calderagames.spacelab.entities.EntityGroup.GroupFormation;
import com.calderagames.spacelab.entities.items.Item;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.light.LightMap;
import com.calderagames.spacelab.tiles.Tile;
import com.calderagames.spacelab.util.MPMParser;

public class Map {

	private GameContent gc;

	private TileMap tileMap;
	private LightMap lightMap;

	private ArrayList<MapCell> grid;
	private ArrayList<Entity> entities;
	private ArrayList<EntityGroup> enemyGroups;
	private ArrayList<FloatingText> texts;

	public Map(GameContent gc, BattleManager bm, String filePath) {
		this.gc = gc;
		MPMParser parser = new MPMParser(filePath);

		tileMap = new TileMap(gc, parser.elemTileMap, gc.getTSM().getTileSet(0), parser.elemTileMap.getElementsByTagName("Layer").getLength() - 1);
		grid = new ArrayList<MapCell>();

		lightMap = new LightMap(gc, this);
		
		int numRow = tileMap.getNumRowMap();
		int numCol = tileMap.getNumColMap();
		int r, c;
		for(int i = 0; i < numRow * numCol; i++) {
			r = (int) (i / tileMap.getNumRowMap());
			c = i - r * numCol;
			grid.add(new MapCell(r, c));
		}

		entities = new ArrayList<Entity>();
		enemyGroups = new ArrayList<EntityGroup>();
		texts = new ArrayList<FloatingText>();

		//add objects
		NodeList entityList = null;
		NodeList groupList = null;

		//add enemies
		Element enemiesElem = (Element) parser.elemObjects.getElementsByTagName("enemies").item(0);

		//add mutants
		entityList = ((Element) enemiesElem.getElementsByTagName("mutants").item(0)).getElementsByTagName("group");

		for(int i = 0; i < entityList.getLength(); i++) {
			if(((Element) entityList.item(i)).getTagName().equals("group")) {
				groupList = ((Element) entityList.item(i)).getElementsByTagName("mutant");

				EntityGroup group = new EntityGroup(gc, GroupFormation.FREE);

				for(int j = 0; j < groupList.getLength(); j++) {
					Element mutantElem = (Element) groupList.item(j);

					int row = Integer.parseInt(mutantElem.getAttribute("row"));
					int col = Integer.parseInt(mutantElem.getAttribute("col"));

					Mutant mutant = new Mutant(gc, this, bm, row, col, mutantElem.getAttribute("facing"));
					group.addEntity(mutant);
					mutant.setGroup(group);
					enemyGroups.add(group);
				}
			}
		}

		//add containers
		Element containersElem = (Element) parser.elemObjects.getElementsByTagName("containers").item(0);

		entityList = containersElem.getElementsByTagName("container");

		for(int i = 0; i < entityList.getLength(); i++) {
			if(entityList.item(i).getNodeName().equals("container")) {
				Element containerElem = (Element) entityList.item(i);

				int row = Integer.parseInt(containerElem.getAttribute("row"));
				int col = Integer.parseInt(containerElem.getAttribute("col"));
				
				Container container = new Container(gc, this, row, col, containerElem.getAttribute("type"));
				
				entities.add(container);
				
				NodeList invList = containerElem.getElementsByTagName("inv").item(0).getChildNodes();
				
				for(int j = 0; j < invList.getLength(); j++) {
					if(invList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Element itemElem = (Element) invList.item(j);
						int id = Integer.parseInt(itemElem.getAttribute("id"));
						
						container.addItem(new Item(gc, this, row, col, id));
					}
				}
			}
		}
	}

	public void update(double dt) {

		for(Entity e : entities)
			e.update(dt);
		for(EntityGroup gp : enemyGroups)
			gp.update(dt);

		for(Iterator<FloatingText> iter = texts.iterator(); iter.hasNext();) {
			FloatingText text = iter.next();
			text.update(dt);

			if(text.isOver())
				iter.remove();
		}
		
		for(Iterator<EntityGroup> iter = enemyGroups.iterator(); iter.hasNext();) {
			EntityGroup group = iter.next();
			
			if(group.isGroupDead())
				iter.remove();
		}
	}

	public void render(SpriteBatch sb) {
		//floor
		tileMap.render(sb, 0, false);
		tileMap.render(sb, 1, false);
		
		//back
		tileMap.render(sb, 2, false);
		tileMap.render(sb, 3, false);
		tileMap.render(sb, 4, false);
		
		//front
		tileMap.render(sb, 5, true);
		tileMap.render(sb, 6, true);

		for(Entity e : entities)
			e.render(sb);
		for(EntityGroup gp : enemyGroups)
			gp.render(sb);
	}
	
	public void renderText(SpriteBatch sb) {
		for(FloatingText text : texts)
			text.render(sb);
	}

	public void addFloatingText(String text, int x, int y, int colorType) {
		texts.add(new FloatingText((int) (x - 32 + Math.random() * 64), y, text, gc.getRM().getFont("mithril", 16), 1000, colorType));
	}
	
	public void addFloatingDialog(String text, int x , int y, int delay) {
		texts.add(new FloatingText(x, y, text, gc.getRM().getFont("mithril", 16), delay, FloatingText.INFO));
	}

	public boolean isWin() {
		return enemyGroups.isEmpty();
	}
	
	public LightMap getLightMap() {
		return lightMap;
	}
	
	public MapCell getMapCell(int row, int col) {
		return grid.get(row * tileMap.getNumRowMap() + col);
	}

	public TileMap getTileMap() {
		return tileMap;
	}

	public boolean isTileBlock(int row, int col) {
		return tileMap.getTypei(row, col, tileMap.getCollisionLayerIndex()) == Tile.BLOCK;
	}
	
	public boolean isTileBlock(float x, float y) {
		return tileMap.getTypei(Map.toRow(y), Map.toCol(x), tileMap.getCollisionLayerIndex()) == Tile.BLOCK;
	}

	public boolean isMapCellBusy(int row, int col) {
		return !getMapCell(row, col).getEntities().isEmpty();
	}

	public static int toRow(float y) {
		return (int) (y / Tile.tileSize);
	}

	public static int toCol(float x) {
		return (int) (x / Tile.tileSize);
	}
	
	public void dispose() {
		lightMap.dispose();
	}
}
