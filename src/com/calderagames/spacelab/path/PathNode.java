package com.calderagames.spacelab.path;

import java.util.ArrayList;

public class PathNode {
	public float g, h;
	public int row, col;
	public boolean closed, opened;
	public PathNode parent;
	int type;

	public PathNode(int row, int col, int type) {
		this.row = row;
		this.col = col;
		this.type = type;
	}

	float getFcost() {
		return g + h;
	}

	void setParent(PathNode parent) {
		this.parent = parent;
	}

	void setPosition(int row, int col) {
		this.row = row;
		this.col = col;
	}

	void addToPath(ArrayList<PathNode> path) {
		if(parent == null)
			return;
		path.add(parent);
		parent.addToPath(path);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof PathNode) {
			PathNode node = (PathNode) o;
			return node.row == row && node.col == col;
		}
		else
			return false;
	}
}
