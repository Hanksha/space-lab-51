package com.calderagames.spacelab.audio;

import com.calderagames.spacelab.entities.BattleManager;

public class MusicManager {

	private AudioSystem as;
	private String currMusic = "main-theme1";
	private BattleManager bm;
	
	public MusicManager(AudioSystem as, BattleManager bm) {
		this.as = as;
		this.bm = bm;
	}
	
	public void update() {
		
		
		if(bm.isBattle() && !currMusic.equals("battle-theme")) {
			currMusic = "battle-theme";
			as.playMusic(currMusic, true);
		}
		else if(!bm.isBattle() && currMusic.equals("battle-theme")) {
			as.stopMusic();
			currMusic = "main-theme1";
		}
		else {
			if(!as.isMusicPlaying()) {
				if(currMusic.equals("main-theme1")) {
					currMusic = "main-theme2";
					as.playMusic(currMusic, false);
				}
				else if(currMusic.equals("main-theme2")) {
					currMusic = "main-theme1";
					as.playMusic(currMusic, false);
				}
			}
		}
	}
}
