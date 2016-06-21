package com.calderagames.spacelab.entities.items;

import com.calderagames.spacelab.entities.DynamicEntity;

public class Effect {

	/**Duration of the effect in turn unit*/
	public int duration;
	/**Number of turn left*/
	public int turnLeft;
	/**Target attribute*/
	public DynamicEntity.Attributes targetAttr;
	/**Value (can be positive or negative*/
	public int value;
	/**String ID that identify the effect (mostly for dynamic entities to not have duplicate effect)*/
	public String stringID;
	/**Name and description (infotip)*/
	public String desc;
	
	public Effect(int duration, DynamicEntity.Attributes targetAttr, int value, String stringID, String desc) {
		this.duration = duration;
		this.targetAttr = targetAttr;
		this.value = value;
		this.stringID = stringID;
		this.desc = desc;
		
		turnLeft = duration;
	}
	
	public void apply() {
		turnLeft = Math.max(turnLeft - 1, 0);
	}
	
	public boolean isOver() {
		return turnLeft == 0;
	}
	
	public Effect duplicate() {
		return new Effect(duration, targetAttr, value, stringID, desc);
	}
}
