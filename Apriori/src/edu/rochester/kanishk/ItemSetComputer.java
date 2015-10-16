package edu.rochester.kanishk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemSetComputer {
	
	List<Transaction> trnasactionList;
	
	List<ItemSet> itemSets;

	public ItemSetComputer(List<Transaction> trnasactionList, Map<Item, Integer> oneItemSet) {
		this.trnasactionList = trnasactionList;
		createItemSet(oneItemSet);
	}
	
	private void createItemSet(Map<Item, Integer> oneItemSet) {
		this.itemSets = new ArrayList<>();
		for(Entry<Item, Integer> e : oneItemSet.entrySet()) {
			ItemSet i = new ItemSet(new ArrayList<>(), e.getValue());
			i.itemSet.add(e.getKey());
		}
	}
	
	
	private void aprioriGen(List<ItemSet> itemSets, int itemSetCount) {
		List<ItemSet> candidateSets = new ArrayList<>();
		for(ItemSet itemSet : itemSets) {
			for(ItemSet itemSet2 : itemSets) {
				int i = 0;
				boolean join = true;
				ItemSet candidateSet = new ItemSet();
				while(i < itemSetCount) {
					if(i == itemSetCount - 1) {
						join &= itemSet.itemSet.get(i).isLessThan(itemSet2.itemSet.get(i));
						candidateSet.addItem(itemSet.itemSet.get(i));
						candidateSet.addItem(itemSet2.itemSet.get(i));
					} else {
						join &= itemSet.itemSet.get(i).equals(itemSet2.itemSet.get(i));
					}
					if(join) {
						break;
					}
					i++;
				}
				candidateSet.sortItems();			
				if(join && hasFrequentSubset(itemSets, candidateSet)) {
					candidateSets.add(candidateSet);
				}
			}
		}
	}
	
	private boolean hasFrequentSubset(List<ItemSet> itemSets, ItemSet candidateSet) {
		for(Item i : candidateSet.itemSet) {
			List<Item> itemList = new ArrayList<>();
			itemList.addAll(candidateSet.itemSet);
			itemList.remove(i);
			for(ItemSet item : itemSets) {
				if(!isSubset(item.itemSet, itemList)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isSubset(List<Item> itemSet, List<Item> candidateSubset) {
		for(Item i : candidateSubset) {
			if(!itemSet.contains(i)) {
				return false;
			}
		}
		return true;
	}
}
