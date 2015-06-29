package analyzer;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import protoGenerator.Block;



public class Analyzer {

	public static ArrayList<Node> analyse(String path, int level) {
		System.out.println(" Node Analyser: Setting up base node, reading file.");
		boolean inLevel = false;
		boolean inBlackObstacles = false;
		ArrayList<Link> allLinks = new ArrayList<Link>();
		ArrayList<Node> allNodes = new ArrayList<Node>();
		File in = new File(path);
		Node floor = new Node(new Block(40, 760, 1200, 40));
		BufferedReader input = null;
		allNodes.add(floor);
		allNodes.add(new Node(new Block(40,0,1200,40)));
		allNodes.add(new Node(new Block(0,0,40,800)));
		allNodes.add(new Node(new Block(1240,0,40,800)));

		try {
			input = new BufferedReader(new FileReader(in));
			String currentLine; 
			while ((currentLine = input.readLine()) != null) {
				String delims = "[\n\t\r<]+";
				String tokens[] = currentLine.split(delims);
				if(tokens[1].equals("Level"+level+">")) {
					inLevel= true;
				}
				if(inLevel) {
					if(tokens[1].equals("/Level"+level+">")) {
						inLevel= false;
					}
					if(tokens[1].equals("BlackObstacles>")) {
						inBlackObstacles = true;
					}
					if(inBlackObstacles) {
						if(tokens[1].equals("/BlackObstacles>")) {
							inBlackObstacles = false;
						}
						if(tokens[1].contains("Obstacle ")) {
							String[] values = tokens[1].split("[\"]");
							values[0] = values[0].substring("Obstacle ".length()-1, values[0].length());
							int width=0, height=0, x=0, y=0;
							for(int i =0; i < 8; i+=2) {
								if(values[i].equals(" X=")) {
									x = Integer.parseInt(values[i+1]);
								}
								if(values[i].equals(" Y=")) {
									y = Integer.parseInt(values[i+1]);
								}
								if(values[i].equals(" width=")) {
									width = Integer.parseInt(values[i+1]);
								}
								if(values[i].equals(" height=")) {
									height = Integer.parseInt(values[i+1]);
								}
							}
							floor.links.add(new Link(floor, new Node(new Block(x, y, width, height))));
						}
					}
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(" - Reading file Successful, mapping links.");

		for(Link l : floor.links) {
			allLinks.add(l);
			Node currentNode = l.target;
			allNodes.add(currentNode);

			Link backlink = new Link(currentNode, floor);
			currentNode.links.add(backlink);
			allLinks.add(backlink);

			for(Link l2 : floor.links) {
				if(l2.target != currentNode) {
					Link newLink = new Link(currentNode, l2.target);
					currentNode.links.add(newLink);
					allLinks.add(newLink);
				}
			}
		}
		
		System.out.println(" - Creating node Areas.");
		
		for(Node n : allNodes) {
			int x = n.block.getCoordX();
			int xS = n.block.getSizeX();
			int y = n.block.getCoordY();
			//300 = speed
			int[] xpointsB = {x-300, x+xS+300, x+xS+300+3000	, x+xS		,	x+xS, x	, x				, x-300-3000 };
			int[] ypointsB = {y-300, y-300	, y-300+3000	, y-300+3000, 	y	, y	, y-300+3000	, y-300+3000 };
			Area areaBall = new Area( new Polygon(xpointsB, ypointsB, 8) );
			
			int[] xpointsAC = {x-300, x+xS+300, x+xS+300+3000	, x+xS		,	x+xS, x	, x				, x-300-3000 };
			int[] ypointsAC = {y-450, y-500	, y-450+3000	, y-450+3000, 	y	, y	, y-450+3000	, y-450+3000 };
			Area areaAssistedCircle = new Area( new Polygon(xpointsAC, ypointsAC, 8) );
			
			int[] xpointsC = {x		, x+xS	, x+xS+300+3000	, x+xS		,	x+xS, x	, x			, x-300-3000 };
			int[] ypointsC = {y-50	, y-50	, y-50+3000		, y-50+3000	, 	y	, y	, y-50+3000	, y-50+3000 };
			Area areaCube = new Area( new Polygon(xpointsC, ypointsC, 8) );
			
			
			Area smallerArea = new Area(areaAssistedCircle);
			smallerArea.subtract(areaBall);
			//first pass: build area
			int[] larray = new int[n.links.size()];
			int larraycnt = 0;
			for(Link l : n.links) {
				if(areaBall.intersects(l.target.block.getRect())) {
					larray[larraycnt] += 1;
					areaBall.subtract(new Area (l.target.block.getRect()));
					int closestX = getClosestX(l.origin.block, l.target.block);
					int tx = l.target.block.getCoordX();
					int tsx = l.target.block.getSizeX();
					int ty = l.target.block.getCoordY();
					int tsy = l.target.block.getSizeY();					
					if(l.origin.block.getCoordY() < l.target.block.getCoordY()) {
						int c = (tx-closestX ) / (ty - l.origin.block.getCoordY()+1);
						int c2 =(tx+tsx - closestX) / (ty - l.origin.block.getCoordY()+1);
						int[] xpoints = {tx+tsx, tx, tx +3000*c2,tx+tsx +3000*c};
						int[] ypoints = {ty, ty , ty-3000, ty-3000};
						areaBall.subtract(new Area(new Polygon(xpoints,ypoints, 4)));
					} else {
						int c = (tx-closestX ) / (ty+tsy - l.origin.block.getCoordY()+1);
						int c2 =(tx+tsx - closestX) / (ty+tsy - l.origin.block.getCoordY()+1);
						int[] xpoints = {tx+tsx, tx, tx +3000*c2,tx+tsx +3000*c};
						int[] ypoints = {ty+tsy, ty+tsy , ty+tsy-3000, ty+tsy-3000};
						areaBall.subtract(new Area(new Polygon(xpoints,ypoints, 4)));
					}
				}
				if(areaCube.intersects(l.target.block.getRect())) {
					larray[larraycnt] += 2;
					areaCube.subtract(new Area (l.target.block.getRect()));
					int closestX = getClosestX(l.origin.block, l.target.block);
					int tx = l.target.block.getCoordX();
					int tsx = l.target.block.getSizeX();
					int ty = l.target.block.getCoordY();
					int tsy = l.target.block.getSizeY();
					if(l.origin.block.getCoordY() < l.target.block.getCoordY()) {
						int c = (tx-closestX ) / (ty - l.origin.block.getCoordY()+1);
						int c2 =(tx+tsx - closestX) / (ty - l.origin.block.getCoordY()+1);
						int[] xpoints = {tx+tsx, tx, tx +3000*c2,tx+tsx +3000*c};
						int[] ypoints = {ty, ty , ty-3000, ty-3000};
						areaCube.subtract(new Area(new Polygon(xpoints,ypoints, 4)));
					} else {
						int c = (tx-closestX ) / (ty+tsy - l.origin.block.getCoordY()+1);
						int c2 =(tx+tsx - closestX) / (ty+tsy - l.origin.block.getCoordY()+1);
						int[] xpoints = {tx+tsx, tx, tx +3000*c2,tx+tsx +3000*c};
						int[] ypoints = {ty+tsy, ty+tsy , ty+tsy-3000, ty+tsy-3000};
						areaCube.subtract(new Area(new Polygon(xpoints,ypoints, 4)));
					}
				}
				
				if(smallerArea.intersects(l.target.block.getRect())) {
					larray[larraycnt] += 4;
					smallerArea.subtract(new Area (l.target.block.getRect()));
					int closestX = getClosestX(l.origin.block, l.target.block);
					int tx = l.target.block.getCoordX();
					int tsx = l.target.block.getSizeX();
					int ty = l.target.block.getCoordY();
					int tsy = l.target.block.getSizeY();
					if(l.origin.block.getCoordY() < l.target.block.getCoordY()) {
						int c = (tx-closestX ) / (ty - l.origin.block.getCoordY()+1);
						int c2 =(tx+tsx - closestX) / (ty - l.origin.block.getCoordY()+1);
						int[] xpoints = {tx+tsx, tx, tx +3000*c2,tx+tsx +3000*c};
						int[] ypoints = {ty, ty , ty-3000, ty-3000};
						smallerArea.subtract(new Area(new Polygon(xpoints,ypoints, 4)));
					} else {
						int c = (tx-closestX ) / (ty+tsy - l.origin.block.getCoordY()+1);
						int c2 =(tx+tsx - closestX) / (ty+tsy - l.origin.block.getCoordY()+1);
						int[] xpoints = {tx+tsx, tx, tx +3000*c2,tx+tsx +3000*c};
						int[] ypoints = {ty+tsy, ty+tsy , ty+tsy-3000, ty+tsy-3000};
						smallerArea.subtract(new Area(new Polygon(xpoints,ypoints, 4)));
					}
				}
				larraycnt++;
			}
			larraycnt = 0;
			//second pass
			for(Link l : n.links) {
					if((larray[larraycnt] & 1) == 1) {
						if(areaBall.intersects(l.target.block.getLandingArea())) {
							l.setBall(true);
						}
					}
					if((larray[larraycnt] & 2) == 2) {
						if(areaCube.intersects(l.target.block.getLandingArea())) {
							l.setCube(true);
						}
					}
					if((larray[larraycnt] & 4) == 4) {
						if(smallerArea.intersects(l.target.block.getLandingArea())) {
							l.setBallWithCube(true);
						}
					}
				larraycnt++;
			}
			
		}
		System.out.println(" - Areas analysed, links classified. Removing empty links.");		
		//clear empty Links from Nodes
		for(Node nd : allNodes) {
			ArrayList<Link> toRemove = new ArrayList<Link>();
			for(Link l : nd.links) {
				if(l.getChannel() == 0) {
					toRemove.add(l);
				}
			}
			for(Link l : toRemove) {
				if(l.getChannel() == 0) {
					nd.links.remove(l);
					allLinks.remove(l);
				}
			}
			toRemove.clear();
		}
		for (int j = 1; j < 3; j++) {
			allNodes.remove(allNodes.get(j));
		}
		return allNodes;
	}

	private static int getClosestX(Block block, Block block2) {
		int counter = 0;
		int smallest = Math.abs(block.getCoordX() - block2.getCoordX());
		int lastTested = Math.abs(block.getCoordX()+block.getSizeX() - block2.getCoordX());
		if(lastTested < smallest) {
			counter = 1;
			smallest = lastTested;
		}
		lastTested = Math.abs(block.getCoordX()+block.getSizeX() - block2.getCoordX()+block.getSizeX());
		if(lastTested < smallest) {
			counter = 2;
			smallest = lastTested;
		}
		lastTested = Math.abs(block.getCoordX() - block2.getCoordX()+block.getSizeX());
		if(lastTested < smallest) {
			counter = 3;
			smallest = lastTested;
		}
		if(counter == 0 || counter == 3) {
			return block.getCoordX();
		} else return block.getCoordX()+block.getSizeX();
	}

	@SuppressWarnings("unused")
	private static boolean obstructs(Block block, Block origin, Block target) {
		Rectangle rect = new Rectangle(block.getCoordX(), block.getCoordY(), block.getSizeX(), block.getSizeY());
		if(intersects(new Point(origin.getCoordX(), origin.getCoordY()), 
				new Point(target.getCoordX(), target.getCoordY()), rect)) return true;
		if(intersects(new Point(origin.getCoordX() + origin.getSizeX(), origin.getCoordY()), 
				new Point(target.getCoordX(), target.getCoordY()), rect)) return true;
		if(intersects(new Point(origin.getCoordX() + origin.getSizeX(), origin.getCoordY()), 
				new Point(target.getCoordX() + target.getSizeX(), target.getCoordY()), rect)) return true;
		if(intersects(new Point(origin.getCoordX(), origin.getCoordY()), 
				new Point(target.getCoordX() + target.getSizeX(), target.getCoordY()), rect)) return true;
		return false;
	}
	
	public static int calculateOutCode (Point k, Rectangle r) {
		int outcodeK = 0; // inside
		if(k.x < r.getMinX())
			outcodeK += 1; //left
		if(k.x > r.getMaxX())
			outcodeK += 2; //right
		if(k.y < r.getMinY())
			outcodeK += 4; // bottom
		if(k.y > r.getMaxY())
			outcodeK += 8; // top
		return outcodeK;
	}
	public static boolean intersects(Point k, Point z, Rectangle r) {
		double x0 = k.getX();
		double x1 = z.getX();
		double y0 = k.getY();
		double y1 = z.getY();
		
		int outcodeK = calculateOutCode(k, r);
		int outcodeZ = calculateOutCode(z, r);
		while(true) {

			if((outcodeK | outcodeZ) == 0) {
				return true; // inside
			}
			if((outcodeK & outcodeZ) != 0) {
				return false;
			}
			
			double x = 0, y = 0;
			int outcodeOut = outcodeK != 0 ? outcodeK : outcodeZ;
			if((outcodeOut & 8) == 8) {
				x = x0 + (x1 - x0) * (r.getMaxY() - y0) / (y1 - y0);
				y = r.getMaxY();
			}
			if((outcodeOut & 4) == 4) {
				x = x0 + (x1 - x0) * (r.getMinY() - y0) / (y1 - y0);
				y = r.getMinY();
			}
			if((outcodeOut & 2) == 2) {
				y = y0 + (y1 - y0) * (r.getMaxX() - x0) / (x1 - x0);
				x = r.getMaxX();
			}
			if((outcodeOut & 1) == 1) {
				y = y0 + (y1 - y0) * (r.getMinX() - x0) / (x1 - x0);
				x = r.getMinX();
			}
			
			if (outcodeOut == outcodeK) {
				x0 = x;
				y0 = y;
				outcodeK = calculateOutCode(new Point ((int)x0, (int)y0), r);
			} else {
				x1 = x;
				y1 = y;
				outcodeZ = calculateOutCode(new Point ((int)x1, (int)y1), r);
			}
			
		}
	}
}
