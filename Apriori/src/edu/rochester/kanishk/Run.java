package edu.rochester.kanishk;

import java.io.IOException;

import edu.rochester.kanishk.apriori.Generator;
import edu.rochester.kanishk.apriori.ItemSetComputer;
import edu.rochester.kanishk.fastapriori.FastGenerator;
import edu.rochester.kanishk.fastapriori.FastItemSetComputer;

public class Run {

	public static void main(String args[]) {
		if (args.length >= 2) {
			Integer support = Integer.parseInt(args[0]);
			String inputFile = args[1];
			String outputFile = args[2];
			int option = 0;
			if(args.length == 3) {
				option = Integer.parseInt(args[2]);
			}
			try {
				if(option == 2) {
					aprioriOptimized(inputFile, outputFile, support);
				} else {
					apriori(inputFile, outputFile, support);
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println(
					"Invalid input. Enter the program arguments as:" + "<support_count> <input_file> <output_file>");
		}
	}

	private static void apriori(String fileName, String outputFile, int supportCount) throws IOException, InterruptedException {
		Generator generator = new Generator();
		generator.generateItems(fileName, supportCount);
		ItemSetComputer computer = new ItemSetComputer(generator.getTransList(), generator.getOneItemSet());
		computer.generateKItemSets(supportCount, outputFile);
	}

	private static void aprioriOptimized(String fileName, String outputFile, int supportCount) throws IOException, InterruptedException {
		FastGenerator generator = new FastGenerator();
		generator.generateItems(fileName, supportCount);
		FastItemSetComputer computer = new FastItemSetComputer(generator.getTransList(), 
				generator.getOneItemSet());
		computer.generateKItemSets(supportCount, outputFile);
	}

}
