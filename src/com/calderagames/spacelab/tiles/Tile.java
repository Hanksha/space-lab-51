package com.calderagames.spacelab.tiles;

public class Tile {
	public static final int tileSize = 64;
	
	private float[] texCoords;
	private float[] texCoordsRotated;
	private int type;
	private int nature;

	//tile id
	public static final int BLOCK_ID = 256;
	
	//tile type
	public static final int NORMAL = 0;
	public static final int BLOCK = 1;

	//tile nature
	public static final int NONE = 0;

	public Tile(float[] texCoords, int type, int nature) {
		this.type = type;
		this.nature = nature;
		this.texCoords = texCoords;
	}

	public void setTexCoordsRotated(float[] rotated) {
		texCoordsRotated = rotated;
	}

	public float[] getTexCoords() {
		return texCoords;
	}

	public float[] getTexCoordsRotated() {
		return texCoordsRotated;
	}
	
	public int getType() {
		return type;
	}

	public int getNature() {
		return nature;
	}
}
