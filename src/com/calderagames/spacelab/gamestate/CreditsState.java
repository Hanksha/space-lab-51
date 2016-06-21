package com.calderagames.spacelab.gamestate;

import static com.calderagames.spacelab.util.ResolutionHandler.HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.WIDTH;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL13;

import com.calderagames.spacelab.font.TrueTypeFont;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamestate.GameStateManager.GameStates;
import com.calderagames.spacelab.graphics.FrameBufferObject;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.input.InputEvent;
import com.calderagames.spacelab.input.InputListener;
import com.calderagames.spacelab.input.InputProcessor;
import com.calderagames.spacelab.shader.ShaderProgramManager;

public class CreditsState implements GameState, InputListener {

	private GameContent gc;
	private GameStateManager gsm;

	private SpriteBatch sb;

	//FBO
	private FrameBufferObject fbo;

	private Sprite sprBg;
	private Sprite sprTitle;
	private Sprite sprFog;
	private Sprite sprHauLogo;

	private TrueTypeFont font32;

	private float scale;
	private float scaleAngle;
	
	private float fogPosX;
	
	private float posY = HEIGHT / 2;
	
	private String[] texts = new String[]{"saite 2015",
	                                      " game development competition entry",
	                                      "",
	                                      "",
	                                      "team",
	                                      "",
	                                      "Vanneza cura (art & animation)",
	                                      "Vivien Jovet (game design & programming)",
	                                      "",
	                                      "",
	                                      "Music",
	                                      "Ossuary 2 Turn - incompetech.com",
	                                      "Ossuary 4 Animate - incompetech.com",
	                                      "Mechanolith - incompetech.com",
	                                      "",
	                                      "Sound effects",
	                                      "(All from soundbible.com)",
	                                      "",
	                                      "",
	                                      "Made in one week with",
	                                      "light weight java game library",
	                                      };

	public CreditsState(GameStateManager gsm, GameContent gc) {
		this.gsm = gsm;
		this.gc = gc;

		//Init FBO
		fbo = new FrameBufferObject(WIDTH, HEIGHT);

		sb = gc.getSpriteBatch();

		gc.getRM().getTexture("backgroundtitle").load();

		sprBg = new Sprite(1280, 720, new TextureRegion(gc.getRM().getTexture("backgroundtitle"), 0, 0, 640, 360));
		sprTitle = new Sprite(402, 216, new TextureRegion(gc.getRM().getTexture("backgroundtitle"), 3, 363, 201, 108));
		sprFog = new Sprite(1280, 720, new TextureRegion(gc.getRM().getTexture("backgroundtitle"), 640, 0, 640, 360));
		sprHauLogo = new Sprite(654, 210, new TextureRegion(gc.getRM().getTexture("backgroundtitle"), 224, 363, 327, 105));

		font32 = gc.getRM().getFont("mithril", 32);
		
		gc.getAS().playMusic("main-theme1", true);
	}

	@Override
	public void update(double dt) {
		scaleAngle = (float) Math.min(scaleAngle + 80 * dt, 360f);
		scaleAngle = scaleAngle == 360f ? 0f : scaleAngle;

		scale = (float) (1f + 0.05f * Math.cos(Math.toRadians(scaleAngle)));
		
		fogPosX += 50 * dt;
		
		if(fogPosX >= 1280)
			fogPosX = 0;
		
		posY -= 25 * dt;
		
		while(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
					fireInput(new InputEvent("back", true));
			}
		}
		
		while(Controllers.next()) {
			if(Controllers.getEventButtonState()) {
				if(Controllers.getEventControlIndex() == InputProcessor.BUTTON_B)
					fireInput(new InputEvent("back", true));
			}
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
		sb.draw(sprTitle, WIDTH / 2 - (sprTitle.getWidth() / 2) * scale,  posY - (sprTitle.getHeight() / 2) * scale - 50, scale, false, 0);
		sb.draw(sprHauLogo, WIDTH / 2 - (sprHauLogo.getWidth() / 2) * scale,  posY - (sprHauLogo.getHeight() / 2) * scale + 290 + texts.length * (font32.getHeight() + 10), scale, false, 0);
		sb.end();

		int incY = 0;
		for(String text: texts) {
			font32.drawStringCenter(sb, WIDTH / 2, posY + incY + 200, text);
			incY += font32.getHeight() + 10;
		}
		
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
	public void fireInput(InputEvent event) {
		if(event.state) {
			if(event.name.equals("back")) {
				gsm.setState(GameStates.INTRO_STATE);
			}
		}
	}
}
