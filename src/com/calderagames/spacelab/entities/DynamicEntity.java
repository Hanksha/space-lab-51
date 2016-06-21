package com.calderagames.spacelab.entities;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.geom.Shape;

import com.calderagames.spacelab.entities.capacities.Capacity;
import com.calderagames.spacelab.entities.capacities.Interact;
import com.calderagames.spacelab.entities.capacities.Move;
import com.calderagames.spacelab.entities.items.Effect;
import com.calderagames.spacelab.entities.items.Inventory;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamecontent.ResourceManager;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.path.Path;
import com.calderagames.spacelab.tiles.Tile;
import com.calderagames.spacelab.util.Timer;

public class DynamicEntity extends Entity {

	/**Reference to the group formation, 
	 * null if doesn't belong to a group*/
	protected EntityGroup group;

	/**Cost to move from 1 tile*/
	protected float moveCost;

	/**Amount of AP recovered per turn</br>
	 * Default value: 2*/
	protected int apPerTurn = 2;
	
	/**Determine the priority at the beginning of a battle*/
	protected int initiativePoints;

	/**Current amount of action points*/
	protected int actionPoints;
	protected int bonusAP;

	/**Maximum amount of action points*/
	protected int maxActionPoints;

	/**Maximum damage amount that can be taken*/
	protected int maxDamage;

	/**Current damage amount received*/
	protected int damageAmount;
	
	protected boolean isDead;
	
	/**Given critical percent*/
	protected int baseCrit = 10;
	/**Given precision percent*/
	protected int basePrecision = 75;

	/**Current experience level*/
	protected int xpLevel = 1;
	/**Current amount of experience for the level*/
	protected int xpAmount;
	/**Base maximum experience for level 1, base = 25*/
	protected final int xpBaseMax = 25;
	
	/**Path currently use, null if not on a path*/
	protected Path currentPath;

	/**Target position of the current action, x = column and y = row*/
	protected Point targetActPos;
	
	/**Speed on the path</br>
	 * Default value: 4*/
	protected float speedOnPath = 4;

	/**Equivalent to Entity position vector2f, but represent the y row and x col*/
	protected Point posOnGrid;

	/**angle of the direction the entity is looking*/
	protected float angleDir;
	protected boolean facing;

	//Attributes
	/**
	 * <ul>
	 * <li>Robustness increase the maximum amount of damage that can be taken. 4 damage for 1</li>
	 * <li>Stamina increase the maximum AP and AP/turn. 1 max AP for 2 and 1 AP/turn for 3</li>
	 * <li>Precision increase the change to touch the target and critical damage. 0.5% for 1</li>
	 * <li>Velocity decrease the total move cost. -0.05% of half total cost for 1</li>
	 * <li>Tactical increase the initiative points and give extra AP at the beginning of a battle. 1 for 1, 3 for 1 extra AP</li>
	 * </ul>
	 */
	public enum Attributes {
							ROBUSTNESS, STAMINA, PRECISION, VELOCITY, TACTICAL
	};

	public int[] attributes = new int[Attributes.values().length];

	/**Number of attribute points available, 2 points per level up*/
	protected int attrPoints;
	
	/**Capacities*/
	protected ArrayList<Capacity> capacities;
	protected Capacity selectedCapa;
	
	/**Effects*/
	protected ArrayList<Effect> effects;

	/**Boolean flag for turn based mode*/
	protected boolean isTurnBased;
	/**True if the entity can play its turn*/
	protected boolean isTurn;
	/**False if new turn should update*/
	protected boolean turnUpdateDone;
	/**When not in turn based mode, a turn is 2 seconds*/
	protected Timer timerTurn = new Timer(2000);
	
	/**Inventory*/
	protected Inventory inv;
	
	/**Sprite of the portrait of the entity 
	to be displayed in the HUD*/
	protected Sprite sprPortrait;
	protected Sprite sprMiniPortrait;
	protected Sprite sprite;
	
	protected Shape shape;

	public DynamicEntity(GameContent gc, Map map, int row, int col) {
		super(gc, map, row, col);
		sprite = new Sprite(32, 32, ResourceManager.whitePixel);
		position = new Vector2f(col * Tile.tileSize + Tile.tileSize / 2, row * Tile.tileSize + Tile.tileSize / 2);
		posOnGrid = new Point(col, row);
		targetActPos = new Point(col, row);
		effects = new ArrayList<Effect>();
		capacities = new ArrayList<Capacity>();
		capacities.add(new Move(gc, this));
		capacities.add(new Interact(gc, this));
		inv = new Inventory(this);
		sprPortrait = new Sprite(64, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 642, 1, 32, 32));
	}

	protected void updateMovement(double dt) {
		if(currentPath != null) {
			currentPath.update(dt);
			position = currentPath.getPointOnPath();
			posOnGrid.setLocation(Map.toCol(position.x), Map.toRow(position.y));
			angleDir = currentPath.getDirectionAngle();
			
			if(currentPath.isEnd()) {
				executeAction();
				currentPath = null;
			}
		}

		if((int) (position.y / Tile.tileSize) != currMapCell.getRow() || (int) (position.x / Tile.tileSize) != currMapCell.getCol()) {
			currMapCell.getEntities().remove(this);
			currMapCell = map.getMapCell((int) (position.y / Tile.tileSize), (int) (position.x / Tile.tileSize));
			currMapCell.getEntities().add(this);
		}
	}

	public void updateEffects() {
		for(Iterator<Effect> i = effects.iterator(); i.hasNext();) {
			Effect effect = i.next();
			
			effect.apply();
			
			if(effect.isOver()) {
				i.remove();
			}
		}
	}
	
	public void updateTurn() {
		//update effects
		updateEffects();
		//recover action points
		if(!isTurnBased || (isTurnBased && isTurn)) {
			actionPoints = Math.min(actionPoints + getActionPointPerTurn(), getMaxActionPoints());
			inv.updateTurn();
		}
		
		for(Capacity cap: capacities)
			cap.updateTurn();
		
		if(!isTurnBased && damageAmount != 0)
			healDamage(15);
		
		turnUpdateDone = true;
	}
	
	@Override
	public void update(double dt) {
		updateMovement(dt);
		
		if(!isTurnBased) {
			if(timerTurn.tick()) {
				timerTurn.update();
				isTurn = true;
				turnUpdateDone = false;
			}
			else
				isTurn = false;
		}
		
		if(!turnUpdateDone)
			updateTurn();
			
		for(Capacity cap: capacities)
			cap.update(dt);
		
		inv.update(dt);
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.setColor(1f, 1f, 1f, 1f);

		sb.draw(sprite, position.x - sprite.width / 2, position.y - sprite.height, facing, (int) (position.y - 1));
		
		for(Capacity cap: capacities)
			cap.render(sb);
	}
	
	@Override
	public void renderText(SpriteBatch sb) {
	}

	@Override
	public void renderDebug(SpriteBatch sb) {
	}
	
	/**Execute target action, automatically executed at the end of the path*/
	protected void executeAction() {
		if(selectedCapa.getDesc().equals("Interact")){
			selectedCapa.use(targetActPos.getY(), targetActPos.getX());
		}
	}

	@Override
	public void interact(Entity source) {
		if(source.equals(this))
			return;
	}

	public void useActionPoint(int value) {
		if(bonusAP > 0){
			bonusAP -= value;
			
			if(bonusAP < 0){
				value = Math.abs(bonusAP);
				bonusAP = 0;
			}
		}
		
		actionPoints = Math.max(actionPoints - value, 0);
	}
	
	public void useAttributePoint(Attributes attribute) {
		if(attrPoints >= 1) {
			attrPoints--;
			
			attributes[attribute.ordinal()] += 1;
		}
	}

	public void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	public void addXP(int value) {
		xpAmount += value; 
		int maxXP = getMaxXP();
		if(xpAmount >= maxXP) {
			xpAmount = xpAmount - maxXP;
			map.addFloatingText("+1 lvl", (int) position.x, (int) (position.y - shape.getHeight()), 1500);
			xpLevel++;
			attrPoints++;
		}
	}
	
	public void addBonusAP(int value) {
		bonusAP += value;
	}

	public void healDamage(int value) {
		if(isDead())
			return;
		
		damageAmount = Math.max(damageAmount - value, 0);
		map.addFloatingText("+"+ value, (int) (position.x), (int) (position.y - shape.getHeight() - 10), FloatingText.GREEN_HEALTHY);
	}
	
	/**
	 * @param source the entity attacking
	 * @param damage amount of damage
	 * @return true if the attack was successful
	 */
	public boolean attack(DynamicEntity source, int damage) {
		int precision = source.getAttribute(Attributes.PRECISION);
		
		float touchRange = (basePrecision + precision * 0.5f) / 100;
		
		if(Math.random() > touchRange){
			map.addFloatingText("Miss", (int) (position.x), (int) (position.y - shape.getHeight() - 10), FloatingText.RED_BLOOD);
			return false;
		}
		
		float critRange = (baseCrit + precision * 0.5f) / 100; 
		int mul = 1;
		
		if(Math.random() <= critRange)
			mul = 2;
		
		damageAmount = Math.min(damageAmount + damage * mul,  getMaxDamage());
		
		map.addFloatingText("-"+ damage * mul + (mul == 2?" (Crit)":""), (int) (position.x), (int) (position.y - shape.getHeight() - 10), FloatingText.RED_BLOOD);
		
		if(source instanceof PlayableEntity && this instanceof PlayableEntity)
			map.addFloatingDialog("Heh! Be careful! That hurts!",(int) (position.x), (int) (position.y - shape.getHeight() - 10), 1500);
		
		if(getDamageAmount() == getMaxDamage()) {
			isDead = true;
		}
		
		return true;
	}
	
	public void setPath(Path path) {
		currentPath = path;
		path.setSpeed(speedOnPath);
		path.getPoints().get(0).set(position);
		path.reset();
	}

	public void setTurnBased(boolean b) {
		if(currentPath != null)
			currentPath.cut();
		
		isTurnBased = b;
	}
	
	public void setTurn(boolean b) {
		isTurn = b;
		turnUpdateDone = false;
	}
	
	public void setGroup(EntityGroup group) {
		this.group = group;
	}
	
	public void setIsDead(boolean b) {
		isDead = b;
	}
	
	public void setSelectedCapacity(int index) {
		if(index < 0 || index >= capacities.size())
			return;
		
		selectedCapa = capacities.get(index);
	}
	
	public void setAttribute(Attributes attr, int value) {
		attributes[attr.ordinal()] = value;
	}

	public int getAttribute(Attributes attr) {
		int value = attributes[attr.ordinal()];

		for(Effect effect : effects) {
			if(effect.targetAttr == attr)
				value += effect.value;
		}

		return Math.max(value, 0);
	}

	/**
	 * @return action points + bonus
	 */
	public int getActionPoints() {
		return actionPoints + bonusAP;
	}

	/**Base maximum action points + stamina bonus*/
	public int getMaxActionPoints() {
		return maxActionPoints + (int) getAttribute(Attributes.STAMINA) / 2;
	}

	/**Base action points per turn + stamina bonus*/
	public int getActionPointPerTurn() {
		return apPerTurn + (int) getAttribute(Attributes.STAMINA) / 3;
	}
	
	/**Path cost deducted the velocity bonus*/
	public int getTotalPathCost(Path path) {
		float percent = getAttribute(Attributes.VELOCITY) * 0.05f;
		float pathCost = path.getPathTotalMoveCost(moveCost);
		
		return Math.round(Math.max(pathCost - ((pathCost / 2) * percent), 1f));
	}
	
	/**Base maximum damage + robustness bonus*/
	public int getMaxDamage() {
		return maxDamage + getAttribute(Attributes.ROBUSTNESS) * 4;
	}
	
	public int getDamageAmount() {
		return damageAmount;
	}

	/**
	 * base = 50</br>
	 * n = level, maxXP = n *( ... (2 * (1 * base))) 
	 * @return maximum xp for the level
	 */
	public int getMaxXP() {
		int max = xpBaseMax;
		
		for(int i = 1; i <= xpLevel; i++) {
			max += max * 2;
		}
		
		return max;
	}
	
	public int getXP() {
		return xpAmount;
	}
	
	public int getAttrPoints() {
		return attrPoints;
	}
	
	public boolean hasAttrPoints() {
		return attrPoints > 0;
	}
	
	public int getInitiativePoints() {
		return initiativePoints + getAttribute(Attributes.TACTICAL);
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	public EntityGroup getGroup() {
		return group;
	}
	
	public Point getPosOnGrid() {
		return posOnGrid;
	}

	public Point getTargetActionPos() {
		return targetActPos;
	}
	
	public boolean isTurn() {
		return isTurn && !isDead();
	}
	
	public boolean isTurnbased() {
		return isTurnBased;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public Path getCurrentPath() {
		return currentPath;
	}

	public ArrayList<Capacity> getCapacities() {
		return capacities;
	}
	
	public Capacity getSelectedCapacity() {
		return selectedCapa;
	}
	
	@Override
	public Shape getShape() {
		return shape;
	}
	
	public Sprite getPortrait() {
		return sprPortrait;
	}
	
	public Sprite getMiniPortrait() {
		return sprMiniPortrait;
	}
	
	@Override
	public String toString() {
		return "Dynamic Entity position:" + position.toString();
	}
}
