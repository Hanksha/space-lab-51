package com.calderagames.spacelab.shader;

import static com.calderagames.spacelab.util.ResolutionHandler.HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.WIDTH;

import java.util.Stack;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Camera;
import com.calderagames.spacelab.util.ResolutionHandler;
import com.joml.matrix.Matrix4f;
import com.joml.utils.CamMath;

public class ShaderProgramManager {

	public static final short NUM_SHADER = 3;
	public static final short DEFAULT_SHADER = 0;
	public static final short MAIN_SHADER = 1;
	public static final short FONT_SHADER = 2;

	//shaders
	private ShaderProgram[] shaders;
	private ShaderProgram currentShaderProg;
	private short currentShaderIndex;

	//matrix
	private Matrix4f projectionMatrix;
	private Matrix4f projectionScreenMatrix;
	private Stack<Matrix4f> matrixStack;
	private Matrix4f modelViewMatrix;

	//camera
	private Camera cam;

	public static final int SCENE_PROJECTION = 0;
	public static final int SCREEN_PROJECTION = 1;

	public ShaderProgramManager(GameContent gc) {
		matrixStack = new Stack<Matrix4f>();
		modelViewMatrix = new Matrix4f();
		modelViewMatrix.identity();
		
		projectionMatrix = new Matrix4f();
		projectionScreenMatrix = new Matrix4f();
		CamMath.ortho(0, WIDTH, HEIGHT, 0, 1, -1, projectionMatrix);
		CamMath.ortho(0, ResolutionHandler.CURRENT_WIDTH, ResolutionHandler.CURRENT_HEIGHT, 0, 1, -1, projectionScreenMatrix);
		
		cam = new Camera();
		
		shaders = new ShaderProgram[NUM_SHADER];
		//load and set default shader
		shaders[DEFAULT_SHADER] = new ShaderProgram("./resources/shaders/default_shader.vs.glsl", "./resources/shaders/default_shader.fs.glsl", "default");
		shaders[DEFAULT_SHADER].addLocation("u_colortexture");
		shaders[DEFAULT_SHADER].setUniform1i("u_colortexture", 0);
		shaders[DEFAULT_SHADER].addLocation("projection");
		shaders[DEFAULT_SHADER].setUniformMat4fv("projection", projectionMatrix);
		shaders[DEFAULT_SHADER].addLocation("u_color");
		shaders[DEFAULT_SHADER].setUniform4f("u_color", 1f, 1f, 1f, 1f);
		shaders[DEFAULT_SHADER].addLocation("modelView");
		//load and set main shader
		shaders[MAIN_SHADER] = new ShaderProgram("./resources/shaders/main_shader.vs.glsl", "./resources/shaders/main_shader.fs.glsl", "main");
		shaders[MAIN_SHADER].addLocation("u_resolution");
		shaders[MAIN_SHADER].setUniform2f("u_resolution", WIDTH, HEIGHT);
		shaders[MAIN_SHADER].addLocation("u_colortexture");
		shaders[MAIN_SHADER].setUniform1i("u_colortexture", 0);
		shaders[MAIN_SHADER].addLocation("u_lightmap");
		shaders[MAIN_SHADER].setUniform1i("u_lightmap", 1);
		shaders[MAIN_SHADER].addLocation("projection");
		shaders[MAIN_SHADER].setUniformMat4fv("projection", projectionMatrix);
		shaders[MAIN_SHADER].addLocation("u_color");
		shaders[MAIN_SHADER].setUniform4f("u_color", 1f, 1f, 1f, 1f);
		shaders[MAIN_SHADER].addLocation("modelView");
		//load and set font outline shader
		shaders[FONT_SHADER] = new ShaderProgram("./resources/shaders/default_shader.vs.glsl", "./resources/shaders/font_shader.fs.glsl", "font");
		shaders[FONT_SHADER].addLocation("u_colortexture");
		shaders[FONT_SHADER].setUniform1i("u_colortexture", 0);
		shaders[FONT_SHADER].addLocation("projection");
		shaders[FONT_SHADER].setUniformMat4fv("projection", projectionMatrix);
		shaders[FONT_SHADER].addLocation("u_color");
		shaders[FONT_SHADER].setUniform4f("u_color", 1f, 1f, 1f, 1f);
		shaders[FONT_SHADER].addLocation("modelView");
		shaders[FONT_SHADER].addLocation("u_outline_color");
		shaders[FONT_SHADER].addLocation("u_isoutline");
		
		currentShaderIndex = DEFAULT_SHADER;
		currentShaderProg = shaders[DEFAULT_SHADER];
	}

	public void update(double dt) {

	}

	public void updateProjScreen() {
		projectionScreenMatrix = new Matrix4f();
		projectionScreenMatrix.identity();
		CamMath.ortho(0, ResolutionHandler.CURRENT_WIDTH, ResolutionHandler.CURRENT_HEIGHT, 0, 1, -1, projectionScreenMatrix);
	}

	public void pushMatrix() {
		matrixStack.push(modelViewMatrix);
		modelViewMatrix = new Matrix4f(modelViewMatrix);
	}

	public void popMatrix() {
		modelViewMatrix = matrixStack.pop();
	}

	public Matrix4f getModelViewMatrix() {
		return modelViewMatrix;
	}

	public void setShaderModelViewMatrix() {
		currentShaderProg.setUniformMat4f("modelView", modelViewMatrix);
	}

	public void setProjectionMatrix(int index) {
		if(index == SCENE_PROJECTION)
			currentShaderProg.setUniformMat4fv("projection", projectionMatrix);
		else if(index == SCREEN_PROJECTION)
			currentShaderProg.setUniformMat4fv("projection", projectionScreenMatrix);
	}

	public void setColor4f(float r, float g, float b, float a) {
		currentShaderProg.setUniform4fv("u_color", r, g, b, a);
	}

	public void setColor4i(int r, int g, int b, int a) {
		currentShaderProg.setUniform4fv("u_color", r / 255f, g / 255f, b / 255f, a / 255f);
	}

	public void useProgram() {
		currentShaderProg.Begin();
	}

	public void endProgram() {
		currentShaderProg.End();
	}

	public ShaderProgram getCurrentShaderProg() {
		return currentShaderProg;
	}

	/**
	 * @param index
	 *            - {@link ShaderProgramManager#DEFAULT_SHADER}
	 */
	public void setCurrentShaderProg(short index) {
		if(index < 0 || index >= NUM_SHADER || index == currentShaderIndex)
			return;
		currentShaderProg.End();
		currentShaderProg = shaders[index];
		currentShaderIndex = index;
	}

	/**
	 * @param index
	 *            - {@link ShaderProgramManager#DEFAULT_SHADER}
	 */
	public ShaderProgram getShaderProg(int index) {
		return shaders[index];
	}

	public Camera getCam() {
		return cam;
	}

	public float getCamX() {
		return cam.getX();
	}

	public float getCamY() {
		return cam.getY();
	}

	public short getCurrentIndex() {
		return currentShaderIndex;
	}
}
