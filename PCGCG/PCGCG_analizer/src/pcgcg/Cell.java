package pcgcg;

import java.awt.geom.Point2D;

public class Cell {
	public int ballArea = 0;
	public int cubeArea = 0;
	public boolean fitsBall = false;
	public Point2D topleft, botright;
	public double sizeX, sizeY;
	public boolean occupied= false;
	public int closestOccupied = Integer.MAX_VALUE;
	public int closestOccupiedX = Integer.MAX_VALUE;
	public int closestOccupiedY = Integer.MAX_VALUE;
	public boolean fitsCube = false;
	public boolean coop = false;
	
	public Cell( int leftx, int topy, int rightx, int boty ) {
		topleft = new Point2D.Double(leftx,topy);
		botright = new Point2D.Double(rightx, boty);
		sizeX = Math.abs(leftx - rightx);
		sizeY = Math.abs(topy - boty);
	}

	@Override
	public String toString() {
		return "Cell[" + topleft + "," + botright
				+ "]";
	}

	public boolean occupied() {
		return occupied;
	}

}
