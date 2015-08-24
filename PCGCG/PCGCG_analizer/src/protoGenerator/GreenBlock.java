package protoGenerator;

public class GreenBlock extends Block {
	
	public GreenBlock() {
		super();
	}
	
	public GreenBlock(int coordX, int coordY) {
		super(coordX, coordY);
	}
	
	public GreenBlock(int coordX, int coordY, int sizeX, int sizeY) {
		super(coordX, coordY);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

}
