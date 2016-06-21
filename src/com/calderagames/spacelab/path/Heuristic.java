package com.calderagames.spacelab.path;

public interface Heuristic {
	public float getHeuristic(PathNode node, PathNode end);
}
