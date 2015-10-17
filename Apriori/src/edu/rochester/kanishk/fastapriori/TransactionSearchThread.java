package edu.rochester.kanishk.fastapriori;

import java.util.Set;
import java.util.concurrent.Callable;

public class TransactionSearchThread implements Callable<Void> {
	
	private Transaction transaction;
	
	private ItemSet itemSet;
	
	public TransactionSearchThread(Transaction transaction, ItemSet itemSet) {
		this.transaction = transaction;
		this.itemSet = itemSet;
	}

	@Override
	public Void call() throws Exception {
		if (candidateInTransaction(transaction, itemSet)) {
			itemSet.count += 1;
		}
		return null;
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
