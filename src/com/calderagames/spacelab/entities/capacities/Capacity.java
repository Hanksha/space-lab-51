package com.calderagames.spacelab.entities.capacities;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.entities.FloatingText;
import com.calderagames.spacelab.entities.PlayableEntity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.tiles.Tile;

public abstract class Capacity {
	/**Reference to game content*/
	protected GameContent gc;
	/**Reference to owner dynamic entity*/
	protected DynamicEntity owner;
	/**Cost in AP to use the capacity*/
	protected int APCost;
	/**Number of turn for the capacity to be available again after use*/
	protected int coolDown;
	/**See {@link Capacity.coolDown}*/
	protected int cdTurnLeft;
	/**Range in pixel of the capacity*/
	protected int range;
	/**Description of the capacity*/
	protected String desc;
	/**Current target*/
	protected Entity currTarget;
	/**Icon that represents the capacity*/
	protected Sprite sprIcon;
	
	protected boolean canTargetOwner;

	public Capacity(GameContent gc, DynamicEntity owner, int APCost, int coolDown, int range, String desc) {
		this.gc = gc;
		this.owner = owner;
		this.APCost = APCost;
		this.coolDown = coolDown;
		this.range = range;
		this.desc = desc;
	}

	public void updateTurn() {
		if(owner.isTurn()) {
			cdTurnLeft = Math.max(cdTurnLeft - 1, 0);
		}
	}

	public void update(double dt) {
	}

	public void render(SpriteBatch sb) {
	}

	public boolean use(int row, int col) {
		if(!owner.getMap().getMapCell(row, col).getEntities().isEmpty()){
			for(Entity entity: owner.getMap().getMapCell(row, col).getEntities())
				if(use(entity))
					return true;
			
			
			return false;
		}
		else
			return false;
	}

	/**
	 * Attempt to use the capacity
	 * @param target target of the capacity
	 * @return true if the capacity can be used.
	 */
	public boolean use(Entity target) {
		//check AP, CD and range
		if(!canUse() || !checkRange(target))
			return false;
		
		//check if target is dead
		if(target instanceof DynamicEntity && ((DynamicEntity) target).isDead())
			return false;
		
		if(!canTargetOwner && target.equals(owner))
			return false;

		owner.useActionPoint(APCost);

		currTarget = target;

		cdTurnLeft = coolDown;

		return true;
	}

	/**
	 * @return true is not under CD and owner has enough AP
	 */
	public boolean canUse() {
		return owner.getActionPoints() >= APCost && isAvailable();
	}

	/**
	 * @param target target entity
	 * @return true if the target is in range and there is no obstacle on the way
	 */
	public boolean checkRange(Entity target) {
		if(target == null)
			return false;

		return checkRange(target.getPosition().x, target.getPosition().y);
	}

	public boolean checkRange(float x, float y) {
		//check for the range
		float dx = x - owner.getPosition().x;
		float dy = y - owner.getPosition().y;

		float distance = (float) Math.sqrt(dx * dx + dy * dy);
		if(distance > range){
			if(owner instanceof PlayableEntity)
				owner.getMap().addFloatingText("My target is too far!", (int) owner.getPosition().x, (int) (owner.getPosition().y - owner.getShape().getHeight() - 10), FloatingText.INFO);
			return false;
		}

		float angle = (float) Math.toDegrees(Math.atan2(dx, dy));

		//check for obstacle
		for(int dist = 0; dist <= distance; dist++) {
			int row = Map.toRow(owner.getPosition().y + (float) (Math.cos(Math.toRadians(angle)) * dist));
			int col = Map.toCol(owner.getPosition().x + (float) (Math.sin(Math.toRadians(angle)) * dist));
			if(owner.getMap().getTileMap().getTypei(row, col, owner.getMap().getTileMap().getCollisionLayerIndex()) == Tile.BLOCK) {
				if(owner instanceof PlayableEntity)
					owner.getMap().addFloatingText("My target is not visible!", (int) owner.getPosition().x, (int) (owner.getPosition().y - owner.getShape().getHeight() - 10), FloatingText.INFO);
				return false;
			}
		}

		return true;
	}

	public void setIcon(Sprite sprite) {
		sprIcon = sprite;
	}

	public Sprite getIcon() {
		return sprIcon;
	}

	public boolean isAvailable() {
		return cdTurnLeft == 0;
	}

	public int getAPCost() {
		return APCost;
	}

	public int getCoolDown() {
		return coolDown;
	}

	public int getCdTurnLeft() {
		return cdTurnLeft;
	}

	public int getRange() {
		return range;
	}

	public String getDesc() {
		return desc;
	}
}
