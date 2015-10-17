package edu.rochester.kanishk.apriori;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.rochester.kanishk.Constants;

/**
 * This class computes the k-frequent itemsets where k > 2 
 * @author kanishk
 */
public class ItemSetComputer {

	private List<Transaction> trnasactionList;

	private List<ItemSet> itemSets;

	private BufferedWriter writer;

	public ItemSetComputer(List<Transaction> trnasactionList, Map<Item, Integer> oneItemSet) {
		this.trnasactionList = trnasactionList;
		createItemSet(oneItemSet);
	}
	
	private void createItemSet(Map<Item, Integer> oneItemSet) {
		this.itemSets = new ArrayList<>();
		for (Entry<Item, Integer> e : oneItemSet.entrySet()) {
			ItemSet i = new ItemSet(new ArrayList<>(), e.getValue());
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
				List<ItemSet> candidateSets = aprioriGen(itemSets, itemSetCount);
				for (Transaction t : trnasactionList) {
					//For each itemset in candidate set, check if it occurs in the transaction
					for (ItemSet i : candidateSets) {
						if (candidateInTransaction(t, i)) {
							i.count += 1;
						}
					}
				}
				List<ItemSet> frequentSets = new ArrayList<>();
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
		int itemSetIndex = 0, candidateSetIndex = 0;
		List<Item> itemSet = i.itemSet;
		List<Item> transaction = trans.itemList;
		while (itemSetIndex < itemSet.size() && candidateSetIndex < transaction.size()) {
			if (itemSet.get(itemSetIndex).equals(transaction.get(candidateSetIndex))) {
				itemSetIndex++;
			}
			candidateSetIndex++;
		}
		return itemSetIndex == itemSet.size();
	}

	/**
	 * Generates the candidate sets of length itemSetCount + 1
	 */
	private List<ItemSet> aprioriGen(List<ItemSet> itemSets, int itemSetCount) {
		List<ItemSet> candidateSets = new ArrayList<>();
		for (ItemSet itemSet : itemSets) {
			for (ItemSet itemSet2 : itemSets) {
				int i = 0;
				boolean join = true;
				ItemSet candidateSet = new ItemSet();
				//Perform join operations between frequent itemsets
				while (i < itemSetCount) {
					// Checking condition l1[1] = l2[1] ^ l1[2] = l2[2] ^.....^ l1[k -1] < l2[k -1]
					if (i == itemSetCount - 1) {
						join = join && itemSet.itemSet.get(i).isLessThan(itemSet2.itemSet.get(i));
						candidateSet.addItem(itemSet.itemSet.get(i));
						candidateSet.addItem(itemSet2.itemSet.get(i));
					} else {
						join = join && itemSet.itemSet.get(i).equals(itemSet2.itemSet.get(i));
						candidateSet.addItem(itemSet.itemSet.get(i));
					}
					if (!join) {
						break;
					}
					i++;
				}
				if (join) {
					candidateSet.sortItems();
					// Prune the candidate set
					if (hasFrequentSubset(itemSets, candidateSet)) {
						candidateSets.add(candidateSet);
					}
				}
			}
		}
		return candidateSets;
	}
	
	/**
	 * Finds whether all the k-1 length subsets of candidate set belong to 
	 * the k-frequent itemset(Lk-1)
	 */
	private boolean hasFrequentSubset(List<ItemSet> itemSets, ItemSet candidateSet) {
		int itemIndex = 0;
		while (itemIndex < candidateSet.itemSet.size()) {
			boolean found = false;
			for (ItemSet itemSet : itemSets) {
				if (isSubset(itemSet.itemSet, candidateSet.itemSet, itemIndex)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
			itemIndex++;
		}
		return true;
	}
	
	/**
	 * Check if candidate subset equals itemSet or not. Since both are in lexicographic
	 * order, we can scan index by index. The eliminate index is the index in the candidate set we
	 * are not evaluating. Since a k-candidate itemset will have k subsets of length k-1. It means
	 * for every iteration, one element is missing. That element's index is pointed by the
	 * eliminateIndex parameter.
	 */
	private boolean isSubset(List<Item> itemSet, List<Item> candidateSubset, int eliminateIndex) {
		int counter = 0, itemSetIndex = 0;
		while (counter < candidateSubset.size()) {
			if (counter != eliminateIndex) {
				if (!itemSet.get(itemSetIndex).equals(candidateSubset.get(counter))) {
					return false;
				}
				itemSetIndex++;
			}
			counter++;
		}
		return true;
	}

	private void createFileStream(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		this.writer = Files.newBufferedWriter(path, Constants.ENCODING);
	}

	/** Writes the k-itemsets to file.*/
	private void writeLineToFile(List<ItemSet> itemSet) throws IOException {
		for (ItemSet i : itemSet) {
			this.writer.write(i.toString());
			this.writer.newLine();
		}
	}
}
