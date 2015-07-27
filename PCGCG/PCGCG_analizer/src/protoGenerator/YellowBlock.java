package protoGenerator;

public class YellowBlock extends Block {
	
	public YellowBlock() {
		super();
	}
	
	public YellowBlock(int coordX, int coordY) {
		super(coordX, coordY);
	}
	
	public YellowBlock(int coordX, int coordY, int sizeX, int sizeY) {
		super(coordX, coordY);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

}
