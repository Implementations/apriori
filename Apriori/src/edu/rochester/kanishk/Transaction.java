package edu.rochester.kanishk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Transaction {
	List<Item> itemList;

	public Transaction() {
		this.itemList = new ArrayList<>(12);
	}

	public void add(String category, String itemType, String value) {
		itemList.add(new Item(category, itemType, value));
	}

	public void add(Item item) {
		itemList.add(item);
	}

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
		}
		itemList = newList;
		Collections.sort(itemList);
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
}
