package com.calderagames.spacelab.entities;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import com.calderagames.spacelab.entities.items.Item;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.tiles.Tile;

public class Container extends Entity implements Obstacle {
	
	public static final String BARREL = "barrel";
	public static final String CHEST = "chest";
	
	protected ArrayList<Item> items;
	protected boolean open;
	
	protected Sprite sprClose;
	protected Sprite sprOpen;
	
	protected Shape shape;
	
	public Container(GameContent gc, Map map, int row, int col, String type) {
		super(gc, map, row, col);
		position = new Vector2f(col * Tile.tileSize + Tile.tileSize / 2, row * Tile.tileSize + Tile.tileSize / 2);
		items = new ArrayList<Item>();
		
		shape = new Rectangle(0, 0, 31, 29);
		
		if(type.equals(BARREL)) {
			sprClose = new Sprite(41, 62, new TextureRegion(gc.getRM().getTexture("texatlas"), 197, 470, 21, 31));
			sprOpen = new Sprite(38, 58, new TextureRegion(gc.getRM().getTexture("texatlas"), 231, 471, 19, 29));
		}
		else if(type.equals(CHEST)) {
			sprClose = new Sprite(56, 66, new TextureRegion(gc.getRM().getTexture("texatlas"), 255, 466, 28, 33));
			sprOpen = new Sprite(52, 62, new TextureRegion(gc.getRM().getTexture("texatlas"), 288, 467, 26, 31));
		}
	}
	
	@Override
	public void update(double dt) {
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.draw(open?sprOpen:sprClose, position.x - sprOpen.getWidth() / 2, position.y - sprOpen.getHeight() + 10, (int) (position.y + sprOpen.getHeight() / 2));
	}

	@Override
	public void renderText(SpriteBatch sb) {
	}
	
	@Override
	public void renderDebug(SpriteBatch sb) {
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void addItem(Item item) {
		items.add(item);
	}
	
	@Override
	public void interact(Entity source) {
		open = true;
		
		if(source instanceof DynamicEntity) {
			Iterator<Item> iter = items.iterator();
			
			boolean remove;
			
			while(iter.hasNext()) {
				remove = false;
				Item item = iter.next();
				
				if(((DynamicEntity) source).getInventory().addItem(item)) {
					remove = true;
				}
				else {
					for(DynamicEntity entity: ((DynamicEntity) source).getGroup().getEntities()) {
						if(entity.equals(source))
							continue;
						
						if(entity.getInventory().addItem(item)) {
							remove = true;
							break;
						}
					}
				}
				
				if(remove)
					iter.remove();
			}
		}
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	@Override
	public Shape getShape() {
		return shape;
	}

	@Override
	public boolean isBlock() {
		return true;
	}
}
