package protoGenerator;

import java.util.ArrayList;

public class Level {
	private ArrayList<Component> components;
	private PlayerComponent player1;
	private PlayerComponent player2;
	
	public Level() {
		components = new ArrayList<Component>();
		
	}
	
	
	public boolean addComponent(Component c) {
		return components.add(c);
	}

	public ArrayList<Component> getComponents() {
		return components;
	}

	public void setComponents(ArrayList<Component> components) {
		this.components = components;
	}

	public PlayerComponent getPlayer1() {
		return player1;
	}

	public void setPlayer1(PlayerComponent player1) {
		this.player1 = player1;
	}

	public PlayerComponent getPlayer2() {
		return player2;
	}

	public void setPlayer2(PlayerComponent player2) {
		this.player2 = player2;
	}

}
