package com.calderagames.spacelab.gamestate;

import static com.calderagames.spacelab.util.ResolutionHandler.HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.WIDTH;

import java.util.ArrayList;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.calderagames.spacelab.audio.MusicManager;
import com.calderagames.spacelab.entities.BattleManager;
import com.calderagames.spacelab.entities.Brother;
import com.calderagames.spacelab.entities.DialogueTrigger;
import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.EntityGroup;
import com.calderagames.spacelab.entities.PlayableEntity;
import com.calderagames.spacelab.entities.UnitEngineer;
import com.calderagames.spacelab.entities.UnitMedic;
import com.calderagames.spacelab.entities.UnitSoldier;
import com.calderagames.spacelab.entities.EntityGroup.GroupFormation;
import com.calderagames.spacelab.font.TrueTypeFont;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.gamestate.GameStateManager.GameStates;
import com.calderagames.spacelab.graphics.Camera;
import com.calderagames.spacelab.graphics.FrameBufferObject;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.hud.Event;
import com.calderagames.spacelab.hud.HUD;
import com.calderagames.spacelab.hud.Menu;
import com.calderagames.spacelab.hud.MenuItem;
import com.calderagames.spacelab.hud.MenuListener;
import com.calderagames.spacelab.hud.RectangleText;
import com.calderagames.spacelab.hud.Event.EventType;
import com.calderagames.spacelab.input.InputEvent;
import com.calderagames.spacelab.input.InputListener;
import com.calderagames.spacelab.input.InputProcessor;
import com.calderagames.spacelab.shader.ShaderProgramManager;
import com.calderagames.spacelab.util.MathUtil;
import com.calderagames.spacelab.util.ResolutionHandler;
import com.calderagames.spacelab.util.Timer;

public class PlayState implements GameState, InputListener, MenuListener {

	private GameContent gc;
	private SpriteBatch sb;
	private GameStateManager gsm;

	//FBO
	private FrameBufferObject fbo;

	private Map testMap;
	/**Battle manager*/
	private BattleManager bm;
	private MusicManager musicManager;
	private Camera cam;
	private boolean freeCam, leftCam, rightCam, upCam, downCam;

	private HUD hud;
	private Sprite sprBg;
	private Sprite sprMouseLeft;
	private Sprite sprMouseRight;
	private Sprite sprBtnA;
	private Sprite sprBtnX;
	private Sprite sprBtnY;

	private Menu menu;
	private boolean showMenu;
	private TrueTypeFont font32;
	private TrueTypeFont font16;
	private boolean isGameOver;

	/**Group controlled by the player*/
	private EntityGroup pGroup;
	private PlayableEntity soldier;
	private PlayableEntity medic;
	private PlayableEntity engineer;
	private DynamicEntity brother;

	private Timer timerIntroText;
	private String introText = "Agent Preston,$" + "$" + "Station 51 is the a space laboratory of high importance to Caldera Corp.$" +
							   "the station didn't give any sign of life since a week.$" +
							   "your mission is to go near the station and check if it is still operational.$" +
							   "do not enter the station, I repeat do not enter the station.$" + "the research of the laboratory is top secret.$" +
							   "report directly to me after your mission$" + "$" + "Chief Agent Jobokai$" + "Caldera corp.$$$$";
	private int introTextIndex;

	private ArrayList<DialogueTrigger> dialTriggs;
	private DialogueTrigger currDialTrigg;

	public PlayState(GameStateManager gsm, GameContent gc) {
		this.gc = gc;
		this.gsm = gsm;
		sb = gc.getSpriteBatch();
		cam = gc.getSPM().getCam();

		//Init FBO
		fbo = new FrameBufferObject(WIDTH, HEIGHT);

		pGroup = new EntityGroup(gc, GroupFormation.LINE_FRONT);

		bm = new BattleManager(gc, pGroup);

		musicManager = new MusicManager(gc.getAS(), bm);

		testMap = new Map(gc, bm, "./resources/maps/map01.mpm");

		cam.updateBounds(testMap.getTileMap());

		gc.getRM().getTexture("texatlas").load();
		gc.getRM().getTexture("backgroundtitle").load();

		gc.getPathFinder().setMap(testMap);

		soldier = new UnitSoldier(gc, testMap, bm, 13, 1);
		medic = new UnitMedic(gc, testMap, bm, 18, 18);
		engineer = new UnitEngineer(gc, testMap, bm, 31, 11);
		brother = new Brother(gc, testMap, 39, 74);

		pGroup.addEntity(soldier);
		pGroup.setLeader(soldier);

		hud = new HUD(gc, bm, pGroup);

		sprBg = new Sprite(1280, 720, new TextureRegion(gc.getRM().getTexture("backgroundtitle"), 0, 0, 640, 360));
		sprMouseLeft = new Sprite(27, 44, new TextureRegion(gc.getRM().getTexture("texatlas"), 449, 516, 27, 44));
		sprMouseRight = new Sprite(27, 44, new TextureRegion(gc.getRM().getTexture("texatlas"), 476, 516, 27, 44));
		sprBtnA = new Sprite(32, 32, new TextureRegion(gc.getRM().getTexture("texatlas"), 251, 514, 32, 32));
		sprBtnX = new Sprite(32, 32, new TextureRegion(gc.getRM().getTexture("texatlas"), 284, 514, 32, 32));
		sprBtnY = new Sprite(32, 32, new TextureRegion(gc.getRM().getTexture("texatlas"), 317, 514, 32, 32));

		font32 = gc.getRM().getFont("mithril", 32);
		font16 = gc.getRM().getFont("mithril", 16);

		menu = new Menu(gc, "main");
		menu.addMenuListener(this);

		menu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, HEIGHT / 2 - 30, "fullscreen", font32, true), "fullscreen"));
		menu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, HEIGHT / 2 + 30, "quit", font32, true), "quit"));

		Menu subMenu = new Menu(gc, "submenu");
		subMenu.addMenuListener(this);
		subMenu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, HEIGHT / 2 - 30, "are you sure?", font32, true), "quit question"));
		subMenu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, HEIGHT / 2, "yes", font32, true), "yes quit"));
		subMenu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, HEIGHT / 2 + 30, "no", font32, true), "no"));

		menu.addSubMenu(subMenu);

		timerIntroText = new Timer(100);

		dialTriggs = new ArrayList<DialogueTrigger>();
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "Aahhh die mutant!", 200, 2000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "Oh you're not a mutant, sorry!", 200, 2000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "What are you doing here?", 200, 2000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 18, 18, "I'm on a mission, what happened here?", 200, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "The mu... the mutant, they rebelled...", 200, 2500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 18, 18, "What mutant?", 200, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "Station 51 was leading a research to create a human mutant", 200, 6000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "capable of growing healthy human organs and then gather them", 200, 6000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "But something happened...", 200, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "We have to leave the place now!", 200, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 18, 18, "No, I'm looking for my brother in law", 200, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 18, 18, "He's working here, Danish, do you know him?", 200, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "Yes, he's working as a DNA programmer", 200, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, medic, 18, 18, "Maybe he's hiding in the computer room", 200, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 18, 18, "Ok let's go!", 200, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, engineer, 27, 11, "Psssst!", 10, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, engineer, 27, 11, "Over here, quiet!", 10, 2000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 27, 11, "Who are you?", 10, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, engineer, 27, 11, "I'm Chris, I was member of a maintenance ship", 10, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, engineer, 27, 11, "We received a short distance SOS from the station", 10, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, engineer, 27, 11, "When we arrived at the dock, all of my crew was killed by some monsters", 10, 3500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, engineer, 27, 11, "I ... I ranaway until here, but they are too many", 10, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 27, 11, "Go with us, let's kick some mutant ass !", 10, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, engineer, 27, 11, "I will avenge my comrades!", 10, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, brother, 39, 74, "Preston!", 72, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, brother, 39, 74, "You're here!", 72, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, brother, 39, 74, "I can't believe it", 72, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 39, 74, "Your sister send me", 72, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 39, 74, "You're are safe now, the area is clear", 72, 3000));
		dialTriggs.add(new DialogueTrigger(gc, testMap, brother, 39, 74, "Preston?", 72, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, soldier, 39, 74, "Yes?", 72, 1500));
		dialTriggs.add(new DialogueTrigger(gc, testMap, brother, 39, 74, "I think some mutant escaped the station...", 72, 5000));
	}

	@Override
	public void update(double dt) {

		gc.getAS().update(pGroup.getLeader().getPosition().x, pGroup.getLeader().getPosition().y);

		if(timerIntroText.tick() && introTextIndex < introText.length()) {
			introTextIndex++;
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				timerIntroText.setDelay(10);
			else if(introTextIndex != introText.length() && introText.charAt(introTextIndex) == '$')
				timerIntroText.setDelay(200);
			else
				timerIntroText.setDelay(50);

			gc.getAS().playSoundEffect(1, 0.3f, "beep");

			timerIntroText.update();
		}

		if(!isIntroTextDone())
			return;

		//update dialogues
		if(currDialTrigg == null) {
			for(int i = 0; i < dialTriggs.size(); i++) {
				DialogueTrigger dial = dialTriggs.get(i);

				if(dial.update(pGroup.getLeader())) {
					currDialTrigg = dial;
					break;
				}

			}
		}
		else {
			if(currDialTrigg.isDone())
				currDialTrigg = null;
		}

		if(medic.getGroup() == null &&
		   MathUtil.getDistance(medic.getPosition().getX(), medic.getPosition().getY(), soldier.getPosition().getX(), soldier.getPosition().getY()) <= 200)
			pGroup.addEntity(medic);

		if(engineer.getGroup() == null && MathUtil.getDistance(engineer.getPosition().getX(), engineer.getPosition().getY(), soldier.getPosition().getX(),
															   soldier.getPosition().getY()) <= 300)
			pGroup.addEntity(engineer);

		//KEYBOARD INPUT
		while(Keyboard.next()) {

			if(Keyboard.getEventKeyState()) {
				if(Keyboard.getEventKey() == Keyboard.KEY_F1)
					freeCam = !freeCam;
				else if(Keyboard.getEventKey() == Keyboard.KEY_F12) {
					ResolutionHandler.setFullScreen();
				}
				else if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					fireInput(new InputEvent("menu", true));
				}
				else if(Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
					currDialTrigg = null;
				}

				if(Keyboard.getEventKey() == Keyboard.KEY_LEFT)
					fireInput(new InputEvent("-xc", true));
				else if(Keyboard.getEventKey() == Keyboard.KEY_RIGHT)
					fireInput(new InputEvent("+xc", true));

				if(Keyboard.getEventKey() == Keyboard.KEY_UP)
					fireInput(new InputEvent("-yc", true));
				else if(Keyboard.getEventKey() == Keyboard.KEY_DOWN)
					fireInput(new InputEvent("+yc", true));
			}
			else {
				if(Keyboard.getEventKey() == Keyboard.KEY_LEFT)
					fireInput(new InputEvent("-xc", false));
				else if(Keyboard.getEventKey() == Keyboard.KEY_RIGHT)
					fireInput(new InputEvent("+xc", false));

				if(Keyboard.getEventKey() == Keyboard.KEY_UP)
					fireInput(new InputEvent("-yc", false));
				else if(Keyboard.getEventKey() == Keyboard.KEY_DOWN)
					fireInput(new InputEvent("+yc", false));
			}
			if(showMenu)
				menu.handleKeyboardInput();
			else if(!isGameOver && !isDialoguePlaying())
				hud.handleKeyboardInput();
		}

		//MOUSE INPUT
		while(Mouse.next()) {

			if(InputProcessor.isMouseInWindow()) {

				if(InputProcessor.getMouseX() <= 32) {
					if(!leftCam)
						fireInput(new InputEvent("-xc", true));
				}
				else if(leftCam)
					fireInput(new InputEvent("-xc", false));

				if(InputProcessor.getMouseX() >= ResolutionHandler.SCREEN_WIDTH - 32) {
					if(!rightCam)
						fireInput(new InputEvent("+xc", true));
				}
				else if(rightCam)
					fireInput(new InputEvent("+xc", false));

				if(InputProcessor.getMouseY() <= 32) {
					if(!upCam)
						fireInput(new InputEvent("-yc", true));
				}
				else if(upCam)
					fireInput(new InputEvent("-yc", false));

				if(InputProcessor.getMouseY() >= ResolutionHandler.SCREEN_HEIGHT - 32) {
					if(!downCam)
						fireInput(new InputEvent("+yc", true));
				}
				else if(downCam)
					fireInput(new InputEvent("+yc", false));
			}
			if(showMenu)
				menu.handleMouseInput();
			else if(!isGameOver && !isDialoguePlaying())
				hud.handleMouseInput();
		}

		//CONTROLLER INPUT
		while(Controllers.next()) {

			if(Controllers.getEventButtonState()) {
				if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_START) {
					fireInput(new InputEvent("menu", true));
				}
			}

			if(Controllers.isEventAxis()) {
				if(Controllers.getEventSource().getRXAxisValue() < 0)
					fireInput(new InputEvent("-xc", true));
				else if(Controllers.getEventSource().getRXAxisValue() > 0)
					fireInput(new InputEvent("+xc", true));
				else {
					fireInput(new InputEvent("-xc", false));
					fireInput(new InputEvent("+xc", false));
				}
				if(Controllers.getEventSource().getRYAxisValue() < 0)
					fireInput(new InputEvent("-yc", true));
				else if(Controllers.getEventSource().getRYAxisValue() > 0)
					fireInput(new InputEvent("+yc", true));
				else {
					fireInput(new InputEvent("-yc", false));
					fireInput(new InputEvent("+yc", false));
				}
			}
			if(showMenu)
				menu.handleControllerInput();
			else if(!isGameOver && !isDialoguePlaying())
				hud.handleControllerInput();
		}

		if(pGroup.isGroupDead())
			isGameOver = true;

		menu.update(dt);

		pGroup.update(dt);

		testMap.update(dt);

		bm.update(dt);

		hud.update(dt);

		musicManager.update();

		if(!bm.isBattle()) {
			cam.setX(-pGroup.getLeader().getPosition().x + WIDTH / 2);
			cam.setY(-pGroup.getLeader().getPosition().y + HEIGHT / 2);
		}
		else {
			cam.setX(-bm.getPlayingEntity().getPosition().x + WIDTH / 2);
			cam.setY(-bm.getPlayingEntity().getPosition().y + HEIGHT / 2);
		}

		/*if(leftCam)
			cam.setX((float) (cam.x + 150 * dt));
		else if(rightCam)
			cam.setX((float) (cam.x - 150 * dt));
		
		if(upCam)
			cam.setY((float) (cam.y + 150 * dt));
		else if(downCam)
			cam.setY((float) (cam.y - 150 * dt));*/
	}

	private void drawControlInfo(SpriteBatch sb) {
		if(InputProcessor.isGamepad()) {
			sb.draw(50, 550, sprBtnA, -1);
			sb.draw(50, 600, sprBtnX, -1);
			sb.draw(50, 650, sprBtnY, -1);
		}
		else {
			sb.draw(50, 550, sprMouseLeft, -1);
			sb.draw(50, 600, sprMouseRight, -1);
		}
	}

	private void drawControlInfoText(SpriteBatch sb) {
		font16.drawString(sb, 70, 545, "move, interact");
		font16.drawString(sb, 70, 595, "select target, use capacity");
		if(InputProcessor.isGamepad())
			font16.drawString(sb, 70, 645, "change group leader");
	}

	@Override
	public void render() {

		//render light map
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0);
		//Set the default shader
		gc.getSPM().setCurrentShaderProg(ShaderProgramManager.DEFAULT_SHADER);
		gc.getSPM().useProgram();
		testMap.getLightMap().render(sb, (PlayableEntity) pGroup.getLeader());

		gc.getSPM().endProgram();

		gc.getSPM().getShaderProg(ShaderProgramManager.MAIN_SHADER).bindTexture(1, GL11.GL_TEXTURE_2D, testMap.getLightMap().getTexture());

		//Active texture 0
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0);
		fbo.Begin(gc.getSPM(), WIDTH, HEIGHT);
		//Set the main shader
		gc.getSPM().setCurrentShaderProg(ShaderProgramManager.MAIN_SHADER);
		//Set projection matrix
		gc.getSPM().setProjectionMatrix(ShaderProgramManager.SCENE_PROJECTION);

		gc.getSPM().useProgram();

		sb.begin("backgroundtitle", null);
		sb.setColor(1f, 1f, 1f, 1f);
		sb.draw(sprBg, 0, 0, 0);

		sb.end();

		sb.begin("texatlas", cam);

		if(isIntroTextDone()) {
			testMap.render(sb);

			hud.renderBack(sb);

			pGroup.render(sb);

			brother.render(sb);

			if(medic.getGroup() == null)
				medic.render(sb);

			if(engineer.getGroup() == null)
				engineer.render(sb);
		}

		sb.end();

		gc.getSPM().endProgram();
		sb.end();

		gc.getSPM().endProgram();

		//Set the default shader
		gc.getSPM().setCurrentShaderProg(ShaderProgramManager.DEFAULT_SHADER);
		//Set projection matrix
		gc.getSPM().setProjectionMatrix(ShaderProgramManager.SCENE_PROJECTION);

		gc.getSPM().useProgram();

		sb.begin("texatlas", null);

		if(showMenu || !isIntroTextDone())
			drawControlInfo(sb);

		if(!isGameOver && isIntroTextDone())
			hud.render(sb);

		sb.end();

		gc.getSPM().endProgram();

		gc.getSPM().setCurrentShaderProg(ShaderProgramManager.FONT_SHADER);
		gc.getSPM().setProjectionMatrix(ShaderProgramManager.SCENE_PROJECTION);
		gc.getSPM().useProgram();

		sb.setCam(null);

		if(!isGameOver && isIntroTextDone())
			hud.renderText(sb);

		if(showMenu && isIntroTextDone())
			menu.renderText(sb);

		if(isGameOver)
			font32.drawStringCenter(sb, WIDTH / 2, 50, "game over");

		if(testMap.isWin() && isGameFinish()) {
			font32.drawStringCenter(sb, WIDTH / 2, 50, "You killed the last mutant");
			font32.drawStringCenter(sb, WIDTH / 2, 80, "And saved your brother in law");
		}

		if(showMenu || !isIntroTextDone())
			drawControlInfoText(sb);

		if(!isIntroTextDone())
			gc.getRM().getFont("mithril", 16).drawString(sb, 50, 50, introText.substring(0, introTextIndex));

		sb.setCam(cam);
		testMap.renderText(sb);
		sb.setCam(null);

		sb.end();
		fbo.End();
		gc.getSPM().endProgram();
	}

	@Override
	public int getFBOTexture() {
		return fbo.getTexture();
	}

	@Override
	public void displose() {
		fbo.dispose();
		testMap.dispose();
		gc.getRM().getTexture("texatlas").dispose();
		gc.getRM().getTexture("backgroundtitle").dispose();
	}

	private boolean isDialoguePlaying() {
		return currDialTrigg != null;
	}

	private boolean isIntroTextDone() {
		return introTextIndex == introText.length() && timerIntroText.tick();
	}

	private boolean isGameFinish() {
		return dialTriggs.isEmpty();
	}

	@Override
	public void fireInput(InputEvent event) {
		if(event.state) {
			if(event.name.equals("free cam")) {
				freeCam = !freeCam;
			}
			else if(event.name.equals("-xc")) {
				leftCam = true;
				rightCam = false;
			}
			else if(event.name.equals("+xc")) {
				leftCam = false;
				rightCam = true;
			}
			else if(event.name.equals("-yc")) {
				upCam = true;
				downCam = false;
			}
			else if(event.name.equals("+yc")) {
				upCam = false;
				downCam = true;
			}
			else if(event.name.equals("menu")) {
				showMenu = !showMenu;
			}
		}
		else {
			if(event.name.equals("-xc"))
				leftCam = false;
			else if(event.name.equals("+xc"))
				rightCam = false;
			else if(event.name.equals("-yc"))
				upCam = false;
			else if(event.name.equals("+yc"))
				downCam = false;
		}

	}

	@Override
	public void actionPerformed(Event event) {
		if(event.getType() == EventType.VALIDATION) {
			if(event.getActionCommand().equals("fullscreen")) {
				ResolutionHandler.setFullScreen();
			}
			else if(event.getActionCommand().equals("quit")) {
				menu.showSubMenu(0);
			}
			else if(event.getActionCommand().equals("yes quit")) {
				gsm.setState(GameStates.INTRO_STATE);
			}
		}
	}

}
