package protoGenerator;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class Block extends Component {

	public int sizeX;
	public int sizeY;
	
	public Block() {
		super();
	}
	
	public Block(int coordX, int coordY) {
		super(coordX, coordY);
	}
	
	public Block(int coordX, int coordY, int sizeX, int sizeY) {
		super(coordX, coordY);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + getCoordX() + "," + getCoordY() +
				"," + getSizeX() + "," + getSizeY() + ")";
	}
	public int getSizeX() {
		return sizeX;
	}
	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	public boolean contains(int posX, int posY) {
		if(posX >= getCoordX() && posY >= getCoordY())
			if(posX<= getCoordX()+sizeX && posY <= getCoordY()+sizeY)
				return true;
		return false;
	}

	public Rectangle getRect() {
		return new Rectangle(getCoordX(), getCoordY(), getSizeX(), getSizeY());
	}

	public Rectangle2D getLandingArea(int height) {
		return new Rectangle(getCoordX(), getCoordY()-height, getSizeX(), height);
	}

	public Rectangle2D getLandingArea() {
		return this.getLandingArea(50); // default 50
	}
	
	
}
