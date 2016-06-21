package com.calderagames.spacelab.entities.items;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;

public class EmmergencyKit extends Item {

	public EmmergencyKit(GameContent gc, Map map, int row, int col) {
		super(gc, map, row, col, 4);

		ItemFactory.makeItem(id, this, gc.getRM());
	}

	@Override
	public boolean use(DynamicEntity user, DynamicEntity target) {
		if(user.isTurnbased() && !user.isTurn())
			return false;

		if(!target.isDead())
			return false;
		
		if(user.getActionPoints() < useCost)
			return false;

		user.useActionPoint(useCost);
		applyItem(target);

		return true;
	}

	@Override
	protected void applyItem(DynamicEntity target) {
		super.applyItem(target);

		target.setIsDead(false);
		target.healDamage(50);
	}
}
