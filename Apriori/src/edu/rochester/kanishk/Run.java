package edu.rochester.kanishk;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Run {

	public static void main(String args[]) {
		if(args.length >= 2) {
			Integer support = Integer.parseInt(args[0]);
			Path path = Paths.get(args[1]);
			String ouputFile = args[2];
			Generator generator = new Generator();
			try {
				generator.generateItems(path, support);
				ItemSetComputer computer = new ItemSetComputer(generator.transList, 
						generator.oneItemSet);
				computer.generateKItemSets(support, ouputFile);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Invalid input. Enter the program arguments as:"
					+ "<support_count> <input_file> <output_file>");
		}
	}
	
	
}
