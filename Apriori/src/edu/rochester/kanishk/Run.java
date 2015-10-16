package edu.rochester.kanishk;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Run {

	public static void main(String args[]) {
		if(args.length >= 2) {
			Integer support = Integer.parseInt(args[1]);
			Path path = FileSystems.getDefault().getPath("", args[0]);
			Generator generator = new Generator();
			try {
				generator.generateItems(path, support);
				ItemSetComputer computer = new ItemSetComputer(generator.transList, 
						generator.oneItemSet);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Invalid input");
		}
	}
	
	
}
