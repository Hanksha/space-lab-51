package com.calderagames.spacelab.graphics;

public class TextureRegion {

	private float[] texCoords;

	private float x, y, width, height;

	public TextureRegion(float texWidth, float texHeight, float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		x /= texWidth;
		y /= texHeight;
		width /= texWidth;
		height /= texHeight;

		texCoords = new float[] { x, y, x + width, y, x, y + height, x + width, y, x + width, y + height, x, y + height, };
	}

	public TextureRegion(Texture texture, float x, float y, float width, float height) {
		this(texture.getTextureWidth(), texture.getTextureHeight(), x, y, width, height);
	}

	public TextureRegion(float[] coords) {
		texCoords = coords;
	}

	public float[] getRegion() {
		return texCoords;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
