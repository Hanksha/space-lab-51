package com.calderagames.spacelab.gamecontent;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.calderagames.spacelab.font.TrueTypeFont;
import com.calderagames.spacelab.graphics.Color;
import com.calderagames.spacelab.graphics.Texture;
import com.calderagames.spacelab.graphics.TextureRegion;
import com.calderagames.spacelab.util.DataUtil;
import com.calderagames.spacelab.util.ErrorLog;

public class ResourceManager {

	private GameContent gc;

	private HashMap<String, Texture> textures;
	private HashMap<Integer, TrueTypeFont> titleFont;
	private HashMap<Integer, TrueTypeFont> mithrilFont;
	private HashMap<String, HashMap<Integer, TrueTypeFont>> fonts;
	private Document optionsDoc;
	public Element tilesetMeta;
	
	public static TextureRegion whitePixel;

	public ResourceManager(GameContent gc) {
		this.gc = gc;
		loadContent();
	}

	public void loadContent() {
		loadAllTextures();
		loadAllTrueTypeFont();
		loadAllTexts();
		loadTileSet();
		
		whitePixel = new TextureRegion(getTexture("texatlas"), 512, 0, 1, 1);
	}

	private void loadTileSet() {
		HashMap<String, Integer> tileTypes = new HashMap<String, Integer>();
		tileTypes.put("normal", 0);
		tileTypes.put("0", 0);
		tileTypes.put("block", 1);

		HashMap<String, Integer> tileNatures = new HashMap<String, Integer>();
		tileNatures.put("none", 0);
		tileNatures.put("0", 0);

		Document doc = DataUtil.parseXMLFile("./resources/sprites/space lab_meta.xml");
		Element tilesetElem = (Element) doc.getElementsByTagName("TileSet").item(0);
		tilesetMeta = (Element) tilesetElem.getElementsByTagName("MetaData").item(0);
		NodeList tileList = tilesetMeta.getElementsByTagName("Tile");

		int numId = tileList.getLength();

		int[] meta_type = new int[numId];
		int[] meta_nature = new int[numId];

		for(int i = 0; i < numId; i++) {
			Element tileElem = (Element) tileList.item(i);
			int type = tileTypes.get(tileElem.getAttribute("type"));
			int nature = tileNatures.get(tileElem.getAttribute("nature"));

			meta_type[i] = type;
			meta_nature[i] = nature;
		}
		
		gc.getTSM().addTileSet(0, gc.getTSM().createTileSet(getTexture("texatlas"), meta_type, meta_nature, 16, 16, 0, 0, 0));
	}

	public void loadOptions() {
	
	}

	public void loadAllTexts() {
		
	}

	private void loadAllTextures() {
		textures = new HashMap<String, Texture>();
		textures.put("texatlas", loadTexture("./resources/sprites/texture-atlas.png", GL11.GL_NEAREST, GL13.GL_TEXTURE0));
		textures.put("backgroundtitle", loadTexture("./resources/sprites/background-title.png", GL11.GL_NEAREST, GL13.GL_TEXTURE0));
	}
	
	private Texture loadTexture(String filepath, int filter, int texUnit) {
		return new Texture(filepath, filter, texUnit);
	}

	private void loadAllTrueTypeFont() {
		//special char
		//button A : ¢
		//button B : £
		//button X : ¤
		//button Y : ¥

		fonts = new HashMap<String, HashMap<Integer, TrueTypeFont>>();
		mithrilFont = new HashMap<Integer, TrueTypeFont>();
		titleFont = new HashMap<Integer, TrueTypeFont>();

		fonts.put("mithril", mithrilFont);
		fonts.put("title", titleFont);
		mithrilFont.put(32, loadTrueTypeFont("./resources/fonts/Mithril.ttf", 32, Font.PLAIN, true, new Color(0f, 0f, 0f, 1f)));
		mithrilFont.put(16, loadTrueTypeFont("./resources/fonts/Mithril.ttf", 16, Font.PLAIN, true, new Color(0f, 0f, 0f, 1f)));
		mithrilFont.put(12, loadTrueTypeFont("./resources/fonts/Mithril.ttf", 12, Font.PLAIN, true, new Color(0f, 0f, 0f, 1f)));
	}

	private TrueTypeFont loadTrueTypeFont(String filePath, float size, int style, boolean antiAlias, Color outline) {
		try {
			FileInputStream fs = new FileInputStream(filePath);
			Font javaFont = Font.createFont(Font.TRUETYPE_FONT, fs);
			javaFont = javaFont.deriveFont(size);
			javaFont = javaFont.deriveFont(style);

			fs.close();
			return new TrueTypeFont(gc, javaFont, antiAlias, outline);

		} catch(FontFormatException | IOException e) {
			ErrorLog.writeToErrorLog(e.getStackTrace());
			e.printStackTrace();
		}

		return null;
	}

	public Texture getTexture(String key) {
		return textures.get(key);
	}

	public TrueTypeFont getFont(String family, int key) {
		return fonts.get(family).get(key);
	}

	public Document getOptionsDoc() {
		return optionsDoc;
	}

	public void dispose() {
		titleFont.clear();

		for(String key : textures.keySet()) {
			textures.get(key).dispose();
		}
	}
}
