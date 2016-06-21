package com.calderagames.spacelab.gamecontent;

import com.calderagames.spacelab.TremblorSystem;
import com.calderagames.spacelab.audio.AudioSystem;
import com.calderagames.spacelab.gamecontent.ResourceManager;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.graphics.TextureBinder;
import com.calderagames.spacelab.path.Chebyshev;
import com.calderagames.spacelab.path.PathFinder;
import com.calderagames.spacelab.shader.ShaderProgramManager;
import com.calderagames.spacelab.tiles.TileSetManager;

public class GameContent {
	private ShaderProgramManager SPM;
	private TextureBinder TB;
	private SpriteBatch spriteBatch;
	private TremblorSystem TS;
	private ResourceManager RM;
	private TileSetManager TSM;
	private AudioSystem AS;
	private PathFinder pathFinder;
	
	public GameContent() {
		TSM = new TileSetManager(this, 1);
		RM = new ResourceManager(this);
		SPM = new ShaderProgramManager(this);
		AS = new AudioSystem(this);
		TB = new TextureBinder(RM);
		spriteBatch = new SpriteBatch(this, 2000);
		pathFinder = new PathFinder(new Chebyshev());
	}
	
	/**
	 * @return Resource Manager
	 */
	public ResourceManager getRM() {
		return RM;
	}
	
	/**
	 * @return Shader Manager
	 */
	public ShaderProgramManager getSPM() {
		return SPM;
	}
	
	/**
	 * @return Audio System
	 */
	public AudioSystem getAS() {
		return AS;
	}
	
	/**
	 * @return Tremblor System
	 */
	public TremblorSystem getTS() {
		return TS;
	}
	
	/**
	 * @return Tile Set Manager
	 */
	public TileSetManager getTSM() {
		return TSM;
	}
	
	/**
	 * @return Texture Binder
	 */
	public TextureBinder getTexBinder() {
		return TB;
	}
	
	public PathFinder getPathFinder() {
		return pathFinder;
	}
	
	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}
	
	public void dispose() {
		RM.dispose();
		AS.dispose();
		spriteBatch.dispose();
	}
}
