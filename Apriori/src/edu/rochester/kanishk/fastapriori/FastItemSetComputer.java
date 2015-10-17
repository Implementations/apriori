package edu.rochester.kanishk.fastapriori;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.rochester.kanishk.Constants;
import edu.rochester.kanishk.fastapriori.ItemSet;
import edu.rochester.kanishk.fastapriori.Transaction;

/**
 * This class computes the k-frequent itemsets where k > 2 
 * @author kanishk
 */
public class FastItemSetComputer {

	private List<Transaction> trnasactionList;

	private Set<ItemSet> itemSets;

	private BufferedWriter writer;
	
	private static int THREAD_POOL_SIZE = 20;
	
	private static ExecutorService SERVICE;

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
				SERVICE = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
				Set<ItemSet> candidateSets = aprioriGen(itemSets, itemSetCount);
				for (Transaction t : trnasactionList) {
					//For each itemset in candidate set, check if it occurs in the transaction
					for (ItemSet i : candidateSets) {
						if (candidateInTransaction(t, i)) {
							i.count += 1;
						}
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
				System.out.println("Frequent set count:" + frequentSets.size());
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
	 * Checks if the transaction contains the candidate itemset i.
	 */
	private boolean candidateInTransaction(Transaction trans, ItemSet i) {
		Set<Item> itemSet = i.itemSet;
		Set<Item> transaction = trans.itemSet;
		return transaction.containsAll(itemSet);
	}

	/**
	 * Generates the candidate sets of length itemSetCount + 1
	 * @throws InterruptedException 
	 */
	private Set<ItemSet> aprioriGen(Set<ItemSet> itemSets, int itemSetCount) throws InterruptedException {
		Set<ItemSet> candidateSets = Collections.newSetFromMap(new ConcurrentHashMap<>());
		for (ItemSet itemSet : itemSets) {
			for (ItemSet itemSet2 : itemSets) {
				SERVICE.execute(new CandidateSetThread(itemSets, itemSet, itemSet2, candidateSets));
			}
		}
		SERVICE.shutdown();
		SERVICE.awaitTermination(1, TimeUnit.HOURS);
		System.out.println("Candidates generated:" + (itemSetCount + 1) + "Count: " + candidateSets.size());
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
