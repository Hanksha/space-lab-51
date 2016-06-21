package com.calderagames.spacelab.path;

public class Chebyshev implements Heuristic {

	@Override
	public float getHeuristic(PathNode node, PathNode end) {
		int dx = Math.abs(node.col - end.col);
		int dy = Math.abs(node.row - end.row);
		return Math.max(dx, dy);
	}

}
