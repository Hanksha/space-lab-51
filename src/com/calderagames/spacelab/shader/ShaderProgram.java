package com.calderagames.spacelab.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector4f;

import com.joml.matrix.Matrix4f;

public class ShaderProgram {

	// shader program
	private int shaderProgram;
	private String name;
	private HashMap<String, Integer> locations;
	private boolean inUse;

	public ShaderProgram(String vertexShaderPath, String fragmentShaderPath, String name) {
		this.name = name;
		createProgram(vertexShaderPath, fragmentShaderPath);

		locations = new HashMap<String, Integer>();
	}

	private void createProgram(String vertexShaderPath, String fragmentShaderPath) {
		// init shader program
		shaderProgram = glCreateProgram();
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		int pixelShader = glCreateShader(GL_FRAGMENT_SHADER);

		StringBuilder vertexShaderSource = new StringBuilder();
		StringBuilder fragmentShaderSource = new StringBuilder();

		// read source
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(vertexShaderPath)));
		} catch(FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = null;

		try {
			while((line = br.readLine()) != null) {
				vertexShaderSource.append(line);
				vertexShaderSource.append("\n");

			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fragmentShaderPath)));
		} catch(FileNotFoundException e1) {
			e1.printStackTrace();
		}

		line = null;

		try {
			while((line = br.readLine()) != null) {
				fragmentShaderSource.append(line);
				fragmentShaderSource.append("\n");

			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		// compile source
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);
		// System.err.println("Vertex Shader - " + name + ":\n" +
		// glGetShaderInfoLog(vertexShader, 1000));
		// check compile status
		if(glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
			JOptionPane.showMessageDialog(null, "Vertex Shader - " + name + ":\n" + glGetShaderInfoLog(vertexShader, 1000),
										  "Vertex shader wasn't able to be compiled correctly.\n", JOptionPane.ERROR_MESSAGE);
			System.err.println("Vertex shader wasn't able to be compiled correctly.\n");
		}
		else {
			// System.out.println("Vertex shader compiled correctly");
		}

		glShaderSource(pixelShader, fragmentShaderSource);
		glCompileShader(pixelShader);
		// System.err.println("Fragment Shader - " + name + ":\n" +
		// glGetShaderInfoLog(pixelShader, 1000));
		if(glGetShaderi(pixelShader, GL_COMPILE_STATUS) == GL_FALSE) {
			JOptionPane.showMessageDialog(null, "Fragment Shader - " + name + ":\n" + glGetShaderInfoLog(pixelShader, 1000),
										  "Fragment shader wasn't able to be compiled correctly.\n", JOptionPane.ERROR_MESSAGE);
			System.err.println("Fragment shader wasn't able to be compiled correctly.\n");
		}
		else {
			// System.out.println("Pixel shader compiled correctly");
		}

		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, pixelShader);

		glBindAttribLocation(shaderProgram, 0, "in_position");
		glBindAttribLocation(shaderProgram, 1, "in_texCoord");
		glBindAttribLocation(shaderProgram, 2, "in_color");

		glLinkProgram(shaderProgram);
		glValidateProgram(shaderProgram);
	}

	public void addLocation(String name) {
		locations.put(name, glGetUniformLocation(shaderProgram, name));
	}

	public void Begin() {
		if(inUse)
			return;
		glUseProgram(shaderProgram);
		inUse = true;
	}

	public void End() {
		if(!inUse)
			return;
		glUseProgram(0);
		inUse = false;
	}

	public int getShaderProgram() {
		return shaderProgram;
	}

	public void setUniform1i(String name, int v0) {
		Begin();
		glUniform1i(locations.get(name), v0);
		End();
	}

	public void setUniform1iv(String name, int v0) {
		glUniform1i(locations.get(name), v0);
	}

	public void setUniform2i(String name, int v0, int v1) {
		Begin();
		glUniform2i(locations.get(name), v0, v1);
		End();
	}

	public void setUniform1f(String name, float v0) {
		Begin();
		glUniform1f(locations.get(name), v0);
		End();
	}

	public void setUniform2f(String name, float v0, float v1) {
		Begin();
		glUniform2f(locations.get(name), v0, v1);
		End();
	}

	public void setUniform2fv(String name, float v0, float v1) {
		glUniform2f(locations.get(name), v0, v1);
	}

	public void setUniform3f(String name, float v0, float v1, float v2) {
		Begin();
		glUniform3f(locations.get(name), v0, v1, v2);
		End();
	}

	public void setUniform4f(String name, Vector4f v) {
		Begin();
		glUniform4f(locations.get(name), v.x, v.y, v.z, v.w);
		End();
	}

	public void setUniform4f(String name, float v0, float v1, float v2, float v3) {
		Begin();
		glUniform4f(locations.get(name), v0, v1, v2, v3);
		End();
	}

	public void setUniform4fv(String name, float v0, float v1, float v2, float v3) {
		glUniform4f(locations.get(name), v0, v1, v2, v3);
	}

	public void setUniformMat4fv(String name, Matrix4f mat) {
		Begin();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		mat.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(locations.get(name), false, buffer);
		End();
	}

	public void setUniformMat4f(String name, Matrix4f mat) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		mat.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(locations.get(name), false, buffer);
	}

	public void bindTexture(int texn, int target, int texture) {
		glActiveTexture(GL_TEXTURE0 + texn);
		glBindTexture(target, texture);
	}

	public void activeTexture(int n) {
		glActiveTexture(GL_TEXTURE0 + n);
	}
}
