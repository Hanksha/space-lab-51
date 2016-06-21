package com.calderagames.spacelab.entities.capacities;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.TextureRegion;

public class Move extends Capacity {

	public Move(GameContent gc, DynamicEntity owner) {
		super(gc, owner, 0, 0, 0, "Move");
		
		setIcon(new Sprite(42, 42, new TextureRegion(gc.getRM().getTexture("texatlas"), 1, 612, 21, 21)));
	}

}
