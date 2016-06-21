package com.calderagames.spacelab.entities;

import com.calderagames.spacelab.font.TrueTypeFont;
import com.calderagames.spacelab.graphics.Color;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.util.Timer;

public class FloatingText {

	public static final int RED_BLOOD = 0;
	public static final int GREEN_HEALTHY = 1;
	public static final int ORANGE_HEALTHY = 2;
	public static final int INFO = 3;
	public static final int VIOLET_POISON = 4;
	public static final int WHITE = 5;

	private float x, y;
	private int width, height;
	private String text;
	private Timer timer;
	private TrueTypeFont font;
	private Color color;
	private boolean dynamic = true;
	private float fade;

	public FloatingText(float x, float y, String text, TrueTypeFont font, int delay, int colorType) {
		this(x, y, text, font, delay);

		switch(colorType) {
		case RED_BLOOD:
			this.color = new Color(Color.RED_BLOOD);
			break;
		case GREEN_HEALTHY:
			this.color = new Color(Color.GREEN_HEALTHY);
			break;
		case ORANGE_HEALTHY:
			this.color = new Color(Color.ORANGE_FIRE);
			break;
		case INFO:
			this.color = new Color(Color.WHITE);
			dynamic = false;
			break;
		case VIOLET_POISON:
			this.color = new Color(Color.VIOLET_POISON);
			break;
		case WHITE:
			this.color = new Color(Color.WHITE);
			break;
		default:
			break;
		}

		fade = 90;
	}

	public FloatingText(float x, float y, String text, TrueTypeFont font, int delay) {
		this.x = x;
		this.y = y;
		this.text = text.toUpperCase();
		this.font = font;
		timer = new Timer(delay);

		width = font.getWidth(text);
		height = font.getHeight(text);

		color = new Color(Color.WHITE);
	}

	public void update(double dt) {
		if(dynamic)
			y -= 100 * dt;

		if(fade > 0) {
			if(dynamic)
				fade -= 100 * dt;
			else if(timer.tick())
				fade -= 200 * dt;

			if(fade < 0)
				fade = 0;
		}

		color.a = (float) Math.sin(Math.toRadians(fade));
	}

	public boolean isOver() {
		return (timer.tick() && dynamic) || (!dynamic && fade == 0);
	}

	public void render(SpriteBatch sb) {
		font.drawString(sb, x - width / 2, y - height / 2, text, color);
	}
}
