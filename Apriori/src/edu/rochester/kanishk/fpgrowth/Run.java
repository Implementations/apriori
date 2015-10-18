package edu.rochester.kanishk.fpgrowth;

import java.io.IOException;

/**
 * Running arguments: support_count input_file output_file run_option
 */
public class Run {

	public static void main(String args[]) {
		long time = System.currentTimeMillis();
		if (args.length >= 2) {
			Integer support = Integer.parseInt(args[0]);
			String inputFile = args[1];
			String outputFile = args[2];
			processInput(inputFile, outputFile, support);
		} else {
			System.out.println("Invalid input. Enter the program arguments as:" + 
					"<support_count> <input_file> <output_file>");
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Running time: " + time/1000 + " seconds");
	}
	
	private static void processInput(String inputFile, String outputFile, int support) {
		try {
			System.out.println("Using FPGrowth apriori...............");
			Preprocessor preprocessor = new Preprocessor();
			preprocessor.generateItems(inputFile, support);
			FPGrowth fpGrowth = new FPGrowth(preprocessor.getOneItemMap());
			fpGrowth.fpGrowth(preprocessor.getTransList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
