package com.calderagames.spacelab.entities.capacities;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.TextureRegion;

public class MedigelBlast extends MachineGunBlast {

	public MedigelBlast(GameContent gc, DynamicEntity owner, int value, int range) {
		super(gc, owner, value, range);
		setIcon(new Sprite(42, 42, new TextureRegion(gc.getRM().getTexture("texatlas"), 67, 612, 21, 21)));
		sprBullet = new Sprite(24, 36, new TextureRegion(gc.getRM().getTexture("texatlas"), 15, 635, 6, 9));
		timerCandence.setDelay(300);
		maxBullet = 3;
		damageBullet = value / maxBullet;
		soundId = "medigel-splash";
		desc = "Medigel blast $Cost: 2 $CD: 1 turn $range: " + range;
		canTargetOwner = true;
	}

	@Override
	protected void actionOnTouch() {
		if(currTarget instanceof DynamicEntity) {
			((DynamicEntity) currTarget).healDamage(damageBullet);
		}
	}
}
