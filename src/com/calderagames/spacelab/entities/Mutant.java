package com.calderagames.spacelab.entities;

import org.newdawn.slick.geom.Rectangle;

import com.calderagames.spacelab.animation.Animation;
import com.calderagames.spacelab.entities.capacities.Punch;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.Texture;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.tiles.Tile;

public class Mutant extends Enemy {

	public Mutant(GameContent gc, Map map, BattleManager bm, int row, int col, String facing) {
		super(gc, map, bm, row, col);
		shape = new Rectangle(0, 0, 46, 60);
		sprite = new Sprite(46, 62, new TextureRegion(gc.getRM().getTexture("texatlas"), 513, 290, 23, 31));
		sprMiniPortrait = new Sprite(34, 32, new TextureRegion(gc.getRM().getTexture("texatlas"), 516, 291, 17, 16));
		sprDead = new Sprite(64, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 612, 386, 32, 32));
		
		moveCost = 1;
		actionPoints = 5;
		apPerTurn = 3;
		maxActionPoints = 5;
		maxDamage = 100;
		damageAmount = 0;
		speedOnPath = 2;
		
		setAttribute(Attributes.ROBUSTNESS, 2);
		setAttribute(Attributes.STAMINA, 2);
		setAttribute(Attributes.PRECISION, 4);
		setAttribute(Attributes.VELOCITY, 5);
		setAttribute(Attributes.TACTICAL, 2);
		
		if(facing.equals("up")) {
			angleDir = 180;
		}
		else if(facing.equals("down")) {
			angleDir = 180;
		}
		else if(facing.equals("right")) {
			angleDir = 90;
		}
		else if(facing.equals("left")) {
			angleDir = -90;
		}
		
		scanRange = 4 * Tile.tileSize;
		scanAngle = 75;
		
		capacities.add(new Punch(gc, this, 12));
		
		Texture texture = gc.getRM().getTexture("texatlas");
		
		animSet.setAnimation(0, new Animation(56, 62, new TextureRegion[8], 100, false));
		animSet.setSoundEffect(0, new int[] {0, 4}, "footstep-1", false, 1f, 0.1f, 0, 0);
		for(int i = 0; i < animSet.getAnimation(0).getFrames().length; i++) {
			animSet.getAnimation(0).getFrames()[i] = new TextureRegion(texture, 513 + 29 * i, 290, 28, 31);
		}
		
		animSet.setAnimation(1, new Animation(56, 62, new TextureRegion[8], 100, false));
		animSet.setSoundEffect(1, new int[] {0, 4}, "footstep-1", false, 1f, 0.1f, 0, 0);
		for(int i = 0; i < animSet.getAnimation(1).getFrames().length; i++) {
			animSet.getAnimation(1).getFrames()[i] = new TextureRegion(texture, 513 + 29 * i, 322, 28, 31);
		}
		
		animSet.setAnimation(2, new Animation(56, 62, new TextureRegion[2], 200, false));
		for(int i = 0; i < animSet.getAnimation(2).getFrames().length; i++) {
			animSet.getAnimation(2).getFrames()[i] = new TextureRegion(texture, 513 + 29 * i, 354, 28, 31);
		}
	}

	@Override
	public String toString() {
		return "Mutant position:" + position.toString();
	}
}
