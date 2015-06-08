package analyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JComboBox;




public class DiscreteViewer extends JComponent{
	public static DiscreteViewer instance;
	public static int WIDE=640, HIGH=480;
	private static Overlay currentOverlay = Overlay.Distance;
	private ButtonsBar control = new ButtonsBar();


	private static final long serialVersionUID = 3477983548381680944L;
	private static final Color cellBackground = new Color(230,230,230);
	private static final Color cellOccBackground = new Color(0x3e3e3e);
	private static final Color cellCubeBackground = new Color(0x9BCA3E);
	private static final Color cellNotBallBackground = new Color(0xED5314);
	private static final Color cellBorder = new Color(170,170,170);
	private Cell[][] cells;
	
	
	private class ButtonsBar extends JToolBar {
		private static final long serialVersionUID = -3350787588687266167L;
		private Action ballOverlay = new OverlayComboAction("Overlay");
		private JComboBox<Overlay> overlayCombo = new JComboBox<Overlay>();
		
		ButtonsBar() {
			this.add(overlayCombo);
			overlayCombo.addActionListener(ballOverlay);
			for (Overlay k : Overlay.values()) {
				overlayCombo.addItem(k);
            }
		}
		
		@SuppressWarnings("unused")
		private class OverlayAction extends AbstractAction {
			private static final long serialVersionUID = 298648541828787207L;
			
			Overlay type;
			
			public OverlayAction(Overlay type) {
				super(type.toString());
				this.type = type;
			}

			@SuppressWarnings("static-access")
			@Override
			public void actionPerformed(ActionEvent e) {
				instance.currentOverlay = type;
			}
		}
		
	}
	private class OverlayComboAction extends AbstractAction {
		private static final long serialVersionUID = 298648541828787207L;
		
		Overlay type;
		
		public OverlayComboAction(String name) {
			super(name);
		}

		@SuppressWarnings("static-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<Overlay> combo = (JComboBox<Overlay>) e.getSource();
            type = (Overlay) combo.getSelectedItem();
            instance.currentOverlay = type;
            repaint();
		}
	}
	
	public DiscreteViewer(Cell[][] discCells) {
		this();
		this.cells = discCells;
	}

	public DiscreteViewer() {
		this.setOpaque(true);
	}
	
	public void setCells (Cell[][] cells ) {
		this.cells = cells;
	}

	public enum Overlay {
		Ball, Cube, Distance, None
	}
	
	public static void run() {
		EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame f = new JFrame("DiscreteViewer");
                f.add(instance.control, BorderLayout.NORTH);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                if(instance == null)
                	instance = new DiscreteViewer();
                f.add(new JScrollPane(instance), BorderLayout.CENTER);
                f.pack();
                f.setLocationByPlatform(true);
                f.setVisible(true);
            }
        });
	}
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDE, HIGH);
    }
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(new Color(51,51,51));
        g.fillRect(0, 0, getWidth(), getHeight());
        drawCells(g);
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

	public static void run(Cell[][] discCells) {
		instance = new DiscreteViewer(discCells);
		WIDE = (int) discCells[0][0].sizeX * (discCells.length -1);
		HIGH = (int) discCells[0][0].sizeY * (discCells[0].length-1);
		run();
	}
	
	
}
