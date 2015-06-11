package analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class ConnectedComponentTwoPass {
	private static HashMap<Integer, Set<Cell>> hash, ballHash;
	
	public static void pass(Cell[][] discCells) {		
		hash = new HashMap<Integer, Set<Cell>>();
		ballHash = new HashMap<Integer, Set<Cell>>();
		int currentLabel = 0;
		int ballcurrentLabel = 0;
		
		
		for (int i = 0; i < discCells.length-1; i++) {
			for (int j = 0; j < discCells[0].length-1; j++) {
				if(discCells[i][j].ball) {
					int label = eightConnectedBall(ballcurrentLabel, i, j, discCells);
					if(label == Integer.MAX_VALUE) {
						label = ++ballcurrentLabel;
						ballHash.put(new Integer(label), new HashSet<Cell>());
					}
					ballHash.get(label).add(discCells[i][j]);
				}
				if(discCells[i][j].cube) {
					int label = eightConnected(currentLabel, i, j, discCells);
					if(label == Integer.MAX_VALUE) {
						label = ++currentLabel;
						hash.put(new Integer(label), new HashSet<Cell>());
					}
					hash.get(label).add(discCells[i][j]);
				}
			}
		}
		//Second pass
		for (int i = 0; i < discCells.length-1; i++) {
			for (int j = 0; j < discCells[0].length-1; j++) {
				if(discCells[i][j].ball) {
					int label = eightConnectedBall(ballcurrentLabel, i, j, discCells);
					if(label == Integer.MAX_VALUE) {
						label = ++ballcurrentLabel;
						ballHash.put(new Integer(label), new HashSet<Cell>());
					}
					Integer l = getBallLabel(discCells[i][j]);
					if(l != label) {
						ballHash.get(label).addAll(ballHash.get(l));
						ballHash.remove(l);
					}
				}
				if(discCells[i][j].cube) {
					int label = eightConnected(currentLabel, i, j, discCells);
					if(label == Integer.MAX_VALUE) {
						label = ++currentLabel;
						hash.put(new Integer(label), new HashSet<Cell>());
					}
					Integer l = getLabel(discCells[i][j]);
					if(l != label) {
						hash.get(label).addAll(hash.get(l));
						hash.remove(l);
					}
				}
			}
		}
		
		for( Integer i : ballHash.keySet()) {
			for(Cell c : ballHash.get(i)) {
				c.ballArea = i.intValue();
			}
		}
		
		for( Integer i : hash.keySet()) {
			for(Cell c : hash.get(i)) {
				c.cubeArea = i.intValue();
			}
		}
	}

	private static Integer eightConnected(int currentLabel, int x, int y, Cell[][] discCells) {
		int smallestLabel = Integer.MAX_VALUE;
		int label = smallestLabel;
		
		if(y != 0) {
			label = getLabel(discCells[x][y-1]);
			if(label < smallestLabel) smallestLabel = label;
			if(x != 0) {
				label = getLabel(discCells[x-1][y-1]);
				if(label < smallestLabel) smallestLabel = label;
			}
			if(x != discCells.length-1) {
				label = getLabel(discCells[x+1][y-1]);
				if(label < smallestLabel) smallestLabel = label;
			}
		}
		if(x != 0) {
			label = getLabel(discCells[x-1][y]);
			if(label < smallestLabel) smallestLabel = label;
		}
		if(x != discCells.length-1) {
			label = getLabel(discCells[x+1][y]);
			if(label < smallestLabel) smallestLabel = label;
		}
		if(y != discCells[0].length -1) {
			label = getLabel(discCells[x][y+1]);
			if(label < smallestLabel) smallestLabel = label;
			if(x != 0) {
				label = getLabel(discCells[x-1][y+1]);
				if(label < smallestLabel) smallestLabel = label;
			}
			if(x != discCells.length-1) {
				label = getLabel(discCells[x+1][y+1]);
				if(label < smallestLabel) smallestLabel = label;
			}
		}
		
		return new Integer(smallestLabel);
	}
	
	private static Integer eightConnectedBall(int currentLabel, int x, int y, Cell[][] discCells) {
		int smallestLabel = Integer.MAX_VALUE;
		int label = smallestLabel;
		
		if(y != 0) {
			label = getBallLabel(discCells[x][y-1]);
			if(label < smallestLabel) smallestLabel = label;
			if(x != 0) {
				label = getBallLabel(discCells[x-1][y-1]);
				if(label < smallestLabel) smallestLabel = label;
			}
			if(x != discCells.length-1) {
				label = getBallLabel(discCells[x+1][y-1]);
				if(label < smallestLabel) smallestLabel = label;
			}
		}
		if(x != 0) {
			label = getBallLabel(discCells[x-1][y]);
			if(label < smallestLabel) smallestLabel = label;
		}
		if(x != discCells.length-1) {
			label = getBallLabel(discCells[x+1][y]);
			if(label < smallestLabel) smallestLabel = label;
		}
		if(y != discCells[0].length -1) {
			label = getBallLabel(discCells[x][y+1]);
			if(label < smallestLabel) smallestLabel = label;
			if(x != 0) {
				label = getBallLabel(discCells[x-1][y+1]);
				if(label < smallestLabel) smallestLabel = label;
			}
			if(x != discCells.length-1) {
				label = getBallLabel(discCells[x+1][y+1]);
				if(label < smallestLabel) smallestLabel = label;
			}
		}
		
		return new Integer(smallestLabel);
	}
	
	private static Integer getLabel( Cell ic) {
		for( Integer i : hash.keySet()) {
			if(hash.get(i).contains(ic))
				return i;
		}
		return Integer.MAX_VALUE;
	}
	
	private static Integer getBallLabel( Cell ic) {
		for( Integer i : ballHash.keySet()) {
			if(ballHash.get(i).contains(ic))
				return i;
		}
		return Integer.MAX_VALUE;
	}
}
