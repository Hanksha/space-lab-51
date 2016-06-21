package com.calderagames.spacelab.gamestate;

import static com.calderagames.spacelab.util.ResolutionHandler.HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.WIDTH;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL13;

import com.calderagames.spacelab.font.TrueTypeFont;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamestate.GameStateManager.GameStates;
import com.calderagames.spacelab.graphics.FrameBufferObject;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.hud.Event;
import com.calderagames.spacelab.hud.Menu;
import com.calderagames.spacelab.hud.MenuItem;
import com.calderagames.spacelab.hud.MenuListener;
import com.calderagames.spacelab.hud.RectangleText;
import com.calderagames.spacelab.hud.Event.EventType;
import com.calderagames.spacelab.shader.ShaderProgramManager;

public class IntroState implements GameState, MenuListener {

	private GameContent gc;
	private GameStateManager gsm;

	private SpriteBatch sb;

	//FBO
	private FrameBufferObject fbo;

	private Sprite sprBg;
	private Sprite sprTitle;
	private Sprite sprFog;

	private TrueTypeFont font32;

	private float scale;
	private float scaleAngle;
	
	private float fogPosX;
	
	private Menu menu;

	public IntroState(GameStateManager gsm, GameContent gc) {
		this.gsm = gsm;
		this.gc = gc;

		//Init FBO
		fbo = new FrameBufferObject(WIDTH, HEIGHT);

		sb = gc.getSpriteBatch();

		gc.getRM().getTexture("backgroundtitle").load();

		sprBg = new Sprite(1280, 720, new TextureRegion(gc.getRM().getTexture("backgroundtitle"), 0, 0, 640, 360));
		sprTitle = new Sprite(402, 216, new TextureRegion(gc.getRM().getTexture("backgroundtitle"), 3, 363, 201, 108));
		sprFog = new Sprite(1280, 720, new TextureRegion(gc.getRM().getTexture("backgroundtitle"), 640, 0, 640, 360));

		font32 = gc.getRM().getFont("mithril", 32);
		
		gc.getAS().playMusic("main-theme2", true);
		
		menu = new Menu(gc, "main");
		
		menu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, HEIGHT / 2 + 150, "press start", font32, true), "start"));
		menu.addMenuListener(this);
		
		Menu subMenu = new Menu(gc, "sub");
		subMenu.addMenuListener(this);
		menu.addSubMenu(subMenu);
		int y = HEIGHT / 2 + 120;
		subMenu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, y, "start game", font32, true), "start game"));
		y += 40;
		subMenu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, y, "credits", font32, true), "credits"));
		y += 40;
		subMenu.addMenuItem(new MenuItem(new RectangleText(WIDTH / 2, y, "quit", font32, true), "quit"));
		y += 40;
		
	}

	@Override
	public void update(double dt) {
		scaleAngle = (float) Math.min(scaleAngle + 80 * dt, 360f);
		scaleAngle = scaleAngle == 360f ? 0f : scaleAngle;

		scale = (float) (1f + 0.05f * Math.cos(Math.toRadians(scaleAngle)));
		
		fogPosX += 50 * dt;
		
		if(fogPosX >= 1280)
			fogPosX = 0;
		
		while(Keyboard.next()) {
			menu.handleKeyboardInput();
		}
		
		while(Mouse.next()) {
			menu.handleMouseInput();
		}
		
		while(Controllers.next()) {
			menu.handleControllerInput();
		}
		
		gc.getAS().update(WIDTH / 2, HEIGHT / 2);
	}

	@Override
	public void render() {
		//Active texture 0
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0);
		//Set the default shader
		gc.getSPM().setCurrentShaderProg(ShaderProgramManager.DEFAULT_SHADER);
		//Set projection matrix
		gc.getSPM().setProjectionMatrix(ShaderProgramManager.SCENE_PROJECTION);

		gc.getSPM().useProgram();
		fbo.Begin(gc.getSPM(), WIDTH, HEIGHT);

		sb.begin("backgroundtitle", null);

		sb.draw(sprBg, 0, 0, 0);
		sb.draw(sprFog, fogPosX, 0, 0);
		sb.draw(sprFog, fogPosX - 1280, 0, 0);
		sb.draw(sprTitle, WIDTH / 2 - (sprTitle.getWidth() / 2) * scale, HEIGHT / 2 - (sprTitle.getHeight() / 2) * scale - 50, scale, false, 0);
		sb.end();

		menu.renderText(sb);
		
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
		gc.getRM().getTexture("backgroundtitle").load();
		gc.getAS().stopMusic();
	}

	@Override
	public void actionPerformed(Event event) {
		if(event.getType() == EventType.VALIDATION) {
    		if(event.getActionCommand().equals("start")) {
    			menu.showSubMenu(0);
    		}
    		else if(event.getActionCommand().equals("credits")) {
    			gsm.setState(GameStates.CREDITS_STATE);
    		}
    		else if(event.getActionCommand().equals("start game")) {
    			System.out.println("test");
    			gsm.setState(GameStates.PLAY_STATE);
    		}
    		else if(event.getActionCommand().equals("quit")) {
    			gsm.exitAll();
    		}
		}
	}
}
