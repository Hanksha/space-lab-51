package com.calderagames.spacelab;

import static com.calderagames.spacelab.util.ResolutionHandler.CURRENT_HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.CURRENT_WIDTH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamestate.GameStateManager;
import com.calderagames.spacelab.input.InputProcessor;
import com.calderagames.spacelab.util.BufferLoader;
import com.calderagames.spacelab.util.ErrorLog;
import com.calderagames.spacelab.util.ResolutionHandler;
import com.calderagames.spacelab.util.Timer;

public class Game {

	//time step
	public static final int TARGET_FPS = 60;
	private long startTime;
	private double dt;
	public static int FPS;
	private int frameCounter;
	private Timer timerFPS;
	
	//Game content
	private GameContent gc;
	
	//Game state manager
	private GameStateManager gsm;
	
	
	public Game() {
		setUpDisplay();
		setUpOpenGL();
		setUpOpenAL();
		InputProcessor.init();
		gc = new GameContent();
		gsm = new GameStateManager(gc, this);
		
		gameLoop();
	}

	private void gameLoop() {
		startTime = System.nanoTime();
		timerFPS = new Timer(1000);
		
		while(!Display.isCloseRequested()) {

			//calculate delta time
			dt = (System.nanoTime() - startTime) / 1000000000d;
			startTime = System.nanoTime();

			//clear screen
			GL11.glClearColor(0f, 0f, 0f, 0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			//update game logic
			update();

			//render
			render();

			frameCounter++;

			if(timerFPS.tick()) {
				FPS = frameCounter;
				frameCounter = 0;
				timerFPS.update();
			}

			//update display
			Display.update();
			Display.sync(TARGET_FPS);
		}
		exit();
	}
	
	private void update() {
		Display.setTitle("Space Lab 51 - FPS: " + FPS);
		InputProcessor.update();
		gsm.update(dt);
	}
	
	private void render() {
		gsm.render();
	}
	
	private void setUpDisplay() {
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		
		ResolutionHandler.setDisplayMode((int) CURRENT_WIDTH, (int) CURRENT_HEIGHT, false);
		Display.setTitle("Space Lab 51");
		try {
			Display.setIcon(BufferLoader.loadIcon(new FileInputStream("./resources/logo.png")));
		} catch(FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Display.setVSyncEnabled(true);
		Display.setResizable(false);
		
		try {
			Display.create(pixelFormat, contextAttributes);
		} catch(LWJGLException e) {
			ErrorLog.writeToErrorLog(e.getStackTrace());
			e.printStackTrace();
		}
	}
	
	private void setUpOpenGL() {
		GL11.glViewport(0, 0, (int) CURRENT_WIDTH, (int) CURRENT_HEIGHT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glClearColor(0f, 0f, 0f, 0f);
	}
	
	private void setUpOpenAL() {
		try {
			AL.create();
		} catch(LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void exit() {
		gc.dispose();
		AL.destroy();
		Display.destroy();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		File nativeFolder = new File("native");
		System.setProperty("org.lwjgl.librarypath", nativeFolder.getAbsolutePath());
		System.setProperty("java.library.path", nativeFolder.getAbsolutePath());
		new Game();
	}
}
