package com.calderagames.spacelab.entities.capacities;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.util.Timer;

public class Punch extends Capacity {

	protected int damage;
	
	protected Timer timerPunch;
	protected Timer timerClaws;
	protected Timer timerGlobal;
	
	
	protected boolean punched;
	
	protected Sprite sprClaw;
	
	public Punch(GameContent gc, DynamicEntity owner, int damage) {
		super(gc, owner, 1, 0, 92, "Basic gun short $Cost: 1 $CD: 0 turn $Damage: " + damage + "$Range: " + 92);

		this.damage = damage;
		
		timerPunch = new Timer(400);
		timerClaws = new Timer(400);
		timerGlobal = new Timer(800);
		
		sprClaw = new Sprite(64, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 1, 646, 32, 32));
	}
	
	@Override
	public void update(double dt) {
		if(!punched && timerPunch.tick()) {
			if(currTarget instanceof DynamicEntity) {
				((DynamicEntity) currTarget).attack(owner, damage);
				punched = true;
				gc.getAS().playSoundEffect(1f, 0.5f, "bite");
				timerClaws.update();
			}
		}
	}
	
	@Override
	public void render(SpriteBatch sb) {
		super.render(sb);
		if(currTarget != null && !timerClaws.tick() && punched)
			sb.draw(currTarget.getPosition().x, currTarget.getPosition().y - currTarget.getShape().getHeight() / 2, sprClaw, (int) currTarget.getPosition().y + 1);
	}

	@Override
	public boolean use(Entity target) {
		if(super.use(target)) {
			punched = false;
			timerPunch.update();
			timerGlobal.update();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canUse() {
		return super.canUse() && timerGlobal.tick();
	}
}
