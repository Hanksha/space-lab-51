package com.calderagames.spacelab.entities.items;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.util.vector.Vector2f;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.FloatingText;
import com.calderagames.spacelab.entities.UnitEngineer;
import com.calderagames.spacelab.entities.UnitMedic;
import com.calderagames.spacelab.entities.UnitSoldier;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.path.BezierCurve;
import com.calderagames.spacelab.path.QuadraticBezierCurve;

public class RenderableInventory extends Inventory {

	private class TransitingItem {
		public Item item;
		public BezierCurve curve;
		public Vector2f point;
		private float t;
		private float inc;

		public TransitingItem(Item item, BezierCurve curve) {
			this.item = item;
			this.curve = curve;

			inc = (float) (0.8f + 0.5f * Math.random());

			point = curve.getPoint(0);
		}

		public void update(double dt) {
			t += inc * dt;

			if(t > 1)
				t = 1;

			point = curve.getPoint(t);
		}

		public void render(SpriteBatch sb) {
			item.render(sb, (int) point.x, (int) point.y);
		}

		public boolean isDone() {
			return t == 1;
		}
	}

	private ArrayList<TransitingItem> transitingItems;

	public RenderableInventory(GameContent gc, DynamicEntity owner) {
		super(owner);
		this.gc = gc;
		transitingItems = new ArrayList<TransitingItem>();
	}

	@Override
	public void update(double dt) {
		super.update(dt);

		Iterator<TransitingItem> iter = transitingItems.iterator();

		while(iter.hasNext()) {
			TransitingItem item = iter.next();

			item.update(dt);
			
			if(item.isDone()) {
				owner.getMap().addFloatingText(item.item.getName(), (int) owner.getPosition().x, (int) (owner.getPosition().y - owner.getShape().getHeight() - 10), FloatingText.WHITE);
				
				iter.remove();
			}
		}
	}

	public void render(SpriteBatch sb) {
		for(TransitingItem item : transitingItems) {
			item.render(sb);
		}
	}

	@Override
	public boolean addItem(Item item) {
		if(super.addItem(item)) {

			int posY = 0;
			
			if(owner instanceof UnitSoldier)
				posY = 50;
			else if(owner instanceof UnitMedic)
				posY = 150;
			else if(owner instanceof UnitEngineer)
				posY = 250;
			
			transitingItems.add(new TransitingItem(item, new QuadraticBezierCurve(
																				  new Vector2f(gc.getSPM().getCamX() + item.getPosition().x,
																							   gc.getSPM().getCamY() + item.getPosition().y),
																				  new Vector2f(gc.getSPM().getCamX() + item.getPosition().x +
																							   (float) (200 - 400 * Math.random()), gc.getSPM().getCamY() +
																							   item.getPosition().y +
																																	(float) (200 -
																																			 400 * Math.random())),
																				  new Vector2f(150, posY))));
			
			return true;
		}

		return false;
	}

}
