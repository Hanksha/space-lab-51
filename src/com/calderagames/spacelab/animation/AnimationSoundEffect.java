package com.calderagames.spacelab.animation;

import com.calderagames.spacelab.audio.AudioSystem;

public class AnimationSoundEffect {

	private int[] keyFrames;
	private String id;
	private boolean random;
	private float pitch;
	private float gain;
	private int offsetX;
	private int offsetY;
	private int prevFrame = -1;

	public AnimationSoundEffect(int[] keyFrames, String soundId, boolean random, float pitch, float gain, int offsetX, int offsetY) {
		this.keyFrames = keyFrames;
		id = soundId;
		this.random = random;
		this.pitch = pitch;
		this.gain = gain;
		this.offsetX = offsetX;
		this.offsetX = offsetX;
	}

	public void playSound(AudioSystem as, int frame, float x, float y) {
		if(frame != prevFrame) {
			for(Integer keyFrame : keyFrames) {
				if(keyFrame == frame) {
					as.playSoundEffect(id, pitch, gain, x + offsetX, y + offsetY);
					break;
				}
			}
		}
		prevFrame = frame;
	}

	public void setKeyFrames(int[] keyFrames) {
		this.keyFrames = keyFrames;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public void setPrevFrame(int prevFrame) {
		this.prevFrame = prevFrame;
	}
}
