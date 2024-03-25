import java.awt.Graphics;
import java.awt.Image;
import java.util.LinkedList;
import java.util.Stack;

public class BadGuy {
	private final int MAPSIZE = 40;
	Image myImage;
	int x,y;
	private final Node[][] nodeMap = new Node[MAPSIZE][MAPSIZE];
	LinkedList<Node> openList = new LinkedList<>();	// TODO: make this a binary heap
	LinkedList<Node> closedList = new LinkedList<>();
	Stack<Node> finalPath = new Stack<>();

	public BadGuy( Image i ) {
		myImage=i;
		x = 30;
		y = 10;
	}
	
	public boolean reCalcPath(boolean[][] map, int targx, int targy) {
		// clear everything, not sure if this is correct or not but oh well
		openList.clear();
		closedList.clear();
		finalPath.clear();
		for (int i = 0; i < MAPSIZE; i++) {
			for (int j = 0; j < MAPSIZE; j++) {
				nodeMap[i][j] = new Node(i, j);
			}
		}

		// looking for lowest f, where f = g + h, g = distance to node, and h = heuristic distance from node to target
		// using manhattan distance for h
		int f, g;	// TODO: check if these are necessary
		boolean found = false;
		Node checking;

		// get current node
		Node curr = nodeMap[x][y];
		// push it to open list
		openList.addLast(curr);

		// make all unwalkable nodes closed
		// could possibly precalculate this too
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j]) {
					closedList.addLast(nodeMap[i][j]);
				}
			}
		}

		/*
			steps:
			1) look for lowest score on open list, this is new curr
			2) switch it to closed
			3) for each of the nodes adjacent to curr:
				- if unwalkable (map true) or in closed ignore
				- if not on open, make this parent and add to open, then calculate and record f, g, h
				- if on open, check to see if this g cost is better, if so, change parent and recalculate f and g

			stop if all nodes have been checked or if target is added to closed list
		*/
		while (((closedList.size()) != (map.length * map.length))) {
			// find current lowest f, this is the curent node

			f = Integer.MAX_VALUE;

			for (Node node : openList) {
				if (node.f <= f) {
					f = node.f;
					curr = node;
				}
			}

			// add it to the closed list
			closedList.addLast(curr);
			openList.remove(curr);
			System.out.println("node " + curr.x + ", " + curr.y + " added to closed list.");

			if (closedList.contains(nodeMap[targx][targy])) {
				found = true;
				break;
			}

			// add each surrounding node to openList
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					// if diagonal (i and j not 0) g = 14, otherwise g = 10
					if ((i != 0 && j != 0)) {
						g = 14;
					} else {
						g = 10;
					}

					// if not out of bounds or itself
					if (!(
							((curr.x + i) < 0)
								||
							((curr.x + i) > (map.length - 1))
								||
							((curr.y + j) < 0)
								||
							((curr.y + j) > (map.length - 1))
					) && (i != 0 || j != 0)) {

						// and if not in open list already
						checking = nodeMap[curr.x + i][curr.y + j];
						if (!openList.contains(checking) && !closedList.contains(checking)) {
							// add to open, set parent, and calculate f, g, and h
							checking.setParent(curr);
							checking.setH(calcH(checking.x, checking.y, targx, targy));
							checking.setG(g);
							checking.setF();
							openList.addLast(checking);
						}
						// if in open list, recheck g
						else if (openList.contains(checking)){
							if (checking.g > g) {
								checking.setG(g);
								checking.setParent(curr);
								checking.setF();
							}
						}
					}
				}
			}
		}

		// get final path by pushing the path from the target back to the source to a stack, and pop the last one later in move
		curr = closedList.getLast();
		while (curr.parent != null) {
			finalPath.push(curr);
			curr = curr.parent;
		}

		return found;
	}

	public int calcH(int x, int y, int targX, int targY) {
		return ((Math.abs(targX - x) + Math.abs(targY - y)) * 10);
	}
	
	public void move(boolean[][] map, int targx, int targy) {
		if (x == targx && y == targy) {
			return;
		}

		// so reCalcPath and if we can find one, follow it, otherwise run towards
		// for later assignments maybe start from opposite end as well, or only recalculate when player's x and y change

		if (reCalcPath(map, targx, targy)) {
			// follow A* path, if we have one defined
			Node next = finalPath.pop();
			x = next.x;
			y = next.y;
		}
		else {
			// no path known, so just do a dumb 'run towards' behaviour
			int newx = x, newy = y;
			if (targx<x)
				newx--;
			else if (targx>x)
				newx++;
			if (targy<y)
				newy--;
			else if (targy>y)
				newy++;
			if (!map[newx][newy]) {
				x=newx;
				y=newy;
			}
		}
	}
	
	public void paint(Graphics g) {
		g.drawImage(myImage, x*20, y*20, null);
	}
	
}

