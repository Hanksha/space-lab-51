package com.calderagames.spacelab.hud;

import org.newdawn.slick.geom.Rectangle;

import com.calderagames.spacelab.graphics.Color;
import com.calderagames.spacelab.graphics.SpriteBatch;

public class MenuItem {

	private String actionCommand;
	private RectangleText rectText;

	public MenuItem(RectangleText rectText, String actionCommand) {
		this.rectText = rectText;
		this.actionCommand = actionCommand;
	}

	public String getActionCommand() {
		return actionCommand;
	}

	public Rectangle getRectangle() {
		return rectText;
	}

	public void render(SpriteBatch sb, Color color) {
		rectText.render(sb, color);
	}
}
