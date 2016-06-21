package com.calderagames.spacelab.animation;

import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.util.Timer;

public class Animation {

	private TextureRegion[] frames;
	private Sprite sprite;
	private Timer timerAnimation;
	private int currentFrame;
	private boolean hasPlayedOnce;
	private boolean enablePlayOnce;

	public Animation(float width, float height, TextureRegion[] frames, int delay, boolean playOnce) {
		timerAnimation = new Timer(delay);
		sprite = new Sprite();
		setAnimation(width, height, frames, delay, playOnce);
	}

	public void setAnimation(float width, float height, TextureRegion[] frames, int delay, boolean playOnce) {
		this.frames = frames;
		sprite.setWidth(width);
		sprite.setHeight(height);
		sprite.texRegion = frames[0];
		timerAnimation.setDelay(delay);
		enablePlayOnce = playOnce;
		currentFrame = 0;
		hasPlayedOnce = false;
	}

	public void setDelay(int delay) {
		timerAnimation.setDelay(delay);
	}

	public void setPlayOnce(boolean b) {
		enablePlayOnce = b;
	}

	public Sprite getFrame() {
		if(sprite.texRegion == null)
			sprite.texRegion = frames[currentFrame];

		return sprite;
	}

	public TextureRegion[] getFrames() {
		return frames;
	}

	public boolean hasPlayed() {
		return hasPlayedOnce;
	}

	public int getCurrentFrameIndex() {
		return currentFrame;
	}

	public boolean isLastFrame() {
		if(currentFrame == frames.length - 1)
			return true;
		else
			return false;
	}

	public void reset() {
		currentFrame = 0;
		hasPlayedOnce = false;
		timerAnimation.update();
		sprite.texRegion = frames[currentFrame];
	}

	public void update() {

		if(timerAnimation.getDelay() < 0)
			return;

		if(enablePlayOnce) {
			if(hasPlayedOnce)
				return;
		}

		if(timerAnimation.tick()) {

			currentFrame++;

			if(currentFrame >= frames.length) {
				hasPlayedOnce = true;
				if(!enablePlayOnce)
					currentFrame = 0;
				else
					currentFrame = frames.length - 1;
			}

			timerAnimation.update();
			sprite.texRegion = frames[currentFrame];
		}

	}

	public void render(SpriteBatch spriteBatcher, float x, float y, boolean flip, int z_order) {
		spriteBatcher.draw(getFrame(), x, y, flip, z_order);
	}
}
