package com.calderagames.spacelab.graphics;

public class Color {

	// Colors
	public static final Color RED_BLOOD = new Color(198, 62, 62, 255);
	public static final Color GREEN_HEALTHY = new Color(62, 198, 100, 255);
	public static final Color ORANGE_FIRE = new Color(198, 116, 62, 255);
	public static final Color VIOLET_POISON = new Color(139, 62, 198, 255);
	public static final Color WHITE = new Color(255, 255, 255, 255);

	public float r, g, b, a;

	public Color() {
		r = 1f;
		g = 1f;
		b = 1f;
		a = 1f;
	}

	public Color(Color color) {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	}

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1f;
	}

	public Color(int r, int g, int b, int a) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
		this.a = a / 255f;
	}

	public Color(int r, int g, int b) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
		this.a = 1f;
	}

	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setColor(int r, int g, int b, int a) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
		this.a = a / 255f;
	}

	public void setColor(Color color) {
		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
	}
}