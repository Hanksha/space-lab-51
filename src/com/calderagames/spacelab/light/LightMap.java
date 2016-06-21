package com.calderagames.spacelab.light;

import com.calderagames.spacelab.entities.PlayableEntity;
import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.graphics.FrameBufferObject;
import com.calderagames.spacelab.graphics.SpriteBatch;
import com.calderagames.spacelab.util.ResolutionHandler;

public class LightMap {

	private GameContent gc;
	private Map map;
	private FrameBufferObject fbo;
	
	public LightMap(GameContent gc, Map map) {
		this.gc = gc;
		this.map = map;
		
		fbo = new FrameBufferObject(ResolutionHandler.WIDTH, ResolutionHandler.HEIGHT);
	}

	
	public void render(SpriteBatch sb, PlayableEntity player) {
		fbo.setClearColor(0.4f, 0.4f, 0.4f);
		sb.begin("texatlas", gc.getSPM().getCam());
		fbo.Begin(gc.getSPM(), ResolutionHandler.WIDTH, ResolutionHandler.HEIGHT);
		
		map.getTileMap().render(sb, map.getTileMap().getCollisionLayerIndex() - 1, false);
		
		player.renderLight(sb);
		
		sb.end();
		
		fbo.End();
		
	}
	
	public int getTexture() {
		return fbo.getTexture();
	}


	public void dispose() {
		fbo.dispose();
	}
}
