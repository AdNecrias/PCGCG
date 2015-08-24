package protoGenerator;
import java.util.Random;

public class RandomLevelGenerator extends LevelGenerator {

	public static int levelBase = 678;
	public boolean debug = false;
	public Random random = new Random(1);
	public int maxDepth = 2;
	

	public RandomLevelGenerator(Level level) {
		this.level = level;
	}
	
	public RandomLevelGenerator() {
		
	}

	@Override
	public void run() {
		random = new Random(System.nanoTime());
		if(debug) {
			System.out.println("! - Running Debug Mode. - !");
			random = new Random(7);
		}
		
		int depth = random.nextInt(maxDepth) + 1;
		int posX = 150;
		int posY = 0;
		
		if(debug)
			System.out.println("Depth: " + depth);
		
		level.setPlayer1(new PlayerComponent(150, levelBase - 10));
		level.setPlayer2(new PlayerComponent(75, levelBase -10));
		if(debug)
			level.addComponent(new Separator());
		
		for(int i = 0; i < depth; i++) {
			if(random.nextFloat() < .9f)
			{
				posX += populate(posX, posY);
			} else
				posX += 300;
		}
		endLevel();
		
	}
	
	public void endLevel() {
		level.addComponent(new End());
	}
	
	public int populate(int x, int y) {
		int incrX = 300;
		int type = random.nextInt(6);
		if(debug)
			System.out.println("Type: " + type);
		switch (type)
		{
			case 0:
				incrX = 150;
				level.addComponent(new Gem(x + 50, levelBase - (y + 50)));
				break;
			case 1: 
				level.addComponent(new Block(x +50 , levelBase - (y + 150), 100, 30));
				level.addComponent(new Gem(x + 50, levelBase - (y + 325)));
				break;
			case 2:
				incrX = 600;
				level.addComponent(new Block(x +50 , levelBase - (y + 250), 100, 30));
				level.addComponent(new Block(x +225 , levelBase - (y + 350), 100, 30));
				level.addComponent(new Gem(x + 225, levelBase - (y + 475)));
				break;
			case 3:
				incrX = 315;
				level.addComponent(new Block(x, levelBase - (y + 80), 30, 200));
				level.addComponent(new Block(x + 110, levelBase - (y + 165), 200, 30));
				level.addComponent(new Block(x + 220, levelBase - (y + 80), 30, 200));
				level.addComponent(new Gem(x + 45, levelBase - (y + 45)));
				break;
			case 4:
				incrX = 600;
				level.addComponent(new Block(x + 50 , levelBase - (y + 300), 100, 30));
				level.addComponent(new Block(x + 250 , levelBase - (y + 300), 100, 30));
				level.addComponent(new Gem(x + 50, levelBase - (y + 500)));
				level.addComponent(new Gem(x + 250, levelBase - (y + 500)));
				level.addComponent(new Gem(x + 355, levelBase - (y + 425)));
				break;
			case 5:
				//empty
				break;
		
		}
		if(debug)
			level.addComponent(new Separator());
		
		return incrX;
	}
	
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
}
