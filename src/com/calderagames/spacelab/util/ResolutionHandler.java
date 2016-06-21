package com.calderagames.spacelab.util;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class ResolutionHandler {

	//default values
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	public static float CURRENT_WIDTH = 1280;
	public static float CURRENT_HEIGHT = 720;

	public static float SCALE = CURRENT_WIDTH / WIDTH;

	public static float SCREEN_WIDTH = WIDTH * SCALE;
	public static float SCREEN_HEIGHT = HEIGHT * SCALE;

	public static float SCREEN_OFFSET_X = 0;
	public static float SCREEN_OFFSET_Y = 0;

	public static boolean linear_filtering = false;
	public static boolean isFullScreen = false;

	public static void setWindowDimension(float width, float height) {
		CURRENT_WIDTH = width;
		CURRENT_HEIGHT = height;

		if(CURRENT_WIDTH / CURRENT_HEIGHT == 16f / 9f || CURRENT_WIDTH / CURRENT_HEIGHT == 16f / 10f || CURRENT_WIDTH / CURRENT_HEIGHT == 4f / 3f) {
			SCALE = CURRENT_WIDTH / WIDTH;
			SCREEN_WIDTH = WIDTH * SCALE;
			SCREEN_HEIGHT = HEIGHT * SCALE;
			SCREEN_OFFSET_X = Math.abs(CURRENT_WIDTH - SCREEN_WIDTH) / 2;
			SCREEN_OFFSET_Y = Math.abs(CURRENT_HEIGHT - SCREEN_HEIGHT) / 2;
		}
		else {

			SCALE = (float) ((Math.floor(CURRENT_WIDTH / 16f) * 16f) / WIDTH);

			if(WIDTH * SCALE >= CURRENT_WIDTH)
				SCALE = (float) ((Math.floor(CURRENT_HEIGHT / 9f) * 9f) / HEIGHT);

			SCREEN_WIDTH = WIDTH * SCALE;
			SCREEN_HEIGHT = HEIGHT * SCALE;
			SCREEN_OFFSET_X = Math.abs(CURRENT_WIDTH - SCREEN_WIDTH) / 2;
			SCREEN_OFFSET_Y = Math.abs(CURRENT_HEIGHT - SCREEN_HEIGHT) / 2;
		}
	}

	public static float getPosX(float x) {
		return x + SCREEN_OFFSET_X;
	}

	public static float getPosY(float y) {
		return y + SCREEN_OFFSET_Y;
	}

	public static ResolutionList getAllResolution() {
		ArrayList<DisplayMode> buffer = new ArrayList<DisplayMode>();
		ArrayList<Integer> res_width = new ArrayList<Integer>();
		ArrayList<Integer> res_height = new ArrayList<Integer>();

		try {
			DisplayMode[] displays = Display.getAvailableDisplayModes();

			for(DisplayMode display : displays) {
				buffer.add(display);
			}
		} catch(LWJGLException e) {
			e.printStackTrace();
		}
		DisplayMode currentDisplay = null;

		for(int i = 0; i < buffer.size(); i++) {
			currentDisplay = buffer.get(i);

			for(int j = 0; j < buffer.size(); j++) {
				if(currentDisplay.getWidth() * currentDisplay.getHeight() < buffer.get(j).getWidth() * buffer.get(j).getHeight()) {
					break;
				}
				else if(j == buffer.size() - 1) {
					res_width.add(currentDisplay.getWidth());
					res_height.add(currentDisplay.getHeight());
					buffer.remove(i);
					i = 0;
					break;
				}
			}
		}

		//trim duplicated values
		for(int i = 0; i < res_width.size(); i++) {
			int width = res_width.get(i);
			int height = res_height.get(i);

			for(int j = i + 1; j < res_width.size(); j++) {
				if(width == res_width.get(j) && height == res_height.get(j)) {
					res_width.set(j, 0);
					res_height.set(j, 0);
				}
			}
		}
		for(int i = 0; i < res_width.size(); i++) {
			if(res_width.get(i) == 0) {
				res_width.remove(i);
				res_height.remove(i);
				i--;
			}
		}

		//add native resolution 1280 x 720
		res_width.add(0, 1280);
		res_height.add(0, 720);

		return new ResolutionList(res_width, res_height);
	}
	
	public static void setFullScreen() {
		isFullScreen = !isFullScreen;
		setDisplayMode(WIDTH, HEIGHT, isFullScreen);
	}

	public static void setDisplayMode(int width, int height, boolean fullscreen) {

		//return if requested DisplayMode is already set
		if((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen)) {
			return;
		}

		try {
			DisplayMode targetDisplayMode = null;

			if(fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for(int i = 0; i < modes.length; i++) {
					DisplayMode current = modes[i];

					if((current.getWidth() == width) && (current.getHeight() == height)) {
						if((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						/*if we've found a match for bpp and frequence against the original display mode 
						 * then it's probably best to go for this one 
						 * since it's most likely compatible with the monitor*/
						if((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			}
			else {
				targetDisplayMode = new DisplayMode(width, height);
			}

			if(targetDisplayMode == null) {
				System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch(LWJGLException e) {
			ErrorLog.writeToErrorLog(e.getStackTrace());
			System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}
	}
}
