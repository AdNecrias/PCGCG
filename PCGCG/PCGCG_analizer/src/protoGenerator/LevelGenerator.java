package protoGenerator;

public abstract class LevelGenerator {
	public Level level;
	public int debugSeed;
	public boolean debug = false;
	
	public LevelGenerator(Level level, int seed) {
		this.level = level;
		this.debugSeed = seed;
	}
	public LevelGenerator(Level level, int seed, boolean debug) {
		this.level = level;
		this.debugSeed = seed;
	}

	public LevelGenerator(Level level) {
		this.level = level;
	}
	
	public LevelGenerator() {
		
	}


	abstract void run();
	
	
	
	
	
	public int getDebugSeed() {
		return debugSeed;
	}
	public void setDebugSeed(int debugSeed) {
		this.debugSeed = debugSeed;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
}
	
