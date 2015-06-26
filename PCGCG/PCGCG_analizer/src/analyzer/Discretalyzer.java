package analyzer;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Discretalyzer {
	public static int cellSizeX = 10;
	public static int cellSizeY = 10;
	public static final int levelSizeX = 1200; //1200
	public static final int levelSizeY = 800; //800
	public static Cell [][] cellMatrix;
	public static final int cubeHeight = 30;

	public static Cell[][] analyse(String path, int level) {
		System.out.println(" Discrete Analyser: Reading file, painting Node/Platform cells.");
		
		cellMatrix = new Cell[levelSizeX/cellSizeX+1][levelSizeY/cellSizeY+1];
		for(int x = 0, i = 0; x < levelSizeX; x += cellSizeX) {
			for(int y = 0, j =0; y < levelSizeY; y += cellSizeY) {
				cellMatrix[i][j] = new Cell(x, y, x+cellSizeX, y+cellSizeY);
				j++;
			}
			i++;
		}

		File in = new File(path);
		BufferedReader input = null;
		boolean inLevel = false;
		boolean inBlackObstacles = false;
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
							paintCells(x,y,width,height);
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
		System.out.println(" - File read, painting border cells.");
		paintBorders();
		
		System.out.println(" - Calculating cell distance to painted cell.");
		calculateProximity();
		System.out.println(" - Mapping ball restriction.");
		ballRestriction();
		System.out.println(" - Mapping cube restriction.");
		cubeRestriction();
		
		return cellMatrix;
	}

	private static void paintBorders() {
		paintCells(0,760,1200,40);
		paintCells(0,0,1200,40);
		paintCells(0,0,40,800);
		paintCells(1160,0,40,800);
		
	}

	private static int getClosestOccupied(int i, int j) {
		try {
			if(cellMatrix[i][j] == null) {
				return 0;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
		
		return cellMatrix[i][j].closestOccupied;
	}

	private static void calculateProximity() {
		//first pass
		for(int i = 0; i < cellMatrix.length-1; i++) {
			for(int j =0; j < cellMatrix[0].length-1; j++) {
				if(cellMatrix[i][j].occupied) {
					cellMatrix[i][j].closestOccupied = 0;
					continue;
				}
				int minClosestX = Integer.MAX_VALUE;
				int minClosestY = Integer.MAX_VALUE;
				minClosestX = Math.min(minClosestX, getClosestOccupied(i-1, j));
				minClosestX = Math.min(minClosestX, getClosestOccupied(i+1, j));
				minClosestY = Math.min(minClosestY, getClosestOccupied(i, j+1));
				minClosestY = Math.min(minClosestY, getClosestOccupied(i, j-1));
				
				if(minClosestX != Integer.MAX_VALUE) {
					minClosestX = minClosestX + cellSizeX;
				}
				if(minClosestY != Integer.MAX_VALUE) {
					minClosestY = minClosestY + cellSizeY;
				}
				int minClosest = Math.min(minClosestX, minClosestY);
				cellMatrix[i][j].closestOccupied = minClosest;
			}
		}
		// second inverted pass
		for(int i = cellMatrix.length-2; i > -1; i--) {
			for(int j = cellMatrix[0].length-2; j > -1; j--) {
				if(cellMatrix[i][j].occupied) {
					cellMatrix[i][j].closestOccupied = 0;
					continue;
				}
				int minClosestX = Integer.MAX_VALUE;
				int minClosestY = Integer.MAX_VALUE;
				minClosestX = Math.min(minClosestX, getClosestOccupied(i-1, j));
				minClosestX = Math.min(minClosestX, getClosestOccupied(i+1, j));
				minClosestY = Math.min(minClosestY, getClosestOccupied(i, j+1));
				minClosestY = Math.min(minClosestY, getClosestOccupied(i, j-1));
				
				if(minClosestX != Integer.MAX_VALUE) {
					minClosestX = minClosestX + cellSizeX;
				}
				if(minClosestY != Integer.MAX_VALUE) {
					minClosestY = minClosestY + cellSizeY;
				}
				int minClosest = Math.min(minClosestX, minClosestY);
				cellMatrix[i][j].closestOccupied = minClosest;
			}
		}
	}
	
	private static boolean checkOccupiedX(int i, int j, int dist, int dist2) { //dist2 = 100
		int distance = cellSizeX;
		int cnt = 1;
		while(distance<dist2) {
			if(getClosestOccupied(i-cnt, j) < dist && getClosestOccupied(i+cnt, j) <dist) {
				return false;
			}
			distance +=cellSizeX;
		}
		return true;
	}
	private static boolean checkOccupiedY(int i, int j, int dist, int dist2) { //dist2 = 100
		int distance = cellSizeY;
		int cnt = 1;
		while(distance<dist2) {
			if(getClosestOccupied(i, j-cnt) < dist && getClosestOccupied(i, j+cnt) <dist) {
				return false;
			}
			distance +=cellSizeY;
		}
		return true;
	}


	private static void cubeRestriction() { // assumir cube size = 20 / 100
		for(int i = 0; i < cellMatrix.length-1; i++) {
			for(int j =0; j < cellMatrix[0].length-1; j++) {
				if(cellMatrix[i][j].closestOccupied > cubeHeight-1) {
					//TODO dynamically calculate number of cells on big side
					if(checkOccupiedX(i, j, cubeHeight-1, 99)) {
						cellMatrix[i][j].cube = true;
					}
					if(checkOccupiedY(i, j, cubeHeight-1, 99)) {
						cellMatrix[i][j].cube = true;
					}						
				} else {
					cellMatrix[i][j].cube = false;
				}
			}
		}
	}


	private static void ballRestriction() { // assumir ball size = 50
		for(int i = 0; i < cellMatrix.length-1; i++) {
			for(int j =0; j < cellMatrix[0].length-1; j++) {
				if(cellMatrix[i][j].closestOccupied > 49) {
					cellMatrix[i][j].ball = true;
				} else {
					cellMatrix[i][j].ball = false;
				}
			}
		}
		
	}


	private static void paintCells(int x, int y, int width, int height) {
		Rectangle area = new Rectangle(x, y, width, height);
		
		for(int i = 0; i < cellMatrix.length-1; i++) {
			for(int j =0; j < cellMatrix[0].length-1; j++) {
				if(area.intersects((int)cellMatrix[i][j].topleft.getX(), (int) cellMatrix[i][j].topleft.getY(),
						(int) cellMatrix[i][j].sizeX, (int) cellMatrix[i][j].sizeY)) {
					cellMatrix[i][j].occupied =true;
				}
				
			}
		}
		
	}
}
