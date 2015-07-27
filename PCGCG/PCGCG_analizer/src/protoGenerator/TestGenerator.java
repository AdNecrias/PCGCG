package protoGenerator;

import java.util.Random;

public class TestGenerator extends LevelGenerator {
	public boolean colourCounter;

	public Random random = new Random(1);

	public TestGenerator(Level level) {
		this.level = level;
	}
	
	public TestGenerator(Level level, int seed) {
		this.level = level;
		this.debugSeed = seed;
	}
	public TestGenerator(Level level, int seed, boolean debug) {
		this.level = level;
		this.debugSeed = seed;
	}

	public TestGenerator() {

	}
	
	@Override
	void run() {
		/*
		level.setPlayer1(new PlayerComponent(1190, 710));
		level.setPlayer2(new PlayerComponent(1090, 710));
		level.addComponent(new Gem(1200, 40));
		
		level.addComponent(new Block(40, floor - 100, 100, 100));
		level.addComponent(new Block(40, floor - 200, 100, 100));
		level.addComponent(new Block(40, floor - 50, 100, 50));
		
		level.addComponent(new Block(700, floor - 300, 100, 40));
		level.addComponent(new Block(600, floor - 400, 100, 40));
		level.addComponent(new Block(500, floor - 350, 100, 40));
		level.addComponent(new Block(900, floor - 500, 100, 40));*/
		
		
		//Temporary
				level.setPlayer1(new PlayerComponent(150, 700));
				level.setPlayer2(new PlayerComponent(75, 700));
				level.addComponent(new Gem(150, 150));
		
		for(int i = 40; i < 1240; i+=10) {
			for (int j = 40; j < 760; j+=10) {
				setBlock(i,j);
			}
		}
		
	}

	public void setBlock(int i, int j) {
		if(colourCounter) {
			level.addComponent(new GreenBlock(i, j, 10, 10));
		} else {
			level.addComponent(new YellowBlock(i, j, 10, 10));
		}
		colourCounter = !colourCounter;
	}

}
