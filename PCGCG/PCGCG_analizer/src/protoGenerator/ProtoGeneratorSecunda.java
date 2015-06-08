package protoGenerator;

import java.util.ArrayList;
import java.util.Random;

public class ProtoGeneratorSecunda extends LevelGenerator {
	public boolean info = false;
	public Random random = new Random(1);
	public final int ceiling = 40;
	public final int floor = 760;
	public final int wallLeft = 40;
	public final int wallRight = 1240;
	public ArrayList<Block> interditionArea = null;
	int timeout = 0;

	public ProtoGeneratorSecunda(Level level) {
		this.level = level;
	}
	
	public ProtoGeneratorSecunda(Level level, int seed) {
		this.level = level;
		this.debugSeed = seed;
	}
	public ProtoGeneratorSecunda(Level level, int seed, boolean debug) {
		this.level = level;
		this.debugSeed = seed;
	}

	public ProtoGeneratorSecunda() {

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
		interditionArea = new ArrayList<Block>();
		
		/*Generate Box and platform*/
		int posX = random.nextInt(940);
		int sizeX = random.nextInt(150) + 151;
		
		int posY = random.nextInt(350)+300;
		int sizeY = random.nextInt(20) + 20;
		
		level.addComponent(new Block(posX, floor - posY, sizeX, sizeY));
		level.setPlayer2(new PlayerComponent(posX + sizeX/2, floor - posY - 50));
		interditionArea.add(new Block(posX, floor - posY- 100, sizeX, sizeY + 100));
		
		/*Generate Circle*/
		
		level.setPlayer1(new PlayerComponent(1190, 710));
		
		/*Set Objective platform*/
		
		posX = random.nextInt(940);
		sizeX = random.nextInt(250) + 51;
		
		posY = random.nextInt(100) + 300;
		sizeY = random.nextInt(20) + 20;
		while(timeout < 10000 && interdicted(posX, floor - posY, sizeX, sizeY)) {
			posX = random.nextInt(940);
			sizeX = random.nextInt(250) + 51;
			posY = random.nextInt(100) + 300;
			sizeY = random.nextInt(20) + 20;
			timeout++;
		}
		level.addComponent(new Block(posX, floor - posY, sizeX, sizeY));
		level.addComponent(new Gem(posX + sizeX/2, floor - posY - 50));
		interditionArea.add(new Block(posX + sizeX/2 -500, floor - posY - 100, 100,100));
		
		/*Add extra platforms*/
		
		int extrapads = random.nextInt(4) + 1;
		for (int i = 0; i < extrapads; i++) {
			posX = random.nextInt(wallRight);
			
			float sector = 1.0f/(extrapads+1);
			posX = Math.min(Math.max((int)(posX*sector + wallRight*sector*(i) - 100), wallLeft) , wallRight);
			
			sizeX = random.nextInt(50) + 51;
			
			posY = random.nextInt(250) + 150;
			sizeY = random.nextInt(20) + 20;
			while(timeout < 10000 && interdicted(posX, floor - posY, sizeX, sizeY)) {
				posX = Math.min(Math.max((int)(posX*sector + wallRight*sector*(i) - 100), wallLeft) , wallRight);
				sizeX = random.nextInt(50) + 51;
				posY = random.nextInt(250) + 150;
				sizeY = random.nextInt(20) + 20;
				timeout++;
			}
			level.addComponent(new Block(posX, floor - posY, sizeX, sizeY));
			interditionArea.add(new Block(posX, floor - posY, sizeX, sizeY));
			
			int jitterX = random.nextInt(400) - 200;
			int jitterY = random.nextInt(200) - 100;
			while(timeout < 10000 && interdicted(posX + sizeX/2 + jitterX -50, (floor - posY - 50) - 150 + jitterY, 100, 100)) {
				jitterX = random.nextInt(400) - 200;
				jitterY = random.nextInt(200) - 100;
				timeout++;
			}
			level.addComponent(new Gem(
					/*X*/	Math.max(Math.min((posX + sizeX/2) + jitterX, wallRight-50), wallLeft+50),
					/*Y*/	(floor - posY - 50) - 100 + jitterY)
			);
			interditionArea.add(new Block(Math.max(Math.min((posX + sizeX/2) + jitterX, wallRight-50), wallLeft+50)-50, (floor - posY - 50) - 100 + jitterY -50, 100, 100));
		}
		
		/*end*/
		
		/*debug*/
		/*
		for(Block b : interditionArea) {
			level.addComponent(new GreenBlock(b.getCoordX(), b.getCoordY(), b.getSizeX(), b.getSizeY()));
		}
		*/
	}

	public boolean interdicted(int posX, int posY, int sizeX, int sizeY) {
		for(Block b : interditionArea) {
			if(b.contains(posX, posY))
				return true;
			if(b.contains(posX+sizeX, posY+sizeY))
				return true;
		}	
		return false;
	}

}
