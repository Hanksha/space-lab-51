package com.calderagames.spacelab.entities;

import org.lwjgl.util.vector.Vector2f;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.tiles.Tile;
import com.calderagames.spacelab.util.MathUtil;
import com.calderagames.spacelab.util.Timer;

public class DialogueTrigger {

	private GameContent gc;
	private Map map;
	private String text;
	private Timer timer;
	private boolean triggered;
	private Vector2f position;
	private int range;
	private DynamicEntity speaker;
	
	public DialogueTrigger(GameContent gc, Map map, DynamicEntity speaker, int row, int col, String text, int range, int delay) {
		this.gc = gc;
		this.map = map;
		this.speaker = speaker;
		this.text = text;
		position = new Vector2f(col * Tile.tileSize + Tile.tileSize / 2, row * Tile.tileSize + Tile.tileSize / 2);
		this.range = range;
		timer = new Timer(delay);
	}
	
	public boolean update(DynamicEntity leader) {
		if(triggered)
			return false;
		
		if(MathUtil.getDistance(position.x, position.y, leader.getPosition().x, leader.getPosition().y) <= range) {
			triggered = true;
			map.addFloatingDialog(text, (int) speaker.getPosition().x - 20, (int)(speaker.getPosition().y - speaker.getShape().getHeight() - 30), timer.getDelay());
			timer.update();
			
			if(speaker.getCurrentPath() != null) {
				speaker.getCurrentPath().cut();
			}
			if(leader.getCurrentPath() != null) {
				leader.getCurrentPath().cut();
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean isDone() {
		return triggered && timer.tick();
	}
	
	public boolean isPlaying() {
		return triggered && !timer.tick();
	}

}
