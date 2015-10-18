package edu.rochester.kanishk.fpgrowth;

import java.util.ArrayList;
import java.util.List;

/**
 * The item class represents a single item in a transaction.
 */
public class Item implements Comparable<Item> {
	/** The category of the item. Age, hours_per_week etc.*/
	String category;
	
	/** The type of item. This is used to identify a unique item in transaction.
	 * The continous values group is also stored in this field.*/
	String itemType;

	/**The actual value of the item. For example age value the itemType will hold the group value(
	 * youth, senior etc.). This field will hold the actual numeric value. 
	 * */
	String value;
	
	public Item(String category, String itemType, String value) {
		super();
		this.category = category;
		this.itemType = itemType;
		this.value = value;
	}

	@Override
	public int hashCode() {
		return itemType.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (itemType == null) {
			if (other.itemType != null)
				return false;
		} else if (!itemType.equals(other.itemType))
			return false;
		return true;
	}

	@Override
	public int compareTo(Item o) {
		return itemType.compareTo(o.itemType);
	}
	
	public boolean isLessThan(Item o) {
		return compareTo(o) < 0;
	}
	
	/** A header class that maintains 1-frequent item set and also
	 * chain of node links for tree traversal*/
	public static class Header implements Comparable<Header> {
		private int count;
		private Item item;
		private List<FPTreeNode> chainLinks;
		
		public Header(int count, Item item) {
			this.count = count;
			this.item = item;
			this.chainLinks = new ArrayList<>();
		}

		public int getCount() {
			return count;
		}
		
		public void incrementCount() {
			this.count++;
		}

		public Item getItem() {
			return item;
		}
		
		//Adding the node to link it with its header table
		public void addChainLinks(FPTreeNode node) {
			for(FPTreeNode n : chainLinks) {
				if(n == node) {
					return;
				}
			}
			this.chainLinks.add(node);
		}

		@Override
		public int hashCode() {
			return item.hashCode();
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for(FPTreeNode i : chainLinks) {
				Item item = i.getTreeItem();
				sb.append("-->").append(item.itemType).append(":").append(i.count);
			}
			return sb.toString();
		}
		
		public void print() {
			System.out.println(toString());
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Header other = (Header) obj;
			if (item == null) {
				if (other.item != null)
					return false;
			} else if (!item.equals(other.item))
				return false;
			return true;
		}

		@Override
		public int compareTo(Header o) {
			return Integer.compare(count, o.count);
		}
	}
	
}
