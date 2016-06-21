package com.calderagames.spacelab.entities.capacities;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.util.MathUtil;

public class Interact extends Capacity {

	public Interact(GameContent gc, DynamicEntity owner) {
		super(gc, owner, 0, 0, 92, "Interact");

		setIcon(new Sprite(42, 42, new TextureRegion(gc.getRM().getTexture("texatlas"), 23, 612, 21, 21)));
	}

	@Override
	public boolean use(Entity target) {
		//check the distance with the target
		if(target != null && !target.equals(owner) && MathUtil.getDistance(target.getPosition().x, target.getPosition().y, owner.getPosition().x, owner.getPosition().y) <= range) {
			target.interact(owner);

			return true;
		}

		return false;
	}

	@Override
	public boolean use(int row, int col) {
		if(!owner.getMap().getMapCell(row, col).getEntities().isEmpty())
			return use(owner.getMap().getMapCell(row, col).getEntities().get(0));
		
		return false;
	}
}
