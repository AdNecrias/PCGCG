package pcgcg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


public class Blob {
	
	public ArrayList<Cell> cells;
	public int top, left, right, bottom;
	
	public Color color;
	
	public Blob() {
		Random r = new Random();
		color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		cells = new ArrayList<Cell>();
	}
	
	public Blob(int top, int left, int bottom, int right) {
		this();
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

}
