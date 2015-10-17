package edu.rochester.kanishk.fastapriori;

import java.util.Set;

public class TransactionSearchThread implements Runnable {
	
	private Transaction transaction;
	
	private ItemSet itemSet;
	
	public TransactionSearchThread(Transaction transaction, ItemSet itemSet) {
		this.transaction = transaction;
		this.itemSet = itemSet;
	}

	@Override
	public void run() {
		if (candidateInTransaction(transaction, itemSet)) {
			itemSet.count += 1;
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

}
