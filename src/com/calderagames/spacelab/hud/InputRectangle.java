package com.calderagames.spacelab.hud;

import org.newdawn.slick.geom.Rectangle;

import com.calderagames.spacelab.input.InputEvent;
import com.calderagames.spacelab.input.InputListener;

@SuppressWarnings("serial")
public class InputRectangle extends Rectangle {

	private InputListener listener;
	private String id;
	private boolean enable;
	
	public InputRectangle(InputListener listener, String id, float x, float y, float width, float height) {
		super(x, y, width, height);
		this.listener = listener;
		this.id = id;
		enable = true;
	}
	
	public boolean contains(float x, float y) {
		if(!enable)
			return false;
		
		boolean result = super.contains(x, y);
		
		if(result)
			listener.fireInput(new InputEvent(id, true));
		
		return result;
	}
	
	public void setEnable(boolean b) {
		enable = b;
	}
	
	public String getId() {
		return id;
	}
}
