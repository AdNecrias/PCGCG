package protoGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Runner {
	public int debugSeed = 1;
	public String path = ".generatedLevels.xml";
	
	
	
	public static void main(String[] args) {
		Runner runner = new Runner();
		runner.loadConfig();
		
		int numLevels = 12;
		Level[] levels= new Level[numLevels];
		
		for(int i = 0; i < numLevels; i++) {
			Level lvl = new Level();
			generateLevel(lvl, new ProtoGeneratorSecunda(lvl, runner.debugSeed));
			levels[i] = lvl;
		}
		Drawer.drawLevels(runner.path, levels);
	}
	
	public static void generateLevel(Level l, LevelGenerator levelGen) {
		long startTime = System.nanoTime();
		
		levelGen.setLevel(l);
		levelGen.run();
		
		System.out.println(levelGen.getClass().getSimpleName() + ": Level Generated in " + (System.nanoTime() - startTime)/1000000.0 + " ms.");
	}
	
	
	
	public void loadConfig() {
		File in = new File("./genConfig.cfg");
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(in));
			String currentLine; 
			while ((currentLine = input.readLine()) != null) {
				String delims = "[\n\t\r=]+";
				String tokens[] = currentLine.split(delims);				
				if(tokens[0].equals("debugSeed")) {
					debugSeed = Integer.parseInt(tokens[1]);
				}
				if(tokens[0].equals("path")) {
					path = tokens[1];
				}
			}
		} catch (FileNotFoundException e) {
			createDefaultConfig();
			loadConfig();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createDefaultConfig() {
		File out = new File("./genConfig.cfg");
		try {
			System.out.println("Writing config File.");
			BufferedWriter output = new BufferedWriter(new FileWriter(out));
			output.write("# This is a configuration file.\n");
			output.write("debugSeed=7\n");
			output.write("path=.\n");
			output.close();			
		} catch (IOException e) {
			System.out.println("Writing config file Failed.");
			e.printStackTrace();
		}
	}
	
	
	
}
