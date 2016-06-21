package com.calderagames.spacelab.animation;

import com.calderagames.spacelab.TremblorSystem;

public class AnimationTremblorEffect {
	private int[] keyFrames;
	private int prevFrame = -1;
	private int force;
	private int delay;
	private int offsetX;
	private int offsetY;

	public AnimationTremblorEffect(int[] keyFrames, int force, int delay, int offsetX, int offsetY) {
		this.keyFrames = keyFrames;
		this.force = force;
		this.delay = delay;
		this.offsetX = offsetX;
		this.offsetX = offsetX;
	}

	public void addTremblor(TremblorSystem ts, int frame, float x, float y) {
		if(frame != prevFrame) {
			for(Integer keyFrame : keyFrames) {
				if(keyFrame == frame) {
					ts.addTremblor(force, x + offsetX, y + offsetY, delay);
					break;
				}
			}
		}
		prevFrame = frame;
	}

	public void setPrevFrame(int prevFrame) {
		this.prevFrame = prevFrame;
	}
}
