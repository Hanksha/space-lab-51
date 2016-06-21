package com.calderagames.spacelab.entities;

import java.util.ArrayList;

import org.newdawn.slick.geom.Rectangle;

import com.calderagames.spacelab.animation.AnimationSet;
import com.calderagames.spacelab.entities.GroupEvent.GroupEventType;
import com.calderagames.spacelab.entities.items.RenderableInventory;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.hud.InputRectangle;
import com.calderagames.spacelab.input.InputEvent;
import com.calderagames.spacelab.input.InputListener;
import com.calderagames.spacelab.path.Path;
import com.calderagames.spacelab.tiles.Tile;

public class PlayableEntity extends DynamicEntity implements InputListener {

	protected BattleManager bm;

	protected ArrayList<InputRectangle> rectCapacities;

	protected Sprite sprDead;
	protected Sprite sprWeapon;
	protected Sprite sprLight;

	protected AnimationSet animSet;
	protected boolean facingWeapon;

	public PlayableEntity(GameContent gc, Map map, BattleManager bm, int row, int col) {
		super(gc, map, row, col);
		this.bm = bm;
		shape = new Rectangle(0, 0, 46, 60);
		selectedCapa = capacities.get(0);

		animSet = new AnimationSet(gc, 3);

		sprWeapon = new Sprite(18, 22, new TextureRegion(gc.getRM().getTexture("texatlas"), 515, 420, 18, 22));
		sprLight = new Sprite(188, 188, new TextureRegion(gc.getRM().getTexture("texatlas"), 1, 737, 94, 94));

		inv = new RenderableInventory(gc, this);

		attrPoints = 1;
	}

	@Override
	public void fireInput(InputEvent event) {
		if(event.state) {
			if(event.name.contains("click")) {
				String[] input = event.name.split("-");
				targetActPos.setLocation(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
				//click left
				if(input[3].equals("left")) {
					if(!map.getMapCell(targetActPos.getY(), targetActPos.getX()).equals(currMapCell)) {
    					//if the target cell contains some entities to interact with
    					if(!map.getMapCell(targetActPos.getY(), targetActPos.getX()).getEntities().isEmpty()) {
    						selectedCapa = capacities.get(1);
    						selectedCapa.use(targetActPos.getY(), targetActPos.getX());
    					}
    					//else move at the cell
    					else {
    						selectedCapa = capacities.get(0);
    					}
					}
					
					//move and interact
					if((selectedCapa.getDesc().equals("Move") || selectedCapa.getDesc().equals("Interact")) &&
							map.getTileMap().getTypei(targetActPos.getY(), targetActPos.getX(), map.getTileMap().getCollisionLayerIndex()) != Tile.BLOCK) {
						if(gc.getPathFinder().searchPath(posOnGrid.getY(), posOnGrid.getX(), targetActPos.getY(), targetActPos.getX(), true)) {
							Path path = gc.getPathFinder().getPath();
							
							int cost = getTotalPathCost(path);
							
							if(isTurnBased) {
								//if have enough AP
								if(getActionPoints() >= cost) {
									setPath(path);
									useActionPoint(cost);
								}
								else {
									map.addFloatingText("It's too far!", (int) position.x, (int) (position.y - shape.getHeight() - 10), FloatingText.WHITE);
								}
							}
							else {
								setPath(path);
								if(group != null)
									group.fireEvent(new GroupEvent(this, GroupEventType.MOVE, targetActPos.getY(), targetActPos.getX()));
							}
						}
					}
				}
				//click right
				else {
					if(selectedCapa.use(targetActPos.getY(), targetActPos.getX())) {
						if(!map.getMapCell(targetActPos.getY(), targetActPos.getX()).getEntities().isEmpty()) {
							Entity entity = map.getMapCell(targetActPos.getY(), targetActPos.getX()).getEntities().get(0);
							if(entity instanceof Enemy) {
								bm.startBattle(((DynamicEntity) entity).getGroup(), true);
							}
						}
					}
				}

			}
			else if(event.name.equals("end turn")) {
				isTurn = false;
			}
			else if(event.name.equals("stop")) {
				if(currentPath != null) {
					currentPath.cut();
					group.fireEvent(new GroupEvent(this, GroupEventType.STOP));
				}
			}
		}
	}

	@Override
	protected void executeAction() {
		super.executeAction();
	}

	public void useItem(int index) {
		if(map.getMapCell(targetActPos.getY(), targetActPos.getX()).getEntities().isEmpty()) {
			inv.useItem(index, this);
		}
		else {
			for(Entity entity: map.getMapCell(targetActPos.getY(),targetActPos.getX()).getEntities()) {
				if(entity instanceof PlayableEntity){
					inv.useItem(index,  (PlayableEntity) entity);
					break;
				}
			}
		}
	}
	
	protected void updateAngleDir() {
		if(currentPath == null && !targetActPos.equals(posOnGrid)) {
			float dx = (targetActPos.getX() * Tile.tileSize + Tile.tileSize / 2) - position.x;
			float dy = position.y - (targetActPos.getY() * Tile.tileSize + Tile.tileSize / 2);

			angleDir = (float) Math.toDegrees(Math.atan2(dx, dy));
		}
	}

	@Override
	public void update(double dt) {
		super.update(dt);

		if(!group.getLeader().equals(this)) {
			targetActPos.setLocation(posOnGrid);
		}

		updateAngleDir();

		if(animSet.getCurrentAnimation() != null) {
			if(angleDir >= -45 && angleDir <= 45) {
				if(animSet.getIndex() != 1)
					animSet.setIndex(1);
				facing = false;
				facingWeapon = true;
			}
			else if(angleDir > 45 && angleDir < 135) {
				if(animSet.getIndex() != 2)
					animSet.setIndex(2);
				facing = false;
				facingWeapon = false;
			}
			else if((angleDir <= -135 && angleDir >= -180) || (angleDir >= 135 && angleDir <= 180)) {
				if(animSet.getIndex() != 0)
					animSet.setIndex(0);
				facing = false;
				if(angleDir <= -135 && angleDir >= -180)
					facingWeapon = true;
				else
					facingWeapon = false;
			}
			else if(angleDir < -45 && angleDir > -135) {
				if(animSet.getIndex() != 2)
					animSet.setIndex(2);
				facing = true;
				facingWeapon = true;
			}

			if(currentPath != null)
				animSet.update(position.x, position.y);

			sprite = animSet.getCurrentAnimation().getFrame();
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		if(!isDead()) {
			super.render(sb);
			sb.draw(sprWeapon, position.x - sprWeapon.getWidth() / 2 + (facing ? -10 : 10), position.y - shape.getHeight() / 3.5f - sprWeapon.getHeight() / 2,
					1, angleDir, facingWeapon, (int) position.y + (animSet.getIndex() == 1 ? -1 : 1));
		}
		else
			sb.draw(position.x, position.y, sprDead, (int) position.y - Tile.tileSize / 2);

		sb.setCam(null);
		((RenderableInventory) inv).render(sb);
		sb.setCam(gc.getSPM().getCam());
	}

	public void renderLight(SpriteBatch sb) {
		sb.draw(sprLight, position.x - sprLight.getWidth() / 2, position.y - sprLight.getHeight() / 2, 1, angleDir, false, 0);
	}
}
