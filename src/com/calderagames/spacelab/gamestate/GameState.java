package com.calderagames.spacelab.gamestate;

public interface GameState {
	
	public void update(double dt);
	
	public void render();
	
	public int getFBOTexture();
	
	public void displose();
}
