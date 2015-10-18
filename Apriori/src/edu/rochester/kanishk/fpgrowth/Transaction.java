package edu.rochester.kanishk.fpgrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.rochester.kanishk.Constants;
import edu.rochester.kanishk.fpgrowth.Item;
import edu.rochester.kanishk.fpgrowth.Item.Header;

public class Transaction {
	List<Item> itemList;

	public Transaction() {
		this.itemList = new ArrayList<>(12);
	}

	public void add(String category, String itemType, String value) {
		itemList.add(new Item(category, itemType, value));
	}
	
	/**
	 * Takes the median capital gain and capital loss value to use for grouping. The grouping is
	 * as follows.
	 * None:0, 0 < Low <= median, High > median.
	 * The discrete values are: gain_none, gain_low, gain_high. Same goes for capital loss. 
	 */
	public void cleanUp(float gain, float loss, Map<Item, Integer> oneItemSet) {
		correctValue(gain, itemList.get(Constants.GAIN_INDEX), Constants.GAIN);
		correctValue(loss, itemList.get(Constants.LOSS_INDEX), Constants.LOSS);
		ageGroup(itemList.get(Constants.AGE_INDEX));
		hoursGroup(itemList.get(Constants.HOURS_INDEX));
		List<Item> newList = new ArrayList<>(itemList.size());
		Integer itemCount;
		for (Item i : itemList) {
			if (!Constants.GARBAGE.equals(i.value)) {
				newList.add(i);
				itemCount = oneItemSet.get(i);
				if (itemCount == null) {
					oneItemSet.put(i, 1);
				} else {
					itemCount++;
					oneItemSet.put(i, itemCount);
				}
			}
			i.value = null;
		}
		itemList = newList;
	}

	private void correctValue(float correctValue, Item item, String gainLoss) {
		if (!Constants.GARBAGE.equals(item.value)) {
			int value = Integer.parseInt(item.value);
			if (value == 0) {
				item.itemType = gainLoss + Constants.CAPITAL_NONE;
			} else if (value < correctValue) {
				item.itemType = gainLoss + Constants.LOW;
			} else {
				item.itemType = gainLoss + Constants.HIGH;
			}
		}
	}
	
	/**
	 * Age into group
	 */
	private void ageGroup(Item item) {
		if (!Constants.GARBAGE.equals(item.value)) {
			int value = Integer.parseInt(item.value);
			if (value <= 25) {
				item.itemType = Constants.YOUTH;
			} else if (value <= 45) {
				item.itemType = Constants.MIDDLE_AGE;
			} else if (value < 65) {
				item.itemType = Constants.SENIOR;
			} else {
				item.itemType = Constants.SUPER_SENIOR;
			}
		}
	}

	/**
	 * Working hours into group
	 */
	private void hoursGroup(Item item) {
		if (!Constants.GARBAGE.equals(item.value)) {
			int value = Integer.parseInt(item.value);
			if (value <= 25) {
				item.itemType = Constants.PART_TIME;
			} else if (value <= 40) {
				item.itemType = Constants.FULL_TIME;
			} else if (value < 60) {
				item.itemType = Constants.OVERTIME;
			} else {
				item.itemType = Constants.BURNOUT;
			}
		}
	}
	
	/**
	 * Removes all the items from the transactions which are not in 1-frequent itemset.
	 * Then sorts the items in descending order of their count in frequent itemset.
	 */
	public void filterAndSortTransactionItems(Map<Item, Header> oneItemSet, 
			Comparator<Item> itemComparator) {
		List<Item> newList = new ArrayList<>(itemList.size());
		for(Item i : itemList) {
			if(oneItemSet.containsKey(i)) {
				newList.add(i);
			}
		}
		itemList = newList;
		Collections.sort(itemList, itemComparator);
	}
	
	/**
	 * Gets all the items in the transaction as a string.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Item i : itemList) {
			sb.append(i.itemType).append(" ,");
		}
		return sb.toString();
	}
	
	public void print() {
		System.out.println(toString());
	}
	
	public static class ItemComparator implements Comparator<Item> {
		
		private Map<Item, Header> oneItemSet;
		
		public ItemComparator(Map<Item, Header> header) {
			this.oneItemSet = header;
		}
		
		@Override
		public int compare(Item o1, Item o2) {
			Header header1 = oneItemSet.get(o1);
			Header header2 = oneItemSet.get(o2);
			return Integer.compare(header2.getCount(), header1.getCount());
		}	
	}
}
