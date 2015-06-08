package protoGenerator;

import java.util.Random;

public class ProtoGeneratorPrime extends LevelGenerator {

	public static int levelBase = 678;
	public boolean info = false;
	public Random random = new Random(1);

	public ProtoGeneratorPrime(Level level) {
		this.level = level;
	}
	
	public ProtoGeneratorPrime(Level level, int seed) {
		this.level = level;
		this.debugSeed = seed;
	}
	public ProtoGeneratorPrime(Level level, int seed, boolean debug) {
		this.level = level;
		this.debugSeed = seed;
	}

	public ProtoGeneratorPrime() {

	}

	@Override
	void run() {
		long seed = System.nanoTime();
		if(debug) {
			seed = debugSeed;
			System.out.println("! - Running Debug Mode. - !");

		}
		if(info)
			System.out.println("Seed: " + seed);
		random = new Random(seed);

		//Temporary
		level.setPlayer1(new PlayerComponent(150, levelBase - 10));
		level.setPlayer2(new PlayerComponent(75, levelBase -10));
		
		int topcnt = random.nextInt(4) +1;
		for(int cnt = topcnt; cnt > 0; cnt-- ) {
			float currentSector = (1.0f/topcnt) * (cnt-1);
			int x = (int) (random.nextFloat()*1000);
			
			x = (int) (x * (1.0f/topcnt) + 1000 * currentSector);
			
			
			int y = 678 - random.nextInt(75) - 75;

			level.addComponent(new Block(x + 50, y+15, 100, 30));

			int x2 = (random.nextInt(150) + 50) * (random.nextBoolean() ? 1 : -1);
			int y2 = random.nextInt(100) + 150;

			if(random.nextBoolean())
				level.addComponent(new Gem(x-x2 + 50, y-y2+15));
			if(random.nextBoolean()) {
				level.addComponent(new Block(x+x2 + 50, y-y2+15, 100, 30));

				level.addComponent(new Gem(x+x2 + 50 + (random.nextInt(100) + 50) * (random.nextBoolean() ? 1 : -1),
						y-y2+15 - random.nextInt(100)));
			}
		}

		return;
	}


}
