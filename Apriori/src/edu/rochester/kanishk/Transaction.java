package edu.rochester.kanishk;

import java.util.ArrayList;
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
		correctValue(gain, itemList.get(Constants.GAIN_INDEX));
		correctValue(loss, itemList.get(Constants.LOSS_INDEX));
		List<Item> newList = new ArrayList<>(itemList.size());
		Integer itemCount;
		for(Item i : itemList) {
			if(!Constants.GARBAGE.equals(i.value)) {
				newList.add(i);
				itemCount = oneItemSet.get(i);
				if(itemCount == null) {
					oneItemSet.put(i, 1);
				} else {
					itemCount++;
					oneItemSet.put(i, itemCount);
				}
			}
		}
		itemList = newList;
	}

	private void correctValue(float correctValue, Item item) {
		if (!Constants.GARBAGE.equals(item.value)) {
			int value = Integer.parseInt(item.value);
			if (value == 0) {
				item.itemType = Constants.CAPITAL_NONE;
			} else if (value < correctValue) {
				item.itemType = Constants.LOW;
			} else {
				item.itemType = Constants.HIGH;
			}
		}
	}
}
