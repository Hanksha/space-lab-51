package com.calderagames.spacelab.animation;

import org.lwjgl.util.vector.Vector2f;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.SpriteBatch;

public class AnimationSet {
	private GameContent gc;

	private Animation[] animations;
	private Vector2f[] offsetsX;
	private Vector2f[] offsetsY;
	private AnimationSoundEffect[] sounds;
	private AnimationTremblorEffect[] tremblors;

	private int currentIndex;

	public AnimationSet(GameContent gc, int numAnimation) {
		this.gc = gc;
		animations = new Animation[numAnimation];
		offsetsX = new Vector2f[numAnimation];
		offsetsY = new Vector2f[numAnimation];
		sounds = new AnimationSoundEffect[numAnimation];
		tremblors = new AnimationTremblorEffect[numAnimation];

		// init vectors
		for(int i = 0; i < numAnimation; i++) {
			offsetsX[i] = new Vector2f(0, 0);
			offsetsY[i] = new Vector2f(0, 0);
		}

		currentIndex = 0;
	}

	public void setAnimation(int index, Animation anim) {
		animations[index] = anim;
	}

	public void setOffsetX(int index, Vector2f offsetX) {
		offsetsX[index] = offsetX;
	}

	public void setOffsetY(int index, Vector2f offsetY) {
		offsetsY[index] = offsetY;
	}

	public void setSoundEffect(int index, int[] keyFrames, String soundEffectId, boolean random, float pitch, float gain, int offsetX, int offsetY) {
		if(sounds[index] == null)
			sounds[index] = new AnimationSoundEffect(keyFrames, soundEffectId, random, pitch, gain, offsetX, offsetY);
		else {
			sounds[index].setKeyFrames(keyFrames);
			sounds[index].setId(soundEffectId);
			sounds[index].setRandom(random);
			sounds[index].setPitch(pitch);
			sounds[index].setGain(gain);
		}
	}

	public void setTremblorEffect(int index, int[] keyFrames, int force, int delay, int offsetX, int offsetY) {
		tremblors[index] = new AnimationTremblorEffect(keyFrames, force, delay, offsetX, offsetY);
	}

	public void setIndex(int index) {
		currentIndex = index;
		animations[index].reset();
	}

	public int getIndex() {
		return currentIndex;
	}
	
	public Animation getCurrentAnimation() {
		return animations[currentIndex];
	}

	public Animation getAnimation(int index) {
		return animations[index];
	}

	public int getSize() {
		return animations.length;
	}

	public void update(float x, float y) {
		animations[currentIndex].update();
		playAnimationSound(x, y);
		playAnimationTremblor(x, y);
	}

	private void playAnimationSound(float x, float y) {
		if(sounds[currentIndex] != null)
			sounds[currentIndex].playSound(gc.getAS(), animations[currentIndex].getCurrentFrameIndex(), x, y);
	}

	private void playAnimationTremblor(float x, float y) {
		if(tremblors[currentIndex] != null)
			tremblors[currentIndex].addTremblor(gc.getTS(), animations[currentIndex].getCurrentFrameIndex(), x, y);
	}

	public void render(SpriteBatch spriteBatcher, float x, float y, boolean flip, int z_order) {
		animations[currentIndex].render(spriteBatcher, (int) x + (!flip ? offsetsX[currentIndex].x : offsetsX[currentIndex].y),
										(int) y + offsetsY[currentIndex].y, flip, z_order);
	}
}
