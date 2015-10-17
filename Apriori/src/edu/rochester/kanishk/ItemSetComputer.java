package edu.rochester.kanishk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
					for (ItemSet i : candidateSets) {
						if (candidateInTransaction(t, i)) {
							i.supportCount += 1;
						}
					}
				}
				List<ItemSet> frequentSets = new ArrayList<>();
				for (ItemSet i : candidateSets) {
					if (i.supportCount >= supportCount) {
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

	private List<ItemSet> aprioriGen(List<ItemSet> itemSets, int itemSetCount) {
		List<ItemSet> candidateSets = new ArrayList<>();
		for (ItemSet itemSet : itemSets) {
			for (ItemSet itemSet2 : itemSets) {
				int i = 0;
				boolean join = true;
				ItemSet candidateSet = new ItemSet();
				while (i < itemSetCount) {
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
					if (hasFrequentSubset(itemSets, candidateSet)) {
						candidateSets.add(candidateSet);
					}
				}
			}
		}
		return candidateSets;
	}

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

	private void writeLineToFile(List<ItemSet> itemSet) throws IOException {
		for (ItemSet i : itemSet) {
			this.writer.write(i.toString());
			this.writer.newLine();
		}
	}
}
