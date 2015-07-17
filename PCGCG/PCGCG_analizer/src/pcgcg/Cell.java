package pcgcg;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import pcgcg.GraphicInterface.Overlay;

public class Cell implements Comparable<Cell>{
	public int ballArea = 0;
	public int cubeArea = 0;
	public boolean fitsBall = false;
	public boolean fitsCube = false;
	public boolean reachBall = false;
	public boolean reachCube = false;
	public boolean reachCoop = false;
	public Point2D topleft, botright;
	public double sizeX, sizeY;
	public boolean occupied= false;
	public int closestOccupied = Integer.MAX_VALUE;
	public int closestOccupiedX = Integer.MAX_VALUE;
	public int closestOccupiedY = Integer.MAX_VALUE;
	public boolean coop = false;
	public boolean traversedCubeLeft, traversedCubeRight;
	public int ballVisits = 0;
	public int maxJumpStrenght=0;
	public int x, y;
	public long id;
	public static long idCounter=0;
	
	public Cell( int leftx, int topy, int rightx, int boty ) {
		topleft = new Point2D.Double(leftx,topy);
		botright = new Point2D.Double(rightx, boty);
		sizeX = Math.abs(leftx - rightx);
		sizeY = Math.abs(topy - boty);
		id = ++idCounter;
	}

	@Override
	public String toString() {
		return "Cell[" + topleft + "," + botright
				+ "]";
	}

	public boolean occupied() {
		return occupied;
	}

	@Override
	public int compareTo(Cell o) {
		if(this.id < o.id) 
			return -1;
		if(this.id > o.id)
			return 1;
		return 0;
	}

	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
		g.setColor(GraphicInterface.cellBackground); // default
		if(occupied())
			g.setColor(GraphicInterface.cellOccBackground);
		else {
			//int dist = Math.max(0,Math.min(cells[i][j].closestOccupied*1, 255)); //old
			int dist = 255 - (maxJumpStrenght*10);
			Color distColor = new Color(255, dist, dist);
			Color toUse = GraphicInterface.cellBackground;
			if(GraphicInterface.instance().currentOverlay == Overlay.Distance)
				toUse = distColor;
			if(GraphicInterface.instance().currentOverlay == Overlay.Ball && (fitsBall||reachBall)) {
				toUse = GraphicInterface.cellNotBallBackground;
				if(reachBall)
					toUse = toUse.darker();
			}
			if(GraphicInterface.instance().currentOverlay == Overlay.Cube && (fitsCube||reachCube)) {
				toUse = GraphicInterface.cellCubeBackground;
				if(reachCube)
					toUse = toUse.darker();
			}
			if(GraphicInterface.instance().currentOverlay == Overlay.Coop && coop) {
				toUse = GraphicInterface.cellCoopBackground;
			}

			g.setColor(toUse);
		}
		g.fillRect((int)topleft.getX(), (int)topleft.getY(), (int)sizeX, (int)sizeY);
		g.setColor(GraphicInterface.cellBorder);
		g.drawRect((int)topleft.getX(), (int)topleft.getY(), (int)sizeX, (int)sizeY);
	
	}
	
	

}
