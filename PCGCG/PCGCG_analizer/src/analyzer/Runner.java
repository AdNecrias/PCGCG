package analyzer;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;

import protoGenerator.Block;
import protoGenerator.Gem;
import protoGenerator.Level;
import protoGenerator.PlayerComponent;
import draw.GraphPanel;
import draw.GraphNode;

public class Runner {

	public static int configMode = 0;
	public static int configExtraGemNumber = 2;
	public static String configPath = "";
	public static String configOutputPath = "";
	public static String readPath = "testLevel.xml";
	public static String writePath = "Out_testLevel.xml";
	
	public static boolean visual = false;

	public static void main(String[] args) throws Exception {
		String[] path = {"testLevel.xml","outputLevel.xml", readPath, writePath};
		int[] level = {3, 0};
		getConfig(path,level);
		configMode=level[1];
		configPath=path[0];
		configOutputPath=path[1];
		readPath = path[2];
		writePath= path[3];
		int levelsToGo = getNumLevels(configPath+readPath);
		int cnt = 1, levelsFailed=0;

		ArrayList<Level> levels = new ArrayList<Level>();
		if(level[0] == -1) {
			while(cnt < levelsToGo) {
				try {
					System.out.println("Analysing level "+ cnt);
					Cell[][] discCells = Discretalyzer.analyse(configPath + readPath, cnt);
					ArrayList<Node> n = Analyzer.analyse(configPath + readPath, cnt);
					System.out.println(" - Analysers done.");
					ArrayList<Node> ngems = generate(n, discCells);
					if(visual) {
						GraphPanel gp = new GraphPanel();
						boolean[] graphsDone = new boolean[1];
						graphsDone[0] = false;
						gp.fakeMain(null, graphsDone);
						DiscreteViewer.run(discCells);
						while(!graphsDone[0]) { //Wait for Graphs Panel to initialize
							wasteTime(1);
						}
						drawGraph(n, gp.graphPanel);
						generateDraw(ngems , gp.graphPanel);
					}
					levels.add(generateLevel(n, ngems, discCells));
					System.out.println("Writing level "+ cnt + " done.");
					cnt++;
				} catch (Exception e) {
					System.out.println("[ERROR!] Error reading level " + cnt + ", skipping.");
					System.out.println(e);
					for(StackTraceElement ste : e.getStackTrace()) {
						System.out.println(ste);
					}
					//throw e;
					levelsFailed++;
					cnt++;
				}
			}
		} else {
			System.out.println("Analysing level "+ level[0]);
			Cell[][] discCells = Discretalyzer.analyse(configPath + readPath, level[0]);
			ArrayList<Node> n = Analyzer.analyse(configPath + readPath, level[0]);
			ArrayList<Node> ngems = generate(n, discCells);
			if(visual) {
				GraphPanel gp = new GraphPanel();
				boolean[] graphsDone = new boolean[1];
				graphsDone[0] = false;
				gp.fakeMain(null, graphsDone);
				DiscreteViewer.run(discCells);
				while(!graphsDone[0]) { //Wait for Graphs Panel to initialize
					wasteTime(1);
				}
				drawGraph(n, gp.graphPanel);
				generateDraw(ngems , gp.graphPanel);
			}
			levels.add(generateLevel(n, ngems, discCells));
		}

		
		
		Level[] levelsArray = new Level[levels.size()];
		levelsArray = levels.toArray(levelsArray);
		protoGenerator.Drawer.drawLevels(configPath + writePath, levelsArray);
		System.out.println("End. "+ (--cnt-levelsFailed) +" levels written at: " + configPath);
	}

	private static int getNumLevels(String path) {
		File in = new File(path);
		BufferedReader input = null;
		boolean inLevel = false;
		int numberOfLevels = 0;
		try {
			input = new BufferedReader(new FileReader(in));
			String currentLine; 
			while ((currentLine = input.readLine()) != null) {
				String delims = "[\n\t\r<]+";
				String tokens[] = currentLine.split(delims);
				
				if(inLevel) {
					if(tokens[1].contains("/Level")) {
						inLevel= false;
					}
				} else if(tokens[1].contains("Level")) {
					inLevel= true;
					numberOfLevels++;
				}
					
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numberOfLevels;
	}

	public static Level generateLevel(ArrayList<Node> n, ArrayList<Node> ngems, Cell[][] discCells) {
		System.out.println(" Generating Level.");
		Level level = new Level();
		Random rnd = new Random(System.currentTimeMillis());

		for(Node node : n) {
			level.addComponent(node.block);
		}
		System.out.println(" - Blocks added.");
		Node cube = ngems.get(2), ball = ngems.get(3);
		int p1X =0, p1Y=0, p2X=0, p2Y=0;
		p1X= (int) cube.block.getLandingArea(100).getCenterX();
		p1Y= (int) cube.block.getLandingArea(100).getCenterY();
		p2X= (int) ball.block.getLandingArea(100).getCenterX();
		p2Y= (int) ball.block.getLandingArea(100).getCenterY();
		boolean placed = false;
		Node target = ngems.get(0);
		Node origin = ngems.get(1);
		Rectangle2D landingAreaBall = ball.block.getLandingArea(100);
		Rectangle2D landingAreaCube = cube.block.getLandingArea(100);
		
		p1X = trim(rnd.nextInt(((int)landingAreaCube.getWidth())) + landingAreaCube.getX()) ;
		p1Y = trim(-(rnd.nextInt(((int)landingAreaCube.getHeight()))) + landingAreaCube.getY());

		Cell tCell = getCell(discCells, (int)target.block.getLandingArea(100).getCenterX(), (int)target.block.getLandingArea(100).getCenterY());
		Cell oCell = getCell(discCells, (int)origin.block.getLandingArea(100).getCenterX(), (int)origin.block.getLandingArea(100).getCenterY());
		System.out.println(" - Placing Player1");
		boolean[][]	checkGrid = new boolean[discCells.length*(int)discCells[0][0].sizeX][discCells[0].length*(int)discCells[0][0].sizeY];
		int timeout = 0;
		final int timeoutValue = Integer.MAX_VALUE/10;
		while(!placed) {
			int x, y;
			x =trim(rnd.nextInt(((int)landingAreaCube.getWidth())) + landingAreaCube.getX());
			y =trim(-(rnd.nextInt(((int)landingAreaCube.getHeight()))) + landingAreaCube.getY());
			if(checkGrid[x][y]) {
				if(++timeout > timeoutValue) {
					System.out.println("[TIMEOUT] Timed out trying to place player 1. Player placement is compromised.");
					p1X = (int)(landingAreaCube.getCenterX());
					p1Y = (int)(landingAreaCube.getCenterY());
					break;
				}
				continue;
			} else {
				checkGrid[x][y] = true;
			}

			oCell = getCell(discCells, x, y);
			if(oCell.occupied) {
				continue;
			}
			p1X = (int)(oCell.topleft.getX() + oCell.sizeX/2);
			p1Y = (int)(oCell.topleft.getY() + oCell.sizeY/2);
			placed = true;
		}
		System.out.println(" - Placed.\n - Placing Player2");
		checkGrid = new boolean[discCells.length*(int)discCells[0][0].sizeX][discCells[0].length*(int)discCells[0][0].sizeY];
		timeout = 0;
		placed =false;
		while(!placed) {
			int x, y;
			
			x = Math.max(1, Math.min(1200, (int) (rnd.nextInt(((int)landingAreaBall.getWidth())) + landingAreaBall.getX())));
			y = Math.max(1, Math.min(768, (int) (-(rnd.nextInt(((int)landingAreaBall.getHeight()))) + landingAreaBall.getY())));
			if(checkGrid[x][y]) {
				if(++timeout > timeoutValue) {
					System.out.println("[TIMEOUT] Timed out trying to place player 2. Player placement is compromised.");
					p2X = (int)(landingAreaBall.getCenterX());
					p2Y = (int)(landingAreaBall.getCenterY());
					break;
				}
				continue;
			} else {
				checkGrid[x][y] = true;
			}
			
			oCell = getCell(discCells, x, y);
			if(tCell.ballArea != oCell.ballArea || oCell.occupied) {
				continue;
			}
			p2X = (int)(oCell.topleft.getX() + oCell.sizeX/2);
			p2Y = (int)(oCell.topleft.getY() + oCell.sizeY/2);
			placed = true;
		}
		if(p1X < p2X + 25 && p1X > p2X - 25)
			if(p1Y < p2Y + 25 && p1Y > p2Y - 25)
				p2Y -= 100;


		level.setPlayer1(new PlayerComponent(p2X, p2Y));
		level.setPlayer2(new PlayerComponent(p1X, p1Y));
		System.out.println(" - Players placed.\n - Reading Gems.");

		for(int i= 4; i < ngems.size(); i++) {
			Node a = ngems.get(i);
			int pX = (int) (a.block.getLandingArea(100).getX() + a.block.getLandingArea(100).getWidth());
			int pY = (int) (a.block.getLandingArea(100).getY() + a.block.getLandingArea(100).getHeight());
			pX -= rnd.nextInt((int) (pX - a.block.getLandingArea(100).getX()));
			pY -= rnd.nextInt((int) (pY - a.block.getLandingArea(100).getY()));
			level.addComponent(new Gem(pX, pY));
		}
		/*
			LevelPrinter lp = new LevelPrinter();
			lp.setLevel(level);
			lp.setOutputPath("Out_"+configPath);
			lp.print();*/
		return level;

	}

	private static int trim(double d) {
		if(d < 0) 
			return 0;
		if(d>1200) 
			return 1200;
		return (int) d;
	}

	private static void getConfig(String[] path, int[] level) {
		File in = new File("config.cfg");
		BufferedReader input = null;
		boolean useful= false;
		try {
			input = new BufferedReader(new FileReader(in));
			String currentLine; 
			while ((currentLine = input.readLine()) != null) {
				String delims = "[=]+";
				String tokens[] = currentLine.split(delims);
				if(tokens[0].equals("path")) {
					path[0] = tokens[1];
					useful = true;
				}
				if(tokens[0].equals("output_path")) {
					path[1] = tokens[1];
					useful = true;
				}
				if(tokens[0].equals("filename")) {
					path[2] = tokens[1];
					useful = true;
				}
				if(tokens[0].equals("output_filename")) {
					path[3] = tokens[1];
					useful = true;
				}
				if(tokens[0].equals("level")) {
					level[0] = Integer.parseInt(tokens[1]);
					useful = true;
				}
				if(tokens[0].equals("mode")) {
					level[1] = Integer.parseInt(tokens[1]);
					useful = true;
				}
				if(tokens[0].equals("visual")) {
					if(tokens[1].equals("true")) {
						visual = true;
					} else if(tokens[1].equals("false")) {
						visual = false;
					}
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(useful) return;
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(in));
			out.write("path=testLevel.xml\noutput_path=testLevel.xml\nlevel=2\n");
			out.write("//mode 0:Cooperative target, all reachable gems; 1:Cooperative target, only interesting(cooperation required) gems; 2: Discrete\nmode=0\n");
			out.write("\nvisual=false");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private static void generateDraw(ArrayList<Node> input,  GraphPanel gp) {

		Node target = input.get(0), origin = input.get(1);		
		GraphPanel.Gem gem = gp.addGem((target.block.getCoordX() + target.block.getSizeX()/2)/2,(target.block.getCoordY() + target.block.getSizeY()/2)/2 );
		gem.setColor(new Color(255,129,0));
		GraphPanel.Gem gem2 = gp.addGem((origin.block.getCoordX() + origin.block.getSizeX()/2)/2,(origin.block.getCoordY() + origin.block.getSizeY()/2)/2 );
		gem2.setColor(new Color(0,126,255).brighter());

		Node cube = input.get(2), ball = input.get(3);
		GraphPanel.Player p1 = gp.addPlayer((cube.block.getCoordX() + cube.block.getSizeX()/2)/2,(cube.block.getCoordY() + cube.block.getSizeY()/2)/2, "Cube" );
		GraphPanel.Player p2 = gp.addPlayer((ball.block.getCoordX() + ball.block.getSizeX()/2)/2 ,(ball.block.getCoordY() + ball.block.getSizeY()/2)/2, "Ball");	


		for(int i= 4; i < input.size(); i++) {
			Node a = input.get(i);
			gp.addGem((a.block.getCoordX() + a.block.getSizeX()/2)/2,(a.block.getCoordY() + a.block.getSizeY()/2)/2 -20 );
		}

	}

	/*
	 * Generates Gems
	 */
	private static ArrayList<Node> generate(ArrayList<Node> n, Cell[][] discCells) {
		System.out.println(" Gem placement\n - Finding cooperation target.");
		ArrayList<Node> output=new ArrayList<Node>();
		//find cooperation target.
		ArrayList<Node> targets=new ArrayList<Node>(), origins=new ArrayList<Node>();
		for(Node node : n) {
			for(Link link : node.links) {
				if(link.isBallWithCube()) {
					targets.add(link.target);
					origins.add(link.origin);
				}
			}
		}
		Node origin = null, target = null;

		Random rnd = new Random(System.currentTimeMillis());

		if(targets.size() == 0) {
			System.out.println("[INFO] No cooperative targets. picking random");

			Node rorigin=null;
			boolean exit = false;
			int timeout = 0, timeoutLimit = 100;
			while(!exit) {
				timeout++;
				rorigin = n.get(rnd.nextInt(n.size()));
				if(timeout > timeoutLimit+1) {
					System.out.println("[TIMEOUT] - No nodes, generation might fail.");
					targets.add(n.get(0));
					origins.add(n.get(0));
					break;
				}
				if(rorigin.links.size()==0) 
					continue;

				Link l = rorigin.links.get(rnd.nextInt(rorigin.links.size()));

				if(!l.target.equals(n.get(1))) {
					targets.add(l.target);
					origins.add(l.origin);
					exit = true;
					continue;
				}
				if(timeout > timeoutLimit) {
					System.out.println("[TIMEOUT]");
					targets.add(l.target);
					origins.add(l.origin);
					exit=true;
				}
			}
		}

		int randIndex = 0;
		randIndex = rnd.nextInt(targets.size());
		origin = origins.get(randIndex);
		target = targets.get(randIndex);

		output.add(target);
		output.add(origin);
		System.out.println(" - Origin and Target selected, mapping discrete map.\n - ConnectedComponentTwoPass begin.");
		ConnectedComponentTwoPass.pass(discCells);
		System.out.println("  -> ConnectedComponentTwoPass Done.\n - Selecting player location.");		
		/* Print Cells*//* * /
		for (int i = 0; i < discCells.length -1; i++) {
			for (int j = 0; j < discCells[0].length-1; j++) {
				System.out.print(discCells[i][j].ballArea + " ");
			}
			System.out.print("\n");
		}
		/**/

		// Place players

		ArrayList<Node> validBall=new ArrayList<Node>(), validCube=new ArrayList<Node>();
		validCube.add(origin);
		Cell tCell = getCell(discCells, (int)target.block.getLandingArea(100).getCenterX(), (int)target.block.getLandingArea(100).getCenterY());
		Cell oCell = getCell(discCells, (int)origin.block.getLandingArea(100).getCenterX(), (int)origin.block.getLandingArea(100).getCenterY());
		if(tCell.ballArea == oCell.ballArea) {
			validBall.add(origin);
		}

		for(Node node : n) {
			if(!node.equals(target)) {
				for(Link link : node.links) {
					if(link.target.equals(target) || validCube.contains(link.target)) {
						if(link.isCube()) {
							validCube.add(link.origin);
						}
					}
					if(link.target.equals(target) || validBall.contains(link.target)) {
						Cell nCell = getCell(discCells, (int)link.target.block.getLandingArea(100).getCenterX(), (int)link.target.block.getLandingArea(100).getCenterY());
						if(tCell.ballArea == nCell.ballArea) {
							if(link.isBall()) {
								validBall.add(link.origin);
							} else
								if(link.isBallWithCube()) {
									if(validCube.contains(link.origin)) {
										validBall.add(link.origin);
									}							
								}
						}
					}
				}
			}
		}
		
		int cindex = rnd.nextInt(validCube.size()), bindex = rnd.nextInt(validBall.size());
		Node cubeNode = validCube.get(cindex);
		if(cubeNode.equals(n.get(0))) {
			cubeNode = validCube.get((validCube.size()) == 1 ? 0: cindex+1);
		}
		Node ballNode = validBall.get(bindex);
		output.add(ballNode);
		if(ballNode.equals(n.get(0))) {
			ballNode = validBall.get((validBall.size()) == 1 ? 0: bindex+1);
		}
		output.add(ballNode);
		
		System.out.println(" - Player locations selected.");
		// chose gem nodes
		System.out.println(" - Picking possible Gem Nodes.");
		HashSet<Node> gemNodes = generateGems(ballNode, 8, discCells);

		gemNodes.remove(cubeNode);
		gemNodes.remove(ballNode);

		System.out.println("  -> " + gemNodes.size() + " possible.");
		output.addAll(gemNodes);

		System.out.println(" - Placing possible extra Gem nodes in cube only Areas.");
		ArrayList<Node> extraNodes = generateExtraNodes(ballNode,discCells);
		System.out.println("  -> " + extraNodes.size() + " possible, selecting " + configExtraGemNumber + ".");
		if(extraNodes.size() > 0)
			addSome(extraNodes, output, configExtraGemNumber, rnd);
		return output;
	}

	private static void addSome(ArrayList<Node> extraNodes,
			ArrayList<Node> output, int number, Random rnd) {
		for (int i = 0; i < number; i++) {
			int randomIndex = rnd.nextInt(extraNodes.size());
			output.add(extraNodes.get(randomIndex));
		}
		
	}

	private static ArrayList<Node> generateExtraNodes(Node origin,
			Cell[][] discCells) {
		ArrayList<Node> result = new ArrayList<Node>();
		Cell originCell = getCell(discCells, (int) origin.block.getLandingArea(100).getCenterX(), (int) origin.block.getLandingArea(100).getCenterY());
		for(int i=1; i< discCells.length-2; i++) {
			for(int j=1; j< discCells[0].length-2; j++) {
				if(discCells[i][j].ballArea != 0 && discCells[i][j].ballArea != originCell.ballArea) {
					if( discCells[i][j].closestOccupied < 150 && discCells[i][j].closestOccupied < discCells[i][j-1].closestOccupied) {
						result.add(new Node(new Block((int)discCells[i][j].topleft.getX(), (int)discCells[i][j].botright.getY(), (int)discCells[i][j].sizeX, 1)));
					}
				}
			}
		}
		return result;
	}

	public static void drawGraph(ArrayList<Node> nodes, GraphPanel gp) {
		ArrayList<GraphNode> gnodes = new ArrayList<GraphNode>();
		int counter = 0;

		for(Node n : nodes) {
			if(counter == 0)
				gnodes.add(gp.addNode(new Point(n.block.getCoordX()/2, n.block.getCoordY()/2), 35, Color.darkGray, GraphPanel.Kind.Square,
						new Rectangle(n.block.getCoordX()/2, n.block.getCoordY()/2, n.block.getSizeX()/2, n.block.getSizeY()/2)));
			else {
				gnodes.add(gp.addNode(new Point(n.block.getCoordX()/2, n.block.getCoordY()/2), 35, Color.gray, GraphPanel.Kind.Square,
						new Rectangle(n.block.getCoordX()/2, n.block.getCoordY()/2, n.block.getSizeX()/2, n.block.getSizeY()/2)));
			}			
			counter++;
		}
		counter = 0;
		for(Node c : nodes) {
			for(Link x : c.links) {
				Node target = x.target;
				int index = nodes.indexOf(target);
				gp.connect(gnodes.get(counter).node, gnodes.get(index).node, x.getInfo());
			}
			counter++;
		}


	}

	public static HashSet<Node> generateGems(Node origin, int depth, Cell[][] discCells) {
		HashSet<Node> gemNodes=new HashSet<Node>();
		Cell originCell = getCell(discCells, origin.block.getCoordX(), origin.block.getCoordY());
		
		for (Link l : origin.links) {
			if(configMode == 0) {
				if(l.getChannel() != 0) {
					gemNodes.add(l.target);
					if(depth > 0)
						gemNodes.addAll(generateGems(l.target, depth-1, discCells));
				}
			} else if(configMode == 1 || configMode == 3) {
				boolean simple = false;
				if(simple) {
					if(l.getChannel() > 3) {
						gemNodes.add(l.target);
						if(depth > 0)
							gemNodes.addAll(generateGems(l.target, depth-1, discCells));
					}					
				} else {
					Cell targetCell = getCell(discCells, (int)l.target.block.getLandingArea(10).getCenterX(), (int)l.target.block.getLandingArea(10).getCenterY());
					if(originCell.ballArea != targetCell.ballArea &&
							originCell.cubeArea == targetCell.cubeArea) {
						gemNodes.add(l.target);	
					} else if(l.getChannel() > 3 && originCell.ballArea == targetCell.ballArea) {
						gemNodes.add(l.target);
					}
					if(depth > 0)
						gemNodes.addAll(generateGems(l.target, depth-1, discCells));
				}
			}
		}
		return gemNodes;
	}

	private static Cell getCell(Cell[][] discCells, int coordX, int coordY) {
		for (int i = 0; i < discCells.length -1; i++) {
			for (int j = 0; j < discCells[0].length-1; j++) {
				Rectangle r = new Rectangle((int) discCells[i][j].topleft.getX(), (int) discCells[i][j].topleft.getY(), (int) discCells[i][j].sizeX, (int) discCells[i][j].sizeY);
				if(r.contains(coordX, coordY)) {
					return discCells[i][j];
				}
			}
		}
		Cell c = new Cell(-1000, -1000, -1000, -1000);
		c.occupied = true;
		return c;
	}

	public static void wasteTime(int t) {
		int counter = 0;
		int counter2 = 0;
		while(counter2 < t) {
			counter++;
			if(counter > Integer.MAX_VALUE - 20)
			{
				counter = 0;
				counter2++;
			}
		}
	}

}
