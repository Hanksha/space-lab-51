package com.calderagames.spacelab.path;

import org.lwjgl.util.vector.Vector2f;

public class SimpleBezierCurve implements BezierCurve {

	private Vector2f p0;
	private Vector2f p1;
	
	public SimpleBezierCurve(Vector2f p0, Vector2f p1) {
		this.p0 = p0;
		this.p1 = p1;
	}
	
	@Override
	public Vector2f getPoint(float t) {
		Vector2f point = new Vector2f();
		
		point.x = (1-t) * p0.x + t * p1.x;
		point.y = (1-t) * p0.y + t * p1.y;
		
		return point;
	}

	@Override
	public void setStartPoint(Vector2f point) {
		p0 = point;
		
	}

	@Override
	public void setEndPoint(Vector2f point) {
		p1 = point;
	}

}
