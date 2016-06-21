package com.calderagames.spacelab.entities.items;

import java.util.ArrayList;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.FloatingText;
import com.calderagames.spacelab.gamecontent.GameContent;

public class Inventory {

	protected GameContent gc;
	protected DynamicEntity owner;
	protected ArrayList<Item> items;
	protected int maxItem = 8;
	protected int turnLeft;
	
	public Inventory(DynamicEntity owner) {
		this.owner = owner;
		
		items = new ArrayList<Item>();
	}
	
	public void updateTurn() {
		turnLeft = Math.max(turnLeft - 1, 0);
	}
	
	public void update(double dt) {
		
	}
	
	public void useItem(int index, DynamicEntity target) {
		if(turnLeft == 0 && index >= 0 && index < items.size()) {
			if(items.get(index).use(owner, target)) {
				items.remove(index);
				turnLeft = 1;
			}
		}
		else
			owner.getMap().addFloatingText("I can't use that yet!", (int) owner.getPosition().x, (int) (owner.getPosition().y - owner.getShape().getHeight() - 10), FloatingText.INFO);
	}
	
	
	public String getDesc(int index) {
		if(index < 0 || index >= items.size())
			return "";
		else
			return items.get(index).getDescription();
	}

	
	public boolean addItem(Item item) {
		if(items.size() == maxItem)
			return false;
		
		items.add(item);
		
		item.pickUp();
		
		return true;
	}
	
	public ArrayList<Item> getItems() {
		return items;
	}
}
