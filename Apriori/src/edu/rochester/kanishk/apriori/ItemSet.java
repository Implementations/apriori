package edu.rochester.kanishk.apriori;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class to represent a list of items and their support count
 * */
public class ItemSet {
	
	List<Item> itemSet;
	
	int count;
	
	public ItemSet(List<Item> itemSet, int supportCount) {
		this.count = supportCount;
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
	
	@Override
	public String toString() {
		 // Getsall the items in the itemset as a string.
		StringBuilder sb = new StringBuilder();
		for(Item i : itemSet) {
			sb.append(i.itemType).append(" ,");
		}
		sb.append("::count:").append(count);
		return sb.toString();
	}
	
	public void print() {
		System.out.println(toString());
	}
}
