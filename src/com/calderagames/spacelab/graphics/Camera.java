package com.calderagames.spacelab.graphics;

import static com.calderagames.spacelab.util.ResolutionHandler.HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.WIDTH;

import com.calderagames.spacelab.gamemap.TileMap;

public class Camera {

	public float x, y;
	private float xmin, xmax, ymin, ymax;

	public Camera() {
	}

	public void updateBounds(TileMap map) {
		xmax = 0;
		xmin = WIDTH - map.getWidth();
		ymax = 0;
		ymin = HEIGHT - map.getHeight();
	}

	public void setX(float x) {
		this.x = x;
		checkBounds();
	}

	public void setY(float y) {
		this.y = y;
		checkBounds();
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	private void checkBounds() {
		if(this.x < xmin)
			this.x = xmin;
		else if(this.x > xmax)
			this.x = xmax;
		if(this.y < ymin)
			this.y = ymin;
		else if(this.y > ymax)
			this.y = ymax;
	}
}
