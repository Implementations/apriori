package edu.rochester.kanishk.fpgrowth;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The class to represent a list of items and their support count
 * */
public class ItemSet {
	
	Set<Item> itemSet;
	
	int count;
	
	private int hashcode;
	
	public ItemSet(Set<Item> itemSet, int supportCount) {
		this.count = supportCount;
		this.itemSet = itemSet;
		this.hashcode = 0;
	}
	
	public ItemSet() {
		this.itemSet = new LinkedHashSet<>();
	}
	
	public void addItem(Item i) {
		this.itemSet.add(i);
	}
	
	
	
	@Override
	public int hashCode() {
		if(hashcode  != 0) {
			StringBuilder sb = new StringBuilder();
			for(Item i : itemSet) {
				sb.append(i.itemType);
			}
			hashcode = sb.toString().hashCode();
		}
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemSet other = (ItemSet) obj;
		if (itemSet == null) {
			if (other.itemSet != null)
				return false;
		} else if (itemSet.size() != other.itemSet.size() || !itemSet.equals(other.itemSet))
			return false;
		return true;
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
