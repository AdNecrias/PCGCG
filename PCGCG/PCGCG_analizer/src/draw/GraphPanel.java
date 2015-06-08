package draw;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.*;

/**
 * @author John B. Matthews; distribution per GPL.
 */
public class GraphPanel extends JComponent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8353518389283144111L;
	private static final int WIDE = 640;
    private static final int HIGH = 480;
    private static final int RADIUS = 35;
    private static final Random rnd = new Random();
    private ControlPanel control = new ControlPanel();
    private int radius = RADIUS;
    private Kind kind = Kind.Circular;
    private List<Gem> gems = new ArrayList<Gem>();
    private List<Player> players = new ArrayList<Player>();
    private List<Node> nodes = new ArrayList<Node>();
    private List<Node> selected = new ArrayList<Node>();
    private List<Edge> edges = new ArrayList<Edge>();
    private List<AArea> areas = new ArrayList<AArea>();
    private Point mousePt = new Point(WIDE / 2, HIGH / 2);
    private Rectangle mouseRect = new Rectangle();
    private boolean selecting = false;
    private static Edge selectedEdge = null;
    public GraphPanel graphPanel;
    
    public void fakeMain(String [] args, boolean done[]) {
    	EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame f = new JFrame("GraphPanel");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                graphPanel = new GraphPanel();
                f.add(graphPanel.control, BorderLayout.NORTH);
                f.add(new JScrollPane(graphPanel), BorderLayout.CENTER);
                f.getRootPane().setDefaultButton(graphPanel.control.defaultButton);
                f.pack();
                f.setLocationByPlatform(true);
                f.setVisible(true);
                done[0] = true;
            }
        });
    }
    
    public void addArea(Area a, Color c) {
    	areas.add(new AArea(a, c));
    	repaint();
    }
    
    public void addNode() {
    	Node.selectNone(nodes);
        Point p = mousePt.getLocation();
        Color color = control.hueIcon.getColor();
        Node n = new Node(p, radius, color, kind);
        n.setSelected(true);
        nodes.add(n);
        repaint();
    }
    public GraphNode addNode(Point p, int radius, Color color, Kind kind) {
    	Node.selectNone(nodes);
        Node n = new Node(p, radius, color, kind);
        n.setSelected(true);
        nodes.add(n);
        repaint();
        return new GraphNode(n);
    }
    public GraphNode addNode(Point p, int radius, Color color, Kind kind, Rectangle bondary) {
    	Node.selectNone(nodes);
        Node n = new Node(p, radius, color, kind, bondary);
        n.setSelected(true);
        nodes.add(n);
        repaint();
        return new GraphNode(n);
    }
    
    public void connect(Node n1, Node n2) {
    	Edge e = new Edge(n1, n2);
    	edges.add(e);
    	repaint();
    }
    public void connect(Node n1, Node n2, String info) {
    	Edge e = new Edge(n1, n2);
    	e.info = info;
    	edges.add(e);
    	repaint();
    }

    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame f = new JFrame("GraphPanel");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                GraphPanel gp = new GraphPanel();
                f.add(gp.control, BorderLayout.NORTH);
                f.add(new JScrollPane(gp), BorderLayout.CENTER);
                f.getRootPane().setDefaultButton(gp.control.defaultButton);
                f.pack();
                f.setLocationByPlatform(true);
                f.setVisible(true);
            }
        });
    }

    public GraphPanel() {
        this.setOpaque(true);
        this.addMouseListener(new MouseHandler());
        this.addMouseMotionListener(new MouseMotionHandler());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDE, HIGH);
    }

    @Override
    public void paintComponent(Graphics g) throws ConcurrentModificationException {
        g.setColor(new Color(0x00f0f0f0));
        g.fillRect(0, 0, getWidth(), getHeight());
        for (Edge e : edges) {
            e.draw(g);
        }
        for (Node n : nodes) {
            n.draw(g);
        }
        for (AArea a : areas) {
        	a.draw(g);
        }
        for (Player p: players) {
        	p.draw(g);
        }
        for (Gem gem: gems) {
        	gem.draw(g);
        }
        if (selecting) {
            g.setColor(Color.darkGray);
            g.drawRect(mouseRect.x, mouseRect.y,
                mouseRect.width, mouseRect.height);
        }
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            selecting = false;
            mouseRect.setBounds(0, 0, 0, 0);
            if (e.isPopupTrigger()) {
                showPopup(e);
            }
            e.getComponent().repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mousePt = e.getPoint();
            if (e.isShiftDown()) {
                Node.selectToggle(nodes, mousePt);
            } else if (e.isPopupTrigger()) {
                Node.selectOne(nodes, mousePt);
                showPopup(e);
            } else if (Node.selectOne(nodes, mousePt)) {
                selecting = false;
            } else {
                Node.selectNone(nodes);
                selecting = true;
            }
            
            Edge edge;
            if((edge = overEdge(mousePt.x, mousePt.y)) != null) {
            	showLinkInfo(e, edge);
            	selectedEdge = edge;
            }
                        
            e.getComponent().repaint();
            
            
        }
        private void showLinkInfo(MouseEvent e, Edge edge) {
            control.linkinfo.show(e.getComponent(), e.getX(), e.getY(), edge.info);
        }
        
        public Edge overEdge(int x, int y) {
        	for(Edge e : edges) {
        		if(e.contains(new Point(x,y))) {
        			return e;
        		}
        	}
        	return null;
        }

        private void showPopup(MouseEvent e) {
            control.popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private class MouseMotionHandler extends MouseMotionAdapter {

        Point delta = new Point();

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selecting) {
                mouseRect.setBounds(
                    Math.min(mousePt.x, e.getX()),
                    Math.min(mousePt.y, e.getY()),
                    Math.abs(mousePt.x - e.getX()),
                    Math.abs(mousePt.y - e.getY()));
                Node.selectRect(nodes, mouseRect);
            } else {
                delta.setLocation(
                    e.getX() - mousePt.x,
                    e.getY() - mousePt.y);
                Node.updatePosition(nodes, delta);
                mousePt = e.getPoint();
            }
            
            e.getComponent().repaint();
        }
    }

    public JToolBar getControlPanel() {
        return control;
    }

    private class ControlPanel extends JToolBar {

        /**
		 * 
		 */
		private static final long serialVersionUID = 5755880689266117942L;
		public Action newNode = new NewNodeAction("New");
        private Action clearAll = new ClearAction("Clear");
        private Action kind = new KindComboAction("Kind");
        private Action color = new ColorAction("Color");
        private Action connect = new ConnectAction("Connect");
        private Action delete = new DeleteAction("Delete");
        private Action random = new RandomAction("Random");
        private JButton defaultButton = new JButton(newNode);
        private JComboBox<Kind> kindCombo = new JComboBox<Kind>();
        private ColorIcon hueIcon = new ColorIcon(Color.blue);
        private JPopupMenu popup = new JPopupMenu();
        
        private LinkInfo linkinfo = new LinkInfo();

        ControlPanel() {
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            this.setBackground(Color.lightGray);
            this.add(defaultButton);
            this.add(new JButton(clearAll));
            this.add(kindCombo);
            this.add(new JButton(color));
            this.add(new JLabel(hueIcon));
            JSpinner js = new JSpinner();
            js.setModel(new SpinnerNumberModel(RADIUS, 5, 100, 5));
            js.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    JSpinner s = (JSpinner) e.getSource();
                    radius = (Integer) s.getValue();
                    Node.updateRadius(nodes, radius);
                    GraphPanel.this.repaint();
                }
            });
            this.add(new JLabel("Size:"));
            this.add(js);
            this.add(new JButton(random));

            popup.add(new JMenuItem(newNode));
            popup.add(new JMenuItem(color));
            popup.add(new JMenuItem(connect));
            popup.add(new JMenuItem(delete));
            JMenu subMenu = new JMenu("Kind");
            for (Kind k : Kind.values()) {
                kindCombo.addItem(k);
                subMenu.add(new JMenuItem(new KindItemAction(k)));
            }
            popup.add(subMenu);
            kindCombo.addActionListener(kind);
            
            linkinfo.add(new JMenuItem("Info"));
        }

        class KindItemAction extends AbstractAction {

            /**
			 * 
			 */
			private static final long serialVersionUID = 7047407184345521467L;
			private Kind k;

            public KindItemAction(Kind k) {
                super(k.toString());
                this.k = k;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                kindCombo.setSelectedItem(k);
            }
        }
    }

    private class ClearAction extends AbstractAction {

        /**
		 * 
		 */
		private static final long serialVersionUID = 3761560140847619757L;

		public ClearAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            nodes.clear();
            edges.clear();
            repaint();
        }
    }

    private class ColorAction extends AbstractAction {

        /**
		 * 
		 */
		private static final long serialVersionUID = -2479672376278544506L;

		public ColorAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            Color color = control.hueIcon.getColor();
            color = JColorChooser.showDialog(
                GraphPanel.this, "Choose a color", color);
            if (color != null) {
                Node.updateColor(nodes, color);
                control.hueIcon.setColor(color);
                control.repaint();
                repaint();
            }
        }
    }

    private class ConnectAction extends AbstractAction {

        /**
		 * 
		 */
		private static final long serialVersionUID = -1304046960898694794L;

		public ConnectAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            Node.getSelected(nodes, selected);
            if (selected.size() > 1) {
                for (int i = 0; i < selected.size() - 1; ++i) {
                    Node n1 = selected.get(i);
                    Node n2 = selected.get(i + 1);
                    edges.add(new Edge(n1, n2));
                }
            }
            repaint();
        }
    }

    private class DeleteAction extends AbstractAction {

        /**
		 * 
		 */
		private static final long serialVersionUID = -5904834865578015230L;

		public DeleteAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            ListIterator<Node> iter = nodes.listIterator();
            while (iter.hasNext()) {
                Node n = iter.next();
                if (n.isSelected()) {
                    deleteEdges(n);
                    iter.remove();
                }
            }
            repaint();
        }

        private void deleteEdges(Node n) {
            ListIterator<Edge> iter = edges.listIterator();
            while (iter.hasNext()) {
                Edge e = iter.next();
                if (e.n1 == n || e.n2 == n) {
                    iter.remove();
                }
            }
        }
    }

    private class KindComboAction extends AbstractAction {

        /**
		 * 
		 */
		private static final long serialVersionUID = 7070697834451114125L;

		public KindComboAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<Kind> combo = (JComboBox<Kind>) e.getSource();
            kind = (Kind) combo.getSelectedItem();
            Node.updateKind(nodes, kind);
            repaint();
        }
    }

    private class NewNodeAction extends AbstractAction {

        /**
		 * 
		 */
		private static final long serialVersionUID = -4670724990380277377L;

		public NewNodeAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            Node.selectNone(nodes);
            Point p = mousePt.getLocation();
            Color color = control.hueIcon.getColor();
            Node n = new Node(p, radius, color, kind);
            n.setSelected(true);
            nodes.add(n);
            repaint();
        }
    }

    private class RandomAction extends AbstractAction {

        /**
		 * 
		 */
		private static final long serialVersionUID = -857108994011701243L;

		public RandomAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < 16; i++) {
                Point p = new Point(rnd.nextInt(getWidth()), rnd.nextInt(getHeight()));
                nodes.add(new Node(p, radius, new Color(rnd.nextInt()), kind));
            }
            repaint();
        }
    }

    /**
     * The kinds of node in a graph.
     */
    public enum Kind {

        Circular, Rounded, Square;
    }

    /**
     * An Edge is a pair of Nodes.
     */
    private static class Edge {

        private Node n1;
        private Node n2;
        public String info = "Info: None";

        public Edge(Node n1, Node n2) {
            this.n1 = n1;
            this.n2 = n2;
        }
        
        public boolean intersects(Point k, Point z, Point p) {
        	double precision = 2.0;
            return new Line2D.Float(k, z).ptLineDist(p) <= precision;
        }
        public boolean contains(Point point) {
        	return intersects(n1.getOriginLocation(), n2.getTargetLocation(), point);
		}

		public void draw(Graphics g) {
            Point p1 = n1.getOriginLocation();
            Point p2 = n2.getTargetLocation();
            g.setColor(Color.darkGray);
            if(this.equals(selectedEdge))
                g.setColor(Color.lightGray);
            
            GradientPaint bgPaint = new GradientPaint( 
                    p1.x, 
                    p1.y, 
                    new Color(0,126,255), 
                    p2.x, 
                    p2.y, 
                    new Color(255,129,0) );
            	Graphics2D g2 = (Graphics2D)g;
                g2.setPaint(bgPaint);

            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            g2.setPaint(null);
        }
    }
    
    public static class AArea {
    	private Area area;
    	private Color color;
    	
    	public AArea(Area a, Color c) {
    		this.area = a;
    		this.color = c;
    	}
    	
    	public void draw(Graphics g) {
    		g.setColor(color);
    		g.fillArc(10, 10, 10, 10, 0, 60);
    		g.drawArc(10, 10, 10, 10, 0, 360);
    		if(area != null) {
    			System.out.println("Area Found.");	
    		}
    	}
    }

    /**
     * A Node represents a node in a graph.
     */
    public static class Node {

        private Point p;
        private int r;
        private Color color;
        private Kind kind;
        private boolean selected = false;
        private Rectangle b = new Rectangle();
        private int sizeX, sizeY;
        private ArrayList<Gem> gems;
        private ArrayList<Player> players;

        /**
         * Construct a new node.
         */
        public Node(Point p, int r, Color color, Kind kind) {
            this.p = p;
            this.r = r;
            this.color = color;
            this.kind = kind;
            setBoundary(b);
            this.gems = new ArrayList<Gem>();
            this.players = new ArrayList<Player>();
        }
        public Node(Point p, int r, Color color, Kind kind, Rectangle bondary) {
            this.p = p;
            this.r = r;
            this.color = color;
            this.kind = kind;
            this.b = bondary;
            this.sizeX = bondary.width;
            this.sizeY = bondary.height;
            this.gems = new ArrayList<Gem>();
            this.players = new ArrayList<Player>();
        }
        public void inventoryAdd(Gem gem) {
        	gems.add(gem);
        }
        
        public void inventoryAdd(Player player) {
        	players.add(player);
        }

        /**
         * Calculate this node's rectangular boundary.
         */
        private void setBoundary(Rectangle b) {
            b.setBounds(p.x, p.y, sizeX, sizeY);
        }

        /**
         * Draw this node.
         */
        public void draw(Graphics g) {
            g.setColor(this.color);
            if (this.kind == Kind.Circular) {
                g.fillOval(b.x, b.y, b.width, b.height);
            } else if (this.kind == Kind.Rounded) {
                g.fillRoundRect(b.x, b.y, b.width, b.height, r, r);
            } else if (this.kind == Kind.Square) {
                g.fillRect(b.x, b.y, b.width, b.height);
            }
            if (selected) {
                g.setColor(Color.darkGray);
                g.drawRect(b.x, b.y, b.width, b.height);
            }
            int radius = 7;            
            g.setColor(new Color(0,126,255));
            g.fillOval(getOriginLocation().x-radius/2, getOriginLocation().y-radius/2, radius, radius);

            g.setColor(new Color(255,129,0));
            g.fillOval(getTargetLocation().x-radius/2, getTargetLocation().y-radius/2, radius, radius);
        }

        /**
         * Return this node's location.
         */
        public Point getLocation() {
            return p;
        }
        
        public Point getOriginLocation() {
        	return new Point(p.x - 15 + b.width/2, p.y + 15);
        }
        public Point getTargetLocation() {
        	return new Point(p.x + 15 + b.width/2, p.y + 15);
        }

        /**
         * Return true if this node contains p.
         */
        public boolean contains(Point p) {
            return b.contains(p);
        }

        /**
         * Return true if this node is selected.
         */
        public boolean isSelected() {
            return selected;
        }

        /**
         * Mark this node as selected.
         */
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        /**
         * Collected all the selected nodes in list.
         */
        public static void getSelected(List<Node> list, List<Node> selected) {
            selected.clear();
            for (Node n : list) {
                if (n.isSelected()) {
                    selected.add(n);
                }
            }
        }

        /**
         * Select no nodes.
         */
        public static void selectNone(List<Node> list) {
            for (Node n : list) {
                n.setSelected(false);
            }
        }

        /**
         * Select a single node; return true if not already selected.
         */
        public static boolean selectOne(List<Node> list, Point p) {
            for (Node n : list) {
                if (n.contains(p)) {
                    if (!n.isSelected()) {
                        Node.selectNone(list);
                        n.setSelected(true);
                    }
                    return true;
                }
            }
            return false;
        }

        /**
         * Select each node in r.
         */
        public static void selectRect(List<Node> list, Rectangle r) {
            for (Node n : list) {
                n.setSelected(r.contains(n.p));
            }
        }

        /**
         * Toggle selected state of each node containing p.
         */
        public static void selectToggle(List<Node> list, Point p) {
            for (Node n : list) {
                if (n.contains(p)) {
                    n.setSelected(!n.isSelected());
                }
            }
        }

        /**
         * Update each node's position by d (delta).
         */
        public static void updatePosition(List<Node> list, Point d) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.p.x += d.x;
                    n.p.y += d.y;
                    n.setBoundary(n.b);
                    n.inventoryMove(d);
                }
            }
        }

        private void inventoryMove(Point d) {
        	for(Gem g : gems) {
        		g.move(d);
        	}
        	for(Player p : players) {
        		p.move(d);
        	}
		}
		/**
         * Update each node's radius r.
         */
        public static void updateRadius(List<Node> list, int r) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.r = r;
                    n.setBoundary(n.b);
                }
            }
        }

        /**
         * Update each node's color.
         */
        public static void updateColor(List<Node> list, Color color) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.color = color;
                }
            }
        }

        /**
         * Update each node's kind.
         */
        public static void updateKind(List<Node> list, Kind kind) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.kind = kind;
                }
            }
        }
    }

    private static class ColorIcon implements Icon {

        private static final int WIDE = 20;
        private static final int HIGH = 20;
        private Color color;

        public ColorIcon(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, WIDE, HIGH);
        }

        public int getIconWidth() {
            return WIDE;
        }

        public int getIconHeight() {
            return HIGH;
        }
    }

	public Gem addGem(int x, int y) {
		Gem g = new Gem(x,y);
		gems.add(g);
		return g;
	}
	
	public static class Gem {
		private Color color;
		private Point p;
		private Polygon pol;
		
		public Gem(int x, int y) {
			this(new Point(x,y));
		}

		public void move(Point d) {
			p.x += d.x;
			p.y += d.y;			
		}

		public Gem(Point point) {
			color = Color.magenta;
			setLocation(point);
		}
		
		public void setLocation(Point p) {
			this.p = p;
			int []xpoints = {p.x-10, p.x, p.x+10, p.x};
			int []ypoints = {p.y, p.y +10, p.y, p.y -10};
			pol = new Polygon(xpoints, ypoints, 4);
		}
		
		public Point getLocation() {
			return p;
		}
		
		public void draw(Graphics g) {
    		g.setColor(color);			
    		g.fillPolygon(pol);
    		g.setColor(color.darker().darker());
    		g.drawPolygon(pol);
    	}

		@Override
		public String toString() {
			return "Gem [p=" + p + "]";
		}

		public void setColor(Color color) {
			this.color = color;
			
		}
		
	}
	
	public Player addPlayer(int x, int y, String type) {
		if(type.equals("Ball")) {
			Player p = new Player(x,y, Player.PlayerType.Ball);
			players.add(p);
			return p;
		}
		if(type.equals("Cube")) {
			Player p = new Player(x,y, Player.PlayerType.Cube);
			players.add(p);			
			return p;
		}
		return null;
	}
	public static class Player {
		private Point p;
		private PlayerType type;
		public enum PlayerType {
			Cube,Ball
		}
		
		public Player(int x, int y, PlayerType type) {
			this(new Point(x,y), type);
		}

		public void move(Point d) {
			p.x += d.x;
			p.y += d.y;			
		}

		public Player(Point point,  PlayerType type) {
			this.type = type;
			setLocation(point);
		}
		
		public void setLocation(Point p) {
			this.p = p;
		}
		
		public Point getLocation() {
			return p;
		}
		
		public void draw(Graphics g) {
			Color c;
			if(type == PlayerType.Cube) {
				c = Color.green.darker();
				g.setColor(c);
				g.fillRect(p.x -10, p.y -10, 20, 20);
				c = c.darker().darker();
				g.setColor(c);
				g.drawRect(p.x -10, p.y -10, 20, 20);
			}				
			if(type == PlayerType.Ball) {
				c = Color.yellow;
				g.setColor(c);
				g.fillOval(p.x -10, p.y -10, 20, 20);
				c = c.darker().darker();
				g.setColor(c);
				g.drawOval(p.x -10, p.y -10, 20, 20);
			}
    	}

		@Override
		public String toString() {
			return "Player [p=" + p + ", type=" + type + "]";
		}
		
		
	}
}