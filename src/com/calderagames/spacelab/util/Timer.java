package com.calderagames.spacelab.util;

public class Timer {

	private double timer;
	private int delay;

	public Timer(int delay) {
		this.delay = delay;
		timer = System.nanoTime() / 1000000d;
	}

	public void setDelay(int d) {
		delay = d;
	}

	public boolean tick() {
		if(delay <= 0)
			return true;
		else if(timer + delay < System.nanoTime() / 1000000d)
			return true;
		else
			return false;
	}

	public int getRemainingTime() {
		return (int) ((timer + delay) - System.nanoTime() / 1000000d);
	}

	public int getDelay() {
		return delay;
	}

	public void update() {
		timer = System.nanoTime() / 1000000d;
	}
}
