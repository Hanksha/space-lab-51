package com.calderagames.spacelab.entities;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.geom.Shape;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.gamemap.MapCell;
import com.calderagames.spacelab.graphics.SpriteBatch;

public abstract class Entity {

	/**Reference to the game content*/
	protected GameContent gc;
	/**Reference to the map*/
	protected Map map;
	/**Position*/
	protected Vector2f position;
	/**Map cell the entity is currently standing on*/
	protected MapCell currMapCell;
	
	public Entity(GameContent gc, Map map, int row, int col) {
		this.gc = gc;
		this.map = map;
		currMapCell = map.getMapCell(row, col);
		currMapCell.getEntities().add(this);
		position = new Vector2f();
	}
	
	public abstract void update(double dt);
	
	public abstract void render(SpriteBatch sb);
	
	public abstract void renderText(SpriteBatch sb);
	
	public abstract void renderDebug(SpriteBatch sb);
	
	public abstract void interact(Entity source);
	
	public Vector2f getPosition() {
		return position;
	}
	
	public Map getMap() {
		return map;
	}
	
	public abstract Shape getShape();
}
