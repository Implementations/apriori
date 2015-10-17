package edu.rochester.kanishk.fastapriori;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import edu.rochester.kanishk.Constants;

/**
 * This class computes the k-frequent itemsets where k > 2 
 * @author kanishk
 */
public class FastItemSetComputer {

	private List<Transaction> trnasactionList;

	private Set<ItemSet> itemSets;

	private BufferedWriter writer;

	public FastItemSetComputer(List<Transaction> trnasactionList, Map<Item, Integer> oneItemSet) {
		this.trnasactionList = trnasactionList;
		createItemSet(oneItemSet);
	}
	
	private void createItemSet(Map<Item, Integer> oneItemSet) {
		this.itemSets = new LinkedHashSet<>();
		for (Entry<Item, Integer> e : oneItemSet.entrySet()) {
			ItemSet i = new ItemSet(new LinkedHashSet<>(), e.getValue());
			i.itemSet.add(e.getKey());
			itemSets.add(i);
		}
	}
	
	public void generateKItemSets(int supportCount, String ouputFile) throws IOException {
		int itemSetCount = 1;
		createFileStream(ouputFile);
		try {
			while (!itemSets.isEmpty()) {
				writeLineToFile(itemSets);
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
				itemSetCount++;
			}
		} finally {
			if(writer != null) {
				writer.close();
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
	 */
	private Set<ItemSet> aprioriGen(Set<ItemSet> itemSets, int itemSetCount) {
		Set<ItemSet> candidateSets = new LinkedHashSet<>();
		for (ItemSet itemSet : itemSets) {
			for (ItemSet itemSet2 : itemSets) {
				int i = 0;
				boolean join = true;
				ItemSet candidateSet = new ItemSet();
				/*Perform join operations between frequent itemsets
				 * The itemsets are maintained in a lexicographic order
				 */
				Iterator<Item> itemSetIterator = itemSet.itemSet.iterator();
				Iterator<Item> itemSet2Iterator = itemSet2.itemSet.iterator();
				while (itemSetIterator.hasNext() && itemSet2Iterator.hasNext()) {
					// Checking condition l1[1] = l2[1] ^ l1[2] = l2[2] ^.....^ l1[k -1] < l2[k -1]					
					Item item1 = itemSetIterator.next();
					Item item2 = itemSet2Iterator.next();
					if (i == itemSetCount - 1) {
						join = join && item1.isLessThan(item2);
						candidateSet.addItem(item1);
						candidateSet.addItem(item2);
					} else {
						join = join && item1.equals(item2);
						candidateSet.addItem(item1);
					}
					if (!join) {
						break;
					}
					i++;
				}
				if (join) {
					// Prune the candidate set
					if (hasFrequentSubset(itemSets, candidateSet)) {
						candidateSets.add(candidateSet);
					}
				}
			}
		}
		return candidateSets;
	}

	private boolean hasFrequentSubset(Set<ItemSet> itemSets, ItemSet candidateSet) {;
		Set<Item> tempSet = new HashSet<>();
		tempSet.addAll(candidateSet.itemSet);
		for(Item i : candidateSet.itemSet) {
			boolean found = false;
			tempSet.remove(i);
			for(ItemSet itemSet : itemSets) {
				if(itemSet.itemSet.containsAll(tempSet)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
			tempSet.add(i);
		}
		return true;
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
}
