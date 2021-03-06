package pcgcg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import protoGenerator.Level;
import protoGenerator.PlayerComponent;

public class GraphicInterface extends JComponent {
	private static final long serialVersionUID = 1L;
	public static final Color cellBackground = new Color(230,230,230);
	public static final Color cellOccBackground = new Color(0x3e3e3e);
	public static final Color cellCubeBackground = new Color(0x9BCA3E);
	public static final Color cellCoopBackground = new Color(0x6d3eca);
	public static final Color cellNotBallBackground = new Color(0xED5314);
	public static final Color cellBorder = new Color(170,170,170);
	public static final int levelSizeX = 1280;
	public static final int levelSizeY = 800;
	public static final int cellSizeX = 16;
	public static final int cellSizeY = 16;
	public static long seed = 1;

	public Overlay currentOverlay = Overlay.None;
	private Cell[][] cells;
	private PlayerPosition playerBall = new PlayerPosition();
	private ArrayList<Gem> gems;
	private PlayerPosition playerCube = new PlayerPosition();
	private ArrayList<Blob> blobs;
	private float bias = 0.0f;
	private float difficulty = 0.0f;
	private float coop = 0.0f;
	private int gemsToGenerate = 4;

	private int[] heuristics = {
			-1, -1, -1
	};
	public void setHeuristics(int balance, int colaboration, int difficulty) {
		heuristics[0] = balance;
		heuristics[1] = colaboration;
		heuristics[2] = difficulty;
	}
	public int getHeuristicBalance(){
		return heuristics[0];
	}
	public int getHeuristicColaboration(){
		return heuristics[1];
	}
	public int getHeuristicDifficulty(){
		return heuristics[2];
	}
	public int setHeuristicBalance(int b){
		return heuristics[0] = b;
	}
	public int setHeuristicColaboration(int c){
		return heuristics[1] = c;
	}
	public int setHeuristicDifficulty(int d){
		return heuristics[2] = d;
	}

	private Rectangle mouseRect = new Rectangle();
	private Point mousePt = new Point();

	public static int WIDE=levelSizeX, HIGH=levelSizeY;
	private static GraphicInterface instance = null;
	public static GraphicInterface instance() {
		if(instance == null)
			instance = new GraphicInterface();
		return instance;
	}

	private ButtonsBar control = new ButtonsBar();
	private ToolBar tool = new ToolBar();
	private Tool toolSelected = Tool.None;

	public enum Tool {
		None, Cube, Ball, Paint, Gem;
	}

	private class ToolBar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private JLabel hLabel = new HeuristicLabel();
		private JPanel hPanel = new JPanel();
		private JSpinner gemSpinner = new JSpinner(new SpinnerNumberModel(gemsToGenerate, 1, 100, 1));
		private JSpinner seedSpinner = new JSpinner(new SpinnerNumberModel(1L, Long.MIN_VALUE+1, Long.MAX_VALUE-1, 1L));
		private JSlider biasSlider = new JSlider(-10, 10, 0);
		private JSlider colabSlider = new JSlider(-10, 10, 0);
		private JSlider difficultySlider = new JSlider(0, 3, 0);
		private ChangeListener changeListener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent ce)
			{
				JSlider source = (JSlider)ce.getSource();
				if (!source.getValueIsAdjusting()) {
					instance().bias = ((float)instance().tool.biasSlider.getValue())/10.0f;
					instance.coop = ((float)instance().tool.colabSlider.getValue())/10.0f;
					instance().difficulty = ((float)instance().tool.difficultySlider.getValue());
				}	
			}
		};
		private ChangeListener gemChangeListener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent ce)
			{
				instance().gemsToGenerate = (int) gemSpinner.getValue();
				if(seedSpinner.getValue().getClass().equals(Double.class)) {
					GraphicInterface.seed = ((Double) seedSpinner.getValue()).longValue();
				} else
					GraphicInterface.seed = (long) seedSpinner.getValue();
			}
		};

		ToolBar() {
			super(JToolBar.VERTICAL);

			JPanel gemPanel = new JPanel();
			gemPanel.setLayout(new GridLayout(3, 3));
			gemPanel.add(new JLabel("Seed: "));
			seedSpinner.setEditor(new JSpinner.NumberEditor(seedSpinner, "#"));
			((JSpinner.NumberEditor)seedSpinner.getEditor()).getTextField().setColumns(12);
			seedSpinner.addChangeListener(gemChangeListener);
			gemPanel.add(seedSpinner);
			gemPanel.add(new JLabel("N. Gems: "));
			gemSpinner.addChangeListener(gemChangeListener);
			gemPanel.add(gemSpinner);
			this.add(gemPanel);

			
			Hashtable<Integer, JLabel> labels1 = new Hashtable<Integer, JLabel>();
			labels1.put(-10, new JLabel("Non-colaborative"));
			labels1.put(10, new JLabel("Colaborative"));
			colabSlider.setLabelTable(labels1);
			colabSlider.setPaintLabels(true);
			colabSlider.addChangeListener(changeListener);
			this.add(colabSlider);
			Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
			labels.put(-10, new JLabel("Cube"));
			labels.put(0, new JLabel("Balanced"));
			labels.put(10, new JLabel("Ball"));
			biasSlider.setLabelTable(labels);
			biasSlider.setPaintLabels(true);
			biasSlider.addChangeListener(changeListener);
			this.add(biasSlider);
			Hashtable<Integer, JLabel> dlabels = new Hashtable<Integer, JLabel>();
			dlabels.put(0, new JLabel("Easier"));
			dlabels.put(3, new JLabel("Harder"));
			difficultySlider.setLabelTable(dlabels);
			difficultySlider.setPaintLabels(true);
			difficultySlider.addChangeListener(changeListener);
			this.add(difficultySlider);
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new GridLayout(24, 2));

			for (Tool t : Tool.values()) {
				JButton b = new JButton(new ToolAction(t));
				b.setText(t.toString());
				buttonsPanel.add(b);
			}
			this.add(buttonsPanel);

			hPanel.add(hLabel);
			this.add(hPanel);
			hPanel.setMaximumSize(new Dimension(308, 1440));
		}

		private class HeuristicLabel extends JLabel {
			private static final long serialVersionUID = 1L;
			private GraphicInterface gi;

			public HeuristicLabel() {
				super();
				this.setText("Test");
			}

			@Override
			public void paint(Graphics g) {
				gi = GraphicInterface.instance();
				int div = Math.max(1, gems.size());
				this.setText("Balance: " + (float)gi.getHeuristicBalance()/div + " Colaboration: " + (float)gi.getHeuristicColaboration()/div + " Difficulty: " + (float)gi.getHeuristicDifficulty()/div);
				super.paint(g);
			}

		}

		private class ToolAction extends AbstractAction {
			private static final long serialVersionUID = 1L;
			Tool action;
			ToolAction(Tool action) {
				this.action = action;
			}
			@Override
			public void actionPerformed(ActionEvent arg0) {
				toolSelected = action;
			}
		}
	}

	private class ButtonsBar extends JToolBar {
		private static final long serialVersionUID = 1L;
		private Action ballOverlay = new OverlayComboAction("Overlay");
		private JComboBox<Overlay> overlayCombo = new JComboBox<Overlay>();
		private JButton loadButton = new JButton(new LoadAction());
		private JButton saveButton = new JButton(new SaveAction());
		private JButton generateButton = new JButton(new GenerateAction());
		private JButton generateNewSeedButton = new JButton(new GenerateNewSeedAction());
		private JButton matchHeuristicsButton = new JButton(new MatchHeuristicsAction());

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
			generateNewSeedButton.setText("Regenerate with a New Seed");
			this.add(generateNewSeedButton);
			matchHeuristicsButton.setText("Regenerate until matching Heuristics");
			this.add(matchHeuristicsButton);

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
				repaint();
			}
		}
		private class GenerateNewSeedAction extends GenerateAction {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				instance().regenSeed();
				super.actionPerformed(arg0);
			}
		}
		private class GenerateAction extends AbstractAction {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				instance().generate();
				repaintAll();
			}
		}
		private class MatchHeuristicsAction extends AbstractAction {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				matchHeuristics();
				repaintAll();
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
		this.setOpaque(true);
		this.addMouseListener(new MouseHandler());
		this.addMouseMotionListener(new MouseMotionHandler());
	}

	private class MouseHandler extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			mouseRect.setBounds(0, 0, 0, 0);
			if (e.isPopupTrigger()) {
				//showPopup(e);
			}
			e.getComponent().repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mousePt = e.getPoint();

			Cell cell;

			if((cell = overCell(mousePt.x, mousePt.y)) != null) {
				//do stuff with Tool
				if(toolSelected == Tool.Ball) {
					playerBall.active = true;
					playerBall.x = mousePt.x;
					playerBall.y = mousePt.y;
				} else if (toolSelected == Tool.Cube) {
					playerCube.active = true;
					playerCube.x = mousePt.x;
					playerCube.y = mousePt.y;
				} else if (toolSelected == Tool.Paint) {
					cell.occupied = !cell.occupied;
				} else if (toolSelected == Tool.Gem) {
					gems.add(new Gem(mousePt.x, mousePt.y, cell));
				}

			}

			e.getComponent().repaint();


		}

		public Cell overCell(int mousex, int mousey) {
			return getCell(mousex, mousey);
		}
	}

	private class MouseMotionHandler extends MouseMotionAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
		}
	}

	private void matchHeuristics() {
		int balance = tool.biasSlider.getValue(), difficulty = 200, colaboration = tool.colabSlider.getValue();
		int difficultyMarginMultiplier = 200;
		int margin = 1;
		boolean searching = true;
		int timeout = 0;
		switch (tool.difficultySlider.getValue())	{
		case 0:
			difficulty = 200;
			break;
		case 1:
			difficulty = 350;
			break;
		case 2:
			difficulty = 500;
			break;
		case 3:
			difficulty = 750;
			break;
		}
		
		while(searching) {
			generate();
			System.out.println(balance +", "+colaboration+", "+ difficulty + " vs " + getHeuristicBalance() + ", " + getHeuristicColaboration() + ", " + getHeuristicDifficulty()/gemsToGenerate);
			if(getHeuristicBalance() > balance - margin && getHeuristicBalance() < balance + margin) {
				if(getHeuristicColaboration() > colaboration - margin && getHeuristicBalance() < colaboration + margin) {
					//if(getHeuristicDifficulty()/gemsToGenerate > difficulty - margin*difficultyMarginMultiplier && getHeuristicDifficulty()/gemsToGenerate < difficulty + margin*difficultyMarginMultiplier) {
						searching = false;
						System.out.println("Done!");
					//}
				}
			}
			if(timeout++ > 9999) {
				searching = false;
			}
			if(searching) {
				regenSeed();
			}
			repaintAll();
		}
		System.out.println("Done in " + timeout + " regenerations.");
	}


public void generate() {
	clearGems();
	for (int i = 0; i < cells.length-1; i++) {
		for (int j = 0; j < cells[0].length-1; j++) {
			clearInfo(cells[i][j]);
		}
	}
	repaint();
	for (int i = 0; i < cells.length-1; i++) {
		for (int j = 0; j < cells[0].length-1; j++) {
			evaluateCell(cells[i][j], i, j);
		}
	}
	reachabilityCube();
	reachabilityBall();
	reachabilityCoop();
	generateGems();
	evaluateHeuristics();
	repaintAll();
}



private void repaintAll() {
	repaint();
	tool.repaint();
	control.repaint();
}
private void evaluateHeuristics() {
	int colabH=0, diffiH=0, balanH=0;
	for(Gem g : gems) {
		int diff = 0;

		if(g.cell.reachCube) {
			balanH -=1;
		} else {
			if(!g.cell.coopExclusive) {
				balanH +=1;
			}
		}
		if(g.cell.coopExclusive) {
			colabH +=1;
			diff += (30 - g.cell.maxCoopJumpStrenght);
		} else {
			colabH -=1;
			diff += Math.min(23 - g.cell.maxJumpStrenght, 30 - g.cell.maxCoopJumpStrenght);
		}
		//A* distance?
				diff+= distanceToPlayer(g.cell);
				diffiH +=diff;
	}
	setHeuristics(balanH, colabH, diffiH);
}
private int distanceToPlayer(Cell cell) {
	if(cell.ballExclusive) {
		return distance(playerBall.x, playerBall.y, cell.x, cell.y);
	} else if (cell.cubeExclusive) {
		return distance(playerCube.x, playerCube.y, cell.x, cell.y);
	} else {
		return Math.min(distance(playerBall.x, playerBall.y, cell.x, cell.y), distance(playerCube.x, playerCube.y, cell.x, cell.y));
	}
}

private int distance(int x, int y, int x2, int y2) {
	int dx = Math.abs(x - x2);
	int dy = Math.abs(y - y2);
	Double dist = Math.sqrt(dx*dx + dy*dy);
	return dist.intValue();
}
private void clearGems() {
	gems.clear();
}

private boolean checkEight(int x, int y, Function<Cell, Boolean> function) {
	if(
			function.apply(cells[x-1][y-1]) &&
			function.apply(cells[x][y-1]) &&
			function.apply(cells[x+1][y-1]) &&
			function.apply(cells[x-1][y]) &&
			function.apply(cells[x+1][y]) &&
			function.apply(cells[x-1][y+1]) &&
			function.apply(cells[x][y+1]) &&
			function.apply(cells[x+1][y+1])
			) {
		return true;
	}
	return false;
}
private boolean checkAnyEight(int x, int y, Function<Cell, Boolean> function) {
	if(
			function.apply(cells[x-1][y-1]) ||
			function.apply(cells[x][y-1]) ||
			function.apply(cells[x+1][y-1]) ||
			function.apply(cells[x-1][y]) ||
			function.apply(cells[x+1][y]) ||
			function.apply(cells[x-1][y+1]) ||
			function.apply(cells[x][y+1]) ||
			function.apply(cells[x+1][y+1])
			) {
		return true;
	}
	return false;
}
@SuppressWarnings("unused")
private boolean checkSixteen(int x, int y, Function<Cell, Boolean> function) {
	if(
			function.apply(cells[x-2][y-2]) &&
			function.apply(cells[x-1][y-2]) &&
			function.apply(cells[x][y-2]) &&
			function.apply(cells[x+1][y-2]) &&
			function.apply(cells[x+2][y-2]) &&
			function.apply(cells[x-2][y-1]) &&
			function.apply(cells[x+2][y-1]) &&
			function.apply(cells[x-2][y]) &&
			function.apply(cells[x+2][y]) &&
			function.apply(cells[x-2][y+1]) &&
			function.apply(cells[x+2][y+1]) &&
			function.apply(cells[x-2][y+2]) &&
			function.apply(cells[x-1][y+2]) &&
			function.apply(cells[x][y+2]) &&
			function.apply(cells[x+1][y+2]) &&
			function.apply(cells[x+2][y+2])
			) {
		return true;
	}
	return false;
}
private boolean checkAnySixteen(int x, int y, Function<Cell, Boolean> function) {
	if(
			function.apply(cells[x-2][y-2]) ||
			function.apply(cells[x-1][y-2]) ||
			function.apply(cells[x][y-2]) ||
			function.apply(cells[x+1][y-2]) ||
			function.apply(cells[x+2][y-2]) ||
			function.apply(cells[x-2][y-1]) ||
			function.apply(cells[x+2][y-1]) ||
			function.apply(cells[x-2][y]) ||
			function.apply(cells[x+2][y]) ||
			function.apply(cells[x-2][y+1]) ||
			function.apply(cells[x+2][y+1]) ||
			function.apply(cells[x-2][y+2]) ||
			function.apply(cells[x-1][y+2]) ||
			function.apply(cells[x][y+2]) ||
			function.apply(cells[x+1][y+2]) ||
			function.apply(cells[x+2][y+2])
			) {
		return true;
	}
	return false;
}

private void generateGems() {
	ArrayList<Cell> coopExclusiveCells = new ArrayList<Cell>();
	ArrayList<Cell> ballExclusiveCells = new ArrayList<Cell>();
	ArrayList<Cell> cubeExclusiveCells = new ArrayList<Cell>();

	Function<Cell, Boolean> reachBall = new Function<Cell, Boolean>() {
		@Override
		public Boolean apply(Cell t) {
			return t.reachBall;
		}
	};
	Function<Cell, Boolean> reachCube = new Function<Cell, Boolean>() {
		@Override
		public Boolean apply(Cell t) {
			return t.reachCube;
		}
	};
	Function<Cell, Boolean> reachCoop = new Function<Cell, Boolean>() {
		@Override
		public Boolean apply(Cell t) {
			return t.reachCoop;
		}
	};
	@SuppressWarnings("unused") // Test
	Function<Cell, Boolean> empty = new Function<Cell, Boolean>() {
		@Override
		public Boolean apply(Cell t) {
			return !t.occupied;
		}
	};

	for (int i = 2; i < cells.length-3; i++) {
		for (int j = 2; j < cells[0].length-3; j++) {
			Cell cell = cells[i][j];
			if(cell.reachBall && checkEight(i, j, reachBall) && !checkAnyEight(i, j, reachCube)) {
				cell.ballExclusive = true;
				ballExclusiveCells.add(cell);
			}
			if(cell.reachCube && !cell.reachBall && !checkAnyEight(i, j, reachBall) && !checkAnySixteen(i, j, reachBall)) {
				cell.cubeExclusive = true;
				cubeExclusiveCells.add(cell);
			}

			if(cell.reachCoop && checkEight(i, j, reachCoop) && !checkEight(i, j, reachBall)) {
				cell.coopExclusive = true;
				coopExclusiveCells.add(cell);
			}
		}
	}
	//TODO implement sliders (missing coop slider)
	Random rand = new Random(seed);
	gemsToGenerate += (rand.nextInt(2) -1);
	gemsToGenerate = Math.max(1, gemsToGenerate);
	int cnt = 0, timeout = 100*gemsToGenerate + gemsToGenerate;
	for(int i = 0; i < gemsToGenerate + cnt; i++) {
		boolean success = true;
		float bias = rand.nextFloat()*2-1, coop =rand.nextFloat()*2-1;
		int id = 0;
		if(coop > instance().coop) {
			if(coopExclusiveCells.size()>0) {
				id =rand.nextInt(coopExclusiveCells.size()-1);
				gems.add(new Gem(coopExclusiveCells.get(id)));
			} else {
				success = false;
			}

		} else {
			if(bias > instance().bias) { // pref cube
				if(cubeExclusiveCells.size()>0) {
					id = rand.nextInt(cubeExclusiveCells.size()-1);
					gems.add(new Gem(cubeExclusiveCells.get(id)));			
				} else {
					success = false;
				}
			} else { // pref ball
				if(ballExclusiveCells.size()>0) {
					id = rand.nextInt(ballExclusiveCells.size()-1);
					gems.add(new Gem(ballExclusiveCells.get(id)));			
				} else {
					success = false;
				}
			}
		}
		if(!success) {
			if(cnt > timeout) {
				System.out.println("Timeout when generating gems... Are players well positioned?");
				break;
			}
			cnt++;
		}
	}
}

public void regenSeed() {
	seed = System.nanoTime();
	tool.seedSpinner.setValue(seed);
}



private void reachabilityCoop() {
	int[] startPosition = {-2,-2};
	Cell startCell = getCell(playerBall.x+50, playerBall.y+50, startPosition);
	if(startPosition[0] < 0 || startPosition[1] < 0) { // Error
		System.out.println("Error - reachabilityBall");
	}
	TreeSet<Cell> cellsToAnalyse = new TreeSet<Cell>();
	cellsToAnalyse.add(startCell);
	if(!startCell.fitsBall) {
		for(int i=0; i < 2;i++) {
			cellsToAnalyse.add(getCell(playerBall.x+25, playerBall.y+50*i+25, startPosition));
			cellsToAnalyse.add(getCell(playerBall.x+75, playerBall.y+50*i+25, startPosition));
		}
	}
	int cnt = 0;
	while(!cellsToAnalyse.isEmpty()) {
		Cell current = cellsToAnalyse.first();
		analyseCoopReachability(current, cellsToAnalyse);
		cellsToAnalyse.remove(current);
		cnt++;
		if(cnt > 100000) {
			System.out.print("[Timeout] ");
			break;
		}
	}
	//System.out.println(cnt + " reachabilityCoop done.");
}
private void analyseCoopReachability(Cell current,
		TreeSet<Cell> cellsToAnalyse) {
	int x = current.x, y = current.y;

	if(current.fitsBall) {
		cells[x][y].reachCoop = true;

		if(!cells[x][y+1].fitsBall) { // has ground
			current.maxCoopJumpStrenght = 24;
			for (int i = -1; i < 2; i++) {
				if(cells[x+i][y-1].maxCoopJumpStrenght < 23) {
					cells[x+i][y-1].maxCoopJumpStrenght = 23;
					cellsToAnalyse.add(cells[x+i][y-1]);
				}
			}

			if(cells[x-1][y].maxCoopJumpStrenght != 24) {
				cellsToAnalyse.add(cells[x-1][y]);
			}
			if(cells[x+1][y].maxCoopJumpStrenght != 24) {
				cellsToAnalyse.add(cells[x+1][y]);
			}
		} else {
			if(current.maxCoopJumpStrenght > 0 && current.maxCoopJumpStrenght < 30) { // mid air?
				for (int i = -1; i < 2; i++) {
					if(cells[x+i][y-1].maxCoopJumpStrenght < current.maxCoopJumpStrenght-1) {
						cells[x+i][y-1].maxCoopJumpStrenght = current.maxCoopJumpStrenght-1;
						cellsToAnalyse.add(cells[x+i][y-1]);
					}
				}
			}
			for (int i = -1; i < 2; i++) {
				if(!cells[x+i][y+1].reachCoop) {
					cellsToAnalyse.add(cells[x+i][y+1]);
				}
			}
			if((cells[x][y+1].reachCube && !cells[x][y+2].fitsCube)||( cells[x][y+2].reachCube && !cells[x][y+3].fitsCube)) { //TODO switch to occupiable by cube
				current.maxCoopJumpStrenght = 30;
				for (int i = -1; i < 2; i++) {
					if(cells[x+i][y-1].maxCoopJumpStrenght < 29) {
						cells[x+i][y-1].maxCoopJumpStrenght = 29;
						cellsToAnalyse.add(cells[x+i][y-1]);
					}
				}
			}
		}
	}
}
private void reachabilityCube() {
	int[] startPosition = {-2,-2};
	getCell(playerCube.x+50, playerCube.y+50, startPosition);
	if(startPosition[0] < 0 || startPosition[1] < 0) { // Error
		System.out.println("Error - reachabilityCube");
	}
	checkCubeReach(startPosition[0], startPosition[1], 0);
}
private void reachabilityBall() {
	int[] startPosition = {-2,-2};
	Cell startCell = getCell(playerBall.x+50, playerBall.y+50, startPosition);
	if(startPosition[0] < 0 || startPosition[1] < 0) { // Error
		System.out.println("Error - reachabilityBall");
	}
	TreeSet<Cell> cellsToAnalyse = new TreeSet<Cell>();
	cellsToAnalyse.add(startCell);
	if(!startCell.fitsBall) {
		for(int i=0; i < 2;i++) {
			cellsToAnalyse.add(getCell(playerBall.x+25, playerBall.y+50*i+25, startPosition));
			cellsToAnalyse.add(getCell(playerBall.x+75, playerBall.y+50*i+25, startPosition));
		}
	}
	int cnt = 0;
	while(!cellsToAnalyse.isEmpty()) {
		Cell current = cellsToAnalyse.first();
		analyseBallReachability(current, cellsToAnalyse);
		cellsToAnalyse.remove(current);
		cnt++;
		if(cnt > 100000) {
			System.out.print("[Timeout] ");
			break;
		}
	}
	//System.out.println(cnt + " reachabilityBall done.");
}

private void analyseBallReachability(Cell current,
		TreeSet<Cell> cellsToAnalyse) {
	int x = current.x, y = current.y;

	if(current.fitsBall) {
		cells[x][y].reachBall = true;
		if(!cells[x][y+1].fitsBall) { // has ground
			current.maxJumpStrenght = 24;
			for (int i = -1; i < 2; i++) {
				if(cells[x+i][y-1].maxJumpStrenght < 23) {
					cells[x+i][y-1].maxJumpStrenght = 23;
					cellsToAnalyse.add(cells[x+i][y-1]);
				}
			}
			if(cells[x-1][y].maxJumpStrenght != 24) {
				cellsToAnalyse.add(cells[x-1][y]);
			}
			if(cells[x+1][y].maxJumpStrenght != 24) {
				cellsToAnalyse.add(cells[x+1][y]);
			}
		} else {
			if(current.maxJumpStrenght > 0 && current.maxJumpStrenght < 24) { // mid air?
				for (int i = -1; i < 2; i++) {
					if(cells[x+i][y-1].maxJumpStrenght < current.maxJumpStrenght-1) {
						cells[x+i][y-1].maxJumpStrenght = current.maxJumpStrenght-1;
						cellsToAnalyse.add(cells[x+i][y-1]);
					}
				}
			}
			for (int i = -1; i < 2; i++) {
				if(!cells[x+i][y+1].reachBall) {
					cellsToAnalyse.add(cells[x+i][y+1]);
				}
			}
		}
	}
}

private void checkCubeReach(int x, int y, int speed) {
	if(cells[x][y].reachCube) {
		if(speed>0) {
			if(cells[x][y].traversedCubeLeft) return;
			cells[x][y].traversedCubeLeft = true;				
		} else if(speed < 0) {
			if(cells[x][y].traversedCubeRight) return;
			cells[x][y].traversedCubeRight = true;	
		} else {
			return;
		}
	}
	if(cells[x][y].fitsCube) {
		cells[x][y].reachCube = true;
		if(!cells[x][y+1].fitsCube) {
			if(!cells[x-1][y].fitsCube) {
				checkCubeReach(x-1, y-1, -1);
				checkCubeReach(x, y-1, 0);
			} else {
				checkCubeReach(x, y-1, -1);
				checkCubeReach(x-1, y, -1);
				checkCubeReach(x-2, y, -1);
				checkCubeReach(x-3, y, -1);
			}
			if(!cells[x+1][y].fitsCube) {
				checkCubeReach(x+1, y-1, 1);
				checkCubeReach(x, y-1, 0);
			} else {
				checkCubeReach(x, y-1, 1);
				checkCubeReach(x+1, y, 1);
				checkCubeReach(x+2, y, 1);
				checkCubeReach(x+3, y, 1);
			}
		} else { //fall
			if(speed<0){
				checkCubeReach(x-1, y+1, speed);
			}
			checkCubeReach(x, y+1, speed);
			if(speed>0) {
				checkCubeReach(x+1, y+1, speed);
			}
		}
	}
}

private Cell getCell(int x, int y, int[] position) {
	for (int i = 0; i < cells.length -1; i++) {
		for (int j = 0; j < cells[0].length-1; j++) {
			Rectangle r = new Rectangle((int) cells[i][j].topleft.getX(), (int) cells[i][j].topleft.getY(), (int) cells[i][j].sizeX, (int) cells[i][j].sizeY);
			if(r.contains(x, y)) {
				position[0] = i;
				position[1] = j;
				return cells[i][j];
			}
		}
	}
	position[0] = -1;
	position[1] = -1;
	return null;
}
private Cell getCell(int x, int y) {
	int[] position = {0,0};
	return getCell(x, y, position);
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
}

private void clearInfo(Cell cell) {
	cell.ballArea=0;
	cell.ballVisits=0;
	cell.closestOccupied = Integer.MAX_VALUE;
	cell.closestOccupiedX = Integer.MAX_VALUE;
	cell.closestOccupiedY = Integer.MAX_VALUE;
	cell.coop = false;
	cell.cubeArea = 0;
	cell.fitsBall = false;
	cell.fitsCube = false;
	cell.reachBall = false;
	cell.reachCube = false;
	cell.reachCoop = false;
	cell.maxJumpStrenght = 0;
	cell.maxCoopJumpStrenght = 0;
	cell.ballExclusive=false;
	cell.cubeExclusive=false;
	cell.coopExclusive=false;
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
	clearBlobInfo();
	for (int i = 3; i < cells.length-4; i++) {
		for (int j = 3; j < cells[0].length-4; j++) {
			Cell current = cells[i][j];
			if(current.occupied) {
				if(cells[i-1][j].blob != null) {
					current.blob = cells[i-1][j].blob;
					current.blob.cells.add(current);
				} else if(cells[i][j-1].blob != null) {
					current.blob = cells[i][j-1].blob;
					current.blob.cells.add(current);
				} else {
					current.blob = new Blob(current.y, current.x, current.y+(int)current.sizeY, current.x+(int)current.sizeX);
					current.blob.cells.add(current);
					blobs.add(current.blob);
				}
			}
		}
	}
	ArrayList<Blob> extraBlobs = new ArrayList<Blob>();
	while(true) {
		extraBlobs.clear();
		for(Blob b: blobs) {
			sectionBlob(b, extraBlobs);
		}

		blobs.addAll(extraBlobs);
		if(extraBlobs.isEmpty()) {
			break;
		}
	}

	ArrayList<Obstacle> boxes = new ArrayList<Obstacle>();

	for(Blob b: blobs) {
		Obstacle o = new Obstacle();
		Cell c = b.cells.get(0);
		o.x= c.x;
		o.y= c.y;
		o.sizeX = (int)c.sizeX*b.xCells;
		o.sizeY = (int)c.sizeY*b.yCells;
		boxes.add(o);
	}

	Level[] levelsArray = new Level[1];
	levelsArray[0] = new Level();
	levelsArray[0].setPlayer1(new PlayerComponent(playerBall.x,	playerBall.y));
	levelsArray[0].setPlayer2(new PlayerComponent(playerCube.x,	playerCube.y));
	for(Obstacle o: boxes) {
		levelsArray[0].addComponent(new protoGenerator.Block(o.x*cellSizeX, o.y*cellSizeY, o.sizeX, o.sizeY));
	}
	for(Gem g : gems) {
		levelsArray[0].addComponent(new protoGenerator.Gem(g.x, g.y));
	}
	protoGenerator.Drawer.drawLevels(selectedFile.getAbsolutePath(), levelsArray);
}

private void clearBlobInfo() {
	for (int i = 3; i < cells.length-4; i++) {
		for (int j = 3; j < cells[0].length-4; j++) {
			cells[i][j].blob=null;
		}
	}
	blobs = new ArrayList<Blob>();
}



private void sectionBlob(Blob b, ArrayList<Blob> extraBlobs) {
	ArrayList<Cell> inBlob = new ArrayList<Cell>();
	int i=3, j=3;
	searchloop:
		for (i = 3; i < cells.length -4; i++) {
			for (j = 3; j < cells[0].length-4; j++) {
				if(cells[i][j] == b.cells.get(0)) {
					break searchloop;
				}
			}
		}

	//getMax x
	int xCnt = 1;
	while(cells[i+xCnt][j].blob != null && cells[i+xCnt][j].blob.equals(b)) {
		inBlob.add(cells[i+xCnt][j]);
		xCnt++;
	}
	int yCnt = 1;
	boolean lineOkay = true;
	while(lineOkay) {
		for(int k = 0; k < xCnt; k++) {
			if(cells[i+k][j+yCnt].blob == null || !cells[i+k][j+yCnt].blob.equals(b)) {
				lineOkay = false;
			}
		}
		if(lineOkay) {
			for(int k = 0; k < xCnt; k++) {
				inBlob.add(cells[i+k][j+yCnt]);
			}
		}
		yCnt++;
	}
	//System.out.println("i,j=" + i + "," + j+ " x=" + xCnt + ", y=" + yCnt);
	b.xCells = xCnt;
	b.yCells = yCnt-1;
	Blob newBlob = new Blob();
	inBlob.add(cells[i][j]);
	for(Cell c : b.cells) {
		if(!inBlob.contains(c)){
			c.blob=newBlob;
			newBlob.cells.add(c);
		}
	}
	b.cells.removeAll(newBlob.cells);
	if(newBlob.cells.size() > 0)
		extraBlobs.add(newBlob);
}

private class Obstacle {
	int x, y, sizeX, sizeY;
}

public void loadFile(File selectedFile) {
	Cell[][] result=freshCells();
	int level = selectLevel(selectedFile);
	instance().playerBall.active = false;
	instance().playerCube.active = false;
	clearGems();

	BufferedReader input = null;
	boolean inLevel = false;
	boolean inBlackObstacles = false;
	boolean inCollectibles = false;
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
				if(tokens[1].contains("Collectibles")) {
					inCollectibles = true;
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
				if(inCollectibles) {
					if(tokens[1].contains("/Collectibles")) {
						inCollectibles = false;
					}
					if(tokens[1].contains("Collectible ")) {
						String[] values = tokens[1].split("[\"]");
						values[0] = values[0].substring("Collectible ".length()-1, values[0].length());
						int x=0, y=0;
						for(int i =0; i < 4; i+=2) {
							if(values[i].equals(" X=")) {
								x = Integer.parseInt(values[i+1]);
							}
							if(values[i].equals(" Y=")) {
								y = Integer.parseInt(values[i+1]);
							}
						}
						gems.add(new Gem(x, y, getCell(x, y)));
					}
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
	evaluateHeuristics();
	repaintAll();
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
			f.add(instance.tool, BorderLayout.WEST);
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
	//drawCells(g);
	for(int i = 0; i < cells.length-1; i++) {
		for(int j =0; j < cells[0].length-1; j++) {
			cells[i][j].draw(g);
		}
	}
	for (Gem gem : gems) {
		drawGem(g, gem);
	}
	drawPlayers(g);
}

private void drawGem(Graphics g, Gem gem) {
	int border = 4;
	int size = 40;
	if(gem.active) {
		int npoints = 4;
		int[] xpoints = {gem.x-size, gem.x, gem.x+size, gem.x};
		int[] ypoints = {gem.y, gem.y+size, gem.y, gem.y-size};
		Polygon outer = new Polygon(xpoints, ypoints, npoints);
		int[] xopoints = {gem.x-size+border, gem.x, gem.x+size-border, gem.x};
		int[] yopoints = {gem.y, gem.y+size-border, gem.y, gem.y-size+border};
		Polygon inner = new Polygon(xopoints, yopoints, npoints);
		g.setColor(Color.black);
		g.fillPolygon(outer);
		g.setColor(new Color(153,50,204)); // gemPink
		g.fillPolygon(inner);
	}
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
	instance.gems = new ArrayList<GraphicInterface.Gem>();
	paintBorders(instance.cells);
}

public enum Overlay {
	None, Ball, Cube, JumpStrenght, Coop, CoopJumpStrenght, GemExclusivity, Blob
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
			instance.cells[i][j].x = i;
			instance.cells[i][j].y = j;
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
			fresh[i][j].x = i;
			fresh[i][j].y = j;
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

private class Gem {
	public int x = 0;
	public int y = 0;
	Cell cell;
	boolean active = true;

	public Gem(Cell cell) {
		this.cell = cell;
		x = cell.x*cellSizeX;
		y = cell.y*cellSizeY;
	}
	public Gem(int x, int y, Cell cell) {
		this.x = x;
		this.y = y;
		this.cell = cell;
	}
}

}
