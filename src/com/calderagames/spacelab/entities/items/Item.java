package com.calderagames.spacelab.entities.items;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.entities.FloatingText;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.tiles.Tile;

public class Item extends Entity {

	protected int id;
	protected int useCost;
	protected Effect[] effects;
	protected Sprite sprItem;
	protected Rectangle shape;
	protected String desc;

	public Item(GameContent gc, Map map, int row, int col, int id) {
		super(gc, map, row, col);
		position = new Vector2f(col * Tile.tileSize + Tile.tileSize / 2, row * Tile.tileSize + Tile.tileSize / 2);
		this.id = id;
		shape = new Rectangle(0, 0, 64, 64);

		ItemFactory.makeItem(id, this, gc.getRM());
	}

	@Override
	public void update(double dt) {
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.draw(position.x, position.y, sprItem, 0);
	}

	public void render(SpriteBatch sb, int x, int y) {
		sb.draw(sprItem, x, y, -1);
	}

	@Override
	public void renderText(SpriteBatch sb) {
	}

	@Override
	public void renderDebug(SpriteBatch sb) {
	}

	@Override
	public void interact(Entity source) {
		if(source instanceof DynamicEntity) {
			((DynamicEntity) source).getInventory().addItem(this);
		}
	}

	public boolean use(DynamicEntity user, DynamicEntity target) {
		if(user.isTurnbased() && !user.isTurn())
			return false;

		if(user.getActionPoints() < useCost) {
			map.addFloatingText("I can't use that right now!", (int) user.getPosition().x, (int) (user.getPosition().y - user.getShape().getHeight() - 10), FloatingText.INFO);
			return false;
		}

		user.useActionPoint(useCost);
		applyItem(target);

		return true;
	}

	protected void applyItem(DynamicEntity target) {
		for(Effect effect : effects)
			target.addEffect(effect.duplicate());

	}

	public void pickUp() {
		currMapCell.getEntities().remove(this);
	}

	public boolean drop(Map map, int row, int col) {
		if(map.isTileBlock(row, col) || map.isMapCellBusy(row, col))
			return false;

		this.map = map;
		currMapCell = map.getMapCell(row, col);
		currMapCell.getEntities().add(this);

		position.set(col * Tile.tileSize + Tile.tileSize / 2, row * Tile.tileSize + Tile.tileSize / 2);

		return true;
	}

	public void setEffects(Effect[] effects) {
		this.effects = effects;
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}

	public void setUseCost(int cost) {
		useCost = cost;
	}

	public void setSprite(Sprite sprite) {
		sprItem = sprite;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return desc;
	}

	public String getName() {
		return desc.substring(0, desc.indexOf('$'));
	}

	@Override
	public Vector2f getPosition() {
		return position;
	}

	@Override
	public Map getMap() {
		return map;
	}

	@Override
	public Shape getShape() {
		return shape;
	}

}
