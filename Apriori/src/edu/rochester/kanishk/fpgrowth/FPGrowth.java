package edu.rochester.kanishk.fpgrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.rochester.kanishk.fpgrowth.Item.Header;
import edu.rochester.kanishk.fpgrowth.Transaction.ItemComparator;

public class FPGrowth {
	
	private static Comparator<Item> ITEM_COMPARATOR;
	
	/*A heade table that maintains a 1-itemset and maintains a chain link to link
	 * all similar items present in FP tree for ease of traversal.
	 */
	private Map<Item, Header> oneItemSet;
	
	private FPTreeNode root;
	
	public FPGrowth(Map<Item, Header> oneItemSet) {
		this.root = new FPTreeNode();
		this.oneItemSet = oneItemSet;
		ITEM_COMPARATOR = new ItemComparator(this.oneItemSet);
	}
	
	public void fpGrowth(List<Transaction> transactions) {
		for(Transaction t : transactions) {
			t.filterAndSortTransactionItems(oneItemSet, ITEM_COMPARATOR);
//			t.print();
			insertItems(root, t.itemList, oneItemSet);
		}
		List<Header> headers = new ArrayList<>();
		headers.addAll(oneItemSet.values());
		Collections.sort(headers);
	}
	
	private void insertItems(FPTreeNode current, List<Item> transactionItems, Map<Item, Header> oneItemSet) {
		for(Item item : transactionItems) {
			current = current.insertAndReturnChild(item);
			Header header = oneItemSet.get(item);
			header.addChainLinks(current);
		}
	}
}
