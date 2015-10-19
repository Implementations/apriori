package edu.rochester.kanishk.fpgrowth;

import java.util.ArrayList;
import java.util.List;

import edu.rochester.kanishk.fpgrowth.Header;

/**
 * A header class that maintains 1-frequent item set and also chain of node
 * links for tree traversal
 */
public class Header implements Comparable<Header> {
	private int count;
	private Item item;
	private boolean autoIncrementCount;
	private List<FPTreeNode> chainLinks;

	public Header(Item item, int initCount) {
		this.item = item;
		this.count = initCount;
		this.chainLinks = new ArrayList<>();
	}
	
	public Header(Item item, int initCount, boolean autoIncrementCount) {
		this.item = item;
		this.count = initCount;
		this.chainLinks = new ArrayList<>();
		this.autoIncrementCount = autoIncrementCount;
	}

	public int getCount() {
		return count;
	}

	public void incrementCountByValue(int value) {
		if(autoIncrementCount) {
			this.count += value;			
		}
	}

	public Item getItem() {
		return item;
	}

	public List<FPTreeNode> getChainLinks() {
		return this.chainLinks;
	}

	// Adding the node to link it with its header table
	public void addChainLinks(FPTreeNode node) {
		for (FPTreeNode n : chainLinks) {
			if (n == node) {
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
		for (FPTreeNode i : chainLinks) {
			Item item = i.getTreeItem();
			sb.append("-->").append(item.itemType).append(":").append(i.count);
		}
		return sb.toString();
	}

	public void print() {
		System.out.println(count + " is total:::" + toString());
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
	
	
//	/** Remove the nodes which have count less than support count*/
//	public void removeNodesBelowSupportCount(int supportCount) {
//		List<FPTreeNode> newChain = new ArrayList<>();
//		for (FPTreeNode node : chainLinks) {
//			if (node.count < supportCount) {
//				node.removeNode();
//			} else {
//				newChain.add(node);
//			}
//		}
//		this.chainLinks = newChain;
//	}
}
