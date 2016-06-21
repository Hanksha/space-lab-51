package com.calderagames.spacelab.graphics;

public class Sprite {
	public TextureRegion texRegion;
	public float width, height;

	public Sprite() {
	}

	public Sprite(float width, float height, TextureRegion texRegion) {
		this.width = width;
		this.height = height;
		this.texRegion = texRegion;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
