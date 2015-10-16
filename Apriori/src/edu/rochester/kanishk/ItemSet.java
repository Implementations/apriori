package edu.rochester.kanishk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemSet {
	
	List<Item> itemSet;
	
	int supportCount;
	
	public ItemSet(List<Item> itemSet, int supportCount) {
		this.supportCount = supportCount;
		this.itemSet = itemSet;
	}
	
	public ItemSet() {
		this.itemSet = new ArrayList<>();
	}
	
	public void addItem(Item i) {
		this.itemSet.add(i);
	}
	
	public void sortItems() {
		Collections.sort(itemSet);
	}
	
}
