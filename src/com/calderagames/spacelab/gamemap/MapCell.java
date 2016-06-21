package com.calderagames.spacelab.gamemap;

import java.util.ArrayList;

import com.calderagames.spacelab.entities.Entity;

public class MapCell {
	private ArrayList<Entity> entities;
	private int row, col;
	
	public MapCell(int row, int col) {
		this.row = row;
		this.col = col;
		entities = new ArrayList<Entity>();
	}
	
	public void interact(Entity source) {
		for(Entity e: entities) {
			e.interact(source);
		}
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
}
