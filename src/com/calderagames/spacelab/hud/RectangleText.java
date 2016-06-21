package com.calderagames.spacelab.hud;

import org.newdawn.slick.geom.Rectangle;

import com.calderagames.spacelab.font.TrueTypeFont;
import com.calderagames.spacelab.graphics.Color;
import com.calderagames.spacelab.graphics.SpriteBatch;

public class RectangleText extends Rectangle {

	private static final long serialVersionUID = 1L;
	private TrueTypeFont font;
	private String text;
	private boolean centerX, centerY, flipX;

	public RectangleText(int x, int y, String text, TrueTypeFont font, boolean centerX, boolean centerY, boolean flipX) {
		this(x, y, text, font, centerX, centerY);
		this.flipX = flipX;
	}

	public RectangleText(int x, int y, String text, TrueTypeFont font, boolean centerX, boolean centerY) {
		this(x, y, text, font, centerX);
		this.centerY = centerY;
	}

	public RectangleText(int x, int y, String text, TrueTypeFont font, boolean centerX) {
		this(x, y, text, font);
		this.centerX = centerX;
	}

	public RectangleText(int x, int y, String text, TrueTypeFont font) {
		super(x, y, font.getWidth(text), font.getHeight(text));

		this.text = text;
		this.font = font;
	}

	public void render(SpriteBatch sb, Color color) {
		updateSize();
		font.drawString(sb, x - (centerX ? width / 2 : 0) - (flipX ? width : 0), y - (centerY ? height / 2 : 0), text, color);
	}

	@Override
	public boolean contains(float xp, float yp) {
		if(xp <= x - (centerX ? width / 2 : 0) - (flipX ? width : 0)) {
			return false;
		}
		if(yp <= y - (centerY ? height / 2 : 0)) {
			return false;
		}
		if(xp >= x - (centerX ? width / 2 : 0) - (flipX ? width : 0) + width) {
			return false;
		}
		if(yp >= y - (centerY ? height / 2 : 0) + height) {
			return false;
		}

		return true;
	}

	public void updateSize() {
		this.width = font.getWidth(text);
		this.height = font.getHeight(text);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setFont(TrueTypeFont font) {
		this.font = font;
	}

	@Override
	public float getX() {
		return x - (centerX ? width / 2 : 0) - (flipX ? width : 0);
	}

	@Override
	public float getY() {
		return y - (centerY ? height / 2 : 0);
	}

	@Override
	public float getMaxX() {
		return x - (centerX ? width / 2 : 0) - (flipX ? width : 0) + width;
	}

	@Override
	public float getMaxY() {
		return y - (centerY ? height / 2 : 0) + height;
	}

	public String getText() {
		return text;
	}
}
