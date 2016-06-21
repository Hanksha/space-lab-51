package com.calderagames.spacelab.graphics;

import org.lwjgl.opengl.GL11;

import com.calderagames.spacelab.gamecontent.ResourceManager;

public class TextureBinder {

	private ResourceManager rm;

	public int textId;

	public TextureBinder(ResourceManager rm) {
		this.rm = rm;
	}

	public void bindTexture(String texName) {
		if(textId == rm.getTexture(texName).getTextureID())
			return;

		textId = rm.getTexture(texName).getTextureID();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textId);
	}

	public void bindTexture(int id) {
		if(textId == id || id == -1)
			return;

		textId = id;

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textId);

	}

}
