package com.calderagames.spacelab.graphics;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.calderagames.spacelab.util.ErrorLog;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {

	private int width;
	private int height;
	private int id;
	private String filepath;
	private int filter;
	private int texUnit;
	private boolean loaded;

	public Texture(String filepath, int filter, int texUnit) {
		this.filepath = filepath;
		this.filter = filter;
		this.texUnit = texUnit;

		//get width and height
		try {
			InputStream in = new FileInputStream(filepath);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			in.close();
		} catch(IOException e) {
			ErrorLog.writeToErrorLog(e.getStackTrace());
			e.printStackTrace();
		}

	}

	public void load() {
		if(loaded)
			return;

		ByteBuffer buffer = null;

		try {
			InputStream in = new FileInputStream(filepath);
			PNGDecoder decoder = new PNGDecoder(in);

			width = decoder.getWidth();
			height = decoder.getHeight();

			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			in.close();

		} catch(IOException e) {
			ErrorLog.writeToErrorLog(e.getStackTrace());
			e.printStackTrace();
		}

		genTexture(buffer);

		loaded = true;
	}

	public Texture(BufferedImage image, int filter, int texUnit) throws IOException {
		width = image.getWidth();
		height = image.getHeight();
		this.filter = filter;
		this.texUnit = texUnit;

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); //Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); //Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF)); //Alpha component. Only for RGBA
			}
		}

		genTexture(buffer);

		loaded = true;
	}

	private void genTexture(ByteBuffer buffer) {
		buffer.flip();

		id = GL11.glGenTextures();
		GL13.glActiveTexture(texUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		buffer.clear();
	}

	public int getTextureID() {
		return id;
	}

	public int getTextureWidth() {
		return width;
	}

	public int getTextureHeight() {
		return height;
	}

	public void dispose() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDeleteTextures(id);
		id = 0;
		loaded = false;
	}
}
