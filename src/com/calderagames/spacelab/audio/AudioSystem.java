package com.calderagames.spacelab.audio;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector2f;

import static org.lwjgl.openal.AL10.*;

import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.OggDecoder;

import com.calderagames.spacelab.gamecontent.GameContent;
import com.calderagames.spacelab.util.ResolutionHandler;

public class AudioSystem {

	//array list of all the sound data
	private HashMap<String, IntBuffer> buffers;

	//array list of all the sources
	private ArrayList<SoundEffect> soundEffects;
	private IntBuffer currMusicBuffer;
	private boolean musicPlaying;

	private Vector2f playerPos;

	private Random rand;

	private final int MAXDIST = 1080;

	//default values
	public float generalVol = 1f;
	public float soundEffectVol = 1f;
	public float musicVol = 1f;

	public AudioSystem(GameContent gc) {
		buffers = new HashMap<String, IntBuffer>();
		soundEffects = new ArrayList<SoundEffect>();
		
		loadAllBuffers();
		setListenerValues();
		
		rand = new Random();
		
		playerPos = new Vector2f(0f, 0f);
	}

	public void update(float x, float y) {
		playerPos.set(x, y);

		for(int i = 0; i < soundEffects.size(); i++) {
			setGain(soundEffects.get(i));
			if((alGetSourcei(soundEffects.get(i).getSource(), AL_SOURCE_STATE)) == AL_STOPPED) {
				soundEffects.get(i).dispose();
				soundEffects.remove(i);
			}
		}
		
		if(musicPlaying && alGetSourcei(currMusicBuffer.get(0), AL_SOURCE_STATE) == AL_STOPPED) {
			musicPlaying = false;
		}
	}

	private void loadAllBuffers() {
		//music
		addBuffer("./resources/audio/music/main-theme1.ogg", "main-theme1");
		addBuffer("./resources/audio/music/main-theme2.ogg", "main-theme2");
		addBuffer("./resources/audio/music/battle-theme.ogg", "battle-theme");
		
		//sound effect
		addBuffer("./resources/audio/fx/machine-gun-shot.ogg", "machine-gun-shot");
		addBuffer("./resources/audio/fx/gun-shot.ogg", "gun-shot");
		addBuffer("./resources/audio/fx/medigel-splash.ogg", "medigel-splash");
		addBuffer("./resources/audio/fx/footstep-1.ogg", "footstep-1");
		addBuffer("./resources/audio/fx/bite.ogg", "bite");
		addBuffer("./resources/audio/fx/beep.ogg", "beep");
	}

	private void addBuffer(String path, String id) {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		alGenBuffers(buffer);
		
		OggData oggFile = null;
		OggDecoder decoder = new OggDecoder();

		try {
			oggFile = decoder.getData(new FileInputStream(path));
		} catch(IOException e) {
			e.printStackTrace();
		}
		alBufferData(buffer.get(0), AL_FORMAT_STEREO16, oggFile.data, oggFile.rate);
		
		buffers.put(id, buffer);
	}

	private IntBuffer addSource(boolean fx, IntBuffer buffer, boolean loop, float pitch, float gain, float nativeGain, float x, float y) {
		IntBuffer source = BufferUtils.createIntBuffer(1);
		alGenSources(source);

		nativeGain *= soundEffectVol;
		nativeGain *= generalVol;

		alSourcei(source.get(0), AL_BUFFER, buffer.get(0));
		alSourcef(source.get(0), AL_PITCH, pitch);
		alSourcef(source.get(0), AL_GAIN, gain);
		alSource3f(source.get(0), AL_VELOCITY, 0f, 0f, 0f);
		alSource3f(source.get(0), AL_POSITION, 0f, 0f, 0f);
		alSourcei(source.get(0), AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
		
		if(fx)
			soundEffects.add(new SoundEffect(source, nativeGain, x, y));

		alSourcePlay(source);
		
		return source;
	}

	public void playMusic(String id, boolean loop) {
		if(!buffers.containsKey(id))
			return;
		
		if(musicPlaying)
			stopMusic();
		
		musicPlaying = true;
		currMusicBuffer = addSource(false, buffers.get(id), true, 1f, 0.5f, 0.5f, ResolutionHandler.WIDTH / 2, ResolutionHandler.HEIGHT / 2);
	}
	
	public void stopMusic() {
		if(!musicPlaying)
			return;
		
		AL10.alSourceStop(currMusicBuffer);
		AL10.alDeleteSources(currMusicBuffer);
		
		musicPlaying = false;
	}
	
	public boolean isMusicPlaying() {
		return musicPlaying;
	}
	
	public void playSoundEffect(String id, float x, float y) {
		if(!buffers.containsKey(id))
			return;
		
		float nativeGain = 1f;

		float gain = 1f;
		gain -= calculateGainDec(gain, x, y);
		if(gain < 0f) {
			gain = 0f;
		}
		addSource(true, buffers.get(id), false, 1f, gain, nativeGain, x, y);
	}

	public void playSoundEffect(String id, float pitch, float gain, float x, float y) {
		if(!buffers.containsKey(id))
			return;
		
		float nativeGain = gain;

		gain -= calculateGainDec(gain, x, y);
		if(gain < 0f) {
			gain = 0f;
		}

		addSource(true, buffers.get(id), false, pitch, gain, nativeGain, x, y);
	}

	/**
	 * Play the sound at the player's position, useful for UI related sounds
	 * 
	 * @param pitch
	 * @param gain
	 * @param id
	 */
	public void playSoundEffect(float pitch, float gain, String id) {
		if(!buffers.containsKey(id))
			return;
		playSoundEffect(id, pitch, gain, playerPos.getX(), playerPos.getY());
	}

	public void playSoundEffect(String id, float pitch, float gain, float x, float y, boolean loop) {
		if(!buffers.containsKey(id))
			return;
		float nativeGain = gain;

		gain -= calculateGainDec(gain, x, y);
		if(gain < 0f) {
			gain = 0f;
		}
		addSource(true, buffers.get(id), loop, pitch, gain, nativeGain, x, y);
	}

	private float calculateGainDec(float gain, float x, float y) {
		float distanceX = Math.abs(playerPos.x - x);
		float distanceY = Math.abs(playerPos.y - y);
		float distance = (float) Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));

		if(distance >= MAXDIST)
			return 1f;

		float dec = distance / MAXDIST;

		return dec;
	}

	private void setGain(SoundEffect se) {
		float gain = se.getNativeGain();

		gain -= calculateGainDec(gain, se.getX(), se.getY());
		if(gain < 0f) {
			gain = 0f;
		}

		se.setGain(gain);
	}

	public float getRandom(float min, float max) {
		return rand.nextFloat() * (max - min) + min;
	}

	public void setListenerValues() {
		alListener3f(AL_POSITION, 0f, 0f, 0f);
		alListener3f(AL_VELOCITY, 0f, 0f, 0f);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(6);
		buffer.put(new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f });
		buffer.flip();
		alListener(AL_ORIENTATION, buffer);
	}

	public void stopAllSound() {
		for(int i = 0; i < soundEffects.size(); i++) {
			soundEffects.get(i).dispose();
			soundEffects.remove(i);
		}
	}

	public void dispose() {
		stopMusic();
			
		for(int i = 0; i < soundEffects.size(); i++) {
			soundEffects.get(i).dispose();
		}
		
		Iterator<IntBuffer> iter = buffers.values().iterator();
		
		while(iter.hasNext()) {
			alDeleteBuffers(iter.next());
		}
	
		soundEffects.clear();
		buffers.clear();
	}
}
