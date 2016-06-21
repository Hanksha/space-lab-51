package com.calderagames.spacelab.entities.capacities;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.util.vector.Vector2f;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.path.Path;
import com.calderagames.spacelab.util.Timer;

public class MachineGunBlast extends Capacity {

	protected int damageBullet;
	protected ArrayList<Path> pathBullets;
	protected Sprite sprBullet;
	protected Timer timerCandence;
	protected int bulletCounter;
	protected int maxBullet;
	protected String soundId;
	
	public MachineGunBlast(GameContent gc, DynamicEntity owner, int damage, int range) {
		super(gc, owner, 2, 1, range, "Machine gun blast $Cost: 2 $CD: 1 turn $Damage: " + damage + "$Range: " + range);
		setIcon(new Sprite(42, 42, new TextureRegion(gc.getRM().getTexture("texatlas"), 89, 612, 21, 21)));
		sprBullet = new Sprite(10, 32, new TextureRegion(gc.getRM().getTexture("texatlas"), 35, 647, 5, 16));
		maxBullet = 5;
		damageBullet = damage / maxBullet;
		timerCandence = new Timer(100);
		pathBullets = new ArrayList<Path>();
		soundId = "machine-gun-shot";
	}

	@Override
	public void update(double dt) {
		if(currTarget != null) {
			if(timerCandence.tick()) {
				timerCandence.update();
				fireBullet();
			}
		}
		
		if(!pathBullets.isEmpty()) {
			Iterator<Path> iter = pathBullets.iterator();
			
			while(iter.hasNext()) {
				Path pathBullet = iter.next();
    			if(pathBullet.isEnd()) {
    				actionOnTouch();
    
    				iter.remove();
    			}
				pathBullet.update(dt);
			}
		}
		else if(bulletCounter <= 0)
			currTarget = null;
	}
	
	protected void actionOnTouch() {
		if(currTarget instanceof DynamicEntity) {
			((DynamicEntity) currTarget).attack(owner, damageBullet);
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		if(!pathBullets.isEmpty()) {
			for(Path pathBullet: pathBullets) {
    			sb.draw(sprBullet, pathBullet.getPointOnPath().x - sprBullet.getWidth() / 2, pathBullet.getPointOnPath().y - sprBullet.getHeight() / 2, 1,
    					pathBullet.getDirectionAngle(), false, (int) currTarget.getPosition().y + 1);
			}
		}
	}

	@Override
	public boolean use(Entity target) {
		if(pathBullets.isEmpty() && super.use(target)) {
			bulletCounter = maxBullet;
			fireBullet();
			timerCandence.update();
			return true;
		}
			
		return false;
	}
	
	
	private void fireBullet() {
		if(bulletCounter <= 0)
			return;
		
		Vector2f p0 = new Vector2f(owner.getPosition().x, owner.getPosition().y - owner.getShape().getHeight() / 3.2f);
		Vector2f p1 = new Vector2f(currTarget.getPosition().x, currTarget.getPosition().y - currTarget.getShape().getHeight() / 1.5f);
		Path path = new Path(p0, p1);
		path.setSpeed(4);
		pathBullets.add(path);
		gc.getAS().playSoundEffect(1f, 0.5f, soundId);
		bulletCounter--;
	}
}
