package com.calderagames.spacelab.entities.capacities;

import org.lwjgl.util.vector.Vector2f;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.path.Path;

public class SimpleGunShot extends Capacity {

	protected int damage;

	protected Sprite sprBullet;
	protected Path pathBullet;

	public SimpleGunShot(GameContent gc, DynamicEntity owner, int damage, int range) {
		super(gc, owner, 1, 0, range, "Basic gun shot $Cost: 1 $CD: 0 turn $Damage: " + damage + "$Range: " + range);
		this.damage = damage;
		setIcon(new Sprite(42, 42, new TextureRegion(gc.getRM().getTexture("texatlas"), 45, 612, 21, 21)));
		sprBullet = new Sprite(4, 28, new TextureRegion(gc.getRM().getTexture("texatlas"), 6, 636, 1, 7));
	}

	@Override
	public void update(double dt) {
		if(pathBullet != null) {
			pathBullet.update(dt);
			if(pathBullet.isEnd()) {
				if(currTarget instanceof DynamicEntity) {
					((DynamicEntity) currTarget).attack(owner, damage);
				}

				pathBullet = null;
				currTarget = null;
			}
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		if(pathBullet != null) {
			sb.draw(sprBullet, pathBullet.getPointOnPath().x - sprBullet.getWidth() / 2, pathBullet.getPointOnPath().y - sprBullet.getHeight() / 2, 1,
					pathBullet.getDirectionAngle(), false, (int) (pathBullet.getPointOnPath().y - sprBullet.getHeight() / 2));
		}
	}

	@Override
	public boolean use(Entity target) {
		if(pathBullet == null && target != null && super.use(target)) {
			Vector2f p0 = new Vector2f(owner.getPosition().x, owner.getPosition().y - owner.getShape().getHeight() / 3.2f);
			Vector2f p1 = new Vector2f(target.getPosition().x, target.getPosition().y - target.getShape().getHeight() / 1.5f);
			pathBullet = new Path(p0, p1);
			pathBullet.setSpeed(4);
			gc.getAS().playSoundEffect(1f, 0.5f, "gun-shot");
			return true;
		}
			
		return false;
	}
}
