package edu.rochester.kanishk.fastapriori;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.rochester.kanishk.Constants;

/**
 * @author kanishk
 * Generates the transaction data and 1-frequent itemsets. Fields like fnlwght, education num
 * are not taken. Continous fields like age, number of hours, capital gain, capital loss are discretized.
 * The capital gain and capital loss are collected and their median value is used for grouping.
 */
public class FastGenerator {
	
	private Map<Item, Integer> oneItemSet;
	
	private Set<Transaction> transList;
	
	private int supportCount;
	
	private ExecutorService SERVICE = Executors.newFixedThreadPool(20);

	public void generateItems(String filePath, int supportCount) throws IOException, InterruptedException {
		List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());
		this.supportCount = supportCount;
		List<Integer> capitalGain = Collections.synchronizedList(new ArrayList<>());
		List<Integer> capitalLoss = Collections.synchronizedList(new ArrayList<>());
		BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), Constants.ENCODING);
		String line = null;
		int counter = 0;
		while ((line = reader.readLine()) != null) {
			SERVICE.submit(new TransactionCreator(counter, transactions, line, capitalGain, capitalLoss));
			counter++;
		}
		SERVICE.shutdown();
		SERVICE.awaitTermination(1, TimeUnit.HOURS);
		Collections.sort(capitalGain);
		Collections.sort(capitalLoss);
		float medianGain = medianCalculate(capitalGain);
		float medianLoss = medianCalculate(capitalLoss);
		generateOneItemSet(transactions, medianGain, medianLoss);
		transList = new LinkedHashSet<>(transactions.size());
		transList.addAll(transactions);
	}
	
	private float medianCalculate(List<Integer> sortedList) {
		int size = sortedList.size();
		int index = size/2;
		if(size % 2 == 0) {
			return (sortedList.get(index) + sortedList.get(size - 1))/2;
		} else {
			return sortedList.get(index);
		}
	}
	
	/**
	 * Generate frequent one itemset.
	 */
	private void generateOneItemSet(List<Transaction> transactions, 
			float medianGain, float medianLoss) {
		this.oneItemSet = new HashMap<>();
		for(Transaction t : transactions) {
			t.cleanUp(medianGain, medianLoss, oneItemSet); 
		}
		Map<Item, Integer> filteredMap = new HashMap<>();
		for(Entry<Item, Integer> e : oneItemSet.entrySet()) {
			if(e.getValue() >= supportCount) {
				filteredMap.put(e.getKey(), e.getValue());
			}
		}
		this.oneItemSet = filteredMap;
	}
	
	/**
	 * Takes a single line from data file and creates transaction object from it.
	 * Removes redundant fields from the transaction. Also adds the capital gain and
	 * loss values to calculate the median values.
	 */
	public static class TransactionCreator implements Runnable {
		List<Transaction> transList;
		List<Integer> gain;
		List<Integer> loss;
		String value;
		int id;

		public TransactionCreator(int id, List<Transaction> transList, String value, List<Integer> gain, 
				List<Integer> loss) {
			this.transList = transList;
			this.value = value;
			this.gain = gain;
			this.loss = loss;
			this.id = id;
		}

		@Override
		public void run() {
			String[] values = value.split(", ");
			Transaction transaction = new Transaction(this.id);
			transaction.add("age", values[0], values[0]);
			transaction.add("work", values[1], values[1]);
			transaction.add("edu", values[3], values[3]);
			transaction.add("marital", values[5], values[5]);
			transaction.add("job", values[6], values[6]);
			transaction.add("rel", values[7], values[7]);
			transaction.add("race", values[8], values[8]);
			transaction.add("sex", values[9], values[9]);
			transaction.add("gain", values[10], values[10]);
			transaction.add("loss", values[11], values[11]);
			transaction.add("hours", values[12], values[12]);
			transaction.add("country", values[13], values[13]);
			transaction.add("status", values[14], values[14]);
			transList.add(transaction);
			if(!values[10].equals(Constants.GARBAGE)) {
				gain.add(Integer.parseInt(values[10]));
			}
			if(!values[11].equals(Constants.GARBAGE)) {
				loss.add(Integer.parseInt(values[11]));
			}
		}
	}

	public Map<Item, Integer> getOneItemSet() {
		return oneItemSet;
	}

	public Set<Transaction> getTransList() {
		return transList;
	}
}
