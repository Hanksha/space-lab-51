package com.calderagames.spacelab.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.graphics.Sprite;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.Texture;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.shader.ShaderProgramManager;

/**
 * A TrueType font implementation for Slick updated for opengl core profile 3.2
 * by @author Vivien Jovet (Hanksha)
 * 
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias4444)
 * @author Kevin Glass (kevglass)
 * @author Peter Korzuszek (genail)
 */
public class TrueTypeFont {
	/** Array that holds necessary information about the font characters */
	private TextureRegion[] charArray = new TextureRegion[256];

	/** Map of user defined font characters (Character <-> IntObject) */
	private Map<Character, TextureRegion> customChars = new HashMap<Character, TextureRegion>();

	/** Boolean flag on whether AntiAliasing is enabled or not */
	private boolean antiAlias;

	/** Color of the outline, no outline if null */
	private com.calderagames.spacelab.graphics.Color outline;

	/** Font's size */
	private int fontSize = 0;

	/** Font's height */
	private int fontHeight = 0;

	/** Texture used to cache the font 0-255 characters */
	private Texture fontTexture;

	/** Default font texture width */
	private int textureWidth = 512;

	/** Default font texture height */
	private int textureHeight = 512;

	/** A reference to Java's AWT Font that we create our font texture from */
	private Font font;

	/** The font metrics for our Java AWT font */
	private FontMetrics fontMetrics;

	private GameContent gc;

	/**
	 * Constructor for the TrueTypeFont class Pass in the preloaded standard
	 * Java TrueType font, and whether you want it to be cached with
	 * AntiAliasing applied.
	 * 
	 * @param font
	 *            Standard Java AWT font
	 * @param antiAlias
	 *            Whether or not to apply AntiAliasing to the cached font
	 * @param additionalChars
	 *            Characters of font that will be used in addition of first 256
	 *            (by unicode).
	 */
	public TrueTypeFont(GameContent gc, java.awt.Font font, boolean antiAlias, com.calderagames.spacelab.graphics.Color outline, char[] additionalChars) {
		this.gc = gc;
		this.font = font;
		this.fontSize = font.getSize();
		this.antiAlias = antiAlias;
		this.outline = outline;

		createSet(additionalChars);
	}

	/**
	 * Constructor for the TrueTypeFont class Pass in the preloaded standard
	 * Java TrueType font, and whether you want it to be cached with
	 * AntiAliasing applied.
	 * 
	 * @param font
	 *            Standard Java AWT font
	 * @param antiAlias
	 *            Whether or not to apply AntiAliasing to the cached font
	 */
	public TrueTypeFont(GameContent gc, java.awt.Font font, boolean antiAlias, com.calderagames.spacelab.graphics.Color outline) {
		this(gc, font, antiAlias, outline, null);
	}

	/**
	 * Create a standard Java2D BufferedImage of the given character
	 * 
	 * @param ch
	 *            The character to create a BufferedImage for
	 * 
	 * @return A BufferedImage containing the character
	 */
	private BufferedImage getFontImage(char ch) {
		// Create a temporary image to extract the character's size
		BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		if(antiAlias == true) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		fontMetrics = g.getFontMetrics();
		int charwidth = fontMetrics.charWidth(ch);
		if(charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fontMetrics.getHeight();
		if(charheight <= 0) {
			charheight = fontSize;
		}

		// Create another image holding the character we are creating
		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth + 1, charheight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if(antiAlias == true) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		gt.setFont(font);
		int charx = 0;
		int chary = 0;

		gt.setColor(Color.WHITE);
		gt.drawString(String.valueOf(ch), (charx), (chary) + fontMetrics.getAscent());

		return fontImage;

	}

	/**
	 * Create and store the font
	 * 
	 * @param customCharsArray
	 *            Characters that should be also added to the cache.
	 */
	private void createSet(char[] customCharsArray) {
		// If there are custom chars then I expand the font texture twice
		if(customCharsArray != null && customCharsArray.length > 0) {
			textureWidth *= 2;
		}

		//In any case this should be done in other way. Texture with size
		//512x512
		//can maintain only 256 characters with resolution of 32x32. The
		//texture
		//size should be calculated dynamicaly by looking at character sizes.

		try {

			BufferedImage imgTemp = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) imgTemp.getGraphics();

			g.setColor(new Color(255, 255, 255, 0));
			g.fillRect(0, 0, textureWidth, textureHeight);

			int rowHeight = 0;
			int positionX = 0;
			int positionY = 0;

			int customCharsLength = (customCharsArray != null) ? customCharsArray.length : 0;

			for(int i = 0; i < 256 + customCharsLength; i++) {

				// get 0-255 characters and then custom characters
				char ch = (i < 256) ? (char) i : customCharsArray[i - 256];

				BufferedImage fontImage = getFontImage(ch);

				/*newIntObject.width = fontImage.getWidth();
				newIntObject.height = fontImage.getHeight();*/

				if(positionX + fontImage.getWidth() >= textureWidth) {
					positionX = 0;
					positionY += rowHeight;
					rowHeight = 0;
				}

				TextureRegion newTexReg = new TextureRegion(textureWidth, textureHeight, positionX, positionY, fontImage.getWidth(), fontImage.getHeight());
				/*newIntObject.storedX = positionX;
				newIntObject.storedY = positionY;*/

				if(fontImage.getHeight() > fontHeight) {
					fontHeight = fontImage.getHeight();
				}

				if(fontImage.getHeight() > rowHeight) {
					rowHeight = fontImage.getHeight();
				}

				// Draw it here
				g.drawImage(fontImage, positionX, positionY, null);

				positionX += fontImage.getWidth();

				if(i < 256) { //standard characters
					charArray[i] = newTexReg;
				}
				else { //custom characters
					customChars.put(new Character(ch), newTexReg);
				}

				fontImage = null;
			}

			fontTexture = new Texture(imgTemp, antiAlias ? GL11.GL_LINEAR : GL11.GL_NEAREST, GL13.GL_TEXTURE0);

		} catch(IOException e) {
			System.err.println("Failed to create font.");
			e.printStackTrace();
		}
	}

	/**
	 * Get the width of a given String
	 * 
	 * @param whatchars
	 *            The characters to get the width of
	 * 
	 * @return The width of the characters
	 */
	public int getWidth(String whatchars) {
		int totalwidth = 0;
		TextureRegion texReg = null;
		int currentChar = 0;
		for(int i = 0; i < whatchars.length(); i++) {
			currentChar = whatchars.charAt(i);
			if(currentChar < 256) {
				texReg = charArray[currentChar];
			}
			else {
				texReg = customChars.get(new Character((char) currentChar));
			}

			if(texReg != null)
				totalwidth += texReg.getWidth() - 1;
		}
		return totalwidth;
	}

	/**
	 * Get the font's height
	 * 
	 * @return The height of the font
	 */
	public int getHeight() {
		return fontHeight;
	}

	/**
	 * Get the height of a String
	 * 
	 * @return The height of a given string
	 */
	public int getHeight(String HeightString) {
		return fontHeight;
	}

	/**
	 * Get the font's line height
	 * 
	 * @return The line height of the font
	 */
	public int getLineHeight() {
		return fontHeight;
	}

	/**
	 * Draw a string
	 * 
	 * @param x
	 *            The x position to draw the string
	 * @param y
	 *            The y position to draw the string
	 * @param whatchars
	 *            The string to draw
	 * @param color
	 *            The color to draw the text
	 */
	public void drawString(SpriteBatch sb, float x, float y, String whatchars, com.calderagames.spacelab.graphics.Color color) {
		drawString(sb, x, y, whatchars, color, 0, whatchars.length() - 1);
	}

	/**
	 * @see Font#drawString(float, float, String, org.newdawn.slick.Color, int,
	 *      int)
	 */
	public void drawString(SpriteBatch sb, float x, float y, String whatchars, com.calderagames.spacelab.graphics.Color color, int startIndex, int endIndex) {

		if(gc.getSPM().getCurrentIndex() == ShaderProgramManager.FONT_SHADER) {
			gc.getSPM().getCurrentShaderProg().setUniform1iv("u_isoutline", outline != null ? 1 : 0);
			if(outline != null)
				gc.getSPM().getCurrentShaderProg().setUniform4fv("u_outline_color", outline.r, outline.g, outline.b, 1f);
		}

		sb.begin(fontTexture.getTextureID());

		sb.setColor(color);

		TextureRegion texReg = null;
		Sprite sprite = new Sprite();
		int charCurrent;

		int totalwidth = 0;
		for(int i = 0; i < whatchars.length(); i++) {
			charCurrent = whatchars.charAt(i);
			
			if(charCurrent == '$') {
				y += getHeight() + 2;
				totalwidth = 0;
				continue;
			}
			
			if(charCurrent < 256) {
				texReg = charArray[charCurrent];
			}
			else {
				texReg = customChars.get(new Character((char) charCurrent));
			}

			sprite.texRegion = texReg;
			sprite.setWidth(texReg.getWidth());
			sprite.setHeight(texReg.getHeight());
			
			if(texReg != null) {
				if((i >= startIndex) || (i <= endIndex))
					sb.draw(sprite, x + totalwidth, y, -1);

				totalwidth += texReg.getWidth() - 1;
			}
		}

		sb.end();
	}

	/**
	 * Draw a string
	 * 
	 * @param x
	 *            The x position to draw the string
	 * @param y
	 *            The y position to draw the string
	 * @param whatchars
	 *            The string to draw
	 */
	public void drawString(SpriteBatch sb, float x, float y, String whatchars) {
		drawString(sb, x, y, whatchars, com.calderagames.spacelab.graphics.Color.WHITE);
	}
	
	public void drawStringCenter(SpriteBatch sb, float centerX, float centerY, String whatchars) {
		drawString(sb, centerX - getWidth(whatchars) / 2, centerY - getHeight() / 2, whatchars, com.calderagames.spacelab.graphics.Color.WHITE);
	}

}
