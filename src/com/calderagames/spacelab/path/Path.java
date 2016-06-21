package com.calderagames.spacelab.path;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

public class Path {

	private ArrayList<Vector2f> points;
	private BezierCurve curve;
	private float speedOnPath;
	private float lastAngle;
	private int index;
	private float t;

	public Path() {
		this.points = new ArrayList<>();
		curve = new SimpleBezierCurve(null, null);
	}

	public Path(Vector2f[] points) {
		this();
		for(Vector2f point : points)
			addPoint(point);
		if(points.length >= 2)
			curve = new SimpleBezierCurve(this.points.get(0), this.points.get(1));
	}

	public Path(Vector2f p0, Vector2f p1) {
		this.points = new ArrayList<>();
		addPoint(p0);
		addPoint(p1);
		curve = new SimpleBezierCurve(p0, p1);
	}

	public void update(double dt) {
		if(points.isEmpty())
			return;

		t += speedOnPath * dt;

		if(t >= 1) {
			if(index < points.size() - 2) {
				index++;
				curve.setStartPoint(points.get(index));
				curve.setEndPoint(points.get(index + 1));

				t = t - 1;
			}
			else {
				t = 1;
			}
		}
	}

	public void addPoint(Vector2f point) {
		points.add(point);
	}

	public void cut() {
		if(index == points.size() - 2)
			return; 
		points.subList(index, points.size() - 1).clear();
	}

	public void prefix(Path path) {
		points.addAll(0, path.getPoints());
	}

	public void affix(Path path) {
		affix(path, path.getPoints().size());
	}

	public void affix(Path path, int length) {
		points.addAll(path.getPoints().subList(0, length));
	}

	public boolean isEnd() {
		return (t == 1 && index >= points.size() - 2);
	}

	public void setSpeed(float speedOnPath) {
		this.speedOnPath = speedOnPath;
	}

	/**
	 * @return the vector direction at the current position on the path
	 */
	public float getDirectionAngle() {
		return getDirectionAngle(index);
	}

	public float getDirectionAngle(int index) {
		float angle = lastAngle;

		if(index <= points.size() - 2) {
			float dx = points.get(index + 1).x - points.get(index).x;
			float dy = points.get(index).y - points.get(index + 1).y;
			
			angle = (float) Math.toDegrees(Math.atan2(dx, dy));
		}
		lastAngle = angle;
		return angle;
	}

	public Vector2f getPointOnPath() {
		return curve.getPoint(t);
	}
	
	public ArrayList<Vector2f> getPoints() {
		return points;
	}
	
	public float getPathTotalMoveCost(float moveCost) {
		float total = 0;
		float diagCost = (float) Math.sqrt((moveCost * moveCost) * 2);
		for(int i = 0; i <= points.size() - 2; i++) {
			if((points.get(i + 1).x != points.get(i).x) && (points.get(i + 1).y != points.get(i).y))
				total += diagCost;
			else
				total += moveCost;
		}
		
		return total;
	}
	
	public boolean removeLast() {
		if(points.size() <= 2)
			return false;
		
		points.remove(points.size() - 1);
		
		return true;
	}

	public void reset() {
		index = 0;
		t = 0;
		
		if(points.size() >= 2) {
    		curve.setStartPoint(points.get(index));
    		curve.setEndPoint(points.get(index + 1));
		}
		else {
			curve.setStartPoint(points.get(index));
    		curve.setEndPoint(points.get(index));
		}
	}

}
