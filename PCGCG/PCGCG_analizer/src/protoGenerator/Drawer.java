package protoGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Drawer {
	
	public static int height = 300;
	
	public static void drawLevels(String path, Level[] levels) {
		int currlevel = 1;
		File out = new File(path);
		BufferedWriter output;
		
		try {
		output = new BufferedWriter(new FileWriter(out));		
		output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		output.write(" <Levels>\n");
		
		for( Level level : levels) {
			ArrayList<Component> components = level.getComponents();
			ArrayList<Gem> collectibles = new ArrayList<Gem>();
			ArrayList<Block> blackObstacles = new ArrayList<Block>();
			ArrayList<GreenBlock> greenObstacles = new ArrayList<GreenBlock>();
			ArrayList<YellowBlock> yellowObstacles = new ArrayList<YellowBlock>();
			for(Component c : components) {
				if(c.getClass().getSimpleName().equals("Gem")) {
					collectibles.add((Gem)c);
				}
				
				if(c.getClass().getSimpleName().equals("Block")) {
					blackObstacles.add((Block)c);
				}
				if(c.getClass().getSimpleName().equals("GreenBlock")) {
					greenObstacles.add((GreenBlock)c);
				}
				if(c.getClass().getSimpleName().equals("YellowBlock")) {
					yellowObstacles.add((YellowBlock)c);
				}
			}
			
			
				output.write("  <Level"+ currlevel + ">\n");			
				output.write("  <Description />\n");
				output.write("  <Tips />\n");
				output.write("  <BallStartingPosition X=\"" + level.getPlayer1().getCoordX() + "\" Y=\"" + level.getPlayer1().getCoordY() + "\" />\n");
				output.write("  <SquareStartingPosition X=\"" + level.getPlayer2().getCoordX() + "\" Y=\"" + level.getPlayer2().getCoordY() + "\" />\n");
				output.write("  <Collectibles>\n");
				for(Gem g : collectibles) {
					output.write("   <Collectible X=\"" + g.getCoordX() + "\" Y=\"" + g.getCoordY() + "\" />\n");
				}			
				output.write("  </Collectibles>\n");
				
				output.write("  <GreenObstacles>\n");
				for(GreenBlock g : greenObstacles) {
					output.write("   <Obstacle X=\"" + g.getCoordX() + "\" Y=\"" + g.getCoordY() + "\" width=\"" + g.getSizeX() + "\" height=\"" + g.getSizeY() + "\" centered=\"false\" />\n");
				}	
				output.write("  </GreenObstacles>\n");
				
				output.write("  <BlackObstacles>\n");
				for(Block g : blackObstacles) {
					output.write("   <Obstacle X=\"" + g.getCoordX() + "\" Y=\"" + g.getCoordY() + "\" width=\"" + g.getSizeX() + "\" height=\"" + g.getSizeY() + "\" centered=\"false\" />\n");
				}			
				output.write("  </BlackObstacles>\n");
				
				output.write("  <YellowObstacles>\n");
				for(YellowBlock g : yellowObstacles) {
					output.write("   <Obstacle X=\"" + g.getCoordX() + "\" Y=\"" + g.getCoordY() + "\" width=\"" + g.getSizeX() + "\" height=\"" + g.getSizeY() + "\" centered=\"false\" />\n");
				}	
				output.write("  </YellowObstacles>\n");
				
				output.write("  <GreenElevators />\n");
				output.write("  <BlackElevators />\n");
				output.write("  <YellowElevators />\n");
				output.write("  <OrangeElevators />\n");

				output.write("  <HighScores />\n");
				output.write(" </Level"+ (currlevel) + ">\n");
				currlevel++;
				
		}
		output.write("</Levels>\n");
		output.close();
		} catch (IOException e) {
			System.out.println("Path: "+ path);
			e.printStackTrace();
		}
	}
	
	public static void draw(String path) {
		int currlevel = 1;
		File in = new File(path);
		String outpath = path.replace(".lvl", ".xml");
		File out = new File(outpath);
		BufferedReader input;
		BufferedWriter output;
		
		ArrayList<PlayerComponent> players = new ArrayList<PlayerComponent>();
		ArrayList<Gem> collectibles = new ArrayList<Gem>();
		ArrayList<Block> blackObstacles = new ArrayList<Block>();
		
		
		try {
			String currentLine; 
			input = new BufferedReader(new FileReader(in));
			while ((currentLine = input.readLine()) != null) {
				String delims = "[ (),]+";
				String tokens[] = currentLine.split(delims);
				
				int tok = 0;
				int toklength = tokens.length;
				String token = "";
				while(tok < toklength) {
					token = tokens[tok];
					
					if(token.equals("PlayerComponent")) {
						players.add(new PlayerComponent(Integer.parseInt(tokens[tok+1]), Integer.parseInt(tokens[tok+2])));
						tok +=2;
					}
					if(token.equals("Gem")) {
						collectibles.add(new Gem(Integer.parseInt(tokens[tok+1]), Integer.parseInt(tokens[tok+2])));
						tok +=2;
					}
					if(token.equals("Block")) {
						
						int posX = Integer.parseInt(tokens[tok+1]);
						int posY = Integer.parseInt(tokens[tok+2]);
						int sizeX = Integer.parseInt(tokens[tok+3]);
						int sizeY = Integer.parseInt(tokens[tok+4]);
						
						blackObstacles.add(new Block(posX, posY, sizeX, sizeY));
						
						tok +=4;
					}
					if(token.equals("Ënd")) {
						//currlevel +=1;
					}
					
					tok++;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			output = new BufferedWriter(new FileWriter(out));
			
			output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			output.write(" <Levels>\n");
			output.write("  <Level"+ currlevel + " >\n");			
			output.write("  <Description />\n");
			output.write("  <Tips />\n");
			output.write("  <BallStartingPosition X=\"" + players.get(0).getCoordX() + "\" Y=\"" + players.get(0).getCoordY() + "\" />\n");
			output.write("  <SquareStartingPosition X=\"" + players.get(1).getCoordX() + "\" Y=\"" + players.get(1).getCoordY() + "\" />\n");
			output.write("  <Collectibles>\n");
			for(Gem g : collectibles) {
				output.write("   <Collectible X=\"" + g.getCoordX() + "\" Y=\"" + g.getCoordY() + "\" />\n");
			}			
			output.write("  </Collectibles>\n");
			output.write("  <GreenObstacles />\n");
			output.write("  <BlackObstacles>\n");
			for(Block g : blackObstacles) {
				output.write("   <Obstacle X=\"" + g.getCoordX() + "\" Y=\"" + g.getCoordY() + "\" width=\"" + g.getSizeX() + "\" height=\"" + g.getSizeY() + "\" centered=\"true\" />\n");
			}			
			output.write("  </BlackObstacles>\n");
			output.write("  <YellowObstacles />\n");
			output.write("  <GreenElevators />\n");
			output.write("  <BlackElevators />\n");
			output.write("  <YellowElevators />\n");
			output.write("  <OrangeElevators />\n");

			output.write("  <HighScores />\n");
			output.write(" </Level"+ (currlevel) + ">\n");
			output.write("</Levels>\n");
			
			
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void drawOld(String path) {
		
		File in = new File(path);
		File out = new File(path + "d");
		BufferedReader input;
		BufferedWriter output;
		
		char [][] mat = new char[1000][height];
		
		for(int i = 0; i < 1000; i++) {
			for(int j = 0; j < height; j++) {
				mat[i][j] = ' ';
			}
		}
		
		
		try {
			String currentLine; 
			input = new BufferedReader(new FileReader(in));
			while ((currentLine = input.readLine()) != null) {
				String delims = "[ (),]+";
				String tokens[] = currentLine.split(delims);
				
				int tok = 0;
				int toklength = tokens.length;
				String token = "";
				while(tok < toklength) {
					token = tokens[tok];
					
					if(token.equals("PlayerComponent")) {
						mat[Integer.parseInt(tokens[tok+1])][Integer.parseInt(tokens[tok+2])] = 'P';
						tok +=2;
					}
					if(token.equals("Gem")) {
						mat[Integer.parseInt(tokens[tok+1])][Integer.parseInt(tokens[tok+2])] = 'G';
						tok +=2;
					}
					if(token.equals("Block")) {
						
						int posX = Integer.parseInt(tokens[tok+1]);
						int posY = Integer.parseInt(tokens[tok+2]);
						int sizeX = Integer.parseInt(tokens[tok+3]);
						int sizeY = Integer.parseInt(tokens[tok+4]);
						
						for(int i = posX - (sizeX)/2; i < posX + (sizeX)/2; i++) {
							for(int j = posY - (sizeY)/2; j < posY + (sizeY)/2; j++) {
								mat[i][j] = 'B';
							}
						}
						
						
						tok +=4;
					}
					
					tok++;
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			output = new BufferedWriter(new FileWriter(out));
			String s = "";
				
			for(int i = 0; i < 1000; i++) {
				for(int j = 0; j < height; j++) {
					s += mat[i][j];
				}
				output.write(s + "\n");
				s = "";
			}
			
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
