package edu.rochester.kanishk;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Generator {
	
	Map<Item, Integer> oneItemSet;
	
	List<Transaction> transList;
	
	int supportCount;
	
	private ExecutorService SERVICE = Executors.newFixedThreadPool(20);

	public void getItems(Path path, int supportCount) throws IOException, InterruptedException {
		this.transList = Collections.synchronizedList(new ArrayList<>());
		this.supportCount = supportCount;
		List<Integer> capitalGain = Collections.synchronizedList(new ArrayList<>());
		List<Integer> capitalLoss = Collections.synchronizedList(new ArrayList<>());
		BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			SERVICE.submit(new TransactionCreator(transList, line, capitalGain, capitalLoss));
		}
		SERVICE.shutdown();
		SERVICE.awaitTermination(1, TimeUnit.HOURS);
		Collections.sort(capitalGain);
		Collections.sort(capitalLoss);
		float medianGain = medianCalculate(capitalGain);
		float medianLoss = medianCalculate(capitalLoss);
		generateOneItemSet(transList, medianGain, medianLoss);
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
				System.out.println(e.getKey().itemType + " " + e.getValue());
			}
		}
		this.oneItemSet = filteredMap;
		System.out.println("Size :" + oneItemSet.size());
	}

	public static class TransactionCreator implements Runnable {
		List<Transaction> transList;
		List<Integer> gain;
		List<Integer> loss;
		String value;

		public TransactionCreator(List<Transaction> transList, String value, List<Integer> gain, 
				List<Integer> loss) {
			this.transList = transList;
			this.value = value;
			this.gain = gain;
			this.loss = loss;
		}

		@Override
		public void run() {
			String[] values = value.split(", ");
			Transaction transaction = new Transaction();
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
}
