package edu.rochester.kanishk.fastapriori;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The class to represent a list of items and their support count
 * */
public class ItemSet {
	
	Set<Item> itemSet;
	
	int count;
	
	public ItemSet(Set<Item> itemSet, int supportCount) {
		this.count = supportCount;
		this.itemSet = itemSet;
	}
	
	public ItemSet() {
		this.itemSet = new LinkedHashSet<>();
	}
	
	public void addItem(Item i) {
		this.itemSet.add(i);
	}
	
	@Override
	public String toString() {
		// Gets all the items in the itemset as a string.
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
