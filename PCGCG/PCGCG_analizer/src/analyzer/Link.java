package analyzer;


public class Link {
	private short channel;
	private boolean ball;
	private boolean cube;
	private boolean ballWithCube;
	private boolean cubeWithBall;
	public Node target;
	public Node origin;
	
	public Link(Node origin, Node target) {
		this.origin = origin;
		this.target = target;
		//evaluate();
	}
	
	public void evaluate() {
		if(origin.block == null) {
			System.out.println("Error, origin block was null");
			return;
		}
		if(target.block == null) {
			System.out.println("Error, target block was null");
			return;
		}
		
		int oHeight = origin.block.getCoordY();
		//int oYLow = oHeight - origin.block.getSizeY();
		int oXLeft = origin.block.getCoordX();
		int oXRight = origin.block.getCoordX() + origin.block.getSizeX();
		
		int tHeight = target.block.getCoordY();
		//int tYLow = tHeight - target.block.getSizeY();
		int tXLeft = target.block.getCoordX();
		int tXRight= target.block.getCoordX() + target.block.getSizeX();
		
		if(!(tXLeft> oXLeft && tXRight < oXRight)) {
			if(tHeight >= oHeight) {
				setCube(true);
				setBall(true);
			}
		} else
		{
			if(tHeight > oHeight - 300) {
				setBall(true);
			} else 
				if(tHeight > oHeight - 450) {
					setBallWithCube(true);
				}
		}

		
		
		if(oXLeft > tXRight || oXRight < tXLeft) {
			if(tHeight > oHeight - 300) {
				setBall(true);
			} else 
				if(tHeight > oHeight - 450) {
					setBallWithCube(true);
				}
		}
		if(tXLeft > oXLeft && tXRight < oXRight) {
			if(tHeight > oHeight) {
				if(tHeight > oHeight - 300) {
					setBall(true);
				} else 
					if(tHeight > oHeight - 450) {
						setBallWithCube(true);
					}
			}			
		}
//		
//		System.out.println(channel + " " + oHeight + " " + oXLeft + " " + oXRight + " [] " + tHeight + " " + tXLeft + " " + tXRight);
	}
	
	public String getInfo(){
		return "Info: "+this.getChannel() + (this.isBall()?" Ball":"") + (this.isCube()?" Cube":"") + (this.isBallWithCube()?" Ball with Cube":"");
	}
	
	
	public short getChannel() {
		return channel;
	}
	
	private void updateChannel() {
		short result = 0;
		if(isBall()) result += 1;
		if(isCube()) result += 2;
		if(isBallWithCube()) result += 4;
		if(isCubeWithBall()) result += 8;
		channel = result;		
	}
	

	public boolean isBall() {
		return ball;
	}

	public void setBall(boolean ball) {
		this.ball = ball;
		updateChannel();
	}

	public boolean isCube() {
		return cube;
	}

	public void setCube(boolean cube) {
		this.cube = cube;
		updateChannel();
	}

	public boolean isBallWithCube() {
		return ballWithCube;
	}

	public void setBallWithCube(boolean ballwithcube) {
		this.ballWithCube = ballwithcube;
		updateChannel();
	}

	public boolean isCubeWithBall() {
		return cubeWithBall;
	}

	public void setCubeWithBall(boolean cubewithball) {
		this.cubeWithBall = cubewithball;
		updateChannel();
	}
	public Node getTarget() {
		return target;
	}
	public void setTarget(Node target) {
		this.target = target;
	}
	public Node getOrigin() {
		return origin;
	}
	public void setOrigin(Node origin) {
		this.origin = origin;
	}
}
