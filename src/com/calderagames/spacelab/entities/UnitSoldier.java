package com.calderagames.spacelab.entities;

import com.calderagames.spacelab.animation.Animation;
import com.calderagames.spacelab.entities.capacities.MachineGunBlast;
import com.calderagames.spacelab.entities.capacities.SimpleGunShot;
import com.calderagames.spacelab.entities.items.ItemFactory;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.Texture;
import com.calderagames.spacelab.graphics.TextureRegion;

public class UnitSoldier extends PlayableEntity {

	public UnitSoldier(GameContent gc, Map map, BattleManager bm, int row, int col) {
		super(gc, map, bm, row, col);
		
		Texture texture = gc.getRM().getTexture("texatlas");
		sprite = new Sprite(42, 58, new TextureRegion(texture, 513, 1, 21, 31));
		sprPortrait = new Sprite(72, 60, new TextureRegion(texture, 93, 514, 36, 30));
		sprMiniPortrait = new Sprite(34, 32, new TextureRegion(texture, 515, 3, 17, 16));
		sprDead = new Sprite(64, 64, new TextureRegion(texture, 513, 386, 32, 32));
		sprWeapon = new Sprite(17, 54, new TextureRegion(texture, 536, 420, 17, 54));
		
		animSet.setAnimation(0, new Animation(42, 58, new TextureRegion[8], 100, false));
		animSet.setSoundEffect(0, new int[] {0, 4}, "footstep-1", false, 1f, 0.1f, 0, 0);
		for(int i = 0; i < animSet.getAnimation(0).getFrames().length; i++) {
			animSet.getAnimation(0).getFrames()[i] = new TextureRegion(texture, 513 + 22 * i, 1, 21, 31);
		}
		
		animSet.setAnimation(1, new Animation(42, 58, new TextureRegion[6], 100, false));
		animSet.setSoundEffect(1, new int[] {0, 3}, "footstep-1", false, 1f, 0.1f, 0, 0);
		for(int i = 0; i < animSet.getAnimation(1).getFrames().length; i++) {
			animSet.getAnimation(1).getFrames()[i] = new TextureRegion(texture, 513 + 22 * i, 33, 21, 31);
		}
		
		animSet.setAnimation(2, new Animation(42, 58, new TextureRegion[6], 100, false));
		animSet.setSoundEffect(2, new int[] {0, 3}, "footstep-1", false, 1f, 0.1f, 0, 0);
		for(int i = 0; i < animSet.getAnimation(2).getFrames().length; i++) {
			animSet.getAnimation(2).getFrames()[i] = new TextureRegion(texture, 513 + 22 * i, 65, 21, 31);
		}
		
		moveCost = 1;
		actionPoints = 0;
		maxActionPoints = 5;
		maxDamage = 100;
		damageAmount = 0;
		
		setAttribute(Attributes.ROBUSTNESS, 8);
		setAttribute(Attributes.STAMINA, 4);
		setAttribute(Attributes.PRECISION, 5);
		setAttribute(Attributes.VELOCITY, 2);
		setAttribute(Attributes.TACTICAL, 3);
		
		capacities.add(new SimpleGunShot(gc, this, 10, 500));
		capacities.add(new MachineGunBlast(gc, this, 50, 800));
		selectedCapa = capacities.get(0);
		
		inv.getItems().add(ItemFactory.createItem(gc, map, 0));
		inv.getItems().add(ItemFactory.createItem(gc, map, 1));
		inv.getItems().add(ItemFactory.createItem(gc, map, 4));
		inv.getItems().add(ItemFactory.createItem(gc, map, 5));
	}
	
	@Override
	public void interact(Entity source) {
		super.interact(source);
	}
	
	@Override
	public String toString() {
		return "Soldier position:" + position.toString();
	}
}
