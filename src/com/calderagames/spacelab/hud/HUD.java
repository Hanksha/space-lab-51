package com.calderagames.spacelab.hud;

import static com.calderagames.spacelab.util.ResolutionHandler.WIDTH;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Point;
import org.newdawn.slick.geom.Rectangle;

import com.calderagames.spacelab.entities.BattleManager;
import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.entities.EntityGroup;
import com.calderagames.spacelab.entities.EntityGroupListener;
import com.calderagames.spacelab.entities.GroupEvent;
import com.calderagames.spacelab.entities.PlayableEntity;
import com.calderagames.spacelab.entities.DynamicEntity.Attributes;
import com.calderagames.spacelab.entities.GroupEvent.GroupEventType;
import com.calderagames.spacelab.font.TrueTypeFont;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.Color;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.input.InputEvent;
import com.calderagames.spacelab.input.InputListener;
import com.calderagames.spacelab.input.InputProcessor;
import com.calderagames.spacelab.tiles.Tile;
import com.calderagames.spacelab.util.ResolutionHandler;

public class HUD implements InputListener, EntityGroupListener {

	/**Reference to game content*/
	private GameContent gc;

	/**Reference to battle manager*/
	private BattleManager bm;
	/**Reference to player group*/
	private EntityGroup pGroup;

	private Sprite sprFrame;
	private Sprite sprHalo;
	private Sprite sprNoAp;
	private Sprite sprAp;
	private Sprite sprXpBar;
	private Sprite sprXp;
	private Sprite sprHpBar;
	private Sprite sprMiniHpBar;
	private Sprite sprCursorRed;
	private Sprite sprCursorGreen;
	private Sprite sprCursorBlue;
	private Sprite sprCursorLeader;
	private Sprite sprSpaceBar;
	private Sprite sprButtonB;
	private Sprite sprCrossDead;
	private Sprite sprHourGlass;
	private Sprite sprInv;
	private Sprite sprAttr;
	private Sprite sprHovered;
	private Sprite sprInvTop;
	private Sprite sprInvMiddle;
	private Sprite sprBgAttr;

	private ArrayList<InputRectangle> buttonRects;
	private ArrayList<InputRectangle> portraitRects;
	private ArrayList<InputRectangle> capaRects;
	private boolean showCapacities;
	private String hoveredCapaDesc = "";
	private int hoveredCapaIndex;
	private boolean showInv;
	private String hoveredItemDesc = "";
	private int hoveredInvIndex;
	private boolean showAttr;
	private int hoveredAttrIndex;

	//Cursor
	private Point cursorPos;

	//Font
	private TrueTypeFont font16;
	private TrueTypeFont font12;

	public HUD(GameContent gc, BattleManager bm, EntityGroup pGroup) {
		this.gc = gc;
		this.bm = bm;
		this.pGroup = pGroup;
		pGroup.setGroupListener(this);
		font16 = gc.getRM().getFont("mithril", 16);
		font12 = gc.getRM().getFont("mithril", 12);

		cursorPos = new Point(pGroup.getLeader().getPosOnGrid());

		//init input rectangles
		buttonRects = new ArrayList<InputRectangle>();
		portraitRects = new ArrayList<InputRectangle>();
		capaRects = new ArrayList<InputRectangle>();
		updatePortraits();
		updateCapaRects();

		//init all the sprites
		sprFrame = new Sprite(182, 96, new TextureRegion(gc.getRM().getTexture("texatlas"), 1, 514, 91, 48));
		sprHalo = new Sprite(182, 96, new TextureRegion(gc.getRM().getTexture("texatlas"), 1, 563, 91, 48));
		sprNoAp = new Sprite(14, 14, new TextureRegion(gc.getRM().getTexture("texatlas"), 93, 550, 7, 7));
		sprAp = new Sprite(14, 14, new TextureRegion(gc.getRM().getTexture("texatlas"), 101, 550, 7, 7));
		sprXpBar = new Sprite(96, 6, new TextureRegion(gc.getRM().getTexture("texatlas"), 93, 546, 48, 3));
		sprXp = new Sprite(1, 4, new TextureRegion(gc.getRM().getTexture("texatlas"), 142, 546, 1, 1));
		sprHpBar = new Sprite(1, 12, new TextureRegion(gc.getRM().getTexture("texatlas"), 109, 550, 1, 6));
		sprMiniHpBar = new Sprite(66, 10, new TextureRegion(gc.getRM().getTexture("texatlas"), 126, 580, 66, 10));
		sprCursorRed = new Sprite(64, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 148, 547, 32, 32));
		sprCursorGreen = new Sprite(64, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 181, 547, 32, 32));
		sprCursorBlue = new Sprite(64, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 214, 547, 32, 32));
		sprCursorLeader = new Sprite(64, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 93, 558, 32, 32));
		sprSpaceBar = new Sprite(138, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 247, 547, 69, 32));
		sprButtonB = new Sprite(64, 64, new TextureRegion(gc.getRM().getTexture("texatlas"), 317, 547, 32, 32));
		sprCrossDead = new Sprite(32, 32, new TextureRegion(gc.getRM().getTexture("texatlas"), 93, 591, 16, 16));
		sprHourGlass = new Sprite(42, 42, new TextureRegion(gc.getRM().getTexture("texatlas"), 193, 580, 21, 21));
		sprInv = new Sprite(24, 24, new TextureRegion(gc.getRM().getTexture("texatlas"), 110, 591, 12, 12));
		sprAttr = new Sprite(24, 24, new TextureRegion(gc.getRM().getTexture("texatlas"), 123, 591, 12, 12));
		sprHovered = new Sprite(42, 42, new TextureRegion(gc.getRM().getTexture("texatlas"), 215, 580, 21, 21));
		sprInvTop = new Sprite(46, 46, new TextureRegion(gc.getRM().getTexture("texatlas"), 237, 580, 23, 23));
		sprInvMiddle = new Sprite(46, 46, new TextureRegion(gc.getRM().getTexture("texatlas"), 237, 604, 23, 23));
		sprBgAttr = new Sprite(180, 156, new TextureRegion(gc.getRM().getTexture("texatlas"), 357, 514, 90, 78));

		buttonRects.add(new InputRectangle(this, "space", 566, 609, 150, 110));

		for(int i = 0; i < 8; i++) {
			buttonRects.add(new InputRectangle(this, "inv-" + i, 194, 14 + i * sprInvTop.getHeight(), sprInvTop.getWidth(), sprInvTop.getHeight()));
			buttonRects.get(i).setEnable(false);
		}

		for(int i = 0; i < 5; i++) {
			buttonRects.add(new InputRectangle(this, "attr-" + i, 202, 45 + i * 25, 160, 25));
			buttonRects.get(i).setEnable(false);
		}
	}

	public void handleKeyboardInput() {
		if(Keyboard.getEventKeyState()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_SPACE)
				fireInput(new InputEvent("space", true));
			if(Keyboard.getEventKey() == Keyboard.KEY_1)
				fireInput(new InputEvent("capa-" + 0, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_2)
				fireInput(new InputEvent("capa-" + 1, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_3)
				fireInput(new InputEvent("capa-" + 2, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_4)
				fireInput(new InputEvent("capa-" + 3, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_5)
				fireInput(new InputEvent("capa-" + 4, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_6)
				fireInput(new InputEvent("capa-" + 5, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_7)
				fireInput(new InputEvent("capa-" + 6, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_8)
				fireInput(new InputEvent("capa-" + 7, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_9)
				fireInput(new InputEvent("capa-" + 8, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_0)
				fireInput(new InputEvent("capa-" + 9, true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_LEFT)
				fireInput(new InputEvent("left cursor", true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_RIGHT)
				fireInput(new InputEvent("right cursor", true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_UP)
				fireInput(new InputEvent("up cursor", true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_DOWN)
				fireInput(new InputEvent("down cursor", true));
			else if(Keyboard.getEventKey() == Keyboard.KEY_RETURN)
				fireInput(new InputEvent("click", true));

		}
	}

	public void handleMouseInput() {
		cursorPos.setLocation(Map.toCol(InputProcessor.getMouseX() - gc.getSPM().getCam().getX()),
							  Map.toRow(InputProcessor.getMouseY() - gc.getSPM().getCam().getY()));

		if(showCapacities) {
			boolean hovered = false;
			for(int i = 0; i < capaRects.size(); i++) {
				InputRectangle rect = capaRects.get(i);
				if(Rectangle.contains(InputProcessor.getMouseX() - gc.getSPM().getCam().getX() - pGroup.getLeader().getPosition().x,
									  InputProcessor.getMouseY() - gc.getSPM().getCam().getY() - pGroup.getLeader().getPosition().y, rect.getX(), rect.getY(),
									  rect.getWidth(), rect.getHeight())) {
					fireInput(new InputEvent("hover-" + i, false));
					hovered = true;
					break;
				}

			}

			if(!hovered) {
				hoveredCapaDesc = "";
				hoveredCapaIndex = 0;
			}
		}

		if(showInv) {
			boolean hovered = false;
			for(int i = 0, counter = 0; i < buttonRects.size(); i++) {
				InputRectangle rect = buttonRects.get(i);

				if(!rect.getId().contains("inv-"))
					continue;

				if(Rectangle.contains(InputProcessor.getMouseX(), InputProcessor.getMouseY(), rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight())) {
					fireInput(new InputEvent("hoverInv-" + counter, false));
					hovered = true;
					break;
				}
				counter++;
			}

			if(!hovered) {
				hoveredItemDesc = "";
				hoveredInvIndex = 0;
			}
		}

		if(showAttr) {
			boolean hovered = false;
			for(int i = 0, counter = 0; i < buttonRects.size(); i++) {
				InputRectangle rect = buttonRects.get(i);

				if(!rect.getId().contains("attr-"))
					continue;

				if(Rectangle.contains(InputProcessor.getMouseX(), InputProcessor.getMouseY(), rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight())) {
					fireInput(new InputEvent("hoverAttr-" + counter, false));
					hovered = true;
					break;
				}
				counter++;
			}

			if(!hovered) {
				hoveredAttrIndex = 0;
			}
		}

		if(Mouse.getEventButtonState()) {
			if(Mouse.getEventButton() == 0) {
				for(InputRectangle rect : buttonRects) {
					if(rect.contains(InputProcessor.getMouseX(), InputProcessor.getMouseY()))
						return;
				}
				for(InputRectangle rect : portraitRects) {
					if(rect.contains(InputProcessor.getMouseX(), InputProcessor.getMouseY()))
						return;
				}
				if(showCapacities) {
					for(InputRectangle rect : capaRects) {
						if(rect.contains(InputProcessor.getMouseX() - gc.getSPM().getCam().getX() - pGroup.getLeader().getPosition().x,
										 InputProcessor.getMouseY() - gc.getSPM().getCam().getY() - pGroup.getLeader().getPosition().y))
							return;
					}
				}

				fireInput(new InputEvent("click-left", true));
			}
			else if(Mouse.getEventButton() == 1) {
				fireInput(new InputEvent("click-right", true));
			}
		}
	}

	public void handleControllerInput() {
		if(Controllers.isEventPovX()) {
			if(Controllers.getEventSource().getPovX() < 0)
				fireInput(new InputEvent("left cursor", true));
			else if(Controllers.getEventSource().getPovX() > 0)
				fireInput(new InputEvent("right cursor", true));
		}
		else if(Controllers.isEventPovY()) {
			if(Controllers.getEventSource().getPovY() < 0)
				fireInput(new InputEvent("up cursor", true));
			else if(Controllers.getEventSource().getPovY() > 0)
				fireInput(new InputEvent("down cursor", true));
		}

		if(Controllers.isEventButton()) {
			if(Controllers.getEventButtonState()) {
				if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_A) {
					if(showAttr)
						fireInput(new InputEvent("attr-" + hoveredAttrIndex, true));
					else if(showInv)
						fireInput(new InputEvent("inv-" + hoveredInvIndex, true));
					else
						fireInput(new InputEvent("click-left", true));
				}
				else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_X)
					fireInput(new InputEvent("click-right", true));
				else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_B)
					fireInput(new InputEvent("space", true));
				else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_Y)
					pGroup.setNextLeader();
				else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_SELECT){
					if(!showInv && !showAttr)
						fireInput(new InputEvent("inv", true));
					else
						fireInput(new InputEvent("attr", true));
				}
				else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_RB || Controllers.getEventControlIndex() == InputProcessor.BUTTON_LB) {
					if(showCapacities) {
						int index = pGroup.getLeader().getCapacities().indexOf(pGroup.getLeader().getSelectedCapacity());

						if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_RB)
							index = Math.min(index + 1, pGroup.getLeader().getCapacities().size() - 1);
						else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_LB)
							index = Math.max(index - 1, 0);

						fireInput(new InputEvent("capa-" + index, true));
						fireInput(new InputEvent("hover-" + index, true));
					}
					else if(showAttr) {
						if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_RB)
							hoveredAttrIndex = Math.min(hoveredAttrIndex + 1, 4);
						else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_LB)
							hoveredAttrIndex = Math.max(hoveredAttrIndex - 1, 0);
					}
					else if(showInv) {
						if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_RB)
							hoveredInvIndex = Math.min(hoveredInvIndex + 1, 7);
						else if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_LB)
							hoveredInvIndex = Math.max(hoveredInvIndex - 1, 0);
					}
				}
			}

		}
	}

	private void updatePortraits() {
		portraitRects.clear();
		for(int i = 0; i < pGroup.getEntities().size(); i++) {
			portraitRects.add(new InputRectangle(this, "leader-" + i, 10, 10 + i * 104, 80, 60));
			portraitRects.add(new InputRectangle(this, "inv", 88, 10 + i * 104, 24, 24));
			portraitRects.add(new InputRectangle(this, "attr", 114, 10 + i * 104, 24, 24));
		}
	}

	private void updateCapaRects() {
		capaRects.clear();
		for(int angle = 180, i = 0; i < pGroup.getLeader().getCapacities().size(); i++, angle -= 45) {
			capaRects.add(new InputRectangle(this, "capa-" + i, 0, 0, 42, 42));
			capaRects.get(i).setCenterX((float) (Math.sin(Math.toRadians(angle)) * 80));
			capaRects.get(i).setCenterY((float) (Math.cos(Math.toRadians(angle)) * 80));
		}
	}

	public void update(double dt) {
		//set the skip turn button enable only during battle
		if(bm.isBattle())
			buttonRects.get(0).setEnable(true);
		else
			buttonRects.get(0).setEnable(false);
	}

	public void renderBack(SpriteBatch sb) {
		//draw cursor
		drawCursors(sb);
	}

	public void render(SpriteBatch sb) {

		//draw portrait, life bar, AP bar, XP bar
		int x = 10, y, count = 0;
		for(Iterator<DynamicEntity> iter = pGroup.getEntities().iterator(); iter.hasNext(); count++) {
			PlayableEntity entity = (PlayableEntity) iter.next();

			y = 10 + count * 104;

			drawPlayerInfo(sb, entity, x, y);
		}

		drawBattleHud(sb);

		drawCapacities(sb);

		drawInventory(sb);

		sb.setColor(1f, 1f, 1f, 1f);
	}

	private void drawCursors(SpriteBatch sb) {
		sb.draw(sprCursorBlue, pGroup.getLeader().getTargetActionPos().getX() * Tile.tileSize, pGroup.getLeader().getTargetActionPos().getY() * Tile.tileSize,
				0);

		if(bm.isBattle())
			sb.draw(sprCursorLeader, bm.getPlayingEntity().getPosition().x - Tile.tileSize / 2, bm.getPlayingEntity().getPosition().getY() - Tile.tileSize / 2,
					0);
		else
			sb.draw(sprCursorLeader, pGroup.getLeader().getPosition().x - Tile.tileSize / 2, pGroup.getLeader().getPosition().getY() - Tile.tileSize / 2, 0);

		if(!pGroup.getLeader().getMap().isMapCellBusy(cursorPos.getY(), cursorPos.getX()) &&
		   !pGroup.getLeader().getMap().isTileBlock(cursorPos.getY(), cursorPos.getX()))
			sb.draw(sprCursorGreen, cursorPos.getX() * Tile.tileSize, cursorPos.getY() * Tile.tileSize, 0);
		else
			sb.draw(sprCursorRed, cursorPos.getX() * Tile.tileSize, cursorPos.getY() * Tile.tileSize, 0);
	}

	private void drawPlayerInfo(SpriteBatch sb, DynamicEntity entity, int x, int y) {
		sb.setColor(1f, 1f, 1f, 1f);
		//draw frame
		sb.draw(sprFrame, x, y, -1);

		if(pGroup.getLeader().equals(entity)) {
			sb.draw(sprHalo, x, y, -1);

			sb.draw(sprInv, x + 78, y + 4, -1);
			sb.draw(sprAttr, x + 104, y + 4, -1);
		}

		//draw portrait
		sb.draw(entity.getPortrait(), x + 4, y - 6, -1);

		//draw xp bar
		sb.draw(sprXpBar, x + 80, y + 44, -1);
		//draw xp points
		sb.draw(sprXp, x + 80, y + 44, (entity.getXP() * 94) / entity.getMaxXP(), 1, 0, false, -1);

		//draw healt points
		sb.draw(sprHpBar, x + 10, y + 58, ((entity.getMaxDamage() - entity.getDamageAmount()) * 162) / entity.getMaxDamage(), 1, 0, false, -1);

		//draw AP
		for(int i = 0; i < entity.getMaxActionPoints(); i++) {
			if(i < entity.getActionPoints())
				sb.draw(sprAp, x + 14 + i * 20 - (i >= 8 ? 8 * 20 : 0), y + 76 + 22 * ((int) i / 8), -1);
			else
				sb.draw(sprNoAp, x + 14 + i * 20 - (i >= 8 ? 8 * 20 : 0), y + 76 + 22 * ((int) i / 8), -1);
		}

		//157 18
		if(showAttr) {
			sb.draw(sprBgAttr, 196, 18, -1);
		}
	}

	private void drawBattleHud(SpriteBatch sb) {
		if(bm.isBattle()) {
			sb.setColor(1f, 1f, 1f, 1f);
			//draw enemies' health bars
			sb.setCam(gc.getSPM().getCam());
			for(DynamicEntity entity : bm.getOpponentGroup()) {
				if(entity.isDead())
					continue;

				for(int i = 0; i < entity.getActionPoints(); i++) {
					sb.draw(sprAp, entity.getPosition().x - sprMiniHpBar.getWidth() / 2 + i * (sprAp.getWidth() / 2 + 2),
							entity.getPosition().y - entity.getShape().getHeight() - 26, 0.5f, false, -1);
				}

				sb.draw(sprMiniHpBar, entity.getPosition().x - sprMiniHpBar.getWidth() / 2, entity.getPosition().y - entity.getShape().getHeight() - 13, -1);
				sb.draw(sprHpBar, entity.getPosition().x - 32, entity.getPosition().y - entity.getShape().getHeight() - 11,
						((entity.getMaxDamage() - entity.getDamageAmount()) * 64) / entity.getMaxDamage(), 0.5f, 0, false, -1);
			}
			sb.setCam(null);

			//draw mini portrait queue
			int halfWidth = (bm.getEntities().size() * 38) / 2;
			for(int i = 0; i < bm.getEntities().size(); i++) {
				DynamicEntity e = bm.getEntities().get(i);

				sb.draw(e.getMiniPortrait(), WIDTH / 2 - halfWidth + i * 38, (bm.getPlayingEntityIndex() == i ? 5 : 10), -1);

				if(e.isDead())
					sb.draw(sprCrossDead, WIDTH / 2 - halfWidth + i * 38, (bm.getPlayingEntityIndex() == i ? 5 : 10), -1);
			}

			if(pGroup.getLeader().isTurn()) {
				if(InputProcessor.isGamepad())
					sb.draw(sprButtonB, WIDTH / 2 - sprButtonB.getWidth() / 2, 630, -1);
				else
					sb.draw(sprSpaceBar, WIDTH / 2 - sprSpaceBar.getWidth() / 2, 630, -1);
			}
		}
	}

	private void drawCapacities(SpriteBatch sb) {
		sb.setCam(gc.getSPM().getCam());
		//draw capacity rect
		DynamicEntity entity = pGroup.getLeader();
		if(showCapacities) {
			for(int i = 0; i < capaRects.size(); i++) {
				Rectangle rect = capaRects.get(i);
				if(entity.getCapacities().get(i).equals(entity.getSelectedCapacity()))
					sb.setColor(1f, 1f, 1f, 1f);
				else
					sb.setColor(1f, 1f, 1f, 0.6f);

				sb.draw(entity.getPosition().x + rect.getCenterX(), entity.getPosition().y + rect.getCenterY(), entity.getCapacities().get(i).getIcon(), -1);

				if(!entity.getCapacities().get(i).isAvailable())
					sb.draw(entity.getPosition().x + rect.getCenterX(), entity.getPosition().y + rect.getCenterY(), sprHourGlass, -1);

				if(i == hoveredCapaIndex)
					sb.draw(entity.getPosition().x + rect.getCenterX(), entity.getPosition().y + rect.getCenterY(), sprHovered, -1);

			}
		}
		sb.setCam(null);
	}

	public void drawInventory(SpriteBatch sb) {
		if(!showInv)
			return;

		sb.setColor(1f, 1f, 1f, 1f);
		//draw background
		sb.draw(sprInvTop, 194, 14, -1);
		sb.draw(sprInvTop, 194, 14 + sprInvTop.getHeight() * 7 - 1, 1, 1, 0, false, true, -1);
		for(int i = 1; i < 7; i++)
			sb.draw(sprInvMiddle, 194, 14 + i * sprInvTop.getHeight(), -1);

		for(int i = 0; i < pGroup.getLeader().getInventory().getItems().size(); i++) {
			pGroup.getLeader().getInventory().getItems().get(i).render(sb, 194, (int) (14 + i * sprInvTop.getHeight()));

			if(i == hoveredInvIndex)
				sb.draw(194 + sprInvTop.getHeight() / 2, 14 + i * sprInvTop.getHeight() + sprInvTop.getHeight() / 2, sprHovered, -1);
		}
	}

	public void renderText(SpriteBatch sb) {
		if(bm.isBattle()) {
			if(pGroup.getLeader().isTurn()) {
				gc.getRM().getFont("mithril", 16).drawString(sb, WIDTH / 2 - font16.getWidth("Press") / 2, 700 - sprSpaceBar.getHeight() - 25, "Press");
				gc.getRM().getFont("mithril", 16).drawString(sb, WIDTH / 2 - font16.getWidth("to end the turn") / 2, 700, "to end the turn");

			}
		}

		if(showCapacities) {
			font16.drawString(sb, 10, 330, "capacity:");
			font12.drawString(sb, 10, 330 + font16.getHeight() + 5, hoveredCapaDesc);
		}

		if(showInv) {
			font12.drawString(sb, 194 + sprInvMiddle.getWidth() + 10, hoveredInvIndex * sprInvMiddle.getHeight() + sprInvMiddle.getHeight() / 2,
							  hoveredItemDesc);
		}

		if(showAttr) {
			int y = 50;
			sb.setColor(1f, 1f, 1f, 1f);
			String plusPts = pGroup.getLeader().hasAttrPoints() ? "   +" : "";

			font16.drawString(sb, 205, 25, "Attributes (" + pGroup.getLeader().getAttrPoints() + " pts)");

			font16.drawString(sb, 205, y, "Robustness: " + pGroup.getLeader().getAttribute(Attributes.ROBUSTNESS) + plusPts,
							  hoveredAttrIndex == 0 ? Color.RED_BLOOD : Color.WHITE);
			y += 25;
			font16.drawString(sb, 205, y, "Stamina: " + pGroup.getLeader().getAttribute(Attributes.STAMINA) + plusPts,
							  hoveredAttrIndex == 1 ? Color.RED_BLOOD : Color.WHITE);
			y += 25;
			font16.drawString(sb, 205, y, "Precision: " + pGroup.getLeader().getAttribute(Attributes.PRECISION) + plusPts,
							  hoveredAttrIndex == 2 ? Color.RED_BLOOD : Color.WHITE);
			y += 25;
			font16.drawString(sb, 205, y, "Velocity: " + pGroup.getLeader().getAttribute(Attributes.VELOCITY) + plusPts,
							  hoveredAttrIndex == 3 ? Color.RED_BLOOD : Color.WHITE);
			y += 25;
			font16.drawString(sb, 205, y, "Tactical: " + pGroup.getLeader().getAttribute(Attributes.TACTICAL) + plusPts,
							  hoveredAttrIndex == 4 ? Color.RED_BLOOD : Color.WHITE);
			y += 25;
		}
	}

	@Override
	public void fireInput(InputEvent event) {
		if(event.state) {
			if(event.name.contains("leader")) {
				if(!bm.isBattle())
					pGroup.setLeader(Integer.parseInt(event.name.split("-")[1]));
			}
			else if(event.name.equals("capa")) {
				showCapacities = !showCapacities;
			}
			else if(event.name.contains("capa")) {
				showAttr = showInv = false;

				pGroup.getLeader().setSelectedCapacity(Integer.parseInt(event.name.split("-")[1]));
				fireInput(new InputEvent("hover-" + event.name.split("-")[1], false));
			}
			else if(event.name.equals("space")) {
				if(bm.isBattle()) {
					((InputListener) pGroup.getLeader()).fireInput(new InputEvent("end turn", true));
					showCapacities = showAttr = showInv = false;
				}
				else
					((InputListener) pGroup.getLeader()).fireInput(new InputEvent("stop", true));
			}
			else if(event.name.equals("left cursor")) {
				cursorPos.setX(Math.max(cursorPos.getX() - 1, Map.toRow(-gc.getSPM().getCamX())));
			}
			else if(event.name.equals("right cursor")) {
				cursorPos.setX(Math.min(cursorPos.getX() + 1, Map.toRow(-gc.getSPM().getCamX()) + Map.toRow(ResolutionHandler.WIDTH)));
			}
			else if(event.name.equals("up cursor")) {
				cursorPos.setY(Math.max(cursorPos.getY() - 1, Map.toRow(-gc.getSPM().getCamY())));
			}
			else if(event.name.equals("down cursor")) {
				cursorPos.setY(Math.min(cursorPos.getY() + 1, Map.toRow(-gc.getSPM().getCamY()) + Map.toRow(ResolutionHandler.HEIGHT)));
			}
			else if(event.name.contains("click")) {
				if(event.name.contains("left")) {
					for(Entity entity : pGroup.getLeader().getMap().getMapCell(cursorPos.getY(), cursorPos.getX()).getEntities()) {
						if(entity.equals(pGroup.getLeader())) {
							showCapacities = !showCapacities;
							showAttr = showInv = false;
						}
					}
				}
				((InputListener) pGroup.getLeader()).fireInput(new InputEvent("click-" + cursorPos.getX() + "-" + cursorPos.getY() + "-" +
																			  event.name.split("-")[1], true));
			}
			else if(event.name.equals("inv")) {
				showInv = !showInv;
				showCapacities = showAttr = false;

				for(InputRectangle rect : buttonRects) {
					if(rect.getId().contains("inv-"))
						rect.setEnable(showInv);
				}
			}
			else if(event.name.contains("inv")) {
				showInv = false;
				((PlayableEntity) pGroup.getLeader()).useItem(Integer.parseInt(event.name.split("-")[1]));
			}
			else if(event.name.equals("attr")) {
				showAttr = !showAttr;
				showCapacities = showInv = false;

				for(InputRectangle rect : buttonRects) {
					if(rect.getId().contains("attr-"))
						rect.setEnable(showAttr);
				}
			}
			else if(event.name.contains("attr-")) {
				pGroup.getLeader().useAttributePoint(Attributes.values()[Integer.parseInt(event.name.split("-")[1])]);
			}
		}
		else {
			if(event.name.contains("hover-")) {
				hoveredCapaIndex = Integer.parseInt(event.name.split("-")[1]);
				if(hoveredCapaIndex < pGroup.getLeader().getCapacities().size())
					hoveredCapaDesc = pGroup.getLeader().getCapacities().get(hoveredCapaIndex).getDesc();
			}
			else if(event.name.contains("hoverInv-")) {
				hoveredInvIndex = Integer.parseInt(event.name.split("-")[1]);
				if(!pGroup.getLeader().getInventory().getItems().isEmpty())
					hoveredItemDesc = pGroup.getLeader().getInventory().getDesc(hoveredInvIndex);
			}
			else if(event.name.contains("hoverAttr-")) {
				hoveredAttrIndex = Integer.parseInt(event.name.split("-")[1]);
			}
		}
	}

	@Override
	public void fireEvent(GroupEvent event) {
		if(event.type == GroupEventType.NEW_MEMBER) {
			updatePortraits();
		}
		else if(event.type == GroupEventType.LEADER_CHANGE) {
			updateCapaRects();
			showCapacities = showAttr = showInv = false;
		}
	}
}
