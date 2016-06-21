package com.calderagames.spacelab.tiles;

import java.util.ArrayList;

import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.util.Timer;

public class AnimatedTileManager {

	private class AnimatedTile {

		private int id;
		private Timer timer;
		private TextureRegion[] frames;
		private int currentIndex;

		public AnimatedTile(int id, int delay, TextureRegion[] frames) {
			this.id = id;
			timer = new Timer(delay);
			this.frames = frames;
			currentIndex = 0;
		}

		public void update() {
			if(timer.tick()) {
				timer.update();

				currentIndex++;

				if(currentIndex >= frames.length)
					currentIndex = 0;
			}
		}

		public TextureRegion getTextureRegion() {
			return frames[currentIndex];
		}

		public int getId() {
			return id;
		}
	}

	private ArrayList<AnimatedTile> animTiles;

	public AnimatedTileManager() {
		animTiles = new ArrayList<AnimatedTile>();
	}

	/**
	 * @param delay
	 * @param frames
	 * @return the index of the animated tile in the array list
	 */
	public int addAnimatedTile(int id, int delay, TextureRegion[] frames) {

		animTiles.add(new AnimatedTile(id, delay, frames));

		return animTiles.size() - 1;
	}

	public int contains(int id) {

		for(int i = 0; i < animTiles.size(); i++) {
			if(id == animTiles.get(i).getId())
				return i;
		}

		return -1;
	}

	public void update() {
		for(AnimatedTile at : animTiles)
			at.update();
	}

	public TextureRegion getTextureRegion(int index) {
		return animTiles.get(index).getTextureRegion();
	}
}
