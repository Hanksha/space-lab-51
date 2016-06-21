package com.calderagames.spacelab.entities.capacities;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.entities.Obstacle;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.tiles.Tile;

public class ForceFieldBarrier extends Capacity {

	public class FieldBarrier extends Entity implements Obstacle {

		private Sprite sprFull;
		
		private boolean full;
		private boolean active;

		private Rectangle shape;
		
		public FieldBarrier(GameContent gc, Map map, int row, int col) {
			super(gc, map, row, col);

			sprFull = new Sprite(64, 88, new TextureRegion(gc.getRM().getTexture("texatlas"), 67, 674, 32, 44));

			currMapCell.getEntities().remove(this);
			
			shape = new Rectangle(0, 0, 64, 78);
		}

		@Override
		public void interact(Entity source) {
			if(source instanceof DynamicEntity) {
				int apSource = ((DynamicEntity) source).getActionPoints();
				if(apSource >= 1) {
					((DynamicEntity) source).useActionPoint(1);
					
					full = !full;
					
					if(map.isTileBlock(position.x, position.y))
						map.getTileMap().setTile(position.x, position.y, map.getTileMap().getCollisionLayerIndex(), 0);
					else
						map.getTileMap().setTile(position.x, position.y, map.getTileMap().getCollisionLayerIndex(), Tile.BLOCK_ID);

				}
			}
		}

		public void activate(int row, int col) {
			if(active)
				return;
			currMapCell = map.getMapCell(row, col);
			currMapCell.getEntities().add(this);
			
			position.set(col * Tile.tileSize + Tile.tileSize / 2, row * Tile.tileSize + Tile.tileSize / 2);
			active = true;
		}
		
		public void desactivate() {
			if(!active)
				return;
			map.getTileMap().setTile(position.x, position.y, map.getTileMap().getCollisionLayerIndex(), 0);
			currMapCell.getEntities().remove(this);
			active = false;
		}

		@Override
		public boolean isBlock() {
			return true;
		}

		@Override
		public void update(double dt) {
		}

		@Override
		public void render(SpriteBatch sb) {
			sb.draw(sprFull, position.x - sprFull.getWidth() / 2, position.y - sprFull.getHeight() + Tile.tileSize / 2, (int) position.y);
		}

		@Override
		public void renderText(SpriteBatch sb) {
		}

		@Override
		public void renderDebug(SpriteBatch sb) {
		}

		@Override
		public Shape getShape() {
			return shape;
		}
	}


	protected FieldBarrier barrier;

	public ForceFieldBarrier(GameContent gc, DynamicEntity owner) {
		super(gc, owner, 2, 3, 92, "Force Field Barrier$Block the way and$protect from shots $Cost: 2 $CD: 3 turn $Range: " + 92);
		setIcon(new Sprite(42, 42, new TextureRegion(gc.getRM().getTexture("texatlas"), 111, 612, 21, 21)));
		barrier = new FieldBarrier(gc, owner.getMap(), 0, 0);
	}

	@Override
	public void update(double dt) {
		if(isAvailable())
			barrier.desactivate();
	}

	@Override
	public boolean use(int row, int col) {
		if(!canUse() || !checkRange(col * Tile.tileSize + Tile.tileSize / 2, row * Tile.tileSize + Tile.tileSize / 2))
			return false;

		if(!owner.getMap().isTileBlock(row, col) && !owner.getMap().isMapCellBusy(row, col)) {

			barrier.activate(owner.getTargetActionPos().getY(), owner.getTargetActionPos().getX());

			owner.useActionPoint(APCost);

			cdTurnLeft = coolDown;
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean use(Entity target) {

		return false;
	}

	@Override
	public void render(SpriteBatch sb) {
		if(!isAvailable())
			barrier.render(sb);
	}
}
