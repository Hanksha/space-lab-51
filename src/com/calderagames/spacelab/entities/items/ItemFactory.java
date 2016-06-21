package com.calderagames.spacelab.entities.items;

import com.calderagames.spacelab.entities.DynamicEntity.Attributes;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamecontent.ResourceManager;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.TextureRegion;

public abstract class ItemFactory {

	public static void makeItem(int id, Item item, ResourceManager rm) {
		//adrenaboost
		if(id == 0) {
			item.setDescription("Adrena Boost $Effect: +5 stamina $Duration: 3 turns $Cost: 2");
			item.setEffects(new Effect[] { new Effect(3, Attributes.STAMINA, 5, "adrenaboost", "+5 stamina") });
			item.setUseCost(2);
			item.setSprite(new Sprite(46, 46, new TextureRegion(rm.getTexture("texatlas"), 261, 580, 23, 23)));
		}
		//medipack
		else if(id == 1) {
			item.setDescription("Medipack $Effect: +5 robustness $Duration: 5 turns $Cost: 2");
			item.setEffects(new Effect[] { new Effect(5, Attributes.ROBUSTNESS, 5, "medipack", "+5 robustness") });
			item.setUseCost(2);
			item.setSprite(new Sprite(46, 46, new TextureRegion(rm.getTexture("texatlas"), 285, 604, 23, 23)));
		}
		//vitaboost
		else if(id == 2) {
			item.setDescription("Vita Boost $Effect: +10 velocity $Duration: 2 turns $Cost: 2");
			item.setEffects(new Effect[] { new Effect(2, Attributes.VELOCITY, 10, "vitaboost", "+10 velocity") });
			item.setUseCost(2);
			item.setSprite(new Sprite(46, 46, new TextureRegion(rm.getTexture("texatlas"), 309, 580, 23, 23)));
		}
		//eyes drop
		else if(id == 3) {
			item.setDescription("Eyes drop $Effect: +20 precision $Duration: 2 turns $Cost: 2");
			item.setEffects(new Effect[] { new Effect(2, Attributes.PRECISION, 20, "eyesdrop", "+20 precision") });
			item.setUseCost(2);
			item.setSprite(new Sprite(46, 46, new TextureRegion(rm.getTexture("texatlas"), 333, 580, 23, 23)));
		}
		else if(id == 4) {
			item.setDescription("Emmergency Kit $Revive a dead partner $Cost: 6");
			item.setEffects(new Effect[] {});
			item.setUseCost(6);
			item.setSprite(new Sprite(46, 46, new TextureRegion(rm.getTexture("texatlas"), 261, 604, 23, 23)));
		}
		else if(id == 5) {
			item.setDescription("Medigel $Heal the wounds $Cost: 3");
			item.setEffects(new Effect[] {});
			item.setUseCost(3);
			item.setSprite(new Sprite(46, 46, new TextureRegion(rm.getTexture("texatlas"), 285, 580, 23, 23)));
		}
	}

	public static Item createItem(GameContent gc, Map map, int id) {
		Item item = null;
		
		if(id == 0)
			item = new Item(gc, map, 0, 0, id);
		else if(id == 1)
			item = new Item(gc, map, 0, 0, id);
		else if(id == 2)
			item = new Item(gc, map, 0, 0, id);
		else if(id == 3)
			item = new Item(gc, map, 0, 0, id);
		else if(id == 4)
			item = new EmmergencyKit(gc, map, 0, 0);
		else if(id == 5)
			item = new MediGel(gc, map, 0, 0);
		
		makeItem(id, item, gc.getRM());
		
		return item;
	}
}
