package edu.rochester.kanishk.fastapriori;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.rochester.kanishk.Constants;

/**
 * This class computes the k-frequent itemsets where k > 2 
 * @author kanishk
 */
public class FastItemSetComputer {

	private List<Transaction> trnasactionList;

	private Set<ItemSet> itemSets;

	private BufferedWriter writer;
	
	private static int THREAD_POOL_SIZE = 20;
	
	private static ExecutorService SERVICE = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	public FastItemSetComputer(List<Transaction> trnasactionList, Map<Item, Integer> oneItemSet) {
		this.trnasactionList = trnasactionList;
		createItemSet(oneItemSet);
	}
	
	private void createItemSet(Map<Item, Integer> oneItemSet) {
		this.itemSets = new LinkedHashSet<>();
		System.out.println("Threadpool::" + THREAD_POOL_SIZE);
		for (Entry<Item, Integer> e : oneItemSet.entrySet()) {
			ItemSet i = new ItemSet(new LinkedHashSet<>(), e.getValue());
			i.itemSet.add(e.getKey());
			itemSets.add(i);
		}
	}
	
	public void generateKItemSets(int supportCount, String ouputFile) throws IOException, 
						InterruptedException, ExecutionException {
		int itemSetCount = 1;
		createFileStream(ouputFile);
		try {
			while (!itemSets.isEmpty()) {
				writeLineToFile(itemSets);
				Set<ItemSet> candidateSets = aprioriGen(itemSets, itemSetCount);
				for (Transaction t : trnasactionList) {
					//For each itemset in candidate set, check if it occurs in the transaction
					List<Future<Void>> transactions = new ArrayList<>(candidateSets.size());
					for (ItemSet i : candidateSets) {
						transactions.add(SERVICE.submit(new TransactionSearchThread(t, i)));
					}
					for(Future<Void> f : transactions) {
						f.get();
					}
				}
				Set<ItemSet> frequentSets = new LinkedHashSet<>();
				//If count is greater than support count, add to k-frequent itemset
				for (ItemSet i : candidateSets) {
					if (i.count >= supportCount) {
						frequentSets.add(i);
					}
				}
				itemSets = frequentSets;
				itemSetCount++;
			}
		} finally {
			if(writer != null) {
				writer.close();
				SERVICE.shutdown();
			}
		}
	}

	/**
	 * Generates the candidate sets of length itemSetCount + 1
	 */
	private Set<ItemSet> aprioriGen(Set<ItemSet> itemSets, int itemSetCount) {
		Set<ItemSet> candidateSets = new LinkedHashSet<>();
		List<Future<ItemSet>> futureItems = new ArrayList<>();
		for (ItemSet itemSet : itemSets) {
			for (ItemSet itemSet2 : itemSets) {
				futureItems.add(SERVICE.submit(new CandidateSetThread(itemSets, itemSet, itemSet2)));
			}
		}
		for(Future<ItemSet> f : futureItems) {
			ItemSet i;
			try {
				i = f.get();
				if(i != null) {
					candidateSets.add(i);	
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return candidateSets;
	}

	private void createFileStream(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		this.writer = Files.newBufferedWriter(path, Constants.ENCODING);
	}

	private void writeLineToFile(Set<ItemSet> itemSet) throws IOException {
		for (ItemSet i : itemSet) {
			this.writer.write(i.toString());
			this.writer.newLine();
		}
	}
	
	public static void setThreadPool(int threadPoolSize) {
		THREAD_POOL_SIZE = threadPoolSize;
	}
}
