package com.calderagames.spacelab;

import org.lwjgl.util.vector.Vector2f;

import com.calderagames.spacelab.util.Timer;

public class Tremblor {

	// distance max to the player to feel the tremblor full intensity
	private final int maxDistance = com.calderagames.spacelab.tiles.Tile.tileSize * 2;

	private Vector2f pos;
	private float intensity; // intensity felt by the player
	private int force; // original force of the tremblor;
	private Timer timer;
	private double distanceToPlayer;

	public Tremblor(int force, int delay, float posX, float posY) {
		timer = new Timer(delay);
		timer.update();
		this.force = force;
		pos = new Vector2f(posX, posY);
	}

	public void update(float centerX, float centerY) {
		distanceToPlayer = getDistance(pos.x, pos.y, centerX, centerY);

		if(distanceToPlayer <= maxDistance)
			intensity = force;
		else {
			intensity = (float) (force * (maxDistance / distanceToPlayer));
		}
	}

	private static double getDistance(float px, float py, float p2x, float p2y) {

		return Math.sqrt(((px - p2x) * (px - p2x)) + ((py - p2y) * (py - p2y)));
	}

	public boolean isOver() {
		return timer.tick();
	}

	public float getIntensity() {
		return intensity;
	}
}
