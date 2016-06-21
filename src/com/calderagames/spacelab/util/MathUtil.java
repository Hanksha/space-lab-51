package com.calderagames.spacelab.util;

public class MathUtil {
	public static float getDistance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	public static float getDistanceAxis(float p1, float p2) {
		return p1 - p2;
	}
}
