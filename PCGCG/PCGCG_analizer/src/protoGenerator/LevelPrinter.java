package protoGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LevelPrinter {

	private Level level;
	public String outputPath = ".";
	
	public LevelPrinter() {}
	
	public LevelPrinter(Level level) {
		this.level = level;
	}
	
	
	public void print(){
		if(level == null) {
			System.out.println("Nothing to print.");
			return;
		}
		File f = new File(outputPath);
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(f));
				PlayerComponent pc = level.getPlayer1();
				if(pc != null) output.write(pc.toString());
				
				pc = level.getPlayer2();
				if(pc != null) output.write(pc.toString());
				for(Component c : level.getComponents()) {
					output.write(c.toString());
				}				
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
}
