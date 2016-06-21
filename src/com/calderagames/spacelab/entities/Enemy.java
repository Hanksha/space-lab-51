package com.calderagames.spacelab.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.calderagames.spacelab.animation.AnimationSet;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.path.Path;
import com.calderagames.spacelab.tiles.Tile;
import com.calderagames.spacelab.util.MathUtil;
import com.calderagames.spacelab.util.ResolutionHandler;
import com.calderagames.spacelab.util.Timer;

public abstract class Enemy extends DynamicEntity {

	public class BasicEntityCompator implements Comparator<DynamicEntity> {
		@Override
		public int compare(DynamicEntity o1, DynamicEntity o2) {
			float dist1 = MathUtil.getDistance(o1.getPosition().x, o1.getPosition().y, getPosition().x, getPosition().y) * 0.25f;
			float dist2 = MathUtil.getDistance(o2.getPosition().x, o2.getPosition().y, getPosition().x, getPosition().y) * 0.25f;

			float hp1 = o1.getMaxDamage() - o1.getDamageAmount();
			float hp2 = o2.getMaxDamage() - o2.getDamageAmount();

			float total1 = dist1 + hp1;
			float total2 = dist2 + hp2;

			if(total1 > total2)
				return 1;
			if(total1 < total2)
				return -1;

			return 0;
		}
	}

	/**Reference to battle manager*/
	protected BattleManager bm;
	/**Range at which the enemy can detect the player*/
	protected int scanRange;
	/**Angle span of the cone front of the entity*/
	protected float scanAngle;

	protected Sprite sprDead;

	protected AnimationSet animSet;

	private Timer timerSkip;
	private Timer timerDecision;
	private Timer timerAction;
	private Timer timerScan;

	public Enemy(GameContent gc, Map map, BattleManager bm, int row, int col) {
		super(gc, map, row, col);
		this.bm = bm;
		timerSkip = new Timer(2000);
		timerDecision = new Timer(500);
		timerAction = new Timer(500);
		timerScan = new Timer(200);

		animSet = new AnimationSet(gc, 3);
	}

	protected void scanMap() {
		if(isTurnBased || isDead() || bm.isBattle() || !timerScan.tick())
			return;

		if(gc.getSPM().getCamX() + position.x + scanRange < 0 || gc.getSPM().getCamX() + position.x - scanRange > ResolutionHandler.WIDTH)
			return;
		if(gc.getSPM().getCamY() + position.y + scanRange < 0 || gc.getSPM().getCamY() + position.y - scanRange > ResolutionHandler.HEIGHT)
			return;

		float startAngle = angleDir - scanAngle / 2;
		float endAngle = angleDir + scanAngle / 2;

		outerScan: 
		for(float angle = startAngle; angle <= endAngle; angle += 10) {
			for(int dist = 0; dist <= scanRange; dist += 5) {
				int row = Map.toRow(position.y + (float) (Math.cos(Math.toRadians(angle)) * dist));
				int col = Map.toCol(position.x + (float) (Math.sin(Math.toRadians(angle)) * dist));

				if(map.getTileMap().getTypei(row, col, map.getTileMap().getCollisionLayerIndex()) == Tile.BLOCK)
					break;
				else {
					for(Entity e : map.getMapCell(row, col).getEntities()) {
						if(e instanceof PlayableEntity) {
							bm.startBattle(getGroup(), false);
							break outerScan;
						}
					}
				}
			}
		}
		
		timerScan.update();
	}

	protected void processTurnBasedAI() {
		if(currentPath != null || !timerAction.tick())
			return;

		int count = bm.getPlayerGroup().size();

		while(count >= 1) {
			DynamicEntity entity = determineBestTarget(bm.getPlayerGroup().subList(0, count));
			if(entity != null) {
				if(capacities.get(2).canUse()) {
					if(capacities.get(2).checkRange(entity)) {
						capacities.get(2).use(entity);
						selectedCapa = capacities.get(2);
						timerAction.update();
						break;
					}
					else {
						if(gc.getPathFinder().searchPath(posOnGrid.getY(), posOnGrid.getX(), entity.getPosOnGrid().getY(), entity.getPosOnGrid().getX(),
														 true)) {
							Path path = gc.getPathFinder().getPath();

							while(path != null && getTotalPathCost(path) > getActionPoints()) {
								if(!path.removeLast())
									path = null;
							}

							if(path != null) {
								setPath(path);
								selectedCapa = capacities.get(0);
								useActionPoint(getTotalPathCost(path));
								timerAction.update();
								break;
							}
						}
					}
				}
			}

			count--;
		}
	}

	protected DynamicEntity determineBestTarget(List<DynamicEntity> entities) {
		ArrayList<DynamicEntity> sortedList = new ArrayList<>();

		for(int i = 0; i < entities.size(); i++) {
			if(!entities.get(i).isDead())
				sortedList.add(entities.get(i));
		}

		sortedList.sort(new BasicEntityCompator());

		if(sortedList.size() >= 1)
			return sortedList.get(0);
		else
			return null;
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		scanMap();

		if(isTurnBased) {
			if(isTurn && timerDecision.tick()) {
				processTurnBasedAI();

				if(!timerAction.tick())
					timerSkip.update();
			}
			else
				timerSkip.update();

			if(timerSkip.tick()) {
				timerSkip.update();
				isTurn = false;
			}
		}
		else
			timerSkip.update();

		if(animSet.getCurrentAnimation() != null) {
			if(angleDir >= -45 && angleDir <= 45) {
				if(animSet.getIndex() != 1)
					animSet.setIndex(1);
				facing = false;
			}
			else if(angleDir > 45 && angleDir < 135) {
				if(animSet.getIndex() != 2)
					animSet.setIndex(2);
				facing = false;
			}
			else if((angleDir <= -135 && angleDir >= -180) || (angleDir >= 135 && angleDir <= 180)) {
				if(animSet.getIndex() != 0)
					animSet.setIndex(0);
				facing = false;
			}
			else if(angleDir < -45 && angleDir > -135) {
				if(animSet.getIndex() != 2)
					animSet.setIndex(2);
				facing = true;
			}

			if(currentPath != null)
				animSet.update(position.x, position.y);

			sprite = animSet.getCurrentAnimation().getFrame();
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		if(!isDead())
			super.render(sb);
		else
			sb.draw(position.x, position.y, sprDead, (int) position.y);
	}

	@Override
	public void setTurn(boolean b) {
		super.setTurn(b);

		timerDecision.update();
	}
}
