package com.calderagames.spacelab.path;

import org.lwjgl.util.vector.Vector2f;

public class QuadraticBezierCurve implements BezierCurve {

	private Vector2f p0;
	private Vector2f p1;
	private Vector2f p2;

	/**
	 * Constructor for a quadratic bezier curve
	 * 
	 * @param p0
	 *            the first point
	 * @param p1
	 *            the control point
	 * @param p2
	 *            the end point
	 */
	public QuadraticBezierCurve(Vector2f p0, Vector2f p1, Vector2f p2) {
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	@Override
	public void setStartPoint(Vector2f point) {
		p0 = point;
	}
	
	@Override
	public void setEndPoint(Vector2f point) {
		p2 = point;
	}

	@Override
	public Vector2f getPoint(float t) {

		float u = 1 - t;
		float tt = t * t;
		float uu = u * u;

		Vector2f point = new Vector2f();

		point.x = uu * p0.x + 2 * u * t * p1.x + tt * p2.x;
		point.y = uu * p0.y + 2 * u * t * p1.y + tt * p2.y;

		return point;
	}
}
