package com.calderagames.spacelab.path;

import org.lwjgl.util.vector.Vector2f;

public interface BezierCurve {

	/**
	 * Returns the point on the the bezier curve for the time t
	 * 
	 * @param t
	 *            the time between 0 and 1
	 * @return returns the point on the the bezier curve for the time t
	 */
	public Vector2f getPoint(float t);
	
	public void setStartPoint(Vector2f point);
	
	public void setEndPoint(Vector2f point);
}
