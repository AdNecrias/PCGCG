package pcgcg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;



public class GraphicInterface extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final Color cellBackground = new Color(230,230,230);
	private static final Color cellOccBackground = new Color(0x3e3e3e);
	private static final Color cellCubeBackground = new Color(0x9BCA3E);
	private static final Color cellCoopBackground = new Color(0x6d3eca);
	private static final Color cellNotBallBackground = new Color(0xED5314);
	private static final Color cellBorder = new Color(170,170,170);
	public static final int levelSizeX = 1280;
	public static final int levelSizeY = 800;
	public static final int cellSizeX = 16;
	public static final int cellSizeY = 16;
	
	public Overlay currentOverlay = Overlay.None;
	private Cell[][] cells;
	private PlayerPosition playerBall = new PlayerPosition();
	private PlayerPosition playerCube = new PlayerPosition();
	private ArrayList<Blob> blobs;
	
	public static int WIDE=levelSizeX, HIGH=levelSizeY;
	private static GraphicInterface instance = null;
	public static GraphicInterface instance() {
		if(instance == null)
			instance = new GraphicInterface();
		return instance;
	}
	
	private ButtonsBar control = new ButtonsBar();
	
	private class ButtonsBar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action ballOverlay = new OverlayComboAction("Overlay");
		private JComboBox<Overlay> overlayCombo = new JComboBox<Overlay>();
		private JButton loadButton = new JButton(new LoadAction());
		private JButton saveButton = new JButton(new SaveAction());
		private JButton generateButton = new JButton(new GenerateAction());

		ButtonsBar() {
			this.add(overlayCombo);
			overlayCombo.addActionListener(ballOverlay);
			for (Overlay k : Overlay.values()) {
				overlayCombo.addItem(k);
            }
			loadButton.setText("Load");
			this.add(loadButton);
			saveButton.setText("Save");
			this.add(saveButton);
			generateButton.setText("Regenerate");
			this.add(generateButton);
		}

		private class LoadAction extends AbstractAction {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				File f = new File("");
				JFileChooser fc = new JFileChooser(f.getAbsolutePath());
				int returnValue = fc.showOpenDialog(GraphicInterface.instance());
				if(returnValue == JFileChooser.APPROVE_OPTION) {
					GraphicInterface.instance().loadFile(fc.getSelectedFile());
				}
			}
		}
		private class SaveAction extends AbstractAction {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = new File("");
				JFileChooser fc = new JFileChooser(f.getAbsolutePath());
				int returnValue = fc.showSaveDialog(GraphicInterface.instance());
				if(returnValue == JFileChooser.APPROVE_OPTION) {
					GraphicInterface.instance().saveFile(fc.getSelectedFile());
				}
			}
		}
		private class GenerateAction extends AbstractAction {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				instance().generate();
			}
		}
	}
	private class OverlayComboAction extends AbstractAction {
		private static final long serialVersionUID = 298648541828787207L;
		
		Overlay type;
		
		public OverlayComboAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<Overlay> combo = (JComboBox<Overlay>) e.getSource();
            type = (Overlay) combo.getSelectedItem();
            if(instance != null) {
                instance.currentOverlay = type;
            }
            repaint();
		}
	}
	
	private GraphicInterface() {
		
	}
	
	public void generate() {
		for (int i = 0; i < cells.length-1; i++) {
			for (int j = 0; j < cells[0].length-1; j++) {
				evaluateCell(cells[i][j], i, j);
			}
		}
		reachabilityCube();
	}

	private void reachabilityCube() {
		Cell startCell = getCell(playerCube.x, playerCube.y);
		// TODO Auto-generated method stub		
	}

	private Cell getCell(int x, int y) {
		for (int i = 0; i < cells.length -1; i++) {
			for (int j = 0; j < cells[0].length-1; j++) {
				Rectangle r = new Rectangle((int) cells[i][j].topleft.getX(), (int) cells[i][j].topleft.getY(), (int) cells[i][j].sizeX, (int) cells[i][j].sizeY);
				if(r.contains(x, y)) {
					return cells[i][j];
				}
			}
		}
		return null;
	}

	private void evaluateCell(Cell cell, int x, int y) {
		if(x>1&&x<levelSizeX-2&&y>1&&y<levelSizeY-2) { //if not in corner
			if(!cell.occupied) {
				if(eightConnected(x,y))
					cell.fitsCube = true;
				if(eightConnected(x-1,y-1)&&eightConnected(x-1,y+1)&&eightConnected(x+1,y-1)&&eightConnected(x+1,y+1)) {
					cell.fitsBall=true;
				}
			}
		}
		/* Paint above
		if(cell.occupied) {
			Cell targetCell;
			int ty = 1;
			if(y-ty > 0	) {
				targetCell = cells[x][y-ty];
				while(!targetCell.occupied) {
					if(ty>29) {
						break;
					}
					if(x < 1 || x > levelSizeX-1)
						break;
					if(cells[x-1][y-ty].occupied || cells[x+1][y-ty].occupied) {
						//do nothing
					} else {
						if(ty<6)
							targetCell.cube = true;
						targetCell.ball = true;
					}
					ty++;
					if(y-ty > 0	) {
						targetCell = cells[x][y-ty];
					} else
						break;
				}
			}
		}*/
	}

	private boolean eightConnected(int x, int y) {
		if(		cells[x-1][y-1].occupied ||
				cells[x-1][y].occupied ||
				cells[x-1][y+1].occupied ||
				cells[x][y-1].occupied ||
				cells[x][y+1].occupied ||
				cells[x+1][y-1].occupied ||
				cells[x+1][y].occupied ||
				cells[x+1][y+1].occupied)
			return false;
		return true;
	}

	public void saveFile(File selectedFile) {
		// TODO Auto-generated method stub
		
	}

	public void loadFile(File selectedFile) {
		Cell[][] result=freshCells();
		int level = selectLevel(selectedFile);
		instance().playerBall.active = false;
		instance().playerCube.active = false;
		
		BufferedReader input = null;
		boolean inLevel = false;
		boolean inBlackObstacles = false;
		try {
			input = new BufferedReader(new FileReader(selectedFile));
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
					if(tokens[1].contains("SquareStartingPosition")) {
						String[] bvalues = tokens[1].split("[\"]");
						bvalues[0] = bvalues[0].substring("SquareStartingPosition ".length()-1, bvalues[0].length());
						int x=0, y=0;
						for(int i =0; i < 4; i+=2) {
							if(bvalues[i].equals(" X=")) {
								x = Integer.parseInt(bvalues[i+1]);
							}
							if(bvalues[i].equals(" Y=")) {
								y = Integer.parseInt(bvalues[i+1]);
							}
						}
						instance().playerCube.x = x;
						instance().playerCube.y = y;
						instance().playerCube.active = true;
					}
					if(tokens[1].contains("BallStartingPosition")) {
						String[] bvalues = tokens[1].split("[\"]");
						bvalues[0] = bvalues[0].substring("BallStartingPosition ".length()-1, bvalues[0].length());
						int x=0, y=0;
						for(int i =0; i < 4; i+=2) {
							if(bvalues[i].equals(" X=")) {
								x = Integer.parseInt(bvalues[i+1]);
							}
							if(bvalues[i].equals(" Y=")) {
								y = Integer.parseInt(bvalues[i+1]);
							}
						}
						instance().playerBall.x = x;
						instance().playerBall.y = y;
						instance().playerBall.active = true;
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
							paintCells(result, x,y,width,height);
						}
					}
				}
			}
			paintBorders(result);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		GraphicInterface.instance().setCells(result);
		repaint();
	}
	
	private void paintCells(Cell[][] cellMatrix, int x, int y, int width, int height) {
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
	private void paintBorders(Cell[][] cellMatrix) {
		paintCells(cellMatrix,40,760,1200,40);
		paintCells(cellMatrix,40,0,1200,40);
		paintCells(cellMatrix,0,0,40,800);
		paintCells(cellMatrix,1240,0,40,800);
		
	}

	private int selectLevel(File selectedFile) {
		ArrayList<Integer> levels = new ArrayList<Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(selectedFile));
			String currentLine;
			boolean inLevel = false;
			while((currentLine = br.readLine()) != null) {
				String delims = "[\n\t\r<]+";
				String tokens[] = currentLine.split(delims);
				if (tokens[1].contains("Levels")) 
					continue;
				if(inLevel) {
					if (tokens[1].contains("/Level")) {
						inLevel = false;
					}
				} else if (tokens[1].contains("Level")) {
					inLevel= true;
					String lvlDelims = "(Level)|[><\n\t\r]";
					String lvlTokens[] = currentLine.split(lvlDelims);
					levels.add(Integer.parseInt(lvlTokens[2]));
				}
			}

			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("[ERROR] Error finding file" + selectedFile);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[ERROR] error reading file" + selectedFile);
			e.printStackTrace();
		}
		Integer[] levelsArray = new Integer[levels.size()];
		for (int i = 0; i < levels.size(); i++) {
			levelsArray[i] = levels.get(i);
		}
		JComboBox<Integer> comboBox = new JComboBox<Integer>(levelsArray);
		String choice = "Choose a Level";
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(comboBox);
		int result = JOptionPane.showConfirmDialog(GraphicInterface.instance(), panel, choice, JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.OK_OPTION) {
			return levels.get(comboBox.getSelectedIndex());
		}
		return 0;
	}

	public static void run() {
		EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame f = new JFrame("PCGCG Viewer");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                if(instance == null)
                	instance = new GraphicInterface();
                instance.initialize();
                f.add(instance.control, BorderLayout.NORTH);
                f.add(new JScrollPane(instance), BorderLayout.CENTER);
                f.pack();
                f.setLocationByPlatform(true);
                f.setVisible(true);
            }
        });
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(new Color(51,51,51));
        g.fillRect(0, 0, getWidth(), getHeight());
        drawCells(g);
        drawPlayers(g);
	}
	
	private void drawPlayers(Graphics g) {
		int border = 4;
		if(playerBall.active) {
			g.setColor(Color.black);
			g.fillOval(playerBall.x, playerBall.y, 80, 80);
			g.setColor(new Color(255,215,0)); // yellow
			g.fillOval(playerBall.x+border/2, playerBall.y+border/2, 80-border, 80-border);
		}
		if(playerCube.active) {
			g.setColor(Color.black);
			g.fillRect(playerCube.x, playerCube.y, 80, 80);
			g.setColor(new Color(0,100,0)); // green
			g.fillRect(playerCube.x+border/2, playerCube.y+border/2, 80-border, 80-border);
		}
	}

	@Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDE, HIGH);
    }
	
	private void initialize() {
		instance.initializeCells();
	}

	private void drawCells(Graphics g) {
		for(int i = 0; i < cells.length-1; i++) {
			for(int j =0; j < cells[0].length-1; j++) {
				g.setColor(cellBackground); // default
				if(cells[i][j].occupied())
					g.setColor(cellOccBackground);
				else {
					int dist = Math.max(0,Math.min(cells[i][j].closestOccupied*1, 255));
					Color distColor = new Color(255, dist, dist);
					Color toUse = cellBackground;
					if(currentOverlay == Overlay.Distance)
						toUse = distColor;
					if(currentOverlay == Overlay.FitsBall && cells[i][j].fitsBall) {
						toUse = cellNotBallBackground;
					}
					if(currentOverlay == Overlay.FitsCube && cells[i][j].fitsCube) {
						toUse = cellCubeBackground;
					}
					if(currentOverlay == Overlay.Coop && cells[i][j].coop) {
						toUse = cellCoopBackground;
					}
					
					g.setColor(toUse);
				}
				g.fillRect((int)cells[i][j].topleft.getX(), (int)cells[i][j].topleft.getY(), (int)cells[i][j].sizeX, (int)cells[i][j].sizeY);
				g.setColor(cellBorder);
				g.drawRect((int)cells[i][j].topleft.getX(), (int)cells[i][j].topleft.getY(), (int)cells[i][j].sizeX, (int)cells[i][j].sizeY);
			}
		}
		
	}
	
	public enum Overlay {
		None, FitsBall, FitsCube, Distance, Coop
	}

	public Cell[][] getCells() {
		return cells;
	}

	public void setCells(Cell[][] cells) {
		this.cells = cells;
	}
	
	public Cell setCell(int x, int y, Cell cell) {
		Cell previous = this.cells[x][y];
		this.cells[x][y] = cell;
		return previous;
	}
	
	public void initializeCells() {
		instance.cells = new Cell[levelSizeX/cellSizeX+1][levelSizeY/cellSizeY+1];
		for(int x = 0, i = 0; x < levelSizeX; x += cellSizeX) {
			for(int y = 0, j =0; y < levelSizeY; y += cellSizeY) {
				instance.cells[i][j] = new Cell(x, y, x+cellSizeX, y+cellSizeY);
				j++;
			}
			i++;
		}
	}
	
	public Cell[][] freshCells() {
		Cell[][] fresh= new Cell[levelSizeX/cellSizeX+1][levelSizeY/cellSizeY+1];
		for(int x = 0, i = 0; x < levelSizeX; x += cellSizeX) {
			for(int y = 0, j =0; y < levelSizeY; y += cellSizeY) {
				fresh[i][j] = new Cell(x, y, x+cellSizeX, y+cellSizeY);
				j++;
			}
			i++;
		}
		return fresh;
	}
	
	public static void run(Cell[][] discCells) {
		instance = new GraphicInterface();
		instance.setCells(discCells);
		WIDE = (int) discCells[0][0].sizeX * (discCells.length -1);
		HIGH = (int) discCells[0][0].sizeY * (discCells[0].length-1);
		run();
	}
	
	private class PlayerPosition {
		public int x = 0;
		public int y = 0;
		boolean active = false;
	}

}
