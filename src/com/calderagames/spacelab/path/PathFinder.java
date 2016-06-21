package com.calderagames.spacelab.path;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.util.vector.Vector2f;

import com.calderagames.spacelab.entities.DynamicEntity;
import com.calderagames.spacelab.entities.Entity;
import com.calderagames.spacelab.entities.Obstacle;
import com.calderagames.spacelab.gamemap.Map;
import com.calderagames.spacelab.tiles.Tile;

public class PathFinder {

	private ArrayList<PathNode> openList;
	private ArrayList<PathNode> closedList;
	private ArrayList<PathNode> neighbors;
	private Heuristic heuristic;

	private Map map;
	private PathNode mapNode[][];
	private ArrayList<PathNode> pathNodes;
	private float SQRT2 = (float) Math.sqrt(2);
	private PathNode start;
	private PathNode end;
	private int weight = 10;
	private boolean avoidEntity;

	public PathFinder(Heuristic heuristic) {
		openList = new ArrayList<>();
		closedList = new ArrayList<>();
		neighbors = new ArrayList<>();

		this.heuristic = heuristic;
	}

	public void setMap(Map map) {
		this.map = map;
		mapNode = new PathNode[map.getTileMap().getNumRowMap()][map.getTileMap().getNumColMap()];
		for(int row = 0; row < mapNode.length; row++) {
			for(int col = 0; col < mapNode.length; col++) {
				mapNode[row][col] = new PathNode(row, col, map.getTileMap().getTypei(row, col, map.getTileMap().getCollisionLayerIndex()));
			}
		}
	}

	public boolean searchPath(int startRow, int startCol, int endRow, int endCol, boolean avoidEntity) {
		this.avoidEntity = avoidEntity;
		this.start = mapNode[startRow][startCol];
		this.end = mapNode[endRow][endCol];

		if(start.equals(end) || (avoidEntity && isBlock(end) && Math.abs(start.row - end.row) <= 1 && Math.abs(start.col - end.col) <= 1))
			return false;

		prepareSearch();

		while(!openList.isEmpty()) {
			//set the lowest rank item as current
			PathNode current = openList.get(0);

			//add current to closed list and remove from open list
			current.closed = true;
			closedList.add(current);
			openList.remove(current);

			//if end node
			if((current.row == end.row && current.col == end.col) || (avoidEntity && isBlock(end) && !isBlock(current) && Math.abs(current.row - end.row) <= 1 && Math.abs(current.col - end.col) <= 1)) {
				end = current;
				pathNodes = backTrace();
				return true;
			}

			//get neighbors
			getNeighbor(current);

			//add neighbor in open list
			for(PathNode neighbor : neighbors) {

				float ng = getComputeG(current, neighbor);

				if(!neighbor.opened || ng < neighbor.g) {
					neighbor.setParent(current);

					neighbor.h = heuristic.getHeuristic(neighbor, end) * weight;
					neighbor.g = ng;
					neighbor.opened = true;

					//sort the neighbor
					int i = 0;
					do {
						if(openList.isEmpty()) {
							openList.add(neighbor);
							break;
						}
						else if(neighbor.getFcost() < openList.get(i).getFcost()) {
							openList.add(i, neighbor);
							break;
						}
						else if(i == openList.size() - 1) {
							openList.add(neighbor);
							break;
						}
						i++;
					} while(i < openList.size());
				}
			}
		}

		return false;
	}

	public void prepareSearch() {
		reset();
		openList.add(start);
	}

	public ArrayList<PathNode> backTrace() {
		ArrayList<PathNode> path = new ArrayList<PathNode>();
		path.add(end);
		end.addToPath(path);
		Collections.reverse(path);

		return path;
	}

	public ArrayList<PathNode> getPathNodes() {
		return pathNodes;
	}

	public Path getPath() {
		Vector2f[] points = new Vector2f[getPathNodes().size()];

		for(int i = 0; i < points.length; i++) {
			PathNode node = getPathNodes().get(i);
			points[i] = new Vector2f(node.col * Tile.tileSize + Tile.tileSize / 2, node.row * Tile.tileSize + Tile.tileSize / 2);
		}

		return new Path(points);
	}

	public float getComputeG(PathNode current, PathNode neighbor) {
		return current.g + ((neighbor.col != current.col && neighbor.row != current.row) ? SQRT2 : 1);
	}

	private void getNeighbor(PathNode current) {
		//clear neighbor list
		neighbors.clear();
		boolean r, l, u, d;
		r = l = u = d = false;
		//R L D U priority

		//RIGHT
		//check if out of bounds
		if(current.col + 1 < mapNode[0].length) {
			//create neighbor node
			PathNode newNeighbor = mapNode[current.row][current.col + 1];
			//check cell type
			if(!isBlock(newNeighbor) && !newNeighbor.closed) {
				r = true;
				//add to neighbor list
				neighbors.add(newNeighbor);
			}
		}
		//LEFT
		if(current.col - 1 >= 0) {
			PathNode newNeighbor = mapNode[current.row][current.col - 1];
			if(!isBlock(newNeighbor) && !newNeighbor.closed) {
				l = true;
				neighbors.add(newNeighbor);
			}
		}

		//DOWN
		if(current.row + 1 < mapNode.length) {
			PathNode newNeighbor = mapNode[current.row + 1][current.col];
			if(!isBlock(newNeighbor) && !newNeighbor.closed) {
				d = true;
				neighbors.add(newNeighbor);
			}
		}
		//UP
		if(current.row - 1 >= 0) {
			PathNode newNeighbor = mapNode[current.row - 1][current.col];
			if(!isBlock(newNeighbor) && !newNeighbor.closed) {
				u = true;
				neighbors.add(newNeighbor);
			}
		}

		//UP RIGHT
		if(current.row - 1 >= 0 && current.col + 1 < mapNode[0].length) {
			PathNode newNeighbor = mapNode[current.row - 1][current.col + 1];
			if(!isBlock(newNeighbor) && !newNeighbor.closed && u && r) {
				neighbors.add(newNeighbor);
			}
		}
		//UP LEFT
		if(current.row - 1 >= 0 && current.col - 1 >= 0) {
			PathNode newNeighbor = mapNode[current.row - 1][current.col - 1];
			if(!isBlock(newNeighbor) && !newNeighbor.closed && u && l) {
				neighbors.add(newNeighbor);
			}
		}

		//DOWN RIGHT
		if(current.row + 1 < mapNode.length && current.col + 1 < mapNode[0].length) {
			PathNode newNeighbor = mapNode[current.row + 1][current.col + 1];
			if(!isBlock(newNeighbor) && !newNeighbor.closed && d && r) {
				neighbors.add(newNeighbor);
			}
		}

		//DOWN LEFT
		if(current.row + 1 < mapNode.length && current.col - 1 >= 0) {
			PathNode newNeighbor = mapNode[current.row + 1][current.col - 1];
			if(!isBlock(newNeighbor) && !newNeighbor.closed && d && l) {
				neighbors.add(newNeighbor);
			}
		}
	}

	public boolean isBlock(PathNode node) {
		if(!map.getMapCell(node.row, node.col).getEntities().isEmpty()){
			for(Entity e: map.getMapCell(node.row, node.col).getEntities()) {
				if(e instanceof Obstacle && ((Obstacle)e).isBlock())
					return true;
				else if(avoidEntity && e instanceof DynamicEntity && !((DynamicEntity) e).isDead())
					return true;
			}
		}

		switch(node.type) {
		case Tile.BLOCK:
			return true;
		default:
			return false;
		}
	}

	public ArrayList<PathNode> getOpenList() {
		return openList;
	}

	public ArrayList<PathNode> getClosedList() {
		return closedList;
	}

	public void reset() {
		for(PathNode node : openList) {
			mapNode[node.row][node.col].opened = false;
			mapNode[node.row][node.col].closed = false;
			mapNode[node.row][node.col].parent = null;
		}
		for(PathNode node : closedList) {
			mapNode[node.row][node.col].opened = false;
			mapNode[node.row][node.col].closed = false;
			mapNode[node.row][node.col].parent = null;
		}
		openList.clear();
		closedList.clear();
		neighbors.clear();
	}
}
