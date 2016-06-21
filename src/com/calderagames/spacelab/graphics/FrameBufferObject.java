package com.calderagames.spacelab.graphics;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

import com.calderagames.spacelab.shader.ShaderProgramManager;
import com.joml.matrix.Matrix4f;
import com.joml.utils.CamMath;

public class FrameBufferObject {

	private int frameBufferID;
	private int colorTextureID;
	private int depthRenderBufferID;

	// dimensions
	private int FRAME_WIDTH;
	private int FRAME_HEIGHT;
	private Matrix4f projectionMatrix;

	// clear color
	private float r, g, b;

	public FrameBufferObject(int width, int height) {
		FRAME_WIDTH = width;
		FRAME_HEIGHT = height;

		frameBufferID = glGenFramebuffers();
		colorTextureID = glGenTextures();
		depthRenderBufferID = glGenRenderbuffers();

		// frame buffer object
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);

		// color texture
		glBindTexture(GL_TEXTURE_2D, colorTextureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, FRAME_WIDTH, FRAME_HEIGHT, 0, GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTextureID, 0);

		// depth buffer
		glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBufferID);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, FRAME_WIDTH, FRAME_HEIGHT);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBufferID);

		// check completeness
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) {
			// System.out.println("Frame buffer created sucessfully.");
		}
		else
			System.out.println("An error occured creating the frame buffer.");

		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		projectionMatrix = new Matrix4f();
		CamMath.ortho(0, FRAME_WIDTH, FRAME_HEIGHT, 0, 1, -1, projectionMatrix);
	}

	public void Begin(ShaderProgramManager spm, int width, int height) {
		glViewport(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		spm.getCurrentShaderProg().setUniformMat4fv("projection", projectionMatrix);
		spm.useProgram();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
		glClearColor(r, g, b, 0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * This method do not clear the fbo
	 * 
	 * @param width
	 * @param height
	 */
	public void Continue(ShaderProgramManager spm, int width, int height) {
		glViewport(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		spm.getCurrentShaderProg().setUniformMat4fv("projection", projectionMatrix);
		spm.useProgram();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
	}

	public void End() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void dispose() {
		glDeleteFramebuffers(frameBufferID);
		glDeleteRenderbuffers(depthRenderBufferID);
		glDeleteTextures(colorTextureID);
	}

	public void setClearColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public int getWidth() {
		return FRAME_WIDTH;
	}

	public int getHeight() {
		return FRAME_HEIGHT;
	}

	public int getTexture() {
		return colorTextureID;
	}
}
