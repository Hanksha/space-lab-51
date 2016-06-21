package com.calderagames.spacelab.entities;

import java.util.ArrayList;
import java.util.Comparator;

import com.calderagames.spacelab.gamecontent.GameContent;

public class BattleManager {

	private class InitiativeComparator implements Comparator<DynamicEntity> {
		@Override
		public int compare(DynamicEntity e1, DynamicEntity e2) {
			int ip1 = e1.getInitiativePoints();
			int ip2 = e2.getInitiativePoints();

			if(ip1 < ip2)
				return 1;
			if(ip1 > ip2)
				return -1;

			return 0;
		}

	}

	private GameContent gc;
	/**Player group reference*/
	private EntityGroup pGroup;
	/**Opponent group reference*/
	private EntityGroup opGroup;
	/**List of all the entity involved in the current battle,
	 * ordered by initiative points*/
	private ArrayList<DynamicEntity> entities;
	/**Entity which is playing its turn*/
	private DynamicEntity playingEntity;
	/**Boolean flag, true if a battle is on going*/
	private boolean isBattle;
	/**Index of the entity in the list which play the turn*/
	private int entityIndex;
	/**Turn counter (bonus xp for low turn count)*/
	private int turnCounter;

	public BattleManager(GameContent gc, EntityGroup group) {
		this.gc = gc;
		pGroup = group;

		entities = new ArrayList<DynamicEntity>();
	}

	public void update(double dt) {
		if(!isBattle)
			return;

		if(!playingEntity.isTurn()) {

			do {
				entityIndex++;

				if(entityIndex == entities.size())
					entityIndex = 0;

				playingEntity = entities.get(entityIndex);

			} while(playingEntity.isDead());
			
			if(playingEntity instanceof PlayableEntity)
				pGroup.setLeader(playingEntity);

			playingEntity.setTurn(true);
			turnCounter++;
		}

		checkResult();
	}

	/**
	 * @param opponent opponent group to the player group
	 * @param initiate true if the player group initiate the battle (more initiative points)
	 */
	public void startBattle(EntityGroup opponent, boolean initiate) {
		if(!isBattle) {
		
    		if(initiate) {
    			for(DynamicEntity e : pGroup.getEntities())
    				entities.add(e);
    
    			for(DynamicEntity e : opponent.getEntities())
    				entities.add(e);
    		}
    		else {
    			for(DynamicEntity e : opponent.getEntities())
    				entities.add(e);
    
    			for(DynamicEntity e : pGroup.getEntities())
    				entities.add(e);
    		}
    
    		entities.sort(new InitiativeComparator());
    
    		for(DynamicEntity e : entities) {
    			e.setTurnBased(true);
    			e.setTurn(false);
    			e.addBonusAP(e.getInitiativePoints() / 3);
    		}
    
    		playingEntity = entities.get(0);
    		playingEntity.setTurn(true);
    
    		if(playingEntity instanceof PlayableEntity)
    			pGroup.setLeader(playingEntity);
    
    		opGroup = opponent;
    
    		entityIndex = 0;
    		
    		isBattle = true;
		}
	}

	private void checkResult() {
		if(pGroup.isGroupDead() || opGroup.isGroupDead()) {

			isBattle = false;

			for(DynamicEntity e : entities) {
				e.setTurnBased(false);
				e.setTurn(false);
			}

			if(opGroup.isGroupDead()) {
				for(DynamicEntity e : pGroup.getEntities()) {
					e.addXP(50 * opGroup.getEntities().size());
				}
			}
			
			playingEntity = null;
			opGroup = null;
			
			entities.clear();
		}
	}

	public boolean isBattle() {
		return isBattle;
	}

	public ArrayList<DynamicEntity> getEntities() {
		return entities;
	}

	public ArrayList<DynamicEntity> getOpponentGroup() {
		if(opGroup != null)	{
			return opGroup.getEntities();
		}
		else
			return null;
	}
	
	public ArrayList<DynamicEntity> getPlayerGroup() {
		return pGroup.getEntities();
	}
	
	public DynamicEntity getPlayingEntity() {
		if(!isBattle)
			return null;

		return entities.get(entityIndex);
	}

	public int getPlayingEntityIndex() {
		return entityIndex;
	}
}
