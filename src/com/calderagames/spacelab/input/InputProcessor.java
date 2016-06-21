package com.calderagames.spacelab.input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.calderagames.spacelab.util.BufferLoader;
import com.calderagames.spacelab.util.ErrorLog;
import com.calderagames.spacelab.util.Timer;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DirectAndRawInputEnvironmentPlugin;
import net.java.games.input.Rumbler;

import static com.calderagames.spacelab.util.ResolutionHandler.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;

public abstract class InputProcessor {

	// controller buttons
	public static int BUTTON_A = 0;
	public static int BUTTON_B = 1;
	public static int BUTTON_X = 2;
	public static int BUTTON_Y = 3;
	public static int BUTTON_LB = 4;
	public static int BUTTON_RB = 5;
	public static int BUTTON_SELECT = 6;
	public static int BUTTON_START = 7;
	public static int BUTTON_JL = 8;
	public static int BUTTON_JR = 9;

	//controller
	public static Controller gamePad;
	public static boolean controllerOn = false;
	public static Timer timerCheckController = new Timer(1000);

	public static void init() {
		try {
			IntBuffer cursorBuffer = BufferLoader.loadCursorIntBuffer(new FileInputStream("./resources/cursor.png"))[1];
			Mouse.setNativeCursor(new Cursor(32, 32, 4, 31, 1, cursorBuffer, null));
			Keyboard.create();
			Mouse.create();
			Controllers.create();
			
			for(Controller c: ControllerEnvironment.getDefaultEnvironment().getControllers()) {
				if(c.getType() == Controller.Type.GAMEPAD){
					gamePad = c;
					controllerOn = true;
					for(Rumbler r: gamePad.getRumblers())
						r.rumble(10f);
				}
			}
			
		} catch(LWJGLException | FileNotFoundException e) {
			ErrorLog.writeToErrorLog(e.getStackTrace());
			e.printStackTrace();
		}
	}

	public static void update() {
		if(timerCheckController.tick()) {
			timerCheckController.update();
			controllerOn = false;
			DirectAndRawInputEnvironmentPlugin env = new DirectAndRawInputEnvironmentPlugin();
			
			for(Controller c: env.getControllers()) {
				if(c.getType() == Controller.Type.GAMEPAD){
					gamePad = c;
					controllerOn = true;
				}
			}
		}
		
		if(gamePad != null)
			gamePad.poll();
			
		
		Keyboard.poll();
		Mouse.poll();
	}

	public static boolean isGamepad() {
		return controllerOn;
	}
	
	public static boolean isMouseInWindow() {
		return getMouseX() >= 0 && getMouseX() <= SCREEN_WIDTH && getMouseY() >= 0 && getMouseY() <= SCREEN_HEIGHT;
	}
	
	public static int getMouseX() {
		return (int) ((((Mouse.getX() + SCREEN_OFFSET_X) * WIDTH) / SCREEN_WIDTH));
	}

	public static int getMouseY() {
		return (int) ((((CURRENT_HEIGHT - (Mouse.getY() + SCREEN_OFFSET_Y) - 1) * HEIGHT) / SCREEN_HEIGHT));
	}
}
