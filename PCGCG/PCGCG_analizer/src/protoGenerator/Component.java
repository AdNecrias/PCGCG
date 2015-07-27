package protoGenerator;

public class Component {
	private int coordX;
	private int coordY;
	private boolean enabled = true;
	
	
	
	
	
	/**
	 * @param coordX
	 * @param coordY
	 */
	public Component() {
		super();
	}
	
	public Component(int coordX, int coordY) {
		super();
		this.coordX = coordX;
		this.coordY = coordY;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + getCoordX() + "," + getCoordY() + ")";
	}
	
	public int getCoordX() {
		return coordX;
	}
	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}
	public int getCoordY() {
		return coordY;
	}
	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
