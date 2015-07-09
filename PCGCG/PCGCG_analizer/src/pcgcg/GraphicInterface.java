package pcgcg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;



public class GraphicInterface extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final Color cellBackground = new Color(230,230,230);
	private static final Color cellOccBackground = new Color(0x3e3e3e);
	private static final Color cellCubeBackground = new Color(0x9BCA3E);
	private static final Color cellNotBallBackground = new Color(0xED5314);
	private static final Color cellBorder = new Color(170,170,170);
	public static final int levelSizeX = 1280;
	public static final int levelSizeY = 800;
	public static final int cellSizeX = 16;
	public static final int cellSizeY = 16;
	
	public Overlay currentOverlay = Overlay.None;
	private Cell[][] cells;
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

		ButtonsBar() {
			this.add(overlayCombo);
			overlayCombo.addActionListener(ballOverlay);
			for (Overlay k : Overlay.values()) {
				overlayCombo.addItem(k);
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
					if(currentOverlay == Overlay.Ball && cells[i][j].ball) {
						toUse = cellNotBallBackground;
					}
					
					if(currentOverlay == Overlay.Cube && cells[i][j].cube) {
						toUse = cellCubeBackground;
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
		Ball, Cube, Distance, None
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
	
	public static void run(Cell[][] discCells) {
		instance = new GraphicInterface();
		instance.setCells(discCells);
		WIDE = (int) discCells[0][0].sizeX * (discCells.length -1);
		HIGH = (int) discCells[0][0].sizeY * (discCells[0].length-1);
		run();
	}

}
