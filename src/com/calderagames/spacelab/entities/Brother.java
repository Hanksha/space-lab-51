package com.calderagames.spacelab.entities;

import org.newdawn.slick.geom.Rectangle;

import com.calderagames.spacelab.entities.capacities.MedigelBlast;
import com.calderagames.spacelab.entities.capacities.SimpleGunShot;
import com.calderagames.spacelab.entities.items.Item;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.Texture;
import com.calderagames.spacelab.graphics.TextureRegion;

public class Brother extends DynamicEntity {

	public Brother(GameContent gc, Map map, int row, int col) {
		super(gc, map, row, col);

		sprite = new Sprite(48, 58, new TextureRegion(gc.getRM().getTexture("texatlas"), 513, 97, 21, 31));
		sprPortrait = new Sprite(72, 60, new TextureRegion(gc.getRM().getTexture("texatlas"), 130, 514, 36, 30));
		sprMiniPortrait = new Sprite(34, 32, new TextureRegion(gc.getRM().getTexture("texatlas"), 515, 99, 17, 16));

		shape = new Rectangle(0, 0, 48, 58);

		moveCost = 1;
		actionPoints = 5;
		maxActionPoints = 5;
		maxDamage = 100;
		damageAmount = 0;

		setAttribute(Attributes.ROBUSTNESS, 2);
		setAttribute(Attributes.STAMINA, 7);
		setAttribute(Attributes.PRECISION, 2);
		setAttribute(Attributes.VELOCITY, 20);
		setAttribute(Attributes.TACTICAL, 1);

		inv.getItems().add(new Item(gc, map, row, col, 0));
		inv.getItems().add(new Item(gc, map, row, col, 1));
		inv.getItems().add(new Item(gc, map, row, col, 2));
		inv.getItems().add(new Item(gc, map, row, col, 3));

		capacities.add(new SimpleGunShot(gc, this, 10, 400));
		capacities.add(new MedigelBlast(gc, this, 30, 400));
		selectedCapa = capacities.get(0);

		Texture texture = gc.getRM().getTexture("texatlas");

		sprite = new Sprite(42, 62, new TextureRegion(texture, 689, 225, 21, 31));
	}

	@Override
	public String toString() {
		return "Danish:" + position.toString();
	}
}
