package com.calderagames.spacelab.entities;

import java.util.ArrayList;

import com.calderagames.spacelab.entities.GroupEvent.GroupEventType;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.path.Path;
import com.calderagames.spacelab.tiles.Tile;

public class EntityGroup {

	/**FEE: no formation the entities do not follow each other</br>
	 * LINE QUEUE: the entities fall in line behind the group leader</br>
	 * LINE FRONT: the entities form a line from each side of group leader</br>
	 * SPREAD: the entities form a V shape with the group leader at the front
	 * */
	public enum GroupFormation {FREE, LINE_QUEUE, LINE_FRONT, SPREAD}
	
	/**Reference to game content*/
	protected GameContent gc;
	
	/**Contains the entity of the group**/
	protected ArrayList<DynamicEntity> entities;
	
	/**Formation of the group*/
	protected GroupFormation formation;
	
	protected DynamicEntity leader;
	
	protected int leaderIndex;
	
	protected EntityGroupListener gl;
	
	public EntityGroup(GameContent gc) {
		this.gc = gc;
		entities = new ArrayList<>();
	}
	
	public EntityGroup(GameContent gc, GroupFormation formation) {
		this(gc);
		this.formation = formation;
	}
	
	public void update(double dt) {
		for(DynamicEntity entity: entities)
			entity.update(dt);
	}
	
	public void render(SpriteBatch sb) {
		for(DynamicEntity entity: entities)
			entity.render(sb);
	}
	
	public void addEntity(DynamicEntity entity) {
		entities.add(entity);
		entity.setGroup(this);
		
		if(gl != null)
			gl.fireEvent(new GroupEvent(entity, GroupEventType.NEW_MEMBER));
	}
	
	public void fireEvent(GroupEvent event) {
		if(gl != null)
			gl.fireEvent(event);
		
		if(formation == GroupFormation.FREE)
			return;
		
		if(event.type == GroupEventType.STOP) {
			for(DynamicEntity entity: entities) {
				if(entity.getCurrentPath() != null) {
					entity.getCurrentPath().cut();
				}
			}
		}
		else if(event.type == GroupEventType.MOVE) {
			DynamicEntity prevEntity = event.source;
			Path path = null;
			int targetRow = event.targetRow;
			int targetCol = event.targetCol;
			
			for(int i = 0; i < entities.size(); i++) {
				DynamicEntity entity = entities.get(i);
				
				if(entity.equals(event.source) || entity.isDead())
					continue;
				
				targetRow = (int) (prevEntity.getPosition().y / Tile.tileSize);
				targetCol = (int) (prevEntity.getPosition().x / Tile.tileSize);
				
				gc.getPathFinder().searchPath((int) (entity.getPosition().y / Tile.tileSize), (int) (entity.getPosition().x / Tile.tileSize), targetRow, targetCol, false);
				
				path = gc.getPathFinder().getPath();
				path.getPoints().remove(path.getPoints().size() - 1);
				
				path.affix(prevEntity.getCurrentPath(), prevEntity.getCurrentPath().getPoints().size() - 1);
				
				entity.setPath(path);
				
				prevEntity = entity;
			}
		}
	}
	
	public void setLeader(int index) {
		if(index >= 0 && index < entities.size()) {
			if(entities.get(index).isDead())
				return;
			
			leaderIndex = index;
			leader = entities.get(index);
			fireEvent(new GroupEvent(null, GroupEventType.LEADER_CHANGE));
		}
	}
	
	public void setLeader(DynamicEntity entity) {
		if(entities.contains(entity)) {
			
			int index = 0;
			for(DynamicEntity e: entities) {
				if(e.equals(entity)) {
					if(e.isDead())
						break;
					leaderIndex = index;
					
					leader = e;
					fireEvent(new GroupEvent(null, GroupEventType.LEADER_CHANGE));
					break;
				}
				
				index++;
			}
		}
	}
	
	public void setNextLeader() {
		leaderIndex++;
		
		if(leaderIndex >= entities.size())
			leaderIndex = 0;
		
		leader = entities.get(leaderIndex);
		fireEvent(new GroupEvent(null, GroupEventType.LEADER_CHANGE));
	}
	
	public void setFormation(GroupFormation formation) {
		this.formation = formation;
	}
	
	public void setGroupListener(EntityGroupListener gl) {
		this.gl = gl;
	}
	
	public DynamicEntity getLeader() {
		return leader;
	}
	
	public ArrayList<DynamicEntity> getEntities() {
		return entities;
	}
	
	public boolean isGroupDead() {
		for(DynamicEntity e: entities) {
			if(!e.isDead())
				return false;
		}
		
		return true;
	}
}
