package edu.rochester.kanishk;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.rochester.kanishk.apriori.Generator;
import edu.rochester.kanishk.apriori.ItemSetComputer;
import edu.rochester.kanishk.fastapriori.FastGenerator;
import edu.rochester.kanishk.fastapriori.FastItemSetComputer;

/**
 * Running arguments: support_count input_file output_file run_option
 * The first 3 arguments are mandatory. The next one is optional.
 * To run faster apriori, put 2 as the last argument.
 * Running command:
 * java -jar -Xms1024m -Xmx2048m Apriori.jar support_count data_file_path output_file_path
 * For running optimized Apriori:
 * java -jar -Xms1024m -Xmx2048m Apriori.jar support_count data_file_path output_file_path 2.
 */
public class Run {

	public static void main(String args[]) {
		long time = System.currentTimeMillis();
		if (args.length >= 2) {
			Integer support = Integer.parseInt(args[0]);
			String inputFile = args[1];
			String outputFile = args[2];
			processInput(inputFile, outputFile, support, args);
		} else {
			System.out.println(
					"Invalid input. Enter the program arguments as:" + "<support_count> <input_file> <output_file>");
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Running time: " + time/1000 + " seconds");
	}
	
	private static void processInput(String inputFile, String outputFile, int support, String args[]) {
		int option = 0;
		if(args.length > 3) {
			option = Integer.parseInt(args[3]);
		}
		try {
			if(option == 2) {
				aprioriOptimized(inputFile, outputFile, support);
				System.out.println("Optmized Apriori run complete");
			} else {
				apriori(inputFile, outputFile, support);
				System.out.println("Apriori run complete");
			}
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	private static void apriori(String fileName, String outputFile, int supportCount) throws IOException, InterruptedException {
		System.out.println("Using standard apriori...............");
		Generator generator = new Generator();
		generator.generateItems(fileName, supportCount);
		ItemSetComputer computer = new ItemSetComputer(generator.getTransList(), generator.getOneItemSet());
		computer.generateKItemSets(supportCount, outputFile);
	}

	private static void aprioriOptimized(String fileName, String outputFile, int supportCount) throws IOException, 
					InterruptedException, ExecutionException {
		System.out.println("Using optimized apriori...............");		
		FastGenerator generator = new FastGenerator();
		generator.generateItems(fileName, supportCount);
		FastItemSetComputer computer = new FastItemSetComputer(generator.getTransList(), 
				generator.getOneItemSet());
		computer.generateKItemSets(supportCount, outputFile);
	}

}
