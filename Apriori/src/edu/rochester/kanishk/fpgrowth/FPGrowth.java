package edu.rochester.kanishk.fpgrowth;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.rochester.kanishk.Constants;
import edu.rochester.kanishk.fpgrowth.Transaction.ItemComparator;

public class FPGrowth {

	private static Comparator<Item> ITEM_COMPARATOR;

	/*
	 * A heade table that maintains a 1-itemset and maintains a chain link to
	 * link all similar items present in FP tree for ease of traversal.
	 */
	private Map<Item, Header> oneItemSet;

	private FPTreeNode root;

	private int supportCount;

	private BufferedWriter writer;

	public FPGrowth(Map<Item, Header> oneItemSet, int supportCount) {
		this.root = new FPTreeNode();
		this.oneItemSet = oneItemSet;
		this.supportCount = supportCount;
		ITEM_COMPARATOR = new ItemComparator(this.oneItemSet);
	}

	public void fpGrowth(List<Transaction> transactions, String outputFile) throws IOException {
		createFileStream(outputFile);
		for (Transaction t : transactions) {
			t.filterAndSortTransactionItems(oneItemSet, ITEM_COMPARATOR);
			insertItems(root, t.itemList, oneItemSet, 1);
		}
		List<Header> headers = new ArrayList<>();
		headers.addAll(oneItemSet.values());
		Collections.sort(headers);
		for (Header h : headers) {
			ItemSet i = new ItemSet();
			i.count = h.getCount();
			i.addItem(h.getItem());
			List<ItemSet> list = new ArrayList<>();
			list.add(i);
			writeLineToFile(list);
			fpPatternAndTree(h);
		}
	}

	private void insertItems(FPTreeNode current, List<Item> transactionItems, Map<Item, Header> oneItemSet,
			int baseCount) {
		for (Item item : transactionItems) {
			current = current.insertAndReturnChild(item, baseCount);
			Header header = oneItemSet.get(item);
			if (header == null) {
				header = new Header(item, 0, true);
				oneItemSet.put(item, header);
			}
			header.addChainLinks(current);
			header.incrementCountByValue(baseCount);
		}
	}

	// Filter and generate conditional FP-tree
	private void fpPatternAndTree(Header header) throws IOException {
		List<FPTreeNode> linkedNodes = header.getChainLinks(); // Header
																// contains is a
																// single item
																// type
		Map<Item, Header> oneItemSet = new HashMap<>();
		FPTreeNode root = new FPTreeNode();
		for (FPTreeNode node : linkedNodes) {
			List<Item> itemList = new ArrayList<>();
			FPTreeNode current = node.parent;
			while (current != this.root) {
				itemList.add(current.getTreeItem());
				current = current.parent;
			}
			Collections.reverse(itemList);
			insertItems(root, itemList, oneItemSet, node.count);
		}
		List<Header> headers = new ArrayList<>();
		headers.addAll(oneItemSet.values());
		// Collections.sort(headers);
		// Collections.reverse(headers);
		processConditionalFPTree(root, headers, header);
	}

	// Process conditional fp-tree
	private void processConditionalFPTree(FPTreeNode root, List<Header> oneItemSet, Header itemHeader)
			throws IOException {
		for (Header h : oneItemSet) {
			if (h.getCount() >= supportCount) {
				processSingleHeader(root, h, itemHeader);
			}
		}
	}

	// Process a single header among the headers of conditional fp-tree
	private void processSingleHeader(FPTreeNode conditionalRoot, Header conditionalHeader, Header itemHeader)
			throws IOException {
		Map<ItemSet, ItemSet> fpItemSet = new HashMap<>();
		List<FPTreeNode> conditionalList = conditionalHeader.getChainLinks();
		ItemSet twoItemSet = returnTwoItemSet(itemHeader, conditionalHeader.getItem(), conditionalHeader.getCount());
		fpItemSet.put(twoItemSet, twoItemSet);
		for (FPTreeNode node : conditionalList) {
			/* This single node count is greater than support count so all the subsets formed with
			 * its ancestors will also be a frequent itemsets. 
			 */
			List<FPTreeNode> powerSetList = new ArrayList<>();
			FPTreeNode current = node.parent;
			while (current != conditionalRoot) {
				powerSetList.add(current);
				current = current.parent;
			}
			if (!powerSetList.isEmpty()) {
				generatePatterns(node, itemHeader, fpItemSet, powerSetList);
			}
		}
		writeLineToFile(fpItemSet.values());
	}

	private ItemSet returnTwoItemSet(Header mainHeaderItem, Item currentHeaderItem, int count) {
		ItemSet set = new ItemSet();
		set.count = count;
		set.addItem(mainHeaderItem.getItem());
		set.addItem(currentHeaderItem);
		return set;
	}

	// Generate power set of nodes above FPTreeNode
	private void generatePatterns(FPTreeNode node, Header mainItemHeader, Map<ItemSet, ItemSet> fpItemSet,
			List<FPTreeNode> powerSetList) {
		List<List<Item>> powerSets = getPowerSet(powerSetList);
		for (List<Item> itemList : powerSets) {
			ItemSet itemSet = new ItemSet();
			itemSet.count = node.count;
			itemSet.addItem(mainItemHeader.getItem());
			itemSet.addItem(node.getTreeItem());
			for (Item i : itemList) {
				itemSet.addItem(i);
			}
			ItemSet mapItem = fpItemSet.get(itemSet);
			if (mapItem == null) {
				fpItemSet.put(itemSet, itemSet);
			} else {
				mapItem.count += itemSet.count;
			}
		}
	}

	private List<List<Item>> getPowerSet(List<FPTreeNode> powerSet) {
		int powerSetSize = (int) Math.pow(2, powerSet.size());
		int counter = 0, j = 0, size = powerSet.size();
		List<List<Item>> powerSetList = new ArrayList<>();
		for (counter = 0; counter < powerSetSize; counter++) {
			List<Item> itemList = new ArrayList<>();
			for (j = 0; j < size; j++) {
				// Check if jth bit in the counter is set If set then add jth
				// element from set
				if ((counter & (1 << j)) != 0) {
					itemList.add(powerSet.get(j).getTreeItem());
				}
			}
			if (!itemList.isEmpty()) {
				powerSetList.add(itemList);
			}
		}
		return powerSetList;
	}

	private void createFileStream(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		this.writer = Files.newBufferedWriter(path, Constants.ENCODING);
	}

	/** Writes the k-itemsets to file. */
	private void writeLineToFile(Collection<ItemSet> itemSet) throws IOException {
		for (ItemSet i : itemSet) {
			if(i.count >= supportCount) {
//				i.print();				
				this.writer.write(i.toString());
				this.writer.newLine();
			}
		}
	}
}
