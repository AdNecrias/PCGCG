package pcgcg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


public class Blob {
	
	public ArrayList<Cell> cells;
	public int top, left, right, bottom;
	public long id;
	public static long idCounter=0;
	
	public Color color;
	public int xCells =0;
	public int yCells =0;
	
	public Blob() {
		Random r = new Random();
		color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		cells = new ArrayList<Cell>();
		id = ++idCounter;
	}
	
	public Blob(int top, int left, int bottom, int right) {
		this();
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return this == null;
		if(o.getClass() != Blob.class) return false;
		Blob b = (Blob) o;
		return b.id == this.id;
	}
}
