package com.calderagames.spacelab;

import java.util.ArrayList;
import java.util.Random;

import com.calderagames.spacelab.gamecontent.GameContent;

public class TremblorSystem {
	private ArrayList<Tremblor> tremblors;
	private Random rand;
	private int distortion;

	public TremblorSystem(GameContent gc) {
	}

	public void init() {
		rand = new Random();
		tremblors = new ArrayList<Tremblor>();
	}

	public void update(float centerX, float centerY) {

		if(isEmpty())
			return;

		for(Tremblor t : tremblors)
			t.update(centerX, centerY);

		sort();

		distortion = rand.nextInt((int) (tremblors.get(0).getIntensity() + 1));

		for(int i = 0; i < tremblors.size(); i++) {
			if(tremblors.get(i).isOver()) {
				tremblors.remove(i);
			}
		}
	}

	public void addTremblor(int force, float x, float y, int delay) {
		tremblors.add(new Tremblor(force, delay, x, y));
	}

	// sort the arraylist by level of intensity
	private void sort() {
		if(tremblors.isEmpty())
			return;
		float currentValue;
		ArrayList<Tremblor> temp = new ArrayList<Tremblor>();

		for(int i = 0; i < tremblors.size(); i++) {
			currentValue = tremblors.get(i).getIntensity();

			for(int j = 0; j < tremblors.size(); j++) {
				if(currentValue < tremblors.get(j).getIntensity()) {
					break;
				}
				else if(j == tremblors.size() - 1) {
					temp.add(tremblors.get(i));
					tremblors.remove(i);
					i = -1;
					break;
				}
			}
		}

		tremblors.clear();
		tremblors.addAll(temp);
	}

	public int getDistortion() {
		return distortion;
	}

	public boolean isEmpty() {
		return tremblors.isEmpty();
	}
}
