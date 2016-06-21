package com.calderagames.spacelab.entities.items;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;

public class MediGel extends Item {

	public MediGel(GameContent gc, Map map, int row, int col) {
		super(gc, map, row, col, 5);
		
		ItemFactory.makeItem(id, this, gc.getRM());
	}
	
	@Override
	protected void applyItem(DynamicEntity target) {
		target.healDamage(30);
	}
}
