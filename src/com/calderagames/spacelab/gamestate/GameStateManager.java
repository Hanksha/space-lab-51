package com.calderagames.spacelab.gamestate;


import static com.calderagames.spacelab.util.ResolutionHandler.CURRENT_HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.CURRENT_WIDTH;
import static com.calderagames.spacelab.util.ResolutionHandler.HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.SCREEN_HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.SCREEN_OFFSET_X;
import static com.calderagames.spacelab.util.ResolutionHandler.SCREEN_OFFSET_Y;
import static com.calderagames.spacelab.util.ResolutionHandler.SCREEN_WIDTH;
import static com.calderagames.spacelab.util.ResolutionHandler.WIDTH;
import static com.calderagames.spacelab.util.ResolutionHandler.linear_filtering;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.opengl.GL13;

import com.calderagames.spacelab.Game;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.shader.ShaderProgramManager;

public class GameStateManager {
	
	private GameContent gc;
	private Game game;
	
	private GameState gameState;
	
	private Sprite sprScreen;
	
	public enum GameStates {
		INTRO_STATE,
		MENU_STATE,
		PLAY_STATE,
		CREDITS_STATE
	}
	
	public GameStateManager(GameContent gc, Game game) {
		this.gc = gc;
		this.game = game;
		
		sprScreen = new Sprite(WIDTH, HEIGHT, new TextureRegion(WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT));
		
		setState(GameStates.INTRO_STATE);
	}
	
	
	public void update(double dt) {
		//Update the current game state
		gameState.update(dt);

		//if the display resolution is modify it updates the projection matrix and the textured quad
		if(SCREEN_WIDTH != sprScreen.getWidth() || SCREEN_HEIGHT != sprScreen.getHeight()) {
			sprScreen.setWidth(SCREEN_WIDTH);
			sprScreen.setHeight(SCREEN_HEIGHT);
			gc.getSPM().updateProjScreen();
		}
	}
	
	public void render() {
		//render state on it's fbo
		gameState.render();

		glViewport(0, 0, (int) CURRENT_WIDTH, (int) CURRENT_HEIGHT);

		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, linear_filtering ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, linear_filtering ? GL_LINEAR : GL_NEAREST);
		
		gc.getSPM().setCurrentShaderProg(ShaderProgramManager.DEFAULT_SHADER);
		gc.getSPM().setProjectionMatrix(ShaderProgramManager.SCREEN_PROJECTION);

		gc.getSPM().useProgram();
		gc.getSpriteBatch().setColor(1.0f, 1.0f, 1.0f, 1.0f);
		gc.getSpriteBatch().begin(gameState.getFBOTexture());
		gc.getSpriteBatch().draw(sprScreen, SCREEN_OFFSET_X, SCREEN_OFFSET_Y, 1, 1, 0, false, true, -1);
		gc.getSpriteBatch().end();

		gc.getTexBinder().bindTexture(0);

		gc.getSPM().endProgram();
	}
	
	public void setState(GameStates state) {
		if(gameState != null)
			gameState.displose();
		
		System.gc();
		
		if(state == GameStates.INTRO_STATE)
			gameState = new IntroState(this, gc);
		else if(state == GameStates.MENU_STATE)
			gameState = new MenuState(this, gc);
		else if(state == GameStates.PLAY_STATE)
			gameState = new PlayState(this, gc);
		else if(state == GameStates.CREDITS_STATE)
			gameState = new CreditsState(this, gc);
	}
	
	public void exitAll() {
		game.exit();
	}
}
