package com.calderagames.spacelab.audio;

import java.nio.IntBuffer;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector2f;

public class SoundEffect {
	private IntBuffer source;
	private Vector2f pos;
	private float nativeGain;

	public SoundEffect(IntBuffer source, float nativeGain, float x, float y) {
		pos = new Vector2f(x, y);
		this.source = source;
		this.nativeGain = nativeGain;
	}

	public float getX() {
		return pos.getX();
	}

	public float getY() {
		return pos.getY();
	}

	public int getSource() {
		return source.get(0);
	}

	public void dispose() {
		AL10.alSourceStop(source);
		AL10.alDeleteSources(source);
	}

	public float getNativeGain() {
		return nativeGain;
	}

	public float getGain() {
		return AL10.alGetSourcef(source.get(0), AL10.AL_GAIN);
	}

	public void setGain(float gain) {
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, gain);
	}
}
