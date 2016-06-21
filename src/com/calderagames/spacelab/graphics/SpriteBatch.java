package com.calderagames.spacelab.graphics;

import static com.calderagames.spacelab.util.ResolutionHandler.HEIGHT;
import static com.calderagames.spacelab.util.ResolutionHandler.WIDTH;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.geom.Rectangle;

import com.calderagames.spacelab.gamecontent.GameContent;

public class SpriteBatch {

	private class Job implements Comparable<Job> {
		public float x1, x2, x3, x4;
		public float y1, y2, y3, y4;
		public int z_order;
		public TextureRegion texReg;
		public Color color;

		public Job(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Color color, TextureRegion texReg, int z_order) {
			this.x1 = x1;
			this.x2 = x2;
			this.x3 = x3;
			this.x4 = x4;
			this.y1 = y1;
			this.y2 = y2;
			this.y3 = y3;
			this.y4 = y4;
			this.color = color;
			this.texReg = texReg;
			if(z_order == -1)
				this.z_order = Integer.MAX_VALUE;
			else
				this.z_order = z_order;
		}

		@Override
		public int compareTo(Job job) {
			if(job.z_order > z_order)
				return -1;
			else if(job.z_order < z_order)
				return 1;
			else
				return 0;
		}
	}

	private GameContent gc;

	private Camera defaultCam;
	private Camera cam;

	private int currentBind;

	private Color color;
	private float[] colorVert;
	private float x1, y1, x2, y2, x3, y3, x4, y4;
	private float[] posVert;

	//
	private int vaoID;
	private int vertexID;
	private int textureID;
	private int colorID;

	//
	private int size;
	private FloatBuffer bufferVertex;
	private FloatBuffer bufferTexture;
	private FloatBuffer bufferColor;

	//
	private int counter;
	private ArrayList<Job> jobs;

	public SpriteBatch(GameContent gc, int size) {
		this.gc = gc;
		this.size = size;

		defaultCam = new Camera();

		jobs = new ArrayList<>();

		color = new Color(1f, 1f, 1f);
		posVert = new float[12];
		colorVert = new float[24];

		bufferVertex = BufferUtils.createFloatBuffer(2 * 6 * size);
		bufferTexture = BufferUtils.createFloatBuffer(2 * 6 * size);
		bufferColor = BufferUtils.createFloatBuffer(4 * 6 * size);
		counter = 0;

		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);

		// VBO
		vertexID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferVertex, GL15.GL_DYNAMIC_DRAW);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		textureID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferTexture, GL15.GL_DYNAMIC_DRAW);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		colorID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferColor, GL15.GL_DYNAMIC_DRAW);
		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(0);
	}

	public void begin(int texID, Camera cam) {
		if(cam == null)
			this.cam = defaultCam;
		else
			this.cam = cam;

		currentBind = texID;

		gc.getSPM().setShaderModelViewMatrix();
	}

	public void begin(int texID) {
		begin(texID, cam);
	}

	public void begin(String texName, Camera cam) {
		begin(gc.getRM().getTexture(texName).getTextureID(), cam);
	}

	public void begin(String texName) {
		begin(texName, null);
	}

	public void next(String texName) {
		next(gc.getRM().getTexture(texName).getTextureID());
	}

	public void next(int texID) {
		if(currentBind != texID)
			render();

		currentBind = texID;
	}

	public void end() {
		render();
		setColor(1f, 1f, 1f, 1f);
	}

	public void render() {
		if(counter == 0)
			return;

		Collections.sort(jobs);

		for(Job job : jobs) {
			addBuffer(job.x1, job.y1, job.x2, job.y2, job.x3, job.y3, job.x4, job.y4, job.color, job.texReg);
		}

		gc.getTexBinder().bindTexture(currentBind);

		flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexID);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, bufferVertex);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureID);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, bufferTexture);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorID);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, bufferColor);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(vaoID);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6 * counter);
		GL30.glBindVertexArray(0);

		clear();
	}

	private void flip() {
		bufferVertex.flip();
		bufferTexture.flip();
		bufferColor.flip();
	}

	private void clear() {
		counter = 0;
		bufferVertex.clear();
		bufferTexture.clear();
		bufferColor.clear();
		jobs.clear();
	}

	public void draw(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, TextureRegion texRegion, int z_order) {
		if(counter == size)
			render();

		//check if the vertices are out of the screen
		if((cam.getX() + x1 < 0 || cam.getX() + x1 > WIDTH) && (cam.getY() + y1 < 0 || cam.getY() + y1 > HEIGHT) && (cam.getX() + x2 < 0 || cam.getX() + x2 > WIDTH) && (cam.getY() + y2 < 0 || cam.getY() + y2 > HEIGHT) && (cam.getX() + x3 < 0 || cam.getX() + x3 > WIDTH) && (cam.getY() + y3 < 0 || cam.getY() + y3 > HEIGHT) && (cam.getX() + x4 < 0 || cam.getX() + x4 > WIDTH) && (cam.getY() + y4 < 0 || cam.getY() + y4 > HEIGHT))
			return;

		jobs.add(new Job(cam.getX() + x1, cam.getY() + y1, cam.getX() + x2, cam.getY() + y2, cam.getX() + x3, cam.getY() + y3, cam.getX() + x4, cam.getY() + y4, new Color(color), texRegion,
						 z_order));

		counter++;
	}

	public void addBuffer(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Color color, TextureRegion texRegion) {
		posVert[0] = (int) x1;
		posVert[1] = (int) y1;

		posVert[2] = (int) x2;
		posVert[3] = (int) y2;

		posVert[4] = (int) x4;
		posVert[5] = (int) y4;

		posVert[6] = (int) x2;
		posVert[7] = (int) y2;

		posVert[8] = (int) x3;
		posVert[9] = (int) y3;

		posVert[10] = (int) x4;
		posVert[11] = (int) y4;

		bufferVertex.put(posVert);

		bufferTexture.put(texRegion.getRegion());

		colorVert[0] = color.r;
		colorVert[1] = color.g;
		colorVert[2] = color.b;
		colorVert[3] = color.a;

		colorVert[4] = color.r;
		colorVert[5] = color.g;
		colorVert[6] = color.b;
		colorVert[7] = color.a;

		colorVert[8] = color.r;
		colorVert[9] = color.g;
		colorVert[10] = color.b;
		colorVert[11] = color.a;

		colorVert[12] = color.r;
		colorVert[13] = color.g;
		colorVert[14] = color.b;
		colorVert[15] = color.a;

		colorVert[16] = color.r;
		colorVert[17] = color.g;
		colorVert[18] = color.b;
		colorVert[19] = color.a;

		colorVert[20] = color.r;
		colorVert[21] = color.g;
		colorVert[22] = color.b;
		colorVert[23] = color.a;

		bufferColor.put(colorVert);
	}

	public void draw(TextureRegion texRegion, float x, float y, float width, float height, float scaleX, float scaleY, float angle, boolean flipX,
					 boolean flipY, int z_order) {
		width = (flipX ? -1 : 1) * width * scaleX;
		height = (flipY ? -1 : 1) * height * scaleY;

		if(angle != 0) {
			final float cos = (float) Math.cos(Math.toRadians(angle));
			final float sin = (float) Math.sin(Math.toRadians(angle));

			x1 = (cos * (-width / 2) - sin * (-height / 2)) + width / 2 + x;
			y1 = (sin * (-width / 2) + cos * (-height / 2)) + height / 2 + y;

			x2 = (cos * (width / 2) - sin * (-height / 2)) + width / 2 + x;
			y2 = (sin * (width / 2) + cos * (-height / 2)) + height / 2 + y;

			x3 = (cos * (width / 2) - sin * (height / 2)) + width / 2 + x;
			y3 = (sin * (width / 2) + cos * (height / 2)) + height / 2 + y;

			x4 = (cos * (-width / 2) - sin * (height / 2)) + width / 2 + x;
			y4 = (sin * (-width / 2) + cos * (height / 2)) + height / 2 + y;
		}
		else {
			x1 = x;
			y1 = y;

			x2 = x + width;
			y2 = y;

			x3 = x + width;
			y3 = y + height;

			x4 = x;
			y4 = y + height;
		}

		draw(x1 + (flipX ? -width : 0), y1 + (flipY ? -height : 0), x2 + (flipX ? -width : 0), y2 + (flipY ? -height : 0), x3 + (flipX ? -width : 0),
			 y3 + (flipY ? -height : 0), x4 + (flipX ? -width : 0), y4 + (flipY ? -height : 0), texRegion, z_order);
	}

	public void draw(Sprite sprite, float x, float y, float scaleX, float scaleY, float angle, boolean flipX, boolean flipY, int z_order) {
		draw(sprite.texRegion, x, y, sprite.getWidth(), sprite.getHeight(), scaleX, scaleY, angle, flipX, flipY, z_order);
	}

	public void draw(Sprite sprite, float x, float y, float scaleX, float scaleY, float angle, boolean flipX, int z_order) {
		draw(sprite, x, y, scaleX, scaleY, angle, flipX, false, z_order);
	}

	public void draw(Sprite sprite, float x, float y, float scale, boolean flip, int z_order) {
		draw(sprite, x, y, scale, scale, 0, flip, z_order);
	}

	public void draw(Sprite sprite, float x, float y, float scale, float angle, boolean flip, int z_order) {
		draw(sprite, x, y, scale, scale, angle, flip, z_order);
	}

	public void draw(Sprite sprite, float x, float y, boolean flip, int z_order) {
		draw(sprite, x, y, 1, 1, 0, flip, z_order);
	}

	public void draw(Sprite sprite, float x, float y, int z_order) {
		draw(sprite, x, y, 1, 1, 0, false, z_order);
	}
	
	public void draw(float centerX, float centerY, Sprite sprite, int z_order) {
		draw(sprite, centerX - sprite.getWidth() / 2, centerY - sprite.getHeight() / 2, 1, 1, 0, false, z_order);
	}

	public void draw(Rectangle rect, TextureRegion texRegion, int z_order) {
		draw(texRegion, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), 1, 1, 0, false, false, z_order);
	}
	
	public void setColor(float r, float g, float b, float a) {
		color.setColor(r, g, b, a);
	}

	public void setColor(Color color) {
		this.color.setColor(color);
	}

	public void setCam(Camera cam) {
		if(cam == null)
			this.cam = defaultCam;
		else
			this.cam = cam;
	}

	public void dispose() {
		GL15.glDeleteBuffers(vertexID);
		GL15.glDeleteBuffers(textureID);
		GL15.glDeleteBuffers(colorID);
		GL30.glDeleteVertexArrays(vaoID);
	}
}
