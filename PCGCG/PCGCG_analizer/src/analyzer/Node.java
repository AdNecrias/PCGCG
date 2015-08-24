package analyzer;
import java.util.ArrayList;

import protoGenerator.Block;


public class Node {
	private short reachable; // 1=reachable by ball, 2=reachable by cube
	private boolean reachableByBall;
	private boolean reachableByCube;
	public ArrayList<Link> links;
	public Block block;
	
	public Node() {
		links = new ArrayList<Link>();
	}
	public Node(Block block) {
		this();
		this.block = block;
	}
	
	public void setReachableByBall(boolean value) {
		reachableByBall = value;
		updateReachable();
	}
	public boolean isReachableByBall() {
		return reachableByBall;
	}
	public void setReachableByCube(boolean value) {
		reachableByCube = value;
		updateReachable();
	}
	public boolean isReachableByCube() {
		return reachableByCube;
	}
	public short getReachable() {
		return reachable;
	}

	private void updateReachable() {
		short result = 0;
		if(reachableByBall) result += 1;
		if(reachableByCube) result += 2;
		reachable = result;		
	}
	@Override
	public String toString() {
		return "Node [(reachable=" + reachable + ", links=" + links + ", block=" + block + ")]";
	}
	
	

}
